/**
 * 03_Inheritance.java
 * ====================
 * Demonstrating inheritance: child classes reuse and extend parent behaviour.
 *
 * Key concepts: extends, super(), @Override, method hiding vs overriding
 */
public class Inheritance {

    // ─── BASE CLASS (PARENT) ──────────────────────────────────────────────
    static class Vehicle {
        protected String brand;
        protected String model;
        protected int year;
        protected double fuelLevel; // 0.0 to 100.0

        public Vehicle(String brand, String model, int year) {
            this.brand = brand;
            this.model = model;
            this.year = year;
            this.fuelLevel = 100.0;
        }

        public void start() {
            System.out.println(brand + " " + model + " engine started. 🔑");
        }

        public void stop() {
            System.out.println(brand + " " + model + " engine stopped.");
        }

        public void refuel(double amount) {
            fuelLevel = Math.min(100.0, fuelLevel + amount);
            System.out.printf("Refuelled. Fuel level: %.1f%%%n", fuelLevel);
        }

        public void displayInfo() {
            System.out.printf("[%d %s %s] Fuel: %.1f%%%n", year, brand, model, fuelLevel);
        }
    }

    // ─── CHILD CLASS: CAR ─────────────────────────────────────────────────
    static class Car extends Vehicle {
        private int numberOfDoors;

        public Car(String brand, String model, int year, int numberOfDoors) {
            super(brand, model, year); // MUST call parent constructor first
            this.numberOfDoors = numberOfDoors;
        }

        @Override
        public void start() {
            super.start(); // Reuse parent logic
            System.out.println("Seatbelt reminder: Please fasten your seatbelt!");
        }

        public void openTrunk() {
            System.out.println(brand + " " + model + " trunk opened.");
        }

        @Override
        public void displayInfo() {
            super.displayInfo();
            System.out.println("  Doors: " + numberOfDoors);
        }
    }

    // ─── CHILD CLASS: MOTORCYCLE ──────────────────────────────────────────
    static class Motorcycle extends Vehicle {
        private boolean hasSidecar;

        public Motorcycle(String brand, String model, int year, boolean hasSidecar) {
            super(brand, model, year);
            this.hasSidecar = hasSidecar;
        }

        @Override
        public void start() {
            System.out.println(brand + " " + model + " revs up! 🏍️ Vroom vroom!");
        }

        public void wheelie() {
            if (hasSidecar) {
                System.out.println("Can't do a wheelie with a sidecar!");
            } else {
                System.out.println(brand + " " + model + " pops a wheelie! 🤙");
            }
        }
    }

    // ─── CHILD CLASS: ELECTRIC CAR (grandchild) ───────────────────────────
    static class ElectricCar extends Car {
        private double batteryLevel;

        public ElectricCar(String brand, String model, int year) {
            super(brand, model, year, 4);
            this.batteryLevel = 100.0;
            this.fuelLevel = 0.0; // No fuel — uses battery
        }

        @Override
        public void start() {
            System.out.println(brand + " " + model + " starts silently... ⚡");
        }

        public void charge(double amount) {
            batteryLevel = Math.min(100.0, batteryLevel + amount);
            System.out.printf("Charging... Battery: %.1f%%%n", batteryLevel);
        }

        @Override
        public void refuel(double amount) {
            System.out.println("Electric vehicle — use charge() instead of refuel()!");
        }

        @Override
        public void displayInfo() {
            System.out.printf("[%d %s %s] Battery: %.1f%%  ⚡%n", year, brand, model, batteryLevel);
        }
    }

    // ─── MAIN ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {

        System.out.println("=== Car (extends Vehicle) ===");
        Car car = new Car("Toyota", "Corolla", 2023, 4);
        car.start();         // Overridden — adds seatbelt reminder
        car.displayInfo();
        car.openTrunk();
        car.stop();

        System.out.println("\n=== Motorcycle (extends Vehicle) ===");
        Motorcycle moto = new Motorcycle("Kawasaki", "Ninja", 2022, false);
        moto.start();
        moto.wheelie();
        moto.refuel(50);     // Inherited from Vehicle
        moto.displayInfo();  // Inherited from Vehicle

        System.out.println("\n=== Electric Car (extends Car extends Vehicle) ===");
        ElectricCar ev = new ElectricCar("Tesla", "Model 3", 2024);
        ev.start();
        ev.charge(20.0);
        ev.refuel(50.0);     // Overridden — prompts to charge instead
        ev.displayInfo();
        ev.openTrunk();      // Inherited from Car

        // ─── POLYMORPHIC ARRAY ────────────────────────────────────────────
        System.out.println("\n=== Polymorphism: All stored as Vehicle ===");
        Vehicle[] fleet = {
            new Car("BMW", "M3", 2023, 2),
            new Motorcycle("Harley", "Sportster", 2021, true),
            new ElectricCar("Tesla", "Model S", 2024),
            new Car("Ford", "Ranger", 2022, 4)
        };

        for (Vehicle v : fleet) {
            v.start(); // Each calls its own overridden version
        }

        // ─── instanceof CHECK ────────────────────────────────────────────
        System.out.println("\n=== instanceof ===");
        for (Vehicle v : fleet) {
            if (v instanceof ElectricCar ec) {
                ec.charge(30.0); // Pattern matching (Java 16+)
            } else if (v instanceof Car c) {
                c.openTrunk();
            } else if (v instanceof Motorcycle m) {
                m.wheelie();
            }
        }
    }
}
