/**
 * 03_ControlFlow.java
 * ====================
 * Controlling the flow of execution: conditions, switches, and loops.
 */
public class ControlFlow {

    public static void main(String[] args) {

        // ─── IF / ELSE IF / ELSE ─────────────────────────────────────────
        System.out.println("=== If / Else ===");
        int score = 76;

        if (score >= 80) {
            System.out.println("Distinction");
        } else if (score >= 60) {
            System.out.println("Pass");
        } else if (score >= 50) {
            System.out.println("Borderline");
        } else {
            System.out.println("Fail");
        }

        // Ternary operator — short if/else on one line
        String result = score >= 60 ? "Passed" : "Failed";
        System.out.println("Result: " + result);

        // ─── SWITCH STATEMENT ────────────────────────────────────────────
        System.out.println("\n=== Switch ===");
        int day = 3;
        String dayName;

        switch (day) {
            case 1: dayName = "Monday"; break;
            case 2: dayName = "Tuesday"; break;
            case 3: dayName = "Wednesday"; break;
            case 4: dayName = "Thursday"; break;
            case 5: dayName = "Friday"; break;
            default: dayName = "Weekend";
        }
        System.out.println("Day: " + dayName);

        // Modern switch expression (Java 14+)
        String season = "Winter";
        int tempRange = switch (season) {
            case "Summer" -> 35;
            case "Spring", "Autumn" -> 20;
            case "Winter" -> 10;
            default -> 0;
        };
        System.out.println("Winter temp range: ~" + tempRange + "°C");

        // ─── FOR LOOP ────────────────────────────────────────────────────
        System.out.println("\n=== For Loop ===");
        for (int i = 1; i <= 5; i++) {
            System.out.println("Iteration " + i);
        }

        // Counting backwards
        for (int i = 10; i >= 1; i -= 2) {
            System.out.print(i + " ");
        }
        System.out.println();

        // ─── WHILE LOOP ──────────────────────────────────────────────────
        System.out.println("\n=== While Loop ===");
        int num = 1;
        while (num <= 5) {
            System.out.print(num + " ");
            num++;
        }
        System.out.println();

        // ─── DO-WHILE LOOP ───────────────────────────────────────────────
        // Executes the body AT LEAST ONCE before checking the condition
        System.out.println("\n=== Do-While ===");
        int x = 10;
        do {
            System.out.println("x = " + x); // Prints even though x > 5
            x++;
        } while (x < 5); // Condition is false immediately after first run

        // ─── FOR-EACH LOOP ───────────────────────────────────────────────
        System.out.println("\n=== Enhanced For Loop ===");
        String[] languages = {"Java", "Python", "JavaScript", "TypeScript"};
        for (String lang : languages) {
            System.out.println("Language: " + lang);
        }

        // ─── BREAK AND CONTINUE ──────────────────────────────────────────
        System.out.println("\n=== Break and Continue ===");

        // break — exits the loop immediately
        for (int i = 0; i < 10; i++) {
            if (i == 5) break;
            System.out.print(i + " ");
        }
        System.out.println("← broke at 5");

        // continue — skips the current iteration
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) continue; // skip even numbers
            System.out.print(i + " ");
        }
        System.out.println("← only odd numbers");
    }
}
