package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.SeatManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for SeatManager
 * Tests all methods and functionality of the SeatManager utility class
 * 
 * @author Your Name
 */
class SeatManagerTest {
    
    private FlightBookingSystem fbs;
    private Flight flight;
    private Customer customer1;
    private Customer customer2;
    private Customer customer3;
    private LocalDate futureDate;
    
    @BeforeEach
    void setUp() throws FlightBookingSystemException {
        // Initialize flight booking system
        fbs = new FlightBookingSystem();
        futureDate = LocalDate.now().plusDays(30);
        
        // Create test flight with default capacity of 100
        flight = new Flight(1, "BA123", "LHR", "CDG", futureDate);
        flight.setCapacity(100);
        fbs.addFlight(flight);
        
        // Create test customers
        customer1 = new Customer(1, "John Doe", "1234567890", "john@example.com");
        customer2 = new Customer(2, "Jane Smith", "0987654321", "jane@example.com");
        customer3 = new Customer(3, "Bob Johnson", "1112223333", "bob@example.com");
        
        fbs.addCustomer(customer1);
        fbs.addCustomer(customer2);
        fbs.addCustomer(customer3);
    }
    
    @Nested
    @DisplayName("Generate All Seats Tests")
    class GenerateAllSeatsTests {
        
        @Test
        @DisplayName("Should generate correct number of seats for capacity 100")
        void testGenerateSeatsForCapacity100() {
            flight.setCapacity(100);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(100, seats.size());
        }
        
        @Test
        @DisplayName("Should generate seats in correct format")
        void testSeatFormat() {
            flight.setCapacity(12);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(12, seats.size());
            assertTrue(seats.contains("1A"));
            assertTrue(seats.contains("1B"));
            assertTrue(seats.contains("1C"));
            assertTrue(seats.contains("1D"));
            assertTrue(seats.contains("1E"));
            assertTrue(seats.contains("1F"));
            assertTrue(seats.contains("2A"));
        }
        
        @Test
        @DisplayName("Should handle small capacity flights")
        void testSmallCapacityFlight() {
            flight.setCapacity(6);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(6, seats.size());
            assertEquals("1A", seats.get(0));
            assertEquals("1F", seats.get(5));
        }
        
        @Test
        @DisplayName("Should handle single seat flight")
        void testSingleSeatFlight() {
            flight.setCapacity(1);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(1, seats.size());
            assertEquals("1A", seats.get(0));
        }
        
        @Test
        @DisplayName("Should generate seats for exact rows")
        void testExactRowCapacity() {
            flight.setCapacity(18); // Exactly 3 rows
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(18, seats.size());
            assertTrue(seats.contains("3F")); // Last seat of row 3
        }
        
        @Test
        @DisplayName("Should generate seats for partial rows")
        void testPartialRowCapacity() {
            flight.setCapacity(10); // 1 full row + 4 seats
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(10, seats.size());
            assertTrue(seats.contains("1F")); // Full first row
            assertTrue(seats.contains("2D")); // Partial second row
            assertFalse(seats.contains("2E")); // Should not exist
        }
        
        @Test
        @DisplayName("Should handle large capacity flights")
        void testLargeCapacityFlight() {
            flight.setCapacity(300);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(300, seats.size());
            assertTrue(seats.contains("1A"));
            assertTrue(seats.contains("50F"));
        }
        
        @Test
        @DisplayName("Should maintain consistent seat ordering")
        void testSeatOrdering() {
            flight.setCapacity(12);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals("1A", seats.get(0));
            assertEquals("1B", seats.get(1));
            assertEquals("1C", seats.get(2));
            assertEquals("1D", seats.get(3));
            assertEquals("1E", seats.get(4));
            assertEquals("1F", seats.get(5));
            assertEquals("2A", seats.get(6));
        }
    }
    
    @Nested
    @DisplayName("Get Booked Seats Tests")
    class GetBookedSeatsTests {
        
        @Test
        @DisplayName("Should return empty set when no bookings exist")
        void testNoBookings() {
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            assertNotNull(bookedSeats);
            assertTrue(bookedSeats.isEmpty());
        }
        
