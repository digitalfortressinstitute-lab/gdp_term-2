package com.gdp.productsapi.controller;

/**
 * 03_RestController.java
 * =======================
 * A full REST controller for Products, showing all HTTP verbs,
 * proper HTTP status codes, and ResponseEntity usage.
 *
 * Endpoints:
 *   GET    /api/v1/products         → List all products
 *   GET    /api/v1/products/{id}    → Get one product
 *   POST   /api/v1/products         → Create a product
 *   PUT    /api/v1/products/{id}    → Update a product
 *   DELETE /api/v1/products/{id}    → Delete a product
 */

import com.gdp.productsapi.model.Product;
import com.gdp.productsapi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")  // Allow requests from any origin (e.g., Angular frontend)
public class ProductController {

    // Constructor injection — preferred over @Autowired on field
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * GET /api/v1/products
     * Returns all products — HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        return ResponseEntity.ok(products); // 200
    }

    /**
     * GET /api/v1/products/{id}
     * Returns one product — HTTP 200 OK, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Product product = productService.getProductById(id); // throws if not found
        return ResponseEntity.ok(product); // 200
    }

    /**
     * POST /api/v1/products
     * Creates a new product — HTTP 201 Created
     * Body: { "name": "Laptop", "price": 9999.0, "description": "..." }
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity
                .status(HttpStatus.CREATED) // 201
                .body(created);
    }

    /**
     * PUT /api/v1/products/{id}
     * Updates an existing product — HTTP 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {
        Product updated = productService.updateProduct(id, updatedProduct);
        return ResponseEntity.ok(updated); // 200
    }

    /**
     * DELETE /api/v1/products/{id}
     * Deletes a product — HTTP 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build(); // 204
    }

    /**
     * GET /api/v1/products/search?keyword=laptop
     * Search products by name keyword — HTTP 200 OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        List<Product> results = productService.searchByName(keyword);
        return ResponseEntity.ok(results);
    }
}
