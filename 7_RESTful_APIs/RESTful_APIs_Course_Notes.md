# 🌐 RESTful APIs — Course Notes

> **Module 7 | GDP Term 2 | Digital Fortress Institute**

---

## 📌 What is a RESTful API?

**REST** (Representational State Transfer) is an architectural style for designing networked applications. A **RESTful API** is an API that follows REST principles — it uses standard HTTP methods, stateless communication, and predictable URLs.

REST is **not** a protocol or standard — it is a set of constraints and best practices.

---

## 1. 🏗️ REST Design Principles

### 1.1 The Richardson Maturity Model

REST APIs are graded on a maturity scale from Level 0 to Level 3:

| Level | Name | Description | Example |
|---|---|---|---|
| 0 | The Swamp of POX | One URL, one method | `POST /api` (everything) |
| 1 | Resources | Multiple URLs, one method | `POST /products`, `POST /orders` |
| 2 | HTTP Verbs | Correct HTTP methods + status codes | `GET /products`, `POST /products` |
| 3 | Hypermedia (HATEOAS) | Responses include links to related actions | Response body contains `_links` |

> 🎯 **Target Level 2 as a minimum.** Most production APIs live here.

---

### 1.2 HTTP Methods (Verbs)

| Method | Purpose | Idempotent? | Body? |
|---|---|---|---|
| `GET` | Retrieve data | ✅ Yes | ❌ No |
| `POST` | Create a new resource | ❌ No | ✅ Yes |
| `PUT` | Replace a resource entirely | ✅ Yes | ✅ Yes |
| `PATCH` | Partially update a resource | ✅ Yes | ✅ Yes |
| `DELETE` | Remove a resource | ✅ Yes | ❌ No |

**Idempotent** = calling it multiple times produces the same result.

---

### 1.3 HTTP Status Codes

You must return the **correct status code** — not just `200 OK` for everything.

| Code | Meaning | When to Use |
|---|---|---|
| `200 OK` | Success | GET, PUT, PATCH returning data |
| `201 Created` | Resource created | POST that creates a new resource |
| `204 No Content` | Success, no body | DELETE, PUT with no return body |
| `400 Bad Request` | Invalid input | Validation errors, malformed JSON |
| `401 Unauthorized` | Not authenticated | Missing or invalid credentials |
| `403 Forbidden` | Not authorised | Authenticated but lacks permission |
| `404 Not Found` | Resource missing | ID doesn't exist |
| `409 Conflict` | State conflict | Duplicate resource, version mismatch |
| `422 Unprocessable Entity` | Semantic validation failed | Data format OK but business rule violated |
| `429 Too Many Requests` | Rate limited | Client sending too many requests |
| `500 Internal Server Error` | Server error | Unexpected exceptions |

---

### 1.4 URL Naming Conventions

```
✅ Good REST URL design:
GET    /api/v1/products          → Get all products
GET    /api/v1/products/42       → Get product by ID
POST   /api/v1/products          → Create a product
PUT    /api/v1/products/42       → Replace product 42
PATCH  /api/v1/products/42       → Partially update product 42
DELETE /api/v1/products/42       → Delete product 42

GET    /api/v1/products/42/reviews  → Get reviews for product 42
POST   /api/v1/products/42/reviews  → Add review to product 42

❌ Bad REST URL design:
GET  /getProducts
POST /createNewProduct
GET  /products/delete/42
GET  /products?action=delete&id=42
```

**Rules:**
- Use **nouns**, not verbs in the path
- Use **plural** resource names (`/products`, not `/product`)
- Use **lowercase** and hyphens (`/user-profiles`)
- Use **nested routes** for sub-resources
- Include **version** in the base path (`/api/v1/`)

---

## 2. 📐 API Design: DTOs & Validation

### 2.1 Why DTOs?

A **DTO (Data Transfer Object)** is a plain object used to define exactly what data enters and leaves your API — separate from your database entity.

```
Client → [Request DTO] → Controller → Service → Repository → Database
                                                     ↓
Client ← [Response DTO] ← Controller ← Service ← Repository
```

**Benefits:**
- Never accidentally expose sensitive fields (e.g. `password`, `internalNotes`)
- Separate validation logic from persistence logic
- Evolve your API contract without changing the database schema

---

### 2.2 Bean Validation Annotations

Spring Boot includes **Jakarta Bean Validation** (formerly `javax.validation`).

Add these to your `pom.xml` if using Spring Boot 3.x (included by default with `spring-boot-starter-web`):

