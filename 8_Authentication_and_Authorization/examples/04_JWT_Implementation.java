package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 04: JWT Implementation
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - JwtService: generating, validating, and parsing JWTs
 *  - JwtAuthenticationFilter: reading and validating JWTs per request
 *  - How to wire the filter into the Spring Security chain
 *
 * Dependencies (pom.xml):
 *   io.jsonwebtoken:jjwt-api:0.12.3
 *   io.jsonwebtoken:jjwt-impl:0.12.3 (runtime)
 *   io.jsonwebtoken:jjwt-jackson:0.12.3 (runtime)
 *   spring-boot-starter-security
 *
 * application.properties:
 *   jwt.secret=bXktc2VjcmV0LWtleS1mb3ItaHMtMjU2LWFsZ29yaXRobQ==
 *   jwt.expiration-ms=900000
 *   jwt.refresh-expiration-ms=604800000
 *
 * Generate a secret: openssl rand -base64 32
 */

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.*;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.Date;

// ─── JWT SERVICE ──────────────────────────────────────────────────────────────
/**
 * Responsible for all JWT operations:
 *  - Generating access tokens
 *  - Generating refresh tokens
 *  - Validating tokens
 *  - Extracting claims (username, role, expiry)
 */
@Service
class JwtService {

    @Value("${jwt.secret:bXktc2VjcmV0LWtleS1mb3ItaHMtMjU2LWFsZ29yaXRobQ==}")
    private String secret;

    @Value("${jwt.expiration-ms:900000}")         // 15 minutes
    private long accessTokenExpirationMs;

    @Value("${jwt.refresh-expiration-ms:604800000}")  // 7 days
    private long refreshTokenExpirationMs;

    // ─── SIGNING KEY ─────────────────────────────────────────────────────────
    /**
     * Decode the Base64 secret and create an HMAC-SHA256 signing key.
     * This key is used to both SIGN (create) and VERIFY (validate) JWTs.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ─── TOKEN GENERATION ─────────────────────────────────────────────────────
    /**
     * Generate a short-lived ACCESS token.
     *
     * @param username the user's email or username
     * @param role     the user's role ("ROLE_USER" or "ROLE_ADMIN")
     * @return signed JWT string
     */
    public String generateAccessToken(String username, String role) {
        return buildToken(username, role, accessTokenExpirationMs);
    }

    /**
     * Generate a long-lived REFRESH token.
     * Refresh tokens contain only the subject — no role information needed.
     */
    public String generateRefreshToken(String username) {
        return buildToken(username, null, refreshTokenExpirationMs);
    }

    private String buildToken(String username, String role, long expirationMs) {
        JwtBuilder builder = Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey());

        if (role != null) {
            builder.claim("role", role);
        }

        return builder.compact();
    }

    // ─── TOKEN VALIDATION ─────────────────────────────────────────────────────
    /**
     * Validate a token: checks signature and expiry.
     * Returns false (instead of throwing) to keep the JWT filter clean.
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            System.err.println("[JWT] Token expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.err.println("[JWT] Unsupported token: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.err.println("[JWT] Malformed token: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("[JWT] Invalid signature: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("[JWT] Empty token: " + e.getMessage());
        }
        return false;
    }

    // ─── CLAIM EXTRACTION ─────────────────────────────────────────────────────
    /** Extract the username (subject claim) from a validated token */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extract the role custom claim */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /** Check if the token is expired (useful for refresh logic) */
    public boolean isExpired(String token) {
        try {
            return parseClaims(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

// ─── JWT AUTHENTICATION FILTER ────────────────────────────────────────────────
/**
 * This filter runs on EVERY request (once per request, hence OncePerRequestFilter).
 *
 * What it does:
 *  1. Reads the "Authorization: Bearer <token>" header
 *  2. Validates the JWT
 *  3. Extracts the username from the token
 *  4. Loads the user from the database (UserDetailsService)
 *  5. Sets the authentication in Spring's SecurityContext
 *
 * After this filter:
 *  - Spring Security knows who the user is
 *  - @PreAuthorize and route rules can check their roles
 */
@Component
class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Read the Authorization header
        String authHeader = request.getHeader("Authorization");

        // Step 2: Skip if header is missing or doesn't start with "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token (strip "Bearer " prefix)
        String token = authHeader.substring(7);

        // Step 4: Validate the token
        if (!jwtService.isValid(token)) {
            // Token is invalid — let Spring Security handle the 401
            filterChain.doFilter(request, response);
            return;
        }

        // Step 5: Extract username from token
        String username = jwtService.extractUsername(token);

        // Step 6: Only proceed if not already authenticated in this request
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 7: Load the full user from the database
            // This verifies the user still exists and is not disabled
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 8: Create an authentication object
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,                             // credentials (not needed after validation)
                    userDetails.getAuthorities()      // roles/authorities from the user object
                );

            // Attach request details (IP, session) to the auth object
            authentication.setDetails(
                new WebAuthenticationDetailsSource().buildDetails(request)
            );

            // Step 9: Set the authentication in the SecurityContext
            // After this, Spring Security knows the current user for this request
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Step 10: Continue down the filter chain to the controller
        filterChain.doFilter(request, response);
    }
}

/*
 * ─── WIRING THE FILTER INTO SECURITY CONFIG ──────────────────────────────────
 *
 *  In SecurityConfig.filterChain(), add:
 *
 *  @Autowired
 *  private JwtAuthenticationFilter jwtFilter;
 *
 *  http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
 *
 * ─── HOW THE AUTHORIZATION HEADER LOOKS ─────────────────────────────────────
 *
 *  Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiO...
 *
 * ─── WHAT GOES IN THE TOKEN (KEEP IT SMALL) ─────────────────────────────────
 *
 *  ✅ Include: sub (username/email), role, iat, exp
 *  ❌ Don't include: password, card numbers, full profile info
 *
 *  Every request sends the token — keep it lean.
 *
 * ─── TEST THE TOKEN LIFECYCLE ────────────────────────────────────────────────
 *
 *  # 1. Login and get a token
 *  curl -X POST http://localhost:8080/api/v1/auth/login \
 *    -H "Content-Type: application/json" \
 *    -d '{"email":"user@example.com","password":"Password123"}'
 *
 *  # 2. Use the token
 *  curl http://localhost:8080/api/v1/products \
 *    -H "Authorization: Bearer eyJhbGci..."
 *
 *  # 3. Call a protected endpoint without a token → 401
 *  curl http://localhost:8080/api/v1/products
 *
 *  # 4. Use an expired/invalid token → 401
 *  curl http://localhost:8080/api/v1/products \
 *    -H "Authorization: Bearer invalid.token.here"
 */
