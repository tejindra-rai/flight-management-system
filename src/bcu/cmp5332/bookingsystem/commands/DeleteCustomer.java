package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

/**
 * Command to delete (hide) a customer from the system.
 * Uses soft delete - the customer is marked as deleted but not removed from storage.
 */
public class DeleteCustomer implements Command {

    private final int customerId;

    /**
     * Constructs a DeleteCustomer command for the specified customer ID.
     * 
     * @param customerId the ID of the customer to delete
     */
    public DeleteCustomer(int customerId) {
        this.customerId = customerId;
    }

    /**
     * Executes the delete customer command.
     * Marks the customer as deleted so they won't appear in customer lists.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if the customer ID is invalid or already deleted
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get the customer by ID
        Customer customer = flightBookingSystem.getCustomerByID(customerId);
        
        // Check if customer is already deleted
        if (customer.isDeleted()) {
            throw new FlightBookingSystemException("Customer is already deleted.");
        }
        
        // Soft delete - mark as deleted
        customer.setDeleted(true);
        
        System.out.println("Customer #" + customerId + " (" + customer.getName() + ") deleted successfully.");
    }
}