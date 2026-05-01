package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.EmailValidator;

/**
 * Command to add a new customer to the flight booking system with enhanced validation.
 * Includes email validation and proper ID generation.
 * 
 * @author Your Name
 * @version 3.0 - Fixed ID generation logic
 */
public class AddCustomer implements Command {

    private final String name;
    private final String phone;
    private final String email;
    private Customer createdCustomer; // Store created customer for retrieval

    /**
     * Constructs an AddCustomer command with basic customer details.
     * 
     * @param name the customer's name
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public AddCustomer(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
    
    /**
     * Gets the customer that was created by this command.
     * 
     * @return the created customer, or null if command hasn't been executed
     */
    public Customer getCreatedCustomer() {
        return createdCustomer;
    }

    /**
     * Executes the add customer command.
     * Generates a new unique ID and adds the customer to the system with validation.
     * 
     * @param flightBookingSystem the flight booking system
     * @throws FlightBookingSystemException if there's an error adding the customer or validation fails
     */
    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Validate name
        if (name == null || name.trim().isEmpty()) {
            throw new FlightBookingSystemException("Customer name cannot be empty.");
        }
        
        // Validate phone
        if (phone == null || phone.trim().isEmpty()) {
            throw new FlightBookingSystemException("Phone number cannot be empty.");
        }
        
        // Validate phone number format (basic check - only digits and common characters)
        if (!phone.matches("[0-9+\\-\\s()]+")) {
            throw new FlightBookingSystemException("Invalid phone number format. Use only numbers, +, -, (), and spaces.");
        }
        
        // Validate email using EmailValidator
        try {
            EmailValidator.validateEmailOrThrow(email);
        } catch (IllegalArgumentException e) {
            throw new FlightBookingSystemException(e.getMessage());
        }
        
        // Generate a new unique customer ID properly - iterate through all customers to find max ID
        int maxId = 0;
        for (Customer existingCustomer : flightBookingSystem.getCustomers()) {
            if (existingCustomer.getId() > maxId) {
                maxId = existingCustomer.getId();
            }
        }
        int newId = maxId + 1;
        
        // Create new customer with incremented ID
        createdCustomer = new Customer(newId, name, phone, email);
        
        // Add customer to the system
        flightBookingSystem.addCustomer(createdCustomer);
        
        System.out.println("Customer #" + createdCustomer.getId() + " added successfully.");
        System.out.println("Name: " + name);
        System.out.println("Email: " + email);
        System.out.println("Phone: " + phone);
    }
}