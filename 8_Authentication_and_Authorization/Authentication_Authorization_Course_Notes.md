# 🔐 Authentication & Authorization — Course Notes

> **Module 8 | GDP Term 2 | Digital Fortress Institute**

---

## 📌 Core Concepts

### Authentication vs Authorization

| | Authentication (AuthN) | Authorization (AuthZ) |
|---|---|---|
| **Question** | Who are you? | What can you do? |
| **Example** | Logging in with email + password | Can this user delete a product? |
| **When it runs** | First — verifies identity | After AuthN — checks permissions |
| **In Spring** | `AuthenticationManager` | `@PreAuthorize`, `SecurityFilterChain` |

> These two always work together. You cannot authorize someone you haven't authenticated.

---

### Sessions vs Tokens

| | Session-Based Auth | Token-Based Auth (JWT) |
|---|---|---|
| **State stored** | Server (session store) | Client (browser/app) |
| **Scalability** | Hard — server must share sessions | Easy — stateless, no shared state |
| **Works with** | Monoliths, server-rendered apps | APIs, mobile apps, microservices |
| **Revocation** | Easy (delete session) | Harder (blacklist required) |
| **Default in Spring** | Session cookies | Manual config required |

> Modern REST APIs use **token-based auth (JWT)**. We'll focus on this.

---

## 1. 🔑 JWT — JSON Web Tokens

### 1.1 What is a JWT?

A JWT is a **signed, self-contained token** that encodes user information and can be verified without hitting the database on every request.

**Structure:** `header.payload.signature`

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9    ← Header (Base64)
.
eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0
                                           ← Payload (Base64)
.
SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
                                           ← Signature (HMAC-SHA256)
```

**Decoded Header:**
```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

**Decoded Payload (Claims):**
```json
{
  "sub": "user@example.com",
  "role": "USER",
  "iat": 1700000000,
  "exp": 1700003600
}
```

| Claim | Meaning |
|---|---|
| `sub` | Subject — user identity (email or ID) |
| `iat` | Issued At — when the token was created (Unix timestamp) |
| `exp` | Expiry — when the token expires |
| `role` / `roles` | Custom claim — user's permissions |

> ⚠️ JWTs are **signed, not encrypted**. Anyone can Base64-decode and read the payload. Never put sensitive data (passwords, card numbers) in a JWT.

---

### 1.2 JWT Signing Algorithms

| Algorithm | Type | Use Case |
|---|---|---|
| `HS256` | Symmetric (shared secret) | Single service (server signs and verifies) |
| `RS256` | Asymmetric (public/private key) | Multiple services (private key signs, public key verifies) |

We'll use **HS256** for simplicity.

---

### 1.3 Access Token vs Refresh Token

| | Access Token | Refresh Token |
|---|---|---|
| **Purpose** | Authenticate API requests | Get a new access token |
| **Lifetime** | Short (15 min – 1 hour) | Long (7–30 days) |
| **Sent with** | Every API request | Only to `/auth/refresh` |
| **Where stored** | Memory / `Authorization` header | `HttpOnly` cookie (secure) |

**Flow:**
```
Login → [access token (15min) + refresh token (7 days)]
API call → send access token in header
Access token expires → use refresh token to get new access token
Refresh token expires → user must log in again
```

---

## 2. 🔒 Password Security

**Plain text passwords are never acceptable.** Use BCrypt — a one-way hashing algorithm with a built-in salt.

```java
// Hash a password (done at registration)
BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
String hashed = encoder.encode("myPassword123");
// → "$2a$10$7EqJtq98hPqEX7fNZaFWouHPzSHB4zFxzqZOl98NFPVJT7sFR5..."

// Verify a password (done at login)
boolean matches = encoder.matches("myPassword123", hashed);
// → true
```

**BCrypt properties:**
- One-way: you cannot reverse the hash to get the password
- Salted: two hashes of the same password are always different
- Cost factor: `$2a$10$` — `10` is the work factor (higher = slower = more secure)

---

