package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;

/**
 * Command to rebook a cancelled booking.
 * Allows customers to rebook a flight they previously cancelled.
 * 
 * Features:
 * - Reactivates a cancelled booking
 * - Can change to a different flight
 * - Applies cancellation fee if switching flights
 * - Updates booking date
 * 
 * @author Flight Booking System Team
 * @version 1.0
 */
public class RebookFlightCommand implements Command {
    
    private final int customerId;
    private final int oldFlightId;
    private final int newFlightId;
    private final LocalDate newBookingDate;
    
    /**
     * Constructor for rebooking the same flight.
     * 
     * @param customerId the customer ID
     * @param flightId the flight ID to rebook
     */
    public RebookFlightCommand(int customerId, int flightId) {
        this(customerId, flightId, flightId, LocalDate.now());
    }
    
    /**
     * Constructor for rebooking to a different flight.
     * 
     * @param customerId the customer ID
     * @param oldFlightId the original cancelled flight ID
     * @param newFlightId the new flight ID to book
     * @param newBookingDate the new booking date
     */
    public RebookFlightCommand(int customerId, int oldFlightId, int newFlightId, LocalDate newBookingDate) {
        this.customerId = customerId;
        this.oldFlightId = oldFlightId;
        this.newFlightId = newFlightId;
        this.newBookingDate = newBookingDate;
    }
    
    @Override
    public void execute(FlightBookingSystem fbs) throws FlightBookingSystemException {
        // Get customer
        Customer customer = fbs.getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }
        
        if (customer.isDeleted()) {
            throw new FlightBookingSystemException("Cannot rebook for a deleted customer.");
        }
        
        // Get old flight
        Flight oldFlight = fbs.getFlightByID(oldFlightId);
        if (oldFlight == null) {
            throw new FlightBookingSystemException("Original flight with ID " + oldFlightId + " not found.");
        }
        
