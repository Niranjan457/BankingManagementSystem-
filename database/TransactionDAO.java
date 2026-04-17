package database;

// ============================================================
// Member 2: Database & Transactions
// File: database/TransactionDAO.java
// Purpose: CRUD operations for Users, Accounts, ATM, Transactions
// OOP Concept: Encapsulation, Abstraction (DAO pattern)
// ============================================================

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransactionDAO {

    // ──────────────────────────────────────────────
    // USER OPERATIONS
    // ──────────────────────────────────────────────

    /** Validates user login. Returns user's name on success, null on failure. */
    public static String validateUser(String userId, String password) {
        String sql = "SELECT name FROM users WHERE id = ? AND password = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Inserts a new user into the users table. */
    public static boolean createUser(String id, String password, String name,
                                     String email, String phone) {
        String sql = "INSERT INTO users (id, password, name, email, phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.setString(4, email);
            ps.setString(5, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ──────────────────────────────────────────────
    // ACCOUNT OPERATIONS
    // ──────────────────────────────────────────────

    /** Creates a new bank account linked to a user. */
    public static boolean createAccount(String accountNo, String userId,
                                        String type, double balance,
                                        double interestRate, String maturityDate) {
        String sql = "INSERT INTO accounts (account_no, user_id, account_type, balance, interest_rate, maturity_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.setString(2, userId);
            ps.setString(3, type);
            ps.setDouble(4, balance);
            ps.setDouble(5, interestRate);
            ps.setString(6, maturityDate); // null if not FD
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Returns current balance of an account. -1 if not found. */
    public static double getBalance(String accountNo) {
        String sql = "SELECT balance FROM accounts WHERE account_no = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("balance");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /** Updates balance (used internally by deposit/withdraw). */
    public static boolean updateBalance(String accountNo, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_no = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setString(2, accountNo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Fetches account info as a formatted String[]. */
    public static String[] getAccountDetails(String accountNo) {
        String sql = "SELECT a.account_no, a.account_type, a.balance, a.interest_rate, " +
                     "a.maturity_date, u.name, u.email, u.phone " +
                     "FROM accounts a JOIN users u ON a.user_id = u.id " +
                     "WHERE a.account_no = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new String[]{
                    rs.getString("account_no"),
                    rs.getString("account_type"),
                    String.format("%.2f", rs.getDouble("balance")),
                    String.format("%.2f", rs.getDouble("interest_rate")),
                    rs.getString("maturity_date") == null ? "N/A" : rs.getString("maturity_date"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("phone")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Returns the account_type for a given account. */
    public static String getAccountType(String accountNo) {
        String sql = "SELECT account_type FROM accounts WHERE account_no = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("account_type");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Returns maturity date for FD accounts. */
    public static String getMaturityDate(String accountNo) {
        String sql = "SELECT maturity_date FROM accounts WHERE account_no = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("maturity_date");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Fetches all accounts for a given user ID. Returns list of {accountNo, type, balance}. */
    public static List<String[]> getUserAccounts(String userId) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT account_no, account_type, balance FROM accounts WHERE user_id = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    rs.getString("account_no"),
                    rs.getString("account_type"),
                    String.format("%.2f", rs.getDouble("balance"))
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ──────────────────────────────────────────────
    // TRANSACTION OPERATIONS
    // ──────────────────────────────────────────────

    /** Logs a transaction to the transactions table. */
    public static boolean logTransaction(String sender, String receiver,
                                         double amount, String type, String desc) {
        String sql = "INSERT INTO transactions (sender_account, receiver_account, amount, txn_type, description) " +
                     "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, sender);
            ps.setString(2, receiver);
            ps.setDouble(3, amount);
            ps.setString(4, type);
            ps.setString(5, desc);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Returns last N transactions for an account (mini-statement). */
    public static List<String[]> getTransactionHistory(String accountNo, int limit) {
        List<String[]> list = new ArrayList<>();
        String sql = "SELECT txn_id, sender_account, receiver_account, amount, txn_type, txn_date " +
                     "FROM transactions " +
                     "WHERE sender_account = ? OR receiver_account = ? " +
                     "ORDER BY txn_date DESC LIMIT ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.setString(2, accountNo);
            ps.setInt(3, limit);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new String[]{
                    String.valueOf(rs.getInt("txn_id")),
                    rs.getString("sender_account"),
                    rs.getString("receiver_account"),
                    String.format("%.2f", rs.getDouble("amount")),
                    rs.getString("txn_type"),
                    rs.getString("txn_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ──────────────────────────────────────────────
    // ATM OPERATIONS
    // ──────────────────────────────────────────────

    /** Saves an ATM PIN for an account. */
    public static boolean saveATMPin(String accountNo, String pin) {
        String sql = "INSERT INTO atm (account_no, pin) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE pin = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            ps.setString(3, pin);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Validates ATM PIN. Returns true if correct. */
    public static boolean validateATMPin(String accountNo, String pin) {
        String sql = "SELECT pin FROM atm WHERE account_no = ? AND pin = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Checks if an account number exists in DB. */
    public static boolean accountExists(String accountNo) {
        String sql = "SELECT account_no FROM accounts WHERE account_no = ?";
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, accountNo);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
