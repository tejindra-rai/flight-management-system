package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.utils.DynamicPricingCalculator;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a flight in the booking system with dynamic pricing.
 * Supports passenger management, soft deletion, and dynamic pricing via DynamicPricingCalculator.
 *
 * @author Tejindra Rai
 * @version 5.4 - FIXED: Added Nepal airports to flight type determination
 */
public class Flight {

    private int id;
    private String flightNumber;
    private String origin;
    private String destination;
    private LocalDate departureDate;
    private int capacity;
    private double basePrice;
    private boolean deleted;
    private String flightClass;
    private boolean isReturnFlight;
    private FlightType flightType;

    private final Set<Customer> passengers = new HashSet<>();

    /**
     * Constructs a new Flight with the specified details.
     * Validates all parameters, including that the departure date is in the future.
     *
     * @param id            unique flight ID (must be positive)
     * @param flightNumber  flight number (non-empty string)
     * @param origin        origin airport code (exactly 3 uppercase letters)
     * @param destination   destination airport code (exactly 3 uppercase letters, different from origin)
     * @param departureDate departure date (must be in the future relative to now)
     * @throws IllegalArgumentException if any parameter is invalid
     */
    public Flight(int id, String flightNumber, String origin, String destination, LocalDate departureDate) {
        if (id <= 0) {
            throw new IllegalArgumentException("Flight ID must be positive.");
        }
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Flight number cannot be empty.");
        }
        if (origin == null || !origin.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Origin must be a 3-letter uppercase IATA code.");
        }
        if (destination == null || !destination.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Destination must be a 3-letter uppercase IATA code.");
        }
        if (origin.equals(destination)) {
            throw new IllegalArgumentException("Origin and destination cannot be the same.");
        }
        if (departureDate == null || departureDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Departure date must be in the future.");
        }

