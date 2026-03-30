package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 01: REST Design Principles
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates REST design principles:
 *  - Correct HTTP method usage (GET, POST, PUT, PATCH, DELETE)
 *  - Proper HTTP status codes
 *  - RESTful URL naming conventions
 *  - ResponseEntity for full HTTP response control
 *
 * Dependencies: Spring Web (included in spring-boot-starter-web)
 */

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

// ─── Model ──────────────────────────────────────────────────────────────────

class Product {
    private Long id;
    private String name;
    private Double price;
    private String description;

    public Product(Long id, String name, Double price, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public void setName(String name) { this.name = name; }
    public void setPrice(Double price) { this.price = price; }
    public void setDescription(String description) { this.description = description; }
}

// ─── Controller ─────────────────────────────────────────────────────────────

/**
 * REST API Naming Conventions (study these):
 *
 *  GET    /api/v1/products         → Get all products
 *  GET    /api/v1/products/{id}    → Get a single product
 *  POST   /api/v1/products         → Create a new product
 *  PUT    /api/v1/products/{id}    → Replace a product entirely
 *  PATCH  /api/v1/products/{id}    → Partially update a product
 *  DELETE /api/v1/products/{id}    → Delete a product
 *
 *  ✅ Use plural nouns:  /products  /users  /orders
 *  ❌ Don't use verbs:   /getProducts  /createUser  /deleteOrder
 */
@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    // In-memory store (replace with a real database in production)
    private final Map<Long, Product> store = new HashMap<>();
    private Long nextId = 1L;

    // ─── GET /api/v1/products ────────────────────────────────────────────────
    /**
     * Returns all products.
     * Status: 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        List<Product> products = new ArrayList<>(store.values());
        return ResponseEntity.ok(products);  // 200 OK
    }

    // ─── GET /api/v1/products/{id} ───────────────────────────────────────────
    /**
     * Returns a single product by ID.
     * Status: 200 OK | 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        Product product = store.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found (no body)
        }
        return ResponseEntity.ok(product);  // 200 OK
    }

    // ─── POST /api/v1/products ───────────────────────────────────────────────
    /**
     * Creates a new product.
     * Status: 201 Created
     *
     * Note: Use 201 Created (not 200 OK) when a new resource is created.
     */
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        product = new Product(nextId++, product.getName(), product.getPrice(), product.getDescription());
        store.put(product.getId(), product);
        return ResponseEntity.status(HttpStatus.CREATED).body(product);  // 201 Created
    }

    // ─── PUT /api/v1/products/{id} ───────────────────────────────────────────
    /**
     * Replaces a product entirely (all fields must be provided).
     * Status: 200 OK | 404 Not Found
     *
     * PUT is idempotent — calling it multiple times with the same data
     * always produces the same result.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> replace(@PathVariable Long id, @RequestBody Product updated) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
        Product product = new Product(id, updated.getName(), updated.getPrice(), updated.getDescription());
        store.put(id, product);
        return ResponseEntity.ok(product);  // 200 OK
    }

    // ─── PATCH /api/v1/products/{id} ─────────────────────────────────────────
    /**
     * Partially updates a product (only provided fields are changed).
     * Status: 200 OK | 404 Not Found
     *
     * PATCH differs from PUT — you only send the fields you want to change.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Map<String, Object> updates) {
        Product product = store.get(id);
        if (product == null) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }

        // Apply only the fields that were sent
        if (updates.containsKey("name")) {
            product.setName((String) updates.get("name"));
        }
        if (updates.containsKey("price")) {
            product.setPrice(((Number) updates.get("price")).doubleValue());
        }
        if (updates.containsKey("description")) {
            product.setDescription((String) updates.get("description"));
        }

        store.put(id, product);
        return ResponseEntity.ok(product);  // 200 OK
    }

    // ─── DELETE /api/v1/products/{id} ────────────────────────────────────────
    /**
     * Deletes a product.
     * Status: 204 No Content | 404 Not Found
     *
     * Use 204 No Content for successful DELETE — no body is returned.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!store.containsKey(id)) {
            return ResponseEntity.notFound().build();  // 404 Not Found
        }
        store.remove(id);
        return ResponseEntity.noContent().build();  // 204 No Content
    }
}

/*
 * ─── HTTP STATUS CODE REFERENCE ──────────────────────────────────────────────
 *
 *  2xx Success:
 *    200 OK           → GET, PUT, PATCH returning data
 *    201 Created      → POST that creates a new resource
 *    204 No Content   → DELETE or PUT with no response body
 *
 *  4xx Client Errors:
 *    400 Bad Request          → Invalid input / malformed JSON
 *    401 Unauthorized         → Missing or invalid credentials
 *    403 Forbidden            → Authenticated but not authorised
 *    404 Not Found            → Resource doesn't exist
 *    409 Conflict             → Duplicate or version conflict
 *    422 Unprocessable Entity → Validation failed (semantic error)
 *    429 Too Many Requests    → Rate limit exceeded
 *
 *  5xx Server Errors:
 *    500 Internal Server Error → Unexpected server-side exception
 *
 * ─── TEST WITH CURL ──────────────────────────────────────────────────────────
 *
 *  # Create a product
 *  curl -X POST http://localhost:8080/api/v1/products \
 *    -H "Content-Type: application/json" \
 *    -d '{"name":"Laptop","price":999.99,"description":"Gaming laptop"}'
 *
 *  # Get all products
 *  curl http://localhost:8080/api/v1/products
 *
 *  # Get one by ID
 *  curl http://localhost:8080/api/v1/products/1
 *
 *  # Replace product
 *  curl -X PUT http://localhost:8080/api/v1/products/1 \
 *    -H "Content-Type: application/json" \
 *    -d '{"name":"Updated Laptop","price":1099.99,"description":"New version"}'
 *
 *  # Partial update (price only)
 *  curl -X PATCH http://localhost:8080/api/v1/products/1 \
 *    -H "Content-Type: application/json" \
 *    -d '{"price":799.99}'
 *
 *  # Delete
 *  curl -X DELETE http://localhost:8080/api/v1/products/1
 */