        @Test
        @DisplayName("Should return correct booked seats")
        void testBookedSeats() throws FlightBookingSystemException {
            // Create bookings with seats
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("2B");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            assertEquals(2, bookedSeats.size());
            assertTrue(bookedSeats.contains("1A"));
            assertTrue(bookedSeats.contains("2B"));
        }
        
        @Test
        @DisplayName("Should ignore cancelled bookings")
        void testIgnoreCancelledBookings() throws FlightBookingSystemException {
            // Create active booking
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            // Create cancelled booking
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("2B");
            booking2.setCancelled(true);
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            assertEquals(1, bookedSeats.size());
            assertTrue(bookedSeats.contains("1A"));
            assertFalse(bookedSeats.contains("2B"));
        }
        
        @Test
        @DisplayName("Should ignore bookings without seat numbers")
        void testIgnoreBookingsWithoutSeats() throws FlightBookingSystemException {
            // Booking with seat
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            // Booking without seat
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber(null);
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            assertEquals(1, bookedSeats.size());
            assertTrue(bookedSeats.contains("1A"));
        }
        
        @Test
        @DisplayName("Should ignore bookings with empty seat numbers")
        void testIgnoreEmptySeatNumbers() throws FlightBookingSystemException {
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            assertEquals(1, bookedSeats.size());
            assertTrue(bookedSeats.contains("1A"));
        }
        
        @Test
        @DisplayName("Should only return seats for specified flight")
        void testOnlyCurrentFlightSeats() throws FlightBookingSystemException {
            // Create another flight
            Flight flight2 = new Flight(2, "BA456", "CDG", "JFK", futureDate.plusDays(5));
            flight2.setCapacity(100);
            fbs.addFlight(flight2);
            
            // Booking for flight 1
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            // Booking for flight 2
            Booking booking2 = new Booking(customer2, flight2, LocalDate.now());
            booking2.setSeatNumber("2B");
            customer2.addBooking(booking2);
            flight2.addPassenger(customer2);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            assertEquals(1, bookedSeats.size());
            assertTrue(bookedSeats.contains("1A"));
            assertFalse(bookedSeats.contains("2B"));
        }
        
        @Test
        @DisplayName("Should handle duplicate seat numbers correctly")
        void testDuplicateSeatHandling() throws FlightBookingSystemException {
            // Two bookings with same seat (edge case - shouldn't happen but test it)
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("1A");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            // Set should contain only unique entries
            assertEquals(1, bookedSeats.size());
            assertTrue(bookedSeats.contains("1A"));
        }
    }
    
    @Nested
    @DisplayName("Get Available Seats Tests")
    class GetAvailableSeatsTests {
        
        @Test
        @DisplayName("Should return all seats when no bookings exist")
        void testAllSeatsAvailable() {
            flight.setCapacity(12);
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            
            assertEquals(12, availableSeats.size());
            assertTrue(availableSeats.contains("1A"));
            assertTrue(availableSeats.contains("2F"));
        }
        
        @Test
        @DisplayName("Should exclude booked seats from available seats")
        void testExcludeBookedSeats() throws FlightBookingSystemException {
            flight.setCapacity(12);
            
            // Book some seats
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("1B");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            
            assertEquals(10, availableSeats.size());
            assertFalse(availableSeats.contains("1A"));
            assertFalse(availableSeats.contains("1B"));
            assertTrue(availableSeats.contains("1C"));
        }
        