        this.id = id;
        this.flightNumber = flightNumber.trim();
        this.origin = origin.trim();
        this.destination = destination.trim();
        this.departureDate = departureDate;
        this.capacity = 100; // Default value
        this.basePrice = 100.0; // Default value
        this.deleted = false;
        this.flightClass = "Economy"; // Default
        this.isReturnFlight = false;
        this.flightType = determineFlightType(origin, destination); // Auto-detect flight type
    }

    public int getId() {
        return id;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public LocalDate getDepartureDate() {
        return departureDate;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive.");
        }
        this.capacity = capacity;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        if (basePrice <= 0) {
            throw new IllegalArgumentException("Base price must be positive.");
        }
        this.basePrice = basePrice;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getFlightClass() {
        return flightClass;
    }

    public void setFlightClass(String flightClass) {
        this.flightClass = (flightClass != null) ? flightClass.trim() : "Economy";
    }

    public boolean isReturnFlight() {
        return isReturnFlight;
    }

    public void setReturnFlight(boolean returnFlight) {
        this.isReturnFlight = returnFlight;
    }

    /**
     * Gets the type of this flight (domestic or international).
     * 
     * @return the flight type
     */
    public FlightType getFlightType() {
        return flightType;
    }

    /**
     * Sets the type of this flight.
     * 
     * @param flightType the flight type to set
     */
    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    /**
     * FIXED: Determines the flight type based on origin and destination airports.
     * Now supports multiple countries including Nepal, UK, USA, etc.
     * 
     * @param origin the origin airport code
     * @param destination the destination airport code
     * @return FlightType.DOMESTIC if both airports are in the same country, otherwise INTERNATIONAL
     */
    private static FlightType determineFlightType(String origin, String destination) {
        // Nepal airports - ADDED TO FIX THE BUG
        Set<String> nepalAirports = Set.of(
            "KTM", "PKR", "BDP", "BWA", "BIR", "JKR", "DHI", "NEP", 
            "SIF", "TMI", "RJB", "RHP", "LUA", "JMO", "PPL", "RUK", 
            "TPJ", "BJH", "MEY", "KEP", "BGL", "LDN", "NGX", "IMK", 
            "JUM", "DNP", "DAP"
        );
        
        // UK airports
        Set<String> ukAirports = Set.of(
            "LHR", "LGW", "STN", "LTN", "LCY", // London
            "MAN", "BHX", "EDI", "GLA", "BRS", 
            "NCL", "LPL", "EMA", "BFS", "ABZ", "LBA"
        );
        
        // USA airports
        Set<String> usaAirports = Set.of(
            "JFK", "LAX", "ORD", "DFW", "DEN", "SFO", "SEA", 
            "LAS", "MCO", "MIA", "ATL", "BOS", "IAH", "PHX", "IAD"
        );
        
        // Check if both airports are in Nepal (DOMESTIC)
        if (nepalAirports.contains(origin) && nepalAirports.contains(destination)) {
            return FlightType.DOMESTIC;
        }
        
        // Check if both airports are in UK (DOMESTIC)
        if (ukAirports.contains(origin) && ukAirports.contains(destination)) {
            return FlightType.DOMESTIC;
        }
        
        // Check if both airports are in USA (DOMESTIC)
        if (usaAirports.contains(origin) && usaAirports.contains(destination)) {
            return FlightType.DOMESTIC;
        }
        
        // Otherwise, it's INTERNATIONAL
        return FlightType.INTERNATIONAL;
    }

    /**
     * Returns an unmodifiable list of passengers on this flight.
     */
    public List<Customer> getPassengers() {
        return new ArrayList<>(passengers);
    }

    /**
     * Adds a passenger to this flight if there is capacity.
     *
     * @param passenger the customer to add
     * @throws FlightBookingSystemException if the flight is full or passenger already booked
     */
    public void addPassenger(Customer passenger) throws FlightBookingSystemException {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }
        if (passengers.size() >= capacity) {
            throw new FlightBookingSystemException("Flight is full (capacity: " + capacity + ").");
        }
        if (passengers.contains(passenger)) {
            throw new FlightBookingSystemException("Passenger is already booked on this flight.");
        }
        passengers.add(passenger);
    }

    /**
     * Removes a passenger from this flight.
     *
     * @param passenger the customer to remove
     * @throws FlightBookingSystemException if the passenger is not on this flight
     */
    public void removePassenger(Customer passenger) throws FlightBookingSystemException {
        if (passenger == null) {
            throw new IllegalArgumentException("Passenger cannot be null.");
        }
        if (!passengers.remove(passenger)) {
            throw new FlightBookingSystemException("Passenger is not booked on this flight.");
        }
    }

    /**
     * Returns the number of available seats on this flight.
     */
    public int getAvailableSeats() {
        return capacity - passengers.size();
    }

    /**
     * Returns a short string representation of the flight details.
     */
    public String getDetailsShort() {
        return "Flight #" + id + " - " + flightNumber + " - " +
               origin + " → " + destination +
               " (" + departureDate.format(DateTimeFormatter.ofPattern("dd-MMM-yyyy")) + ")";
    }

    /**
     * Returns a detailed string representation including passenger list.
     */
    public String getDetailsLong() {
        StringBuilder sb = new StringBuilder(getDetailsShort() + "\n");
        sb.append("Status: ").append(deleted ? "Deleted" : "Active").append("\n");
        sb.append("Type: ").append(flightType.getDisplayName()).append("\n");
        sb.append("Class: ").append(flightClass).append("\n");
        sb.append("Return Flight: ").append(isReturnFlight ? "Yes" : "No").append("\n");
        sb.append("Capacity: ").append(capacity).append(" seats\n");
        sb.append("Available: ").append(getAvailableSeats()).append(" seats\n");
        sb.append("Base Price: £").append(String.format("%.2f", basePrice)).append("\n");
        sb.append("Passengers (").append(passengers.size()).append("):\n");

        if (passengers.isEmpty()) {
            sb.append("  No passengers booked yet.\n");
        } else {
            for (Customer p : passengers) {
                sb.append("  - ").append(p.getDetailsShort()).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Calculates the current dynamic price for this flight.
     * Delegates to DynamicPricingCalculator.
     */
    public double calculatePrice(LocalDate currentDate) {
        return DynamicPricingCalculator.calculatePrice(this, currentDate);
    }

    public String getPriceBreakdown(LocalDate currentDate) {
        return DynamicPricingCalculator.getPriceBreakdown(this, currentDate);
    }

    public String getPricingMessage(LocalDate currentDate) {
        return DynamicPricingCalculator.getPricingMessage(this, currentDate);
    }

    @Override
    public String toString() {
        return "Flight{" +
               "id=" + id +
               ", flightNumber='" + flightNumber + '\'' +
               ", origin='" + origin + '\'' +
               ", destination='" + destination + '\'' +
               ", departureDate=" + departureDate +
               ", capacity=" + capacity +
               ", basePrice=" + basePrice +
               ", deleted=" + deleted +
               ", flightClass='" + flightClass + '\'' +
               ", isReturnFlight=" + isReturnFlight +
               ", flightType=" + flightType +
               ", passengers=" + passengers.size() +
               '}';
    }
}