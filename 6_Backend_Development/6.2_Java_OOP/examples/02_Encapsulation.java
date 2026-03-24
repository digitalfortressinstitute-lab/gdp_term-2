/**
 * 02_Encapsulation.java
 * ======================
 * Hiding internal state and exposing only safe, controlled access
 * through getters and setters. This prevents invalid data from entering objects.
 */
public class Encapsulation {

    // ─── BANK ACCOUNT ─────────────────────────────────────────────────────
    static class BankAccount {
        private String owner;      // private: cannot be accessed from outside
        private double balance;
        private String accountNumber;

        public BankAccount(String owner, double initialBalance) {
            this.owner = owner;
            this.balance = initialBalance;
            this.accountNumber = generateAccountNumber();
        }

        private String generateAccountNumber() {
            // Private helper — only accessible within the class
            return "ACC-" + (int)(Math.random() * 900000 + 100000);
        }

        // Getter — read-only access
        public double getBalance() { return balance; }
        public String getOwner() { return owner; }
        public String getAccountNumber() { return accountNumber; }

        // Controlled write operations with validation
        public void deposit(double amount) {
            if (amount <= 0) {
                System.out.println("❌ Deposit amount must be positive.");
                return;
            }
            balance += amount;
            System.out.printf("✅ Deposited R%.2f. New balance: R%.2f%n", amount, balance);
        }

        public void withdraw(double amount) {
            if (amount <= 0) {
                System.out.println("❌ Withdrawal amount must be positive.");
                return;
            }
            if (amount > balance) {
                System.out.println("❌ Insufficient funds. Balance: R" + balance);
                return;
            }
            balance -= amount;
            System.out.printf("✅ Withdrawn R%.2f. New balance: R%.2f%n", amount, balance);
        }

        public void transfer(BankAccount target, double amount) {
            System.out.println("\nTransferring R" + amount + " to " + target.getOwner());
            this.withdraw(amount);
            target.deposit(amount);
        }

        @Override
        public String toString() {
            return String.format("[%s] Owner: %s | Balance: R%.2f",
                    accountNumber, owner, balance);
        }
    }

    // ─── PERSON CLASS with validation ─────────────────────────────────────
    static class Person {
        private String name;
        private int age;
        private String email;

        public Person(String name, int age, String email) {
            setName(name);
            setAge(age);
            setEmail(email);
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public String getEmail() { return email; }

        // Setter with validation
        public void setName(String name) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Name cannot be empty.");
            }
            this.name = name.trim();
        }

        public void setAge(int age) {
            if (age < 0 || age > 150) {
                throw new IllegalArgumentException("Age must be between 0 and 150.");
            }
            this.age = age;
        }

        public void setEmail(String email) {
            if (email == null || !email.contains("@")) {
                throw new IllegalArgumentException("Invalid email address.");
            }
            this.email = email.toLowerCase();
        }

        @Override
        public String toString() {
            return String.format("Person{name='%s', age=%d, email='%s'}", name, age, email);
        }
    }

    // ─── MAIN ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Bank Account Encapsulation ===\n");

        BankAccount alice = new BankAccount("Alice", 5000.00);
        BankAccount bob   = new BankAccount("Bob", 1000.00);

        System.out.println(alice);
        System.out.println(bob);

        System.out.println();
        alice.deposit(2000.00);
        alice.withdraw(500.00);
        alice.withdraw(10000.00);  // Should fail
        alice.deposit(-100);       // Should fail

        alice.transfer(bob, 1500.00);

        System.out.println("\nFinal balances:");
        System.out.println(alice);
        System.out.println(bob);

        // Direct field access would cause a compile error (they're private):
        // alice.balance = -99999;  ← ❌ Compile Error

        System.out.println("\n=== Person Validation ===\n");

        Person person = new Person("  Alice  ", 25, "Alice@GDP.com");
        System.out.println(person);

        // Test invalid data
        try {
            person.setAge(200);  // Should throw
        } catch (IllegalArgumentException e) {
            System.out.println("Caught error: " + e.getMessage());
        }

        try {
            person.setEmail("not-an-email");  // Should throw
        } catch (IllegalArgumentException e) {
            System.out.println("Caught error: " + e.getMessage());
        }

        System.out.println("Person unchanged: " + person);
    }
}
