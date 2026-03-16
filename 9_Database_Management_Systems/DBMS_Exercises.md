# Module 9: Database Management Systems — Exercises

Perform the following tasks using the terminal for both MySQL and MongoDB.

---

## Exercise 1: MySQL (SQL) — Inventory System

1. **Connect:** Log into MySQL via terminal.
2. **Create Database:** Create a database named `ecommerce`.
3. **Use Database:** Switch to the `ecommerce` database.
4. **Create Table:** Create a table `products` with:
   - `id` (INT, Primary Key, Auto-increment)
   - `name` (VARCHAR, Not Null)
   - `price` (DECIMAL)
   - `stock` (INT)
5. **Insert Data:** Add 3 products to the table.
6. **Query:**
   - Select all products.
   - Select products with `price` greater than 50.
7. **Update:** Change the `stock` of one product.
8. **Delete:** Delete a product from the table.

---

## Exercise 2: NoSQL (MongoDB) — Blogging Platform

1. **Connect:** Open the MongoDB shell (`mongosh`).
2. **Database:** Create/Switch to a database named `blog_db`.
3. **Collection:** Create a collection named `posts`.
4. **Insert One:** Create a post document with `title`, `author`, `content`, and `likes`.
5. **Insert Many:** Insert 3 post documents at once.
6. **Query:**
   - Find all posts.
   - Find posts written by a specific author.
   - Find posts with more than 10 likes (`$gt`).
7. **Update:** Use `$set` to change the content of a post.
8. **Update Many:** Add a `category: "tech"` field to all posts using `{}` as the filter.
9. **Delete:** Delete a post by its ID or title.

---

## Exercise 3: ERD Design — Library System

1. **Identify Entities:** `Book`, `Member`, `Loan`, `Author`.
2. **List Attributes:** Add 3 to 5 attributes per entity.
3. **Define Relationships:** Member 1..N Loan, Book 1..N Loan, Book N..N Author.
4. **Specify Keys:** Choose primary keys and foreign keys.
5. **Sketch ERD:** Draw the ERD on paper or any diagram tool.

---

## Exercise 4: MySQL — Triggers and Stored Procedures

1. **Setup:** Use the `ecommerce` database.
2. **Create Table:** Create `audit_log` with `id`, `action`, and `changed_at`.
3. **Trigger:** Create a trigger to log an action when a product is inserted.
4. **Insert Test:** Insert a new product and verify `audit_log`.
5. **Stored Procedure:** Create a procedure `GetLowStock` that returns products with `stock < 5`.
6. **Call Procedure:** Run the procedure and verify results.

---

## Bonus Challenge: Comparison Analysis

1. **Schema Change:**
   - In MySQL, try to insert a product with a new column `category` without using `ALTER TABLE`. (What happens?)
   - In MongoDB, insert a new post with a completely different structure (e.g., no `content` but a `tags` array). (What happens?)
2. **Data Relationship:**
   - How would you link an `author` table to the `posts` table in MySQL? (Hint: Foreign Key)
   - How would you represent an `author` in a MongoDB post document? (Hint: Embedding vs. Referencing)
