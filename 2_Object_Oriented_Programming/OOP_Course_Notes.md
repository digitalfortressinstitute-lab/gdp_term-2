# Module 2: Object-Oriented Programming (OOP)
### Languages: Python & Java

---

## What You'll Learn
- Classes & Objects
- Encapsulation
- Inheritance
- Polymorphism
- Abstraction
- Magic / Dunder Methods
- OOP in the context of Web & API Design

---

## Lesson 1: Classes & Objects

A **class** is a blueprint. An **object** is an instance of that blueprint.
Think of a class as a cookie cutter and objects as the cookies.

```python
class Car:
    def __init__(self, brand, speed):
        self.brand = brand
        self.speed = speed

    def describe(self):
        print(f"{self.brand} goes {self.speed}km/h")

my_car = Car("Toyota", 180)
my_car.describe()
# Output: Toyota goes 180km/h
```

### Key Terms
- `__init__` — constructor, runs when object is created
- `self` — refers to the current instance of the class
- **Attributes** — variables that belong to an object
- **Methods** — functions that belong to a class

---

## Lesson 2: Encapsulation

Encapsulation means **hiding internal data** from the outside world.
Use private attributes (`__`) so data can only be accessed through methods.

```python
class BankAccount:
    def __init__(self, owner, balance):
        self.owner = owner
        self.__balance = balance  # private — cannot be accessed directly

    def deposit(self, amount):
        if amount > 0:
            self.__balance += amount
            print(f"Deposited {amount}. New balance: {self.__balance}")

    def withdraw(self, amount):
        if amount > self.__balance:
            print("Insufficient funds")
        else:
            self.__balance -= amount
            print(f"Withdrew {amount}. New balance: {self.__balance}")

    def get_balance(self):
        return self.__balance


account = BankAccount("Victor", 1000)
account.deposit(500)
account.withdraw(200)
print(account.get_balance())  # 1300
# print(account.__balance)    # ERROR — private!
```

### Why Encapsulation Matters in Web Dev
- Protects sensitive data (passwords, balances)
- Controls how data is modified (validation inside setters)
- Used in Django/Flask models to manage database fields

---

## Lesson 3: Inheritance

Inheritance lets a **child class** reuse and extend a **parent class**.
Avoid repeating code — define shared behaviour once.

```python
class Animal:
    def __init__(self, name):
        self.name = name

    def speak(self):
        print(f"{self.name} makes a sound")

    def describe(self):
        print(f"I am {self.name}")


class Dog(Animal):
    def speak(self):
        print(f"{self.name} says: Woof!")


class Cat(Animal):
    def speak(self):
        print(f"{self.name} says: Meow!")


class Parrot(Animal):
    def __init__(self, name, phrase):
        super().__init__(name)  # call parent constructor
        self.phrase = phrase

    def speak(self):
        print(f"{self.name} says: {self.phrase}")


dog = Dog("Rex")
cat = Cat("Whiskers")
parrot = Parrot("Polly", "Pretty bird!")

dog.speak()    # Rex says: Woof!
cat.speak()    # Whiskers says: Meow!
parrot.speak() # Polly says: Pretty bird!
dog.describe() # I am Rex — inherited from Animal
```

### `super()`
Used to call a method from the parent class.
Essential when the child class has its own `__init__` but still needs the parent's setup.

---

## Lesson 4: Polymorphism

Same method name, **different behaviour** depending on the object.
Allows you to write flexible, reusable code.

```python
animals = [Dog("Rex"), Cat("Whiskers"), Parrot("Polly", "Hello!")]

for animal in animals:
    animal.speak()  # each one behaves differently
```

### Polymorphism in Web Dev
```python
class JSONResponse:
    def send(self):
        print("Sending JSON response")

class HTMLResponse:
    def send(self):
        print("Sending HTML response")

class XMLResponse:
    def send(self):
        print("Sending XML response")

responses = [JSONResponse(), HTMLResponse(), XMLResponse()]
for response in responses:
    response.send()  # same call, different output
```

---

## Lesson 5: Abstraction

Abstraction means **hiding complexity** — show only what's necessary.
Use Abstract Base Classes (ABC) to enforce that subclasses implement certain methods.

```python
from abc import ABC, abstractmethod

class Shape(ABC):
    @abstractmethod
    def area(self):
        pass

    @abstractmethod
    def perimeter(self):
        pass

    def describe(self):
        print(f"Area: {self.area()}, Perimeter: {self.perimeter()}")


class Circle(Shape):
    def __init__(self, radius):
        self.radius = radius

    def area(self):
        return round(3.14159 * self.radius ** 2, 2)

    def perimeter(self):
        return round(2 * 3.14159 * self.radius, 2)


class Rectangle(Shape):
    def __init__(self, width, height):
        self.width = width
        self.height = height

    def area(self):
        return self.width * self.height

    def perimeter(self):
        return 2 * (self.width + self.height)


c = Circle(5)
r = Rectangle(4, 6)

c.describe()  # Area: 78.54, Perimeter: 31.42
r.describe()  # Area: 24, Perimeter: 20
```

---

## Lesson 6: Magic / Dunder Methods

Special methods surrounded by double underscores (`__`).
They let you define how objects behave with built-in operations.

```python
class Product:
    def __init__(self, name, price):
        self.name = name
        self.price = price

    def __str__(self):
        # called when you print the object
        return f"Product: {self.name} — ${self.price}"

    def __repr__(self):
        # called in the console / debugging
        return f"Product('{self.name}', {self.price})"

    def __eq__(self, other):
        # called when using ==
        return self.price == other.price

    def __lt__(self, other):
        # called when using <
        return self.price < other.price


p1 = Product("Laptop", 1200)
p2 = Product("Phone", 800)

print(p1)          # Product: Laptop — $1200
print(p1 == p2)    # False
print(p2 < p1)     # True

products = [p1, p2]
products.sort()    # sorts using __lt__
for p in products:
    print(p)
```

---

## Lesson 7: OOP in Web & API Design

OOP is the backbone of web frameworks like Django, Flask, and Spring Boot.

### Django Model Example
```python
# models.py
from django.db import models

class User(models.Model):
    username = models.CharField(max_length=100)
    email = models.EmailField(unique=True)
    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.username
```

### REST API Resource Design
```python
class UserResource:
    def __init__(self, user_id, username, email):
        self.user_id = user_id
        self.username = username
        self.email = email

    def to_dict(self):
        return {
            "id": self.user_id,
            "username": self.username,
            "email": self.email
        }

    @classmethod
    def from_dict(cls, data):
        return cls(data["id"], data["username"], data["email"])


user = UserResource(1, "victor", "victor@mail.com")
print(user.to_dict())
```

---

## Exercises
See `exercises.py` — 6 OOP challenges covering all concepts above.

---

## Resources
- [Python OOP Docs](https://docs.python.org/3/tutorial/classes.html)
- [Real Python OOP Guide](https://realpython.com/python3-object-oriented-programming/)
- [ABC Module](https://docs.python.org/3/library/abc.html)
- [Dunder Methods](https://docs.python.org/3/reference/datamodel.html#special-method-names)
