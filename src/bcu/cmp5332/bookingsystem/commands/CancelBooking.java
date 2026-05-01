package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to cancel an existing booking.
 */
public class CancelBooking implements Command {

    private final int customerId;
    private final int flightId;
    private static final double CANCELLATION_FEE = 25.0; // Fixed cancellation fee

    /**
     * Constructs a CancelBooking command for the specified customer and flight.
     * 
     * @param customerId the ID of the customer canceling the booking
     * @param flightId the ID of the flight to cancel
     */
    public CancelBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    /**
     * Executes the cancel booking command.
     * Marks the booking as cancelled, removes the customer from the flight's passenger list,
     * and applies a cancellation fee.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if customer/flight not found or no active booking exists
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Retrieve customer and flight from the system
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        // Find the booking to cancel
        Booking bookingToCancel = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == flightId && !booking.isCancelled()) {
                bookingToCancel = booking;
                break;
            }
        }
        
        // Check if booking exists
        if (bookingToCancel == null) {
            throw new FlightBookingSystemException("No active booking found for this customer and flight.");
        }
        
        // Mark booking as cancelled
        bookingToCancel.setCancelled(true);
        
        // Apply cancellation fee
        bookingToCancel.setCancellationFee(CANCELLATION_FEE);
        
        // Remove customer from flight's passenger list
        flight.removePassenger(customer);
        
        System.out.println("Booking cancelled successfully!");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Flight: " + flight.getFlightNumber() + " (" + flight.getOrigin() 
                          + " to " + flight.getDestination() + ")");
        System.out.println("Cancellation fee: \u00A3" + String.format("%.2f", CANCELLATION_FEE));
        System.out.println("Seats now available: " + flight.getAvailableSeats());
    }
}