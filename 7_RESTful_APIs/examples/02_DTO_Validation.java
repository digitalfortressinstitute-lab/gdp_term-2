package com.gdp.api;

/**
 * ============================================================
 * Module 7 — RESTful APIs | Example 02: DTOs & Validation
 * GDP Term 2 | Digital Fortress Institute
 * ============================================================
 *
 * This file demonstrates:
 *  - Request DTO with Bean Validation annotations
 *  - Response DTO (what the client receives)
 *  - Update/Patch DTO (all fields optional)
 *  - Using @Valid in the controller to trigger validation
 *  - Separating the API contract from the JPA Entity
 *
 * Key principle: The client never interacts with your Entity directly.
 * DTOs protect your data model and give you full control over your API contract.
 */

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// ─── REQUEST DTO ─────────────────────────────────────────────────────────────
// This is what the client sends when creating a product.
// Validation annotations define the rules.

class CreateProductRequest {

    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private Double price;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Category is required")
    private String category;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;

    // Getters and setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}

// ─── UPDATE DTO ───────────────────────────────────────────────────────────────
// Used for partial updates (PATCH). All fields are optional.
// We don't use @NotBlank etc. here — the client can send only
// the fields they want to change.

class UpdateProductRequest {
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Positive(message = "Price must be greater than zero")
    private Double price;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private Integer stock;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}

// ─── RESPONSE DTO ─────────────────────────────────────────────────────────────
// This is what the API returns — we control exactly what the client sees.
// Note: fields like 'internalCost', 'supplierId', or 'password' are EXCLUDED.

class ProductResponse {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private String category;
    private Integer stock;
    private String createdAt;  // Formatted as a string — good for API consumers

    public ProductResponse(Long id, String name, Double price, String description,
                           String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
        this.createdAt = LocalDateTime.now().toString();
    }

    // Getters (no setters — response objects should be immutable)
    public Long getId() { return id; }
    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public Integer getStock() { return stock; }
    public String getCreatedAt() { return createdAt; }
}

// ─── IN-MEMORY ENTITY (simulated) ─────────────────────────────────────────────
// In a real app this would be a JPA @Entity — kept simple here for clarity.

class ProductEntity {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private String category;
    private Integer stock;
    private Double internalCost;  // Sensitive field — never expose this in the API!

    public ProductEntity(Long id, String name, Double price, String description,
                         String category, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.stock = stock;
        this.internalCost = price * 0.6;  // Supplier cost — internal use only
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public String getDescription() { return description; }
    public void setDescription(String d) { this.description = d; }
    public String getCategory() { return category; }
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
}

// ─── MAPPER HELPER ────────────────────────────────────────────────────────────
// Converts between DTO and Entity. In real apps use MapStruct library.

class ProductMapper {
    public static ProductResponse toResponse(ProductEntity entity) {
        return new ProductResponse(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getDescription(),
            entity.getCategory(),
            entity.getStock()
        );
    }
}

// ─── CONTROLLER ──────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/v1/products")
class ProductDTOController {

    // Simulated in-memory storage
    private final List<ProductEntity> database = new ArrayList<>();
    private Long nextId = 1L;

    /**
     * GET /api/v1/products
     * Returns all products as Response DTOs.
     */
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAll() {
        List<ProductResponse> responses = new ArrayList<>();
        for (ProductEntity entity : database) {
            responses.add(ProductMapper.toResponse(entity));
        }
        return ResponseEntity.ok(responses);
    }

    /**
     * POST /api/v1/products
     *
     * @Valid triggers Bean Validation on the request body.
     * If validation fails, Spring automatically returns 400 Bad Request
     * with a list of field errors (handled by GlobalExceptionHandler).
     */
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        // Map DTO → Entity
        ProductEntity entity = new ProductEntity(
            nextId++,
            request.getName(),
            request.getPrice(),
            request.getDescription(),
            request.getCategory(),
            request.getStock() != null ? request.getStock() : 0
        );
        database.add(entity);

        // Map Entity → Response DTO (never return the entity directly)
        ProductResponse response = ProductMapper.toResponse(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * PATCH /api/v1/products/{id}
     * Uses UpdateProductRequest where all fields are optional.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {

        ProductEntity entity = database.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (entity == null) {
            return ResponseEntity.notFound().build();
        }

        // Only update fields that were provided (partial update)
        if (request.getName() != null) entity.setName(request.getName());
        if (request.getPrice() != null) entity.setPrice(request.getPrice());
        if (request.getDescription() != null) entity.setDescription(request.getDescription());
        if (request.getStock() != null) entity.setStock(request.getStock());

        return ResponseEntity.ok(ProductMapper.toResponse(entity));
    }
}

/*
 * ─── VALIDATION ANNOTATIONS REFERENCE ────────────────────────────────────────
 *
 *  @NotNull         → Field must not be null
 *  @NotBlank        → String must not be null, empty, or whitespace-only
 *  @NotEmpty        → String/Collection must not be null or empty
 *  @Size(min, max)  → String/Collection size must be within range
 *  @Min(value)      → Number must be >= value
 *  @Max(value)      → Number must be <= value
 *  @Positive        → Number must be > 0
 *  @PositiveOrZero  → Number must be >= 0
 *  @Email           → Must be a valid email address
 *  @Pattern(regexp) → Must match a regular expression
 *
 * ─── WHAT HAPPENS WHEN VALIDATION FAILS ─────────────────────────────────────
 *
 *  If @Valid fails, Spring throws MethodArgumentNotValidException.
 *  The GlobalExceptionHandler (Example 03) catches this and returns:
 *
 *  HTTP 400 Bad Request
 *  {
 *    "name": "Product name is required",
 *    "price": "Price must be greater than zero"
 *  }
 *
 * ─── TEST: SEND INVALID PAYLOAD ─────────────────────────────────────────────
 *
 *  curl -X POST http://localhost:8080/api/v1/products \
 *    -H "Content-Type: application/json" \
 *    -d '{}'
 *
 *  Expected response (400 Bad Request):
 *  {
 *    "name": "Product name is required",
 *    "price": "Price is required",
 *    "category": "Category is required"
 *  }
 */
