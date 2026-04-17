-- ============================================================
-- Banking Management System - Database Schema
-- Member 2: Database & Transactions
-- ============================================================

CREATE DATABASE IF NOT EXISTS banking_system;
USE banking_system;

-- Users table: stores login credentials
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(20) PRIMARY KEY,
    password VARCHAR(50) NOT NULL,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(15),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Accounts table: linked to users
CREATE TABLE IF NOT EXISTS accounts (
    account_no VARCHAR(20) PRIMARY KEY,
    user_id VARCHAR(20) NOT NULL,
    account_type ENUM('SAVINGS', 'CURRENT', 'FIXED_DEPOSIT') NOT NULL,
    balance DOUBLE DEFAULT 0.0,
    maturity_date DATE,                  -- Only for Fixed Deposit
    interest_rate DOUBLE DEFAULT 0.0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- ATM table: stores ATM PINs per account
CREATE TABLE IF NOT EXISTS atm (
    account_no VARCHAR(20) PRIMARY KEY,
    pin VARCHAR(4) NOT NULL,
    FOREIGN KEY (account_no) REFERENCES accounts(account_no)
);

-- Transactions table: all money movements
CREATE TABLE IF NOT EXISTS transactions (
    txn_id INT AUTO_INCREMENT PRIMARY KEY,
    sender_account VARCHAR(20),
    receiver_account VARCHAR(20),
    amount DOUBLE NOT NULL,
    txn_type ENUM('DEPOSIT','WITHDRAWAL','TRANSFER_DOMESTIC','TRANSFER_INTERNATIONAL','FD_OPEN') NOT NULL,
    description VARCHAR(200),
    txn_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- Sample seed data for testing
-- ============================================================
INSERT IGNORE INTO users (id, password, name, email, phone) VALUES
    ('admin', 'admin123', 'Admin User', 'admin@bank.com', '9999999999'),
    ('U001', 'pass123', 'Arjun Sharma', 'arjun@email.com', '9876543210'),
    ('U002', 'pass456', 'Priya Mehta', 'priya@email.com', '9123456789');

INSERT IGNORE INTO accounts (account_no, user_id, account_type, balance, interest_rate) VALUES
    ('ACC1001', 'U001', 'SAVINGS', 50000.00, 4.0),
    ('ACC1002', 'U002', 'CURRENT', 100000.00, 0.0);

INSERT IGNORE INTO atm (account_no, pin) VALUES
    ('ACC1001', '1234'),
    ('ACC1002', '5678');
