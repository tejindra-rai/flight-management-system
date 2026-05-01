package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.dao.FlightDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.DatabaseConnectionManager;
import bcu.cmp5332.bookingsystem.dao.mysql.FlightMySQLDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
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
 * Comprehensive test class for FlightDAO and FlightMySQLDAO
 * Tests all CRUD operations and data persistence functionality
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FlightDAOTest {
    
    private static FlightDAO flightDAO;
    private static DatabaseConnectionManager dbManager;
    private static FlightBookingSystem testSystem;
    
    @BeforeAll
    static void setUpDatabase() throws SQLException {
        dbManager = DatabaseConnectionManager.getInstance();
        flightDAO = new FlightMySQLDAO();
        
        // Clean up test data before starting
        cleanUpTestData();
    }
    
    @BeforeEach
    void setUp() {
        testSystem = new FlightBookingSystem();
    }
    
    @AfterAll
    static void tearDown() throws SQLException {
        // Clean up test data after all tests
        cleanUpTestData();
        dbManager.closeConnection();
    }
    
    /**
     * Helper method to clean up test data from database
     */
    private static void cleanUpTestData() throws SQLException {
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Delete test flights (IDs 9000+)
            stmt.executeUpdate("DELETE FROM bookings WHERE flight_id >= 9000");
            stmt.executeUpdate("DELETE FROM flights WHERE id >= 9000");
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
    @DisplayName("Add Flight Tests")
    class AddFlightTests {
        
        @Test
        @Order(10)
        @DisplayName("Should add new flight successfully")
        void testAddFlight() throws SQLException, IOException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9001, "TEST001", "LHR", "JFK", departureDate);
            flight.setBasePrice(500.0);
            flight.setCapacity(200);
            flight.setFlightClass("Economy");
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "Flight should be added successfully");
        }
        
        @Test
        @Order(11)
        @DisplayName("Should add flight with all properties")
        void testAddFlightWithAllProperties() throws SQLException, IOException {
            LocalDate departureDate = LocalDate.now().plusDays(45);
            Flight flight = new Flight(9002, "TEST002", "LHR", "LAX", departureDate);
            flight.setBasePrice(750.0);
            flight.setCapacity(300);
            flight.setFlightClass("Business");
            flight.setReturnFlight(true);
            flight.setDeleted(false);
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "Flight with all properties should be added successfully");
        }
        
        @Test
        @Order(12)
        @DisplayName("Should add domestic flight")
        void testAddDomesticFlight() throws SQLException, IOException {
            LocalDate departureDate = LocalDate.now().plusDays(15);
            Flight flight = new Flight(9003, "DOM001", "LHR", "MAN", departureDate);
            flight.setBasePrice(150.0);
            flight.setCapacity(150);
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "Domestic flight should be added successfully");
        }
        
        @Test
        @Order(13)
        @DisplayName("Should add international flight")
        void testAddInternationalFlight() throws SQLException, IOException {
            LocalDate departureDate = LocalDate.now().plusDays(60);
            Flight flight = new Flight(9004, "INT001", "LHR", "DXB", departureDate);
            flight.setBasePrice(800.0);
            flight.setCapacity(350);
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "International flight should be added successfully");
        }
    }
    
    @Nested
    @DisplayName("Retrieve Flight Tests")
    class RetrieveFlightTests {
        
        @Test
        @Order(20)
        @DisplayName("Should retrieve flight by ID")
        void testGetFlightById() throws SQLException, IOException, FlightBookingSystemException {
            // First add a flight
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight originalFlight = new Flight(9005, "TEST005", "LHR", "CDG", departureDate);
            originalFlight.setBasePrice(300.0);
            originalFlight.setCapacity(180);
            flightDAO.addFlight(originalFlight);
            
            // Retrieve it
            Flight retrievedFlight = flightDAO.getFlightById(9005);
            
            assertNotNull(retrievedFlight, "Retrieved flight should not be null");
            assertEquals(9005, retrievedFlight.getId());
            assertEquals("TEST005", retrievedFlight.getFlightNumber());
            assertEquals("LHR", retrievedFlight.getOrigin());
            assertEquals("CDG", retrievedFlight.getDestination());
            assertEquals(departureDate, retrievedFlight.getDepartureDate());
            assertEquals(300.0, retrievedFlight.getBasePrice(), 0.01);
            assertEquals(180, retrievedFlight.getCapacity());
        }
        
        @Test
        @Order(21)
        @DisplayName("Should return null for non-existent flight ID")
        void testGetNonExistentFlight() throws SQLException, IOException, FlightBookingSystemException {
            Flight flight = flightDAO.getFlightById(99999);
            assertNull(flight, "Non-existent flight should return null");
        }
        
        @Test
        @Order(22)
        @DisplayName("Should retrieve all flights")
        void testGetAllFlights() throws SQLException, IOException, FlightBookingSystemException {
            // Add multiple flights
            LocalDate date1 = LocalDate.now().plusDays(30);
            LocalDate date2 = LocalDate.now().plusDays(45);
            
            Flight flight1 = new Flight(9006, "TEST006", "LHR", "JFK", date1);
            flight1.setBasePrice(500.0);
            flight1.setCapacity(200);
            flightDAO.addFlight(flight1);
            
            Flight flight2 = new Flight(9007, "TEST007", "MAN", "DXB", date2);
            flight2.setBasePrice(600.0);
            flight2.setCapacity(250);
            flightDAO.addFlight(flight2);
            
            // Retrieve all flights
            List<Flight> flights = flightDAO.getAllFlights();
            
            assertNotNull(flights, "Flight list should not be null");
            assertFalse(flights.isEmpty(), "Flight list should not be empty");
            assertTrue(flights.size() >= 2, "Should have at least 2 flights");
        }
        
        @Test
        @Order(23)
        @DisplayName("Should not retrieve deleted flights in getAllFlights")
        void testGetAllFlightsExcludesDeleted() throws SQLException, IOException, FlightBookingSystemException {
            // Add and then delete a flight
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9008, "TEST008", "LHR", "BOS", departureDate);
            flight.setBasePrice(700.0);
            flight.setCapacity(220);
            flightDAO.addFlight(flight);
            
            // Delete it
            flightDAO.deleteFlight(9008);
            
            // Get all flights
            List<Flight> flights = flightDAO.getAllFlights();
            
            // Should not contain the deleted flight
            boolean containsDeleted = flights.stream()
                .anyMatch(f -> f.getId() == 9008);
            
            assertFalse(containsDeleted, "Deleted flight should not appear in getAllFlights");
        }
    }
    
    @Nested
    @DisplayName("Update Flight Tests")
    class UpdateFlightTests {
        
        @Test
        @Order(30)
        @DisplayName("Should update flight properties")
        void testUpdateFlight() throws SQLException, IOException, FlightBookingSystemException {
            // Add initial flight
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9009, "TEST009", "LHR", "JFK", departureDate);
            flight.setBasePrice(500.0);
            flight.setCapacity(200);
            flightDAO.addFlight(flight);
            
            // Update the flight
            flight.setBasePrice(600.0);
            flight.setCapacity(250);
            flight.setFlightClass("Business");
            
            boolean result = flightDAO.updateFlight(flight);
            
            assertTrue(result, "Flight should be updated successfully");
            
            // Verify update
            Flight updatedFlight = flightDAO.getFlightById(9009);
            assertEquals(600.0, updatedFlight.getBasePrice(), 0.01);
            assertEquals(250, updatedFlight.getCapacity());
            assertEquals("Business", updatedFlight.getFlightClass());
        }
        
        @Test
        @Order(31)
        @DisplayName("Should update flight to return flight")
        void testUpdateFlightToReturnFlight() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9010, "TEST010", "LHR", "CDG", departureDate);
            flight.setBasePrice(300.0);
            flight.setCapacity(180);
            flight.setReturnFlight(false);
            flightDAO.addFlight(flight);
            
            // Update to return flight
            flight.setReturnFlight(true);
            boolean result = flightDAO.updateFlight(flight);
            
            assertTrue(result, "Flight should be updated to return flight");
            
            Flight updatedFlight = flightDAO.getFlightById(9010);
            assertTrue(updatedFlight.isReturnFlight(), "Flight should be marked as return flight");
        }
        
        @Test
        @Order(32)
        @DisplayName("Should update flight class")
        void testUpdateFlightClass() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9011, "TEST011", "LHR", "FRA", departureDate);
            flight.setBasePrice(400.0);
            flight.setCapacity(200);
            flight.setFlightClass("Economy");
            flightDAO.addFlight(flight);
            
            // Update flight class
            flight.setFlightClass("First Class");
            flight.setBasePrice(1500.0);
            boolean result = flightDAO.updateFlight(flight);
            
            assertTrue(result, "Flight class should be updated");
            
            Flight updatedFlight = flightDAO.getFlightById(9011);
            assertEquals("First Class", updatedFlight.getFlightClass());
            assertEquals(1500.0, updatedFlight.getBasePrice(), 0.01);
        }
    }
    
    @Nested
    @DisplayName("Delete Flight Tests")
    class DeleteFlightTests {
        
        @Test
        @Order(40)
        @DisplayName("Should soft delete flight")
        void testDeleteFlight() throws SQLException, IOException, FlightBookingSystemException {
            // Add flight
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9012, "TEST012", "LHR", "AMS", departureDate);
            flight.setBasePrice(250.0);
            flight.setCapacity(150);
            flightDAO.addFlight(flight);
            
            // Delete flight
            boolean result = flightDAO.deleteFlight(9012);
            
            assertTrue(result, "Flight should be deleted successfully");
            
            // Verify it's not in regular queries
            Flight deletedFlight = flightDAO.getFlightById(9012);
            assertNull(deletedFlight, "Deleted flight should not be retrievable by ID");
        }
        
        @Test
        @Order(41)
        @DisplayName("Should return false when deleting non-existent flight")
        void testDeleteNonExistentFlight() throws SQLException, IOException {
            boolean result = flightDAO.deleteFlight(99999);
            assertFalse(result, "Deleting non-existent flight should return false");
        }
        
        @Test
        @Order(42)
        @DisplayName("Should handle deleting already deleted flight")
        void testDeleteAlreadyDeletedFlight() throws SQLException, IOException {
            // Add and delete a flight
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9013, "TEST013", "LHR", "DUB", departureDate);
            flight.setBasePrice(200.0);
            flight.setCapacity(120);
            flightDAO.addFlight(flight);
            
            flightDAO.deleteFlight(9013);
            
            // Try to delete again
            boolean result = flightDAO.deleteFlight(9013);
            
            // Should still return true (update happens, even though already deleted)
            assertTrue(result, "Deleting already deleted flight should succeed");
        }
    }
    
    @Nested
    @DisplayName("Load and Store Data Tests")
    class LoadStoreDataTests {
        
        @Test
        @Order(50)
        @DisplayName("Should load data into FlightBookingSystem")
        void testLoadData() throws SQLException, IOException, FlightBookingSystemException {
            // Add some test flights
            LocalDate date1 = LocalDate.now().plusDays(30);
            LocalDate date2 = LocalDate.now().plusDays(45);
            
            Flight flight1 = new Flight(9014, "LOAD001", "LHR", "JFK", date1);
            flight1.setBasePrice(500.0);
            flight1.setCapacity(200);
            flightDAO.addFlight(flight1);
            
            Flight flight2 = new Flight(9015, "LOAD002", "MAN", "CDG", date2);
            flight2.setBasePrice(300.0);
            flight2.setCapacity(150);
            flightDAO.addFlight(flight2);
            
            // Load data into system
            FlightBookingSystem fbs = new FlightBookingSystem();
            flightDAO.loadData(fbs);
            
            // Verify loaded data
            List<Flight> flights = fbs.getFlights();
            assertNotNull(flights, "Loaded flights should not be null");
            assertFalse(flights.isEmpty(), "Should have loaded flights");
            assertTrue(flights.size() >= 2, "Should have loaded at least 2 flights");
        }
        
        @Test
        @Order(51)
        @DisplayName("Should load flights including deleted ones")
        void testLoadDataIncludesDeleted() throws SQLException, IOException, FlightBookingSystemException {
            // Add a flight and delete it
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9016, "DEL001", "LHR", "BER", departureDate);
            flight.setBasePrice(400.0);
            flight.setCapacity(180);
            flightDAO.addFlight(flight);
            flightDAO.deleteFlight(9016);
            
            // Load data
            FlightBookingSystem fbs = new FlightBookingSystem();
            flightDAO.loadData(fbs);
            
            // Check if deleted flight is loaded
            try {
                Flight loadedFlight = fbs.getFlightByID(9016);
                assertNotNull(loadedFlight, "Deleted flight should be loaded");
                assertTrue(loadedFlight.isDeleted(), "Flight should be marked as deleted");
            } catch (FlightBookingSystemException e) {
                fail("Deleted flight should be loaded during data load");
            }
        }
        
        @Test
        @Order(52)
        @DisplayName("Should store data from FlightBookingSystem")
        void testStoreData() throws SQLException, IOException, FlightBookingSystemException {
            // Create system with flights
            FlightBookingSystem fbs = new FlightBookingSystem();
            
            LocalDate date1 = LocalDate.now().plusDays(30);
            LocalDate date2 = LocalDate.now().plusDays(45);
            
            Flight flight1 = new Flight(9017, "STORE001", "LHR", "ZRH", date1);
            flight1.setBasePrice(350.0);
            flight1.setCapacity(160);
            fbs.addFlight(flight1);
            
            Flight flight2 = new Flight(9018, "STORE002", "MAN", "VIE", date2);
            flight2.setBasePrice(400.0);
            flight2.setCapacity(170);
            fbs.addFlight(flight2);
            
            // Store data
            flightDAO.storeData(fbs);
            
            // Verify stored data
            Flight stored1 = flightDAO.getFlightById(9017);
            Flight stored2 = flightDAO.getFlightById(9018);
            
            assertNotNull(stored1, "First flight should be stored");
            assertNotNull(stored2, "Second flight should be stored");
            assertEquals("STORE001", stored1.getFlightNumber());
            assertEquals("STORE002", stored2.getFlightNumber());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Validation Tests")
    class EdgeCaseTests {
        
        @Test
        @Order(60)
        @DisplayName("Should handle flight with minimum valid data")
        void testMinimumValidFlight() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(1);
            Flight flight = new Flight(9019, "MIN", "LHR", "JFK", departureDate);
            flight.setBasePrice(1.0);
            flight.setCapacity(1);
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "Minimum valid flight should be added");
            
            Flight retrieved = flightDAO.getFlightById(9019);
            assertNotNull(retrieved);
            assertEquals(1.0, retrieved.getBasePrice(), 0.01);
            assertEquals(1, retrieved.getCapacity());
        }
        
        @Test
        @Order(61)
        @DisplayName("Should handle flight with maximum capacity")
        void testMaximumCapacity() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9020, "MAX", "LHR", "SYD", departureDate);
            flight.setBasePrice(2000.0);
            flight.setCapacity(853); // A380 max capacity
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "Flight with maximum capacity should be added");
            
            Flight retrieved = flightDAO.getFlightById(9020);
            assertEquals(853, retrieved.getCapacity());
        }
        
        @Test
        @Order(62)
        @DisplayName("Should handle very high price")
        void testVeryHighPrice() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight flight = new Flight(9021, "EXPENSIVE", "LHR", "SYD", departureDate);
            flight.setBasePrice(50000.0);
            flight.setCapacity(12);
            flight.setFlightClass("First Class");
            
            boolean result = flightDAO.addFlight(flight);
            
            assertTrue(result, "Flight with very high price should be added");
            
            Flight retrieved = flightDAO.getFlightById(9021);
            assertEquals(50000.0, retrieved.getBasePrice(), 0.01);
        }
        
        @Test
        @Order(63)
        @DisplayName("Should preserve all flight properties through save and load")
        void testDataIntegrity() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            Flight originalFlight = new Flight(9022, "INTEGRITY", "LHR", "HKG", departureDate);
            originalFlight.setBasePrice(1200.0);
            originalFlight.setCapacity(280);
            originalFlight.setFlightClass("Business");
            originalFlight.setReturnFlight(true);
            originalFlight.setDeleted(false);
            
            // Add to database
            flightDAO.addFlight(originalFlight);
            
            // Retrieve from database
            Flight retrievedFlight = flightDAO.getFlightById(9022);
            
            // Verify all properties
            assertEquals(originalFlight.getId(), retrievedFlight.getId());
            assertEquals(originalFlight.getFlightNumber(), retrievedFlight.getFlightNumber());
            assertEquals(originalFlight.getOrigin(), retrievedFlight.getOrigin());
            assertEquals(originalFlight.getDestination(), retrievedFlight.getDestination());
            assertEquals(originalFlight.getDepartureDate(), retrievedFlight.getDepartureDate());
            assertEquals(originalFlight.getBasePrice(), retrievedFlight.getBasePrice(), 0.01);
            assertEquals(originalFlight.getCapacity(), retrievedFlight.getCapacity());
            assertEquals(originalFlight.getFlightClass(), retrievedFlight.getFlightClass());
            assertEquals(originalFlight.isReturnFlight(), retrievedFlight.isReturnFlight());
            assertEquals(originalFlight.isDeleted(), retrievedFlight.isDeleted());
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @Order(70)
        @DisplayName("Should handle complete CRUD lifecycle")
        void testCompleteCRUDLifecycle() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            
            // CREATE
            Flight flight = new Flight(9023, "CRUD", "LHR", "LAX", departureDate);
            flight.setBasePrice(800.0);
            flight.setCapacity(300);
            boolean added = flightDAO.addFlight(flight);
            assertTrue(added, "Flight should be created");
            
            // READ
            Flight retrieved = flightDAO.getFlightById(9023);
            assertNotNull(retrieved, "Flight should be readable");
            assertEquals("CRUD", retrieved.getFlightNumber());
            
            // UPDATE
            retrieved.setBasePrice(900.0);
            retrieved.setCapacity(320);
            boolean updated = flightDAO.updateFlight(retrieved);
            assertTrue(updated, "Flight should be updated");
            
            Flight afterUpdate = flightDAO.getFlightById(9023);
            assertEquals(900.0, afterUpdate.getBasePrice(), 0.01);
            assertEquals(320, afterUpdate.getCapacity());
            
            // DELETE
            boolean deleted = flightDAO.deleteFlight(9023);
            assertTrue(deleted, "Flight should be deleted");
            
            Flight afterDelete = flightDAO.getFlightById(9023);
            assertNull(afterDelete, "Deleted flight should not be retrievable");
        }
        
        @Test
        @Order(71)
        @DisplayName("Should handle multiple flights from different origins")
        void testMultipleOriginsAndDestinations() throws SQLException, IOException, FlightBookingSystemException {
            LocalDate departureDate = LocalDate.now().plusDays(30);
            
            Flight[] flights = {
                new Flight(9024, "MULTI1", "LHR", "JFK", departureDate),
                new Flight(9025, "MULTI2", "MAN", "DXB", departureDate),
                new Flight(9026, "MULTI3", "EDI", "AMS", departureDate),
                new Flight(9027, "MULTI4", "BHX", "CDG", departureDate)
            };
            
            for (Flight flight : flights) {
                flight.setBasePrice(500.0);
                flight.setCapacity(200);
                flightDAO.addFlight(flight);
            }
            
            List<Flight> allFlights = flightDAO.getAllFlights();
            assertTrue(allFlights.size() >= 4, "Should have at least 4 flights");
        }
    }
}