# 🧱 Java OOP — Course Notes

> **Module 6.2 | GDP Term 2 | Digital Fortress Institute**

---

## 📌 Why OOP in Java?

Java is a **class-based, object-oriented language**. Unlike Python where OOP is optional, in Java **everything** must be inside a class. Mastering OOP in Java is the direct bridge to understanding Spring Boot, where you will work with annotated classes daily.

The four pillars of OOP:
1. **Encapsulation** — hide implementation details
2. **Inheritance** — share behaviour across classes
3. **Polymorphism** — same interface, different behaviour
4. **Abstraction** — define contracts without implementation

---

## 1. 🏗️ Classes and Objects

A **class** is a blueprint. An **object** is an instance of that blueprint.

```java
// Blueprint
public class Car {
    // Fields (attributes)
    String brand;
    String model;
    int year;

    // Constructor — called when object is created
    public Car(String brand, String model, int year) {
        this.brand = brand;
        this.model = model;
        this.year = year;
    }

    // Method (behaviour)
    public void displayInfo() {
        System.out.println(year + " " + brand + " " + model);
    }
}

// Creating objects
public class Main {
    public static void main(String[] args) {
        Car car1 = new Car("Toyota", "Corolla", 2022);
        Car car2 = new Car("BMW", "X5", 2023);

        car1.displayInfo(); // 2022 Toyota Corolla
        car2.displayInfo(); // 2023 BMW X5
    }
}
```

**`this` keyword** — refers to the current object's own fields, used to resolve naming conflicts between parameters and fields.

---

## 2. 🔒 Encapsulation

Encapsulation means **hiding internal state** and only exposing it through controlled methods (getters/setters). This prevents invalid data.

```java
public class BankAccount {
    // private — cannot be accessed directly from outside
    private String owner;
    private double balance;

    public BankAccount(String owner, double initialBalance) {
        this.owner = owner;
        this.balance = initialBalance;
    }

    // Getter — read access
    public double getBalance() {
        return balance;
    }

    // Setter with validation — controlled write access
    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }
        balance += amount;
    }

    public void withdraw(double amount) {
        if (amount > balance) {
            System.out.println("Insufficient funds.");
            return;
        }
        balance -= amount;
    }
}

// Usage
BankAccount account = new BankAccount("Alice", 1000.0);
account.deposit(500.0);
System.out.println(account.getBalance()); // 1500.0
// account.balance = -99999; ← This would be a compile error!
```

---

## 3. 🧬 Inheritance

Inheritance lets a **child class** reuse and extend the behaviour of a **parent class**.

```java
// Parent class
public class Animal {
    String name;

    public Animal(String name) {
        this.name = name;
    }

    public void speak() {
        System.out.println(name + " makes a sound.");
    }
}

// Child class — extends parent
public class Dog extends Animal {

    public Dog(String name) {
        super(name); // Call parent constructor
    }

    // Override parent method
    @Override
    public void speak() {
        System.out.println(name + " says: Woof!");
    }

    // Dog-specific method
    public void fetch() {
        System.out.println(name + " fetches the ball!");
    }
}

public class Cat extends Animal {
    public Cat(String name) {
        super(name);
    }

    @Override
    public void speak() {
        System.out.println(name + " says: Meow!");
    }
}

// Usage
Dog dog = new Dog("Rex");
Cat cat = new Cat("Whiskers");

dog.speak();  // Rex says: Woof!
dog.fetch();  // Rex fetches the ball!
cat.speak();  // Whiskers says: Meow!
```

**`super`** — calls the parent class constructor or method.
**`@Override`** — annotation that confirms you are intentionally overriding a parent method.

---

## 4. 🎭 Polymorphism

Polymorphism means **"many forms"** — the same method call can behave differently depending on the object.

### Runtime Polymorphism (Method Overriding)