        @Test
        @DisplayName("Should return empty list when flight is full")
        void testFullFlight() throws FlightBookingSystemException {
            flight.setCapacity(6);
            
            // Book all seats - each customer books multiple seats
            String[] seats = {"1A", "1B", "1C", "1D", "1E", "1F"};
            
            // Customer 1 gets seats 1A, 1B
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber(seats[0]);
            customer1.addBooking(booking1);
            
            Booking booking2 = new Booking(customer1, flight, LocalDate.now());
            booking2.setSeatNumber(seats[1]);
            customer1.addBooking(booking2);
            flight.addPassenger(customer1);
            
            // Customer 2 gets seats 1C, 1D
            Booking booking3 = new Booking(customer2, flight, LocalDate.now());
            booking3.setSeatNumber(seats[2]);
            customer2.addBooking(booking3);
            
            Booking booking4 = new Booking(customer2, flight, LocalDate.now());
            booking4.setSeatNumber(seats[3]);
            customer2.addBooking(booking4);
            flight.addPassenger(customer2);
            
            // Customer 3 gets seats 1E, 1F
            Booking booking5 = new Booking(customer3, flight, LocalDate.now());
            booking5.setSeatNumber(seats[4]);
            customer3.addBooking(booking5);
            
            Booking booking6 = new Booking(customer3, flight, LocalDate.now());
            booking6.setSeatNumber(seats[5]);
            customer3.addBooking(booking6);
            flight.addPassenger(customer3);
            
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            
            assertTrue(availableSeats.isEmpty());
        }
        
        @Test
        @DisplayName("Should include cancelled booking seats as available")
        void testCancelledBookingSeatsAvailable() throws FlightBookingSystemException {
            flight.setCapacity(6);
            
            // Active booking
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            // Cancelled booking
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("1B");
            booking2.setCancelled(true);
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            
            assertEquals(5, availableSeats.size());
            assertFalse(availableSeats.contains("1A"));
            assertTrue(availableSeats.contains("1B")); // Cancelled seat is available
        }
        
        @Test
        @DisplayName("Should handle mixed booking scenarios")
        void testMixedBookingScenarios() throws FlightBookingSystemException {
            flight.setCapacity(10);
            
            // Active booking
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            // Cancelled booking
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("1B");
            booking2.setCancelled(true);
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            // Booking without seat
            Booking booking3 = new Booking(customer3, flight, LocalDate.now());
            booking3.setSeatNumber(null);
            customer3.addBooking(booking3);
            flight.addPassenger(customer3);
            
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            
            assertEquals(9, availableSeats.size());
            assertFalse(availableSeats.contains("1A"));
            assertTrue(availableSeats.contains("1B"));
        }
    }
    
    @Nested
    @DisplayName("Is Seat Available Tests")
    class IsSeatAvailableTests {
        
        @Test
        @DisplayName("Should return true for available seat")
        void testSeatIsAvailable() {
            flight.setCapacity(100);
            boolean available = SeatManager.isSeatAvailable(flight, "1A", fbs);
            
            assertTrue(available);
        }
        
        @Test
        @DisplayName("Should return false for booked seat")
        void testSeatIsBooked() throws FlightBookingSystemException {
            flight.setCapacity(100);
            
            Booking booking = new Booking(customer1, flight, LocalDate.now());
            booking.setSeatNumber("1A");
            customer1.addBooking(booking);
            flight.addPassenger(customer1);
            
            boolean available = SeatManager.isSeatAvailable(flight, "1A", fbs);
            
            assertFalse(available);
        }
        
        @Test
        @DisplayName("Should return false for null seat number")
        void testNullSeatNumber() {
            flight.setCapacity(100);
            boolean available = SeatManager.isSeatAvailable(flight, null, fbs);
            
            assertFalse(available);
        }
        
        @Test
        @DisplayName("Should return false for empty seat number")
        void testEmptySeatNumber() {
            flight.setCapacity(100);
            boolean available = SeatManager.isSeatAvailable(flight, "", fbs);
            
            assertFalse(available);
        }
        
        @Test
        @DisplayName("Should return false for seat beyond capacity")
        void testSeatBeyondCapacity() {
            flight.setCapacity(6);
            boolean available = SeatManager.isSeatAvailable(flight, "10A", fbs);
            
            assertFalse(available);
        }
        
        @Test
        @DisplayName("Should return true for cancelled booking seat")
        void testCancelledBookingSeatAvailable() throws FlightBookingSystemException {
            flight.setCapacity(100);
            
            Booking booking = new Booking(customer1, flight, LocalDate.now());
            booking.setSeatNumber("1A");
            booking.setCancelled(true);
            customer1.addBooking(booking);
            flight.addPassenger(customer1);
            
            boolean available = SeatManager.isSeatAvailable(flight, "1A", fbs);
            
            assertTrue(available);
        }
        