## 3. 🛡️ Spring Security Setup

### 3.1 Dependencies (pom.xml)

```xml
<!-- Spring Security -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<!-- JWT (JJWT library) -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.12.3</version>
    <scope>runtime</scope>
</dependency>
```

---

### 3.2 The Spring Security Filter Chain

When Spring Security is on the classpath, it automatically adds a **filter chain** that intercepts every request. You configure it via a `SecurityFilterChain` bean.

```
Request
   ↓
[Spring Security Filter Chain]
   ├── UsernamePasswordAuthenticationFilter  ← handles form login
   ├── JwtAuthenticationFilter              ← our custom filter
   ├── ExceptionTranslationFilter           ← converts exceptions to 401/403
   └── FilterSecurityInterceptor            ← checks permissions
   ↓
DispatcherServlet (your controllers)
```

---

### 3.3 SecurityFilterChain Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // Enables @PreAuthorize on methods
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — not needed for stateless JWT APIs
            .csrf(csrf -> csrf.disable())

            // Session management — STATELESS (no server-side sessions)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Route authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public routes — no token required
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                // Admin-only routes
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                // All other /api/** routes require authentication
                .requestMatchers("/api/**").authenticated()
                // Everything else is public
                .anyRequest().permitAll()
            )

            // Add our JWT filter before Spring's default UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## 4. 🎫 JWT Implementation

### 4.1 JWT Service (Generate & Validate)

```java
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:900000}")  // 15 minutes default
    private long expirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /** Generate a JWT for the given username */
    public String generateToken(String username, String role) {
        return Jwts.builder()
                .subject(username)
                .claim("role", role)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    /** Extract the username (subject) from a token */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** Extract the role claim from a token */
    public String extractRole(String token) {
        return parseClaims(token).get("role", String.class);
    }

    /** Check if the token is valid and not expired */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
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
```

**application.properties:**
```properties
# Generate a Base64 secret: openssl rand -base64 32
jwt.secret=bXktc2VjcmV0LWtleS1mb3ItaHMtMjU2LWFsZ29yaXRobQ==
jwt.expiration-ms=900000
jwt.refresh-expiration-ms=604800000
```

> ⚠️ Store `jwt.secret` in environment variables, never in source code.

---

