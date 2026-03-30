package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 07: Full Secure API
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This is the CAPSTONE file for Module 7.
 * It brings together everything from examples 01–06 into a single,
 * production-ready REST API for a Product resource.
 *
 * What's included:
 *  ✅ RESTful URL design with versioning (/api/v1/products)
 *  ✅ Request DTO with Bean Validation (@Valid)
 *  ✅ Response DTO (never expose entities directly)
 *  ✅ Global Exception Handling (@RestControllerAdvice)
 *  ✅ API Key security (X-API-Key header filter)
 *  ✅ Request logging filter
 *  ✅ Token bucket rate limiting
 *  ✅ CORS configuration
 *  ✅ Security response headers
 *  ✅ Pagination support
 *  ✅ Input sanitisation
 *  ✅ Proper HTTP status codes throughout
 *
 * To run this, you need a Spring Boot project with:
 *  - spring-boot-starter-web
 *  - spring-boot-starter-validation
 *
 * application.properties:
 *  server.port=8080
 *  api.security.key=${API_KEY:dev-secret-key}
 */

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.*;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 1: DTOs
// ══════════════════════════════════════════════════════════════════════════════

class SecureProductCreateRequest {
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Size(max = 500, message = "Description max 500 characters")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock = 0;

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public Double getPrice() { return price; }
    public void setPrice(Double p) { this.price = p; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getCategory() { return category; }
    public void setCategory(String c) { this.category = c; }
    public Integer getStock() { return stock; }
    public void setStock(Integer s) { this.stock = s; }
}

class SecureProductUpdateRequest {
    @Size(min = 2, max = 100, message = "Name must be 2–100 characters")
    private String name;

    @Positive(message = "Price must be greater than 0")
    private Double price;

    @Size(max = 500, message = "Description max 500 characters")
    private String description;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    public String getName() { return name; }
    public void setName(String n) { this.name = n; }
    public Double getPrice() { return price; }
    public void setPrice(Double p) { this.price = p; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public Integer getStock() { return stock; }
    public void setStock(Integer s) { this.stock = s; }
}

class SecureProductResponse {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private String category;
    private Integer stock;
    private String createdAt;

    public SecureProductResponse(Long id, String name, Double price, String description,
                                 String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
        this.createdAt = LocalDateTime.now().toString();
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Integer getStock() { return stock; }
    public String getCreatedAt() { return createdAt; }
}

class ApiErrorResponse {
    private int status;
    private String error;
    private String message;
    private String path;
    private LocalDateTime timestamp;