        @Test
        @DisplayName("Should handle various seat formats correctly")
        void testVariousSeatFormats() {
            flight.setCapacity(100);
            
            assertTrue(SeatManager.isSeatAvailable(flight, "1A", fbs));
            assertTrue(SeatManager.isSeatAvailable(flight, "10F", fbs));
            assertTrue(SeatManager.isSeatAvailable(flight, "5C", fbs));
        }
        
        @Test
        @DisplayName("Should return false for invalid seat format")
        void testInvalidSeatFormat() {
            flight.setCapacity(100);
            
            // These are invalid formats not in generated seats
            assertFalse(SeatManager.isSeatAvailable(flight, "1Z", fbs));
            assertFalse(SeatManager.isSeatAvailable(flight, "ABC", fbs));
        }
        
        @Test
        @DisplayName("Should handle multiple bookings correctly")
        void testMultipleBookings() throws FlightBookingSystemException {
            flight.setCapacity(100);
            
            // Book multiple seats
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("1A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("1B");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            // Check availability
            assertFalse(SeatManager.isSeatAvailable(flight, "1A", fbs));
            assertFalse(SeatManager.isSeatAvailable(flight, "1B", fbs));
            assertTrue(SeatManager.isSeatAvailable(flight, "1C", fbs));
        }
    }
    
    @Nested
    @DisplayName("Get Seat Category Tests")
    class GetSeatCategoryTests {
        
        @Test
        @DisplayName("Should return Economy for rows 1-10")
        void testEconomySeats() {
            assertEquals("Economy", SeatManager.getSeatCategory("1A"));
            assertEquals("Economy", SeatManager.getSeatCategory("5C"));
            assertEquals("Economy", SeatManager.getSeatCategory("10F"));
        }
        
        @Test
        @DisplayName("Should return Business for rows 11-15")
        void testBusinessSeats() {
            assertEquals("Business", SeatManager.getSeatCategory("11A"));
            assertEquals("Business", SeatManager.getSeatCategory("13C"));
            assertEquals("Business", SeatManager.getSeatCategory("15F"));
        }
        
        @Test
        @DisplayName("Should return First Class for rows 16+")
        void testFirstClassSeats() {
            assertEquals("First Class", SeatManager.getSeatCategory("16A"));
            assertEquals("First Class", SeatManager.getSeatCategory("20C"));
            assertEquals("First Class", SeatManager.getSeatCategory("50F"));
        }
        
        @Test
        @DisplayName("Should return Unknown for null seat number")
        void testNullSeatCategory() {
            assertEquals("Unknown", SeatManager.getSeatCategory(null));
        }
        
        @Test
        @DisplayName("Should return Unknown for empty seat number")
        void testEmptySeatCategory() {
            assertEquals("Unknown", SeatManager.getSeatCategory(""));
        }
        
        @Test
        @DisplayName("Should return Unknown for invalid seat format")
        void testInvalidSeatCategory() {
            assertEquals("Unknown", SeatManager.getSeatCategory("ABC"));
            assertEquals("Unknown", SeatManager.getSeatCategory("INVALID"));
            assertEquals("Unknown", SeatManager.getSeatCategory("@#$"));
        }
        
        @Test
        @DisplayName("Should handle boundary values correctly")
        void testBoundaryCategories() {
            assertEquals("Economy", SeatManager.getSeatCategory("10A"));
            assertEquals("Business", SeatManager.getSeatCategory("11A"));
            assertEquals("Business", SeatManager.getSeatCategory("15A"));
            assertEquals("First Class", SeatManager.getSeatCategory("16A"));
        }
        
