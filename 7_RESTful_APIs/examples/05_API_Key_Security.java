package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 05: API Key Security
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates API Key Authentication:
 *  - A Filter that checks for a valid X-API-Key header
 *  - Public vs protected route configuration
 *  - Storing the key securely in application.properties
 *  - Returning a structured 401 response when the key is missing/invalid
 *
 * API Key auth is suitable for:
 *  ✅ Server-to-server communication
 *  ✅ Simple internal services
 *  ❌ User-facing mobile/web apps (use JWT/OAuth2 instead — Module 8)
 *
 * Flow:
 *   Client → [X-API-Key: secret] → ApiKeyFilter → Valid? → Controller
 *                                      ↓ Invalid
 *                                  401 Unauthorized
 */

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// ─── APPLICATION PROPERTIES EXAMPLE ──────────────────────────────────────────
/*
 * In src/main/resources/application.properties, add:
 *
 *   api.security.key=my-secret-key-change-in-production
 *
 * In production, use an environment variable instead:
 *   API_SECURITY_KEY=my-secret-key
 *
 * And reference it in application.properties as:
 *   api.security.key=${API_SECURITY_KEY}
 *
 * This way, secrets are never committed to version control.
 */

// ─── API KEY FILTER ───────────────────────────────────────────────────────────
/**
 * Validates the X-API-Key header on every request to protected paths.
 *
 * @Order(3) — runs after logging and security headers filters.
 * The lower the number, the earlier the filter runs.
 */
@Component
@Order(3)
class ApiKeyFilter implements Filter {

    // Injected from application.properties: api.security.key=...
    @Value("${api.security.key:default-key-for-dev}")
    private String validApiKey;

    /**
     * Paths that do NOT require an API key.
     * Anyone can call these without authentication.
     */
    private static final List<String> PUBLIC_PATHS = Arrays.asList(
        "/",
        "/actuator/health",
        "/api/v1/auth/register",
        "/api/v1/auth/login"
    );

    /**
     * Paths that REQUIRE an API key.
     * Pattern: all /api/** routes are protected unless in PUBLIC_PATHS.
     */
    private boolean isProtected(String path) {
        // Public paths bypass auth
        if (PUBLIC_PATHS.contains(path)) return false;
        // All /api/** paths require authentication
        return path.startsWith("/api/");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Skip auth check for public paths or non-API paths
        if (!isProtected(path)) {
            chain.doFilter(request, response);
            return;
        }

        // Read the API key from the request header
        String apiKey = httpRequest.getHeader("X-API-Key");

        // Validate: must be present and match expected value
        if (apiKey == null || apiKey.isBlank()) {
            sendUnauthorized(httpResponse, "Missing API key. Include X-API-Key header.");
            return;
        }

        if (!apiKey.equals(validApiKey)) {
            sendUnauthorized(httpResponse, "Invalid API key.");
            return;
        }

        // Key is valid — continue to the controller
        chain.doFilter(request, response);
    }

    /**
     * Sends a structured 401 Unauthorized response.
     * We build the JSON manually here since we're in a Filter
     * (not yet inside Spring MVC where ResponseEntity works).
     */
    private void sendUnauthorized(HttpServletResponse response, String message)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format(
            "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"%s\"}", message
        ));
    }
}

// ─── MULTIPLE API KEY SUPPORT ─────────────────────────────────────────────────
/**
 * In some systems, different clients have different API keys.
 * This shows how to support a set of valid keys.
 *
 * In production, store keys in a database or secrets manager,
 * not hardcoded here.
 */
@Component
class ApiKeyRegistry {

    // These represent different clients (mobile app, web app, partner system)
    private static final List<String> VALID_KEYS = Arrays.asList(
        "mobile-app-key-abc123",
        "web-app-key-def456",
        "partner-key-ghi789"
    );

    public boolean isValid(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) return false;
        return VALID_KEYS.contains(apiKey);
    }
}

// ─── DEMO CONTROLLER ─────────────────────────────────────────────────────────
/**
 * Test these endpoints:
 *
 *  # ✅ Authorized (valid key)
 *  curl -H "X-API-Key: my-secret-key-change-in-production" \
 *       http://localhost:8080/api/v1/secure-products
 *
 *  # ❌ Missing key → 401
 *  curl http://localhost:8080/api/v1/secure-products
 *
 *  # ❌ Wrong key → 401
 *  curl -H "X-API-Key: wrong-key" http://localhost:8080/api/v1/secure-products
 *
 *  # ✅ Public route (no key needed)
 *  curl http://localhost:8080/actuator/health
 */
@RestController
@RequestMapping("/api/v1/secure-products")
class SecureProductController {

    @GetMapping
    public ResponseEntity<String> getAll() {
        return ResponseEntity.ok("✅ Authorized! Here are your products.");
    }

    @GetMapping("/admin")
    public ResponseEntity<String> admin() {
        return ResponseEntity.ok("✅ Admin access granted.");
    }
}

/*
 * ─── SECURITY BEST PRACTICES FOR API KEYS ────────────────────────────────────
 *
 *  ✅  Store keys in environment variables, not in source code
 *  ✅  Use HTTPS — keys are plaintext, TLS protects them in transit
 *  ✅  Rotate keys regularly (have a process to invalidate old ones)
 *  ✅  Scope keys (read-only vs read-write vs admin)
 *  ✅  Log key usage for audit trails (log which key, not the key itself)
 *  ✅  Rate limit by key (not just by IP)
 *
 *  ❌  Never commit API keys to Git (use .gitignore + environment variables)
 *  ❌  Don't log the full key value — log only the first/last 4 chars
 *  ❌  Don't return the key in any API response
 *
 * ─── HEADER ALTERNATIVES ─────────────────────────────────────────────────────
 *
 *  X-API-Key: my-key          ← most common custom header approach
 *  Authorization: ApiKey my-key  ← uses the standard Authorization header
 *  Authorization: Bearer my-key  ← some services use Bearer for API keys too
 *
 *  Stick with X-API-Key for API key auth — it's the clearest signal of intent.
 */
