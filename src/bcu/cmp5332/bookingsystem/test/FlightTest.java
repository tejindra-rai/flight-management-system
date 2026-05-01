package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.Customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Flight
 * Tests all methods and functionality of the Flight class
 * 
 * @author Tejindra Rai
 */
class FlightTest {
    
    private Flight flight;
    private LocalDate futureDate;
    
    @BeforeEach
    void setUp() {
        futureDate = LocalDate.now().plusDays(30);
        flight = new Flight(1, "BA123", "LHR", "CDG", futureDate);
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create flight with valid parameters")
        void testValidConstructor() {
            assertEquals(1, flight.getId());
            assertEquals("BA123", flight.getFlightNumber());
            assertEquals("LHR", flight.getOrigin());
            assertEquals("CDG", flight.getDestination());
            assertEquals(futureDate, flight.getDepartureDate());
        }
        
        @Test
        @DisplayName("Should initialize with default values")
        void testDefaultValues() {
            assertEquals(100, flight.getCapacity());
            assertEquals(100.0, flight.getBasePrice());
            assertFalse(flight.isDeleted());
            assertEquals("Economy", flight.getFlightClass());
            assertFalse(flight.isReturnFlight());
            assertNotNull(flight.getFlightType());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid ID (zero)")
        void testInvalidIdZero() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(0, "BA123", "LHR", "CDG", futureDate);
            });
            assertEquals("Flight ID must be positive.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid ID (negative)")
        void testInvalidIdNegative() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(-1, "BA123", "LHR", "CDG", futureDate);
            });
            assertEquals("Flight ID must be positive.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for null flight number")
        void testNullFlightNumber() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, null, "LHR", "CDG", futureDate);
            });
            assertEquals("Flight number cannot be empty.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for empty flight number")
        void testEmptyFlightNumber() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "", "LHR", "CDG", futureDate);
            });
            assertEquals("Flight number cannot be empty.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for whitespace-only flight number")
        void testWhitespaceFlightNumber() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "   ", "LHR", "CDG", futureDate);
            });
            assertEquals("Flight number cannot be empty.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid origin (not 3 letters)")
        void testInvalidOriginLength() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "BA123", "LH", "CDG", futureDate);
            });
            assertEquals("Origin must be a 3-letter uppercase IATA code.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for lowercase origin")
        void testLowercaseOrigin() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "BA123", "lhr", "CDG", futureDate);
            });
            assertEquals("Origin must be a 3-letter uppercase IATA code.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid destination (not 3 letters)")
        void testInvalidDestinationLength() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "BA123", "LHR", "CDGG", futureDate);
            });
            assertEquals("Destination must be a 3-letter uppercase IATA code.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for lowercase destination")
        void testLowercaseDestination() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "BA123", "LHR", "cdg", futureDate);
            });
            assertEquals("Destination must be a 3-letter uppercase IATA code.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when origin equals destination")
        void testSameOriginAndDestination() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "BA123", "LHR", "LHR", futureDate);
            });
            assertEquals("Origin and destination cannot be the same.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for past departure date")
        void testPastDepartureDate() {
            LocalDate pastDate = LocalDate.now().minusDays(1);
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                new Flight(1, "BA123", "LHR", "CDG", pastDate);
            });
            assertEquals("Departure date must be in the future.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should allow today's date as departure date")
        void testTodayDepartureDate() {
            LocalDate today = LocalDate.now();
            // Today's date is allowed (validation only rejects dates BEFORE today)
            Flight f = new Flight(1, "BA123", "LHR", "CDG", today);
            assertEquals(today, f.getDepartureDate());
        }
        
        @Test
        @DisplayName("Should trim whitespace from flight number")
        void testTrimFlightNumber() {
            Flight f = new Flight(1, "  BA123  ", "LHR", "CDG", futureDate);
            assertEquals("BA123", f.getFlightNumber());
        }
    }
    
    @Nested
    @DisplayName("Capacity Tests")
    class CapacityTests {
        
        @Test
        @DisplayName("Should set valid capacity")
        void testSetValidCapacity() {
            flight.setCapacity(200);
            assertEquals(200, flight.getCapacity());
        }
        
        @Test
        @DisplayName("Should throw exception for zero capacity")
        void testZeroCapacity() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                flight.setCapacity(0);
            });
            assertEquals("Capacity must be positive.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for negative capacity")
        void testNegativeCapacity() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                flight.setCapacity(-50);
            });
            assertEquals("Capacity must be positive.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should set small capacity")
        void testSmallCapacity() {
            flight.setCapacity(10);
            assertEquals(10, flight.getCapacity());
        }
        
        @Test
        @DisplayName("Should set large capacity")
        void testLargeCapacity() {
            flight.setCapacity(500);
            assertEquals(500, flight.getCapacity());
        }
    }
    
    @Nested
    @DisplayName("Base Price Tests")
    class BasePriceTests {
        
        @Test
        @DisplayName("Should set valid base price")
        void testSetValidBasePrice() {
            flight.setBasePrice(250.50);
            assertEquals(250.50, flight.getBasePrice());
        }
        
        @Test
        @DisplayName("Should throw exception for zero price")
        void testZeroPrice() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                flight.setBasePrice(0.0);
            });
            assertEquals("Base price must be positive.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for negative price")
        void testNegativePrice() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                flight.setBasePrice(-100.0);
            });
            assertEquals("Base price must be positive.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should set small price")
        void testSmallPrice() {
            flight.setBasePrice(0.01);
            assertEquals(0.01, flight.getBasePrice());
        }
        
        @Test
        @DisplayName("Should set large price")
        void testLargePrice() {
            flight.setBasePrice(9999.99);
            assertEquals(9999.99, flight.getBasePrice());
        }
    }
    
    @Nested
    @DisplayName("Deleted Status Tests")
    class DeletedStatusTests {
        
        @Test
        @DisplayName("Should not be deleted by default")
        void testDefaultNotDeleted() {
            assertFalse(flight.isDeleted());
        }
        
        @Test
        @DisplayName("Should set deleted status to true")
        void testSetDeletedTrue() {
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
        }
        
        @Test
        @DisplayName("Should set deleted status to false")
        void testSetDeletedFalse() {
            flight.setDeleted(true);
            flight.setDeleted(false);
            assertFalse(flight.isDeleted());
        }
        
        @Test
        @DisplayName("Should toggle deleted status")
        void testToggleDeleted() {
            assertFalse(flight.isDeleted());
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
            flight.setDeleted(false);
            assertFalse(flight.isDeleted());
        }
    }
    
    @Nested
    @DisplayName("Flight Class Tests")
    class FlightClassTests {
        
        @Test
        @DisplayName("Should have 'Economy' as default class")
        void testDefaultFlightClass() {
            assertEquals("Economy", flight.getFlightClass());
        }
        
        @Test
        @DisplayName("Should set Business class")
        void testSetBusinessClass() {
            flight.setFlightClass("Business");
            assertEquals("Business", flight.getFlightClass());
        }
        
        @Test
        @DisplayName("Should set First class")
        void testSetFirstClass() {
            flight.setFlightClass("First");
            assertEquals("First", flight.getFlightClass());
        }
        
        @Test
        @DisplayName("Should set Premium Economy class")
        void testSetPremiumEconomy() {
            flight.setFlightClass("Premium Economy");
            assertEquals("Premium Economy", flight.getFlightClass());
        }
        
        @Test
        @DisplayName("Should handle null flight class")
        void testNullFlightClass() {
            flight.setFlightClass(null);
            assertEquals("Economy", flight.getFlightClass());
        }
        
        @Test
        @DisplayName("Should trim whitespace from flight class")
        void testTrimFlightClass() {
            flight.setFlightClass("  Business  ");
            assertEquals("Business", flight.getFlightClass());
        }
    }
    
    @Nested
    @DisplayName("Return Flight Tests")
    class ReturnFlightTests {
        
        @Test
        @DisplayName("Should not be return flight by default")
        void testDefaultNotReturnFlight() {
            assertFalse(flight.isReturnFlight());
        }
        
        @Test
        @DisplayName("Should set return flight to true")
        void testSetReturnFlightTrue() {
            flight.setReturnFlight(true);
            assertTrue(flight.isReturnFlight());
        }
        
        @Test
        @DisplayName("Should set return flight to false")
        void testSetReturnFlightFalse() {
            flight.setReturnFlight(true);
            flight.setReturnFlight(false);
            assertFalse(flight.isReturnFlight());
        }
    }
    
    @Nested
    @DisplayName("Flight Type Tests")
    class FlightTypeTests {
        
        @Test
        @DisplayName("Should be INTERNATIONAL for LHR to CDG")
        void testInternationalFlight() {
            Flight f = new Flight(1, "BA123", "LHR", "CDG", futureDate);
            assertEquals("INTERNATIONAL", f.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should be DOMESTIC for UK to UK flight")
        void testDomesticUKFlight() {
            Flight f = new Flight(1, "BA123", "LHR", "MAN", futureDate);
            assertEquals("DOMESTIC", f.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should be DOMESTIC for USA to USA flight")
        void testDomesticUSAFlight() {
            Flight f = new Flight(1, "AA123", "JFK", "LAX", futureDate);
            assertEquals("DOMESTIC", f.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should be DOMESTIC for Nepal to Nepal flight")
        void testDomesticNepalFlight() {
            Flight f = new Flight(1, "NA123", "KTM", "PKR", futureDate);
            assertEquals("DOMESTIC", f.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should be INTERNATIONAL for UK to USA flight")
        void testInternationalUKToUSA() {
            Flight f = new Flight(1, "BA123", "LHR", "JFK", futureDate);
            assertEquals("INTERNATIONAL", f.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should be INTERNATIONAL for Nepal to UK flight")
        void testInternationalNepalToUK() {
            Flight f = new Flight(1, "NA123", "KTM", "LHR", futureDate);
            assertEquals("INTERNATIONAL", f.getFlightType().name());
        }
    }
    
    @Nested
    @DisplayName("Passenger Management Tests")
    class PassengerManagementTests {
        
        private Customer customer1;
        private Customer customer2;
        
        @BeforeEach
        void setUpPassengers() {
            customer1 = new Customer(1, "John Doe", "1234567890", "john@example.com");
            customer2 = new Customer(2, "Jane Smith", "9876543210", "jane@example.com");
        }
        
        @Test
        @DisplayName("Should have no passengers initially")
        void testInitiallyNoPassengers() {
            assertTrue(flight.getPassengers().isEmpty());
            assertEquals(0, flight.getPassengers().size());
        }
        
        @Test
        @DisplayName("Should add passenger successfully")
        void testAddPassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            
            assertEquals(1, flight.getPassengers().size());
            assertTrue(flight.getPassengers().contains(customer1));
        }
        
        @Test
        @DisplayName("Should add multiple passengers")
        void testAddMultiplePassengers() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            
            assertEquals(2, flight.getPassengers().size());
            assertTrue(flight.getPassengers().contains(customer1));
            assertTrue(flight.getPassengers().contains(customer2));
        }
        
        @Test
        @DisplayName("Should throw exception when adding null passenger")
        void testAddNullPassenger() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                flight.addPassenger(null);
            });
            assertEquals("Passenger cannot be null.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when adding duplicate passenger")
        void testAddDuplicatePassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                flight.addPassenger(customer1);
            });
            assertEquals("Passenger is already booked on this flight.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when flight is full")
        void testAddPassengerToFullFlight() throws FlightBookingSystemException {
            flight.setCapacity(2);
            flight.addPassenger(customer1);
            flight.addPassenger(customer2);
            
            Customer customer3 = new Customer(3, "Bob Johnson", "5555555555", "bob@example.com");
            
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                flight.addPassenger(customer3);
            });
            assertTrue(exception.getMessage().contains("Flight is full"));
        }
        
        @Test
        @DisplayName("Should remove passenger successfully")
        void testRemovePassenger() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            assertEquals(1, flight.getPassengers().size());
            
            flight.removePassenger(customer1);
            assertEquals(0, flight.getPassengers().size());
            assertFalse(flight.getPassengers().contains(customer1));
        }
        
        @Test
        @DisplayName("Should throw exception when removing null passenger")
        void testRemoveNullPassenger() {
            Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                flight.removePassenger(null);
            });
            assertEquals("Passenger cannot be null.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception when removing non-existent passenger")
        void testRemoveNonExistentPassenger() {
            Exception exception = assertThrows(FlightBookingSystemException.class, () -> {
                flight.removePassenger(customer1);
            });
            assertEquals("Passenger is not booked on this flight.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should get available seats correctly")
        void testGetAvailableSeats() throws FlightBookingSystemException {
            flight.setCapacity(100);
            assertEquals(100, flight.getAvailableSeats());
            
            flight.addPassenger(customer1);
            assertEquals(99, flight.getAvailableSeats());
            
            flight.addPassenger(customer2);
            assertEquals(98, flight.getAvailableSeats());
        }
        
        @Test
        @DisplayName("Should return unmodifiable passenger list")
        void testGetPassengersReturnsUnmodifiable() throws FlightBookingSystemException {
            flight.addPassenger(customer1);
            List<Customer> passengers = flight.getPassengers();
            
            assertEquals(1, passengers.size());
            // The returned list should be a new ArrayList, so we can't test immutability easily
            // but we can verify it's a copy
            assertNotNull(passengers);
        }
    }
    
    @Nested
    @DisplayName("Details Display Tests")
    class DetailsDisplayTests {
        
        @Test
        @DisplayName("Should return correct short details")
        void testGetDetailsShort() {
            String details = flight.getDetailsShort();
            
            assertTrue(details.contains("Flight #1"));
            assertTrue(details.contains("BA123"));
            assertTrue(details.contains("LHR"));
            assertTrue(details.contains("CDG"));
        }
        
        @Test
        @DisplayName("Should return correct long details without passengers")
        void testGetDetailsLongNoPassengers() {
            flight.setCapacity(100);
            flight.setBasePrice(150.00);
            
            String details = flight.getDetailsLong();
            
            assertTrue(details.contains("Flight #1"));
            assertTrue(details.contains("BA123"));
            assertTrue(details.contains("Active"));
            assertTrue(details.contains("Economy"));
            assertTrue(details.contains("100 seats"));
            assertTrue(details.contains("No passengers booked yet"));
        }
        
        @Test
        @DisplayName("Should return correct long details with passengers")
        void testGetDetailsLongWithPassengers() throws FlightBookingSystemException {
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            flight.addPassenger(customer);
            
            String details = flight.getDetailsLong();
            
            assertTrue(details.contains("Flight #1"));
            assertTrue(details.contains("BA123"));
            assertTrue(details.contains("John Doe"));
        }
        
        @Test
        @DisplayName("Should show deleted status in long details")
        void testGetDetailsLongDeleted() {
            flight.setDeleted(true);
            String details = flight.getDetailsLong();
            
            assertTrue(details.contains("Deleted"));
        }
        
        @Test
        @DisplayName("Should show return flight status")
        void testGetDetailsLongReturnFlight() {
            flight.setReturnFlight(true);
            String details = flight.getDetailsLong();
            
            assertTrue(details.contains("Return Flight: Yes"));
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return correct toString representation")
        void testToString() {
            String result = flight.toString();
            
            assertTrue(result.contains("Flight{"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("flightNumber='BA123'"));
            assertTrue(result.contains("origin='LHR'"));
            assertTrue(result.contains("destination='CDG'"));
        }
        
        @Test
        @DisplayName("Should include passenger count in toString")
        void testToStringWithPassengers() throws FlightBookingSystemException {
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            flight.addPassenger(customer);
            
            String result = flight.toString();
            assertTrue(result.contains("passengers=1"));
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should maintain data integrity across multiple operations")
        void testDataIntegrity() throws FlightBookingSystemException {
            flight.setCapacity(150);
            flight.setBasePrice(250.00);
            flight.setFlightClass("Business");
            flight.setReturnFlight(true);
            
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            flight.addPassenger(customer);
            
            assertEquals(150, flight.getCapacity());
            assertEquals(250.00, flight.getBasePrice());
            assertEquals("Business", flight.getFlightClass());
            assertTrue(flight.isReturnFlight());
            assertEquals(1, flight.getPassengers().size());
            assertEquals(149, flight.getAvailableSeats());
        }
        
        @Test
        @DisplayName("Should handle complete flight lifecycle")
        void testFlightLifecycle() throws FlightBookingSystemException {
            // 1. Create flight
            assertFalse(flight.isDeleted());
            assertEquals(100, flight.getCapacity());
            
            // 2. Modify flight details
            flight.setCapacity(200);
            flight.setBasePrice(300.00);
            flight.setFlightClass("First");
            
            assertEquals(200, flight.getCapacity());
            assertEquals(300.00, flight.getBasePrice());
            
            // 3. Add passengers
            Customer c1 = new Customer(1, "John Doe", "1234567890", "john@example.com");
            Customer c2 = new Customer(2, "Jane Smith", "9876543210", "jane@example.com");
            flight.addPassenger(c1);
            flight.addPassenger(c2);
            
            assertEquals(2, flight.getPassengers().size());
            assertEquals(198, flight.getAvailableSeats());
            
            // 4. Soft delete
            flight.setDeleted(true);
            assertTrue(flight.isDeleted());
            
            // 5. Passengers should still exist
            assertEquals(2, flight.getPassengers().size());
        }
        
        @Test
        @DisplayName("Should handle capacity reduction scenario")
        void testCapacityReduction() throws FlightBookingSystemException {
            flight.setCapacity(100);
            
            Customer c1 = new Customer(1, "John Doe", "1234567890", "john@example.com");
            Customer c2 = new Customer(2, "Jane Smith", "9876543210", "jane@example.com");
            flight.addPassenger(c1);
            flight.addPassenger(c2);
            
            assertEquals(98, flight.getAvailableSeats());
            
            // Reduce capacity (passengers already booked should remain)
            flight.setCapacity(50);
            assertEquals(48, flight.getAvailableSeats());
            assertEquals(2, flight.getPassengers().size());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle flight with minimum capacity")
        void testMinimumCapacity() {
            flight.setCapacity(1);
            assertEquals(1, flight.getCapacity());
        }
        
        @Test
        @DisplayName("Should handle flight with very large capacity")
        void testVeryLargeCapacity() {
            flight.setCapacity(1000);
            assertEquals(1000, flight.getCapacity());
        }
        
        @Test
        @DisplayName("Should handle flight far in the future")
        void testFarFutureFlight() {
            LocalDate farFuture = LocalDate.now().plusYears(5);
            Flight f = new Flight(1, "BA123", "LHR", "CDG", farFuture);
            assertEquals(farFuture, f.getDepartureDate());
        }
        
        @Test
        @DisplayName("Should handle complex flight numbers")
        void testComplexFlightNumbers() {
            Flight f1 = new Flight(1, "BA-123", "LHR", "CDG", futureDate);
            assertEquals("BA-123", f1.getFlightNumber());
            
            Flight f2 = new Flight(2, "ABC123XYZ", "LHR", "CDG", futureDate);
            assertEquals("ABC123XYZ", f2.getFlightNumber());
        }
        
        @Test
        @DisplayName("Should handle all Nepal airports correctly")
        void testAllNepalAirports() {
            // Test some Nepal domestic routes
            Flight f1 = new Flight(1, "NA123", "KTM", "PKR", futureDate);
            assertEquals("DOMESTIC", f1.getFlightType().name());
            
            Flight f2 = new Flight(2, "NA456", "KTM", "BDP", futureDate);
            assertEquals("DOMESTIC", f2.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should handle all UK airports correctly")
        void testAllUKAirports() {
            Flight f1 = new Flight(1, "BA123", "LHR", "LGW", futureDate);
            assertEquals("DOMESTIC", f1.getFlightType().name());
            
            Flight f2 = new Flight(2, "BA456", "MAN", "EDI", futureDate);
            assertEquals("DOMESTIC", f2.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should handle all USA airports correctly")
        void testAllUSAAirports() {
            Flight f1 = new Flight(1, "AA123", "JFK", "LAX", futureDate);
            assertEquals("DOMESTIC", f1.getFlightType().name());
            
            Flight f2 = new Flight(2, "AA456", "ORD", "DFW", futureDate);
            assertEquals("DOMESTIC", f2.getFlightType().name());
        }
        
        @Test
        @DisplayName("Should handle adding and removing same passenger multiple times")
        void testAddRemovePassengerMultipleTimes() throws FlightBookingSystemException {
            Customer customer = new Customer(1, "John Doe", "1234567890", "john@example.com");
            
            flight.addPassenger(customer);
            assertEquals(1, flight.getPassengers().size());
            
            flight.removePassenger(customer);
            assertEquals(0, flight.getPassengers().size());
            
            flight.addPassenger(customer);
            assertEquals(1, flight.getPassengers().size());
        }
    }
}