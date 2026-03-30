package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 07: OAuth2 Basics
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - What OAuth2 is and why it exists
 *  - The Authorization Code Flow (most common, most secure)
 *  - Spring Boot OAuth2 Login (Google / GitHub)
 *  - OAuth2 Resource Server (protecting your API with external JWTs)
 *  - Custom OAuth2 success handler (create your own JWT after OAuth2 login)
 *
 * Dependencies (pom.xml):
 *   spring-boot-starter-oauth2-client      ← for OAuth2 login
 *   spring-boot-starter-oauth2-resource-server  ← for resource server mode
 *
 * application.properties:
 *   spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
 *   spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
 *   spring.security.oauth2.client.registration.google.scope=openid,email,profile
 *   spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
 *   spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
 */

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import org.springframework.context.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.web.*;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

// ─── OAUTH2 EXPLAINED ─────────────────────────────────────────────────────────
/**
 * OAuth2 solves one problem: letting users log into YOUR app using an account
 * they already have at a trusted provider (Google, GitHub, Facebook, etc.)
 * WITHOUT sharing their password with you.
 *
 * Key parties:
 *
 *  Resource Owner     → The user ("I want to log into YourApp using Google")
 *  Client             → Your Spring Boot app (requests access on behalf of the user)
 *  Authorization Server → Google/GitHub (verifies user identity, issues tokens)
 *  Resource Server    → Your API (protects user data; accepts tokens from AuthServer)
 *
 * OAuth2 GRANT TYPES (flows):
 *
 *  1. Authorization Code   ✅ Use this for web/mobile apps
 *     - Secure server-to-server token exchange
 *     - Tokens never exposed in the browser URL
 *
 *  2. Client Credentials   ✅ Use this for machine-to-machine (no user involved)
 *     - Your backend service authenticates to another backend service
 *
 *  3. Implicit             ❌ Deprecated — insecure
 *  4. Password             ❌ Deprecated — never use
 */

// ─── AUTHORIZATION CODE FLOW (Sequence) ──────────────────────────────────────
/**
 *
 *  Step 1: User clicks "Login with Google"
 *          Your app redirects to Google's login page:
 *          https://accounts.google.com/o/oauth2/v2/auth
 *            ?client_id=YOUR_CLIENT_ID
 *            &redirect_uri=http://localhost:8080/login/oauth2/code/google
 *            &response_type=code
 *            &scope=openid email profile
 *
 *  Step 2: User logs into Google and clicks "Allow"
 *
 *  Step 3: Google redirects back to your app with a short-lived AUTH CODE:
 *          http://localhost:8080/login/oauth2/code/google?code=4/abc123...
 *
 *  Step 4: Your app (server-side) exchanges the code for an ACCESS TOKEN:
 *          POST https://oauth2.googleapis.com/token
 *          { code: "4/abc123", client_id: "...", client_secret: "..." }
 *          → { "access_token": "ya29...", "id_token": "eyJhbG..." }
 *
 *  Step 5: Your app fetches the user's profile from Google:
 *          GET https://www.googleapis.com/oauth2/v3/userinfo
 *          Authorization: Bearer ya29...
 *          → { "email": "user@gmail.com", "name": "Jane Doe", "sub": "12345" }
 *
 *  Step 6: Your app creates or updates your local User record
 *          and issues YOUR OWN JWT for subsequent API calls.
 *
 *  Spring Boot handles steps 1–5 automatically with oauth2-client!
 *  You only need to implement step 6 in a custom success handler.
 */

// ─── OAUTH2 SECURITY CONFIG ───────────────────────────────────────────────────

@Configuration
@EnableWebSecurity
class OAuth2SecurityConfig {

    private final OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler;

    public OAuth2SecurityConfig(OAuth2AuthSuccessHandler oAuth2AuthSuccessHandler) {
        this.oAuth2AuthSuccessHandler = oAuth2AuthSuccessHandler;
    }

    @Bean
    public SecurityFilterChain oAuth2FilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/auth/**", "/login/**", "/oauth2/**").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
            )
            // ─── OAuth2 Login ──────────────────────────────────────────────
            .oauth2Login(oauth2 -> oauth2
                // This is the endpoint Spring registers to handle the callback from Google
                // You don't need to create this endpoint yourself
                .redirectionEndpoint(endpoint ->
                    endpoint.baseUri("/login/oauth2/code/*"))
                // After successful OAuth2 login, call our custom handler
                .successHandler(oAuth2AuthSuccessHandler)
            );

        return http.build();
    }
}

// ─── OAUTH2 SUCCESS HANDLER ───────────────────────────────────────────────────
/**
 * Called after successful OAuth2 login (after Spring fetches the user's profile
 * from Google/GitHub). Here we:
 *  1. Extract the user's email from the OAuth2 provider's response
 *  2. Create or update our local User record
 *  3. Issue our own JWT tokens
 *  4. Return them to the client (or redirect to the frontend with tokens in query params)
 */
@Component
class OAuth2AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtService jwtService;
    // In a real app, inject UserRepository to create/update users

