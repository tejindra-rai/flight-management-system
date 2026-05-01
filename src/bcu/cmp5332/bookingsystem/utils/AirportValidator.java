package bcu.cmp5332.bookingsystem.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for validating airport IATA codes.
 * Ensures airport codes are valid 3-letter IATA codes.
 * 
 * @author Binay Chaudhary
 * @version 3.0 - Added airport names mapping
 */
public class AirportValidator {
    
    /**
     * Set of valid IATA airport codes.
     * This is a subset of major international airports.
     * Expand this list as needed for your application.
     */
    private static final Set<String> VALID_AIRPORTS = new HashSet<>(Arrays.asList(
        // United Kingdom
        "LHR","LGW","STN","LTN","LCY","MAN","BHX","EDI","GLA","BRS","NCL","LBA","EMA","BFS",
        // United States
        "JFK","LAX","ORD","DFW","DEN","SFO","SEA","LAS","MCO","MIA","ATL","BOS","IAH","PHX","IAD",
        // Europe
        "CDG","FRA","AMS","MAD","BCN","FCO","MXP","VIE","ZRH","CPH","ARN","OSL","HEL","DUB","BRU",
        "LIS","ATH","PRG","WAW","BUD",
        // Middle East
        "DXB","DOH","AUH","CAI","TLV","IST","JED","RUH",
        // Asia-Pacific
        "HKG","SIN","BKK","NRT","ICN","PEK","PVG","DEL","BOM","SYD","MEL","AKL",
        // Nepal
        "KTM","PKR","BDP","BWA","BIR","JKR","DHI","NEP","SIF","TMI","RJB","RHP","LUA","JMO",
        "PPL","RUK","TPJ","BJH","MEY","KEP","BGL","LDN","NGX","IMK","JUM","DNP","DAP",
        // Africa
        "JNB","CPT","NBO","ADD","LOS",
        // South America
        "GRU","GIG","BOG","LIM","SCL","EZE",
        // Canada
        "YYZ","YVR","YUL","YYC"
    ));
    
    /**
     * Map of airport codes to their full names.
     */
    private static final Map<String, String> AIRPORT_NAMES = new HashMap<>();
    
