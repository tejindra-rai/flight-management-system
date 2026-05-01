package bcu.cmp5332.bookingsystem.dao.mysql;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database connection manager for MySQL.
 * Handles database connection pooling and configuration.
 * 
 * @author Tejindra Rai
 * @version 1.1 - Fixed property key mismatch
 */
public class DatabaseConnectionManager {
    
    private static DatabaseConnectionManager instance;
    private Connection connection;
    
    // Default database configuration
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/flight_booking_system";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "";
    
    // Configuration file path
    private static final String CONFIG_FILE = "./resources/db.properties";
    
    /**
     * Private constructor for singleton pattern.
     */
    private DatabaseConnectionManager() {
        // Load JDBC driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found. Please add the driver to classpath.");
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the singleton instance of DatabaseConnectionManager.
     * 
     * @return the singleton instance
     */
    public static synchronized DatabaseConnectionManager getInstance() {
        if (instance == null) {
            instance = new DatabaseConnectionManager();
        }
        return instance;
    }
    
    /**
     * Gets a database connection. Creates a new connection if one doesn't exist.
     * 
     * @return a database Connection object
     * @throws SQLException if unable to establish connection
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            Properties props = loadDatabaseProperties();
            String url = props.getProperty("db.url", DEFAULT_DB_URL);
            
            // FIXED: Changed from "db.user" to "db.username" to match db.properties
            String user = props.getProperty("db.username", DEFAULT_DB_USER);
            String password = props.getProperty("db.password", DEFAULT_DB_PASSWORD);
            
            // Build connection URL with additional properties from config
            String useSSL = props.getProperty("db.useSSL", "false");
            String serverTimezone = props.getProperty("db.serverTimezone", "UTC");
            String allowPublicKeyRetrieval = props.getProperty("db.allowPublicKeyRetrieval", "true");
            
            // Construct full URL with connection parameters
            String fullUrl = url + "?useSSL=" + useSSL 
                           + "&serverTimezone=" + serverTimezone
                           + "&allowPublicKeyRetrieval=" + allowPublicKeyRetrieval;
            
            System.out.println("Connecting to database: " + url);
            connection = DriverManager.getConnection(fullUrl, user, password);
            System.out.println("Database connection established successfully.");
        }
        return connection;
    }
    
    /**
     * Loads database configuration from properties file.
     * Falls back to default values if file doesn't exist.
     * 
     * @return Properties object with database configuration
     */
    private Properties loadDatabaseProperties() {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            props.load(fis);
            System.out.println("Loaded database configuration from: " + CONFIG_FILE);
        } catch (IOException e) {
            System.out.println("Could not load db.properties file. Using default configuration.");
            System.out.println("Error: " + e.getMessage());
            props.setProperty("db.url", DEFAULT_DB_URL);
            // FIXED: Changed from "db.user" to "db.username"
            props.setProperty("db.username", DEFAULT_DB_USER);
            props.setProperty("db.password", DEFAULT_DB_PASSWORD);
        }
        return props;
    }
    
    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Tests the database connection.
     * 
     * @return true if connection is successful, false otherwise
     */
    public boolean testConnection() {
        try {
            Connection conn = getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            
            if (isValid) {
                // Additional verification - execute a simple query
                conn.createStatement().executeQuery("SELECT 1");
                System.out.println("Database connection test: PASSED");
            }
            
            return isValid;
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("SQL State: " + e.getSQLState());
            e.printStackTrace();
            return false;
        }
    }
}