package bcu.cmp5332.bookingsystem.dao;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object interface for User entities.
 */
public interface UserDAO extends DataAccessObject {

    User getUserById(int userId) throws SQLException, IOException, FlightBookingSystemException;

    User getUserByUsername(String username) throws SQLException, IOException, FlightBookingSystemException;

    boolean addUser(User user) throws SQLException, IOException;

    boolean updateUser(User user) throws SQLException, IOException;

    // ────────────────────────────────────────────────
    // This was missing → now added (fixes line 50 error)
    List<User> getAllUsers() throws SQLException, IOException, FlightBookingSystemException;
}