```java
Animal[] animals = {
    new Dog("Rex"),
    new Cat("Luna"),
    new Dog("Max")
};

for (Animal animal : animals) {
    animal.speak(); // Each calls its own version of speak()
}
// Rex says: Woof!
// Luna says: Meow!
// Max says: Woof!
```

> Even though the array type is `Animal`, the **actual object type** determines which `speak()` runs. This is **dynamic dispatch**.

### Compile-time Polymorphism (Method Overloading)

```java
public class Printer {
    public void print(String text) {
        System.out.println(text);
    }

    public void print(int number) {
        System.out.println("Number: " + number);
    }

    public void print(String text, int times) {
        for (int i = 0; i < times; i++) {
            System.out.println(text);
        }
    }
}
```

---

## 5. 🎨 Abstraction

Abstraction defines **what** something does without specifying **how** it does it.

### Abstract Class

```java
public abstract class Shape {
    String color;

    public Shape(String color) {
        this.color = color;
    }

    // Abstract method — no body, must be implemented by subclasses
    public abstract double area();

    // Concrete method — shared by all shapes
    public void displayColor() {
        System.out.println("Color: " + color);
    }
}

public class Circle extends Shape {
    double radius;

    public Circle(String color, double radius) {
        super(color);
        this.radius = radius;
    }

    @Override
    public double area() {
        return Math.PI * radius * radius;
    }
}

public class Rectangle extends Shape {
    double width, height;

    public Rectangle(String color, double width, double height) {
        super(color);
        this.width = width;
        this.height = height;
    }

    @Override
    public double area() {
        return width * height;
    }
}

// Usage
Shape s1 = new Circle("Red", 5.0);
Shape s2 = new Rectangle("Blue", 4.0, 6.0);

s1.displayColor();                          // Color: Red
System.out.println(s1.area());              // 78.53...
System.out.println(s2.area());              // 24.0
```

---

## 6. 📋 Interfaces

An interface is a **pure contract** — it defines method signatures that implementing classes must honour. A class can implement **multiple interfaces** (unlike single inheritance with `extends`).

```java
// Define contracts
public interface Flyable {
    void fly();
}

public interface Swimmable {
    void swim();
}

// A Duck can both fly and swim
public class Duck implements Flyable, Swimmable {
    String name;

    public Duck(String name) {
        this.name = name;
    }

    @Override
    public void fly() {
        System.out.println(name + " is flying.");
    }

    @Override
    public void swim() {
        System.out.println(name + " is swimming.");
    }
}

// Usage
Duck duck = new Duck("Donald");
duck.fly();   // Donald is flying.
duck.swim();  // Donald is swimming.
```

### Interface vs Abstract Class

| Feature | Interface | Abstract Class |
|---|---|---|
| Can have fields | No (only constants) | Yes |
| Can have concrete methods | Yes (default methods in Java 8+) | Yes |
| Multiple implementation | ✅ Yes | ❌ No |
| Use when | Defining a capability/role | Sharing common behaviour |

---

## 🔗 OOP → Spring Boot Connection

| OOP Concept | Spring Boot Usage |
|---|---|
| Classes | Every component (Controller, Service, Repository) is a class |
| Encapsulation | Private fields + public methods in Service classes |
| Interfaces | Repository interfaces (`JpaRepository`), Service interfaces |
| Inheritance | Spring base classes, `@Entity` objects |
| Abstraction | `@Service`, `@Repository` hide implementation details |
| Polymorphism | Dependency injection accepts any implementation of an interface |

---

## ✅ Summary

| Pillar | Keyword | Purpose |
|---|---|---|
| Encapsulation | `private`, getters, setters | Protect data integrity |
| Inheritance | `extends`, `super` | Code reuse |
| Polymorphism | `@Override`, overloading | Flexible behaviour |
| Abstraction | `abstract`, `interface` | Define contracts |

---

*Next: [6.3 Spring Boot →](../6.3_Spring_Boot/Spring_Boot_Course_Notes.md)*