### 4.2 JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

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

        // 1. Read the Authorization header
        String authHeader = request.getHeader("Authorization");

        // 2. Skip if no token (Spring Security will handle as unauthenticated)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extract the token (remove "Bearer " prefix)
        String token = authHeader.substring(7);

        // 4. Validate the token
        if (!jwtService.isValid(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 5. Extract username and load user details
        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 6. Create authentication object and set in SecurityContext
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
            );
        auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(auth);

        filterChain.doFilter(request, response);
    }
}
```

---

## 5. 👤 User Management

### 5.1 User Entity

```java
@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    public enum Role { ROLE_USER, ROLE_ADMIN }

    // UserDetails interface — Spring Security calls these
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override public String getUsername() { return email; }
    @Override public String getPassword() { return password; }
    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }

    // Getters and setters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
}
```

---

### 5.2 Auth Endpoints (Register & Login)

```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request.getRefreshToken()));
    }
}
```

**Auth response:**
```json
{
  "accessToken": "eyJhbGci...",
  "refreshToken": "eyJhbGci...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

---

## 6. 🛂 Role-Based Access Control (RBAC)

### 6.1 Method-Level Security

With `@EnableMethodSecurity` on your config, you can protect individual controller methods:

```java
@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    // Only ROLE_ADMIN can access this
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() { ... }

    // ADMIN or USER can access this
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile() { ... }

    // User can only access their own resource
    @PreAuthorize("#username == authentication.name")
    @GetMapping("/users/{username}")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) { ... }
}
```

| Expression | Meaning |
|---|---|
| `hasRole('ADMIN')` | User has `ROLE_ADMIN` authority |
| `hasAnyRole('ADMIN', 'USER')` | User has either role |
| `isAuthenticated()` | User is logged in |
| `#param == authentication.name` | SpEL — param matches current user |

---

## 7. 🌐 OAuth2 Basics

### 7.1 What is OAuth2?

OAuth2 is an **authorization framework** that lets users log in with a third-party provider (Google, GitHub, Facebook) without sharing their password with your app.

**Involved parties:**
| Party | Who | Example |
|---|---|---|
| Resource Owner | The user | The person clicking "Login with Google" |
| Client | Your app | Your Spring Boot API |
| Authorization Server | The identity provider | Google's OAuth2 server |
| Resource Server | Your API protecting user data | Your Spring Boot API |

---

### 7.2 Authorization Code Flow (for web apps)

```
User → clicks "Login with Google"
   ↓
Your App  → redirects to Google's auth URL
   ↓
Google    → user logs in + grants permission
   ↓
Google    → redirects back with auth CODE
   ↓
Your App  → exchanges code for ACCESS TOKEN (server-to-server, secure)
   ↓
Your App  → uses access token to get user info from Google
   ↓
Your App  → creates session/JWT for the user
```

---

### 7.3 Spring Boot OAuth2 Login (Google)

**pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-client</artifactId>
</dependency>
```

**application.properties:**
```properties
spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.google.scope=openid,email,profile
```

**Security Config with OAuth2:**
```java
http
    .oauth2Login(oauth2 -> oauth2
        .successHandler(customOAuth2SuccessHandler)
    );
```

---

### 7.4 OAuth2 Resource Server (Protect your API)

When your API accepts tokens issued by an external identity provider (like Auth0 or Keycloak):

**pom.xml:**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
</dependency>
```

**application.properties:**
```properties
spring.security.oauth2.resourceserver.jwt.issuer-uri=https://accounts.google.com
```

**Security config:**
```java
http
    .oauth2ResourceServer(oauth2 -> oauth2
        .jwt(jwt -> jwt.decoder(JwtDecoders.fromIssuerLocation(issuerUri)))
    );
```

---

## 8. 🔐 Security Best Practices

### Token Storage (Frontend)

| Storage | Security | Use for |
|---|---|---|
| `localStorage` | ⚠️ Vulnerable to XSS | Don't store tokens here |
| `sessionStorage` | ⚠️ Vulnerable to XSS | Don't store tokens here |
| `HttpOnly` Cookie | ✅ Safe from JS | Refresh tokens |
| In-memory (JS variable) | ✅ Cleared on tab close | Access tokens |

### Checklist

| ✅ | Check |
|---|---|
| ☐ | Hash all passwords with BCrypt — never store plain text |
| ☐ | Use short-lived access tokens (15 minutes) |
| ☐ | Use long-lived refresh tokens stored in `HttpOnly` cookies |
| ☐ | Store JWT secret in environment variables, never in code |
| ☐ | Validate token on every request — never trust the client |
| ☐ | Use HTTPS — tokens in transit must be encrypted |
| ☐ | Implement token revocation / blacklist for logout |
| ☐ | Return 401 (not 403) when the user is not authenticated |
| ☐ | Return 403 (not 401) when the user lacks permission |
| ☐ | Rotate secrets periodically |
| ☐ | Log auth events (login, logout, failed attempts) for audit |

---

## ✅ Module Summary

| Concept | Key Takeaway |
|---|---|
| AuthN vs AuthZ | Always authenticate first, then authorize |
| JWT | Signed, Base64-encoded token; never encode secrets in it |
| Access + Refresh tokens | Short-lived access + long-lived refresh |
| BCrypt | One-way hash; Spring provides `BCryptPasswordEncoder` |
| `UserDetails` | Spring's interface for user data; implement it on your entity |
| `SecurityFilterChain` | Configure public/private routes and add JWT filter |
| `@PreAuthorize` | Method-level RBAC; needs `@EnableMethodSecurity` |
| OAuth2 | Delegate authentication to Google/GitHub; don't reinvent login |

---

*Module 8 — Authentication & Authorization | GDP Term 2 | Digital Fortress Institute*
