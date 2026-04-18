# 🏦 SecureBank – Banking Management System

> A Java-based GUI Banking Management System built with AWT & Swing, JDBC, and MySQL. Designed for an OODP course project demonstrating core OOP principles.


📌 Project Overview

SecureBank simulates a real-world banking environment with:
- A secure login home page
- A full-featured banking dashboard
- A simulated ATM machine interface

All features are backed by a MySQL database via JDBC, and the UI is built entirely with Java Swing.



✨ Features

 🔐 Authentication
- Home Page Login (User ID + Password)
- Credential validation against database
- ATM PIN-based authentication (max 3 attempts)

🏦 Core Banking
- **Create Account** – Savings, Current, or Fixed Deposit
- **Deposit Money** – Adds funds, logs transaction
- **Withdraw Money** – Enforces account-type rules
- **Check Balance** – Real-time balance from DB
- **Transaction History** – Full paginated table view

### 💳 Account Types
| Type | Interest | Special Rule |

| Savings | 4% p.a. | Min balance ₹500 |
| Current | None | Overdraft up to ₹10,000 |
| Fixed Deposit | 7.5% p.a. | No withdrawal before maturity (1 year) |

## Money Transfer
- **Domestic Transfer** – Free, within SecureBank
- **International Transfer** – 2% fee applied automatically
- Sender/receiver account validation

### 🏧 ATM Features
- PIN generation and change
- Cash withdrawal with quick-select buttons (₹500, ₹1K, ₹2K, ₹5K, ₹10K, ₹20K)
- Balance inquiry
- Mini-statement (last 5 transactions)
- Account lockout after 3 failed PIN attempts

---

##  OOP Concepts Demonstrated

| Concept | Where Used |
|---------|-----------|
| **Encapsulation** | Private fields in `BankAccount`, `DatabaseConnection`; getters/setters |
| **Inheritance** | `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` extend `BankAccount` |
| **Polymorphism** | `deposit()` and `withdraw()` overridden in each subclass; factory method in `BankingService` |
| **Abstraction** | `BankAccount` is abstract; DAO pattern abstracts DB access |

---

## 📁 Project Structure

```
BankingSystem/
├── schema.sql                        ← Database setup script
├── src/
│   ├── Main.java                     ← Entry point
│   ├── database/
│   │   ├── DatabaseConnection.java   ← JDBC connection manager
│   │   └── TransactionDAO.java       ← All DB CRUD operations
│   ├── model/
│   │   ├── BankAccount.java          ← Abstract base class
│   │   ├── SavingsAccount.java       ← Savings + Current + FD classes
│   │   └── BankingService.java       ← Business logic layer
│   └── ui/
│       ├── UITheme.java              ← Shared theme/style constants
│       ├── LoginFrame.java           ← Login home page
│       ├── DashboardFrame.java       ← Main banking dashboard
│       └── ATMFrame.java             ← ATM machine interface
```

---

## 🗄️ Database Schema

```sql
-- Users: login credentials
users (id, password, name, email, phone, created_at)

-- Accounts: linked to users
accounts (account_no, user_id, account_type, balance, interest_rate, maturity_date, created_at)

-- ATM PINs
atm (account_no, pin)

-- All transactions
transactions (txn_id, sender_account, receiver_account, amount, txn_type, description, txn_date)
```

---

## 🛠️ Technologies Used

- **Java 11+** – Core language
- **Java Swing + AWT** – GUI (JFrame, JPanel, JButton, JTable, CardLayout, etc.)
- **JDBC** – Java Database Connectivity
- **MySQL** – Relational database
- **Lambda Expressions** – Used throughout for event handling and stream operations

---

## ⚙️ Setup Instructions

### Prerequisites
- Java JDK 11 or later
- MySQL 8.0+
- `mysql-connector-j-8.x.x.jar` on classpath

### Step 1: Database Setup
```bash
mysql -u root -p < schema.sql
```

### Step 2: Configure DB Password
Open `src/database/DatabaseConnection.java` and set:
```java
private static final String PASSWORD = "your_mysql_password";
```

### Step 3: Compile
```bash
# From BankingSystem/ directory
javac -cp ".:mysql-connector-j-8.0.33.jar" -d out/ src/**/*.java src/Main.java
```

### Step 4: Run
```bash
java -cp "out:mysql-connector-j-8.0.33.jar" Main
```

### Default Test Login
| User ID | Password | Name |
|---------|----------|------|
| `U001` | `pass123` | Arjun Sharma |
| `U002` | `pass456` | Priya Mehta |

---

##  Team Contributions

###  Ankit– Core Banking Logic
**Files:** `model/BankAccount.java`, `model/SavingsAccount.java`, `model/BankingService.java`
- Abstract `BankAccount` class (abstraction + encapsulation)
- Subclasses: `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` (inheritance + polymorphism)
- Business logic: deposit, withdraw, domestic/international transfer
- Factory method pattern in `BankingService`

###   Niranjan -Database & Transactions
**Files:** `database/DatabaseConnection.java`, `database/TransactionDAO.java`, `schema.sql`
- JDBC singleton connection manager
- Full CRUD: users, accounts, ATM pins, transactions
- Transaction logging
- Database schema design

###  Prakash – GUI & ATM Interface
**Files:** `ui/UITheme.java`, `ui/LoginFrame.java`, `ui/DashboardFrame.java`, `ui/ATMFrame.java`, `Main.java`
- Dark-themed, responsive GUI
- Login page with validation
- Dashboard with CardLayout navigation
- ATM interface with PIN auth, withdrawal, mini-statement

---

## 🔗 Integration Flow

```
Main.java
  └── LoginFrame  ──(valid login)──►  DashboardFrame
                                          ├── BankingService (model layer)
                                          │       └── TransactionDAO (DB layer)
                                          └── (nav) ──► ATMFrame
                                                            └── BankingService + TransactionDAO