| Annotation | Description |
|---|---|
| `@NotNull` | Field must not be null |
| `@NotBlank` | String must not be null, empty, or whitespace |
| `@NotEmpty` | Collection/String must not be empty |
| `@Size(min, max)` | String or collection must be within size range |
| `@Min(value)` | Number must be ≥ value |
| `@Max(value)` | Number must be ≤ value |
| `@Email` | Must be a valid email format |
| `@Pattern(regexp)` | Must match a regular expression |
| `@Positive` | Number must be > 0 |
| `@PositiveOrZero` | Number must be ≥ 0 |

---

### 2.3 DTO Implementation

**Request DTO (what the client sends):**

```java
package com.gdp.api.dto;

import jakarta.validation.constraints.*;

public class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Getters and setters (or use Lombok @Data)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
```

**Response DTO (what the API returns):**

```java
package com.gdp.api.dto;

public class ProductResponse {

    private Long id;
    private String name;
    private Double price;
    private String description;
    private String createdAt;

    // Constructor
    public ProductResponse(Long id, String name, Double price, String description, String createdAt) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getCreatedAt() { return createdAt; }
}
```

**Controller using DTOs:**

```java
@PostMapping
public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
    ProductResponse response = productService.createProduct(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

> ⚠️ Always add `@Valid` to trigger Bean Validation on the incoming request body.

---

### 2.4 API Versioning

There are three common strategies:

| Strategy | Example | Pros | Cons |
|---|---|---|---|
| URL Path (recommended) | `/api/v1/products` | Simple, visible, cacheable | URL changes on version bump |
| Header | `Accept: application/vnd.api.v1+json` | Clean URLs | Less visible, harder to test |
| Query Param | `/products?version=1` | Easy to add | Pollutes query params |

**Spring Boot URL versioning:**

```java
@RestController
@RequestMapping("/api/v1/products")
public class ProductControllerV1 { ... }

@RestController
@RequestMapping("/api/v2/products")
public class ProductControllerV2 { ... }
```

---

### 2.5 Pagination & Sorting

Never return all records at once — always paginate large collections.

```java
// Controller
@GetMapping
public ResponseEntity<Page<ProductResponse>> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sortBy) {

    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
    Page<ProductResponse> products = productService.getAllProducts(pageable);
    return ResponseEntity.ok(products);
}
```

**Example response:**
```json
{
  "content": [...],
  "totalElements": 150,
  "totalPages": 15,
  "number": 0,
  "size": 10,
  "first": true,
  "last": false
}
```

---

## 3. 🔧 Middleware

Middleware sits **between** the client request and your controller logic. In Spring Boot, middleware is implemented as **Filters** or **Interceptors**.

### 3.1 Filter vs Interceptor

| | Filter (Servlet) | Interceptor (Spring MVC) |
|---|---|---|
| Lives at | Servlet container level | Spring MVC level |
| Access to Spring beans | Limited | Full |
| Can short-circuit request | ✅ Yes | ✅ Yes |
| Typical use | Logging, CORS, auth header parsing | Logging, auth, adding response headers |
| Interface | `jakarta.servlet.Filter` | `HandlerInterceptor` |

---

### 3.2 Logging Filter

```java
package com.gdp.api.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();
        System.out.println("[" + LocalDateTime.now() + "] → " +
                httpRequest.getMethod() + " " + httpRequest.getRequestURI());

        chain.doFilter(request, response);  // Pass request to next filter/controller

        long duration = System.currentTimeMillis() - startTime;
        System.out.println("[" + LocalDateTime.now() + "] ← " +
                httpRequest.getMethod() + " " + httpRequest.getRequestURI() +
                " | Status: " + httpResponse.getStatus() +
                " | Duration: " + duration + "ms");
    }
}
```

---

### 3.3 Request Interceptor

```java
package com.gdp.api.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequestTimingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        return true;  // true = continue processing; false = abort request
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;
        response.addHeader("X-Response-Time", duration + "ms");
    }
}
```

**Register the interceptor:**

```java
package com.gdp.api.config;

import com.gdp.api.interceptor.RequestTimingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final RequestTimingInterceptor requestTimingInterceptor;

    public WebConfig(RequestTimingInterceptor requestTimingInterceptor) {
        this.requestTimingInterceptor = requestTimingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestTimingInterceptor)
                .addPathPatterns("/api/**");  // Only intercept /api/* routes
    }
}
```

---

### 3.4 Global Exception Handling

Instead of wrapping every method in try/catch, use `@ControllerAdvice` to handle exceptions **globally**.

**Custom exception:**

```java
package com.gdp.api.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
```

**Standard error response DTO:**

```java
package com.gdp.api.dto;

import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}
```

**Global exception handler:**

```java
package com.gdp.api.exception;