    public ApiErrorResponse(int status, String error, String message, String path) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 2: EXCEPTIONS
// ══════════════════════════════════════════════════════════════════════════════

class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product not found with ID: " + id);
    }
}

class OutOfStockException extends RuntimeException {
    public OutOfStockException(String productName) {
        super("Product '" + productName + "' is out of stock");
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 3: SERVICE (Business Logic)
// ══════════════════════════════════════════════════════════════════════════════

class ProductData {
    Long id;
    String name;
    Double price;
    String description;
    String category;
    Integer stock;
    // Sensitive — never expose in API response
    Double supplierCost;

    ProductData(Long id, String name, Double price, String description,
                String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
        this.supplierCost = price * 0.55;  // Internal cost — hidden from response
    }
}

@Service
class SecureProductService {

    private final Map<Long, ProductData> store = new ConcurrentHashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    // Seed with demo data
    public SecureProductService() {
        create(new SecureProductCreateRequest() {{
            setName("Laptop Pro"); setPrice(1299.99);
            setDescription("High-performance laptop"); setCategory("Electronics"); setStock(15);
        }});
        create(new SecureProductCreateRequest() {{
            setName("Wireless Mouse"); setPrice(49.99);
            setDescription("Ergonomic wireless mouse"); setCategory("Accessories"); setStock(100);
        }});
    }

    public List<SecureProductResponse> findAll(int page, int size) {
        return store.values().stream()
                .sorted(Comparator.comparing(p -> p.id))
                .skip((long) page * size)
                .limit(size)
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public SecureProductResponse findById(Long id) {
        ProductData data = store.get(id);
        if (data == null) throw new ProductNotFoundException(id);
        return toResponse(data);
    }

    public SecureProductResponse create(SecureProductCreateRequest request) {
        Long id = idSequence.getAndIncrement();
        ProductData data = new ProductData(
            id, sanitise(request.getName()), request.getPrice(),
            sanitise(request.getDescription()), request.getCategory(), request.getStock()
        );
        store.put(id, data);
        return toResponse(data);
    }

    public SecureProductResponse update(Long id, SecureProductUpdateRequest request) {
        ProductData data = store.get(id);
        if (data == null) throw new ProductNotFoundException(id);

        if (request.getName() != null) data.name = sanitise(request.getName());
        if (request.getPrice() != null) data.price = request.getPrice();
        if (request.getDescription() != null) data.description = sanitise(request.getDescription());
        if (request.getStock() != null) data.stock = request.getStock();

        return toResponse(data);
    }

    public void delete(Long id) {
        if (!store.containsKey(id)) throw new ProductNotFoundException(id);
        store.remove(id);
    }

    public int count() {
        return store.size();
    }

    private SecureProductResponse toResponse(ProductData data) {
        return new SecureProductResponse(
            data.id, data.name, data.price,
            data.description, data.category, data.stock
        );
    }

    // Basic input sanitisation — strip HTML tags
    private String sanitise(String input) {
        if (input == null) return null;
        return input.replaceAll("<[^>]*>", "").replaceAll("[<>\"';]", "").trim();
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 4: CONTROLLER
// ══════════════════════════════════════════════════════════════════════════════

@RestController
@RequestMapping("/api/v1/products")
class SecureProductController {

    private final SecureProductService service;

    public SecureProductController(SecureProductService service) {
        this.service = service;
    }

    /**
     * GET /api/v1/products?page=0&size=10
     * Returns paginated list of products.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<SecureProductResponse> products = service.findAll(page, size);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("content", products);
        response.put("page", page);
        response.put("size", size);
        response.put("totalElements", service.count());
        response.put("totalPages", (int) Math.ceil((double) service.count() / size));

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/products/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<SecureProductResponse> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    /**
     * POST /api/v1/products
     */
    @PostMapping
    public ResponseEntity<SecureProductResponse> create(
            @Valid @RequestBody SecureProductCreateRequest request) {
        SecureProductResponse created = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * PATCH /api/v1/products/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<SecureProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody SecureProductUpdateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    /**
     * DELETE /api/v1/products/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 5: GLOBAL EXCEPTION HANDLER
// ══════════════════════════════════════════════════════════════════════════════

@RestControllerAdvice
class SecureGlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(
            ProductNotFoundException ex, jakarta.servlet.http.HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiErrorResponse(404, "Not Found", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ApiErrorResponse> handleOutOfStock(
            OutOfStockException ex, jakarta.servlet.http.HttpServletRequest req) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ApiErrorResponse(422, "Unprocessable Entity", ex.getMessage(), req.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneral(
            Exception ex, jakarta.servlet.http.HttpServletRequest req) {
        System.err.println("[ERROR] Unhandled: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiErrorResponse(500, "Internal Server Error",
                        "An unexpected error occurred.", req.getRequestURI()));
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 6: FILTERS
// ══════════════════════════════════════════════════════════════════════════════

/** Logging Filter */
@Component
@Order(1)
class FullApiLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        long start = System.currentTimeMillis();
        chain.doFilter(req, res);
        System.out.printf("[API] %s %s → %d (%dms)%n",
            r.getMethod(), r.getRequestURI(),
            ((HttpServletResponse) res).getStatus(),
            System.currentTimeMillis() - start);
    }
}

/** Security Headers Filter */
@Component
@Order(2)
class FullSecurityHeadersFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse r = (HttpServletResponse) res;
        r.addHeader("X-Frame-Options", "DENY");
        r.addHeader("X-Content-Type-Options", "nosniff");
        r.addHeader("X-XSS-Protection", "1; mode=block");
        r.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        chain.doFilter(req, res);
    }
}

/** API Key Filter */
@Component
@Order(3)
class FullApiKeyFilter implements Filter {
    @Value("${api.security.key:dev-secret-key}")
    private String validKey;

    private static final List<String> PUBLIC = List.of("/actuator/health", "/");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest r = (HttpServletRequest) req;
        HttpServletResponse resp = (HttpServletResponse) res;

        if (PUBLIC.contains(r.getRequestURI()) || !r.getRequestURI().startsWith("/api/")) {
            chain.doFilter(req, res);
            return;
        }

        String key = r.getHeader("X-API-Key");
        if (key == null || !key.equals(validKey)) {
            resp.setStatus(401);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\"," +
                "\"message\":\"Invalid or missing X-API-Key header\"}");
            return;
        }
        chain.doFilter(req, res);
    }
}

/** Rate Limiting Filter */
@Component
@Order(4)
class FullRateLimitFilter implements Filter {
    private final Map<String, int[]> buckets = new ConcurrentHashMap<>();
    // [0] = tokens remaining, [1] = last refill timestamp (seconds)
    private static final int LIMIT = 20;

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String ip = req.getRemoteAddr();
        int[] bucket = buckets.computeIfAbsent(ip, k -> new int[]{LIMIT, (int)(System.currentTimeMillis()/1000)});