    static {
        // United Kingdom
        AIRPORT_NAMES.put("LHR", "London Heathrow");
        AIRPORT_NAMES.put("LGW", "London Gatwick");
        AIRPORT_NAMES.put("STN", "London Stansted");
        AIRPORT_NAMES.put("LTN", "London Luton");
        AIRPORT_NAMES.put("LCY", "London City");
        AIRPORT_NAMES.put("MAN", "Manchester");
        AIRPORT_NAMES.put("BHX", "Birmingham");
        AIRPORT_NAMES.put("EDI", "Edinburgh");
        AIRPORT_NAMES.put("GLA", "Glasgow");
        AIRPORT_NAMES.put("BRS", "Bristol");
        AIRPORT_NAMES.put("NCL", "Newcastle");
        AIRPORT_NAMES.put("LBA", "Leeds Bradford");
        AIRPORT_NAMES.put("EMA", "East Midlands");
        AIRPORT_NAMES.put("BFS", "Belfast");
        
        // United States
        AIRPORT_NAMES.put("JFK", "New York JFK");
        AIRPORT_NAMES.put("LAX", "Los Angeles");
        AIRPORT_NAMES.put("ORD", "Chicago O'Hare");
        AIRPORT_NAMES.put("DFW", "Dallas/Fort Worth");
        AIRPORT_NAMES.put("DEN", "Denver");
        AIRPORT_NAMES.put("SFO", "San Francisco");
        AIRPORT_NAMES.put("SEA", "Seattle");
        AIRPORT_NAMES.put("LAS", "Las Vegas");
        AIRPORT_NAMES.put("MCO", "Orlando");
        AIRPORT_NAMES.put("MIA", "Miami");
        AIRPORT_NAMES.put("ATL", "Atlanta");
        AIRPORT_NAMES.put("BOS", "Boston");
        AIRPORT_NAMES.put("IAH", "Houston");
        AIRPORT_NAMES.put("PHX", "Phoenix");
        AIRPORT_NAMES.put("IAD", "Washington Dulles");
        
        // Europe
        AIRPORT_NAMES.put("CDG", "Paris Charles de Gaulle");
        AIRPORT_NAMES.put("FRA", "Frankfurt");
        AIRPORT_NAMES.put("AMS", "Amsterdam Schiphol");
        AIRPORT_NAMES.put("MAD", "Madrid");
        AIRPORT_NAMES.put("BCN", "Barcelona");
        AIRPORT_NAMES.put("FCO", "Rome Fiumicino");
        AIRPORT_NAMES.put("MXP", "Milan Malpensa");
        AIRPORT_NAMES.put("VIE", "Vienna");
        AIRPORT_NAMES.put("ZRH", "Zurich");
        AIRPORT_NAMES.put("CPH", "Copenhagen");
        AIRPORT_NAMES.put("ARN", "Stockholm Arlanda");
        AIRPORT_NAMES.put("OSL", "Oslo");
        AIRPORT_NAMES.put("HEL", "Helsinki");
        AIRPORT_NAMES.put("DUB", "Dublin");
        AIRPORT_NAMES.put("BRU", "Brussels");
        AIRPORT_NAMES.put("LIS", "Lisbon");
        AIRPORT_NAMES.put("ATH", "Athens");
        AIRPORT_NAMES.put("PRG", "Prague");
        AIRPORT_NAMES.put("WAW", "Warsaw");
        AIRPORT_NAMES.put("BUD", "Budapest");
        
        // Middle East
        AIRPORT_NAMES.put("DXB", "Dubai");
        AIRPORT_NAMES.put("DOH", "Doha");
        AIRPORT_NAMES.put("AUH", "Abu Dhabi");
        AIRPORT_NAMES.put("CAI", "Cairo");
        AIRPORT_NAMES.put("TLV", "Tel Aviv");
        AIRPORT_NAMES.put("IST", "Istanbul");
        AIRPORT_NAMES.put("JED", "Jeddah");
        AIRPORT_NAMES.put("RUH", "Riyadh");
        
        // Asia-Pacific
        AIRPORT_NAMES.put("HKG", "Hong Kong");
        AIRPORT_NAMES.put("SIN", "Singapore");
        AIRPORT_NAMES.put("BKK", "Bangkok");
        AIRPORT_NAMES.put("NRT", "Tokyo Narita");
        AIRPORT_NAMES.put("ICN", "Seoul Incheon");
        AIRPORT_NAMES.put("PEK", "Beijing");
        AIRPORT_NAMES.put("PVG", "Shanghai Pudong");
        AIRPORT_NAMES.put("DEL", "New Delhi");
        AIRPORT_NAMES.put("BOM", "Mumbai");
        AIRPORT_NAMES.put("SYD", "Sydney");
        AIRPORT_NAMES.put("MEL", "Melbourne");
        AIRPORT_NAMES.put("AKL", "Auckland");
        
        // Nepal
        AIRPORT_NAMES.put("KTM", "Kathmandu");
        AIRPORT_NAMES.put("PKR", "Pokhara");
        AIRPORT_NAMES.put("BDP", "Bhadrapur");
        AIRPORT_NAMES.put("BWA", "Bhairahawa");
        AIRPORT_NAMES.put("BIR", "Biratnagar");
        AIRPORT_NAMES.put("JKR", "Janakpur");
        AIRPORT_NAMES.put("DHI", "Dhangadhi");
        AIRPORT_NAMES.put("NEP", "Nepalgunj");
        AIRPORT_NAMES.put("SIF", "Simara");
        AIRPORT_NAMES.put("TMI", "Tumlingtar");
        AIRPORT_NAMES.put("RJB", "Rajbiraj");
        AIRPORT_NAMES.put("RHP", "Ramechhap");
        AIRPORT_NAMES.put("LUA", "Lukla");
        AIRPORT_NAMES.put("JMO", "Jomsom");
        AIRPORT_NAMES.put("PPL", "Phaplu");
        AIRPORT_NAMES.put("RUK", "Rukumkot");
        AIRPORT_NAMES.put("TPJ", "Taplejung");
        AIRPORT_NAMES.put("BJH", "Bajhang");
        AIRPORT_NAMES.put("MEY", "Meghauli");
        AIRPORT_NAMES.put("KEP", "Nepalgunj");
        AIRPORT_NAMES.put("BGL", "Baglung");
        AIRPORT_NAMES.put("LDN", "Lamidanda");
        AIRPORT_NAMES.put("NGX", "Manang");
        AIRPORT_NAMES.put("IMK", "Simikot");
        AIRPORT_NAMES.put("JUM", "Jumla");
        AIRPORT_NAMES.put("DNP", "Dolpa");
        AIRPORT_NAMES.put("DAP", "Darchula");
        
        // Africa
        AIRPORT_NAMES.put("JNB", "Johannesburg");
        AIRPORT_NAMES.put("CPT", "Cape Town");
        AIRPORT_NAMES.put("NBO", "Nairobi");
        AIRPORT_NAMES.put("ADD", "Addis Ababa");
        AIRPORT_NAMES.put("LOS", "Lagos");
        
        // South America
        AIRPORT_NAMES.put("GRU", "São Paulo");
        AIRPORT_NAMES.put("GIG", "Rio de Janeiro");
        AIRPORT_NAMES.put("BOG", "Bogotá");
        AIRPORT_NAMES.put("LIM", "Lima");
        AIRPORT_NAMES.put("SCL", "Santiago");
        AIRPORT_NAMES.put("EZE", "Buenos Aires");
        
        // Canada
        AIRPORT_NAMES.put("YYZ", "Toronto");
        AIRPORT_NAMES.put("YVR", "Vancouver");
        AIRPORT_NAMES.put("YUL", "Montreal");
        AIRPORT_NAMES.put("YYC", "Calgary");
    }
    
