/**
 * 06_Collections.java
 * ====================
 * Java Collections Framework: dynamic data structures.
 *
 * Key concepts:
 *  - ArrayList — ordered, dynamic list
 *  - LinkedList — doubly-linked, queue-friendly
 *  - HashMap — key-value store
 *  - HashSet — unique values
 *  - Iterating with for-each, iterator, and streams
 */
import java.util.*;
import java.util.stream.Collectors;

public class Collections_Example {

    public static void main(String[] args) {

        // ─── ARRAYLIST ────────────────────────────────────────────────────
        System.out.println("=== ArrayList ===");
        List<String> students = new ArrayList<>();

        students.add("Alice");
        students.add("Bob");
        students.add("Charlie");
        students.add("Diana");

        System.out.println("All students: " + students);
        System.out.println("Size: " + students.size());
        System.out.println("Get index 1: " + students.get(1));
        System.out.println("Contains 'Bob': " + students.contains("Bob"));

        // Update element
        students.set(1, "Benjamin");
        System.out.println("After update: " + students);

        // Remove by value
        students.remove("Charlie");
        // Remove by index
        students.remove(0);
        System.out.println("After removals: " + students);

        // Sort
        students.add("Alice");
        students.add("Zara");
        Collections.sort(students);
        System.out.println("Sorted: " + students);

        // ─── HASHMAP ─────────────────────────────────────────────────────
        System.out.println("\n=== HashMap (Key-Value) ===");
        Map<String, Integer> examScores = new HashMap<>();

        examScores.put("Alice", 95);
        examScores.put("Bob", 82);
        examScores.put("Charlie", 90);
        examScores.put("Diana", 88);

        System.out.println("Alice's score: " + examScores.get("Alice"));
        System.out.println("Has 'Bob'? " + examScores.containsKey("Bob"));
        System.out.println("Has score 90? " + examScores.containsValue(90));

        // Update value (put replaces if key exists)
        examScores.put("Bob", 87);

        // Iterate over key-value pairs
        System.out.println("\nAll scores:");
        for (Map.Entry<String, Integer> entry : examScores.entrySet()) {
            System.out.printf("  %-10s → %d%n", entry.getKey(), entry.getValue());
        }

        // getOrDefault — returns a fallback if key not found
        int score = examScores.getOrDefault("Eve", 0);
        System.out.println("Eve's score (not found, default): " + score);

        // ─── HASHSET ─────────────────────────────────────────────────────
        System.out.println("\n=== HashSet (Unique Values) ===");
        Set<String> tags = new HashSet<>();

        tags.add("java");
        tags.add("backend");
        tags.add("java");     // Duplicate — will be ignored
        tags.add("spring");
        tags.add("api");

        System.out.println("Tags: " + tags);
        System.out.println("Size (no duplicates): " + tags.size());
        System.out.println("Contains 'java': " + tags.contains("java"));

        // ─── LINKEDLIST AS A QUEUE ───────────────────────────────────────
        System.out.println("\n=== LinkedList as Queue (FIFO) ===");
        Queue<String> queue = new LinkedList<>();

        queue.offer("Task 1");  // add to end
        queue.offer("Task 2");
        queue.offer("Task 3");

        System.out.println("Queue: " + queue);
        System.out.println("Peek (head): " + queue.peek());   // view head
        System.out.println("Poll (remove head): " + queue.poll()); // remove head
        System.out.println("Queue after poll: " + queue);

        // ─── COLLECTIONS UTILITY METHODS ─────────────────────────────────
        System.out.println("\n=== Collections Utility ===");
        List<Integer> numbers = new ArrayList<>(Arrays.asList(5, 2, 8, 1, 9, 3, 7));

        System.out.println("Original: " + numbers);
        System.out.println("Max: " + Collections.max(numbers));
        System.out.println("Min: " + Collections.min(numbers));

        Collections.sort(numbers);
        System.out.println("Sorted: " + numbers);

        Collections.reverse(numbers);
        System.out.println("Reversed: " + numbers);

        Collections.shuffle(numbers);
        System.out.println("Shuffled: " + numbers);

        // ─── STREAMS (Java 8+) ────────────────────────────────────────────
        System.out.println("\n=== Streams API ===");
        List<Integer> scores = Arrays.asList(45, 88, 72, 90, 55, 68, 95, 38);

        // Filter: only scores >= 60
        List<Integer> passing = scores.stream()
                .filter(s -> s >= 60)
                .collect(Collectors.toList());
        System.out.println("Passing scores: " + passing);

        // Map: double each score
        List<Integer> doubled = scores.stream()
                .map(s -> s * 2)
                .collect(Collectors.toList());
        System.out.println("Doubled scores: " + doubled);

        // Average
        OptionalDouble avg = scores.stream()
                .mapToInt(Integer::intValue)
                .average();
        System.out.printf("Average: %.2f%n", avg.getAsDouble());

        // Count students who passed
        long passCount = scores.stream()
                .filter(s -> s >= 60)
                .count();
        System.out.println("Number passing: " + passCount);
    }
}
