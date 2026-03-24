/**
 * 04_Arrays.java
 * ==============
 * Working with fixed-size arrays in Java.
 *
 * Key concepts:
 *  - Declaration, initialisation, access
 *  - Iterating with for and for-each
 *  - 2D arrays
 *  - Arrays utility class
 */
import java.util.Arrays;

public class Arrays_Example {

    public static void main(String[] args) {

        // ─── DECLARING AND INITIALISING ──────────────────────────────────
        System.out.println("=== Declaring Arrays ===");

        // Method 1: declare with size then assign
        int[] marks = new int[5];
        marks[0] = 85;
        marks[1] = 90;
        marks[2] = 78;
        marks[3] = 92;
        marks[4] = 88;

        // Method 2: declare and initialise in one line
        String[] fruits = {"Apple", "Banana", "Cherry", "Durian"};

        System.out.println("Fruits array length: " + fruits.length);
        System.out.println("First fruit: " + fruits[0]);
        System.out.println("Last fruit: " + fruits[fruits.length - 1]);

        // ─── ITERATING ───────────────────────────────────────────────────
        System.out.println("\n=== Iterating with for loop ===");
        for (int i = 0; i < marks.length; i++) {
            System.out.println("marks[" + i + "] = " + marks[i]);
        }

        System.out.println("\n=== Iterating with for-each ===");
        for (String fruit : fruits) {
            System.out.println(fruit);
        }

        // ─── COMMON OPERATIONS ───────────────────────────────────────────
        System.out.println("\n=== Arrays Utility Class ===");

        // Sort an array
        int[] numbers = {5, 2, 8, 1, 9, 3};
        Arrays.sort(numbers);
        System.out.println("Sorted: " + Arrays.toString(numbers));

        // Search in sorted array
        int index = Arrays.binarySearch(numbers, 8);
        System.out.println("Index of 8: " + index);

        // Fill an array with a value
        int[] zeros = new int[5];
        Arrays.fill(zeros, 7);
        System.out.println("Filled: " + Arrays.toString(zeros));

        // Copy an array
        int[] copy = Arrays.copyOf(numbers, numbers.length);
        System.out.println("Copy: " + Arrays.toString(copy));

        // ─── COMPUTE SUM AND AVERAGE ─────────────────────────────────────
        System.out.println("\n=== Sum and Average ===");
        int sum = 0;
        for (int mark : marks) {
            sum += mark;
        }
        double average = (double) sum / marks.length;
        System.out.printf("Sum: %d | Average: %.2f%n", sum, average);

        // ─── FIND MAX AND MIN ─────────────────────────────────────────────
        int max = marks[0];
        int min = marks[0];
        for (int mark : marks) {
            if (mark > max) max = mark;
            if (mark < min) min = mark;
        }
        System.out.println("Max: " + max + " | Min: " + min);

        // ─── 2D ARRAY ─────────────────────────────────────────────────────
        System.out.println("\n=== 2D Array (Matrix) ===");
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        // Print matrix grid
        for (int row = 0; row < matrix.length; row++) {
            for (int col = 0; col < matrix[row].length; col++) {
                System.out.printf("%3d", matrix[row][col]);
            }
            System.out.println();
        }

        System.out.println("Element at [1][2] = " + matrix[1][2]); // 6
    }
}