import com.gdp.api.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Handle resource not found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                404, "Not Found", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // Handle validation errors from @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    // Catch-all for unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                500, "Internal Server Error", "An unexpected error occurred.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

**Error response example:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with ID: 99",
  "path": "/api/v1/products/99",
  "timestamp": "2025-03-30T10:15:30"
}
```

---

## 4. 🔐 Security

### 4.1 API Key Authentication

API key authentication is a simple, widely used mechanism for server-to-server communication. The client sends a secret key in the request header and the server validates it.

**Flow:**
```
Client → [X-API-Key: secret123] → Filter → Validates key → Controller
                                       ↓ (invalid key)
                                   401 Unauthorized
```

**API Key Filter:**

```java
package com.gdp.api.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@Order(1)  // Run this filter first
public class ApiKeyFilter implements Filter {

    @Value("${api.security.key}")
    private String validApiKey;

    // These paths don't require an API key
    private static final String[] PUBLIC_PATHS = {"/", "/actuator/health"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Skip API key check for public paths
        for (String publicPath : PUBLIC_PATHS) {
            if (path.equals(publicPath)) {
                chain.doFilter(request, response);
                return;
            }
        }

        String apiKey = httpRequest.getHeader("X-API-Key");

        if (apiKey == null || !apiKey.equals(validApiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Invalid or missing API key\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}
```

**application.properties:**
```properties
# Store the API key in config (use environment variables in production)
api.security.key=my-super-secret-key-123
```

**Usage:**
```bash
curl -H "X-API-Key: my-super-secret-key-123" http://localhost:8080/api/v1/products
```

> 🔑 In production, use **environment variables** or a **secrets manager** (AWS Secrets Manager, Vault) — never hardcode secrets in source code.

---

### 4.2 CORS Configuration

**CORS (Cross-Origin Resource Sharing)** is a browser security mechanism that blocks frontend JavaScript from calling APIs on a different domain unless the server explicitly allows it.

```
Frontend (http://localhost:3000) → API (http://localhost:8080)
                               ↑
                  Browser blocks this unless CORS headers are present
```

**Global CORS configuration:**

```java
package com.gdp.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // Allowed origins (frontend URLs)
        config.setAllowedOrigins(List.of(
            "http://localhost:3000",   // React dev server
            "http://localhost:4200",   // Angular dev server
            "https://myapp.com"        // Production frontend
        ));

        // Allowed HTTP methods
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Allowed headers
        config.setAllowedHeaders(List.of("*"));

        // Allow credentials (cookies, authorization headers)
        config.setAllowCredentials(true);

        // How long the browser should cache CORS preflight response (in seconds)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}
```

> ⚠️ Never use `allowedOrigins("*")` with `allowCredentials(true)` — this is a security vulnerability. Always specify exact origins.

---

### 4.3 Rate Limiting

Rate limiting prevents abuse by capping how many requests a client can make in a time window.

**Add Bucket4J to `pom.xml`:**

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.0.1</version>
</dependency>
```

**Rate Limiting Filter:**

```java
package com.gdp.api.security;

import io.github.bucket4j.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFilter implements Filter {

    // One bucket per IP address
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createBucket() {
        // Allow 20 requests per minute per IP
        Bandwidth limit = Bandwidth.classic(20, Refill.greedy(20, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String clientIp = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(clientIp, k -> createBucket());

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setStatus(429);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                "{\"status\":429,\"error\":\"Too Many Requests\",\"message\":\"Rate limit exceeded. Try again in 1 minute.\"}"
            );
        }
    }
}
```

---

### 4.4 Input Sanitisation

Never trust user input. Always sanitise data before processing or storing it.

```java
package com.gdp.api.util;

import org.springframework.stereotype.Component;

@Component
public class InputSanitiser {

    /**
     * Remove HTML tags and dangerous characters to prevent XSS.
     */
    public String sanitise(String input) {
        if (input == null) return null;
        // Strip HTML tags
        String stripped = input.replaceAll("<[^>]*>", "");
        // Remove script-related characters
        stripped = stripped.replaceAll("[<>\"'%;()&+]", "");
        return stripped.trim();
    }

    /**
     * Validate that a string contains only alphanumeric characters and spaces.
     */
    public boolean isAlphanumeric(String input) {
        return input != null && input.matches("^[a-zA-Z0-9 ]+$");
    }
}
```

---

### 4.5 Security Checklist

Use this checklist when building any API:

| ✅ | Check |
|---|---|
| ☐ | Use HTTPS in production (TLS/SSL) |
| ☐ | Return the correct HTTP status codes |
| ☐ | Validate all incoming request bodies with `@Valid` |
| ☐ | Use DTOs — never expose your JPA entities directly |
| ☐ | Implement authentication (API key, JWT, OAuth2) |
| ☐ | Configure CORS for your exact frontend origins |
| ☐ | Implement rate limiting on public endpoints |
| ☐ | Sanitise all user input |
| ☐ | Never log sensitive data (passwords, tokens, card numbers) |
| ☐ | Store secrets in environment variables, not in code |
| ☐ | Use global exception handling — never expose stack traces |
| ☐ | Add security headers (use Spring Security for this) |

---

## 5. 🧪 Testing the API

### 5.1 curl Examples

```bash
# GET all products (with API key)
curl -H "X-API-Key: my-secret-key" http://localhost:8080/api/v1/products

# GET with pagination
curl -H "X-API-Key: my-secret-key" \
  "http://localhost:8080/api/v1/products?page=0&size=5&sortBy=name"

# POST — create a product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "X-API-Key: my-secret-key" \
  -d '{"name": "Laptop", "price": 999.99, "description": "Gaming laptop"}'

# PUT — replace a product
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -H "X-API-Key: my-secret-key" \
  -d '{"name": "Updated Laptop", "price": 1099.99, "description": "Upgraded"}'

# PATCH — partial update
curl -X PATCH http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -H "X-API-Key: my-secret-key" \
  -d '{"price": 899.99}'

# DELETE
curl -X DELETE http://localhost:8080/api/v1/products/1 \
  -H "X-API-Key: my-secret-key"
```

### 5.2 Testing Validation Errors

```bash
# Missing required fields → 400 Bad Request
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -H "X-API-Key: my-secret-key" \
  -d '{}'

# Response:
# {
#   "name": "Product name is required",
#   "price": "Price is required"
# }
```

### 5.3 Testing Rate Limiting

```bash
# Exceed the limit with a loop
for i in {1..25}; do
  curl -s -o /dev/null -w "%{http_code}\n" \
    -H "X-API-Key: my-secret-key" \
    http://localhost:8080/api/v1/products
done
# First 20: 200 OK
# After 20: 429 Too Many Requests
```

---

## 6. 🏛️ Complete Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                          CLIENT                              │
└───────────────────────────┬─────────────────────────────────┘
                            │ HTTPS Request
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      MIDDLEWARE LAYER                        │
│                                                             │
│  ┌─────────────────┐  ┌──────────────────┐  ┌───────────┐  │
│  │ CORS Filter     │→ │  API Key Filter  │→ │Rate Limit │  │
│  └─────────────────┘  └──────────────────┘  └───────────┘  │
│                                                    ↓        │
│  ┌─────────────────────────────────────────────────────┐    │
│  │              Request Logging Filter                  │    │
│  └─────────────────────────────────────────────────────┘    │
└───────────────────────────┬─────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      SPRING MVC LAYER                        │
│                                                             │
│  ┌──────────────┐  @Valid   ┌────────────────────────────┐  │
│  │  Controller  │ ────────→ │  Request DTO + Validation   │  │
│  └──────┬───────┘          └────────────────────────────┘  │
│         │                                                   │
│         ▼                                                   │
│  ┌──────────────┐                                          │
│  │   Service    │  ← Business logic                         │
│  └──────┬───────┘                                          │
│         │                                                   │
│         ▼                                                   │
│  ┌──────────────┐                                          │
│  │  Repository  │  ← Data access                           │
│  └──────┬───────┘                                          │
│         │                                                   │
│         ▼                                                   │
│  ┌──────────────┐                                          │
│  │   Database   │                                          │
│  └──────────────┘                                          │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐    │
│  │         GlobalExceptionHandler (@ControllerAdvice)   │    │
│  └─────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

---

## ✅ Module Summary

| Concept | Key Takeaway |
|---|---|
| REST Design | Use nouns in URLs, correct HTTP verbs, proper status codes |
| DTOs | Separate API contract from database entity; always validate input |
| Versioning | Use `/api/v1/` prefix; version changes when breaking the contract |
| Pagination | Never return all records; use `Pageable` with `Page<T>` |
| Filters | Servlet-level middleware — CORS, rate limiting, auth |
| Interceptors | Spring MVC-level — logging, timing, response headers |
| Exception Handling | `@RestControllerAdvice` — standardised JSON error responses |
| API Key Auth | `X-API-Key` header; store secrets in environment variables |
| CORS | Explicitly whitelist allowed frontend origins |
| Rate Limiting | Bucket4J per-IP rate limits; return `429` when exceeded |

---

*Module 7 — RESTful APIs | GDP Term 2 | Digital Fortress Institute*
