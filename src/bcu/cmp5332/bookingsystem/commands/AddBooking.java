package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.DynamicPricingCalculator;

import java.time.LocalDate;

/**
 * Command to add a new booking for a customer on a flight.
 * Calculates dynamic price and checks for conflicts (e.g., overlapping flight dates for the customer).
 * 
 * @author Tejindra Rai
 * @version 2.1 - Fixed cancelled bookings conflict check
 */
public class AddBooking implements Command {

    private final int customerId;
    private final int flightId;

    /**
     * Constructs an AddBooking command.
     * 
     * @param customerId the ID of the customer
     * @param flightId the ID of the flight
     */
    public AddBooking(int customerId, int flightId) {
        this.customerId = customerId;
        this.flightId = flightId;
    }

    /**
     * Executes the add booking command.
     * Validates IDs, checks availability and conflicts, calculates price, and adds the booking.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if validation fails or booking cannot be added
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        Flight flight = flightBookingSystem.getFlightByID(flightId);
        
        if (flight.isDeleted()) {
            throw new FlightBookingSystemException("Cannot book a deleted flight.");
        }
        
        if (flight.getDepartureDate().isBefore(flightBookingSystem.getSystemDate())) {
            throw new FlightBookingSystemException("Cannot book a flight in the past.");
        }
        
        // Check for conflicting bookings (same date) - ONLY ACTIVE BOOKINGS
        for (Booking existing : customer.getBookings()) {
            // Skip cancelled bookings when checking for conflicts
            if (!existing.isCancelled() && 
                existing.getFlight().getDepartureDate().equals(flight.getDepartureDate())) {
                throw new FlightBookingSystemException(
                    "You already have an active booking on " + flight.getDepartureDate() + 
                    ". Cannot book overlapping flights.\n\n" +
                    "💡 Tip: If you want to book this flight, please cancel your existing booking first, " +
                    "or use the Rebook feature from your bookings page."
                );
            }
        }
        
        // Check availability
        if (flight.getAvailableSeats() <= 0) {
            throw new FlightBookingSystemException("No seats available on this flight.");
        }
        
        // Calculate dynamic price
        double price = DynamicPricingCalculator.calculatePrice(flight, flightBookingSystem.getSystemDate());
        
        // Create and add booking
        Booking booking = new Booking(customer, flight, LocalDate.now());
        booking.setBookingPrice(price);
        customer.addBooking(booking);
        flight.addPassenger(customer);
        
        System.out.println("Booking added successfully for " + customer.getName() + " on flight " + flight.getFlightNumber() + " at £" + String.format("%.2f", price));
    }
}