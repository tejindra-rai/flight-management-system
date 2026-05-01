package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.util.List;

/**
 * Command to list all active customers in the system.
 */
public class ListCustomers implements Command {

    /**
     * Executes the list customers command.
     * Displays all active (non-deleted) customers with their details.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if there's an error retrieving customers
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get only active customers (not deleted)
        List<Customer> customers = flightBookingSystem.getActiveCustomers();
        
        // Display each customer's short details
        for (Customer customer : customers) {
            System.out.println(customer.getDetailsShort());
        }
        
        // Display total count
        System.out.println(customers.size() + " customer(s)");
    }
}