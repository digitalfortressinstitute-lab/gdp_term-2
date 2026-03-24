/**
 * 01_ClassesAndObjects.java
 * ==========================
 * The foundation of OOP: defining blueprints (classes)
 * and creating instances (objects) from them.
 */
public class ClassesAndObjects {

    // ─── STUDENT CLASS ───────────────────────────────────────────────────
    static class Student {
        // Fields (attributes / instance variables)
        String name;
        int age;
        String course;

        // Constructor — called when creating a new Student object
        Student(String name, int age, String course) {
            this.name = name;
            this.age = age;
            this.course = course;
        }

        // Method — behaviour the object can perform
        void introduce() {
            System.out.printf("Hi, I'm %s, age %d, studying %s.%n", name, age, course);
        }

        boolean isAdult() {
            return age >= 18;
        }

        // Override toString() — used when you print the object directly
        @Override
        public String toString() {
            return "Student{name='" + name + "', age=" + age + ", course='" + course + "'}";
        }
    }

    // ─── PRODUCT CLASS ────────────────────────────────────────────────────
    static class Product {
        String name;
        double price;
        int stock;

        Product(String name, double price, int stock) {
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        void displayInfo() {
            System.out.printf("%-15s R%.2f  [Stock: %d]%n", name, price, stock);
        }

        boolean isAvailable() {
            return stock > 0;
        }

        void sell(int quantity) {
            if (quantity > stock) {
                System.out.println("Not enough stock for: " + name);
            } else {
                stock -= quantity;
                System.out.println("Sold " + quantity + "x " + name);
            }
        }
    }

    // ─── MAIN ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Student Objects ===");
        Student s1 = new Student("Alice", 22, "Full Stack Development");
        Student s2 = new Student("Bob", 17, "Cybersecurity");
        Student s3 = new Student("Charlie", 25, "Data Science");

        s1.introduce();
        s2.introduce();
        s3.introduce();

        System.out.println("\nIs Alice an adult? " + s1.isAdult());
        System.out.println("Is Bob an adult?   " + s2.isAdult());

        // toString is called automatically when printing an object
        System.out.println("\nPrinting object directly → " + s1);

        System.out.println("\n=== Product Objects ===");
        Product laptop = new Product("Laptop Pro", 12999.99, 5);
        Product phone = new Product("SmartPhone X", 7499.00, 0);
        Product tablet = new Product("Tablet Z", 3299.50, 12);

        laptop.displayInfo();
        phone.displayInfo();
        tablet.displayInfo();

        System.out.println("\nPhone available? " + phone.isAvailable());

        System.out.println();
        laptop.sell(3);
        laptop.sell(4);  // Not enough stock
        laptop.displayInfo();

        // ─── NULL REFERENCE ───────────────────────────────────────────────
        System.out.println("\n=== Null Reference ===");
        Student emptyStudent = null;
        System.out.println("emptyStudent is null: " + (emptyStudent == null));

        // Always check for null before calling methods on a reference
        if (emptyStudent != null) {
            emptyStudent.introduce();
        } else {
            System.out.println("Cannot call method — student is null.");
        }
    }
}