        @Test
        @DisplayName("Should handle all seat letters correctly")
        void testAllSeatLetters() {
            assertEquals("Economy", SeatManager.getSeatCategory("5A"));
            assertEquals("Economy", SeatManager.getSeatCategory("5B"));
            assertEquals("Economy", SeatManager.getSeatCategory("5C"));
            assertEquals("Economy", SeatManager.getSeatCategory("5D"));
            assertEquals("Economy", SeatManager.getSeatCategory("5E"));
            assertEquals("Economy", SeatManager.getSeatCategory("5F"));
        }
    }
    
    @Nested
    @DisplayName("Format Seat Display Tests")
    class FormatSeatDisplayTests {
        
        @Test
        @DisplayName("Should format Economy seats correctly")
        void testFormatEconomySeat() {
            assertEquals("1A (Economy)", SeatManager.formatSeatDisplay("1A"));
            assertEquals("10F (Economy)", SeatManager.formatSeatDisplay("10F"));
        }
        
        @Test
        @DisplayName("Should format Business seats correctly")
        void testFormatBusinessSeat() {
            assertEquals("12A (Business)", SeatManager.formatSeatDisplay("12A"));
            assertEquals("15C (Business)", SeatManager.formatSeatDisplay("15C"));
        }
        
        @Test
        @DisplayName("Should format First Class seats correctly")
        void testFormatFirstClassSeat() {
            assertEquals("20A (First Class)", SeatManager.formatSeatDisplay("20A"));
            assertEquals("50F (First Class)", SeatManager.formatSeatDisplay("50F"));
        }
        
        @Test
        @DisplayName("Should return 'Not assigned' for null seat")
        void testFormatNullSeat() {
            assertEquals("Not assigned", SeatManager.formatSeatDisplay(null));
        }
        
        @Test
        @DisplayName("Should return 'Not assigned' for empty seat")
        void testFormatEmptySeat() {
            assertEquals("Not assigned", SeatManager.formatSeatDisplay(""));
        }
        
        @Test
        @DisplayName("Should format invalid seats with Unknown category")
        void testFormatInvalidSeat() {
            assertEquals("ABC (Unknown)", SeatManager.formatSeatDisplay("ABC"));
        }
        
