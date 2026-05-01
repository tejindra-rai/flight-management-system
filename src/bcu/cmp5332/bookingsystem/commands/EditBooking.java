package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to update/edit an existing booking to a different flight.
 */
public class EditBooking implements Command {

    private final int customerId;
    private final int oldFlightId;
    private final int newFlightId;
    private static final double REBOOKING_FEE = 15.0; // Fixed rebooking fee

    /**
     * Constructs an EditBooking command to change a customer's flight.
     * 
     * @param customerId the ID of the customer whose booking is being updated
     * @param oldFlightId the ID of the current flight
     * @param newFlightId the ID of the new flight
     */
    public EditBooking(int customerId, int oldFlightId, int newFlightId) {
        this.customerId = customerId;
        this.oldFlightId = oldFlightId;
        this.newFlightId = newFlightId;
    }

    /**
     * Executes the edit booking command.
     * Removes customer from old flight, adds to new flight, updates booking details,
     * and applies a rebooking fee.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if customer/flights not found, no booking exists, or new flight is full
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Retrieve customer and flights from the system
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight oldFlight = flightBookingSystem.getFlightByID(oldFlightId);
        Flight newFlight = flightBookingSystem.getFlightByID(newFlightId);
        
        // Check if new flight is deleted
        if (newFlight.isDeleted()) {
            throw new FlightBookingSystemException("Cannot rebook to a deleted flight.");
        }
        
        // Check if new flight has already departed
        if (newFlight.getDepartureDate().isBefore(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("Cannot rebook to a flight that has already departed.");
        }
        
        // Find the booking to update
        Booking bookingToUpdate = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == oldFlightId && !booking.isCancelled()) {
                bookingToUpdate = booking;
                break;
            }
        }
        
        // Check if booking exists
        if (bookingToUpdate == null) {
            throw new FlightBookingSystemException("No active booking found for the old flight.");
        }
        
        // Check if customer already has a booking for the new flight
        for (Booking existingBooking : customer.getBookings()) {
            if (existingBooking.getFlight().getId() == newFlightId && !existingBooking.isCancelled()) {
                throw new FlightBookingSystemException("Customer already has a booking for the new flight.");
            }
        }
        
        // Remove customer from old flight's passenger list
        oldFlight.removePassenger(customer);
        
        // Add customer to new flight's passenger list (this checks capacity)
        newFlight.addPassenger(customer);
        
        // Update the booking with new flight
        bookingToUpdate.setFlight(newFlight);
        bookingToUpdate.setBookingDate(flightBookingSystem.getSystemDate());
        
        // Calculate new price for the new flight
        double newPrice = newFlight.calculatePrice(flightBookingSystem.getSystemDate());
        bookingToUpdate.setBookingPrice(newPrice);
        
        // Apply rebooking fee
        bookingToUpdate.setCancellationFee(bookingToUpdate.getCancellationFee() + REBOOKING_FEE);
        
        System.out.println("Booking updated successfully!");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Old Flight: " + oldFlight.getFlightNumber() + " (" + oldFlight.getOrigin() 
                          + " to " + oldFlight.getDestination() + ")");
        System.out.println("New Flight: " + newFlight.getFlightNumber() + " (" + newFlight.getOrigin() 
                          + " to " + newFlight.getDestination() + ")");
        System.out.println("New Price: \u00A3" + String.format("%.2f", newPrice));
        System.out.println("Rebooking fee: \u00A3" + String.format("%.2f", REBOOKING_FEE));
    }
}