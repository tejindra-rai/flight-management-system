package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to delete (hide) a flight from the system.
 * Uses soft delete - the flight is marked as deleted but not removed from storage.
 */
public class DeleteFlight implements Command {

    private final int flightId;

    /**
     * Constructs a DeleteFlight command for the specified flight ID.
     * 
     * @param flightId the ID of the flight to delete
     */
    public DeleteFlight(int flightId) {
        this.flightId = flightId;
    }

    /**
     * Executes the delete flight command.
     * Marks the flight as deleted so it won't appear in flight lists.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if the flight ID is invalid or already deleted
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get the flight by ID
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Check if flight is already deleted
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Flight is already deleted.");
        }
        
        // Soft delete - mark as deleted
        flight.setDeleted(true);
        
        System.out.println("Flight #" + flightId + " (" + flight.getFlightNumber() + ") deleted successfully.");
    }
}