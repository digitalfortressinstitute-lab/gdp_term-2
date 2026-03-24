/**
 * 06_Interfaces.java
 * ===================
 * Interfaces: define contracts that classes agree to honour.
 * Java allows implementing MULTIPLE interfaces (unlike single inheritance).
 *
 * Concepts:
 *  - Interface definition
 *  - Implementing multiple interfaces
 *  - Default methods (Java 8+)
 *  - Functional interfaces & lambdas
 */
import java.util.Arrays;
import java.util.List;

public class Interfaces {

    // ─── INTERFACE DEFINITIONS ────────────────────────────────────────────

    interface Printable {
        void print(); // abstract by default
    }

    interface Saveable {
        boolean save();
        boolean delete();
    }

    interface Searchable {
        List<String> search(String query);
    }

    // Interface with default method (Java 8+)
    interface Auditable {
        String getCreatedBy();
        String getUpdatedBy();

        // Default provides a ready-made implementation
        default String getAuditSummary() {
            return "Created by: " + getCreatedBy() + " | Updated by: " + getUpdatedBy();
        }
    }

    // ─── DOCUMENT CLASS: implements 3 interfaces ───────────────────────────
    static class Document implements Printable, Saveable, Auditable {
        private String title;
        private String content;
        private String author;

        public Document(String title, String content, String author) {
            this.title = title;
            this.content = content;
            this.author = author;
        }

        @Override
        public void print() {
            System.out.println("============================");
            System.out.println("TITLE: " + title);
            System.out.println("----------------------------");
            System.out.println(content);
            System.out.println("============================");
        }

        @Override
        public boolean save() {
            System.out.println("💾 Document '" + title + "' saved to database.");
            return true;
        }

        @Override
        public boolean delete() {
            System.out.println("🗑️ Document '" + title + "' deleted.");
            return true;
        }

        @Override
        public String getCreatedBy() { return author; }

        @Override
        public String getUpdatedBy() { return author + " (latest)"; }

        public String getTitle() { return title; }
    }

    // ─── NOTIFICATION SYSTEM ─────────────────────────────────────────────
    interface Notifiable {
        void sendNotification(String recipient, String message);
    }

    static class EmailNotifier implements Notifiable {
        @Override
        public void sendNotification(String recipient, String message) {
            System.out.printf("📧 Email → %s: \"%s\"%n", recipient, message);
        }
    }

    static class SMSNotifier implements Notifiable {
        @Override
        public void sendNotification(String recipient, String message) {
            System.out.printf("📱 SMS → %s: \"%s\"%n", recipient, message);
        }
    }

    static class PushNotifier implements Notifiable {
        @Override
        public void sendNotification(String recipient, String message) {
            System.out.printf("🔔 Push → %s: \"%s\"%n", recipient, message);
        }
    }

    // Dispatcher: works with ANY Notifiable implementation
    static void notifyAll(List<Notifiable> notifiers, String recipient, String msg) {
        System.out.println("\nSending notifications to: " + recipient);
        for (Notifiable n : notifiers) {
            n.sendNotification(recipient, msg);
        }
    }

    // ─── FUNCTIONAL INTERFACE + LAMBDA ───────────────────────────────────
    @FunctionalInterface
    interface Transformer<T, R> {
        R transform(T input);
    }

    // ─── MAIN ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Document (3 Interfaces) ===");
        Document doc = new Document(
                "REST API Design Guide",
                "REST APIs use HTTP methods: GET, POST, PUT, DELETE.",
                "Alice"
        );

        doc.print();
        doc.save();
        System.out.println(doc.getAuditSummary()); // Default method from Auditable
        doc.delete();

        // ─── Interface as type reference ─────────────────────────────────
        // You can assign a Document to any of its interface types:
        Printable printRef = doc;
        Saveable saveRef = doc;
        printRef.print();   // Can only call print()
        saveRef.save();     // Can only call save()/delete()

        System.out.println("\n=== Notification System ===");
        List<Notifiable> allChannels = Arrays.asList(
                new EmailNotifier(),
                new SMSNotifier(),
                new PushNotifier()
        );

        notifyAll(allChannels, "bob@gdp.com", "Your order has shipped!");

        // Mix and match — only use some notifiers for some recipients:
        List<Notifiable> emailOnly = List.of(new EmailNotifier());
        notifyAll(emailOnly, "charlie@gdp.com", "Invoice ready for download.");

        // ─── Functional Interface + Lambda ───────────────────────────────
        System.out.println("\n=== Functional Interface (Lambda) ===");

        Transformer<String, String> toUpperCase = s -> s.toUpperCase();
        Transformer<Integer, String> toStatusMsg = code ->
                code == 200 ? "OK" : code == 404 ? "Not Found" : "Unknown";

        System.out.println(toUpperCase.transform("hello world"));
        System.out.println(toStatusMsg.transform(200));
        System.out.println(toStatusMsg.transform(404));

        // ─── Anonymous Class (alternative to lambda) ──────────────────────
        System.out.println("\n=== Anonymous Class ===");
        Notifiable slackNotifier = new Notifiable() {
            @Override
            public void sendNotification(String recipient, String message) {
                System.out.printf("💬 Slack → #%s: \"%s\"%n", recipient, message);
            }
        };
        slackNotifier.sendNotification("general", "Server deployment complete! ✅");
    }
}