    public OAuth2AuthSuccessHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        // Get the user info from the OAuth2 provider (Google/GitHub)
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // Extract email — available in Google's 'email' attribute
        String email = oAuth2User.getAttribute("email");
        String name  = oAuth2User.getAttribute("name");

        System.out.println("OAuth2 login: " + email + " (" + name + ")");

        // In a real app: find or create the user in your database
        // AppUser user = userRepository.findByEmail(email)
        //     .orElseGet(() -> userRepository.save(new AppUser(email, "", UserRole.ROLE_USER)));

        // Issue your own JWT
        String accessToken  = jwtService.generateAccessToken(email, "ROLE_USER");
        String refreshToken = jwtService.generateRefreshToken(email);

        // Option 1: Return tokens in a JSON response body
        response.setContentType("application/json");
        response.getWriter().write(String.format(
            "{\"accessToken\":\"%s\",\"refreshToken\":\"%s\",\"tokenType\":\"Bearer\",\"expiresIn\":900}",
            accessToken, refreshToken
        ));

        // Option 2 (for web apps with a frontend): Redirect with token as a query param
        // getRedirectStrategy().sendRedirect(request, response,
        //     "http://localhost:3000/oauth-callback?token=" + accessToken);
    }
}

// ─── OAUTH2 RESOURCE SERVER ───────────────────────────────────────────────────
/**
 * Use this when your API accepts tokens issued by an EXTERNAL provider
 * (e.g., Auth0, Keycloak, AWS Cognito, Google Identity Platform).
 *
 * The resource server automatically:
 *  1. Reads the Bearer token from the Authorization header
 *  2. Fetches the provider's public keys from the issuer-uri
 *  3. Validates the token signature using those public keys
 *  4. Sets the authentication in SecurityContext
 *
 * Dependencies: spring-boot-starter-oauth2-resource-server
 *
 * application.properties:
 *   spring.security.oauth2.resourceserver.jwt.issuer-uri=https://accounts.google.com
 */

// To enable Resource Server mode, add this to SecurityFilterChain:
//
// http.oauth2ResourceServer(oauth2 -> oauth2
//     .jwt(jwt -> jwt.decoder(JwtDecoders.fromIssuerLocation("https://accounts.google.com")))
// );

// ─── CONTROLLER: OAuth2 Protected Endpoint ───────────────────────────────────

@RestController
@RequestMapping("/api/v1/oauth")
class OAuth2DemoController {

    /**
     * GET /api/v1/oauth/profile
     * Returns the OAuth2 user's attributes from Google.
     * Only callable after successful OAuth2 login (user has an active session).
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> profile(Authentication authentication) {
        OAuth2User user = (OAuth2User) authentication.getPrincipal();
        return ResponseEntity.ok(Map.of(
            "email", user.getAttribute("email") != null ? user.getAttribute("email") : "N/A",
            "name",  user.getAttribute("name")  != null ? user.getAttribute("name")  : "N/A",
            "provider", "Google"
        ));
    }
}

/*
 * ─── OAUTH2 PROVIDER SETUP ────────────────────────────────────────────────────
 *
 *  GOOGLE:
 *   1. Go to console.cloud.google.com → Create Project
 *   2. APIs & Services → Credentials → Create OAuth 2.0 Client ID
 *   3. Application type: Web application
 *   4. Authorized redirect URIs: http://localhost:8080/login/oauth2/code/google
 *   5. Copy Client ID + Client Secret into application.properties (via env vars)
 *
 *  GITHUB:
 *   1. GitHub Settings → Developer settings → OAuth Apps → New OAuth App
 *   2. Homepage URL: http://localhost:8080
 *   3. Authorization callback URL: http://localhost:8080/login/oauth2/code/github
 *   4. Copy Client ID + Client Secret into application.properties (via env vars)
 *
 * ─── LOGIN URL ────────────────────────────────────────────────────────────────
 *
 *  Open in browser (Spring generates these redirect URLs automatically):
 *
 *  Google: http://localhost:8080/oauth2/authorization/google
 *  GitHub: http://localhost:8080/oauth2/authorization/github
 *
 *  Spring will redirect the user to the provider's login page automatically.
 *
 * ─── WHEN TO USE OAUTH2 vs JWT ───────────────────────────────────────────────
 *
 *  Use OAuth2 when:
 *   ✅ You want "Login with Google/GitHub/Facebook"
 *   ✅ You're delegating identity management to a trusted provider
 *   ✅ You want SSO (Single Sign-On) across multiple apps
 *
 *  Use (your own) JWT when:
 *   ✅ You manage user accounts yourself (email + password)
 *   ✅ You're building an API for mobile apps or third-party clients
 *   ✅ You need full control over token claims and lifecycle
 *
 *  In practice: combine both! Let users log in via Google OR email+password.
 *  Always issue your own JWT after any successful login.
 */
