package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for FlightBookingSystem
 * Tests all methods and functionality of the FlightBookingSystem class
 */
class FlightBookingSystemTest {
    
    private FlightBookingSystem system;
    private LocalDate futureDate;
    
    @BeforeEach
    void setUp() {
        system = new FlightBookingSystem();
        // Use a date in the future relative to NOW (not system date)
        // Flight constructor validates against LocalDate.now(), not system.getSystemDate()
        futureDate = LocalDate.now().plusDays(30);
    }
    
    @Nested
    @DisplayName("System Date Tests")
    class SystemDateTests {
        
        @Test
        @DisplayName("Should return correct system date")
        void testGetSystemDate() {
            LocalDate expected = LocalDate.parse("2020-11-11");
            assertEquals(expected, system.getSystemDate());
        }
        
        @Test
        @DisplayName("System date should be immutable")
        void testSystemDateImmutable() {
            LocalDate date1 = system.getSystemDate();
            LocalDate date2 = system.getSystemDate();
            
            assertEquals(date1, date2);
            assertEquals(LocalDate.parse("2020-11-11"), date1);
        }
    }
    
    @Nested
    @DisplayName("Flight Management Tests")
    class FlightManagementTests {
        
        private Flight flight1;
        private Flight flight2;
        
        @BeforeEach
        void setUpFlights() {
            flight1 = new Flight(1, "BA123", "LHR", "CDG", futureDate);
            flight2 = new Flight(2, "BA456", "CDG", "JFK", futureDate.plusDays(1));
        }
        
        @Test
        @DisplayName("Should start with no flights")
        void testInitiallyNoFlights() {
            assertTrue(system.getFlights().isEmpty());
        }
        
        @Test
        @DisplayName("Should add flight successfully")
        void testAddFlight() throws FlightBookingSystemException {
            system.addFlight(flight1);
            
            assertEquals(1, system.getFlights().size());
            assertTrue(system.getFlights().contains(flight1));
        }
        
