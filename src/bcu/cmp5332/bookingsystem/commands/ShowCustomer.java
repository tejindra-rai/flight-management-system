package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to display detailed information about a specific customer.
 */
public class ShowCustomer implements Command {

    private final int customerId;

    /**
     * Constructs a ShowCustomer command for the specified customer ID.
     * 
     * @param customerId the ID of the customer to display
     */
    public ShowCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the show customer command.
     * Retrieves and displays detailed information about the customer including all bookings.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if the customer ID is invalid
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get the customer by ID
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        
        // Display detailed customer information including booking history
        System.out.println(customer.getDetailsLong());
    }
}