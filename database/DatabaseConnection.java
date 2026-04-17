package database;

// ============================================================
// Member 2: Database & Transactions
// File: database/DatabaseConnection.java
// Purpose: Manages JDBC connection to MySQL database
// OOP Concept: Encapsulation (private fields, static factory)
// ============================================================

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // --- Encapsulated connection config ---
    private static final String URL      = "jdbc:mysql://localhost:3306/banking_system";
    private static final String USER     = "root";
    private static final String PASSWORD = "admin123"; // Change to your MySQL password
    private static Connection connection = null;

    // Private constructor prevents instantiation (utility class)
    private DatabaseConnection() {}

    /**
     * Returns a singleton JDBC connection.
     * Uses lazy initialization – connection created only when needed.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL driver
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established successfully.");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-j to classpath.", e);
            }
        }
        return connection;
    }

    /** Closes the connection gracefully. Call on app exit. */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
