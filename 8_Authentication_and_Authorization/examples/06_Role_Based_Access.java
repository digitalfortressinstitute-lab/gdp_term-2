package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 06: Role-Based Access Control (RBAC)
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - Role-based authorization with @PreAuthorize
 *  - Method-level security (requires @EnableMethodSecurity in SecurityConfig)
 *  - Accessing the current authenticated user
 *  - Protecting routes in SecurityFilterChain vs @PreAuthorize
 *  - Ownership checks — a user can only access their own resources
 */

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

// ─── RBAC OVERVIEW ────────────────────────────────────────────────────────────
/**
 * Two layers of authorization in Spring Security:
 *
 *  LAYER 1 — Route-level (in SecurityFilterChain):
 *   Coarse-grained. Controls which URL paths require which roles.
 *   Applied BEFORE the request reaches the controller.
 *
 *   Example:
 *   .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
 *   .requestMatchers("/api/**").authenticated()
 *
 *  LAYER 2 — Method-level (@PreAuthorize):
 *   Fine-grained. Controls individual controller or service methods.
 *   Applied INSIDE the controller/service at runtime.
 *   Requires: @EnableMethodSecurity in your SecurityConfig
 *
 *   Example:
 *   @PreAuthorize("hasRole('ADMIN')")
 *   public List<User> getAllUsers() { ... }
 *
 *  Use BOTH layers for defence in depth.
 */

// ─── ADMIN CONTROLLER ─────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/v1/admin")
class AdminController {

    /**
     * GET /api/v1/admin/users
     * Only ROLE_ADMIN can list all users.
     *
     * @PreAuthorize runs BEFORE the method body.
     * If the user doesn't have ROLE_ADMIN, Spring throws AccessDeniedException → 403.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<String>> getAllUsers() {
        // Imagine fetching users from a database here
        List<String> users = List.of("alice@example.com", "bob@example.com", "charlie@example.com");
        return ResponseEntity.ok(users);
    }

    /**
     * DELETE /api/v1/admin/users/{id}
     * Only ADMIN can delete users.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        System.out.println("Admin deleting user: " + id);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/admin/stats
     * Multiple roles can access this.
     */
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(Map.of("totalUsers", 150, "totalProducts", 320));
    }
}

// ─── PRODUCT CONTROLLER WITH RBAC ─────────────────────────────────────────────

@RestController
@RequestMapping("/api/v1/products")
class SecuredProductController {

    /**
     * GET /api/v1/products
     * Public — anyone can read products (no @PreAuthorize needed if marked permitAll in config).
     */
    @GetMapping
    public ResponseEntity<List<String>> getAll() {
        return ResponseEntity.ok(List.of("Laptop", "Mouse", "Keyboard"));
    }

    /**
     * POST /api/v1/products
     * Only authenticated users can create products.
     */
    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<String> create(@RequestBody String name) {
        return ResponseEntity.status(201).body("Created: " + name);
    }

    /**
     * DELETE /api/v1/products/{id}
     * Only ADMINs can delete products.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        System.out.println("Admin deleting product: " + id);
        return ResponseEntity.noContent().build();
    }
}

// ─── USER PROFILE CONTROLLER (OWNERSHIP CHECKS) ──────────────────────────────

@RestController
@RequestMapping("/api/v1/users")
class UserProfileController {

    /**
     * GET /api/v1/users/{email}
     * A user can only access their OWN profile.
     * ADMINs can access any profile.
     *
     * Spring Expression Language (SpEL):
     *   #email           → the method parameter named 'email'
     *   authentication   → the current authentication object
     *   authentication.name → the current username (email)
     *
     * This evaluates to:
     *   "Does #email match the logged-in user's name, OR is the user an ADMIN?"
     */
    @PreAuthorize("#email == authentication.name or hasRole('ADMIN')")
    @GetMapping("/{email}")
    public ResponseEntity<Map<String, String>> getProfile(@PathVariable String email) {
        return ResponseEntity.ok(Map.of(
            "email", email,
            "fullName", "Jane Doe",
            "role", "ROLE_USER"
        ));
    }

    /**
     * PUT /api/v1/users/{email}
     * A user can only update their own profile.
     */
    @PreAuthorize("#email == authentication.name")
    @PutMapping("/{email}")
    public ResponseEntity<String> updateProfile(
            @PathVariable String email,
            @RequestBody Map<String, String> updates) {
        return ResponseEntity.ok("Profile updated for: " + email);
    }
}

// ─── ACCESSING THE CURRENT USER ───────────────────────────────────────────────

@RestController
@RequestMapping("/api/v1/me")
class MeController {

    /**
     * GET /api/v1/me
     * Returns the currently logged-in user's information.
     *
     * Two ways to get the current user:
     *  1. @AuthenticationPrincipal — injected directly as a method parameter
     *  2. SecurityContextHolder.getContext().getAuthentication()
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal AppUser currentUser  // ← Spring injects the logged-in user
    ) {
        return ResponseEntity.ok(Map.of(
            "email", currentUser.getEmail(),
            "role", currentUser.getRole().name(),
            "enabled", currentUser.isEnabled()
        ));
    }

    /**
     * Alternative: Access the current user via SecurityContextHolder.
     * Useful in service layer where @AuthenticationPrincipal isn't available.
     */
    @GetMapping("/via-context")
    public ResponseEntity<String> viaContext() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        String role = auth.getAuthorities().iterator().next().getAuthority();
        return ResponseEntity.ok("Hello " + username + " | Role: " + role);
    }
}

/*
 * ─── @PreAuthorize EXPRESSION REFERENCE ─────────────────────────────────────
 *
 *  Expression                             | Meaning
 *  ─────────────────────────────────────── | ─────────────────────────────────
 *  hasRole('ADMIN')                       | User has ROLE_ADMIN
 *  hasAnyRole('ADMIN', 'MOD')             | User has any of these roles
 *  isAuthenticated()                      | User is logged in
 *  isAnonymous()                          | User is NOT logged in
 *  permitAll()                            | Anyone (authenticated or not)
 *  denyAll()                              | Blocks everyone
 *  #param == authentication.name         | Method param equals logged-in username
 *  authentication.principal.id == #id    | Principal's ID matches param
 *
 *  hasRole('X') checks for 'ROLE_X' authority automatically.
 *  hasAuthority('ROLE_X') checks for the exact authority string.
 *
 * ─── TEST RBAC ────────────────────────────────────────────────────────────────
 *
 *  # Login as a regular user:
 *  TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
 *    -H "Content-Type: application/json" \
 *    -d '{"email":"user@example.com","password":"Password123"}' \
 *    | jq -r '.accessToken')
 *
 *  # Try admin route as USER → 403 Forbidden
 *  curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/admin/users
 *
 *  # Access own profile → 200 OK
 *  curl -H "Authorization: Bearer $TOKEN" \
 *    "http://localhost:8080/api/v1/users/user@example.com"
 *
 *  # Access someone else's profile → 403 Forbidden
 *  curl -H "Authorization: Bearer $TOKEN" \
 *    "http://localhost:8080/api/v1/users/other@example.com"
 *
 *  # Who am I?
 *  curl -H "Authorization: Bearer $TOKEN" http://localhost:8080/api/v1/me
 */
