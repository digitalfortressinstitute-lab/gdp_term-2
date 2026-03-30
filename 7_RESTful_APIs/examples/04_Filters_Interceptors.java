package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 04: Filters & Interceptors
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates middleware in Spring Boot:
 *
 *  FILTER  (jakarta.servlet.Filter):
 *   - Runs at the Servlet container level (before Spring MVC)
 *   - Used for: logging, CORS, authentication headers, rate limiting
 *
 *  INTERCEPTOR (HandlerInterceptor):
 *   - Runs inside the Spring MVC layer (after the filter chain)
 *   - Has access to the matched Handler (controller method)
 *   - Used for: adding response headers, timing, user context
 *
 *  Request lifecycle:
 *
 *  Client Request
 *      ↓
 *  [Filter 1] → [Filter 2] → [Filter N]
 *      ↓
 *  DispatcherServlet  (Spring MVC front controller)
 *      ↓
 *  [Interceptor.preHandle()]
 *      ↓
 *  Controller Method executes
 *      ↓
 *  [Interceptor.postHandle()]  ← runs before view is rendered
 *      ↓
 *  Response sent to client
 *      ↓
 *  [Interceptor.afterCompletion()]  ← always runs (even on error)
 */

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.logging.Logger;

// ─── FILTER 1: Request Logging ────────────────────────────────────────────────
/**
 * Logs every incoming request and outgoing response with timing.
 * @Order(1) ensures this runs before other filters.
 */
@Component
@Order(1)
class RequestLoggingFilter implements Filter {

    private static final Logger log = Logger.getLogger(RequestLoggingFilter.class.getName());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        String requestId = UUID.randomUUID().toString().substring(0, 8);

        // Add correlation ID to response so clients/devs can trace requests
        httpResponse.addHeader("X-Request-ID", requestId);

        log.info(String.format("[%s] [%s] → %s %s",
            LocalDateTime.now(), requestId,
            httpRequest.getMethod(), httpRequest.getRequestURI()));

        // Pass the request down the filter chain (to the next filter or controller)
        chain.doFilter(request, response);

        // This runs AFTER the controller has returned a response
        long duration = System.currentTimeMillis() - startTime;
        log.info(String.format("[%s] [%s] ← %s %s | Status: %d | Duration: %dms",
            LocalDateTime.now(), requestId,
            httpRequest.getMethod(), httpRequest.getRequestURI(),
            httpResponse.getStatus(), duration));
    }
}

// ─── FILTER 2: Security Headers ──────────────────────────────────────────────
/**
 * Adds standard HTTP security headers to every response.
 * These protect against common web vulnerabilities.
 * @Order(2) — runs after the logging filter.
 */
@Component
@Order(2)
class SecurityHeadersFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Prevent clickjacking
        httpResponse.addHeader("X-Frame-Options", "DENY");

        // Prevent MIME-type sniffing
        httpResponse.addHeader("X-Content-Type-Options", "nosniff");

        // Enable XSS protection (older browsers)
        httpResponse.addHeader("X-XSS-Protection", "1; mode=block");

        // Enforce HTTPS for 1 year
        httpResponse.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

        // Content Security Policy — restrict what resources the page can load
        httpResponse.addHeader("Content-Security-Policy", "default-src 'self'");

        chain.doFilter(request, response);
    }
}

// ─── INTERCEPTOR 1: Request Timing ───────────────────────────────────────────
/**
 * Measures how long each controller method takes to execute.
 * Adds the timing to the response header.
 *
 * Interceptors have access to the handler object (the controller method)
 * which filters don't have.
 */
@Component
class RequestTimingInterceptor implements HandlerInterceptor {

    /**
     * Called BEFORE the controller method executes.
     * Return true to continue processing; false to abort (short-circuit).
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {
        // Store the start time in the request for use in afterCompletion
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;  // Always continue (this interceptor never blocks requests)
    }

    /**
     * Called AFTER the controller method executes (before response is written).
     * Add timing header here.
     */
    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            response.addHeader("X-Response-Time-Ms", String.valueOf(duration));
        }
    }
}

// ─── INTERCEPTOR 2: Maintenance Mode ─────────────────────────────────────────
/**
 * Example of an interceptor that SHORT-CIRCUITS a request.
 * When maintenance mode is ON, every request returns 503 Service Unavailable.
 *
 * This shows how to use preHandle() to block requests conditionally.
 */
@Component
class MaintenanceModeInterceptor implements HandlerInterceptor {

    // Toggle this flag via application.properties or a config bean in production
    private static final boolean MAINTENANCE_MODE = false;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws IOException {

        if (MAINTENANCE_MODE) {
            response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);  // 503
            response.setContentType("application/json");
            response.getWriter().write(
                "{\"status\":503,\"error\":\"Service Unavailable\"," +
                "\"message\":\"System is under maintenance. Please try again later.\"}"
            );
            return false;  // false = STOP processing; controller will NOT run
        }

        return true;  // true = CONTINUE; controller will run normally
    }
}

// ─── WEB MVC CONFIGURATION ────────────────────────────────────────────────────
/**
 * This is where you register interceptors with Spring MVC.
 * Without this, Spring doesn't know about your interceptors.
 */
@Configuration
class WebMvcConfig implements WebMvcConfigurer {

    private final RequestTimingInterceptor requestTimingInterceptor;
    private final MaintenanceModeInterceptor maintenanceModeInterceptor;

    public WebMvcConfig(RequestTimingInterceptor requestTimingInterceptor,
                        MaintenanceModeInterceptor maintenanceModeInterceptor) {
        this.requestTimingInterceptor = requestTimingInterceptor;
        this.maintenanceModeInterceptor = maintenanceModeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Register timing interceptor — applies to all /api/** routes
        registry.addInterceptor(requestTimingInterceptor)
                .addPathPatterns("/api/**");

        // Register maintenance mode interceptor — applies to all routes
        registry.addInterceptor(maintenanceModeInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/health");  // Exclude health check
    }
}

/*
 * ─── FILTER vs INTERCEPTOR SUMMARY ──────────────────────────────────────────
 *
 *  Feature                         Filter              Interceptor
 *  ─────────────────────────────── ─────────────────── ─────────────────────
 *  Layer                           Servlet (before MVC) Spring MVC (in MVC)
 *  Registered via                  @Component + @Order  WebMvcConfigurer
 *  Access to Handler (controller)  ❌ No               ✅ Yes
 *  Best for                        CORS, auth, rate     Logging, auth,
 *                                  limiting, logging    response headers
 *  Can block requests              ✅ Yes               ✅ Yes (return false)
 *  Runs on error responses         ✅ Yes               Partially
 *
 * ─── RESPONSE HEADERS YOU WILL SEE ──────────────────────────────────────────
 *
 *  X-Request-ID: abc12345
 *  X-Response-Time-Ms: 23
 *  X-Frame-Options: DENY
 *  X-Content-Type-Options: nosniff
 *  X-XSS-Protection: 1; mode=block
 *  Strict-Transport-Security: max-age=31536000; includeSubDomains
 *  Content-Security-Policy: default-src 'self'
 */
