/**
 * 01_HelloWorld.java
 * ==================
 * The simplest Java program — the entry point to learning Java.
 *
 * Key concepts:
 *  - Every Java program needs at least ONE class
 *  - The filename MUST match the public class name
 *  - The JVM starts execution at the main() method
 *
 * To compile and run:
 *   javac 01_HelloWorld.java
 *   java HelloWorld
 */
public class HelloWorld {

    public static void main(String[] args) {
        // Print to the console (with newline at the end)
        System.out.println("Hello, World!");

        // Print without a newline
        System.out.print("Hello ");
        System.out.print("from Java!");
        System.out.println(); // adds blank newline

        // Print with formatted values (like printf in C / f-strings in Python)
        String name = "Student";
        int year = 2024;
        System.out.printf("Welcome, %s! Year: %d%n", name, year);

        // String concatenation
        System.out.println("Welcome to " + "Java" + " programming!");
    }
}
