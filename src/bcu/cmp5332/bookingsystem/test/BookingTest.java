package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for Booking
 * Tests all methods and functionality of the Booking class
 * 
 * @author Tejindra Rai
 */
class BookingTest {
    
    private Customer customer;
    private Flight flight;
    private Booking booking;
    private LocalDate bookingDate;
    private LocalDate futureDate;
    
    @BeforeEach
    void setUp() {
        // Set up test data
        customer = new Customer(1, "John Doe", "1234567890", "john.doe@example.com");
        futureDate = LocalDate.now().plusDays(30);
        flight = new Flight(1, "BA123", "LHR", "CDG", futureDate);
        bookingDate = LocalDate.now();
        booking = new Booking(customer, flight, bookingDate);
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create booking with correct customer, flight, and date")
        void testConstructorBasicDetails() {
            assertEquals(customer, booking.getCustomer());
            assertEquals(flight, booking.getFlight());
            assertEquals(bookingDate, booking.getBookingDate());
        }
        
        @Test
        @DisplayName("Should initialize with default values")
        void testConstructorDefaultValues() {
            assertFalse(booking.isCancelled());
            assertEquals(0.0, booking.getCancellationFee());
            assertEquals(0.0, booking.getBookingPrice());
            assertEquals("None", booking.getMealPreference());
            assertNull(booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should create booking with valid parameters")
        void testValidConstructor() {
            Customer testCustomer = new Customer(2, "Jane Smith", "9876543210", "jane@example.com");
            Flight testFlight = new Flight(2, "BA456", "CDG", "JFK", futureDate.plusDays(5));
            LocalDate testDate = LocalDate.now().minusDays(1);
            
            Booking testBooking = new Booking(testCustomer, testFlight, testDate);
            
            assertNotNull(testBooking);
            assertEquals(testCustomer, testBooking.getCustomer());
            assertEquals(testFlight, testBooking.getFlight());
            assertEquals(testDate, testBooking.getBookingDate());
        }
    }
    
    @Nested
    @DisplayName("Customer Getter and Setter Tests")
    class CustomerTests {
        
        @Test
        @DisplayName("Should get customer correctly")
        void testGetCustomer() {
            assertEquals(customer, booking.getCustomer());
            assertEquals("John Doe", booking.getCustomer().getName());
        }
        
        @Test
        @DisplayName("Should set customer correctly")
        void testSetCustomer() {
            Customer newCustomer = new Customer(2, "Jane Smith", "9876543210", "jane@example.com");
            booking.setCustomer(newCustomer);
            
            assertEquals(newCustomer, booking.getCustomer());
            assertEquals("Jane Smith", booking.getCustomer().getName());
        }
        
        @Test
        @DisplayName("Should handle null customer")
        void testSetNullCustomer() {
            booking.setCustomer(null);
            assertNull(booking.getCustomer());
        }
    }
    
    @Nested
    @DisplayName("Flight Getter and Setter Tests")
    class FlightTests {
        
        @Test
        @DisplayName("Should get flight correctly")
        void testGetFlight() {
            assertEquals(flight, booking.getFlight());
            assertEquals("BA123", booking.getFlight().getFlightNumber());
        }
        
        @Test
        @DisplayName("Should set flight correctly")
        void testSetFlight() {
            Flight newFlight = new Flight(2, "BA456", "CDG", "JFK", futureDate.plusDays(5));
            booking.setFlight(newFlight);
            
            assertEquals(newFlight, booking.getFlight());
            assertEquals("BA456", booking.getFlight().getFlightNumber());
        }
        
        @Test
        @DisplayName("Should handle null flight")
        void testSetNullFlight() {
            booking.setFlight(null);
            assertNull(booking.getFlight());
        }
    }
    
    @Nested
    @DisplayName("Booking Date Tests")
    class BookingDateTests {
        
        @Test
        @DisplayName("Should get booking date correctly")
        void testGetBookingDate() {
            assertEquals(bookingDate, booking.getBookingDate());
        }
        
        @Test
        @DisplayName("Should set booking date correctly")
        void testSetBookingDate() {
            LocalDate newDate = LocalDate.now().minusDays(5);
            booking.setBookingDate(newDate);
            
            assertEquals(newDate, booking.getBookingDate());
        }
        
        @Test
        @DisplayName("Should handle future booking dates")
        void testFutureBookingDate() {
            LocalDate futureBookingDate = LocalDate.now().plusDays(10);
            booking.setBookingDate(futureBookingDate);
            
            assertEquals(futureBookingDate, booking.getBookingDate());
        }
        
        @Test
        @DisplayName("Should handle past booking dates")
        void testPastBookingDate() {
            LocalDate pastDate = LocalDate.now().minusYears(1);
            booking.setBookingDate(pastDate);
            
            assertEquals(pastDate, booking.getBookingDate());
        }
    }
    
    @Nested
    @DisplayName("Cancellation Status Tests")
    class CancellationTests {
        
        @Test
        @DisplayName("Should not be cancelled by default")
        void testDefaultNotCancelled() {
            assertFalse(booking.isCancelled());
        }
        
        @Test
        @DisplayName("Should set cancelled status to true")
        void testSetCancelledTrue() {
            booking.setCancelled(true);
            assertTrue(booking.isCancelled());
        }
        
        @Test
        @DisplayName("Should set cancelled status to false")
        void testSetCancelledFalse() {
            booking.setCancelled(true);
            assertTrue(booking.isCancelled());
            
            booking.setCancelled(false);
            assertFalse(booking.isCancelled());
        }
        
        @Test
        @DisplayName("Should toggle cancelled status multiple times")
        void testToggleCancelled() {
            assertFalse(booking.isCancelled());
            
            booking.setCancelled(true);
            assertTrue(booking.isCancelled());
            
            booking.setCancelled(false);
            assertFalse(booking.isCancelled());
            
            booking.setCancelled(true);
            assertTrue(booking.isCancelled());
        }
    }
    
    @Nested
    @DisplayName("Booking Price Tests")
    class BookingPriceTests {
        
        @Test
        @DisplayName("Should have zero price by default")
        void testDefaultPrice() {
            assertEquals(0.0, booking.getBookingPrice());
        }
        
        @Test
        @DisplayName("Should set and get booking price correctly")
        void testSetBookingPrice() {
            booking.setBookingPrice(150.50);
            assertEquals(150.50, booking.getBookingPrice());
        }
        
        @Test
        @DisplayName("Should handle large price values")
        void testLargePrice() {
            booking.setBookingPrice(9999.99);
            assertEquals(9999.99, booking.getBookingPrice());
        }
        
        @Test
        @DisplayName("Should handle small price values")
        void testSmallPrice() {
            booking.setBookingPrice(0.01);
            assertEquals(0.01, booking.getBookingPrice());
        }
        
        @Test
        @DisplayName("Should handle zero price")
        void testZeroPrice() {
            booking.setBookingPrice(0.0);
            assertEquals(0.0, booking.getBookingPrice());
        }
        
        @Test
        @DisplayName("Should update price multiple times")
        void testUpdatePrice() {
            booking.setBookingPrice(100.00);
            assertEquals(100.00, booking.getBookingPrice());
            
            booking.setBookingPrice(200.00);
            assertEquals(200.00, booking.getBookingPrice());
            
            booking.setBookingPrice(150.00);
            assertEquals(150.00, booking.getBookingPrice());
        }
    }
    
    @Nested
    @DisplayName("Cancellation Fee Tests")
    class CancellationFeeTests {
        
        @Test
        @DisplayName("Should have zero cancellation fee by default")
        void testDefaultCancellationFee() {
            assertEquals(0.0, booking.getCancellationFee());
        }
        
        @Test
        @DisplayName("Should set and get cancellation fee correctly")
        void testSetCancellationFee() {
            booking.setCancellationFee(25.00);
            assertEquals(25.00, booking.getCancellationFee());
        }
        
        @Test
        @DisplayName("Should handle large cancellation fees")
        void testLargeCancellationFee() {
            booking.setCancellationFee(500.00);
            assertEquals(500.00, booking.getCancellationFee());
        }
        
        @Test
        @DisplayName("Should handle zero cancellation fee")
        void testZeroCancellationFee() {
            booking.setCancellationFee(0.0);
            assertEquals(0.0, booking.getCancellationFee());
        }
        
        @Test
        @DisplayName("Should update cancellation fee")
        void testUpdateCancellationFee() {
            booking.setCancellationFee(25.00);
            assertEquals(25.00, booking.getCancellationFee());
            
            booking.setCancellationFee(50.00);
            assertEquals(50.00, booking.getCancellationFee());
        }
    }
    
    @Nested
    @DisplayName("Meal Preference Tests")
    class MealPreferenceTests {
        
        @Test
        @DisplayName("Should have 'None' as default meal preference")
        void testDefaultMealPreference() {
            assertEquals("None", booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should set vegetarian meal preference")
        void testSetVegetarianMeal() {
            booking.setMealPreference("Vegetarian");
            assertEquals("Vegetarian", booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should set non-vegetarian meal preference")
        void testSetNonVegetarianMeal() {
            booking.setMealPreference("Non-Vegetarian");
            assertEquals("Non-Vegetarian", booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should set vegan meal preference")
        void testSetVeganMeal() {
            booking.setMealPreference("Vegan");
            assertEquals("Vegan", booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should handle empty meal preference")
        void testEmptyMealPreference() {
            booking.setMealPreference("");
            assertEquals("", booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should handle null meal preference")
        void testNullMealPreference() {
            booking.setMealPreference(null);
            assertNull(booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should update meal preference")
        void testUpdateMealPreference() {
            booking.setMealPreference("Vegetarian");
            assertEquals("Vegetarian", booking.getMealPreference());
            
            booking.setMealPreference("Vegan");
            assertEquals("Vegan", booking.getMealPreference());
        }
    }
    
    @Nested
    @DisplayName("Seat Number Tests")
    class SeatNumberTests {
        
        @Test
        @DisplayName("Should have null seat number by default")
        void testDefaultSeatNumber() {
            assertNull(booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should set and get seat number correctly")
        void testSetSeatNumber() {
            booking.setSeatNumber("12A");
            assertEquals("12A", booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should handle various seat number formats")
        void testVariousSeatFormats() {
            booking.setSeatNumber("1A");
            assertEquals("1A", booking.getSeatNumber());
            
            booking.setSeatNumber("25F");
            assertEquals("25F", booking.getSeatNumber());
            
            booking.setSeatNumber("10B");
            assertEquals("10B", booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should handle window seat")
        void testWindowSeat() {
            booking.setSeatNumber("15A");
            assertEquals("15A", booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should handle aisle seat")
        void testAisleSeat() {
            booking.setSeatNumber("15C");
            assertEquals("15C", booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should set seat number to null")
        void testSetNullSeatNumber() {
            booking.setSeatNumber("12A");
            assertEquals("12A", booking.getSeatNumber());
            
            booking.setSeatNumber(null);
            assertNull(booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should update seat number")
        void testUpdateSeatNumber() {
            booking.setSeatNumber("10A");
            assertEquals("10A", booking.getSeatNumber());
            
            booking.setSeatNumber("15B");
            assertEquals("15B", booking.getSeatNumber());
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should maintain data integrity across multiple operations")
        void testDataIntegrity() {
            // Set all booking properties
            booking.setBookingPrice(250.00);
            booking.setCancellationFee(25.00);
            booking.setMealPreference("Vegetarian");
            booking.setSeatNumber("12A");
            booking.setCancelled(false);
            
            // Verify all data is preserved
            assertEquals(customer, booking.getCustomer());
            assertEquals(flight, booking.getFlight());
            assertEquals(bookingDate, booking.getBookingDate());
            assertEquals(250.00, booking.getBookingPrice());
            assertEquals(25.00, booking.getCancellationFee());
            assertEquals("Vegetarian", booking.getMealPreference());
            assertEquals("12A", booking.getSeatNumber());
            assertFalse(booking.isCancelled());
        }
        
        @Test
        @DisplayName("Should handle complete booking lifecycle")
        void testBookingLifecycle() {
            // 1. Create booking
            assertFalse(booking.isCancelled());
            assertEquals(0.0, booking.getBookingPrice());
            
            // 2. Set booking details
            booking.setBookingPrice(200.00);
            booking.setMealPreference("Vegan");
            booking.setSeatNumber("15C");
            
            assertEquals(200.00, booking.getBookingPrice());
            assertEquals("Vegan", booking.getMealPreference());
            assertEquals("15C", booking.getSeatNumber());
            
            // 3. Cancel booking
            booking.setCancelled(true);
            booking.setCancellationFee(50.00);
            
            assertTrue(booking.isCancelled());
            assertEquals(50.00, booking.getCancellationFee());
            
            // 4. All other data should remain
            assertEquals(200.00, booking.getBookingPrice());
            assertEquals("Vegan", booking.getMealPreference());
            assertEquals("15C", booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should handle booking with customer preferences")
        void testBookingWithCustomerPreferences() {
            // Customer has preferences
            customer.setMealPreference("Vegetarian");
            customer.setHasChildren(true);
            
            // Set booking details
            booking.setBookingPrice(300.00);
            booking.setMealPreference("Vegetarian");
            booking.setSeatNumber("20A");
            
            // Verify customer preferences don't affect booking
            assertEquals("Vegetarian", booking.getMealPreference());
            assertEquals(customer, booking.getCustomer());
            assertEquals("Vegetarian", customer.getMealPreference());
        }
        
        @Test
        @DisplayName("Should handle booking modification scenario")
        void testBookingModification() {
            // Initial booking
            booking.setBookingPrice(150.00);
            booking.setMealPreference("Non-Vegetarian");
            booking.setSeatNumber("10A");
            
            // Customer changes preferences
            booking.setMealPreference("Vegetarian");
            booking.setSeatNumber("12B");
            
            assertEquals("Vegetarian", booking.getMealPreference());
            assertEquals("12B", booking.getSeatNumber());
            assertEquals(150.00, booking.getBookingPrice()); // Price unchanged
        }
        
        @Test
        @DisplayName("Should handle cancellation with refund scenario")
        void testCancellationWithRefund() {
            booking.setBookingPrice(500.00);
            booking.setCancellationFee(100.00);
            booking.setCancelled(true);
            
            assertTrue(booking.isCancelled());
            assertEquals(500.00, booking.getBookingPrice());
            assertEquals(100.00, booking.getCancellationFee());
            
            // Calculate refund (price - cancellation fee)
            double expectedRefund = 500.00 - 100.00;
            assertEquals(400.00, expectedRefund);
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle booking with same customer and flight")
        void testSameCustomerAndFlight() {
            Booking booking1 = new Booking(customer, flight, LocalDate.now());
            Booking booking2 = new Booking(customer, flight, LocalDate.now().plusDays(1));
            
            assertEquals(customer, booking1.getCustomer());
            assertEquals(customer, booking2.getCustomer());
            assertEquals(flight, booking1.getFlight());
            assertEquals(flight, booking2.getFlight());
            assertNotEquals(booking1.getBookingDate(), booking2.getBookingDate());
        }
        
        @Test
        @DisplayName("Should handle negative price values")
        void testNegativePrice() {
            booking.setBookingPrice(-100.00);
            assertEquals(-100.00, booking.getBookingPrice());
        }
        
        @Test
        @DisplayName("Should handle negative cancellation fee")
        void testNegativeCancellationFee() {
            booking.setCancellationFee(-50.00);
            assertEquals(-50.00, booking.getCancellationFee());
        }
        
        @Test
        @DisplayName("Should handle very large numbers")
        void testVeryLargeNumbers() {
            booking.setBookingPrice(999999.99);
            booking.setCancellationFee(99999.99);
            
            assertEquals(999999.99, booking.getBookingPrice());
            assertEquals(99999.99, booking.getCancellationFee());
        }
        
        @Test
        @DisplayName("Should handle special characters in seat number")
        void testSpecialCharactersSeatNumber() {
            booking.setSeatNumber("12A-Window");
            assertEquals("12A-Window", booking.getSeatNumber());
        }
        
        @Test
        @DisplayName("Should handle special characters in meal preference")
        void testSpecialCharactersMealPreference() {
            booking.setMealPreference("Veg + Gluten-Free");
            assertEquals("Veg + Gluten-Free", booking.getMealPreference());
        }
        
        @Test
        @DisplayName("Should handle booking on same day as flight")
        void testSameDayBooking() {
            LocalDate flightDate = futureDate;
            booking.setBookingDate(flightDate);
            
            assertEquals(flightDate, booking.getBookingDate());
            assertEquals(flightDate, flight.getDepartureDate());
        }
    }
}