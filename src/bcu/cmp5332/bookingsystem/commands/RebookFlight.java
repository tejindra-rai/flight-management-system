package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import java.time.LocalDate;

/**
 * Command to rebook a previously cancelled flight.
 * Allows customers to rebook flights they previously cancelled.
 * The new booking will be at current dynamic price.
 * 
 * @author Your Name
 * @version 1.0
 */
public class RebookFlight implements Command {
    
    private final int customerId;
    private final int flightId;
    
    /**
     * Constructs a RebookFlight command.
     * 
     * @param customerId the customer ID who wants to rebook
     * @param flightId the flight ID to rebook
     */
    public RebookFlight(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }
    
    /**
     * Executes the rebook flight command.
     * Checks if customer previously cancelled this flight, then creates new booking.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if rebooking fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get customer
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        if (customer == null) {
            throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
        }
        
        // Get flight
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        if (flight == null) {
            throw new FlightBookingSystemException("Flight with ID " + flightId + " not found.");
        }
        
        // Check if flight is deleted
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Cannot rebook a deleted flight.");
        }
        
        // Check if flight is in the future
        if (flight.getDepartureDate().isBefore(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("Cannot rebook a flight that has already departed.");
        }
        
        // Check if flight has available seats
        if (flight.getAvailableSeats() <= 0) {
            throw new FlightBookingSystemException("Flight is fully booked. No seats available.");
        }
        
        // Check if customer already has an active booking for this flight
        boolean hasActiveBooking = false;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == flightId && !booking.isCancelled()) {
                hasActiveBooking = true;
                break;
            }
        }
        
        if (hasActiveBooking) {
            throw new FlightBookingSystemException(
                "You already have an active booking for this flight.\n" +
                "Cannot book the same flight twice."
            );
        }
        
        // Check if customer previously cancelled this flight
        boolean hasCancelledBooking = false;
        Booking previousBooking = null;
        for (Booking booking : customer.getBookings()) {
            if (booking.getFlight().getId() == flightId && booking.isCancelled()) {
                hasCancelledBooking = true;
                previousBooking = booking;
                break;
            }
        }
        
        // Create new booking
        LocalDate bookingDate = flightBookingSystem.getSystemDate();
        Booking newBooking = new Booking(customer, flight, bookingDate);
        
        // Calculate current price
        double currentPrice = flight.calculatePrice(bookingDate);
        newBooking.setBookingPrice(currentPrice);
        
        // Add booking to customer
        customer.addBooking(newBooking);
        
        // Add customer as passenger to flight
        flight.addPassenger(customer);
        
        // Show success message
        System.out.println(" Flight Rebooked Successfully!");
        System.out.println("Customer: " + customer.getName());
        System.out.println("Flight: " + flight.getFlightNumber() + 
                          " (" + flight.getOrigin() + " to " + flight.getDestination() + ")");
        System.out.println("Departure Date: " + flight.getDepartureDate());
        System.out.println("Current Price: £" + String.format("%.2f", currentPrice));
        
        if (hasCancelledBooking && previousBooking != null) {
            System.out.println("\n Note: You previously cancelled this flight.");
            System.out.println("Previous booking price: £" + String.format("%.2f", previousBooking.getBookingPrice()));
            double priceDifference = currentPrice - previousBooking.getBookingPrice();
            if (priceDifference > 0) {
                System.out.println("  Current price is £" + String.format("%.2f", priceDifference) + 
                                  " higher than your previous booking.");
            } else if (priceDifference < 0) {
                System.out.println(" Current price is £" + String.format("%.2f", Math.abs(priceDifference)) + 
                                  " lower than your previous booking!");
            } else {
                System.out.println("  Price is the same as your previous booking.");
            }
        }
    }
}