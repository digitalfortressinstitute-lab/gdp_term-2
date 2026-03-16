# ============================================================
# Module 2: Object-Oriented Programming — Exercises
#
# For each exercise:
#   Step 1 — Read the brief carefully
#   Step 2 — Plan with pseudocode in the comments
#   Step 3 — Implement in Python
#   Step 4 — Uncomment the tests and verify your output
# ============================================================


# -------------------------------------------------------
# Exercise 1: Classes & Objects
# Build a `Student` class with:
#   Attributes: name, age, grade (0-100)
#   Methods:
#     - introduce()    → "Hi, I'm [name], I'm [age] years old"
#     - get_status()   → returns "Pass" if grade >= 50, else "Fail"
#     - __str__()      → returns "Student: [name] | Grade: [grade]"
# -------------------------------------------------------

class Student:
    # YOUR CODE HERE
    pass


# Tests
# s = Student("Victor", 22, 75)
# s.introduce()              # Hi, I'm Victor, I'm 22 years old
# print(s.get_status())      # Pass
# print(s)                   # Student: Victor | Grade: 75


# -------------------------------------------------------
# Exercise 2: Encapsulation
# Build a `Wallet` class with:
#   Private attribute: __balance
#   Methods:
#     - deposit(amount)    → adds to balance, reject if amount <= 0
#     - withdraw(amount)   → subtracts, reject if insufficient funds
#     - get_balance()      → returns current balance
#     - __str__()          → "Wallet balance: $[balance]"
# -------------------------------------------------------

class Wallet:
    # YOUR CODE HERE
    pass


# Tests
# w = Wallet(500)
# w.deposit(200)
# print(w.get_balance())     # 700
# w.withdraw(1000)           # Insufficient funds
# w.withdraw(300)
# print(w)                   # Wallet balance: $400


# -------------------------------------------------------
# Exercise 3: Inheritance
# Create a base class `Vehicle`:
#   Attributes: brand, speed
#   Method: describe() → "[brand] travels at [speed]km/h"
#
# Create subclasses:
#   - `Car`        → extra attribute: num_doors
#                    override describe() to include doors
#   - `Motorcycle` → extra attribute: has_sidecar (True/False)
#                    override describe() to include sidecar info
#   - `Truck`      → extra attribute: payload_tons
#                    override describe() to include payload
#
# All subclasses must call super().__init__()
# -------------------------------------------------------

class Vehicle:
    # YOUR CODE HERE
    pass

class Car(Vehicle):
    # YOUR CODE HERE
    pass

class Motorcycle(Vehicle):
    # YOUR CODE HERE
    pass

class Truck(Vehicle):
    # YOUR CODE HERE
    pass


# Tests
# c = Car("Toyota", 180, 4)
# m = Motorcycle("Harley", 200, False)
# t = Truck("Volvo", 120, 20)
# c.describe()      # Toyota travels at 180km/h | Doors: 4
# m.describe()      # Harley travels at 200km/h | Sidecar: No
# t.describe()      # Volvo travels at 120km/h | Payload: 20 tons


# -------------------------------------------------------
# Exercise 4: Polymorphism
# Using your Vehicle subclasses above,
# create a list of at least 4 vehicles (mix of Car, Motorcycle, Truck)
# Loop through and call describe() on each.
#
# Then write a function `fastest_vehicle(vehicles)`
# that returns the vehicle with the highest speed.
# -------------------------------------------------------

def fastest_vehicle(vehicles):
    # YOUR CODE HERE
    pass


# Tests
# fleet = [
#     Car("BMW", 250, 2),
#     Motorcycle("Ducati", 280, False),
#     Truck("MAN", 100, 15),
#     Car("Toyota", 180, 4),
# ]
# for v in fleet:
#     v.describe()
# print(fastest_vehicle(fleet))  # Should print Ducati's description


# -------------------------------------------------------
# Exercise 5: Abstraction
# Create an abstract class `Employee` with:
#   Abstract method: get_salary()
#   Concrete method: describe() → "Employee: [name] | Salary: $[get_salary()]"
#
# Subclasses:
#   - `FullTimeEmployee`   → fixed monthly_salary
#   - `PartTimeEmployee`   → hourly_rate * hours_worked
#   - `FreelanceEmployee`  → rate_per_project * num_projects
# -------------------------------------------------------

from abc import ABC, abstractmethod

class Employee(ABC):
    # YOUR CODE HERE
    pass

class FullTimeEmployee(Employee):
    # YOUR CODE HERE
    pass

class PartTimeEmployee(Employee):
    # YOUR CODE HERE
    pass

class FreelanceEmployee(Employee):
    # YOUR CODE HERE
    pass


# Tests
# e1 = FullTimeEmployee("Alice", 5000)
# e2 = PartTimeEmployee("Bob", 20, 80)
# e3 = FreelanceEmployee("Charlie", 500, 6)
# e1.describe()   # Employee: Alice | Salary: $5000
# e2.describe()   # Employee: Bob | Salary: $1600
# e3.describe()   # Employee: Charlie | Salary: $3000


# -------------------------------------------------------
# Exercise 6: Magic Methods + Full OOP Design
# Build a `ShoppingCart` system:
#
# Class `Item`:
#   - Attributes: name, price, quantity
#   - __str__()  → "[name] x[quantity] — $[price each]"
#   - __eq__()   → items are equal if name matches
#
# Class `ShoppingCart`:
#   - Attributes: items (list), owner
#   - add_item(item)      → adds item, increases quantity if already exists
#   - remove_item(name)   → removes item by name
#   - get_total()         → returns sum of price * quantity for all items
#   - __str__()           → lists all items and shows total
#   - __len__()           → returns number of items in cart
# -------------------------------------------------------

class Item:
    # YOUR CODE HERE
    pass

class ShoppingCart:
    # YOUR CODE HERE
    pass


# Tests
# cart = ShoppingCart("Victor")
# cart.add_item(Item("Laptop", 1200, 1))
# cart.add_item(Item("Mouse", 25, 2))
# cart.add_item(Item("Keyboard", 75, 1))
# print(cart)
# print(f"Total items: {len(cart)}")
# print(f"Total cost: ${cart.get_total()}")
# cart.remove_item("Mouse")
# print(cart)


# ============================================================
# BONUS: OOP for Web — Build a mini User Authentication system
#
# Class `User`:
#   - Attributes: username, email, __password (private, store hashed)
#   - Methods:
#       register()           → validates and stores user
#       check_password(pwd)  → returns True/False
#       __str__()            → "User: [username] | [email]"
#
# Class `AuthSystem`:
#   - Stores list of registered users
#   - Methods:
#       register(username, email, password)
#       login(username, password) → returns "Login successful" or error
#       find_user(username)       → returns user object or None
#
# Tip: use Python's hashlib to hash passwords
# import hashlib
# hashed = hashlib.sha256(password.encode()).hexdigest()
# ============================================================

import hashlib

class User:
    # YOUR CODE HERE
    pass

class AuthSystem:
    # YOUR CODE HERE
    pass


# Tests
# auth = AuthSystem()
# auth.register("victor", "victor@mail.com", "secure123")
# auth.register("alice", "alice@mail.com", "password456")
# print(auth.login("victor", "secure123"))   # Login successful
# print(auth.login("victor", "wrongpass"))   # Invalid password
# print(auth.login("ghost", "pass"))         # User not found
