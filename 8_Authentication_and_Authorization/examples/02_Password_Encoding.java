package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 02: Password Encoding with BCrypt
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - Why you must NEVER store plain-text passwords
 *  - How BCrypt hashing works (one-way, salted)
 *  - Using Spring Security's BCryptPasswordEncoder
 *  - Registration and login password flow
 *
 * Dependency (already included in spring-boot-starter-security):
 *   spring-boot-starter-security
 */

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// ─── HOW BCRYPT WORKS ─────────────────────────────────────────────────────────
/**
 * BCrypt is a SLOW, one-way hashing algorithm designed for passwords.
 *
 * Properties:
 *  1. ONE-WAY: You cannot decrypt a BCrypt hash back to the original password
 *  2. SALTED: Each call produces a unique hash even for the same input
 *     (the random salt is embedded inside the hash string)
 *  3. COST FACTOR: The "$2a$10$" prefix means work factor 10
 *     (2^10 = 1024 rounds of hashing — slow by design to resist brute force)
 *
 * Example output for "myPassword123":
 *  Hash 1: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
 *  Hash 2: $2a$10$hvrve3P7kBDvKzp1nBqLyOfq3MmSmBL8G6lGqJsB2Ds/7rqQDVCq6
 *
 * Notice: Hashing "myPassword123" twice gives DIFFERENT hashes (because of the salt)
 * But: BCrypt's matches() can verify both against "myPassword123" ✅
 */

// ─── BCRYPT DEMO ──────────────────────────────────────────────────────────────

public class PasswordEncodingDemo {

    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "myPassword123";

        // ─── HASHING (done at registration) ─────────────────────────────────
        String hash1 = encoder.encode(rawPassword);
        String hash2 = encoder.encode(rawPassword);

        System.out.println("Raw password:  " + rawPassword);
        System.out.println("Hash 1: " + hash1);
        System.out.println("Hash 2: " + hash2);
        System.out.println("Same? " + hash1.equals(hash2));  // false — always different!

        // ─── VERIFICATION (done at login) ────────────────────────────────────
        boolean validLogin   = encoder.matches(rawPassword, hash1);  // true ✅
        boolean invalidLogin = encoder.matches("wrongPassword", hash1);  // false ❌

        System.out.println("\nLogin with correct password: " + validLogin);
        System.out.println("Login with wrong password:   " + invalidLogin);

        // ─── COST FACTOR ─────────────────────────────────────────────────────
        // Default cost = 10. Higher = slower but more secure.
        // For auth systems: 10-12 is a good balance.
        BCryptPasswordEncoder strongEncoder = new BCryptPasswordEncoder(12);
        System.out.println("\n[Cost 12] " + strongEncoder.encode(rawPassword));
    }
}

// ─── WHAT NEVER TO DO ────────────────────────────────────────────────────────
class PasswordAntiPatterns {

    // ❌ WRONG: Storing plain text
    String badRegistration(String password) {
        // return userRepository.save(new User(email, password));  ← TERRIBLE
        throw new UnsupportedOperationException("Never do this!");
    }

    // ❌ WRONG: Using MD5 or SHA1 (too fast — easy to brute force)
    // ❌ WRONG: Encrypting passwords (reversible — if key leaks, all passwords exposed)
    // ❌ WRONG: Hashing without a salt (allows rainbow table attacks)
    // ✅ CORRECT: BCrypt, Argon2id, scrypt — all are slow, salted, one-way
}

// ─── AUTH SERVICE: Registration & Login flow ─────────────────────────────────

@Service
class PasswordAwareAuthService {

    private final PasswordEncoder passwordEncoder;

    // PasswordEncoder bean is defined in SecurityConfig
    public PasswordAwareAuthService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registration flow:
     *  1. Check user doesn't already exist
     *  2. Hash the raw password with BCrypt
     *  3. Save the hashed password to the database
     *  4. Never store or return the raw password
     */
    public void registerUser(String email, String rawPassword) {
        // Step 1: check for duplicates (query your user repository)
        System.out.println("Checking if " + email + " already exists...");

        // Step 2: hash the password
        String hashedPassword = passwordEncoder.encode(rawPassword);
        System.out.println("Password hashed: " + hashedPassword.substring(0, 20) + "...");

        // Step 3: save user with hashed password (use your JPA repository here)
        System.out.println("User saved: email=" + email + ", password=[HASHED]");

        // ✅ rawPassword is now out of scope and GC'd — never stored anywhere
    }

    /**
     * Login flow:
     *  1. Find user by email
     *  2. Use BCrypt's matches() to compare raw password against stored hash
     *  3. Never store the raw password — it goes out of scope immediately
     *  4. If matches → generate JWT (see Example 04)
     */
    public boolean authenticateUser(String email, String rawPassword) {
        // Step 1: load from database (simulated)
        String storedHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";

        // Step 2: verify password WITHOUT decrypting
        boolean authenticated = passwordEncoder.matches(rawPassword, storedHash);
        System.out.println("Login attempt for " + email + ": " + (authenticated ? "✅ Success" : "❌ Failed"));

        return authenticated;
    }
}

/*
 * ─── SUMMARY ─────────────────────────────────────────────────────────────────
 *
 *  REGISTRATION:
 *   raw password → passwordEncoder.encode() → hashed string → save to DB
 *
 *  LOGIN:
 *   raw password + stored hash → passwordEncoder.matches() → boolean
 *
 *  DO:
 *   ✅ Use BCryptPasswordEncoder (Spring default)
 *   ✅ Never log or return the raw password
 *   ✅ Use cost factor 10–12 in production
 *
 *  DON'T:
 *   ❌ Store plain text passwords
 *   ❌ Encrypt (reversible) passwords
 *   ❌ Use MD5 or SHA1 (too fast)
 *   ❌ Write your own hashing algorithm
 *
 * ─── BEAN DEFINITION ─────────────────────────────────────────────────────────
 *
 *  Define this in your SecurityConfig:
 *
 *  @Bean
 *  public PasswordEncoder passwordEncoder() {
 *      return new BCryptPasswordEncoder();
 *  }
 *
 *  Then inject it anywhere:
 *  @Autowired
 *  private PasswordEncoder passwordEncoder;
 */
