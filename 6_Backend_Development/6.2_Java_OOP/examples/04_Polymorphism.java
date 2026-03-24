/**
 * 04_Polymorphism.java
 * =====================
 * Polymorphism: same interface, different behaviour.
 * Runtime (method overriding) and compile-time (method overloading).
 */
public class Polymorphism {

    // ─── PAYMENT SYSTEM EXAMPLE ───────────────────────────────────────────
    static abstract class PaymentMethod {
        protected String owner;
        protected double balance;

        public PaymentMethod(String owner, double balance) {
            this.owner = owner;
            this.balance = balance;
        }

        // Each payment method processes differently
        public abstract boolean processPayment(double amount);

        public double getBalance() { return balance; }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [" + owner + "]";
        }
    }

    static class CreditCard extends PaymentMethod {
        private double creditLimit;

        public CreditCard(String owner, double creditLimit) {
            super(owner, 0); // balance = debt owed
            this.creditLimit = creditLimit;
        }

        @Override
        public boolean processPayment(double amount) {
            if (balance + amount > creditLimit) {
                System.out.println("❌ Credit limit exceeded. Limit: R" + creditLimit);
                return false;
            }
            balance += amount;
            System.out.printf("💳 Credit Card charged R%.2f. Debt: R%.2f%n", amount, balance);
            return true;
        }
    }

    static class DebitCard extends PaymentMethod {

        public DebitCard(String owner, double initialBalance) {
            super(owner, initialBalance);
        }

        @Override
        public boolean processPayment(double amount) {
            if (amount > balance) {
                System.out.println("❌ Insufficient funds. Balance: R" + balance);
                return false;
            }
            balance -= amount;
            System.out.printf("💳 Debit Card — R%.2f deducted. Remaining: R%.2f%n", amount, balance);
            return true;
        }
    }

    static class Cryptocurrency extends PaymentMethod {
        private String coinType;
        private double exchangeRate; // 1 coin = R exchangeRate

        public Cryptocurrency(String owner, double coins, String coinType, double exchangeRate) {
            super(owner, coins);
            this.coinType = coinType;
            this.exchangeRate = exchangeRate;
        }

        @Override
        public boolean processPayment(double amountInRands) {
            double coinsNeeded = amountInRands / exchangeRate;
            if (coinsNeeded > balance) {
                System.out.printf("❌ Insufficient %s. Have: %.6f, Need: %.6f%n",
                        coinType, balance, coinsNeeded);
                return false;
            }
            balance -= coinsNeeded;
            System.out.printf("₿ Paid R%.2f using %.6f %s. Remaining: %.6f%n",
                    amountInRands, coinsNeeded, coinType, balance);
            return true;
        }
    }

    // ─── CHECKOUT SYSTEM — uses polymorphism ──────────────────────────────
    static void checkout(PaymentMethod method, double amount) {
        System.out.println("\nCheckout: R" + amount + " via " + method);
        boolean success = method.processPayment(amount); // polymorphic call
        System.out.println("Status: " + (success ? "✅ Success" : "❌ Failed"));
    }

    // ─── COMPILE-TIME: METHOD OVERLOADING ────────────────────────────────
    static class Formatter {
        static String format(int value) {
            return "Integer: " + value;
        }

        static String format(double value) {
            return String.format("Double: %.2f", value);
        }

        static String format(String value) {
            return "String: \"" + value + "\"";
        }

        static String format(boolean value) {
            return "Boolean: " + (value ? "TRUE" : "FALSE");
        }
    }

    // ─── MAIN ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Polymorphic Payment System ===");

        PaymentMethod[] paymentMethods = {
            new CreditCard("Alice", 5000.0),
            new DebitCard("Bob", 2000.0),
            new Cryptocurrency("Charlie", 0.05, "BTC", 1_200_000)
        };

        checkout(paymentMethods[0], 1500.00); // Uses CreditCard.processPayment()
        checkout(paymentMethods[1], 3000.00); // Uses DebitCard.processPayment() — fails
        checkout(paymentMethods[1], 500.00);  // Uses DebitCard.processPayment() — succeeds
        checkout(paymentMethods[2], 60000.00); // Uses Cryptocurrency.processPayment()

        System.out.println("\n=== Compile-Time Polymorphism (Overloading) ===");
        System.out.println(Formatter.format(42));
        System.out.println(Formatter.format(3.14));
        System.out.println(Formatter.format("Hello Java!"));
        System.out.println(Formatter.format(true));
    }
}
