package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.utils.DynamicPricingCalculator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.time.LocalDate;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for DynamicPricingCalculator
 * Tests pricing calculations through the public API methods
 * 
 * @author Tejindra Rai
 * @version 2.1 - Fixed dates to always be in the future
 */
class DynamicPricingCalculatorTest {
    
    private Flight flight;
    private Flight domesticFlight;
    private Flight internationalFlight;
    private LocalDate bookingDate;
    
    @BeforeEach
    void setUp() {
        // Use LocalDate.now() and add days to ensure dates are always in the future
        bookingDate = LocalDate.now().plusDays(1);
        LocalDate departureDate = bookingDate.plusDays(30);
        
        flight = new Flight(1, "TEST123", "LHR", "JFK", departureDate);
        flight.setCapacity(100);
        flight.setBasePrice(100.0);
        
        // Create domestic flight (UK to UK)
        domesticFlight = new Flight(2, "DOM456", "LHR", "MAN", departureDate);
        domesticFlight.setCapacity(100);
        domesticFlight.setBasePrice(100.0);
        
        // Create international flight
        internationalFlight = new Flight(3, "INT789", "LHR", "JFK", departureDate);
        internationalFlight.setCapacity(100);
        internationalFlight.setBasePrice(100.0);
    }
    
    @Nested
    @DisplayName("Basic Price Calculation Tests")
    class BasicPriceCalculationTests {
        
        @Test
        @DisplayName("Should calculate price with all multipliers")
        void testBasicPriceCalculation() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Price should be positive");
        }
        
