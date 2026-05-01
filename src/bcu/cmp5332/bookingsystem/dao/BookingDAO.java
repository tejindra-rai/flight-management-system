package bcu.cmp5332.bookingsystem.dao;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Booking entities.
 * Defines CRUD operations for bookings.
 * 
 * @author Flight Booking System Team
 * @version 1.0
 */
public interface BookingDAO extends DataAccessObject {
    
    /**
     * Retrieves all bookings for a specific customer.
     * 
     * @param customerId the ID of the customer
     * @return a list of Booking objects for the customer
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    List<Booking> getBookingsByCustomerId(int customerId) throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Retrieves all bookings for a specific flight.
     * 
     * @param flightId the ID of the flight
     * @return a list of Booking objects for the flight
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    List<Booking> getBookingsByFlightId(int flightId) throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Retrieves all bookings from the data source.
     * 
     * @return a list of all Booking objects
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    List<Booking> getAllBookings() throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Adds a new booking to the data source.
     * 
     * @param booking the Booking object to add
     * @return true if successfully added, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean addBooking(Booking booking) throws SQLException, IOException;
    
    /**
     * Updates an existing booking in the data source.
     * 
     * @param booking the Booking object with updated information
     * @return true if successfully updated, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean updateBooking(Booking booking) throws SQLException, IOException;
    
    /**
     * Cancels a booking (marks as cancelled).
     * 
     * @param customerId the customer ID
     * @param flightId the flight ID
     * @return true if successfully cancelled, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean cancelBooking(int customerId, int flightId) throws SQLException, IOException;
}