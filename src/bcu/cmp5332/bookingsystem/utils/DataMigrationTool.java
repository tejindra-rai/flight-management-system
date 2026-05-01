package bcu.cmp5332.bookingsystem.utils;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.dao.mysql.DatabaseConnectionManager;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Utility class to migrate data from old storage (files) to MySQL database
 * using the modern DAO-based system.
 *
 * @author Tejindra Rai
 * @version 3.0 - Updated to use FlightBookingSystemData DAO methods
 */
public class DataMigrationTool {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Flight Booking System - Data Migration Tool ");
        System.out.println(" Version 3.0 - Using DAO-based persistence ");
        System.out.println("=================================================");
        System.out.println();

        try {
            // Step 1: Test database connection
            System.out.println("[1/5] Testing database connection...");
            if (!testDatabaseConnection()) {
                System.err.println("✗ Failed to connect to database!");
                System.err.println("Check db.properties and MySQL server.");
                return;
            }
            System.out.println("✓ Database connection successful!");
            System.out.println();

            // Step 2: Load existing data (from files or old system)
            System.out.println("[2/5] Loading existing data...");
            FlightBookingSystem fbs = loadExistingData();
            printSummary(fbs);
            System.out.println();

            // Step 3: Confirmation
            System.out.println("[3/5] Ready to migrate to MySQL.");
            System.out.println("This will OVERWRITE existing data in the database.");
            System.out.print("Type YES to continue, or anything else to cancel: ");
            String confirm = new java.util.Scanner(System.in).nextLine().trim();
            if (!confirm.equalsIgnoreCase("YES")) {
                System.out.println("Migration cancelled.");
                return;
            }
            System.out.println();

            // Step 4: Perform migration
            System.out.println("[4/5] Migrating data to MySQL...");
            FlightBookingSystemData.safeStore(fbs);
            System.out.println("✓ Migration completed!");
            System.out.println();

            // Step 5: Verify
            System.out.println("[5/5] Verifying migrated data...");
            FlightBookingSystem reloaded = FlightBookingSystemData.load();

            boolean verified = true;

            if (reloaded.getFlights().size() != fbs.getFlights().size()) {
                System.err.println("✗ Flight count mismatch!");
                verified = false;
            }
            if (reloaded.getCustomers().size() != fbs.getCustomers().size()) {
                System.err.println("✗ Customer count mismatch!");
                verified = false;
            }
            if (reloaded.getUsers().size() != fbs.getUsers().size()) {
                System.err.println("✗ User count mismatch!");
                verified = false;
            }

            int origBookings = countBookings(fbs);
            int newBookings = countBookings(reloaded);
            if (newBookings != origBookings) {
                System.err.println("✗ Booking count mismatch!");
                verified = false;
            }

            if (verified) {
                System.out.println("=================================================");
                System.out.println(" ✓ VERIFICATION SUCCESSFUL – MIGRATION COMPLETE ");
                System.out.println("=================================================");
                System.out.println();
                System.out.println("Data has been successfully migrated to MySQL.");
                System.out.println("You can now run the main application normally.");
            } else {
                System.err.println("=================================================");
                System.err.println(" ✗ VERIFICATION FAILED – Check logs ");
                System.err.println("=================================================");
            }

        } catch (Exception e) {
            System.err.println("=================================================");
            System.err.println(" ✗ MIGRATION FAILED ");
            System.err.println("=================================================");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean testDatabaseConnection() {
        try {
            DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
            return dbManager.testConnection();
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
            return false;
        }
    }

    private static FlightBookingSystem loadExistingData() 
            throws FlightBookingSystemException, IOException, SQLException {
        // Use the same loading mechanism as the main app
        System.out.println("Loading data via DAO system...");
        return FlightBookingSystemData.load();
    }

    private static void printSummary(FlightBookingSystem fbs) {
        System.out.println("Data Summary:");
        System.out.println("  Flights:    " + fbs.getFlights().size());
        System.out.println("  Customers:  " + fbs.getCustomers().size());
        System.out.println("  Users:      " + fbs.getUsers().size());

        int totalBookings = 0;
        for (Customer c : fbs.getCustomers()) {
            totalBookings += c.getBookings().size();
        }
        System.out.println("  Bookings:   " + totalBookings);
    }

    private static int countBookings(FlightBookingSystem fbs) {
        int count = 0;
        for (Customer c : fbs.getCustomers()) {
            count += c.getBookings().size();
        }
        return count;
    }
}