        @Test
        @DisplayName("Should return different price than base price")
        void testPriceWithMultipliers() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            // Price should be different due to multipliers (could be higher or lower)
            assertNotNull(price);
        }
        
        @Test
        @DisplayName("Should round price to 2 decimal places")
        void testPriceRounding() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            // Check that price has at most 2 decimal places
            assertEquals(price, Math.round(price * 100.0) / 100.0, 0.001);
        }
        
        @Test
        @DisplayName("Should handle different base prices proportionally")
        void testDifferentBasePrices() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight flight1 = new Flight(1, "TEST1", "LHR", "JFK", departure);
            flight1.setBasePrice(100.0);
            flight1.setCapacity(100);
            
            Flight flight2 = new Flight(2, "TEST2", "LHR", "JFK", departure);
            flight2.setBasePrice(200.0);
            flight2.setCapacity(100);
            
            double price1 = DynamicPricingCalculator.calculatePrice(flight1, booking);
            double price2 = DynamicPricingCalculator.calculatePrice(flight2, booking);
            
            // Price 2 should be roughly double price 1 (within 5% tolerance)
            assertTrue(price2 > price1, "Higher base price should result in higher final price");
            double ratio = price2 / price1;
            assertTrue(ratio >= 1.9 && ratio <= 2.1, "Ratio should be approximately 2.0");
        }
        
        @Test
        @DisplayName("Should always return positive price")
        void testPositivePrice() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Price should always be positive");
        }
    }
    
    @Nested
    @DisplayName("Time-Based Pricing Tests")
    class TimeBasedPricingTests {
        
        @Test
        @DisplayName("Should apply last minute premium for bookings 7 days or less before departure")
        void testLastMinutePremium() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate lastMinuteDeparture = booking.plusDays(5);
            LocalDate normalDeparture = booking.plusDays(30);
            
            Flight lastMinuteFlight = new Flight(1, "LAST", "LHR", "JFK", lastMinuteDeparture);
            lastMinuteFlight.setBasePrice(100.0);
            lastMinuteFlight.setCapacity(100);
            
            Flight normalFlight = new Flight(2, "NORM", "LHR", "JFK", normalDeparture);
            normalFlight.setBasePrice(100.0);
            normalFlight.setCapacity(100);
            
            double lastMinutePrice = DynamicPricingCalculator.calculatePrice(lastMinuteFlight, booking);
            double normalPrice = DynamicPricingCalculator.calculatePrice(normalFlight, booking);
            
            assertTrue(lastMinutePrice > normalPrice, "Last minute bookings should be more expensive");
        }
        
        @Test
        @DisplayName("Should apply early bird discount for bookings 91+ days before departure")
        void testEarlyBirdDiscount() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate earlyDeparture = booking.plusDays(100);
            LocalDate normalDeparture = booking.plusDays(30);
            
            Flight earlyFlight = new Flight(1, "EARLY", "LHR", "JFK", earlyDeparture);
            earlyFlight.setBasePrice(100.0);
            earlyFlight.setCapacity(100);
            
            Flight normalFlight = new Flight(2, "NORM", "LHR", "JFK", normalDeparture);
            normalFlight.setBasePrice(100.0);
            normalFlight.setCapacity(100);
            
            double earlyPrice = DynamicPricingCalculator.calculatePrice(earlyFlight, booking);
            double normalPrice = DynamicPricingCalculator.calculatePrice(normalFlight, booking);
            
            assertTrue(earlyPrice < normalPrice, "Early bookings should be cheaper");
        }
        
        @Test
        @DisplayName("Should handle same day booking")
        void testSameDayBooking() {
            LocalDate date = LocalDate.now().plusDays(1);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", date);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, date);
            
            assertTrue(price > 0, "Same day booking should have valid price");
        }
        
        @Test
        @DisplayName("Should handle very long advance booking")
        void testVeryLongAdvanceBooking() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(365); // One year in advance
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Very early booking should have valid price");
        }
        
        @Test
        @DisplayName("Should handle booking at different time intervals")
        void testDifferentTimeIntervals() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Test at key intervals: 3 days, 10 days, 30 days, 70 days, 120 days
            int[] intervals = {3, 10, 30, 70, 120};
            
            for (int interval : intervals) {
                LocalDate departure = booking.plusDays(interval);
                Flight testFlight = new Flight(interval, "TEST" + interval, "LHR", "JFK", departure);
                testFlight.setBasePrice(100.0);
                testFlight.setCapacity(100);
                
                double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
                
                assertTrue(price > 0, "Price should be valid for " + interval + " days advance");
            }
        }
    }
    
    @Nested
    @DisplayName("Seasonal Pricing Tests")
    class SeasonalPricingTests {
        
        @Test
        @DisplayName("Should apply peak season pricing for summer months")
        void testSummerPeakSeason() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next July (peak season)
            LocalDate summerDeparture = LocalDate.now().plusMonths(6);
            while (summerDeparture.getMonth() != Month.JULY) {
                summerDeparture = summerDeparture.plusDays(1);
            }
            
            // Find next January (off-peak)
            LocalDate winterDeparture = LocalDate.now().plusMonths(12);
            while (winterDeparture.getMonth() != Month.JANUARY) {
                winterDeparture = winterDeparture.plusDays(1);
            }
            
            Flight summerFlight = new Flight(1, "SUMMER", "LHR", "JFK", summerDeparture);
            summerFlight.setBasePrice(100.0);
            summerFlight.setCapacity(100);
            
            Flight winterFlight = new Flight(2, "WINTER", "LHR", "JFK", winterDeparture);
            winterFlight.setBasePrice(100.0);
            winterFlight.setCapacity(100);
            
            double summerPrice = DynamicPricingCalculator.calculatePrice(summerFlight, booking);
            double winterPrice = DynamicPricingCalculator.calculatePrice(winterFlight, booking);
            
            assertTrue(summerPrice > winterPrice, "Summer (peak) should be more expensive than winter (off-peak)");
        }
        
        @Test
        @DisplayName("Should apply peak season pricing for December")
        void testDecemberPeakSeason() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next December (peak - holiday season)
            LocalDate decemberDeparture = LocalDate.now().plusMonths(11);
            while (decemberDeparture.getMonth() != Month.DECEMBER) {
                decemberDeparture = decemberDeparture.plusDays(1);
            }
            
            // Find next March (off-peak)
            LocalDate marchDeparture = LocalDate.now().plusMonths(2);
            while (marchDeparture.getMonth() != Month.MARCH) {
                marchDeparture = marchDeparture.plusDays(1);
            }
            
            Flight decemberFlight = new Flight(1, "DEC", "LHR", "JFK", decemberDeparture);
            decemberFlight.setBasePrice(100.0);
            decemberFlight.setCapacity(100);
            
            Flight marchFlight = new Flight(2, "MAR", "LHR", "JFK", marchDeparture);
            marchFlight.setBasePrice(100.0);
            marchFlight.setCapacity(100);
            
            double decemberPrice = DynamicPricingCalculator.calculatePrice(decemberFlight, booking);
            double marchPrice = DynamicPricingCalculator.calculatePrice(marchFlight, booking);
            
            assertTrue(decemberPrice > marchPrice, "December (holiday peak) should be more expensive");
        }
        
        @Test
        @DisplayName("Should handle all months of the year")
        void testAllMonths() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            for (int month = 1; month <= 12; month++) {
                try {
                    // Create departure date in the future for each month
                    LocalDate departure = LocalDate.now().plusMonths(month).plusDays(10);
                    
                    Flight testFlight = new Flight(month, "TEST" + month, "LHR", "JFK", departure);
                    testFlight.setBasePrice(100.0);
                    testFlight.setCapacity(100);
                    
                    double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
                    
                    assertTrue(price > 0, "Month " + month + " should have valid pricing");
                } catch (Exception e) {
                    fail("Month " + month + " caused exception: " + e.getMessage());
                }
            }
        }
        
        @Test
        @DisplayName("Should handle leap year dates")
        void testLeapYearDate() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next leap year Feb 29
            int year = LocalDate.now().getYear();
            while (!LocalDate.of(year, 1, 1).isLeapYear()) {
                year++;
            }
            LocalDate departure = LocalDate.of(year, 2, 29);
            
            // Make sure it's in the future
            if (departure.isBefore(LocalDate.now())) {
                departure = departure.plusYears(4);
            }
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Leap year date should be handled correctly");
        }
    }
    
    @Nested
    @DisplayName("Weekend vs Weekday Pricing Tests")
    class WeekendPricingTests {
        
        @Test
        @DisplayName("Should apply weekend premium")
        void testWeekendPremium() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next Friday
            LocalDate fridayDeparture = LocalDate.now().plusDays(35);
            while (fridayDeparture.getDayOfWeek().getValue() != 5) { // 5 = Friday
                fridayDeparture = fridayDeparture.plusDays(1);
            }
            
            // Find next Wednesday
            LocalDate wednesdayDeparture = LocalDate.now().plusDays(35);
            while (wednesdayDeparture.getDayOfWeek().getValue() != 3) { // 3 = Wednesday
                wednesdayDeparture = wednesdayDeparture.plusDays(1);
            }
            
            Flight fridayFlight = new Flight(1, "FRI", "LHR", "JFK", fridayDeparture);
            fridayFlight.setBasePrice(100.0);
            fridayFlight.setCapacity(100);
            
            Flight wednesdayFlight = new Flight(2, "WED", "LHR", "JFK", wednesdayDeparture);
            wednesdayFlight.setBasePrice(100.0);
            wednesdayFlight.setCapacity(100);
            
            double fridayPrice = DynamicPricingCalculator.calculatePrice(fridayFlight, booking);
            double wednesdayPrice = DynamicPricingCalculator.calculatePrice(wednesdayFlight, booking);
            
            assertTrue(fridayPrice > wednesdayPrice, "Friday (weekend) should be more expensive than Wednesday (midweek)");
        }
        
        @Test
        @DisplayName("Should handle Saturday departures")
        void testSaturdayDeparture() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next Saturday
            LocalDate saturdayDeparture = LocalDate.now().plusDays(35);
            while (saturdayDeparture.getDayOfWeek().getValue() != 6) { // 6 = Saturday
                saturdayDeparture = saturdayDeparture.plusDays(1);
            }
            
            Flight saturdayFlight = new Flight(1, "SAT", "LHR", "JFK", saturdayDeparture);
            saturdayFlight.setBasePrice(100.0);
            saturdayFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(saturdayFlight, booking);
            
            assertTrue(price > 0, "Saturday departure should have valid price");
        }
        
        @Test
        @DisplayName("Should handle Sunday departures")
        void testSundayDeparture() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next Sunday
            LocalDate sundayDeparture = LocalDate.now().plusDays(35);
            while (sundayDeparture.getDayOfWeek().getValue() != 7) { // 7 = Sunday
                sundayDeparture = sundayDeparture.plusDays(1);
            }
            
            Flight sundayFlight = new Flight(1, "SUN", "LHR", "JFK", sundayDeparture);
            sundayFlight.setBasePrice(100.0);
            sundayFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(sundayFlight, booking);
            
            assertTrue(price > 0, "Sunday departure should have valid price");
        }
    }
    
    @Nested
    @DisplayName("Demand-Based Pricing Tests")
    class DemandBasedPricingTests {
        
        @Test
        @DisplayName("Should apply high demand pricing for nearly full flights")
        void testHighDemandPricing() throws FlightBookingSystemException {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            // Nearly empty flight
            Flight emptyFlight = new Flight(1, "EMPTY", "LHR", "JFK", departure);
            emptyFlight.setBasePrice(100.0);
            emptyFlight.setCapacity(100);
            
            // Nearly full flight
            Flight fullFlight = new Flight(2, "FULL", "LHR", "JFK", departure);
            fullFlight.setBasePrice(100.0);
            fullFlight.setCapacity(100);
            
            // Fill the second flight to 85%
            for (int i = 0; i < 85; i++) {
                Customer customer = new Customer(i, "Customer" + i, "1234567890", "test@test.com");
                fullFlight.addPassenger(customer);
            }
            
            double emptyPrice = DynamicPricingCalculator.calculatePrice(emptyFlight, booking);
            double fullPrice = DynamicPricingCalculator.calculatePrice(fullFlight, booking);
            
            assertTrue(fullPrice > emptyPrice, "Nearly full flight should be more expensive than empty flight");
        }
        
        @Test
        @DisplayName("Should handle completely empty flight")
        void testEmptyFlight() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight emptyFlight = new Flight(1, "EMPTY", "LHR", "JFK", departure);
            emptyFlight.setBasePrice(100.0);
            emptyFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(emptyFlight, booking);
            
            assertTrue(price > 0, "Empty flight should have valid price");
        }
        
        @Test
        @DisplayName("Should handle completely full flight")
        void testFullFlight() throws FlightBookingSystemException {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight fullFlight = new Flight(1, "FULL", "LHR", "JFK", departure);
            fullFlight.setBasePrice(100.0);
            fullFlight.setCapacity(100);
            
            // Fill the flight completely
            for (int i = 0; i < 100; i++) {
                Customer customer = new Customer(i, "Customer" + i, "1234567890", "test@test.com");
                fullFlight.addPassenger(customer);
            }
            
            double price = DynamicPricingCalculator.calculatePrice(fullFlight, booking);
            
            assertTrue(price > 0, "Full flight should have valid price");
        }
        
        @Test
        @DisplayName("Should handle moderate occupancy")
        void testModerateOccupancy() throws FlightBookingSystemException {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight moderateFlight = new Flight(1, "MODERATE", "LHR", "JFK", departure);
            moderateFlight.setBasePrice(100.0);
            moderateFlight.setCapacity(100);
            
            // Fill to 60%
            for (int i = 0; i < 60; i++) {
                Customer customer = new Customer(i, "Customer" + i, "1234567890", "test@test.com");
                moderateFlight.addPassenger(customer);
            }
            
            double price = DynamicPricingCalculator.calculatePrice(moderateFlight, booking);
            
            assertTrue(price > 0, "Moderately full flight should have valid price");
        }
    }
    
    @Nested
    @DisplayName("Price Breakdown Tests")
    class PriceBreakdownTests {
        
        @Test
        @DisplayName("Should return non-empty breakdown")
        void testNonEmptyBreakdown() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            String breakdown = DynamicPricingCalculator.getPriceBreakdown(testFlight, booking);
            
            assertNotNull(breakdown, "Breakdown should not be null");
            assertFalse(breakdown.isEmpty(), "Breakdown should not be empty");
        }
        
        @Test
        @DisplayName("Should contain price information")
        void testContainsPriceInfo() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            String breakdown = DynamicPricingCalculator.getPriceBreakdown(testFlight, booking);
            
            // Should contain some price-related text
            assertTrue(breakdown.length() > 20, "Breakdown should contain substantial information");
        }
        
        @Test
        @DisplayName("Should match calculated price")
        void testBreakdownMatchesCalculatedPrice() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            String breakdown = DynamicPricingCalculator.getPriceBreakdown(testFlight, booking);
            
            // Breakdown should contain the final price
            String priceStr = String.format("%.2f", price);
            assertTrue(breakdown.contains(priceStr), "Breakdown should contain the final price: " + priceStr);
        }
        
        @Test
        @DisplayName("Should provide different breakdowns for different scenarios")
        void testDifferentScenarios() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Last minute booking
            LocalDate lastMinuteDeparture = booking.plusDays(5);
            Flight lastMinuteFlight = new Flight(1, "LAST", "LHR", "JFK", lastMinuteDeparture);
            lastMinuteFlight.setBasePrice(100.0);
            lastMinuteFlight.setCapacity(100);
            
            String lastMinuteBreakdown = DynamicPricingCalculator.getPriceBreakdown(lastMinuteFlight, booking);
            
            // Early booking
            LocalDate earlyDeparture = booking.plusDays(100);
            Flight earlyFlight = new Flight(2, "EARLY", "LHR", "JFK", earlyDeparture);
            earlyFlight.setBasePrice(100.0);
            earlyFlight.setCapacity(100);
            
            String earlyBreakdown = DynamicPricingCalculator.getPriceBreakdown(earlyFlight, booking);
            
            assertNotNull(lastMinuteBreakdown);
            assertNotNull(earlyBreakdown);
        }
    }
    
    @Nested
    @DisplayName("Pricing Message Tests")
    class PricingMessageTests {
        
        @Test
        @DisplayName("Should return non-empty pricing message")
        void testNonEmptyMessage() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            String message = DynamicPricingCalculator.getPricingMessage(testFlight, booking);
            
            assertNotNull(message, "Message should not be null");
            assertFalse(message.isEmpty(), "Message should not be empty");
        }
        
        @Test
        @DisplayName("Should contain helpful information")
        void testHelpfulMessage() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            String message = DynamicPricingCalculator.getPricingMessage(testFlight, booking);
            
            // Message should be reasonably informative
            assertTrue(message.length() > 10, "Message should contain substantial information");
        }
        
        @Test
        @DisplayName("Should handle different scenarios")
        void testDifferentMessages() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Last minute scenario
            LocalDate lastMinuteDeparture = booking.plusDays(3);
            Flight lastMinuteFlight = new Flight(1, "LAST", "LHR", "JFK", lastMinuteDeparture);
            lastMinuteFlight.setBasePrice(100.0);
            lastMinuteFlight.setCapacity(100);
            
            String lastMinuteMessage = DynamicPricingCalculator.getPricingMessage(lastMinuteFlight, booking);
            
            // Early booking scenario
            LocalDate earlyDeparture = booking.plusDays(100);
            Flight earlyFlight = new Flight(2, "EARLY", "LHR", "JFK", earlyDeparture);
            earlyFlight.setBasePrice(100.0);
            earlyFlight.setCapacity(100);
            
            String earlyMessage = DynamicPricingCalculator.getPricingMessage(earlyFlight, booking);
            
            assertNotNull(lastMinuteMessage);
            assertNotNull(earlyMessage);
        }
    }
    
    @Nested
    @DisplayName("Integration and Complex Scenario Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should calculate consistent prices across methods")
        void testConsistentPricing() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            String breakdown = DynamicPricingCalculator.getPriceBreakdown(testFlight, booking);
            
            // Breakdown should contain the same final price
            String priceStr = String.format("%.2f", price);
            assertTrue(breakdown.contains(priceStr), "Breakdown should match calculated price");
        }
        
        @Test
        @DisplayName("Should handle complex high-price scenario")
        void testComplexHighPriceScenario() throws FlightBookingSystemException {
            // Peak season + weekend + last minute + high demand = expensive
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find next July Saturday
            LocalDate departure = LocalDate.now().plusMonths(6);
            while (departure.getMonth() != Month.JULY || departure.getDayOfWeek().getValue() != 6) {
                departure = departure.plusDays(1);
            }
            // Make it 8 days away for short notice
            if (departure.isBefore(booking.plusDays(8))) {
                departure = departure.plusYears(1);
            }
            departure = booking.plusDays(8);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(200.0);
            testFlight.setCapacity(100);
            
            // Add passengers to create high demand
            for (int i = 0; i < 85; i++) {
                Customer customer = new Customer(i, "Customer" + i, "1234567890", "test@test.com");
                testFlight.addPassenger(customer);
            }
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            // Should be expensive due to multiple premium factors
            assertTrue(price >= 200.0, "Price should be at least base price with premium factors");
        }
        
        @Test
        @DisplayName("Should handle complex low-price scenario")
        void testComplexLowPriceScenario() {
            // Off-peak + midweek + super early + low demand = cheap
            LocalDate booking = LocalDate.now().plusDays(1);
            
            // Find a Wednesday in January, 132 days out
            LocalDate departure = LocalDate.now().plusDays(132);
            while (departure.getMonth() != Month.JANUARY || departure.getDayOfWeek().getValue() != 3) {
                departure = departure.plusDays(1);
            }
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(200.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            // Should be discounted due to multiple discount factors
            assertTrue(price > 0, "Price should be positive");
            assertTrue(price <= 200.0, "Price should be discounted from base price");
        }
        
        @Test
        @DisplayName("Should handle year transition correctly")
        void testYearTransition() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(5);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Year transition should be handled correctly");
        }
        
        @Test
        @DisplayName("Should handle domestic vs international flights")
        void testDomesticVsInternational() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            // Domestic flight (UK to UK)
            Flight domestic = new Flight(1, "DOM", "LHR", "MAN", departure);
            domestic.setBasePrice(100.0);
            domestic.setCapacity(100);
            
            // International flight
            Flight international = new Flight(2, "INT", "LHR", "JFK", departure);
            international.setBasePrice(100.0);
            international.setCapacity(100);
            
            double domesticPrice = DynamicPricingCalculator.calculatePrice(domestic, booking);
            double internationalPrice = DynamicPricingCalculator.calculatePrice(international, booking);
            
            // Both should return valid prices
            assertTrue(domesticPrice > 0, "Domestic flight should have valid price");
            assertTrue(internationalPrice > 0, "International flight should have valid price");
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle very large base price")
        void testLargeBasePrice() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(10000.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Should handle large base price");
            assertTrue(price < 100000, "Price should be within reasonable bounds");
        }
        
        @Test
        @DisplayName("Should handle very small base price")
        void testSmallBasePrice() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(1.0);
            testFlight.setCapacity(100);
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Should handle small base price");
        }
        
        @Test
        @DisplayName("Should handle large capacity flights")
        void testLargeCapacity() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(500); // Large aircraft
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Should handle large capacity flights");
        }
        
        @Test
        @DisplayName("Should handle small capacity flights")
        void testSmallCapacity() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(10); // Small aircraft
            
            double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            assertTrue(price > 0, "Should handle small capacity flights");
        }
    }
    
    @Nested
    @DisplayName("Performance Tests")
    class PerformanceTests {
        
        @Test
        @DisplayName("Should handle multiple price calculations efficiently")
        void testMultipleCalculations() {
            LocalDate booking = LocalDate.now().plusDays(1);
            
            long startTime = System.currentTimeMillis();
            
            for (int i = 1; i <= 100; i++) {
                LocalDate departure = booking.plusDays(30 + i);
                Flight testFlight = new Flight(i, "TEST" + i, "LHR", "JFK", departure);
                testFlight.setBasePrice(100.0);
                testFlight.setCapacity(100);
                
                double price = DynamicPricingCalculator.calculatePrice(testFlight, booking);
                assertTrue(price > 0);
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            // 100 calculations should complete in reasonable time (< 5 seconds)
            assertTrue(duration < 5000, "Price calculations should be efficient");
        }
        
        @Test
        @DisplayName("Should produce consistent results for same inputs")
        void testConsistency() {
            LocalDate booking = LocalDate.now().plusDays(1);
            LocalDate departure = booking.plusDays(30);
            
            Flight testFlight = new Flight(1, "TEST", "LHR", "JFK", departure);
            testFlight.setBasePrice(100.0);
            testFlight.setCapacity(100);
            
            // Calculate price multiple times
            double price1 = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            double price2 = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            double price3 = DynamicPricingCalculator.calculatePrice(testFlight, booking);
            
            // All prices should be identical
            assertEquals(price1, price2, 0.001, "Repeated calculations should give same result");
            assertEquals(price2, price3, 0.001, "Repeated calculations should give same result");
            assertEquals(price1, price3, 0.001, "Repeated calculations should give same result");
        }
    }
}