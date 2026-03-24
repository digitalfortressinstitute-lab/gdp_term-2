/**
 * 05_Abstraction.java
 * ====================
 * Abstract classes: define a partial blueprint with abstract methods
 * that subclasses MUST implement.
 *
 * Key concepts:
 *  - abstract class vs concrete class
 *  - abstract method (no body) vs concrete method (has body)
 *  - Template Method design pattern
 */
public class Abstraction {

    // ─── ABSTRACT CLASS ───────────────────────────────────────────────────
    // Cannot be instantiated — only subclassed
    static abstract class Employee {
        protected String name;
        protected String department;
        protected int hoursWorked;

        public Employee(String name, String department) {
            this.name = name;
            this.department = department;
        }

        // ABSTRACT — each employee type has different salary calculation
        public abstract double calculateSalary();

        // CONCRETE — same for all employees
        public void logHours(int hours) {
            this.hoursWorked = hours;
            System.out.println(name + " logged " + hours + " hours.");
        }

        // TEMPLATE METHOD PATTERN —
        // Calls abstract methods to form a complete "template" flow
        public final void generatePayslip() {
            System.out.println("\n--- PAYSLIP ---");
            System.out.println("Employee:   " + name);
            System.out.println("Department: " + department);
            System.out.println("Hours:      " + hoursWorked);
            System.out.printf("Gross Pay:  R%.2f%n", calculateSalary());
            double tax = calculateTax(calculateSalary());
            System.out.printf("Tax (%.0f%%):  R%.2f%n", getTaxRate() * 100, tax);
            System.out.printf("Net Pay:    R%.2f%n", calculateSalary() - tax);
            System.out.println("---------------");
        }

        // Hook method — subclasses can override but don't have to
        protected double getTaxRate() { return 0.25; } // default 25%

        private double calculateTax(double grossPay) {
            return grossPay * getTaxRate();
        }
    }

    // ─── CONCRETE SUBCLASSES ─────────────────────────────────────────────
    static class FullTimeEmployee extends Employee {
        private double monthlySalary;

        public FullTimeEmployee(String name, String department, double monthlySalary) {
            super(name, department);
            this.monthlySalary = monthlySalary;
        }

        @Override
        public double calculateSalary() {
            return monthlySalary; // Fixed monthly salary
        }
    }

    static class PartTimeEmployee extends Employee {
        private double hourlyRate;

        public PartTimeEmployee(String name, String department, double hourlyRate) {
            super(name, department);
            this.hourlyRate = hourlyRate;
        }

        @Override
        public double calculateSalary() {
            return hourlyRate * hoursWorked; // Paid per hour
        }

        @Override
        protected double getTaxRate() { return 0.15; } // Lower tax rate for part-time
    }

    static class Contractor extends Employee {
        private double projectFee;

        public Contractor(String name, String department, double projectFee) {
            super(name, department);
            this.projectFee = projectFee;
        }

        @Override
        public double calculateSalary() {
            return projectFee; // Fixed project fee
        }

        @Override
        protected double getTaxRate() { return 0.30; } // Contractors taxed more
    }

    // ─── ABSTRACT SHAPE HIERARCHY (classic example) ───────────────────────
    static abstract class Shape {
        private String color;

        public Shape(String color) { this.color = color; }

        // Abstract — subclass must provide
        public abstract double area();
        public abstract double perimeter();
        public abstract String shapeName();

        // Concrete — shared logic
        public void describe() {
            System.out.printf("%s %s → Area: %.2f | Perimeter: %.2f%n",
                    color, shapeName(), area(), perimeter());
        }
    }

    static class Circle extends Shape {
        private double r;
        public Circle(String color, double r) { super(color); this.r = r; }

        @Override public double area() { return Math.PI * r * r; }
        @Override public double perimeter() { return 2 * Math.PI * r; }
        @Override public String shapeName() { return "Circle(r=" + r + ")"; }
    }

    static class Rectangle extends Shape {
        private double w, h;
        public Rectangle(String color, double w, double h) { super(color); this.w = w; this.h = h; }

        @Override public double area() { return w * h; }
        @Override public double perimeter() { return 2 * (w + h); }
        @Override public String shapeName() { return "Rectangle(" + w + "×" + h + ")"; }
    }

    static class Triangle extends Shape {
        private double a, b, c;
        public Triangle(String color, double a, double b, double c) { super(color); this.a = a; this.b = b; this.c = c; }

        @Override public double area() {
            double s = (a + b + c) / 2; // Heron's formula
            return Math.sqrt(s * (s - a) * (s - b) * (s - c));
        }
        @Override public double perimeter() { return a + b + c; }
        @Override public String shapeName() { return "Triangle(" + a + "," + b + "," + c + ")"; }
    }

    // ─── MAIN ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        // Abstract Employee — cannot instantiate directly:
        // Employee e = new Employee("X", "Y"); ← Compile Error!

        System.out.println("=== Payslips ===");
        FullTimeEmployee ft = new FullTimeEmployee("Alice", "Engineering", 45000);
        PartTimeEmployee pt = new PartTimeEmployee("Bob", "Marketing", 180);
        Contractor co       = new Contractor("Charlie", "IT", 70000);

        ft.logHours(160);
        pt.logHours(80);
        co.logHours(200);

        ft.generatePayslip();
        pt.generatePayslip();
        co.generatePayslip();

        System.out.println("\n=== Shapes ===");
        Shape[] shapes = {
            new Circle("Red", 5.0),
            new Rectangle("Blue", 8.0, 4.0),
            new Triangle("Green", 3.0, 4.0, 5.0)
        };

        for (Shape shape : shapes) {
            shape.describe(); // Polymorphic call via abstract class reference
        }
    }
}
