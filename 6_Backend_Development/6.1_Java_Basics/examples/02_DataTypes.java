/**
 * 02_DataTypes.java
 * =================
 * Java's data type system: primitives and reference types.
 *
 * Key concepts:
 *  - Primitives: int, long, double, float, boolean, char, byte, short
 *  - Reference types: String, arrays, objects
 *  - Widening vs narrowing conversion
 *  - var keyword (Java 10+ local type inference)
 */
public class DataTypes {

    public static void main(String[] args) {

        // ─── PRIMITIVE TYPES ────────────────────────────────────────────
        int age = 25;                     // 32-bit integer
        long population = 8_000_000_000L; // 64-bit integer (note the L suffix)
        double price = 19.99;             // 64-bit floating point (default for decimals)
        float temperature = 36.6f;        // 32-bit floating point (note the f suffix)
        boolean isEnrolled = true;        // true or false only
        char grade = 'A';                 // single character — use single quotes

        System.out.println("=== Primitive Types ===");
        System.out.println("Age: " + age);
        System.out.println("Population: " + population);
        System.out.println("Price: " + price);
        System.out.println("Temperature: " + temperature);
        System.out.println("Enrolled: " + isEnrolled);
        System.out.println("Grade: " + grade);

        // ─── REFERENCE TYPES ────────────────────────────────────────────
        String name = "Alice";
        String sentence = "I am learning Java.";

        System.out.println("\n=== String (Reference Type) ===");
        System.out.println("Name: " + name);
        System.out.println("Length: " + name.length());
        System.out.println("Uppercase: " + name.toUpperCase());
        System.out.println("Contains 'Al': " + name.contains("Al"));
        System.out.println("Substring: " + sentence.substring(5, 7));

        // String comparison — ALWAYS use .equals(), NOT ==
        String a = "hello";
        String b = "hello";
        System.out.println("\n=== String Comparison ===");
        System.out.println("Using == : " + (a == b));         // May be true due to interning — unreliable
        System.out.println("Using .equals(): " + a.equals(b)); // Always correct ✅

        // ─── TYPE CONVERSION ────────────────────────────────────────────
        System.out.println("\n=== Type Conversion ===");

        // Widening (automatic, safe — smaller → larger)
        int smallNumber = 100;
        long bigNumber = smallNumber;   // int → long: automatic
        double decimal = smallNumber;   // int → double: automatic
        System.out.println("int → double (widening): " + decimal);

        // Narrowing (manual cast required — may lose precision)
        double pi = 3.14159;
        int truncated = (int) pi;       // Drops the decimal part — NOT rounded
        System.out.println("double → int (narrowing): " + truncated);

        // String to int
        String numberStr = "42";
        int parsed = Integer.parseInt(numberStr);
        System.out.println("String → int: " + parsed);

        // int to String
        int num = 99;
        String asString = String.valueOf(num);
        System.out.println("int → String: " + asString);

        // ─── var (local type inference, Java 10+) ───────────────────────
        System.out.println("\n=== var keyword ===");
        var city = "Cape Town";    // Inferred as String
        var count = 10;            // Inferred as int
        System.out.println(city + " has approximately " + count + " million people.");

        // ─── ARITHMETIC ─────────────────────────────────────────────────
        System.out.println("\n=== Arithmetic ===");
        int x = 17, y = 5;
        System.out.println("17 + 5 = " + (x + y));   // 22
        System.out.println("17 - 5 = " + (x - y));   // 12
        System.out.println("17 * 5 = " + (x * y));   // 85
        System.out.println("17 / 5 = " + (x / y));   // 3 (integer division!)
        System.out.println("17 % 5 = " + (x % y));   // 2 (remainder)
        System.out.println("17.0 / 5 = " + (x / 5.0)); // 3.4 (float division)
    }
}
