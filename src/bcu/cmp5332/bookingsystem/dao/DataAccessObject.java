package bcu.cmp5332.bookingsystem.dao;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Generic Data Access Object interface for loading and storing data.
 * This interface defines the contract for all data access implementations,
 * whether file-based or database-based.
 * 
 * @author Flight Booking System Team
 * @version 1.0
 */
public interface DataAccessObject {
    
    /**
     * Loads data from the data source into the FlightBookingSystem.
     * 
     * @param fbs the FlightBookingSystem instance to load data into
     * @throws IOException if there's an error reading from the data source
     * @throws FlightBookingSystemException if there's an error processing the data
     * @throws SQLException if there's a database error (for DB implementations)
     */
    void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, SQLException;
    
    /**
     * Stores data from the FlightBookingSystem to the data source.
     * 
     * @param fbs the FlightBookingSystem instance to save data from
     * @throws IOException if there's an error writing to the data source
     * @throws SQLException if there's a database error (for DB implementations)
     */
    void storeData(FlightBookingSystem fbs) throws IOException, SQLException;
}