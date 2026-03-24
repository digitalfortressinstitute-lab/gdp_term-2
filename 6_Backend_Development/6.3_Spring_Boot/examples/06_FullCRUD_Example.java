/**
 * 06_FullCRUD_Example.java
 * =========================
 * A complete, self-contained example showing all 3 layers wired together:
 *
 *   Product (Model) → ProductRepository → ProductService → ProductController
 *
 * This is pseudo-code that illustrates the architecture — in a real Spring Boot
 * project each class lives in its own file with proper package declarations.
 *
 * ARCHITECTURE OVERVIEW:
 *
 *  Client (Postman / Browser / Angular)
 *      ↓  HTTP Request
 *  [ProductController]   ← @RestController: handles HTTP, maps URLs to methods
 *      ↓
 *  [ProductService]      ← @Service: business logic, validation
 *      ↓
 *  [ProductRepository]   ← @Repository: JPA CRUD operations
 *      ↓
 *  [H2 / PostgreSQL]     ← Database
 *
 * TEST ALL ENDPOINTS WITH CURL:
 *   # List all products
 *   curl http://localhost:8080/api/v1/products
 *
 *   # Create a product
 *   curl -X POST http://localhost:8080/api/v1/products \
 *     -H "Content-Type: application/json" \
 *     -d '{"name":"Laptop Pro","price":12999.99,"description":"High-performance","stock":10}'
 *
 *   # Get one product (replace 1 with actual id)
 *   curl http://localhost:8080/api/v1/products/1
 *
 *   # Search
 *   curl "http://localhost:8080/api/v1/products/search?keyword=laptop"
 *
 *   # Update
 *   curl -X PUT http://localhost:8080/api/v1/products/1 \
 *     -H "Content-Type: application/json" \
 *     -d '{"name":"Laptop Ultra","price":14999.99}'
 *
 *   # Delete
 *   curl -X DELETE http://localhost:8080/api/v1/products/1
 */

// ─── 1. MODEL ─────────────────────────────────────────────────────────────────

// File: src/main/java/com/gdp/productsapi/model/Product.java
/*
package com.gdp.productsapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    private String description;

    @Column(nullable = false)
    private int stock = 0;
}
*/

// ─── 2. REPOSITORY ────────────────────────────────────────────────────────────

// File: src/main/java/com/gdp/productsapi/repository/ProductRepository.java
/*
package com.gdp.productsapi.repository;

import com.gdp.productsapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceLessThan(double maxPrice);
}
*/

// ─── 3. SERVICE ───────────────────────────────────────────────────────────────

// File: src/main/java/com/gdp/productsapi/service/ProductService.java
/*
package com.gdp.productsapi.service;

import com.gdp.productsapi.model.Product;
import com.gdp.productsapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository repo;

    public ProductService(ProductRepository repo) {
        this.repo = repo;
    }

    public List<Product> getAllProducts()           { return repo.findAll(); }
    public List<Product> searchByName(String kw)   { return repo.findByNameContainingIgnoreCase(kw); }

    public Product getProductById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found: " + id));
    }

    public Product createProduct(Product p) {
        if (p.getName() == null || p.getName().isBlank())
            throw new IllegalArgumentException("Name is required");
        if (p.getPrice() <= 0)
            throw new IllegalArgumentException("Price must be > 0");
        return repo.save(p);
    }

    public Product updateProduct(Long id, Product updated) {
        Product existing = getProductById(id);
        if (updated.getName() != null)   existing.setName(updated.getName());
        if (updated.getPrice() > 0)      existing.setPrice(updated.getPrice());
        if (updated.getDescription() != null) existing.setDescription(updated.getDescription());
        return repo.save(existing);
    }

    public void deleteProduct(Long id) {
        if (!repo.existsById(id)) throw new RuntimeException("Product not found: " + id);
        repo.deleteById(id);
    }
}
*/

// ─── 4. CONTROLLER ────────────────────────────────────────────────────────────

// File: src/main/java/com/gdp/productsapi/controller/ProductController.java
/*
package com.gdp.productsapi.controller;

import com.gdp.productsapi.model.Product;
import com.gdp.productsapi.service.ProductService;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(service.getProductById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> search(@RequestParam String keyword) {
        return ResponseEntity.ok(service.searchByName(keyword));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createProduct(product));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product product) {
        return ResponseEntity.ok(service.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
*/

// ─── 5. ENTRY POINT ───────────────────────────────────────────────────────────

// File: src/main/java/com/gdp/productsapi/ProductsApiApplication.java
/*
package com.gdp.productsapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ProductsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductsApiApplication.class, args);
    }
}
*/

/*
 * ─── EXPECTED API RESPONSES ─────────────────────────────────────────────────
 *
 * GET /api/v1/products → 200 OK
 * [
 *   {
 *     "id": 1,
 *     "name": "Laptop Pro",
 *     "price": 12999.99,
 *     "description": "High-performance laptop",
 *     "stock": 10
 *   }
 * ]
 *
 * POST /api/v1/products → 201 CREATED
 * { "id": 2, "name": "iPhone 15", "price": 18999.00, ... }
 *
 * DELETE /api/v1/products/1 → 204 NO CONTENT (empty body)
 *
 * GET /api/v1/products/999 → 500 (RuntimeException — Module 7 adds proper 404)
 *
 * ────────────────────────────────────────────────────────────────────────────
 *
 * NEXT STEPS (Module 7 — RESTful APIs):
 *  - Add @ControllerAdvice for global exception handling (404, 400, 500)
 *  - Add @Valid + DTO validation with @NotBlank, @Min, etc.
 *  - Add pagination with PageRequest and Pageable
 *  - Add API versioning strategy
 *  - Connect to PostgreSQL instead of H2
 */
public class FullCRUD_Example {
    // This file is a reference document — see the commented code above
    public static void main(String[] args) {
        System.out.println("This is a reference file. Look at the comments for the full architecture.");
        System.out.println("See 01_ProjectSetup.md to create and run a real Spring Boot project.");
    }
}
