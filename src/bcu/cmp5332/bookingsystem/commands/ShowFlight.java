package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to display detailed information about a specific flight.
 */
public class ShowFlight implements Command {

    private final int flightId;

    /**
     * Constructs a ShowFlight command for the specified flight ID.
     * 
     * @param flightId the ID of the flight to display
     */
    public ShowFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the show flight command.
     * Retrieves and displays detailed information about the flight including all passengers.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if the flight ID is invalid
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get the flight by ID
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Display detailed flight information including passenger list
        System.out.println(flight.getDetailsLong());
    }
}
