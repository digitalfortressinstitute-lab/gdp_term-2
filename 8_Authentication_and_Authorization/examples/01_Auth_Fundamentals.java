package com.gdp.auth;

/**
 * ============================================================
 * Module 8 — Authentication & Authorization
 * Example 01: Auth Fundamentals
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file covers the core concepts you must understand before
 * writing a single line of auth code:
 *
 *  - Authentication vs Authorization
 *  - Session-based vs Token-based auth
 *  - JWT structure and claims
 *  - Common auth attack vectors
 */

/**
 * ─── AUTHENTICATION vs AUTHORIZATION ─────────────────────────────────────────
 *
 *  AUTHENTICATION (AuthN) — "Who are you?"
 *   - The process of verifying identity
 *   - Example: checking email + password at login
 *   - Result: "You are user@example.com"
 *
 *  AUTHORIZATION (AuthZ) — "What can you do?"
 *   - The process of determining what an authenticated user can access
 *   - Example: checking if the user has ROLE_ADMIN before they can delete users
 *   - Result: "You are allowed to DELETE /api/v1/users/42" or "Forbidden"
 *
 *  ORDER: AuthN must always happen BEFORE AuthZ.
 *   You cannot check permissions for someone whose identity you haven't verified.
 *
 *  HTTP STATUS CODES:
 *   401 Unauthorized  → Not authenticated (bad/missing credentials)
 *   403 Forbidden     → Authenticated but not authorized (lacks permission)
 *
 *  ⚠️ Common mistake: returning 403 when you mean 401.
 *   If the user is not logged in → 401
 *   If the user is logged in but can't access a resource → 403
 */

/**
 * ─── SESSION-BASED vs TOKEN-BASED AUTH ───────────────────────────────────────
 *
 *  SESSION-BASED (Stateful):
 *
 *   1. User logs in → server creates a session and stores it in DB/Redis
 *   2. Server sends back a session ID (stored in a cookie)
 *   3. Every request: browser sends the cookie automatically
 *   4. Server looks up the session ID to get user info
 *
 *   Pros: easy to invalidate (just delete the session)
 *   Cons: server must store sessions → harder to scale horizontally
 *
 *  TOKEN-BASED (Stateless / JWT):
 *
 *   1. User logs in → server generates a signed JWT containing user info
 *   2. Server sends JWT to client (stored in memory or cookie)
 *   3. Every request: client sends JWT in Authorization header
 *   4. Server validates the signature — NO database lookup needed
 *
 *   Pros: stateless → easy to scale (no shared session store)
 *   Cons: harder to revoke (the token is valid until it expires)
 *
 *  WE USE TOKEN-BASED (JWT) for all REST APIs in this course.
 */

/**
 * ─── JWT STRUCTURE ────────────────────────────────────────────────────────────
 *
 *  A JWT is three Base64URL-encoded JSON objects joined by dots:
 *
 *   [HEADER].[PAYLOAD].[SIGNATURE]
 *
 *  Example token:
 *   eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9
 *   .eyJzdWIiOiJ1c2VyQGV4YW1wbGUuY29tIiwicm9sZSI6IlVTRVIiLCJpYXQiOjE3MDAwMDAwMDAsImV4cCI6MTcwMDAwMzYwMH0
 *   .SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
 *
 *  HEADER (decoded):
 *  {
 *    "alg": "HS256",   ← signing algorithm
 *    "typ": "JWT"      ← token type
 *  }
 *
 *  PAYLOAD — contains "claims" (decoded):
 *  {
 *    "sub": "user@example.com",   ← Subject: identifies the user
 *    "role": "USER",              ← Custom claim: user's role
 *    "iat": 1700000000,           ← Issued At (Unix timestamp)
 *    "exp": 1700003600            ← Expiry (iat + 1 hour)
 *  }
 *
 *  SIGNATURE:
 *   HMAC_SHA256(base64(header) + "." + base64(payload), SECRET_KEY)
 *
 *   The signature is what makes the token tamper-proof.
 *   If anyone modifies the payload (e.g., changes "USER" to "ADMIN"),
 *   the signature check will FAIL and the token is rejected.
 *
 *  ⚠️ IMPORTANT: JWTs are SIGNED, not ENCRYPTED.
 *   The payload is Base64URL-encoded — anyone can decode it.
 *   NEVER put sensitive data (passwords, PII, card numbers) in a JWT.
 */

