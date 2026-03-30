package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 03: Spring Security Setup
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - The Spring Security filter chain
 *  - SecurityFilterChain bean configuration
 *  - Stateless session management (for JWT-based APIs)
 *  - Route-level authorization rules (permitAll, authenticated, hasRole)
 *  - Disabling CSRF for REST APIs
 *  - Custom 401 / 403 entry points
 *
 * Dependencies (pom.xml):
 *   spring-boot-starter-security
 *   spring-boot-starter-web
 */

import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;

// ─── CUSTOM 401 HANDLER ───────────────────────────────────────────────────────
/**
 * Spring Security's default 401 response is a redirect to a login page.
 * For REST APIs, we want to return structured JSON instead.
 *
 * This is called when:
 *  - The request hits a protected route without a valid JWT
 *  - The JWT is expired or malformed
 */
@Component
class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         org.springframework.security.core.AuthenticationException authException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"status\":401,\"error\":\"Unauthorized\"," +
            "\"message\":\"Authentication required. Please include a valid Bearer token.\"}"
        );
    }
}

// ─── CUSTOM 403 HANDLER ───────────────────────────────────────────────────────
/**
 * Called when a user IS authenticated but tries to access a resource
 * they don't have permission for.
 *
 * Example: ROLE_USER trying to access an ADMIN endpoint.
 */
@Component
class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       org.springframework.security.access.AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);  // 403
        response.setContentType("application/json");
        response.getWriter().write(
            "{\"status\":403,\"error\":\"Forbidden\"," +
            "\"message\":\"You do not have permission to access this resource.\"}"
        );
    }
}

// ─── SECURITY CONFIGURATION ───────────────────────────────────────────────────
/**
 * The central security config class.
 *
 * @EnableWebSecurity    → activates Spring Security's web support
 * @EnableMethodSecurity → enables @PreAuthorize, @PostAuthorize on methods
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
class SecurityConfig {

    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    // JwtAuthenticationFilter will be defined in Example 04
    // We declare it as a parameter here to show how it's wired
    // private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationEntryPoint authEntryPoint,
                          CustomAccessDeniedHandler accessDeniedHandler) {
        this.authEntryPoint = authEntryPoint;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // ─── CSRF ──────────────────────────────────────────────────────
            // Disable CSRF for REST APIs. CSRF is needed for browser-based
            // session apps (form submissions). JWT-based APIs are not vulnerable
            // to CSRF because they use the Authorization header, not cookies.
            .csrf(csrf -> csrf.disable())

            // ─── SESSION MANAGEMENT ────────────────────────────────────────
            // STATELESS = Spring will never create or use an HttpSession.
            // This is required for JWT-based APIs.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // ─── EXCEPTION HANDLING ────────────────────────────────────────
            // Use our custom JSON handlers instead of Spring's HTML defaults
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)      // 401
                .accessDeniedHandler(accessDeniedHandler)       // 403
            )

            // ─── ROUTE AUTHORIZATION RULES ─────────────────────────────────
            // Rules are evaluated TOP TO BOTTOM — first match wins.
            .authorizeHttpRequests(auth -> auth

                // Public auth routes — no token required
                .requestMatchers("/api/v1/auth/**").permitAll()

                // Public GET endpoints — read-only access without auth
                .requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()

                // Admin-only routes — must have ROLE_ADMIN
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")

                // Actuator health — public
                .requestMatchers("/actuator/health").permitAll()

                // All other /api/** routes require authentication
                .requestMatchers("/api/**").authenticated()

                // Everything else is permitted (static resources, etc.)
                .anyRequest().permitAll()
            );

            // ─── JWT FILTER ────────────────────────────────────────────────
            // In Example 04, we add our JwtAuthenticationFilter here:
            // .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ─── BEANS ────────────────────────────────────────────────────────────────

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager is needed for the login flow.
     * Spring Boot auto-configures this based on your UserDetailsService.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}

/*
 * ─── THE SPRING SECURITY FILTER CHAIN ────────────────────────────────────────
 *
 *  Every HTTP request passes through a chain of filters before reaching
 *  your controllers. Spring Security adds its filters automatically.
 *
 *  Standard filter order (simplified):
 *
 *  1. CorsFilter                        ← handles CORS
 *  2. SecurityContextPersistenceFilter  ← loads/saves SecurityContext
 *  3. LogoutFilter                      ← handles /logout
 *  4. [Your JwtAuthenticationFilter]    ← our custom filter (added here)
 *  5. UsernamePasswordAuthenticationFilter ← handles form login
 *  6. ExceptionTranslationFilter        ← converts exceptions → 401/403
 *  7. FilterSecurityInterceptor         ← checks route authorization
 *
 *  Our JWT filter runs at position 4 (before Spring's own auth filter)
 *  so we can set the authentication BEFORE Spring checks it.
 *
 * ─── ANNOTATION REFERENCE ────────────────────────────────────────────────────
 *
 *  @EnableWebSecurity      → Activates Spring Security's web support
 *  @EnableMethodSecurity   → Enables @PreAuthorize on controllers/services
 *
 *  authorizeHttpRequests() matchers:
 *   .permitAll()           → Anyone can access (authenticated or not)
 *   .authenticated()       → Must have a valid JWT/session
 *   .hasRole("ADMIN")      → Must have ROLE_ADMIN authority
 *   .hasAnyRole("A","B")   → Must have at least one of these roles
 *   .denyAll()             → Block everything (useful for future paths)
 *
 * ─── IMPORTANT BEHAVIOUR ─────────────────────────────────────────────────────
 *
 *  Q: What happens if I add spring-boot-starter-security to pom.xml?
 *  A: Spring Security locks down ALL routes by default with a generated password.
 *     You MUST define a SecurityFilterChain bean to configure your own rules.
 *
 *  Q: What happens if no JWT is provided to an authenticated route?
 *  A: Our JwtAuthenticationEntryPoint returns 401 with our JSON format.
 *
 *  Q: What happens if a USER tries to access an ADMIN route?
 *  A: Our CustomAccessDeniedHandler returns 403 with our JSON format.
 */
