package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.dao.mysql.DatabaseConnectionManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Quick test class to verify MySQL database connection.
 * Run this before attempting full data migration.
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class QuickMySQLTest {
    
    public static void main(String[] args) {
        System.out.println("===============================================");
        System.out.println("  MySQL Connection Test                       ");
        System.out.println("===============================================");
        System.out.println();
        
        try {
            // Test 1: Get database manager instance
            System.out.println("[Test 1] Getting DatabaseConnectionManager instance...");
            DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
            System.out.println("✓ Instance created successfully");
            System.out.println();
            
            // Test 2: Establish connection
            System.out.println("[Test 2] Establishing database connection...");
            Connection conn = dbManager.getConnection();
            
            if (conn == null) {
                System.err.println("✗ Connection is null!");
                return;
            }
            
            if (conn.isClosed()) {
                System.err.println("✗ Connection is closed!");
                return;
            }
            
            System.out.println("✓ Connection established successfully");
            System.out.println();
            
            // Test 3: Get connection metadata
            System.out.println("[Test 3] Retrieving connection information...");
            System.out.println("  Database Product: " + conn.getMetaData().getDatabaseProductName());
            System.out.println("  Database Version: " + conn.getMetaData().getDatabaseProductVersion());
            System.out.println("  Driver Name: " + conn.getMetaData().getDriverName());
            System.out.println("  Driver Version: " + conn.getMetaData().getDriverVersion());
            System.out.println("  Connection URL: " + conn.getMetaData().getURL());
            System.out.println("  Database Name: " + conn.getCatalog());
            System.out.println("  Username: " + conn.getMetaData().getUserName());
            System.out.println();
            
            // Test 4: Execute simple query
            System.out.println("[Test 4] Executing test query (SELECT 1)...");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 as test_value");
            
            if (rs.next()) {
                int value = rs.getInt("test_value");
                System.out.println("✓ Query executed successfully. Result: " + value);
            }
            rs.close();
            stmt.close();
            System.out.println();
            
            // Test 5: Check if database exists and tables are present
            System.out.println("[Test 5] Checking database schema...");
            stmt = conn.createStatement();
            
            // Check if we're using the right database
            rs = stmt.executeQuery("SELECT DATABASE() as current_db");
            if (rs.next()) {
                String currentDb = rs.getString("current_db");
                System.out.println("  Current database: " + currentDb);
                
                if (!"flight_booking_system".equals(currentDb)) {
                    System.err.println("  ⚠ WARNING: Not using 'flight_booking_system' database!");
                }
            }
            rs.close();
            
            // Check for required tables
            System.out.println("  Checking for required tables...");
            String[] requiredTables = {"flights", "customers", "bookings", "users"};
            
            for (String tableName : requiredTables) {
                rs = stmt.executeQuery(
                    "SELECT COUNT(*) as count FROM information_schema.tables " +
                    "WHERE table_schema = 'flight_booking_system' " +
                    "AND table_name = '" + tableName + "'"
                );
                
                if (rs.next() && rs.getInt("count") > 0) {
                    System.out.println("    ✓ Table '" + tableName + "' exists");
                } else {
                    System.err.println("    ✗ Table '" + tableName + "' NOT FOUND!");
                }
                rs.close();
            }
            
            stmt.close();
            System.out.println();
            
            // Test 6: Check table data counts
            System.out.println("[Test 6] Counting records in tables...");
            stmt = conn.createStatement();
            
            for (String tableName : requiredTables) {
                try {
                    rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + tableName);
                    if (rs.next()) {
                        int count = rs.getInt("count");
                        System.out.println("  " + tableName + ": " + count + " records");
                    }
                    rs.close();
                } catch (Exception e) {
                    System.err.println("  ✗ Error reading " + tableName + ": " + e.getMessage());
                }
            }
            
            stmt.close();
            System.out.println();
            
            // Close connection
            System.out.println("[Test 7] Closing connection...");
            dbManager.closeConnection();
            System.out.println("✓ Connection closed successfully");
            System.out.println();
            
            // Final summary
            System.out.println("===============================================");
            System.out.println("  ✓ ALL TESTS PASSED                          ");
            System.out.println("===============================================");
            System.out.println();
            System.out.println("Your MySQL connection is working correctly!");
            System.out.println("You can now run DataMigrationTool.java");
            
        } catch (Exception e) {
            System.err.println();
            System.err.println("===============================================");
            System.err.println("  ✗ TEST FAILED                               ");
            System.err.println("===============================================");
            System.err.println();
            System.err.println("Error Type: " + e.getClass().getSimpleName());
            System.err.println("Error Message: " + e.getMessage());
            System.err.println();
            System.err.println("Full Stack Trace:");
            e.printStackTrace();
            System.err.println();
            System.err.println("Common Fixes:");
            System.err.println("1. Check if MySQL server is running");
            System.err.println("2. Verify db.properties has correct credentials");
            System.err.println("3. Ensure mysql-connector-j JAR is in classpath");
            System.err.println("4. Run database/schema.sql to create tables");
            System.err.println("5. Check MySQL user permissions");
        }
    }
}