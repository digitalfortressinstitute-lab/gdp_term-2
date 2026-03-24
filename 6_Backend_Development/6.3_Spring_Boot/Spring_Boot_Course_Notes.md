# 🌱 Spring Boot — Course Notes

> **Module 6.3 | GDP Term 2 | Digital Fortress Institute**

---

## 📌 What is Spring Boot?

**Spring Boot** is a Java framework for building web applications and REST APIs quickly, with minimal configuration. It sits on top of the **Spring Framework** and automates setup so you can focus on writing business logic.

Think of it like **NestJS** (which you may have used) but for Java.

| Concept | NestJS | Spring Boot |
|---|---|---|
| Controller | `@Controller` | `@RestController` |
| Service | `@Injectable` | `@Service` |
| Repository | TypeORM | JPA / `@Repository` |
| Module | `@Module` | `@SpringBootApplication` |
| Dependency Injection | Constructor injection | `@Autowired` / constructor injection |

---

## 1. 🚀 Project Setup

### Step 1 — Generate a Project

Go to **[start.spring.io](https://start.spring.io)** and configure:

| Setting | Value |
|---|---|
| Project | Maven |
| Language | Java |
| Spring Boot | 3.x (latest stable) |
| Group | `com.gdp` |
| Artifact | `myapp` |
| Packaging | Jar |
| Java | 17 |

**Dependencies to add:**
- Spring Web
- Spring Data JPA
- H2 Database (in-memory, perfect for learning)
- Lombok (reduces boilerplate)

Click **Generate**, unzip, and open in IntelliJ IDEA.

### Step 2 — Project Structure

```
myapp/
├── src/main/java/com/gdp/myapp/
│   ├── MyappApplication.java       ← Entry point
│   ├── controller/
│   │   └── ProductController.java
│   ├── service/
│   │   └── ProductService.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── model/
│       └── Product.java
└── src/main/resources/
    └── application.properties      ← Configuration
```

---

## 2. 📝 The Entry Point

```java
package com.gdp.myapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MyappApplication {
    public static void main(String[] args) {
        SpringApplication.run(MyappApplication.class, args);
    }
}
```

`@SpringBootApplication` is a convenience annotation that combines:
- `@Configuration` — marks this as a config source
- `@EnableAutoConfiguration` — Spring Boot auto-configures beans
- `@ComponentScan` — scans for components in the package

---

## 3. 🎮 REST Controller

The controller handles HTTP requests and maps them to methods.

```java
package com.gdp.myapp.controller;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    // GET /api/products
    @GetMapping
    public List<String> getAll() {
        return List.of("Laptop", "Phone", "Tablet");
    }

    // GET /api/products/42
    @GetMapping("/{id}")
    public String getOne(@PathVariable Long id) {
        return "Product with ID: " + id;
    }

    // POST /api/products
    @PostMapping
    public String create(@RequestBody String productName) {
        return "Created: " + productName;
    }

    // PUT /api/products/42
    @PutMapping("/{id}")
    public String update(@PathVariable Long id, @RequestBody String productName) {
        return "Updated product " + id + " to: " + productName;
    }

    // DELETE /api/products/42
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        return "Deleted product " + id;
    }
}
```

| Annotation | Purpose |
|---|---|
| `@RestController` | Marks class as a REST controller (returns JSON by default) |
| `@RequestMapping` | Base URL prefix for all routes in this controller |
| `@GetMapping` | Maps HTTP GET requests |
| `@PostMapping` | Maps HTTP POST requests |
| `@PutMapping` | Maps HTTP PUT requests |
| `@DeleteMapping` | Maps HTTP DELETE requests |
| `@PathVariable` | Extracts value from the URL path |
| `@RequestBody` | Deserialises JSON body into a Java object |

---

## 4. 🏷️ The Model (Entity)

```java
package com.gdp.myapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "products")
@Data                 // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor    // Generates no-args constructor (required by JPA)
@AllArgsConstructor   // Generates all-args constructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double price;

    private String description;
}
```

| Annotation | Purpose |
|---|---|
| `@Entity` | Marks this class as a JPA entity (maps to a DB table) |
| `@Table(name = "products")` | Specifies the table name |
| `@Id` | Marks the primary key field |
| `@GeneratedValue` | Auto-increments the ID |
| `@Column(nullable = false)` | Column constraints |
| `@Data` (Lombok) | Generates boilerplate code |

---

## 5. 🗄️ Repository (Data Access Layer)

Spring Data JPA provides **ready-made CRUD methods** through `JpaRepository`.

```java
package com.gdp.myapp.repository;

import com.gdp.myapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Spring Data auto-generates this query from the method name!
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Custom query
    List<Product> findByPriceLessThan(double maxPrice);
}
```

`JpaRepository<Product, Long>` gives you for free:
- `findAll()` — get all records
- `findById(id)` — get one by ID
- `save(entity)` — create or update
- `deleteById(id)` — delete
- `count()` — total records

---

## 6. ⚙️ Service Layer

The service layer contains **business logic** and sits between the controller and the repository.

```java
package com.gdp.myapp.service;

import com.gdp.myapp.model.Product;
import com.gdp.myapp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    // Constructor injection (preferred over @Autowired on field)
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with ID: " + id));
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        Product existing = getProductById(id);
        existing.setName(updatedProduct.getName());
        existing.setPrice(updatedProduct.getPrice());
        existing.setDescription(updatedProduct.getDescription());
        return productRepository.save(existing);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
```

---

## 7. 🔗 Wiring It All Together

Now connect the service to the controller:

```java
package com.gdp.myapp.controller;

import com.gdp.myapp.model.Product;
import com.gdp.myapp.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestBody Product product) {
        return ResponseEntity.ok(productService.updateProduct(id, product));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 8. ⚙️ application.properties

```properties
# H2 In-Memory Database (for development/testing)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA settings
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# H2 Console (visit http://localhost:8080/h2-console)
spring.h2.console.enabled=true

# App port
server.port=8080
```

---

## 9. 🧪 Testing the API

Use **Postman**, **Insomnia**, or **curl**:

```bash
# Get all products
curl http://localhost:8080/api/v1/products

# Create a product
curl -X POST http://localhost:8080/api/v1/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Laptop", "price": 999.99, "description": "High-performance laptop"}'

# Get one
curl http://localhost:8080/api/v1/products/1

# Update
curl -X PUT http://localhost:8080/api/v1/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "Gaming Laptop", "price": 1299.99, "description": "Updated"}'

# Delete
curl -X DELETE http://localhost:8080/api/v1/products/1
```

---

## 🏛️ Layer Architecture

```
Request
   ↓
Controller  ← Handles HTTP (routes, request/response)
   ↓
Service     ← Business logic
   ↓
Repository  ← Database operations
   ↓
Database
```

This separation of concerns is the backbone of scalable backend development.

---

## ✅ Summary

| Layer | Annotation | Responsibility |
|---|---|---|
| Controller | `@RestController` | Handle HTTP, return responses |
| Service | `@Service` | Business logic |
| Repository | `@Repository` | Data access (CRUD) |
| Model | `@Entity` | Represents DB table |

---

*This is the foundation for Module 7 — RESTful APIs (middleware, validation, advanced security).*
