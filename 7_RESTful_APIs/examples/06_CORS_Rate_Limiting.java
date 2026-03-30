package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 06: CORS & Rate Limiting
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  1. CORS (Cross-Origin Resource Sharing) configuration
 *  2. Rate Limiting using the Token Bucket algorithm
 *
 * ─── CORS ────────────────────────────────────────────────────────────────────
 * CORS is a browser security mechanism. When your Angular/React frontend
 * (e.g. http://localhost:4200) calls your Spring API (http://localhost:8080),
 * the browser blocks the request unless the server says "this origin is allowed".
 *
 * Mobile apps, Postman, and curl are NOT affected by CORS — it's browser-only.
 *
 * ─── RATE LIMITING ───────────────────────────────────────────────────────────
 * Rate limiting caps how many requests a client can make in a time window.
 * This protects against:
 *  - Abuse / scraping
 *  - Brute force attacks
 *  - Accidental infinite loops from client bugs
 *
 * Strategy used: Token Bucket (implemented manually below)
 *  - Each IP starts with N tokens
 *  - Each request consumes 1 token
 *  - Tokens refill at a fixed rate over time
 *  - When tokens run out → 429 Too Many Requests
 *
 * For production, use Bucket4J library:
 *   <dependency>
 *     <groupId>com.github.vladimir-bukhtoyarov</groupId>
 *     <artifactId>bucket4j-core</artifactId>
 *     <version>8.0.1</version>
 *   </dependency>
 */

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// ─── CORS CONFIGURATION ───────────────────────────────────────────────────────

@Configuration
class CorsConfig {

    /**
     * Configure CORS globally for all /api/** routes.
     *
     * In development: allow localhost origins.
     * In production: restrict to your real domain(s).
     *
     * CRITICAL: Never use allowedOrigins("*") with allowCredentials(true).
     * The browser will reject it AND it's a security vulnerability.
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // ✅ Specify exact allowed origins (not "*" when using credentials)
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",    // React dev server
            "http://localhost:4200",    // Angular dev server
            "https://myapp.com",        // Production frontend
            "https://staging.myapp.com" // Staging environment
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(List.of(
            "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
        ));

        // Allowed request headers
        // "*" means the server accepts any header the client sends
        config.setAllowedHeaders(List.of("*"));

        // Headers the browser is allowed to read from the response
        config.setExposedHeaders(List.of(
            "X-Request-ID",
            "X-Response-Time-Ms",
            "X-RateLimit-Remaining"  // We'll add this in the rate limiter below
        ));

        // Allow credentials (Authorization header, cookies)
        config.setAllowCredentials(true);

        // Cache the CORS preflight response for 1 hour (reduces OPTIONS requests)
        config.setMaxAge(3600L);

        // Apply this CORS config to all /api/** routes
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return new CorsFilter(source);
    }
}

// ─── RATE LIMITER (Token Bucket — Manual Implementation) ──────────────────────

/**
 * Tracks the token bucket state for a single IP address.
 * Thread-safe using AtomicInteger.
 */
class TokenBucket {
    private final int capacity;             // Max tokens
    private final int refillAmount;         // Tokens added each refill
    private final long refillIntervalMs;    // How often to refill (milliseconds)

    private final AtomicInteger tokens;     // Current token count
    private long lastRefillTime;            // Timestamp of last refill

    public TokenBucket(int capacity, int refillAmount, long refillIntervalMs) {
        this.capacity = capacity;
        this.refillAmount = refillAmount;
        this.refillIntervalMs = refillIntervalMs;
        this.tokens = new AtomicInteger(capacity);
        this.lastRefillTime = System.currentTimeMillis();
    }

    /**
     * Attempt to consume 1 token.
     * @return true if a token was available (request allowed),
     *         false if out of tokens (request should be rate-limited)
     */
    public synchronized boolean tryConsume() {
        refillIfNeeded();

        if (tokens.get() > 0) {
            tokens.decrementAndGet();
            return true;
        }
        return false;
    }

    public int getTokensRemaining() {
        refillIfNeeded();
        return tokens.get();
    }

    private void refillIfNeeded() {
        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTime;

        // Check if it's time for a refill
        if (elapsed >= refillIntervalMs) {
            int newTokens = Math.min(capacity, tokens.get() + refillAmount);
            tokens.set(newTokens);
            lastRefillTime = now;
        }
    }
}

// ─── RATE LIMITING FILTER ─────────────────────────────────────────────────────

/**
 * Applies rate limiting per IP address.
 *
 * Configuration:
 *  - 20 requests per minute per IP
 *  - Tokens refill to 20 every 60 seconds
 *
 * Returns HTTP 429 Too Many Requests when limit is exceeded.
 * Includes X-RateLimit-Remaining header on every response.
 */
@Component
@Order(4)  // Runs after API key filter (Order 3)
class RateLimitingFilter implements Filter {

    // One bucket per client IP address
    private final Map<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    // Rate limit settings: 20 requests per IP per minute
    private static final int CAPACITY = 20;
    private static final int REFILL_AMOUNT = 20;
    private static final long REFILL_INTERVAL_MS = 60_000;  // 60 seconds

    private TokenBucket getBucket(String clientIp) {
        // computeIfAbsent is thread-safe — creates a bucket if one doesn't exist
        return buckets.computeIfAbsent(clientIp,
            ip -> new TokenBucket(CAPACITY, REFILL_AMOUNT, REFILL_INTERVAL_MS));
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Identify client — use X-Forwarded-For in production (when behind a proxy)
        String clientIp = getClientIp(httpRequest);
        TokenBucket bucket = getBucket(clientIp);

        // Add remaining tokens to response header (good practice for API clients)
        httpResponse.addHeader("X-RateLimit-Limit", String.valueOf(CAPACITY));
        httpResponse.addHeader("X-RateLimit-Remaining", String.valueOf(bucket.getTokensRemaining()));

        if (bucket.tryConsume()) {
            chain.doFilter(request, response);  // Request allowed
        } else {
            // Rate limit exceeded → 429 Too Many Requests
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.setCharacterEncoding("UTF-8");
            httpResponse.addHeader("Retry-After", "60");  // Tell client to wait 60s
            httpResponse.getWriter().write(
                "{\"status\":429,\"error\":\"Too Many Requests\"," +
                "\"message\":\"Rate limit exceeded. You can make 20 requests per minute. " +
                "Please wait and try again.\",\"retryAfterSeconds\":60}"
            );
        }
    }

    /**
     * Extract the real client IP.
     * When behind a reverse proxy (Nginx, CloudFlare), the real IP is in
     * the X-Forwarded-For header, not getRemoteAddr().
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            // X-Forwarded-For can be a comma-separated list; take the first IP
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

/*
 * ─── CORS PREFLIGHT EXPLAINED ─────────────────────────────────────────────────
 *
 *  For "non-simple" requests (POST/PUT/DELETE or custom headers), the browser
 *  first sends an OPTIONS "preflight" request to ask:
 *   "Is this origin allowed to call you with these headers?"
 *
 *  If the server replies with the correct CORS headers, the browser sends
 *  the real request. If not, the browser blocks it and shows a CORS error.
 *
 *  Preflight request:
 *    OPTIONS /api/v1/products
 *    Origin: http://localhost:3000
 *    Access-Control-Request-Method: POST
 *    Access-Control-Request-Headers: Content-Type, X-API-Key
 *
 *  Preflight response:
 *    HTTP 200 OK
 *    Access-Control-Allow-Origin: http://localhost:3000
 *    Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
 *    Access-Control-Allow-Headers: *
 *    Access-Control-Max-Age: 3600
 *
 * ─── RATE LIMIT RESPONSE HEADERS ─────────────────────────────────────────────
 *
 *  X-RateLimit-Limit: 20          → Max requests per window
 *  X-RateLimit-Remaining: 17      → Requests left in current window
 *  Retry-After: 60                → Seconds to wait (only on 429)
 *
 * ─── TEST RATE LIMITING ───────────────────────────────────────────────────────
 *
 *  # Send 25 requests and observe the status codes
 *  for i in {1..25}; do
 *    echo -n "Request $i: "
 *    curl -s -o /dev/null -w "%{http_code}\n" \
 *      -H "X-API-Key: my-secret-key-change-in-production" \
 *      http://localhost:8080/api/v1/products
 *  done
 *
 *  Expected output:
 *    Request 1:  200
 *    ...
 *    Request 20: 200
 *    Request 21: 429   ← Rate limit hit!
 *    ...
 *    Request 25: 429
 */
