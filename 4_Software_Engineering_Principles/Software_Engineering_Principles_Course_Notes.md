# Module 4: Software Engineering Principles
### SOLID, Design Patterns & Modularization

---

## What You'll Learn
- SOLID Principles
- Design Patterns (Creational, Structural, Behavioural)
- Modularization & Code Organization
- DRY, KISS, and YAGNI Principles
- Applying these to real Web & OOP projects

---

## Lesson 1: SOLID Principles

SOLID is a set of 5 principles that make code easier to maintain, extend, and test.

---

### S — Single Responsibility Principle (SRP)
> A class should have **one and only one reason to change.**

❌ Bad — one class doing too much:
```python
class User:
    def __init__(self, name, email):
        self.name = name
        self.email = email

    def save_to_database(self):
        print(f"Saving {self.name} to DB...")  # should not be here

    def send_welcome_email(self):
        print(f"Sending email to {self.email}...")  # should not be here
```

✅ Good — each class has one job:
```python
class User:
    def __init__(self, name, email):
        self.name = name
        self.email = email

class UserRepository:
    def save(self, user):
        print(f"Saving {user.name} to DB...")

class EmailService:
    def send_welcome(self, user):
        print(f"Sending email to {user.email}...")
```

---

### O — Open/Closed Principle (OCP)
> Classes should be **open for extension, closed for modification.**
> Add new behaviour without changing existing code.

❌ Bad — adding a new shape requires modifying existing class:
```python
class AreaCalculator:
    def calculate(self, shape):
        if shape["type"] == "circle":
            return 3.14 * shape["radius"] ** 2
        elif shape["type"] == "rectangle":
            return shape["width"] * shape["height"]
        # adding triangle means editing this class — bad!
```

✅ Good — extend by adding new classes:
```python
from abc import ABC, abstractmethod

class Shape(ABC):
    @abstractmethod
    def area(self):
        pass

class Circle(Shape):
    def __init__(self, radius):
        self.radius = radius
    def area(self):
        return 3.14 * self.radius ** 2

class Rectangle(Shape):
    def __init__(self, width, height):
        self.width = width
        self.height = height
    def area(self):
        return self.width * self.height

class Triangle(Shape):  # new shape — no existing code changed
    def __init__(self, base, height):
        self.base = base
        self.height = height
    def area(self):
        return 0.5 * self.base * self.height

class AreaCalculator:
    def calculate(self, shape: Shape):
        return shape.area()
```

---

### L — Liskov Substitution Principle (LSP)
> Subclasses should be **replaceable for their parent class** without breaking the program.

❌ Bad — child breaks parent behaviour:
```python
class Bird:
    def fly(self):
        print("Flying!")

class Penguin(Bird):
    def fly(self):
        raise Exception("Penguins can't fly!")  # breaks LSP
```

✅ Good — restructure the hierarchy:
```python
class Bird:
    def move(self):
        pass

class FlyingBird(Bird):
    def move(self):
        print("Flying!")

class SwimmingBird(Bird):
    def move(self):
        print("Swimming!")

class Eagle(FlyingBird):
    pass

class Penguin(SwimmingBird):
    pass
```

---

### I — Interface Segregation Principle (ISP)
> Don't force a class to implement methods it doesn't need.
> **Many small interfaces > one large interface.**

❌ Bad — one bloated interface:
```python
class Worker(ABC):
    @abstractmethod
    def work(self): pass

    @abstractmethod
    def eat(self): pass

    @abstractmethod
    def sleep(self): pass

class Robot(Worker):
    def work(self): print("Working")
    def eat(self): pass    # robots don't eat — forced to implement
    def sleep(self): pass  # robots don't sleep — forced to implement
```

✅ Good — split into focused interfaces:
```python
class Workable(ABC):
    @abstractmethod
    def work(self): pass

class Eatable(ABC):
    @abstractmethod
    def eat(self): pass

class Human(Workable, Eatable):
    def work(self): print("Human working")
    def eat(self): print("Human eating")

class Robot(Workable):
    def work(self): print("Robot working")
```

---

### D — Dependency Inversion Principle (DIP)
> High-level modules should not depend on low-level modules.
> **Both should depend on abstractions.**

❌ Bad — tightly coupled:
```python
class MySQLDatabase:
    def save(self, data):
        print(f"Saving to MySQL: {data}")

class UserService:
    def __init__(self):
        self.db = MySQLDatabase()  # hard dependency — can't swap DB

    def create_user(self, data):
        self.db.save(data)
```

✅ Good — depend on abstraction:
```python
class Database(ABC):
    @abstractmethod
    def save(self, data): pass

class MySQLDatabase(Database):
    def save(self, data):
        print(f"Saving to MySQL: {data}")

class MongoDatabase(Database):
    def save(self, data):
        print(f"Saving to MongoDB: {data}")

class UserService:
    def __init__(self, db: Database):  # inject any DB
        self.db = db

    def create_user(self, data):
        self.db.save(data)

# Swap databases without touching UserService
service = UserService(MongoDatabase())
service.create_user({"name": "Victor"})
```

