package com.gdp.productsapi.service;

/**
 * 04_ServiceLayer.java
 * =====================
 * The service layer — where business logic lives.
 * It sits between the Controller (HTTP) and Repository (Database).
 *
 * Key responsibilities:
 *  - Validate business rules
 *  - Orchestrate repository calls
 *  - Throw meaningful exceptions
 *  - Transform data if needed
 */

import com.gdp.productsapi.model.Product;
import com.gdp.productsapi.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service   // Marks this as a Spring-managed bean (auto-detected by @ComponentScan)
public class ProductService {

    private final ProductRepository productRepository;

    // Constructor injection — Spring injects the repository automatically
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // ─── READ ─────────────────────────────────────────────────────────────

    public List<Product> getAllProducts() {
        return productRepository.findAll(); // JPA built-in
    }

    public Product getProductById(Long id) {
        // orElseThrow: if not found, throw RuntimeException (returns 500 by default)
        // Later in Module 7 we'll replace this with a custom exception + 404 handler
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    public List<Product> searchByName(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    // ─── CREATE ───────────────────────────────────────────────────────────

    public Product createProduct(Product product) {
        // Business rule: name and price are required
        if (product.getName() == null || product.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required.");
        }
        if (product.getPrice() <= 0) {
            throw new IllegalArgumentException("Product price must be greater than 0.");
        }
        return productRepository.save(product); // JPA insert
    }

    // ─── UPDATE ───────────────────────────────────────────────────────────

    public Product updateProduct(Long id, Product updatedProduct) {
        // 1. Find existing (throws if not found)
        Product existing = getProductById(id);

        // 2. Apply updates
        if (updatedProduct.getName() != null && !updatedProduct.getName().isBlank()) {
            existing.setName(updatedProduct.getName());
        }
        if (updatedProduct.getPrice() > 0) {
            existing.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getDescription() != null) {
            existing.setDescription(updatedProduct.getDescription());
        }

        // 3. Save and return
        return productRepository.save(existing); // JPA update (entity already has ID)
    }

    // ─── DELETE ───────────────────────────────────────────────────────────

    public void deleteProduct(Long id) {
        // Verify it exists before deleting
        if (!productRepository.existsById(id)) {
            throw new RuntimeException("Cannot delete — product not found with ID: " + id);
        }
        productRepository.deleteById(id);
    }

    // ─── STATS (bonus business logic) ─────────────────────────────────────

    public double getAveragePrice() {
        List<Product> all = getAllProducts();
        if (all.isEmpty()) return 0;
        return all.stream()
                .mapToDouble(Product::getPrice)
                .average()
                .orElse(0);
    }

    public long countProducts() {
        return productRepository.count();
    }
}
