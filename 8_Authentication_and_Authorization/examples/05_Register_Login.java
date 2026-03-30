package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 05: Register & Login Endpoints
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - User entity implementing UserDetails (Spring's user contract)
 *  - UserRepository for database access
 *  - UserDetailsService implementation (Spring Security hook)
 *  - AuthService: register and login business logic
 *  - AuthController: /api/v1/auth/register, /login, /refresh endpoints
 *  - Request/Response DTOs for auth endpoints
 *
 * Prerequisites:
 *  - Example 02 (BCrypt)
 *  - Example 04 (JwtService)
 *  - spring-boot-starter-data-jpa + H2/PostgreSQL
 */

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 1: USER ENTITY
// ══════════════════════════════════════════════════════════════════════════════

/**
 * The User entity is both a JPA @Entity (maps to the 'users' database table)
 * AND implements Spring Security's UserDetails interface.
 *
 * UserDetails is Spring Security's contract for user information.
 * Spring will call getUsername(), getPassword(), getAuthorities() etc.
 */
@Entity
@Table(name = "users")
class AppUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;           // Stores the BCrypt hash — never plaintext

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(nullable = false)
    private boolean accountNonLocked = true;

    public enum UserRole {
        ROLE_USER,
        ROLE_ADMIN,
        ROLE_MODERATOR
    }

    // Default constructor required by JPA
    public AppUser() {}

    public AppUser(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ─── UserDetails interface (Spring Security calls these) ─────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Convert our enum to Spring's GrantedAuthority
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email;  // We use email as the username
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return accountNonLocked; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }

    // ─── Getters and setters ──────────────────────────────────────────────────
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 2: REPOSITORY
// ══════════════════════════════════════════════════════════════════════════════

@Repository
interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    boolean existsByEmail(String email);
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 3: UserDetailsService IMPLEMENTATION
// ══════════════════════════════════════════════════════════════════════════════

/**
 * Spring Security calls loadUserByUsername() during authentication.
 * We tell Spring how to find our user from the database.
 *
 * This is also called by JwtAuthenticationFilter (Example 04) to load
 * the full user after extracting the username from the JWT.
 */
@Service
class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                    "User not found with email: " + email
                ));
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 4: REQUEST/RESPONSE DTOs
// ══════════════════════════════════════════════════════════════════════════════

class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
        regexp = "^(?=.*[A-Z])(?=.*[0-9]).+$",
        message = "Password must contain at least one uppercase letter and one number"
    )
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    public String getFullName() { return fullName; }
    public void setFullName(String n) { this.fullName = n; }
}

class LoginRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public String getEmail() { return email; }
    public void setEmail(String e) { this.email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}

class RefreshTokenRequest {
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String t) { this.refreshToken = t; }
}

class AuthResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;        // Seconds until access token expires
    private String email;
    private String role;

    public AuthResponse(String accessToken, String refreshToken, long expiresIn,
                        String email, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.expiresIn = expiresIn;
        this.email = email;
        this.role = role;
    }

    public String getAccessToken() { return accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public String getTokenType() { return tokenType; }
    public long getExpiresIn() { return expiresIn; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 5: AUTH SERVICE
// ══════════════════════════════════════════════════════════════════════════════

@Service
class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Registration flow:
     *  1. Check email is not already taken
     *  2. Hash the password
     *  3. Save the new user
     *  4. Issue JWT tokens immediately (auto-login after registration)
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered: " + request.getEmail());
        }

        AppUser user = new AppUser(
            request.getEmail(),
            passwordEncoder.encode(request.getPassword()),  // ✅ Hashed
            AppUser.UserRole.ROLE_USER                      // Default role
        );
        userRepository.save(user);

        return createAuthResponse(user);
    }

    /**
     * Login flow:
     *  1. Use Spring's AuthenticationManager to verify credentials
     *     (internally calls UserDetailsService.loadUserByUsername + BCrypt.matches)
     *  2. If invalid → AuthenticationException → 401
     *  3. If valid → issue JWT tokens
     */
    public AuthResponse login(LoginRequest request) {
        // This line does the heavy lifting:
        // - Calls loadUserByUsername(email) to find the user
        // - Calls passwordEncoder.matches(raw, hashed) to verify password
        // - Throws BadCredentialsException if either check fails
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        AppUser user = (AppUser) auth.getPrincipal();
        return createAuthResponse(user);
    }

    /**
     * Refresh token flow:
     *  1. Validate the refresh token
     *  2. Extract username
     *  3. Load user from DB (check they're still active)
     *  4. Issue a new access token
     */
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.isValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String email = jwtService.extractUsername(refreshToken);
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Issue a new access token (and rotate the refresh token for security)
        return createAuthResponse(user);
    }

    private AuthResponse createAuthResponse(AppUser user) {
        String accessToken  = jwtService.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());
        return new AuthResponse(accessToken, refreshToken, 900, user.getEmail(), user.getRole().name());
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 6: AUTH CONTROLLER
// ══════════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v1/auth")
class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * POST /api/v1/auth/register
     * Body: { "email": "...", "password": "...", "fullName": "..." }
     *
     * Returns 201 Created + JWT tokens on success.
     * Returns 400 if validation fails or email is taken.
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/v1/auth/login
     * Body: { "email": "...", "password": "..." }
     *
     * Returns 200 OK + JWT tokens on success.
     * Returns 401 if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/v1/auth/refresh
     * Body: { "refreshToken": "..." }
     *
     * Returns 200 OK + new access token.
     * Returns 401 if refresh token is invalid/expired.
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        AuthResponse response = authService.refreshToken(request.getRefreshToken());
        return ResponseEntity.ok(response);
    }
}

/*
 * ─── REGISTER EXAMPLE ────────────────────────────────────────────────────────
 *
 *  curl -X POST http://localhost:8080/api/v1/auth/register \
 *    -H "Content-Type: application/json" \
 *    -d '{"email":"user@example.com","password":"Password123","fullName":"Jane Doe"}'
 *
 *  Response (201 Created):
 *  {
 *    "accessToken": "eyJhbG...",
 *    "refreshToken": "eyJhbG...",
 *    "tokenType": "Bearer",
 *    "expiresIn": 900,
 *    "email": "user@example.com",
 *    "role": "ROLE_USER"
 *  }
 *
 * ─── LOGIN EXAMPLE ───────────────────────────────────────────────────────────
 *
 *  curl -X POST http://localhost:8080/api/v1/auth/login \
 *    -H "Content-Type: application/json" \
 *    -d '{"email":"user@example.com","password":"Password123"}'
 *
 * ─── REFRESH EXAMPLE ─────────────────────────────────────────────────────────
 *
 *  curl -X POST http://localhost:8080/api/v1/auth/refresh \
 *    -H "Content-Type: application/json" \
 *    -d '{"refreshToken":"eyJhbG..."}'
 *
 * ─── USING THE ACCESS TOKEN ───────────────────────────────────────────────────
 *
 *  curl http://localhost:8080/api/v1/products \
 *    -H "Authorization: Bearer eyJhbG..."
 */
