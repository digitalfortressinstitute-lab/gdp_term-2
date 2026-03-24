# 🌱 Spring Boot — Example 01: Project Setup Guide

> **Module 6.3 | GDP Term 2 | Digital Fortress Institute**

---

## Step 1: Generate the Project

Visit **[start.spring.io](https://start.spring.io)** and configure:

| Setting | Value |
|---|---|
| Project | **Maven** |
| Language | **Java** |
| Spring Boot | **3.x** (latest stable) |
| Group | `com.gdp` |
| Artifact | `products-api` |
| Name | `products-api` |
| Packaging | **Jar** |
| Java | **17** |

### Dependencies to Add:
- ✅ **Spring Web** — REST controllers
- ✅ **Spring Data JPA** — database access
- ✅ **H2 Database** — in-memory DB (development)
- ✅ **Lombok** — reduces boilerplate (getters/setters)
- ✅ **Spring Boot DevTools** — hot reload

Click **GENERATE**, unzip the file, and open the folder in **IntelliJ IDEA**.

---

## Step 2: Project Structure

After generating, your project looks like:

```
products-api/
├── pom.xml                                  ← Maven build file
└── src/
    ├── main/
    │   ├── java/com/gdp/productsapi/
    │   │   ├── ProductsApiApplication.java   ← Entry point
    │   │   ├── controller/
    │   │   │   └── ProductController.java
    │   │   ├── service/
    │   │   │   └── ProductService.java
    │   │   ├── repository/
    │   │   │   └── ProductRepository.java
    │   │   └── model/
    │   │       └── Product.java
    │   └── resources/
    │       └── application.properties        ← Configuration
    └── test/
        └── java/com/gdp/productsapi/
            └── ProductsApiApplicationTests.java
```

---

## Step 3: application.properties

Paste this into `src/main/resources/application.properties`:

```properties
# ── H2 In-Memory Database ──────────────────────────────
spring.datasource.url=jdbc:h2:mem:productsdb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# ── JPA / Hibernate ────────────────────────────────────
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# ── H2 Web Console (access at /h2-console) ─────────────
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# ── Server ─────────────────────────────────────────────
server.port=8080
```

---

## Step 4: Run the App

In IntelliJ IDEA:
- Click the **▶️ Run** button next to `ProductsApiApplication.java`

Or via terminal:
```bash
./mvnw spring-boot:run
```

You should see:
```
Tomcat started on port(s): 8080 (http)
Started ProductsApiApplication in 2.5 seconds
```

Visit:
- **API**: `http://localhost:8080/api/v1/products`
- **H2 Console**: `http://localhost:8080/h2-console`

---

## Step 5: pom.xml Dependencies Reference

```xml
<dependencies>
    <!-- Spring Web: REST controllers -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>

    <!-- Spring Data JPA: ORM / database access -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>

    <!-- H2: In-memory database for development -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok: reduces boilerplate -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>

    <!-- DevTools: hot reload -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-devtools</artifactId>
        <scope>runtime</scope>
        <optional>true</optional>
    </dependency>
</dependencies>
```