        // Find the cancelled booking
        Booking cancelledBooking = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == oldFlightId && booking.isCancelled()) {
                cancelledBooking = booking;
                break;
            }
        }
        
        if (cancelledBooking == null) {
            throw new FlightBookingSystemException(
                "No cancelled booking found for customer " + customer.getName() + 
                " on flight " + oldFlight.getFlightNumber()
            );
        }
        
        // Check if rebooking the same flight or switching
        if (oldFlightId == newFlightId) {
            // Rebooking the SAME flight
            rebookSameFlight(customer, oldFlight, cancelledBooking);
        } else {
            // Switching to a DIFFERENT flight
            rebookDifferentFlight(fbs, customer, oldFlight, cancelledBooking);
        }
    }
    
    /**
     * Rebooks the same cancelled flight.
     */
    private void rebookSameFlight(Customer customer, Flight flight, Booking cancelledBooking) 
            throws FlightBookingSystemException {
        
        // Check if flight is available
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException(
                "Flight " + flight.getFlightNumber() + " is no longer available."
            );
        }
        
        // Check capacity
        int currentPassengers = flight.getPassengers().size();
        if (currentPassengers >= flight.getCapacity()) {
            throw new FlightBookingSystemException(
                "Flight " + flight.getFlightNumber() + " is fully booked. " +
                "Available seats: 0, Current capacity: " + currentPassengers + "/" + flight.getCapacity()
            );
        }
        
        // Reactivate the booking
        cancelledBooking.setCancelled(false);
        cancelledBooking.setBookingDate(newBookingDate);
        
        // No additional fee for rebooking same flight
        // Cancellation fee already applied when cancelled
        
        // Add customer back to flight's passenger list
        if (!flight.getPassengers().contains(customer)) {
            flight.addPassenger(customer);
        }
        
        System.out.println("Booking successfully reactivated!");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Flight: " + flight.getFlightNumber() + " (" + flight.getOrigin() + 
                         " → " + flight.getDestination() + ")");
        System.out.println("Original Price: £" + cancelledBooking.getBookingPrice());
        System.out.println("Cancellation Fee: £" + cancelledBooking.getCancellationFee());
        System.out.println("Total Paid: £" + (cancelledBooking.getBookingPrice() + 
                         cancelledBooking.getCancellationFee()));
    }
    
    /**
     * Rebooks to a different flight.
     */
    private void rebookDifferentFlight(FlightBookingSystem fbs, Customer customer, 
                                      Flight oldFlight, Booking cancelledBooking) 
            throws FlightBookingSystemException {
        
        // Get new flight
        Flight newFlight = fbs.getFlightByID(newFlightId);
        if (newFlight == null) {
            throw new FlightBookingSystemException("New flight with ID " + newFlightId + " not found.");
        }
        
        if (newFlight.isDeleted()) {
            throw new FlightBookingSystemException(
                "Flight " + newFlight.getFlightNumber() + " is no longer available."
            );
        }
        
        // Check capacity on new flight
        int currentPassengers = newFlight.getPassengers().size();
        if (currentPassengers >= newFlight.getCapacity()) {
            throw new FlightBookingSystemException(
                "Flight " + newFlight.getFlightNumber() + " is fully booked. " +
                "Available seats: 0, Current capacity: " + currentPassengers + "/" + newFlight.getCapacity()
            );
        }
        
        // Calculate new price
        double newPrice = newFlight.calculatePrice(LocalDate.now());
        double oldPrice = cancelledBooking.getBookingPrice();
        double cancellationFee = cancelledBooking.getCancellationFee();
        double priceDifference = newPrice - oldPrice;
        
        // Remove old cancelled booking
        customer.getBookings().remove(cancelledBooking);
        
        // Create new booking
        Booking newBooking = new Booking(customer, newFlight, newBookingDate);
        newBooking.setBookingPrice(newPrice);
        
        // If new flight is more expensive, customer pays the difference + original cancellation fee
        // If new flight is cheaper, customer gets credit but still pays cancellation fee
        if (priceDifference > 0) {
            newBooking.setCancellationFee(cancellationFee); // Keep original cancellation fee
            System.out.println("Additional amount to pay: £" + priceDifference);
        } else if (priceDifference < 0) {
            // Customer gets a credit for the price difference
            double credit = Math.abs(priceDifference);
            System.out.println("You have a credit of: £" + credit);
            System.out.println("(This can be used for future bookings)");
            newBooking.setCancellationFee(cancellationFee);
        }
        
        // Add new booking
        customer.addBooking(newBooking);
        newFlight.addPassenger(customer);
        
        System.out.println("\n════════════════════════════════════════");
        System.out.println("  REBOOKING CONFIRMED!");
        System.out.println("════════════════════════════════════════");
        System.out.println("\nCustomer: " + customer.getName());
        System.out.println("\nORIGINAL FLIGHT (Cancelled):");
        System.out.println("  Flight: " + oldFlight.getFlightNumber() + 
                         " (" + oldFlight.getOrigin() + " → " + oldFlight.getDestination() + ")");
        System.out.println("  Original Price: £" + oldPrice);
        System.out.println("  Cancellation Fee: £" + cancellationFee);
        
        System.out.println("\nNEW FLIGHT:");
        System.out.println("  Flight: " + newFlight.getFlightNumber() + 
                         " (" + newFlight.getOrigin() + " → " + newFlight.getDestination() + ")");
        System.out.println("  Departure: " + newFlight.getDepartureDate());
        System.out.println("  New Price: £" + newPrice);
        System.out.println("  Price Difference: £" + priceDifference);
        
        System.out.println("\nTOTAL COST:");
        double totalCost = newPrice + cancellationFee;
        if (priceDifference > 0) {
            totalCost += priceDifference;
        }
        System.out.println("  Total: £" + totalCost);
        System.out.println("════════════════════════════════════════\n");
    }
    
    /**
     * Undoes the rebooking operation.
     * Note: This method is not part of the Command interface.
     */
    public void undo(FlightBookingSystem fbs) throws FlightBookingSystemException {
        // Find the rebooking and reverse it
        Customer customer = fbs.getCustomerByID(customerId);
        if (customer == null) {
            return;
        }
        
        Flight flight = fbs.getFlightByID(newFlightId);
        if (flight == null) {
            return;
        }
        
        // Find the booking and cancel it again
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == newFlightId && !booking.isCancelled()) {
                booking.setCancelled(true);
                flight.getPassengers().remove(customer);
                System.out.println("Rebooking has been undone.");
                return;
            }
        }
    }
}