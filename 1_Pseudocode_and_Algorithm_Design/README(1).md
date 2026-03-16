# Module 1: Pseudocode & Algorithm Design for Web / OOP Problems

---

## What You'll Learn
- What pseudocode is and why it matters
- How to break down problems before writing code
- Algorithm design techniques
- Applying pseudocode to Web and OOP problems
- Time & Space Complexity basics (Big O)

---

## Lesson 1: What is Pseudocode?

Pseudocode is a plain-English description of what your code should do.
It has no strict syntax — the goal is to **think through logic** before touching a keyboard.

### Rules of Good Pseudocode
- Be clear and unambiguous
- Use simple action words: SET, GET, IF, LOOP, CALL, RETURN
- Indent to show structure
- Don't worry about language syntax

### Example — Login Check
```
START
  GET username from input
  GET password from input

  IF username exists in database THEN
    IF password matches stored password THEN
      RETURN "Login successful"
    ELSE
      RETURN "Wrong password"
    END IF
  ELSE
    RETURN "User not found"
  END IF
END
```

---

## Lesson 2: Algorithm Design Techniques

### 1. Sequence
Steps executed one after another.
```
START
  SET total = 0
  GET price of item
  GET quantity
  SET total = price * quantity
  PRINT total
END
```

### 2. Selection (IF / ELSE)
Make decisions based on conditions.
```
IF user is logged in THEN
  SHOW dashboard
ELSE
  REDIRECT to login page
END IF
```

### 3. Iteration (Loops)
Repeat steps until a condition is met.
```
SET count = 0
WHILE count < 10 DO
  PRINT count
  SET count = count + 1
END WHILE
```

### 4. Functions / Procedures
Reusable blocks of logic.
```
FUNCTION calculateTotal(price, quantity)
  SET total = price * quantity
  RETURN total
END FUNCTION
```

---

## Lesson 3: Pseudocode for Web Problems

### Example — Search Bar
```
START
  GET search query from user input

  IF query is empty THEN
    SHOW "Please enter a search term"
  ELSE
    CALL searchDatabase(query)
    IF results found THEN
      DISPLAY results list
    ELSE
      DISPLAY "No results found"
    END IF
  END IF
END
```

### Example — Add to Cart (E-Commerce)
```
START
  GET selected product
  GET quantity from user

  IF product is in stock THEN
    IF quantity <= stock available THEN
      ADD product to cart
      UPDATE stock count
      SHOW "Item added to cart"
    ELSE
      SHOW "Not enough stock"
    END IF
  ELSE
    SHOW "Product out of stock"
  END IF
END
```

### Example — Form Validation
```
START
  GET name, email, password from form

  IF name is empty THEN
    SHOW "Name is required"
  ELSE IF email format is invalid THEN
    SHOW "Enter a valid email"
  ELSE IF password length < 8 THEN
    SHOW "Password too short"
  ELSE
    SUBMIT form
    SHOW "Registration successful"
  END IF
END
```

---

## Lesson 4: Pseudocode for OOP Problems

### Example — Design a Class
```
CLASS BankAccount
  ATTRIBUTES:
    owner
    balance (private)

  METHOD deposit(amount)
    IF amount > 0 THEN
      SET balance = balance + amount
    END IF
  END METHOD

  METHOD withdraw(amount)
    IF amount > balance THEN
      RETURN "Insufficient funds"
    ELSE
      SET balance = balance - amount
    END IF
  END METHOD

  METHOD getBalance()
    RETURN balance
  END METHOD

END CLASS
```

### Example — Inheritance Design
```
CLASS Animal
  ATTRIBUTE: name

  METHOD speak()
    PRINT "Some sound"
  END METHOD
END CLASS

CLASS Dog EXTENDS Animal
  METHOD speak()
    PRINT "Woof!"
  END METHOD
END CLASS

CLASS Cat EXTENDS Animal
  METHOD speak()
    PRINT "Meow!"
  END METHOD
END CLASS

FOR each animal IN [Dog, Cat] DO
  CALL animal.speak()
END FOR
```

---

## Lesson 5: Big O — Time & Space Complexity Basics

Understanding how efficient your algorithm is.

| Notation | Name | Example |
|----------|------|---------|
| O(1) | Constant | Accessing an array index |
| O(n) | Linear | Looping through a list |
| O(n²) | Quadratic | Nested loops |
| O(log n) | Logarithmic | Binary search |

### Example
```
# O(n) — loops once through the list
FOR each item IN list DO
  PRINT item
END FOR

# O(n²) — nested loop
FOR each item IN list DO
  FOR each other item IN list DO
    COMPARE item with other item
  END FOR
END FOR
```

---

## Exercises
See `exercises.py` — 6 algorithm design challenges to solve in pseudocode then Python.

---

## Resources
- [Khan Academy — Algorithms](https://www.khanacademy.org/computing/computer-science/algorithms)
- [Big O Cheat Sheet](https://www.bigocheatsheet.com/)
