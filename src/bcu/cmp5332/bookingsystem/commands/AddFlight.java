package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.AirportValidator;
import java.time.LocalDate;

/**
 * Command to add a new flight to the system with enhanced validation.
 * Includes airport code validation and proper ID generation.
 * 
 * @author Your Name
 * @version 3.0 - Fixed ID generation logic
 */
public class AddFlight implements Command {

    private final String flightNumber;
    private final String origin;
    private final String destination;
    private final LocalDate departureDate;
    private Flight createdFlight; // Store created flight for retrieval

    /**
     * Constructs an AddFlight command with flight details.
     * 
     * @param flightNumber the flight number
     * @param origin the origin airport code
     * @param destination the destination airport code
     * @param departureDate the departure date
     */
    public AddFlight(String flightNumber, String origin, String destination, LocalDate departureDate) {
        this.flightNumber = flightNumber;
        this.origin = origin;
        this.destination = destination;
        this.departureDate = departureDate;
    }
    
    /**
     * Gets the flight that was created by this command.
     * 
     * @return the created flight, or null if command hasn't been executed
     */
    public Flight getCreatedFlight() {
        return createdFlight;
    }
    
    /**
     * Executes the add flight command.
     * Validates airport codes and creates the flight with proper ID.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if validation fails or flight cannot be added
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Validate flight number
        if (flightNumber == null || flightNumber.trim().isEmpty()) {
            throw new FlightBookingSystemException("Flight number cannot be empty.");
        }
        
        // Validate origin airport
        try {
            AirportValidator.validateAirportOrThrow(origin);
        } catch (IllegalArgumentException e) {
            throw new FlightBookingSystemException("Origin airport error: " + e.getMessage());
        }
        
        // Validate destination airport
        try {
            AirportValidator.validateAirportOrThrow(destination);
        } catch (IllegalArgumentException e) {
            throw new FlightBookingSystemException("Destination airport error: " + e.getMessage());
        }
        
        // Check that origin and destination are different
        if (origin.trim().equalsIgnoreCase(destination.trim())) {
            throw new FlightBookingSystemException("Origin and destination airports must be different!");
        }
        
        // Validate departure date (must be in the future)
        if (departureDate.isBefore(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("Departure date must be in the future.");
        }
        
        // Generate new flight ID properly - iterate through all flights to find max ID
        int maxId = 0;
        for (Flight existingFlight : flightBookingSystem.getFlights()) {
            if (existingFlight.getId() > maxId) {
                maxId = existingFlight.getId();
            }
        }
        int newId = maxId + 1;
        
        // Create the flight with uppercase airport codes
        createdFlight = new Flight(newId, flightNumber, origin.toUpperCase(), 
                                   destination.toUpperCase(), departureDate);
        flightBookingSystem.addFlight(createdFlight);
        
        // Success message
        System.out.println("Flight #" + createdFlight.getId() + " added successfully!");
        System.out.println("Flight Number: " + flightNumber);
        System.out.println("Route: " + AirportValidator.getAirportName(origin) + 
                          " (" + origin.toUpperCase() + ") to " + 
                          AirportValidator.getAirportName(destination) + 
                          " (" + destination.toUpperCase() + ")");
        System.out.println("Departure Date: " + departureDate);
    }
}