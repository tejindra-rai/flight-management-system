package bcu.cmp5332.bookingsystem.dao;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Customer entities.
 * Defines CRUD operations for customers.
 * 
 * @author Flight Booking System Team
 * @version 1.0
 */
public interface CustomerDAO extends DataAccessObject {
    
    /**
     * Retrieves a customer by their ID.
     * 
     * @param customerId the ID of the customer to retrieve
     * @return the Customer object, or null if not found
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    Customer getCustomerById(int customerId) throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Retrieves all customers from the data source.
     * 
     * @return a list of all Customer objects
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    List<Customer> getAllCustomers() throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Adds a new customer to the data source.
     * 
     * @param customer the Customer object to add
     * @return true if successfully added, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean addCustomer(Customer customer) throws SQLException, IOException;
    
    /**
     * Updates an existing customer in the data source.
     * 
     * @param customer the Customer object with updated information
     * @return true if successfully updated, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean updateCustomer(Customer customer) throws SQLException, IOException;
    
    /**
     * Deletes a customer from the data source (soft delete).
     * 
     * @param customerId the ID of the customer to delete
     * @return true if successfully deleted, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean deleteCustomer(int customerId) throws SQLException, IOException;
}