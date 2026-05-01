package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.utils.AirportValidator;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests specifically for Nepal airport codes validation.
 * Ensures all Nepal airports are recognized by the system.
 * 
 * @author Your Name
 * @version 1.0
 */
@DisplayName("Nepal Airport Validation Tests")
public class NepalAirportTest {

    // ==================== International Airports Tests ====================

    @Test
    @DisplayName("Test Tribhuvan International Airport (KTM)")
    public void testKathmandu() {
        assertTrue(AirportValidator.isValidAirport("KTM"));
        assertTrue(AirportValidator.isValidAirport("ktm"));
        assertTrue(AirportValidator.isValidAirport("Ktm"));
    }

    @Test
    @DisplayName("Test Pokhara International Airport (PKR)")
    public void testPokhara() {
        assertTrue(AirportValidator.isValidAirport("PKR"));
        assertDoesNotThrow(() -> AirportValidator.validateAirportOrThrow("PKR"));
    }

    @Test
    @DisplayName("Test Gautam Buddha International Airport (BWA)")
    public void testBhairahawa() {
        assertTrue(AirportValidator.isValidAirport("BWA"));
        assertDoesNotThrow(() -> AirportValidator.validateAirportOrThrow("BWA"));
    }

    // ==================== Major Domestic Airports Tests ====================

    @Test
    @DisplayName("Test Biratnagar Airport (BIR)")
    public void testBiratnagar() {
        assertTrue(AirportValidator.isValidAirport("BIR"));
    }

    @Test
    @DisplayName("Test Bhadrapur Airport (BDP)")
    public void testBhadrapur() {
        assertTrue(AirportValidator.isValidAirport("BDP"));
    }

    @Test
    @DisplayName("Test Janakpur Airport (JKR)")
    public void testJanakpur() {
        assertTrue(AirportValidator.isValidAirport("JKR"));
    }

    @Test
    @DisplayName("Test Nepalgunj Airport (NEP)")
    public void testNepalgunj() {
        assertTrue(AirportValidator.isValidAirport("NEP"));
        assertTrue(AirportValidator.isValidAirport("KEP"));
    }

    @Test
    @DisplayName("Test Dhangadhi Airport (DHI)")
    public void testDhangadhi() {
        assertTrue(AirportValidator.isValidAirport("DHI"));
    }

    // ==================== Mountain / Tourist Airports ====================

    @Test
    @DisplayName("Test Lukla Airport (LUA)")
    public void testLukla() {
        assertTrue(AirportValidator.isValidAirport("LUA"));
        assertDoesNotThrow(() -> AirportValidator.validateAirportOrThrow("LUA"));

        String message = AirportValidator.getValidationMessage("LUA");
        assertTrue(message.isEmpty());
    }

    @Test
    public void testJomsom() {
        assertTrue(AirportValidator.isValidAirport("JMO"));
    }

    @Test
    public void testTumlingtar() {
        assertTrue(AirportValidator.isValidAirport("TMI"));
    }

    @Test
    public void testPhaplu() {
        assertTrue(AirportValidator.isValidAirport("PPL"));
    }

    @Test
    public void testSimikot() {
        assertTrue(AirportValidator.isValidAirport("IMK"));
    }

    @Test
    public void testJumla() {
        assertTrue(AirportValidator.isValidAirport("JUM"));
    }

    @Test
    public void testManang() {
        assertTrue(AirportValidator.isValidAirport("NGX"));
    }

    // ==================== Regional Airports ====================

    @Test public void testSimara() { assertTrue(AirportValidator.isValidAirport("SIF")); }
    @Test public void testRajbiraj() { assertTrue(AirportValidator.isValidAirport("RJB")); }
    @Test public void testRamechhap() { assertTrue(AirportValidator.isValidAirport("RHP")); }
    @Test public void testMeghauli() { assertTrue(AirportValidator.isValidAirport("MEY")); }
    @Test public void testTaplejung() { assertTrue(AirportValidator.isValidAirport("TPJ")); }
    @Test public void testBajhang() { assertTrue(AirportValidator.isValidAirport("BJH")); }
    @Test public void testRukum() { assertTrue(AirportValidator.isValidAirport("RUK")); }
    @Test public void testBaglung() { assertTrue(AirportValidator.isValidAirport("BGL")); }
    @Test public void testLamidanda() { assertTrue(AirportValidator.isValidAirport("LDN")); }
    @Test public void testDang() { assertTrue(AirportValidator.isValidAirport("DNP")); }
    @Test public void testDarchula() { assertTrue(AirportValidator.isValidAirport("DAP")); }

    // ==================== Route Validation ====================

    @Test
    public void testKathmanduToPokhara() {
        assertDoesNotThrow(() ->
            AirportValidator.validateOriginDestination("KTM", "PKR")
        );
    }

    @Test
    public void testSameAirport() {
        assertThrows(IllegalArgumentException.class, () ->
            AirportValidator.validateOriginDestination("KTM", "KTM")
        );
    }

    // ==================== Batch Nepal Airports ====================

    @Test
    public void testAllNepalAirports() {
        String[] airports = {
            "KTM","PKR","BWA","BIR","BDP","JKR","NEP","DHI",
            "SIF","TMI","RJB","RHP","LUA","JMO","PPL","RUK",
            "TPJ","BJH","BGL","LDN","NGX","IMK","JUM","DNP",
            "DAP","MEY","KEP"
        };

        for (String a : airports) {
            assertTrue(AirportValidator.isValidAirport(a));
        }
    }

    @Test
    public void testInvalidCodeMessage() {
        String message = AirportValidator.getValidationMessage("XYZ");
        assertFalse(message.isEmpty());
        assertTrue(message.contains("Invalid airport code"));
    }
}
