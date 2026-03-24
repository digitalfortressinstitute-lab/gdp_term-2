# ☕ Java Basics — Course Notes

> **Module 6.1 | GDP Term 2 | Digital Fortress Institute**

---

## 📌 Why Java?

Java is one of the most widely used programming languages in the world. It powers Android apps, enterprise backends, and is the foundation of **Spring Boot** — the framework we use later in this module. Java is:

- **Statically typed** — every variable has a type declared at compile time
- **Object-Oriented** — everything lives inside classes
- **Platform independent** — "Write Once, Run Anywhere" via the JVM
- **Strongly typed** — the compiler catches type errors before runtime

---

## 1. 🖥️ Hello World

Every Java program starts with a **class** and a **main method** — the entry point.

```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, World!");
    }
}
```

**Key points:**
- `public class HelloWorld` — the class name must match the filename (`HelloWorld.java`)
- `public static void main(String[] args)` — the JVM looks for exactly this signature to start the program
- `System.out.println()` — prints text and adds a newline

---

## 2. 📦 Data Types

Java has two categories of types: **primitive** and **reference**.

### Primitive Types

| Type | Size | Example | Use Case |
|------|------|---------|----------|
| `int` | 32-bit | `int age = 25;` | Whole numbers |
| `long` | 64-bit | `long population = 8_000_000_000L;` | Large whole numbers |
| `double` | 64-bit | `double price = 9.99;` | Decimal numbers |
| `float` | 32-bit | `float temp = 36.6f;` | Less-precise decimals |
| `boolean` | 1-bit | `boolean isActive = true;` | True/false |
| `char` | 16-bit | `char grade = 'A';` | Single character |

### Reference Types

```java
String name = "Alice";       // Text — most common reference type
int[] scores = {85, 90, 78}; // Array of ints
```

### Type Conversion

```java
int x = 10;
double y = x;          // Widening (automatic — safe)
int z = (int) 9.99;    // Narrowing (manual cast — data may be lost)
```

---

## 3. 🔄 Control Flow

### if / else if / else

```java
int score = 75;

if (score >= 80) {
    System.out.println("Distinction");
} else if (score >= 60) {
    System.out.println("Pass");
} else {
    System.out.println("Fail");
}
```

### switch

```java
String day = "Monday";

switch (day) {
    case "Monday":
    case "Tuesday":
        System.out.println("Start of the week");
        break;
    case "Friday":
        System.out.println("End of the week");
        break;
    default:
        System.out.println("Midweek");
}
```

### Loops

**for loop:**
```java
for (int i = 0; i < 5; i++) {
    System.out.println("Count: " + i);
}
```

**while loop:**
```java
int count = 0;
while (count < 3) {
    System.out.println("count = " + count);
    count++;
}
```

**for-each loop (enhanced for):**
```java
String[] fruits = {"Apple", "Banana", "Cherry"};

for (String fruit : fruits) {
    System.out.println(fruit);
}
```

---

## 4. 🗃️ Arrays

Arrays in Java have a **fixed size** set at creation.

```java
// Declaration and initialisation
int[] numbers = {10, 20, 30, 40, 50};

// Access by index (0-based)
System.out.println(numbers[0]);  // 10

// Length
System.out.println(numbers.length);  // 5

// Iterate
for (int i = 0; i < numbers.length; i++) {
    System.out.println(numbers[i]);
}

// 2D Array
int[][] matrix = {
    {1, 2, 3},
    {4, 5, 6}
};
System.out.println(matrix[1][2]);  // 6
```

---

## 5. 🔧 Methods

Methods are named blocks of reusable code inside a class.

```java
public class Calculator {

    // Method with parameters and a return value
    public static int add(int a, int b) {
        return a + b;
    }

    // Method with no return value (void)
    public static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    public static void main(String[] args) {
        int result = add(5, 3);   // Call the method
        System.out.println(result); // 8

        greet("Alice");           // Hello, Alice!
    }
}
```

### Method Overloading

Java allows **multiple methods with the same name** as long as their parameter lists differ.

```java
public static int multiply(int a, int b) { return a * b; }
public static double multiply(double a, double b) { return a * b; }
```

---

## 6. 📚 Collections (ArrayList & HashMap)

Unlike arrays, collections can **grow and shrink dynamically**.

### ArrayList

```java
import java.util.ArrayList;

ArrayList<String> students = new ArrayList<>();

students.add("Alice");
students.add("Bob");
students.add("Charlie");

System.out.println(students.get(0));  // Alice
System.out.println(students.size());  // 3

students.remove("Bob");

for (String student : students) {
    System.out.println(student);
}
```

### HashMap (Key-Value pairs)

```java
import java.util.HashMap;

HashMap<String, Integer> scores = new HashMap<>();

scores.put("Alice", 95);
scores.put("Bob", 80);
scores.put("Charlie", 88);

System.out.println(scores.get("Alice"));       // 95
System.out.println(scores.containsKey("Bob")); // true

for (String name : scores.keySet()) {
    System.out.println(name + " → " + scores.get(name));
}
```

---

## 7. 🚨 Exception Handling

```java
try {
    int result = 10 / 0;
} catch (ArithmeticException e) {
    System.out.println("Error: " + e.getMessage());
} finally {
    System.out.println("This always runs");
}
```

---

## ✅ Summary

| Concept | Key Takeaway |
|---|---|
| Data types | Java is statically typed — declare types explicitly |
| Control flow | `if/else`, `switch`, `for`, `while`, enhanced `for` |
| Arrays | Fixed size, 0-indexed |
| Methods | Reusable code blocks; support overloading |
| Collections | `ArrayList` (dynamic list), `HashMap` (key-value store) |
| Exceptions | Use `try/catch/finally` to handle runtime errors |

---

*Next: [6.2 Java OOP →](../6.2_Java_OOP/Java_OOP_Course_Notes.md)*
