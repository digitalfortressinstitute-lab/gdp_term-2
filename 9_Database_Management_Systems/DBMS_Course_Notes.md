# Module 9: Database Management Systems (DBMS)

This module covers the fundamentals of both Relational (SQL) and Non-Relational (NoSQL) databases, focusing on terminal-based interactions.

---

## Part 1: Relational Databases (MySQL)

Relational databases use Structured Query Language (SQL) and organize data into tables with predefined schemas.

### 0. Installation

#### Linux (Ubuntu/Debian)
To install MySQL Server and the client:
```bash
sudo apt update
sudo apt install mysql-server
```
To secure the installation:
```bash
sudo mysql_secure_installation
```
To check if the service is running:
```bash
sudo systemctl status mysql
```

#### Windows
1.  Download the **MySQL Installer** from the [official MySQL website](https://dev.mysql.com/downloads/installer/).
2.  Run the installer and choose "Developer Default" or "Server only".
3.  Follow the setup wizard, set a **Root Password**, and keep the default port (3306).
4.  Once installed, search for "MySQL Command Line Client" in the Start menu or add the MySQL `bin` folder to your **System Environment Variables (PATH)** to use it in any terminal.

### 1. Terminal Basics
To log into MySQL from the terminal:
```bash
mysql -u root -p
```

### 2. Database Operations
- **Show all databases:** `SHOW DATABASES;`
- **Create a database:** `CREATE DATABASE shop_db;`
- **Use a database:** `USE shop_db;`
- **Delete a database:** `DROP DATABASE shop_db;`

### 3. Table Operations (CRUD)

#### CREATE
```sql
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### READ
- **Show all tables:** `SHOW TABLES;`
- **Describe table structure:** `DESCRIBE users;`
- **Select all data:** `SELECT * FROM users;`
- **Select with condition:** `SELECT * FROM users WHERE username = 'victor';`

#### UPDATE
```sql
UPDATE users SET email = 'newemail@example.com' WHERE id = 1;
```

#### DELETE
```sql
DELETE FROM users WHERE id = 1;
```

### 4. Advanced Queries (Joins)
```sql
SELECT orders.id, users.username, orders.amount
FROM orders
INNER JOIN users ON orders.user_id = users.id;
```

### 5. Keys and Constraints
- **Primary Key:** uniquely identifies a row.
- **Foreign Key:** links to a primary key in another table.
- **Unique:** prevents duplicate values.
- **Not Null:** requires a value.
- **Default:** sets a default value.

```sql
CREATE TABLE orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 6. Indexes
Indexes speed up reads but can slow down writes.
```sql
CREATE INDEX idx_users_email ON users(email);
SHOW INDEX FROM users;
```

### 7. Aggregation and Grouping
```sql
SELECT status, COUNT(*) AS total_orders
FROM orders
GROUP BY status
HAVING total_orders > 1;
```

### 8. Sorting and Pagination
```sql
SELECT * FROM users
ORDER BY created_at DESC
LIMIT 10 OFFSET 0;
```

### 9. Transactions and ACID
- **ACID:** Atomicity, Consistency, Isolation, Durability.
```sql
START TRANSACTION;
UPDATE users SET email = 'updated@example.com' WHERE id = 1;
UPDATE orders SET status = 'paid' WHERE user_id = 1;
COMMIT;
```

### 10. Normalization (Basics)
- **1NF:** No repeating groups, atomic values.
- **2NF:** 1NF + no partial dependency on a composite key.
- **3NF:** 2NF + no transitive dependencies.

### 11. ERD (Entity-Relationship Diagram) Basics
- **Entity:** a thing (table).
- **Attribute:** a property (column).
- **Relationship:** connection between entities.
- **Cardinality:** one-to-one, one-to-many, many-to-many.

Example entities:
- **User** (`id`, `username`, `email`)
- **Order** (`id`, `user_id`, `amount`)

Relationship:
- **User 1..N Order** (one user can have many orders).

### 11. Views
```sql
CREATE VIEW active_users AS
SELECT id, username, email FROM users WHERE created_at >= '2024-01-01';
SELECT * FROM active_users;
```

### 12. Triggers
```sql
CREATE TABLE audit_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(50),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

DELIMITER //
CREATE TRIGGER after_user_insert
AFTER INSERT ON users
FOR EACH ROW
BEGIN
    INSERT INTO audit_log (action) VALUES ('user created');
END//
DELIMITER ;
```

### 13. Stored Procedures (Basics)
```sql
DELIMITER //
CREATE PROCEDURE GetUserOrders(IN userId INT)
BEGIN
    SELECT * FROM orders WHERE user_id = userId;
END//
DELIMITER ;

CALL GetUserOrders(1);
```

### 14. Users and Privileges
```sql
CREATE USER 'app_user'@'localhost' IDENTIFIED BY 'strong_password';
GRANT SELECT, INSERT, UPDATE ON shop_db.* TO 'app_user'@'localhost';
FLUSH PRIVILEGES;
```

### 15. Backup and Restore (Basics)
```bash
mysqldump -u root -p shop_db > shop_db.sql
mysql -u root -p shop_db < shop_db.sql
```

---

## Part 2: NoSQL Databases (MongoDB)

NoSQL databases are document-oriented, using JSON-like formats (BSON) and are schema-less.

### 0. Installation

#### Linux (Ubuntu/Debian)
To install MongoDB, you typically need to add their repository first:
```bash
curl -fsSL https://pgp.mongodb.com/server-7.0.asc | sudo gpg --dearmor -o /etc/apt/trusted.gpg.d/mongodb-server-7.0.gpg
echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu jammy/mongodb-org/7.0 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-7.0.list
sudo apt update
sudo apt install -y mongodb-org
```
To start the MongoDB service:
```bash
sudo systemctl start mongod
sudo systemctl enable mongod
```
To check if the service is running:
```bash
sudo systemctl status mongod
```

#### Windows
1.  Download the **MongoDB Community Server** `.msi` from the [official MongoDB website](https://www.mongodb.com/try/download/community).
2.  Run the installer, choose "Complete" installation.
3.  Ensure "Install MongoDB as a Service" is checked.
4.  Optionally install **MongoDB Compass** (graphical UI).
5.  Download the **MongoDB Shell (mongosh)** separately and add its `bin` folder to your **PATH** to use the `mongosh` command in your terminal.

### 1. Terminal Basics
To enter the MongoDB shell (mongosh):
```bash
mongosh
```

### 2. Database Operations
- **Show all databases:** `show dbs`
- **Switch/Create database:** `use shop_db`
- **Check current database:** `db`

### 3. Collection Operations (CRUD)

#### CREATE (Insert)
```javascript
db.users.insertOne({
    username: "victor",
    email: "victor@example.com",
    tags: ["developer", "linux"]
});

db.users.insertMany([
    { username: "alice", age: 25 },
    { username: "bob", age: 30 }
]);
```

#### READ (Find)
- **Find all:** `db.users.find()`
- **Find with filter:** `db.users.find({ username: "victor" })`
- **Find with operators:** `db.users.find({ age: { $gt: 20 } })` (Age > 20)

#### UPDATE
```javascript
db.users.updateOne(
    { username: "victor" },
    { $set: { email: "victor_new@example.com" } }
);
```

#### DELETE
```javascript
db.users.deleteOne({ username: "alice" });
```

### 4. Indexes
```javascript
db.users.createIndex({ email: 1 }, { unique: true });
db.users.getIndexes();
```

### 5. Aggregation Pipeline
```javascript
db.orders.aggregate([
  { $match: { status: "paid" } },
  { $group: { _id: "$user_id", total: { $sum: "$amount" } } },
  { $sort: { total: -1 } }
]);
```

### 6. Schema Design Tips
- Embed data when it is frequently accessed together.
- Reference data when it grows large or is shared widely.
- Avoid unbounded arrays in a single document.

### 7. Transactions (Replica Sets)
```javascript
const session = db.getMongo().startSession();
session.startTransaction();
try {
  const usersCol = session.getDatabase("shop_db").users;
  const ordersCol = session.getDatabase("shop_db").orders;
  usersCol.updateOne({ _id: 1 }, { $set: { email: "updated@example.com" } });
  ordersCol.updateOne({ _id: 10 }, { $set: { status: "paid" } });
  session.commitTransaction();
} catch (e) {
  session.abortTransaction();
}
session.endSession();
```

### 8. Backup and Restore (Basics)
```bash
mongodump --db shop_db --out ./backup
mongorestore --db shop_db ./backup/shop_db
```

---

## SQL vs NoSQL: When to use what?

| Feature | SQL (MySQL) | NoSQL (MongoDB) |
|---------|-------------|-----------------|
| Structure | Rigid, Predefined Schema | Flexible, Dynamic Schema |
| Scaling | Vertical (Bigger Server) | Horizontal (More Servers) |
| Relationships | Complex Joins | Denormalization/Embedding |
| Best For | Complex queries, ACID compliance | Rapid development, Big Data, Real-time apps |

---

## Quick Revision Cheatsheet
- **SQL Join Types:** INNER, LEFT, RIGHT, FULL.
- **Common Clauses:** `WHERE`, `GROUP BY`, `HAVING`, `ORDER BY`, `LIMIT`.
- **NoSQL Operators:** `$gt`, `$lt`, `$in`, `$and`, `$or`, `$set`, `$inc`.