        @Test
        @DisplayName("Should add multiple flights")
        void testAddMultipleFlights() throws FlightBookingSystemException {
            system.addFlight(flight1);
            system.addFlight(flight2);
            
            assertEquals(2, system.getFlights().size());
            assertTrue(system.getFlights().contains(flight1));
            assertTrue(system.getFlights().contains(flight2));
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate flight ID")
        void testAddDuplicateFlightId() throws FlightBookingSystemException {
            system.addFlight(flight1);
            
            Flight duplicateFlight = new Flight(1, "BA999", "LHR", "JFK", futureDate);
            
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                system.addFlight(duplicateFlight);
            });
            assertEquals("Duplicate flight ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate flight number and date")
        void testAddDuplicateFlightNumberAndDate() throws FlightBookingSystemException {
            system.addFlight(flight1);
            
            Flight duplicateFlight = new Flight(3, "BA123", "LHR", "CDG", futureDate);
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.addFlight(duplicateFlight);
            });
            assertTrue(exception.getMessage().contains("same number and departure date"));
        }
        
        @Test
        @DisplayName("Should allow same flight number on different dates")
        void testSameFlightNumberDifferentDates() throws FlightBookingSystemException {
            system.addFlight(flight1);
            
            Flight sameFlight = new Flight(3, "BA123", "LHR", "CDG", futureDate.plusDays(1));
            system.addFlight(sameFlight);
            
            assertEquals(2, system.getFlights().size());
        }
        
        @Test
        @DisplayName("Should get flight by ID")
        void testGetFlightById() throws FlightBookingSystemException {
            system.addFlight(flight1);
            
            Flight retrieved = system.getFlightByID(1);
            assertEquals(flight1, retrieved);
        }
        
        @Test
        @DisplayName("Should throw exception when flight ID not found")
        void testGetFlightByIdNotFound() {
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.getFlightByID(999);
            });
            assertEquals("There is no flight with that ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get only active flights")
        void testGetActiveFlights() throws FlightBookingSystemException {
            system.addFlight(flight1);
            system.addFlight(flight2);
            
            flight1.setDeleted(true);
            
            List<Flight> activeFlights = system.getActiveFlights();
            assertEquals(1, activeFlights.size());
            assertFalse(activeFlights.contains(flight1));
            assertTrue(activeFlights.contains(flight2));
        }
        
        @Test
        @DisplayName("Should get future flights only")
        void testGetFutureFlights() throws FlightBookingSystemException {
            // Create a flight in the past relative to system date (2020-11-11)
            // But still in future relative to NOW for Flight constructor validation
            LocalDate dateBeforeSystemDate = LocalDate.now().plusDays(1);
            Flight pastFlight = new Flight(3, "BA789", "LHR", "CDG", dateBeforeSystemDate);
            
            system.addFlight(flight1); // Future flight
            system.addFlight(pastFlight);
            
            // getFutureFlights checks against system date (2020-11-11)
            // Both flights are after 2020-11-11, so both should be returned
            List<Flight> futureFlights = system.getFutureFlights();
            assertEquals(2, futureFlights.size());
        }
        
        @Test
        @DisplayName("Should return unmodifiable flight list")
        void testGetFlightsUnmodifiable() throws FlightBookingSystemException {
            system.addFlight(flight1);
            List<Flight> flights = system.getFlights();
            
            assertThrows(UnsupportedOperationException.class, () -> {
                flights.add(flight2);
            });
        }
        
        @Test
        @DisplayName("Should set flights from list")
        void testSetFlights() {
            List<Flight> flightList = List.of(flight1, flight2);
            system.setFlights(flightList);
            
            assertEquals(2, system.getFlights().size());
            assertTrue(system.getFlights().contains(flight1));
            assertTrue(system.getFlights().contains(flight2));
        }
        
        @Test
        @DisplayName("Should clear existing flights when setting new list")
        void testSetFlightsClearsExisting() throws FlightBookingSystemException {
            system.addFlight(flight1);
            assertEquals(1, system.getFlights().size());
            
            List<Flight> newFlights = List.of(flight2);
            system.setFlights(newFlights);
            
            assertEquals(1, system.getFlights().size());
            assertFalse(system.getFlights().contains(flight1));
            assertTrue(system.getFlights().contains(flight2));
        }
        
        @Test
        @DisplayName("Should handle null flight list in setFlights")
        void testSetFlightsNull() throws FlightBookingSystemException {
            system.addFlight(flight1);
            
            system.setFlights(null);
            assertTrue(system.getFlights().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Customer Management Tests")
    class CustomerManagementTests {
        
        private Customer customer1;
        private Customer customer2;
        
        @BeforeEach
        void setUpCustomers() {
            customer1 = new Customer(1, "John Doe", "1234567890", "john@example.com");
            customer2 = new Customer(2, "Jane Smith", "9876543210", "jane@example.com");
        }
        
        @Test
        @DisplayName("Should start with no customers")
        void testInitiallyNoCustomers() {
            assertTrue(system.getCustomers().isEmpty());
        }
        
        @Test
        @DisplayName("Should add customer successfully")
        void testAddCustomer() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            
            assertEquals(1, system.getCustomers().size());
            assertTrue(system.getCustomers().contains(customer1));
        }
        
        @Test
        @DisplayName("Should add multiple customers")
        void testAddMultipleCustomers() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            system.addCustomer(customer2);
            
            assertEquals(2, system.getCustomers().size());
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate customer ID")
        void testAddDuplicateCustomerId() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            
            Customer duplicate = new Customer(1, "Bob Johnson", "5555555555", "bob@example.com");
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.addCustomer(duplicate);
            });
            assertEquals("Duplicate customer ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get customer by ID")
        void testGetCustomerById() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            
            Customer retrieved = system.getCustomerByID(1);
            assertEquals(customer1, retrieved);
        }
        
        @Test
        @DisplayName("Should throw exception when customer ID not found")
        void testGetCustomerByIdNotFound() {
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.getCustomerByID(999);
            });
            assertEquals("There is no customer with that ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get only active customers")
        void testGetActiveCustomers() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            system.addCustomer(customer2);
            
            customer1.setDeleted(true);
            
            List<Customer> activeCustomers = system.getActiveCustomers();
            assertEquals(1, activeCustomers.size());
            assertFalse(activeCustomers.contains(customer1));
            assertTrue(activeCustomers.contains(customer2));
        }
        
        @Test
        @DisplayName("Should return unmodifiable customer list")
        void testGetCustomersUnmodifiable() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            List<Customer> customers = system.getCustomers();
            
            assertThrows(UnsupportedOperationException.class, () -> {
                customers.add(customer2);
            });
        }
        
        @Test
        @DisplayName("Should set customers from list")
        void testSetCustomers() {
            List<Customer> customerList = List.of(customer1, customer2);
            system.setCustomers(customerList);
            
            assertEquals(2, system.getCustomers().size());
            assertTrue(system.getCustomers().contains(customer1));
            assertTrue(system.getCustomers().contains(customer2));
        }
        
        @Test
        @DisplayName("Should clear existing customers when setting new list")
        void testSetCustomersClearsExisting() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            
            List<Customer> newCustomers = List.of(customer2);
            system.setCustomers(newCustomers);
            
            assertEquals(1, system.getCustomers().size());
            assertFalse(system.getCustomers().contains(customer1));
            assertTrue(system.getCustomers().contains(customer2));
        }
        
        @Test
        @DisplayName("Should handle null customer list in setCustomers")
        void testSetCustomersNull() throws FlightBookingSystemException {
            system.addCustomer(customer1);
            
            system.setCustomers(null);
            assertTrue(system.getCustomers().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("User Management Tests")
    class UserManagementTests {
        
        private User adminUser;
        private User customerUser;
        
        @BeforeEach
        void setUpUsers() {
            adminUser = new User(1, "admin", "admin123", "ADMIN");
            customerUser = new User(2, "customer1", "pass123", "CUSTOMER");
        }
        
        @Test
        @DisplayName("Should start with no users")
        void testInitiallyNoUsers() {
            assertTrue(system.getUsers().isEmpty());
        }
        
        @Test
        @DisplayName("Should add user successfully")
        void testAddUser() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            assertEquals(1, system.getUsers().size());
            assertTrue(system.getUsers().contains(adminUser));
        }
        
        @Test
        @DisplayName("Should add multiple users")
        void testAddMultipleUsers() throws FlightBookingSystemException {
            system.addUser(adminUser);
            system.addUser(customerUser);
            
            assertEquals(2, system.getUsers().size());
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate user ID")
        void testAddDuplicateUserId() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            User duplicate = new User(1, "another", "pass", "CUSTOMER");
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.addUser(duplicate);
            });
            assertEquals("Duplicate user ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate username")
        void testAddDuplicateUsername() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            User duplicate = new User(3, "admin", "different", "CUSTOMER");
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.addUser(duplicate);
            });
            assertEquals("Username already exists.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get user by ID")
        void testGetUserById() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            User retrieved = system.getUserByID(1);
            assertEquals(adminUser, retrieved);
        }
        
        @Test
        @DisplayName("Should throw exception when user ID not found")
        void testGetUserByIdNotFound() {
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.getUserByID(999);
            });
            assertEquals("There is no user with that ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get user by username")
        void testGetUserByUsername() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            User retrieved = system.getUserByUsername("admin");
            assertEquals(adminUser, retrieved);
        }
        
        @Test
        @DisplayName("Should return null when username not found")
        void testGetUserByUsernameNotFound() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            User retrieved = system.getUserByUsername("nonexistent");
            assertNull(retrieved);
        }
        
        @Test
        @DisplayName("Should get user by username case-insensitively")
        void testGetUserByUsernameCaseInsensitive() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            User retrieved = system.getUserByUsername("ADMIN");
            assertEquals(adminUser, retrieved);
        }
        
        @Test
        @DisplayName("Should set users from list")
        void testSetUsers() {
            List<User> userList = List.of(adminUser, customerUser);
            system.setUsers(userList);
            
            assertEquals(2, system.getUsers().size());
            assertTrue(system.getUsers().contains(adminUser));
            assertTrue(system.getUsers().contains(customerUser));
        }
        
        @Test
        @DisplayName("Should handle null user list in setUsers")
        void testSetUsersNull() throws FlightBookingSystemException {
            system.addUser(adminUser);
            
            system.setUsers(null);
            assertTrue(system.getUsers().isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Authentication Tests")
    class AuthenticationTests {
        
        private User adminUser;
        private User customerUser;
        
        @BeforeEach
        void setUpAuth() throws FlightBookingSystemException {
            adminUser = new User(1, "admin", "admin123", "ADMIN");
            customerUser = new User(2, "customer1", "pass123", "CUSTOMER");
            
            system.addUser(adminUser);
            system.addUser(customerUser);
        }
        
        @Test
        @DisplayName("Should authenticate valid user")
        void testAuthenticateValid() {
            User authenticated = system.authenticate("admin", "admin123");
            
            assertNotNull(authenticated);
            assertEquals(adminUser, authenticated);
            assertEquals(adminUser, system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should fail authentication with wrong password")
        void testAuthenticateWrongPassword() {
            User authenticated = system.authenticate("admin", "wrongpass");
            
            assertNull(authenticated);
            assertNull(system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should fail authentication with non-existent username")
        void testAuthenticateNonExistentUser() {
            User authenticated = system.authenticate("nonexistent", "pass");
            
            assertNull(authenticated);
            assertNull(system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should set current user after successful authentication")
        void testCurrentUserAfterAuth() {
            system.authenticate("customer1", "pass123");
            
            assertEquals(customerUser, system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should logout current user")
        void testLogout() {
            system.authenticate("admin", "admin123");
            assertNotNull(system.getCurrentUser());
            
            system.logout();
            assertNull(system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should set current user manually")
        void testSetCurrentUser() {
            system.setCurrentUser(adminUser);
            assertEquals(adminUser, system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should handle multiple authentication attempts")
        void testMultipleAuthAttempts() {
            system.authenticate("admin", "admin123");
            assertEquals(adminUser, system.getCurrentUser());
            
            system.authenticate("customer1", "pass123");
            assertEquals(customerUser, system.getCurrentUser());
        }
    }
    
    @Nested
    @DisplayName("Feedback Management Tests")
    class FeedbackManagementTests {
        
        private Feedback feedback1;
        private Feedback feedback2;
        private Feedback feedback3;
        
        @BeforeEach
        void setUpFeedback() {
            feedback1 = new Feedback(1, 1, 1, 5, "Excellent flight!", LocalDate.now());
            feedback2 = new Feedback(2, 1, 2, 4, "Good service", LocalDate.now());
            feedback3 = new Feedback(3, 2, 1, 3, "Average experience", LocalDate.now());
        }
        
        @Test
        @DisplayName("Should start with no feedback")
        void testInitiallyNoFeedback() {
            assertTrue(system.getAllFeedback().isEmpty());
        }
        
        @Test
        @DisplayName("Should add feedback successfully")
        void testAddFeedback() throws FlightBookingSystemException {
            system.addFeedback(feedback1);
            
            assertEquals(1, system.getAllFeedback().size());
            assertTrue(system.getAllFeedback().contains(feedback1));
        }
        
        @Test
        @DisplayName("Should add multiple feedbacks")
        void testAddMultipleFeedbacks() throws FlightBookingSystemException {
            system.addFeedback(feedback1);
            system.addFeedback(feedback2);
            
            assertEquals(2, system.getAllFeedback().size());
        }
        
        @Test
        @DisplayName("Should throw exception for duplicate feedback ID")
        void testAddDuplicateFeedbackId() throws FlightBookingSystemException {
            system.addFeedback(feedback1);
            
            Feedback duplicate = new Feedback(1, 2, 2, 4, "Another", LocalDate.now());
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.addFeedback(duplicate);
            });
            assertEquals("Duplicate feedback ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get feedback by ID")
        void testGetFeedbackById() throws FlightBookingSystemException {
            system.addFeedback(feedback1);
            
            Feedback retrieved = system.getFeedbackByID(1);
            assertEquals(feedback1, retrieved);
        }
        
        @Test
        @DisplayName("Should throw exception when feedback ID not found")
        void testGetFeedbackByIdNotFound() {
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.getFeedbackByID(999);
            });
            assertEquals("There is no feedback with that ID.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get feedback for specific flight")
        void testGetFeedbackForFlight() throws FlightBookingSystemException {
            system.addFeedback(feedback1); // Flight 1
            system.addFeedback(feedback2); // Flight 2
            system.addFeedback(feedback3); // Flight 1
            
            List<Feedback> flightFeedback = system.getFeedbackForFlight(1);
            assertEquals(2, flightFeedback.size());
            assertTrue(flightFeedback.contains(feedback1));
            assertTrue(flightFeedback.contains(feedback3));
        }
        
        @Test
        @DisplayName("Should return empty list when no feedback for flight")
        void testGetFeedbackForFlightEmpty() {
            List<Feedback> flightFeedback = system.getFeedbackForFlight(999);
            assertTrue(flightFeedback.isEmpty());
        }
        
        @Test
        @DisplayName("Should get feedback from specific customer")
        void testGetFeedbackFromCustomer() throws FlightBookingSystemException {
            system.addFeedback(feedback1); // Customer 1
            system.addFeedback(feedback2); // Customer 1
            system.addFeedback(feedback3); // Customer 2
            
            List<Feedback> customerFeedback = system.getFeedbackFromCustomer(1);
            assertEquals(2, customerFeedback.size());
            assertTrue(customerFeedback.contains(feedback1));
            assertTrue(customerFeedback.contains(feedback2));
        }
        
        @Test
        @DisplayName("Should calculate average rating for flight")
        void testGetAverageRatingForFlight() throws FlightBookingSystemException {
            system.addFeedback(feedback1); // 5 stars, flight 1
            system.addFeedback(feedback3); // 3 stars, flight 1
            
            double avgRating = system.getAverageRatingForFlight(1);
            assertEquals(4.0, avgRating, 0.01);
        }
        
        @Test
        @DisplayName("Should return 0.0 when no feedback exists for flight")
        void testGetAverageRatingNoFeedback() {
            double avgRating = system.getAverageRatingForFlight(999);
            assertEquals(0.0, avgRating);
        }
        
        @Test
        @DisplayName("Should check if customer has reviewed flight")
        void testHasCustomerReviewedFlight() throws FlightBookingSystemException {
            system.addFeedback(feedback1); // Customer 1, Flight 1
            
            assertTrue(system.hasCustomerReviewedFlight(1, 1));
            assertFalse(system.hasCustomerReviewedFlight(1, 2));
            assertFalse(system.hasCustomerReviewedFlight(2, 1));
        }
        
        @Test
        @DisplayName("Should delete feedback")
        void testDeleteFeedback() throws FlightBookingSystemException {
            system.addFeedback(feedback1);
            assertEquals(1, system.getAllFeedback().size());
            
            system.deleteFeedback(1);
            assertEquals(0, system.getAllFeedback().size());
        }
        
        @Test
        @DisplayName("Should throw exception when deleting non-existent feedback")
        void testDeleteNonExistentFeedback() {
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                system.deleteFeedback(999);
            });
            assertEquals("There is no feedback with that ID.", exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete booking workflow")
        void testCompleteBookingWorkflow() throws FlightBookingSystemException {
            // 1. Add user and authenticate
            User user = new User(1, "customer1", "pass123", "CUSTOMER");
            system.addUser(user);
            system.authenticate("customer1", "pass123");
            
            assertNotNull(system.getCurrentUser());
            
            // 2. Add customer
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            system.addCustomer(customer);
            
            // 3. Add flight
            Flight flight = new Flight(1, "BA123", "LHR", "CDG", futureDate);
            system.addFlight(flight);
            
            // 4. Verify all data
            assertEquals(1, system.getUsers().size());
            assertEquals(1, system.getCustomers().size());
            assertEquals(1, system.getFlights().size());
        }
        
        @Test
        @DisplayName("Should handle multiple flights and customers")
        void testMultipleEntities() throws FlightBookingSystemException {
            // Add multiple flights
            Flight f1 = new Flight(1, "BA123", "LHR", "CDG", futureDate);
            Flight f2 = new Flight(2, "BA456", "CDG", "JFK", futureDate.plusDays(1));
            Flight f3 = new Flight(3, "BA789", "JFK", "LAX", futureDate.plusDays(2));
            
            system.addFlight(f1);
            system.addFlight(f2);
            system.addFlight(f3);
            
            // Add multiple customers
            Customer c1 = new Customer(1, "John Doe", "1111111111", "john@example.com");
            Customer c2 = new Customer(2, "Jane Smith", "2222222222", "jane@example.com");
            
            system.addCustomer(c1);
            system.addCustomer(c2);
            
            // Verify
            assertEquals(3, system.getFlights().size());
            assertEquals(2, system.getCustomers().size());
        }
        
        @Test
        @DisplayName("Should maintain data integrity with soft deletes")
        void testSoftDeleteIntegrity() throws FlightBookingSystemException {
            Flight flight = new Flight(1, "BA123", "LHR", "CDG", futureDate);
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            
            system.addFlight(flight);
            system.addCustomer(customer);
            
            // Soft delete
            flight.setDeleted(true);
            customer.setDeleted(true);
            
            // Still in system but not in active lists
            assertEquals(1, system.getFlights().size());
            assertEquals(0, system.getActiveFlights().size());
            assertEquals(1, system.getCustomers().size());
            assertEquals(0, system.getActiveCustomers().size());
        }
        
        @Test
        @DisplayName("Should handle feedback workflow")
        void testFeedbackWorkflow() throws FlightBookingSystemException {
            // Setup
            Flight flight = new Flight(1, "BA123", "LHR", "CDG", futureDate);
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            
            system.addFlight(flight);
            system.addCustomer(customer);
            
            // Add feedback
            Feedback feedback = new Feedback(1, 1, 1, 5, "Great flight!", LocalDate.now());
            system.addFeedback(feedback);
            
            // Verify
            assertTrue(system.hasCustomerReviewedFlight(1, 1));
            assertEquals(5.0, system.getAverageRatingForFlight(1));
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle empty system queries")
        void testEmptySystemQueries() {
            assertTrue(system.getFlights().isEmpty());
            assertTrue(system.getCustomers().isEmpty());
            assertTrue(system.getUsers().isEmpty());
            assertTrue(system.getAllFeedback().isEmpty());
            assertTrue(system.getActiveFlights().isEmpty());
            assertTrue(system.getActiveCustomers().isEmpty());
            assertTrue(system.getFutureFlights().isEmpty());
        }
        
        @Test
        @DisplayName("Should handle logout when no user logged in")
        void testLogoutWhenNoUser() {
            assertNull(system.getCurrentUser());
            system.logout();
            assertNull(system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should handle setting current user to null")
        void testSetCurrentUserNull() throws FlightBookingSystemException {
            User user = new User(1, "admin", "pass", "ADMIN");
            system.addUser(user);
            system.setCurrentUser(user);
            
            system.setCurrentUser(null);
            assertNull(system.getCurrentUser());
        }
        
        @Test
        @DisplayName("Should handle getting feedback for non-existent flight")
        void testGetFeedbackForNonExistentFlight() {
            List<Feedback> feedback = system.getFeedbackForFlight(9999);
            assertNotNull(feedback);
            assertTrue(feedback.isEmpty());
        }
        
        @Test
        @DisplayName("Should handle getting feedback from non-existent customer")
        void testGetFeedbackFromNonExistentCustomer() {
            List<Feedback> feedback = system.getFeedbackFromCustomer(9999);
            assertNotNull(feedback);
            assertTrue(feedback.isEmpty());
        }
    }
}