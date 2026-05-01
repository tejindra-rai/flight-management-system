package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.dao.mysql.BookingMySQLDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.CustomerMySQLDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.DatabaseConnectionManager;
import bcu.cmp5332.bookingsystem.dao.mysql.FlightMySQLDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import org.junit.jupiter.api.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for BookingMySQLDAO
 * Tests all CRUD operations, data persistence, and booking-specific functionality
 * 
 * This version works with the ORIGINAL BookingDAO interface (no FlightBookingSystem parameter)
 * 
 * @author Tejindra Rai
 * @version 1.0 - Compatible with original DAO interface
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BookingMySQLDAOTest {
    
    private static BookingMySQLDAO bookingDAO;
    private static FlightMySQLDAO flightDAO;
    private static CustomerMySQLDAO customerDAO;
    private static DatabaseConnectionManager dbManager;
    private static FlightBookingSystem testSystem;
    
    // Test data IDs (using high numbers to avoid conflicts)
    private static final int TEST_CUSTOMER_ID_1 = 9001;
    private static final int TEST_CUSTOMER_ID_2 = 9002;
    private static final int TEST_FLIGHT_ID_1 = 9001;
    private static final int TEST_FLIGHT_ID_2 = 9002;
    
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dbManager = DatabaseConnectionManager.getInstance();
        bookingDAO = new BookingMySQLDAO();
        flightDAO = new FlightMySQLDAO();
        customerDAO = new CustomerMySQLDAO();
        
        // Clean up any existing test data
        cleanUpTestData();
        
        // Create test customers and flights
        createTestData();
    }
    
    @BeforeEach
    void setUp() throws SQLException {
        testSystem = new FlightBookingSystem();
        // Clean up bookings before each test to prevent duplicates
        cleanUpTestBookings();
    }
    
    @AfterEach
    void tearDownEach() throws SQLException {
        // Clean up bookings after each test to ensure isolation
        cleanUpTestBookings();
    }
    
    @AfterAll
    static void tearDown() throws SQLException {
        // Clean up test data
        cleanUpTestData();
        dbManager.closeConnection();
    }
    
    /**
     * Helper method to clean up only bookings (called between tests)
     */
    private static void cleanUpTestBookings() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            // Delete test bookings only
            stmt.executeUpdate("DELETE FROM bookings WHERE customer_id >= 9000");
        }
    }
    
    /**
     * Helper method to clean up test data from database
     */
    private static void cleanUpTestData() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Delete test bookings
            stmt.executeUpdate("DELETE FROM bookings WHERE customer_id >= 9000");
            
            // Delete test customers
            stmt.executeUpdate("DELETE FROM customers WHERE id >= 9000");
            
            // Delete test flights
            stmt.executeUpdate("DELETE FROM flights WHERE id >= 9000");
        }
    }
    
    /**
     * Helper method to create test data
     */
    private static void createTestData() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create test customers
            stmt.executeUpdate(String.format(
                "INSERT INTO customers (id, name, phone, email, deleted, has_children, age_group, meal_preference) " +
                "VALUES (%d, 'Test Customer 1', '1234567890', 'test1@example.com', false, false, 'Adult', 'None')",
                TEST_CUSTOMER_ID_1
            ));
            
            stmt.executeUpdate(String.format(
                "INSERT INTO customers (id, name, phone, email, deleted, has_children, age_group, meal_preference) " +
                "VALUES (%d, 'Test Customer 2', '0987654321', 'test2@example.com', false, true, 'Adult', 'Vegetarian')",
                TEST_CUSTOMER_ID_2
            ));
            
            // Create test flights
            LocalDate departureDate = LocalDate.now().plusDays(30);
            stmt.executeUpdate(String.format(
                "INSERT INTO flights (id, flight_number, origin, destination, departure_date, capacity, base_price, deleted, flight_class, is_return_flight) " +
                "VALUES (%d, 'TEST001', 'LHR', 'JFK', '%s', 200, 500.00, false, 'Economy', false)",
                TEST_FLIGHT_ID_1, departureDate
            ));
            
            stmt.executeUpdate(String.format(
                "INSERT INTO flights (id, flight_number, origin, destination, departure_date, capacity, base_price, deleted, flight_class, is_return_flight) " +
                "VALUES (%d, 'TEST002', 'MAN', 'DXB', '%s', 250, 600.00, false, 'Business', false)",
                TEST_FLIGHT_ID_2, departureDate
            ));
        }
    }
    
    @Nested
    @DisplayName("Database Connection Tests")
    class DatabaseConnectionTests {
        
        @Test
        @Order(1)
        @DisplayName("Should establish database connection successfully")
        void testDatabaseConnection() {
            assertTrue(dbManager.testConnection(), "Database connection should be successful");
        }
        
        @Test
        @Order(2)
        @DisplayName("Should get valid connection object")
        void testGetConnection() throws SQLException {
            Connection conn = dbManager.getConnection();
            assertNotNull(conn, "Connection should not be null");
            assertFalse(conn.isClosed(), "Connection should be open");
        }
    }
    
    @Nested
    @DisplayName("Add Booking Tests")
    class AddBookingTests {
        
        @Test
        @Order(10)
        @DisplayName("Should add new booking successfully")
        void testAddBooking() throws SQLException, IOException, FlightBookingSystemException {
            // Load test data
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            booking.setCancelled(false);
            
            boolean result = bookingDAO.addBooking(booking);
            
            assertTrue(result, "Booking should be added successfully");
        }
        
        @Test
        @Order(11)
        @DisplayName("Should add booking with all properties")
        void testAddBookingWithAllProperties() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(600.00);
            booking.setCancellationFee(0.0);
            booking.setMealPreference("Vegetarian");
            booking.setSeatNumber("12A");
            booking.setCancelled(false);
            
            boolean result = bookingDAO.addBooking(booking);
            
            assertTrue(result, "Booking with all properties should be added successfully");
        }
        
        @Test
        @Order(12)
        @DisplayName("Should prevent duplicate bookings")
        void testPreventDuplicateBooking() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking1 = new Booking(customer, flight, bookingDate);
            booking1.setBookingPrice(500.00);
            bookingDAO.addBooking(booking1);
            
            // Try to add duplicate
            Booking booking2 = new Booking(customer, flight, bookingDate);
            booking2.setBookingPrice(500.00);
            
            assertThrows(SQLException.class, () -> bookingDAO.addBooking(booking2),
                "Should throw SQLException for duplicate booking");
        }
    }
    
    @Nested
    @DisplayName("Update Booking Tests")
    class UpdateBookingTests {
        
        @Test
        @Order(20)
        @DisplayName("Should update booking price")
        void testUpdateBookingPrice() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking first
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            bookingDAO.addBooking(booking);
            
            // Update price
            booking.setBookingPrice(550.00);
            boolean updated = bookingDAO.updateBooking(booking);
            
            assertTrue(updated, "Booking price should be updated");
            
            // Verify update by loading bookings from database
            List<Booking> bookings = bookingDAO.getBookingsByCustomerId(TEST_CUSTOMER_ID_1);
            assertFalse(bookings.isEmpty(), "Should find updated booking");
            assertEquals(550.00, bookings.get(0).getBookingPrice(), 0.01, "Price should match updated value");
        }
        
        @Test
        @Order(21)
        @DisplayName("Should update booking seat number")
        void testUpdateSeatNumber() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            booking.setSeatNumber("10A");
            bookingDAO.addBooking(booking);
            
            // Update seat
            booking.setSeatNumber("12B");
            boolean updated = bookingDAO.updateBooking(booking);
            
            assertTrue(updated, "Seat number should be updated");
        }
        
        @Test
        @Order(22)
        @DisplayName("Should update booking meal preference")
        void testUpdateMealPreference() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(600.00);
            booking.setMealPreference("None");
            bookingDAO.addBooking(booking);
            
            // Update meal preference
            booking.setMealPreference("Vegan");
            boolean updated = bookingDAO.updateBooking(booking);
            
            assertTrue(updated, "Meal preference should be updated");
        }
        
        @Test
        @Order(23)
        @DisplayName("Should update multiple booking properties")
        void testUpdateMultipleProperties() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(600.00);
            booking.setSeatNumber("10A");
            booking.setMealPreference("None");
            bookingDAO.addBooking(booking);
            
            // Update multiple properties
            booking.setBookingPrice(650.00);
            booking.setSeatNumber("15C");
            booking.setMealPreference("Vegetarian");
            boolean updated = bookingDAO.updateBooking(booking);
            
            assertTrue(updated, "Multiple properties should be updated");
        }
    }
    
    @Nested
    @DisplayName("Cancel Booking Tests")
    class CancelBookingTests {
        
        @Test
        @Order(30)
        @DisplayName("Should cancel booking successfully")
        void testCancelBooking() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            bookingDAO.addBooking(booking);
            
            // Cancel booking
            boolean cancelled = bookingDAO.cancelBooking(TEST_CUSTOMER_ID_1, TEST_FLIGHT_ID_1);
            
            assertTrue(cancelled, "Booking should be cancelled");
        }
        
        @Test
        @Order(31)
        @DisplayName("Should return false when cancelling non-existent booking")
        void testCancelNonExistentBooking() throws SQLException, IOException {
            boolean cancelled = bookingDAO.cancelBooking(99999, 99999);
            assertFalse(cancelled, "Should return false for non-existent booking");
        }
        
        @Test
        @Order(32)
        @DisplayName("Should handle cancelling already cancelled booking")
        void testCancelAlreadyCancelledBooking() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(600.00);
            bookingDAO.addBooking(booking);
            
            // Cancel once
            bookingDAO.cancelBooking(TEST_CUSTOMER_ID_2, TEST_FLIGHT_ID_2);
            
            // Try to cancel again
            boolean cancelled = bookingDAO.cancelBooking(TEST_CUSTOMER_ID_2, TEST_FLIGHT_ID_2);
            
            assertFalse(cancelled, "Should return false when cancelling already cancelled booking");
        }
    }
    
    @Nested
    @DisplayName("Retrieve Bookings Tests")
    class RetrieveBookingsTests {
        
        @Test
        @Order(40)
        @DisplayName("Should retrieve bookings by customer ID")
        void testGetBookingsByCustomerId() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            bookingDAO.addBooking(booking);
            
            // Retrieve bookings - NOTE: No testSystem parameter!
            List<Booking> bookings = bookingDAO.getBookingsByCustomerId(TEST_CUSTOMER_ID_1);
            
            assertFalse(bookings.isEmpty(), "Should find bookings for customer");
            assertEquals(1, bookings.size(), "Should find exactly one booking");
        }
        
        @Test
        @Order(41)
        @DisplayName("Should retrieve bookings by flight ID")
        void testGetBookingsByFlightId() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            // Add booking
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            bookingDAO.addBooking(booking);
            
            // Retrieve bookings - NOTE: No testSystem parameter!
            List<Booking> bookings = bookingDAO.getBookingsByFlightId(TEST_FLIGHT_ID_1);
            
            assertFalse(bookings.isEmpty(), "Should find bookings for flight");
            assertEquals(1, bookings.size(), "Should find exactly one booking");
        }
        
        @Test
        @Order(42)
        @DisplayName("Should retrieve all bookings")
        void testGetAllBookings() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            // Add multiple bookings
            Customer customer1 = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Customer customer2 = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight1 = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            Flight flight2 = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking1 = new Booking(customer1, flight1, bookingDate);
            booking1.setBookingPrice(500.00);
            bookingDAO.addBooking(booking1);
            
            Booking booking2 = new Booking(customer2, flight2, bookingDate);
            booking2.setBookingPrice(600.00);
            bookingDAO.addBooking(booking2);
            
            // Retrieve all bookings
            List<Booking> allBookings = bookingDAO.getAllBookings();
            
            assertNotNull(allBookings, "Bookings list should not be null");
            assertTrue(allBookings.size() >= 2, "Should have at least 2 bookings");
        }
    }
    
    @Nested
    @DisplayName("Load and Store Data Tests")
    class LoadStoreDataTests {
        
        @Test
        @Order(50)
        @DisplayName("Should load booking data into FlightBookingSystem")
        void testLoadData() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            // Add a booking
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            bookingDAO.addBooking(booking);
            
            // Create new system and load data
            FlightBookingSystem newSystem = new FlightBookingSystem();
            customerDAO.loadData(newSystem);
            flightDAO.loadData(newSystem);
            bookingDAO.loadData(newSystem);
            
            // Verify loaded
            assertNotNull(newSystem, "Loaded system should not be null");
            Customer loadedCustomer = newSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            assertFalse(loadedCustomer.getBookings().isEmpty(), "Customer should have bookings loaded");
        }
        
        @Test
        @Order(51)
        @DisplayName("Should store booking data from FlightBookingSystem")
        void testStoreData() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            // Create and add booking to system
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            customer.addBooking(booking);
            flight.addPassenger(customer);
            
            // Store data
            bookingDAO.storeData(testSystem);
            
            // Verify in database
            List<Booking> bookings = bookingDAO.getBookingsByCustomerId(TEST_CUSTOMER_ID_1);
            assertFalse(bookings.isEmpty(), "Booking should be stored in database");
        }
        
        @Test
        @Order(52)
        @DisplayName("Should preserve booking properties during load")
        void testBookingPropertiesPreserved() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            // Create booking with specific properties
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking originalBooking = new Booking(customer, flight, bookingDate);
            originalBooking.setBookingPrice(525.50);
            originalBooking.setMealPreference("Vegan");
            originalBooking.setSeatNumber("20D");
            bookingDAO.addBooking(originalBooking);
            
            // Load into new system
            FlightBookingSystem newSystem = new FlightBookingSystem();
            customerDAO.loadData(newSystem);
            flightDAO.loadData(newSystem);
            bookingDAO.loadData(newSystem);
            
            // Retrieve and verify
            Customer loadedCustomer = newSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            List<Booking> loadedBookings = loadedCustomer.getBookings();
            
            assertFalse(loadedBookings.isEmpty(), "Should load booking");
            
            if (!loadedBookings.isEmpty()) {
                Booking loadedBooking = loadedBookings.get(0);
                assertEquals(525.50, loadedBooking.getBookingPrice(), 0.01, 
                    "Booking price should be preserved");
                assertEquals("Vegan", loadedBooking.getMealPreference(), 
                    "Meal preference should be preserved");
                assertEquals("20D", loadedBooking.getSeatNumber(), 
                    "Seat number should be preserved");
            }
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Validation Tests")
    class EdgeCaseTests {
        
        @Test
        @Order(60)
        @DisplayName("Should handle booking with zero price")
        void testZeroPrice() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(0.0);
            
            boolean result = bookingDAO.addBooking(booking);
            assertTrue(result, "Should handle zero price booking");
        }
        
        @Test
        @Order(61)
        @DisplayName("Should handle booking with very high price")
        void testVeryHighPrice() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(99999.99);
            
            boolean result = bookingDAO.addBooking(booking);
            assertTrue(result, "Should handle very high price");
        }
        
        @Test
        @Order(62)
        @DisplayName("Should handle null meal preference")
        void testNullMealPreference() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            booking.setMealPreference(null);
            
            assertDoesNotThrow(() -> bookingDAO.addBooking(booking), 
                "Should handle null meal preference");
        }
        
        @Test
        @Order(63)
        @DisplayName("Should handle null seat number")
        void testNullSeatNumber() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            booking.setSeatNumber(null);
            
            assertDoesNotThrow(() -> bookingDAO.addBooking(booking), 
                "Should handle null seat number");
        }
        
        @Test
        @Order(64)
        @DisplayName("Should handle cancelled booking flag")
        void testCancelledFlag() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            booking.setCancelled(true);
            booking.setCancellationFee(125.00);
            
            boolean result = bookingDAO.addBooking(booking);
            assertTrue(result, "Should handle cancelled booking with fee");
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @Order(70)
        @DisplayName("Should handle complete booking lifecycle")
        void testCompleteBookingLifecycle() throws SQLException, IOException, FlightBookingSystemException {
            // Load initial data
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            // CREATE
            Booking booking = new Booking(customer, flight, bookingDate);
            booking.setBookingPrice(500.00);
            booking.setMealPreference("Vegetarian");
            booking.setSeatNumber("10A");
            boolean added = bookingDAO.addBooking(booking);
            assertTrue(added, "Booking should be created");
            
            // UPDATE
            booking.setBookingPrice(550.00);
            booking.setSeatNumber("12B");
            boolean updated = bookingDAO.updateBooking(booking);
            assertTrue(updated, "Booking should be updated");
            
            // CANCEL
            boolean cancelled = bookingDAO.cancelBooking(TEST_CUSTOMER_ID_1, TEST_FLIGHT_ID_1);
            assertTrue(cancelled, "Booking should be cancelled");
        }
        
        @Test
        @Order(71)
        @DisplayName("Should handle multiple bookings for same customer")
        void testMultipleBookingsSameCustomer() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Flight flight1 = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            Flight flight2 = testSystem.getFlightByID(TEST_FLIGHT_ID_2);
            LocalDate bookingDate = LocalDate.now();
            
            // Book two different flights
            Booking booking1 = new Booking(customer, flight1, bookingDate);
            booking1.setBookingPrice(500.00);
            boolean added1 = bookingDAO.addBooking(booking1);
            
            Booking booking2 = new Booking(customer, flight2, bookingDate);
            booking2.setBookingPrice(600.00);
            boolean added2 = bookingDAO.addBooking(booking2);
            
            assertTrue(added1, "First booking should be added");
            assertTrue(added2, "Second booking should be added");
        }
        
        @Test
        @Order(72)
        @DisplayName("Should handle multiple customers on same flight")
        void testMultipleCustomersSameFlight() throws SQLException, IOException, FlightBookingSystemException {
            customerDAO.loadData(testSystem);
            flightDAO.loadData(testSystem);
            
            Customer customer1 = testSystem.getCustomerByID(TEST_CUSTOMER_ID_1);
            Customer customer2 = testSystem.getCustomerByID(TEST_CUSTOMER_ID_2);
            Flight flight = testSystem.getFlightByID(TEST_FLIGHT_ID_1);
            LocalDate bookingDate = LocalDate.now();
            
            Booking booking1 = new Booking(customer1, flight, bookingDate);
            booking1.setBookingPrice(500.00);
            booking1.setSeatNumber("10A");
            boolean added1 = bookingDAO.addBooking(booking1);
            
            Booking booking2 = new Booking(customer2, flight, bookingDate);
            booking2.setBookingPrice(500.00);
            booking2.setSeatNumber("10B");
            boolean added2 = bookingDAO.addBooking(booking2);
            
            assertTrue(added1, "First customer booking should be added");
            assertTrue(added2, "Second customer booking should be added");
        }
    }
}