    /**
     * Gets the full name of an airport from its code.
     * 
     * @param airportCode the 3-letter IATA code
     * @return the full airport name, or the code itself if not found
     */
    public static String getAirportName(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return "Unknown";
        }
        
        String code = airportCode.trim().toUpperCase();
        return AIRPORT_NAMES.getOrDefault(code, code);
    }
    
    /**
     * Validates an airport code.
     * 
     * @param airportCode the airport code to validate
     * @return true if airport code is valid, false otherwise
     */
    public static boolean isValidAirport(String airportCode) {
        if (airportCode == null || airportCode.trim().isEmpty()) {
            return false;
        }
        
        String code = airportCode.trim().toUpperCase();
        
        if (code.length() != 3) {
            return false;
        }
        
        if (!code.matches("[A-Z]{3}")) {
            return false;
        }
        
        return VALID_AIRPORTS.contains(code);
    }
    
    /**
     * Validates an airport code and throws exception if invalid.
     * 
     * @param airportCode the airport code to validate
     * @throws IllegalArgumentException if airport code is invalid
     */
    public static void validateAirportOrThrow(String airportCode)
            throws IllegalArgumentException {
        
        if (airportCode == null || airportCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Airport code cannot be empty.");
        }
        
        String code = airportCode.trim().toUpperCase();
        
        if (code.length() != 3) {
            throw new IllegalArgumentException(
                "Airport code must be exactly 3 characters. Example: LHR, JFK, DXB"
            );
        }
        
        if (!code.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException(
                "Airport code must contain only letters (A-Z)."
            );
        }
        
        if (!VALID_AIRPORTS.contains(code)) {
            throw new IllegalArgumentException(
                "Invalid airport code: " + code + ". Please use a valid IATA airport code."
            );
        }
    }
    
    /**
     * Gets a user-friendly validation message for an airport code.
     * 
     * @param airportCode the airport code to validate
     * @return validation message (empty string if valid, error message if invalid)
     */
    public static String getValidationMessage(String airportCode) {
        try {
            validateAirportOrThrow(airportCode);
            return "";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Gets all valid airport codes.
     * 
     * @return set of valid airport codes
     */
    public static Set<String> getValidAirports() {
        return new HashSet<>(VALID_AIRPORTS);
    }
    
    /**
     * Checks if two airport codes are different.
     * 
     * @param origin origin airport code
     * @param destination destination airport code
     * @return true if different, false if same or invalid
     */
    public static boolean areDifferent(String origin, String destination) {
        if (origin == null || destination == null) {
            return false;
        }
        return !origin.trim().equalsIgnoreCase(destination.trim());
    }
    
    /**
     * Validates that origin and destination are different valid airports.
     * 
     * @param origin origin airport code
     * @param destination destination airport code
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateOriginDestination(String origin, String destination)
            throws IllegalArgumentException {
        
        validateAirportOrThrow(origin);
        validateAirportOrThrow(destination);
        
        if (!areDifferent(origin, destination)) {
            throw new IllegalArgumentException(
                "Origin and destination airports must be different."
            );
        }
    }
    
    /**
     * Adds a new airport code to the valid airports set.
     * 
     * @param airportCode the airport code to add
     * @return true if added successfully, false otherwise
     */
    public static boolean addAirport(String airportCode) {
        if (airportCode == null || airportCode.length() != 3) {
            return false;
        }
        
        String code = airportCode.trim().toUpperCase();
        if (!code.matches("[A-Z]{3}")) {
            return false;
        }
        
        VALID_AIRPORTS.add(code);
        return true;
    }
    
    /**
     * Adds a new airport with its name.
     * 
     * @param airportCode the 3-letter code
     * @param airportName the full airport name
     * @return true if added successfully
     */
    public static boolean addAirport(String airportCode, String airportName) {
        if (addAirport(airportCode)) {
            String code = airportCode.trim().toUpperCase();
            AIRPORT_NAMES.put(code, airportName);
            return true;
        }
        return false;
    }
}