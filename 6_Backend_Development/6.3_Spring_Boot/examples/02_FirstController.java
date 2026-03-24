package com.gdp.productsapi.controller;

/**
 * 02_FirstController.java
 * ========================
 * Your first Spring Boot controller — a simple "Hello World" style REST controller
 * that demonstrates the basic annotations and request mapping.
 *
 * Run the app and visit: http://localhost:8080/hello
 */

import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Map;

@RestController                    // Marks this as a REST controller — returns JSON/text
@RequestMapping("/hello")          // All routes in this class are prefixed with /hello
public class HelloController {

    // GET /hello  →  Plain text response
    @GetMapping
    public String sayHello() {
        return "Hello from Spring Boot! 🚀";
    }

    // GET /hello/name/Alice  →  Returns "Hello, Alice!"
    @GetMapping("/name/{name}")
    public String greet(@PathVariable String name) {
        return "Hello, " + name + "! Welcome to Java Spring Boot.";
    }

    // GET /hello/info  →  Returns a JSON object
    @GetMapping("/info")
    public Map<String, Object> getInfo() {
        return Map.of(
                "application", "Products API",
                "version", "1.0.0",
                "timestamp", LocalDateTime.now().toString(),
                "status", "running"
        );
    }

    // GET /hello/greet?name=Bob&language=Java
    @GetMapping("/greet")
    public String greetWithQuery(
            @RequestParam String name,
            @RequestParam(defaultValue = "Spring Boot") String language) {
        return String.format("Hello %s! You are learning %s.", name, language);
    }

    // POST /hello/echo  →  Echoes back whatever body you send
    @PostMapping("/echo")
    public Map<String, Object> echo(@RequestBody Map<String, Object> body) {
        return Map.of(
                "received", body,
                "keys", body.keySet(),
                "size", body.size()
        );
    }
}
