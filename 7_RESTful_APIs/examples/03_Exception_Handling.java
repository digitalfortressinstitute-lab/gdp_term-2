package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 03: Global Exception Handling
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - Custom exception classes
 *  - Standardised ErrorResponse DTO
 *  - @RestControllerAdvice for global exception handling
 *  - Handling validation errors from @Valid
 *  - Catch-all handler for unexpected exceptions
 *
 * Without this, Spring returns a generic Whitelabel Error page or
 * an ugly stacktrace. A good API always returns structured JSON errors.
 */

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

// ─── CUSTOM EXCEPTIONS ────────────────────────────────────────────────────────

/**
 * Throw this when a resource (product, user, order, etc.) is not found.
 * Maps to HTTP 404 Not Found.
 */
class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " not found with ID: " + id);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

/**
 * Throw this when a business rule is violated.
 * Maps to HTTP 422 Unprocessable Entity.
 * Example: trying to purchase a product with 0 stock
 */
class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}

/**
 * Throw this when a duplicate resource is detected.
 * Maps to HTTP 409 Conflict.
 * Example: registering with an email that already exists.
 */
class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}

// ─── ERROR RESPONSE DTO ───────────────────────────────────────────────────────
// Every error response has the same structure — predictable for clients.

class ErrorResponse {
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

    // Getters (Jackson needs these to serialise to JSON)
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

// ─── GLOBAL EXCEPTION HANDLER ─────────────────────────────────────────────────
/**
 * @RestControllerAdvice intercepts exceptions thrown from ANY controller
 * and maps them to structured HTTP responses.
 *
 * Without this, a RuntimeException causes a 500 response with a stack trace.
 * With this, we return clean JSON with the correct status code.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException → 404 Not Found
     *
     * Example trigger:
     *   throw new ResourceNotFoundException("Product", 99L);
     *
     * Response:
     *   HTTP 404
     *   { "status": 404, "error": "Not Found", "message": "Product not found with ID: 99", ... }
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
            ResourceNotFoundException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            404, "Not Found", ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * Handles BusinessRuleException → 422 Unprocessable Entity
     *
     * Example trigger:
     *   throw new BusinessRuleException("Cannot order out-of-stock product");
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(
            BusinessRuleException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            422, "Unprocessable Entity", ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Handles DuplicateResourceException → 409 Conflict
     *
     * Example trigger:
     *   throw new DuplicateResourceException("Email already registered");
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleConflict(
            DuplicateResourceException ex, HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            409, "Conflict", ex.getMessage(), request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    /**
     * Handles @Valid validation failures → 400 Bad Request
     *
     * Triggered automatically by Spring when @Valid fails on @RequestBody.
     * Returns a map of fieldName → errorMessage for all failing fields.
     *
     * Response:
     *   HTTP 400
     *   { "name": "Product name is required", "price": "Price is required" }
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    /**
     * Catch-all for any unhandled exceptions → 500 Internal Server Error
     *
     * IMPORTANT: Never expose the real exception message to the client.
     * Log it server-side and return a generic message.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(
            Exception ex, HttpServletRequest request) {

        // ✅ Log the real error server-side (use a proper logger in production)
        System.err.println("Unhandled exception: " + ex.getMessage());
        ex.printStackTrace();

        // ❌ Never return ex.getMessage() — it may leak internal details
        ErrorResponse error = new ErrorResponse(
            500,
            "Internal Server Error",
            "An unexpected error occurred. Please contact support.",
            request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}

// ─── DEMO CONTROLLER (shows exceptions in action) ─────────────────────────────

class CreateItemRequest {
    @NotBlank(message = "Name is required")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

@RestController
@RequestMapping("/api/v1/demo-exceptions")
class DemoExceptionController {

    /**
     * GET /api/v1/demo-exceptions/not-found?id=99
     * Demonstrates ResourceNotFoundException → 404
     */
    @GetMapping("/not-found")
    public ResponseEntity<String> notFound(@RequestParam Long id) {
        throw new ResourceNotFoundException("Product", id);
    }

    /**
     * GET /api/v1/demo-exceptions/business-rule
     * Demonstrates BusinessRuleException → 422
     */
    @GetMapping("/business-rule")
    public ResponseEntity<String> businessRule() {
        throw new BusinessRuleException("Cannot order 0-stock product: 'Headphones'");
    }

    /**
     * GET /api/v1/demo-exceptions/conflict
     * Demonstrates DuplicateResourceException → 409
     */
    @GetMapping("/conflict")
    public ResponseEntity<String> conflict() {
        throw new DuplicateResourceException("Email 'user@example.com' is already registered");
    }

    /**
     * POST /api/v1/demo-exceptions/validate
     * Send {} to trigger validation errors → 400
     */
    @PostMapping("/validate")
    public ResponseEntity<String> validate(@Valid @RequestBody CreateItemRequest request) {
        return ResponseEntity.ok("Valid: " + request.getName());
    }
}

/*
 * ─── ERROR RESPONSE FORMAT ────────────────────────────────────────────────────
 *
 *  All errors return:
 *  {
 *    "status": 404,
 *    "error": "Not Found",
 *    "message": "Product not found with ID: 99",
 *    "path": "/api/v1/products/99",
 *    "timestamp": "2025-03-30T10:15:30"
 *  }
 *
 *  Validation errors return a field map:
 *  {
 *    "name": "Product name is required",
 *    "price": "Price is required"
 *  }
 *
 * ─── TEST THESE ENDPOINTS ────────────────────────────────────────────────────
 *
 *  curl http://localhost:8080/api/v1/demo-exceptions/not-found?id=99
 *  curl http://localhost:8080/api/v1/demo-exceptions/business-rule
 *  curl http://localhost:8080/api/v1/demo-exceptions/conflict
 *  curl -X POST http://localhost:8080/api/v1/demo-exceptions/validate \
 *    -H "Content-Type: application/json" -d '{}'
 */
