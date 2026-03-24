package com.gdp.productsapi.repository;

/**
 * 05_Repository_JPA.java
 * =======================
 * The repository layer — data access via Spring Data JPA.
 *
 * Spring Data auto-generates SQL queries from method names.
 * You just declare the interface — Spring writes the implementation.
 *
 * Also includes the Product entity model for reference.
 */

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

// ─── PRODUCT ENTITY ──────────────────────────────────────────────────────────
// (normally this would be in model/Product.java)

@Entity
@Table(name = "products")
@Data               // Lombok: generates getters, setters, toString, equals, hashCode
@NoArgsConstructor  // Lombok: generates no-arg constructor (required by JPA)
@AllArgsConstructor // Lombok: generates all-arg constructor
class Product {

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

// ─── PRODUCT REPOSITORY ───────────────────────────────────────────────────────
// Extends JpaRepository<EntityType, PrimaryKeyType>
// You get 15+ built-in methods for free!

@Repository
interface ProductRepository extends JpaRepository<Product, Long> {

    // ── Method Name Queries (Spring Data interprets these automatically) ──

    // SELECT * FROM products WHERE name LIKE '%keyword%' (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // SELECT * FROM products WHERE price < maxPrice
    List<Product> findByPriceLessThan(double maxPrice);

    // SELECT * FROM products WHERE price BETWEEN min AND max
    List<Product> findByPriceBetween(double min, double max);

    // SELECT * FROM products WHERE stock > 0
    List<Product> findByStockGreaterThan(int minStock);

    // SELECT * FROM products WHERE name = ? (exact match)
    Optional<Product> findByName(String name);

    // SELECT * FROM products WHERE stock = 0
    List<Product> findByStockEquals(int stock);

    // COUNT(*) WHERE price < maxPrice
    long countByPriceLessThan(double maxPrice);

    // ── Custom JPQL queries (Java Persistence Query Language) ────────────

    // JPQL uses class/field names, not table/column names
    @Query("SELECT p FROM Product p WHERE p.price > :minPrice ORDER BY p.price DESC")
    List<Product> findExpensiveProductsSorted(@Param("minPrice") double minPrice);

    // ── Native SQL (use sparingly — not portable across databases) ────────

    @Query(value = "SELECT * FROM products WHERE stock < 5", nativeQuery = true)
    List<Product> findLowStockProductsNative();
}

/*
 * ── Built-in JpaRepository Methods (no code needed) ─────────────────────────
 *
 * findAll()                   → List<Product> (all rows)
 * findAll(Sort sort)          → sorted list
 * findAll(Pageable pageable)  → paginated list
 * findById(id)                → Optional<Product>
 * save(product)               → Product (insert or update)
 * saveAll(list)               → List<Product> (bulk insert/update)
 * deleteById(id)              → void
 * delete(product)             → void
 * deleteAll()                 → void (drops all rows!)
 * existsById(id)              → boolean
 * count()                     → long (total rows)
 *
 * ─────────────────────────────────────────────────────────────────────────────
 */
