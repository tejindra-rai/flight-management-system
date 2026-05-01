package bcu.cmp5332.bookingsystem.data;

import bcu.cmp5332.bookingsystem.dao.BookingDAO;
import bcu.cmp5332.bookingsystem.dao.CustomerDAO;
import bcu.cmp5332.bookingsystem.dao.DataAccessObject;
import bcu.cmp5332.bookingsystem.dao.FlightDAO;
import bcu.cmp5332.bookingsystem.dao.UserDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.BookingMySQLDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.CustomerMySQLDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.FeedbackMySQLDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.FlightMySQLDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.UserMySQLDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Central class responsible for loading and storing all system data.
 * Uses DAO pattern to abstract database operations.
 * 
 * @author Tejindra Rai
 * @version 5.0 - Added Feedback support
 */
public class FlightBookingSystemData {

    // DAO instances (MySQL implementations)
    private static final FlightDAO flightDAO       = new FlightMySQLDAO();
    private static final CustomerDAO customerDAO   = new CustomerMySQLDAO();
    private static final UserDAO userDAO           = new UserMySQLDAO();
    private static final BookingDAO bookingDAO     = new BookingMySQLDAO();
    private static final DataAccessObject feedbackDAO = new FeedbackMySQLDAO();

    /**
     * Loads all data from the database using DAOs.
     * Dependencies must be loaded in the correct order.
     */
    public static FlightBookingSystem load() 
            throws FlightBookingSystemException, IOException, SQLException {
        
        FlightBookingSystem fbs = new FlightBookingSystem();

        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Loading Data from MySQL Database    ║");
        System.out.println("╚════════════════════════════════════════╝");

        // 1. Load flights
        System.out.println("📦 Loading flights...");
        flightDAO.loadData(fbs);

        // 2. Load customers
        System.out.println("📦 Loading customers...");
        customerDAO.loadData(fbs);

        // 3. Load users
        System.out.println("📦 Loading users...");
        userDAO.loadData(fbs);

        // 4. Load bookings (requires flights and customers already loaded)
        System.out.println("📦 Loading bookings...");
        bookingDAO.loadData(fbs);

        // 5. Load feedback
        System.out.println("📦 Loading feedback...");
        feedbackDAO.loadData(fbs);

        System.out.println("✅ Data loading complete!");
        System.out.println("   Flights: " + fbs.getFlights().size());
        System.out.println("   Customers: " + fbs.getCustomers().size());
        System.out.println("   Users: " + fbs.getUsers().size());
        System.out.println("   Feedback: " + fbs.getAllFeedback().size());
        
        return fbs;
    }

    /**
     * Stores all data to the database using DAOs.
     */
    public static void store(FlightBookingSystem fbs) 
            throws IOException, SQLException {
        
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   Storing Data to MySQL Database      ║");
        System.out.println("╚════════════════════════════════════════╝");

        System.out.println("💾 Storing flights...");
        flightDAO.storeData(fbs);

        System.out.println("💾 Storing customers...");
        customerDAO.storeData(fbs);

        System.out.println("💾 Storing users...");
        userDAO.storeData(fbs);

        System.out.println("💾 Storing bookings...");
        bookingDAO.storeData(fbs);

        System.out.println("💾 Storing feedback...");
        feedbackDAO.storeData(fbs);

        System.out.println("✅ Data storage complete!");
    }

    /**
     * Safe wrapper for load() with fallback to empty system.
     */
    public static FlightBookingSystem safeLoad() {
        try {
            return load();
        } catch (Exception ex) {
            System.err.println("╔════════════════════════════════════════╗");
            System.err.println("║  ⚠️  WARNING: Failed to load data!    ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("Reason: " + ex.getMessage());
            ex.printStackTrace();
            System.err.println("Starting with a new empty system.");
            return new FlightBookingSystem();
        }
    }

    /**
     * Safe wrapper for store() with error handling.
     */
    public static void safeStore(FlightBookingSystem fbs) {
        try {
            store(fbs);
        } catch (Exception ex) {
            System.err.println("╔════════════════════════════════════════╗");
            System.err.println("║  ⚠️  WARNING: Failed to store data!   ║");
            System.err.println("╚════════════════════════════════════════╝");
            System.err.println("Reason: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}