---

## Lesson 2: Design Patterns

Design patterns are **proven solutions** to common software problems.
Grouped into 3 categories: Creational, Structural, Behavioural.

---

### Creational Patterns

#### Singleton — only one instance of a class exists
```python
class Config:
    _instance = None

    def __new__(cls):
        if cls._instance is None:
            cls._instance = super().__new__(cls)
            cls._instance.settings = {}
        return cls._instance

c1 = Config()
c2 = Config()
print(c1 is c2)  # True — same instance
c1.settings["theme"] = "dark"
print(c2.settings)  # {"theme": "dark"}
```

#### Factory — create objects without specifying exact class
```python
class Notification:
    def send(self, message): pass

class EmailNotification(Notification):
    def send(self, message):
        print(f"Email: {message}")

class SMSNotification(Notification):
    def send(self, message):
        print(f"SMS: {message}")

class PushNotification(Notification):
    def send(self, message):
        print(f"Push: {message}")

class NotificationFactory:
    @staticmethod
    def create(type_):
        types = {
            "email": EmailNotification,
            "sms": SMSNotification,
            "push": PushNotification
        }
        if type_ not in types:
            raise ValueError(f"Unknown type: {type_}")
        return types[type_]()

notif = NotificationFactory.create("email")
notif.send("Welcome to DigitalFort!")
```

---

### Structural Patterns

#### Decorator — add behaviour to an object dynamically
```python
class Coffee:
    def cost(self):
        return 5

    def description(self):
        return "Coffee"

class MilkDecorator:
    def __init__(self, coffee):
        self._coffee = coffee

    def cost(self):
        return self._coffee.cost() + 2

    def description(self):
        return self._coffee.description() + " + Milk"

class SugarDecorator:
    def __init__(self, coffee):
        self._coffee = coffee

    def cost(self):
        return self._coffee.cost() + 1

    def description(self):
        return self._coffee.description() + " + Sugar"

order = SugarDecorator(MilkDecorator(Coffee()))
print(order.description())  # Coffee + Milk + Sugar
print(order.cost())         # 8
```

---

### Behavioural Patterns

#### Observer — notify multiple objects when state changes
```python
class EventEmitter:
    def __init__(self):
        self._listeners = []

    def subscribe(self, listener):
        self._listeners.append(listener)

    def emit(self, event, data=None):
        for listener in self._listeners:
            listener.update(event, data)

class Logger:
    def update(self, event, data):
        print(f"[LOG] Event: {event} | Data: {data}")

class EmailAlert:
    def update(self, event, data):
        print(f"[EMAIL] Sending alert for: {event}")

emitter = EventEmitter()
emitter.subscribe(Logger())
emitter.subscribe(EmailAlert())
emitter.emit("user_registered", {"username": "Victor"})
```

#### Strategy — swap algorithms at runtime
```python
class Sorter:
    def __init__(self, strategy):
        self.strategy = strategy

    def sort(self, data):
        return self.strategy(data)

data = [5, 2, 9, 1, 7]
asc_sorter = Sorter(sorted)
desc_sorter = Sorter(lambda x: sorted(x, reverse=True))

print(asc_sorter.sort(data))   # [1, 2, 5, 7, 9]
print(desc_sorter.sort(data))  # [9, 7, 5, 2, 1]
```

---

## Lesson 3: Modularization

Break your code into **focused, reusable modules**.
Each file/module should have a clear, single purpose.

### Recommended Project Structure (Web App)
```
my_app/
│
├── models/
│   ├── user.py         # User class & data logic
│   └── product.py      # Product class & data logic
│
├── services/
│   ├── auth_service.py     # Login, register, JWT
│   └── email_service.py    # Email sending logic
│
├── controllers/
│   ├── user_controller.py  # Handle user API routes
│   └── product_controller.py
│
├── utils/
│   ├── validators.py   # Input validation helpers
│   └── helpers.py      # General utility functions
│
└── main.py             # Entry point
```

---

## Lesson 4: DRY, KISS & YAGNI

| Principle | Meaning | Rule |
|-----------|---------|------|
| **DRY** | Don't Repeat Yourself | Extract repeated logic into functions/classes |
| **KISS** | Keep It Simple, Stupid | Write the simplest solution that works |
| **YAGNI** | You Aren't Gonna Need It | Don't build features you don't need yet |

```python
# DRY — bad: repeated logic
def get_full_name_user(user):
    return user["first"] + " " + user["last"]

def get_full_name_admin(admin):
    return admin["first"] + " " + admin["last"]

# DRY — good: one reusable function
def get_full_name(person):
    return person["first"] + " " + person["last"]
```

---

## Exercises
See `Software_Engineering_Principles_Exercises.py` — 6 challenges applying SOLID and Design Patterns.

---

## Resources
- [SOLID Principles Explained](https://realpython.com/solid-principles-python/)
- [Refactoring Guru — Design Patterns](https://refactoring.guru/design-patterns/python)
- [Python Design Patterns](https://python-patterns.guide/)