        // Refill if a minute has passed
        int now = (int)(System.currentTimeMillis() / 1000);
        if (now - bucket[1] >= 60) {
            bucket[0] = LIMIT;
            bucket[1] = now;
        }

        HttpServletResponse resp = (HttpServletResponse) res;
        resp.addHeader("X-RateLimit-Limit", String.valueOf(LIMIT));
        resp.addHeader("X-RateLimit-Remaining", String.valueOf(bucket[0]));

        if (bucket[0] > 0) {
            bucket[0]--;
            chain.doFilter(req, res);
        } else {
            resp.setStatus(429);
            resp.setContentType("application/json");
            resp.addHeader("Retry-After", "60");
            resp.getWriter().write("{\"status\":429,\"error\":\"Too Many Requests\"," +
                "\"message\":\"Rate limit: 20 requests/minute. Retry after 60 seconds.\"}");
        }
    }
}

// ══════════════════════════════════════════════════════════════════════════════
//  SECTION 7: CORS & WEB MVC CONFIG
// ══════════════════════════════════════════════════════════════════════════════

@Configuration
class FullCorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("X-RateLimit-Limit", "X-RateLimit-Remaining", "X-Request-ID"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);
        return new CorsFilter(source);
    }
}

/*
 * ═══════════════════════════════════════════════════════════════════════════════
 *  COMPLETE API REFERENCE
 * ═══════════════════════════════════════════════════════════════════════════════
 *
 *  All requests require: -H "X-API-Key: dev-secret-key"
 *
 *  # List all products (paginated)
 *  curl -H "X-API-Key: dev-secret-key" \
 *       "http://localhost:8080/api/v1/products?page=0&size=5"
 *
 *  # Get one product
 *  curl -H "X-API-Key: dev-secret-key" \
 *       http://localhost:8080/api/v1/products/1
 *
 *  # Create a product
 *  curl -X POST http://localhost:8080/api/v1/products \
 *    -H "Content-Type: application/json" \
 *    -H "X-API-Key: dev-secret-key" \
 *    -d '{"name":"Keyboard","price":79.99,"description":"Mechanical keyboard",
 *          "category":"Accessories","stock":50}'
 *
 *  # Partial update (price only)
 *  curl -X PATCH http://localhost:8080/api/v1/products/1 \
 *    -H "Content-Type: application/json" \
 *    -H "X-API-Key: dev-secret-key" \
 *    -d '{"price":999.99}'
 *
 *  # Delete
 *  curl -X DELETE -H "X-API-Key: dev-secret-key" \
 *       http://localhost:8080/api/v1/products/2
 *
 *  # Test validation (empty body → 400)
 *  curl -X POST http://localhost:8080/api/v1/products \
 *    -H "Content-Type: application/json" \
 *    -H "X-API-Key: dev-secret-key" \
 *    -d '{}'
 *
 *  # Test 404
 *  curl -H "X-API-Key: dev-secret-key" http://localhost:8080/api/v1/products/999
 *
 *  # Test 401 (no key)
 *  curl http://localhost:8080/api/v1/products
 *
 *  # Test 429 (run 25 times fast)
 *  for i in {1..25}; do
 *    echo -n "Req $i: "
 *    curl -s -o /dev/null -w "%{http_code}\n" \
 *      -H "X-API-Key: dev-secret-key" http://localhost:8080/api/v1/products
 *  done
 */
