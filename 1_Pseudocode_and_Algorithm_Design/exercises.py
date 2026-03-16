# ============================================================
# Module 1: Pseudocode & Algorithm Design — Exercises
#
# For each exercise:
#   Step 1 — Write the pseudocode in the comments
#   Step 2 — Implement it in Python below
# ============================================================


# -------------------------------------------------------
# Exercise 1: Sequence
# Calculate the total cost of an order.
# Get item price and quantity, apply 10% tax, print total.
#
# PSEUDOCODE:
# START
#   ...write your pseudocode here...
# END
# -------------------------------------------------------

def calculate_total(price, quantity):
    # YOUR CODE HERE
    pass

# Test
# print(calculate_total(100, 3))  # Expected: 330.0


# -------------------------------------------------------
# Exercise 2: Selection
# Grade checker.
# Given a score (0-100), print the grade:
#   90-100 → A
#   80-89  → B
#   70-79  → C
#   60-69  → D
#   below  → F
#
# PSEUDOCODE:
# START
#   ...write your pseudocode here...
# END
# -------------------------------------------------------

def get_grade(score):
    # YOUR CODE HERE
    pass

# Test
# print(get_grade(85))  # Expected: B
# print(get_grade(42))  # Expected: F


# -------------------------------------------------------
# Exercise 3: Iteration
# FizzBuzz — a classic algorithm problem.
# Loop from 1 to 100:
#   If divisible by 3 → print "Fizz"
#   If divisible by 5 → print "Buzz"
#   If divisible by both → print "FizzBuzz"
#   Otherwise → print the number
#
# PSEUDOCODE:
# START
#   ...write your pseudocode here...
# END
# -------------------------------------------------------

def fizzbuzz():
    # YOUR CODE HERE
    pass

# Test
# fizzbuzz()


# -------------------------------------------------------
# Exercise 4: Web Problem — User Registration
# Write a function that validates a registration form.
# Rules:
#   - username must be at least 3 characters
#   - email must contain "@" and "."
#   - password must be at least 8 characters
# Return a list of any errors, or "Registration successful" if all pass.
#
# PSEUDOCODE:
# START
#   ...write your pseudocode here...
# END
# -------------------------------------------------------

def validate_registration(username, email, password):
    # YOUR CODE HERE
    pass

# Test
# print(validate_registration("Jo", "notanemail", "pass"))
# print(validate_registration("Victor", "victor@mail.com", "secure123"))


# -------------------------------------------------------
# Exercise 5: OOP Problem — Design a Queue
# A queue is FIFO (First In, First Out) — like a line at a shop.
# Build a Queue class with:
#   - enqueue(item)  → adds item to the back
#   - dequeue()      → removes and returns item from the front
#   - peek()         → returns front item without removing
#   - is_empty()     → returns True if queue is empty
#   - size()         → returns number of items
#
# PSEUDOCODE:
# CLASS Queue
#   ...write your pseudocode here...
# END CLASS
# -------------------------------------------------------

class Queue:
    # YOUR CODE HERE
    pass

# Test
# q = Queue()
# q.enqueue("Alice")
# q.enqueue("Bob")
# q.enqueue("Charlie")
# print(q.peek())    # Expected: Alice
# print(q.dequeue()) # Expected: Alice
# print(q.size())    # Expected: 2


# -------------------------------------------------------
# Exercise 6: Algorithm Efficiency
# Given a list of numbers, find the largest number.
# Write TWO versions:
#   Version A — using a loop (O(n))
#   Version B — using Python's built-in max() (O(n))
# Then write a third version that finds the TOP 3 largest numbers.
#
# PSEUDOCODE:
# START
#   ...write your pseudocode here...
# END
# -------------------------------------------------------

def find_largest(numbers):
    # YOUR CODE HERE — Version A (loop)
    pass

def find_top_three(numbers):
    # YOUR CODE HERE — Top 3
    pass

# Test
# print(find_largest([3, 1, 9, 4, 7]))    # Expected: 9
# print(find_top_three([3, 1, 9, 4, 7]))  # Expected: [9, 7, 4]


# ============================================================
# BONUS: Search Algorithm
# Implement Binary Search.
# Given a SORTED list and a target value,
# return the index of the target or -1 if not found.
#
# PSEUDOCODE:
# START
#   ...write your pseudocode here...
# END
# ============================================================

def binary_search(sorted_list, target):
    # YOUR CODE HERE
    pass

# Test
# print(binary_search([1, 3, 5, 7, 9, 11], 7))   # Expected: 3
# print(binary_search([1, 3, 5, 7, 9, 11], 4))   # Expected: -1
