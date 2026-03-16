# ============================================================
# Module 4: Software Engineering Principles — Exercises
#
# For each exercise:
#   Step 1 — Read the brief
#   Step 2 — Identify which principle(s) apply
#   Step 3 — Implement the solution
#   Step 4 — Uncomment tests and verify output
# ============================================================

from abc import ABC, abstractmethod


# -------------------------------------------------------
# Exercise 1: Single Responsibility Principle (SRP)
#
# The class below does too many things. Refactor it into
# separate classes, each with one responsibility:
#   - `Order`           → stores order data
#   - `OrderRepository` → handles saving to DB
#   - `InvoiceService`  → handles generating invoices
#   - `EmailService`    → handles sending confirmation emails
# -------------------------------------------------------

# REFACTOR THIS:
class BadOrder:
    def __init__(self, item, price):
        self.item = item
        self.price = price

    def save_to_db(self):
        print(f"Saving order for {self.item} to database...")

    def generate_invoice(self):
        print(f"Invoice: {self.item} — ${self.price}")

    def send_confirmation_email(self):
        print(f"Email sent: Your order for {self.item} is confirmed!")


# YOUR REFACTORED CODE HERE


# Tests
# order = Order("Laptop", 1200)
# repo = OrderRepository()
# invoice = InvoiceService()
# email = EmailService()
# repo.save(order)
# invoice.generate(order)
# email.send_confirmation(order)


# -------------------------------------------------------
# Exercise 2: Open/Closed Principle (OCP)
#
# Build a `PaymentProcessor` that handles multiple payment methods.
# You should be able to add new payment methods (PayPal, Crypto, etc.)
# WITHOUT modifying the PaymentProcessor class.
#
# Payment methods to implement:
#   - CreditCardPayment
#   - PayPalPayment
#   - CryptoPayment
# -------------------------------------------------------

class PaymentMethod(ABC):
    @abstractmethod
    def pay(self, amount):
        pass


# YOUR PAYMENT METHOD CLASSES HERE


class PaymentProcessor:
    def process(self, payment: PaymentMethod, amount):
        # YOUR CODE HERE
        pass


# Tests
# processor = PaymentProcessor()
# processor.process(CreditCardPayment(), 500)   # Paid $500 via Credit Card
# processor.process(PayPalPayment(), 200)        # Paid $200 via PayPal
# processor.process(CryptoPayment(), 1000)       # Paid $1000 via Crypto


# -------------------------------------------------------
# Exercise 3: Liskov Substitution Principle (LSP)
#
# The hierarchy below violates LSP. Fix it by redesigning
# the class structure so all substitutions work correctly.
#
# Animals to model: Eagle, Penguin, Dolphin, Cheetah
# Behaviours: fly(), swim(), run()
# Not all animals can do all behaviours — design accordingly.
# -------------------------------------------------------

# BROKEN — fix this hierarchy:
class BrokenAnimal:
    def fly(self): pass
    def swim(self): pass
    def run(self): pass

class BrokenEagle(BrokenAnimal):
    def fly(self): print("Eagle flying")
    def swim(self): raise Exception("Eagles can't swim!")  # LSP violation
    def run(self): raise Exception("Eagles don't run!")    # LSP violation


# YOUR FIXED HIERARCHY HERE


# Tests
# eagle = Eagle()
# penguin = Penguin()
# dolphin = Dolphin()
# cheetah = Cheetah()
# eagle.move()    # Eagle soars through the sky
# penguin.move()  # Penguin swims gracefully
# dolphin.move()  # Dolphin glides through water
# cheetah.move()  # Cheetah sprints at full speed


# -------------------------------------------------------
# Exercise 4: Dependency Inversion Principle (DIP)
#
# Build a `ReportGenerator` that can export reports
# in different formats without being tied to any specific one.
#
# Export formats:
#   - PDFExporter    → "Exporting report as PDF..."
#   - CSVExporter    → "Exporting report as CSV..."
#   - JSONExporter   → "Exporting report as JSON..."
#
# ReportGenerator should accept any exporter via its constructor.
# -------------------------------------------------------

class Exporter(ABC):
    @abstractmethod
    def export(self, data):
        pass


# YOUR EXPORTER CLASSES HERE


class ReportGenerator:
    def __init__(self, exporter: Exporter):
        # YOUR CODE HERE
        pass

    def generate(self, data):
        # YOUR CODE HERE
        pass


# Tests
# data = {"title": "Q1 Sales", "total": 50000}
# ReportGenerator(PDFExporter()).generate(data)
# ReportGenerator(CSVExporter()).generate(data)
# ReportGenerator(JSONExporter()).generate(data)


# -------------------------------------------------------
# Exercise 5: Factory Pattern
#
# Build a `DatabaseFactory` that creates database connection objects.
# Supported databases: PostgreSQL, MongoDB, SQLite
#
# Each connection class should have:
#   - connect()     → prints connection message
#   - disconnect()  → prints disconnection message
#   - query(sql)    → prints the query being run
# -------------------------------------------------------

class DatabaseConnection(ABC):
    @abstractmethod
    def connect(self): pass

    @abstractmethod
    def disconnect(self): pass

    @abstractmethod
    def query(self, sql): pass


# YOUR DATABASE CLASSES AND FACTORY HERE


# Tests
# db = DatabaseFactory.create("postgres")
# db.connect()
# db.query("SELECT * FROM users")
# db.disconnect()

# db2 = DatabaseFactory.create("mongo")
# db2.connect()
# db2.query("db.users.find({})")
# db2.disconnect()


# -------------------------------------------------------
# Exercise 6: Observer Pattern
#
# Build a stock price alert system:
#
# Class `Stock`:
#   - Attributes: symbol, price
#   - Method: update_price(new_price) → notifies all observers
#   - Methods: subscribe(observer), unsubscribe(observer)
#
# Observer classes:
#   - `PriceLogger`     → logs every price change
#   - `AlertSystem`     → alerts if price drops below a threshold
#   - `TradingBot`      → prints "BUY" if price drops, "SELL" if rises
# -------------------------------------------------------

class Observer(ABC):
    @abstractmethod
    def update(self, symbol, old_price, new_price):
        pass


class Stock:
    # YOUR CODE HERE
    pass


class PriceLogger(Observer):
    # YOUR CODE HERE
    pass


class AlertSystem(Observer):
    def __init__(self, threshold):
        self.threshold = threshold
    # YOUR CODE HERE


class TradingBot(Observer):
    # YOUR CODE HERE
    pass


# Tests
# stock = Stock("AAPL", 150)
# stock.subscribe(PriceLogger())
# stock.subscribe(AlertSystem(140))
# stock.subscribe(TradingBot())
#
# stock.update_price(155)
# stock.update_price(138)
# stock.update_price(160)


# ============================================================
# BONUS: Combine SOLID + Patterns
#
# Build a mini E-Commerce Order Pipeline:
#   1. `Order` class (SRP — only holds order data)
#   2. `DiscountStrategy` (Strategy Pattern — percentage, flat, no discount)
#   3. `PaymentMethod` (Factory — credit card, PayPal)
#   4. `OrderObserver` (Observer — logger, email notifier)
#   5. `OrderService` (DIP — depends on abstractions, not concretions)
#      - place_order(order, discount, payment) → applies discount,
#        processes payment, notifies observers
# ============================================================

# YOUR BONUS CODE HERE