        @Test
        @DisplayName("Should handle various seat numbers consistently")
        void testFormatVariousSeats() {
            assertEquals("1A (Economy)", SeatManager.formatSeatDisplay("1A"));
            assertEquals("11B (Business)", SeatManager.formatSeatDisplay("11B"));
            assertEquals("16C (First Class)", SeatManager.formatSeatDisplay("16C"));
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should handle complete booking workflow")
        void testCompleteBookingWorkflow() throws FlightBookingSystemException {
            flight.setCapacity(12);
            
            // 1. Get all available seats initially
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            assertEquals(12, availableSeats.size());
            
            // 2. Book a seat
            String selectedSeat = "1A";
            assertTrue(SeatManager.isSeatAvailable(flight, selectedSeat, fbs));
            
            Booking booking = new Booking(customer1, flight, LocalDate.now());
            booking.setSeatNumber(selectedSeat);
            customer1.addBooking(booking);
            flight.addPassenger(customer1);
            
            // 3. Verify seat is no longer available
            assertFalse(SeatManager.isSeatAvailable(flight, selectedSeat, fbs));
            
            // 4. Check updated available seats
            availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            assertEquals(11, availableSeats.size());
            assertFalse(availableSeats.contains(selectedSeat));
        }
        
        @Test
        @DisplayName("Should handle multiple customers booking seats")
        void testMultipleCustomerBookings() throws FlightBookingSystemException {
            flight.setCapacity(20);
            
            // Customer 1 books Economy
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("5A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            // Customer 2 books Business
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("12B");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            // Customer 3 books First Class
            Booking booking3 = new Booking(customer3, flight, LocalDate.now());
            booking3.setSeatNumber("20C");
            customer3.addBooking(booking3);
            flight.addPassenger(customer3);
            
            // Verify all bookings
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            assertEquals(3, bookedSeats.size());
            assertTrue(bookedSeats.contains("5A"));
            assertTrue(bookedSeats.contains("12B"));
            assertTrue(bookedSeats.contains("20C"));
            
            // Verify seat categories
            assertEquals("Economy", SeatManager.getSeatCategory("5A"));
            assertEquals("Business", SeatManager.getSeatCategory("12B"));
            assertEquals("First Class", SeatManager.getSeatCategory("20C"));
        }
        
        @Test
        @DisplayName("Should handle cancellation and rebooking")
        void testCancellationAndRebooking() throws FlightBookingSystemException {
            // Create a completely fresh FBS and flight for this test
            FlightBookingSystem testFbs = new FlightBookingSystem();
            Flight testFlight = new Flight(98, "CANCEL123", "LHR", "JFK", LocalDate.now().plusDays(20));
            testFlight.setCapacity(20);
            testFbs.addFlight(testFlight);
            
            // Create fresh customers
            Customer c1 = new Customer(201, "Cancel User 1", "5555555555", "cancel1@test.com");
            Customer c2 = new Customer(202, "Cancel User 2", "6666666666", "cancel2@test.com");
            
            testFbs.addCustomer(c1);
            testFbs.addCustomer(c2);
            
            // Step 1: Create booking for c1
            Booking booking1 = new Booking(c1, testFlight, LocalDate.now());
            booking1.setSeatNumber("10A");
            c1.addBooking(booking1);
            testFlight.addPassenger(c1);
            
            // Verify seat is booked
            Set<String> bookedSeats = SeatManager.getBookedSeats(testFlight, testFbs);
            assertEquals(1, bookedSeats.size(), "Should have 1 booked seat initially");
            assertTrue(bookedSeats.contains("10A"), "10A should be in booked seats");
            
            // Step 2: Cancel c1's booking
            booking1.setCancelled(true);
            
            // Step 3: Verify cancelled booking is not in booked seats
            bookedSeats = SeatManager.getBookedSeats(testFlight, testFbs);
            assertEquals(0, bookedSeats.size(), "Cancelled bookings should not count as booked");
            assertFalse(bookedSeats.contains("10A"), "10A should not be in booked seats after cancellation");
            
            // Step 4: Create new booking for c2 on same seat
            Booking booking2 = new Booking(c2, testFlight, LocalDate.now());
            booking2.setSeatNumber("10A");
            c2.addBooking(booking2);
            testFlight.addPassenger(c2);
            
            // Step 5: Verify c2's booking shows up
            bookedSeats = SeatManager.getBookedSeats(testFlight, testFbs);
            assertEquals(1, bookedSeats.size(), "Should have 1 active booking from c2");
            assertTrue(bookedSeats.contains("10A"), "10A should be booked by c2");
            
            // Verify both customers exist but only c2's booking is active
            assertEquals(2, testFbs.getCustomers().size(), "Should have 2 customers");
            assertEquals(1, c1.getBookings().size(), "c1 should have 1 booking (cancelled)");
            assertEquals(1, c2.getBookings().size(), "c2 should have 1 booking (active)");
            assertTrue(c1.getBookings().get(0).isCancelled(), "c1's booking should be cancelled");
            assertFalse(c2.getBookings().get(0).isCancelled(), "c2's booking should not be cancelled");
        }
        
        @Test
        @DisplayName("Should maintain seat consistency across operations")
        void testSeatConsistency() throws FlightBookingSystemException {
            // Create a completely fresh FBS and flight for this test
            FlightBookingSystem testFbs = new FlightBookingSystem();
            Flight testFlight = new Flight(99, "TEST123", "LHR", "CDG", LocalDate.now().plusDays(30));
            testFlight.setCapacity(30);
            testFbs.addFlight(testFlight);
            
            // Create 4 fresh customers
            Customer c1 = new Customer(101, "Test User 1", "1111111111", "test1@test.com");
            Customer c2 = new Customer(102, "Test User 2", "2222222222", "test2@test.com");
            Customer c3 = new Customer(103, "Test User 3", "3333333333", "test3@test.com");
            Customer c4 = new Customer(104, "Test User 4", "4444444444", "test4@test.com");
            
            testFbs.addCustomer(c1);
            testFbs.addCustomer(c2);
            testFbs.addCustomer(c3);
            testFbs.addCustomer(c4);
            
            // Each customer books 1 seat - using valid seats for 30-seat capacity (rows 1-5)
            Booking booking1 = new Booking(c1, testFlight, LocalDate.now());
            booking1.setSeatNumber("1A");
            c1.addBooking(booking1);
            testFlight.addPassenger(c1);
            
            Booking booking2 = new Booking(c2, testFlight, LocalDate.now());
            booking2.setSeatNumber("2C");
            c2.addBooking(booking2);
            testFlight.addPassenger(c2);
            
            Booking booking3 = new Booking(c3, testFlight, LocalDate.now());
            booking3.setSeatNumber("3B");
            c3.addBooking(booking3);
            testFlight.addPassenger(c3);
            
            Booking booking4 = new Booking(c4, testFlight, LocalDate.now());
            booking4.setSeatNumber("4F");
            c4.addBooking(booking4);
            testFlight.addPassenger(c4);
            
            // Verify all operations return consistent results
            Set<String> bookedSeats = SeatManager.getBookedSeats(testFlight, testFbs);
            List<String> availableSeats = SeatManager.getAvailableSeats(testFlight, testFbs);
            
            assertEquals(4, bookedSeats.size(), "Expected 4 booked seats");
            assertEquals(26, availableSeats.size(), "Expected 26 available seats");
            
            String[] seats = {"1A", "2C", "3B", "4F"};
            for (String seat : seats) {
                assertTrue(bookedSeats.contains(seat), "Booked seats should contain " + seat);
                assertFalse(availableSeats.contains(seat), "Available seats should not contain " + seat);
                assertFalse(SeatManager.isSeatAvailable(testFlight, seat, testFbs), seat + " should not be available");
            }
        }
        
        @Test
        @DisplayName("Should handle full flight scenario")
        void testFullFlightScenario() throws FlightBookingSystemException {
            flight.setCapacity(6);
            List<String> allSeats = SeatManager.generateAllSeats(flight);
            
            // Customer 1 books seats 1A, 1B
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber(allSeats.get(0));
            customer1.addBooking(booking1);
            
            Booking booking2 = new Booking(customer1, flight, LocalDate.now());
            booking2.setSeatNumber(allSeats.get(1));
            customer1.addBooking(booking2);
            flight.addPassenger(customer1);
            
            // Customer 2 books seats 1C, 1D
            Booking booking3 = new Booking(customer2, flight, LocalDate.now());
            booking3.setSeatNumber(allSeats.get(2));
            customer2.addBooking(booking3);
            
            Booking booking4 = new Booking(customer2, flight, LocalDate.now());
            booking4.setSeatNumber(allSeats.get(3));
            customer2.addBooking(booking4);
            flight.addPassenger(customer2);
            
            // Customer 3 books seats 1E, 1F
            Booking booking5 = new Booking(customer3, flight, LocalDate.now());
            booking5.setSeatNumber(allSeats.get(4));
            customer3.addBooking(booking5);
            
            Booking booking6 = new Booking(customer3, flight, LocalDate.now());
            booking6.setSeatNumber(allSeats.get(5));
            customer3.addBooking(booking6);
            flight.addPassenger(customer3);
            
            // Verify flight is full
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, fbs);
            assertTrue(availableSeats.isEmpty());
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            assertEquals(6, bookedSeats.size());
            
            // No seat should be available
            for (String seat : allSeats) {
                assertFalse(SeatManager.isSeatAvailable(flight, seat, fbs));
            }
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle flight with capacity 1")
        void testMinimumCapacity() {
            flight.setCapacity(1);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(1, seats.size());
            assertEquals("1A", seats.get(0));
            assertTrue(SeatManager.isSeatAvailable(flight, "1A", fbs));
        }
        
        @Test
        @DisplayName("Should handle very large flight capacity")
        void testLargeCapacity() {
            flight.setCapacity(500);
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(500, seats.size());
            assertTrue(seats.contains("1A"));
            assertTrue(seats.contains("50F"));
        }
        
        @Test
        @DisplayName("Should handle seat numbers with different row lengths")
        void testVariableRowNumbers() {
            assertEquals("Economy", SeatManager.getSeatCategory("1A"));
            assertEquals("Economy", SeatManager.getSeatCategory("9B"));
            assertEquals("Business", SeatManager.getSeatCategory("11C"));
            assertEquals("First Class", SeatManager.getSeatCategory("100D"));
        }
        
        @Test
        @DisplayName("Should handle customer with multiple bookings on same flight")
        void testMultipleBookingsSameCustomer() throws FlightBookingSystemException {
            flight.setCapacity(20);
            
            // Customer 1 books two seats (maybe for family)
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("5A");
            customer1.addBooking(booking1);
            
            Booking booking2 = new Booking(customer1, flight, LocalDate.now());
            booking2.setSeatNumber("5B");
            customer1.addBooking(booking2);
            
            flight.addPassenger(customer1);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            assertTrue(bookedSeats.contains("5A"));
            assertTrue(bookedSeats.contains("5B"));
        }
        
        @Test
        @DisplayName("Should handle whitespace in seat numbers")
        void testWhitespaceInSeatNumbers() {
            // These should be treated as different from valid seats
            assertNotEquals("1A (Economy)", SeatManager.formatSeatDisplay(" 1A"));
            assertNotEquals("1A (Economy)", SeatManager.formatSeatDisplay("1A "));
        }
        
        @Test
        @DisplayName("Should maintain TreeSet ordering for booked seats")
        void testBookedSeatsOrdering() throws FlightBookingSystemException {
            flight.setCapacity(20);
            
            // Book seats in random order - using different customers for each booking
            Booking booking1 = new Booking(customer1, flight, LocalDate.now());
            booking1.setSeatNumber("15A");
            customer1.addBooking(booking1);
            flight.addPassenger(customer1);
            
            Booking booking2 = new Booking(customer2, flight, LocalDate.now());
            booking2.setSeatNumber("1B");
            customer2.addBooking(booking2);
            flight.addPassenger(customer2);
            
            Booking booking3 = new Booking(customer3, flight, LocalDate.now());
            booking3.setSeatNumber("10C");
            customer3.addBooking(booking3);
            flight.addPassenger(customer3);
            
            // Create additional customer for 4th booking
            Customer customer4 = new Customer(4, "Alice Brown", "4445556666", "alice@example.com");
            fbs.addCustomer(customer4);
            
            Booking booking4 = new Booking(customer4, flight, LocalDate.now());
            booking4.setSeatNumber("5D");
            customer4.addBooking(booking4);
            flight.addPassenger(customer4);
            
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            
            // TreeSet should maintain natural ordering
            assertNotNull(bookedSeats);
            assertEquals(4, bookedSeats.size());
            assertTrue(bookedSeats.contains("15A"));
            assertTrue(bookedSeats.contains("1B"));
            assertTrue(bookedSeats.contains("10C"));
            assertTrue(bookedSeats.contains("5D"));
        }
        
        @Test
        @DisplayName("Should handle booking without adding passenger to flight")
        void testBookingWithoutAddingPassenger() {
            flight.setCapacity(20);
            
            // Create booking but don't add to flight
            Booking booking = new Booking(customer1, flight, LocalDate.now());
            booking.setSeatNumber("5A");
            customer1.addBooking(booking);
            // Note: not calling flight.addPassenger()
            
            // Seat should still be detected as booked
            Set<String> bookedSeats = SeatManager.getBookedSeats(flight, fbs);
            assertTrue(bookedSeats.contains("5A"));
        }
        
        @Test
        @DisplayName("Should handle zero-length seat generation")
        void testCapacityBoundaries() {
            // Test what happens with edge case capacities
            flight.setCapacity(7); // One row + 1 seat
            List<String> seats = SeatManager.generateAllSeats(flight);
            
            assertEquals(7, seats.size());
            assertEquals("1A", seats.get(0));
            assertEquals("2A", seats.get(6));
        }
    }
}