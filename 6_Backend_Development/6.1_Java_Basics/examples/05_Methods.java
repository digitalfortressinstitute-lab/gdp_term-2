/**
 * 05_Methods.java
 * ===============
 * Methods: defining reusable blocks of logic.
 *
 * Key concepts:
 *  - Method signature (access, static, return type, name, params)
 *  - void vs return type
 *  - Method overloading
 *  - Pass by value
 *  - Recursion
 */
public class Methods {

    // ─── SIMPLE METHOD ───────────────────────────────────────────────────
    public static void greet(String name) {
        System.out.println("Hello, " + name + "!");
    }

    // ─── RETURN VALUE ────────────────────────────────────────────────────
    public static int add(int a, int b) {
        return a + b;
    }

    public static double calculateArea(double radius) {
        return Math.PI * radius * radius;
    }

    // ─── METHOD OVERLOADING ───────────────────────────────────────────────
    // Same name, different parameters — Java resolves at compile time
    public static int multiply(int a, int b) {
        return a * b;
    }

    public static double multiply(double a, double b) {
        return a * b;
    }

    public static String multiply(String text, int times) {
        return text.repeat(times);
    }

    // ─── DEFAULT-STYLE PARAMETER (using overloads) ───────────────────────
    public static void printLine(String message) {
        printLine(message, 1);
    }

    public static void printLine(String message, int count) {
        for (int i = 0; i < count; i++) {
            System.out.println(message);
        }
    }

    // ─── PASS BY VALUE ───────────────────────────────────────────────────
    // Java passes primitives by value — the original is not modified
    public static void tryToModify(int x) {
        x = 999; // Only changes the local copy
    }

    // ─── VARARGS ─────────────────────────────────────────────────────────
    // Accept any number of arguments of the same type
    public static int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    // ─── RECURSION ───────────────────────────────────────────────────────
    // A method that calls itself — must have a base case to avoid infinite loop
    public static int factorial(int n) {
        if (n <= 1) return 1;        // Base case
        return n * factorial(n - 1); // Recursive case
    }

    public static int fibonacci(int n) {
        if (n <= 1) return n;                          // Base cases: fib(0)=0, fib(1)=1
        return fibonacci(n - 1) + fibonacci(n - 2);   // Recursive case
    }

    // ─── MAIN ────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Greet ===");
        greet("Alice");
        greet("Bob");

        System.out.println("\n=== Return Values ===");
        int result = add(7, 3);
        System.out.println("7 + 3 = " + result);
        System.out.printf("Area of circle r=5: %.2f%n", calculateArea(5));

        System.out.println("\n=== Overloading ===");
        System.out.println(multiply(4, 5));          // int version → 20
        System.out.println(multiply(2.5, 4.0));      // double version → 10.0
        System.out.println(multiply("Ha", 3));       // String version → HaHaHa

        System.out.println("\n=== Default-style (Overloads) ===");
        printLine("---");               // 1 time
        printLine("Hello!", 3);         // 3 times

        System.out.println("\n=== Pass By Value ===");
        int original = 42;
        tryToModify(original);
        System.out.println("Original value after tryToModify: " + original); // Still 42

        System.out.println("\n=== Varargs ===");
        System.out.println("sum(1,2,3) = " + sum(1, 2, 3));
        System.out.println("sum(10,20,30,40) = " + sum(10, 20, 30, 40));

        System.out.println("\n=== Recursion — Factorial ===");
        for (int i = 1; i <= 7; i++) {
            System.out.println(i + "! = " + factorial(i));
        }

        System.out.println("\n=== Recursion — Fibonacci ===");
        for (int i = 0; i <= 9; i++) {
            System.out.print(fibonacci(i) + " ");
        }
        System.out.println();
    }
}
