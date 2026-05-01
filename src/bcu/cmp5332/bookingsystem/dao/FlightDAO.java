package bcu.cmp5332.bookingsystem.dao;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for Flight entities.
 * Defines CRUD operations for flights.
 * 
 * @author Flight Booking System Team
 * @version 1.0
 */
public interface FlightDAO extends DataAccessObject {
    
    /**
     * Retrieves a flight by its ID.
     * 
     * @param flightId the ID of the flight to retrieve
     * @return the Flight object, or null if not found
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    Flight getFlightById(int flightId) throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Retrieves all flights from the data source.
     * 
     * @return a list of all Flight objects
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     * @throws FlightBookingSystemException if there's a system error
     */
    List<Flight> getAllFlights() throws SQLException, IOException, FlightBookingSystemException;
    
    /**
     * Adds a new flight to the data source.
     * 
     * @param flight the Flight object to add
     * @return true if successfully added, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean addFlight(Flight flight) throws SQLException, IOException;
    
    /**
     * Updates an existing flight in the data source.
     * 
     * @param flight the Flight object with updated information
     * @return true if successfully updated, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean updateFlight(Flight flight) throws SQLException, IOException;
    
    /**
     * Deletes a flight from the data source (soft delete).
     * 
     * @param flightId the ID of the flight to delete
     * @return true if successfully deleted, false otherwise
     * @throws SQLException if there's a database error
     * @throws IOException if there's a file I/O error
     */
    boolean deleteFlight(int flightId) throws SQLException, IOException;
}