/**
 * ─── STANDARD JWT CLAIMS ─────────────────────────────────────────────────────
 *
 *  Registered (standard) claims — defined by the JWT spec:
 *
 *   iss  → Issuer:       who created the token ("myapp.com")
 *   sub  → Subject:      who the token is about ("user@example.com")
 *   aud  → Audience:     who the token is for ("myapp-api")
 *   exp  → Expiration:   when the token expires (Unix timestamp)
 *   iat  → Issued At:    when the token was created
 *   jti  → JWT ID:       unique ID for this token (prevents replay attacks)
 *   nbf  → Not Before:   token is not valid before this time
 *
 *  Custom (private) claims — you define these:
 *   role    → "USER" or "ADMIN"
 *   userId  → 42
 *   email   → "user@example.com"
 *
 *  Keep custom claims minimal — the token travels with every request.
 */

/**
 * ─── ACCESS TOKEN vs REFRESH TOKEN ───────────────────────────────────────────
 *
 *  ACCESS TOKEN:
 *   - Used to authenticate API requests
 *   - Short-lived: 15 minutes
 *   - Sent in every request: Authorization: Bearer <token>
 *   - Stored: in memory (JavaScript variable) — NOT in localStorage
 *
 *  REFRESH TOKEN:
 *   - Used ONLY to get a new access token when it expires
 *   - Long-lived: 7–30 days
 *   - Sent ONLY to: POST /api/v1/auth/refresh
 *   - Stored: in an HttpOnly cookie (not accessible by JavaScript)
 *
 *  Typical flow:
 *
 *  1. User logs in:
 *     POST /api/v1/auth/login
 *     → { "accessToken": "...", "refreshToken": "...", "expiresIn": 900 }
 *
 *  2. Access token used for 15 minutes of API calls
 *
 *  3. Access token expires → use refresh token:
 *     POST /api/v1/auth/refresh
 *     Body: { "refreshToken": "..." }
 *     → { "accessToken": "...", "expiresIn": 900 }
 *
 *  4. Refresh token expires → user must log in again
 *
 *  5. Logout → invalidate both tokens server-side (blacklist or delete)
 */

/**
 * ─── COMMON AUTH VULNERABILITIES ─────────────────────────────────────────────
 *
 *  1. STORING TOKENS IN LOCALSTORAGE
 *     Problem: XSS (Cross-Site Scripting) attacks can steal localStorage
 *     Solution: Store access tokens in memory; refresh tokens in HttpOnly cookies
 *
 *  2. LONG-LIVED ACCESS TOKENS
 *     Problem: If stolen, attacker has long window of access
 *     Solution: 15-minute expiry for access tokens
 *
 *  3. WEAK JWT SECRET
 *     Problem: Attacker can brute-force a weak secret and forge tokens
 *     Solution: Use a 256-bit random secret; rotate periodically
 *
 *  4. NOT VALIDATING TOKENS ON EVERY REQUEST
 *     Problem: Expired or tampered tokens pass through
 *     Solution: Always validate signature + expiry in the JWT filter
 *
 *  5. RETURNING WRONG STATUS CODES
 *     Problem: Returning 403 for unauthenticated users confuses clients
 *     Solution: 401 for missing/invalid auth, 403 for insufficient permissions
 *
 *  6. LOGGING TOKENS
 *     Problem: Tokens in logs can be used by anyone with log access
 *     Solution: Never log Authorization headers or token values
 *
 *  7. NO RATE LIMITING ON /auth ENDPOINTS
 *     Problem: Brute force attacks on login
 *     Solution: Rate limit login/register to ~5 requests/min per IP
 */

// No runnable code in this file — these are foundational concepts.
// See examples 02-08 for the implementation.

public class AuthFundamentals {
    public static void main(String[] args) {
        System.out.println("=== Module 8: Auth Fundamentals ===");
        System.out.println("AuthN = Who are you? → via credentials");
        System.out.println("AuthZ = What can you do? → via roles/permissions");
        System.out.println("JWT = Signed token: [header].[payload].[signature]");
        System.out.println("401 = Not authenticated | 403 = Not authorized");
        System.out.println("See course notes and examples 02-08 for full implementation.");
    }
}
