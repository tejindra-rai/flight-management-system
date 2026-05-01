package bcu.cmp5332.bookingsystem.dao.mysql;

import bcu.cmp5332.bookingsystem.dao.FlightDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of FlightDAO.
 * Handles all database operations for Flight entities.
 * 
 * @author Flight Booking System Team
 * @version 1.0
 */
public class FlightMySQLDAO implements FlightDAO {
    
    private final DatabaseConnectionManager dbManager;
    
    /**
     * Constructor that initializes the database connection manager.
     */
    public FlightMySQLDAO() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, SQLException {
        // FIXED: Removed WHERE deleted = false to load ALL flights (including deleted)
        // This is important for data migration and maintaining data integrity
        String query = "SELECT * FROM flights ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String flightNumber = rs.getString("flight_number");
                String origin = rs.getString("origin");
                String destination = rs.getString("destination");
                LocalDate departureDate = rs.getDate("departure_date").toLocalDate();
                
                Flight flight = new Flight(id, flightNumber, origin, destination, departureDate);
                flight.setCapacity(rs.getInt("capacity"));
                flight.setBasePrice(rs.getDouble("base_price"));
                flight.setDeleted(rs.getBoolean("deleted"));
                flight.setFlightClass(rs.getString("flight_class"));
                flight.setReturnFlight(rs.getBoolean("is_return_flight"));
                
                fbs.addFlight(flight);
            }
        }
    }
    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException, SQLException {
        String query = "INSERT INTO flights (id, flight_number, origin, destination, departure_date, " +
                      "capacity, base_price, deleted, flight_class, is_return_flight) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "flight_number = VALUES(flight_number), " +
                      "origin = VALUES(origin), " +
                      "destination = VALUES(destination), " +
                      "departure_date = VALUES(departure_date), " +
                      "capacity = VALUES(capacity), " +
                      "base_price = VALUES(base_price), " +
                      "deleted = VALUES(deleted), " +
                      "flight_class = VALUES(flight_class), " +
                      "is_return_flight = VALUES(is_return_flight)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            for (Flight flight : fbs.getFlights()) {
                pstmt.setInt(1, flight.getId());
                pstmt.setString(2, flight.getFlightNumber());
                pstmt.setString(3, flight.getOrigin());
                pstmt.setString(4, flight.getDestination());
                pstmt.setDate(5, Date.valueOf(flight.getDepartureDate()));
                pstmt.setInt(6, flight.getCapacity());
                pstmt.setDouble(7, flight.getBasePrice());
                pstmt.setBoolean(8, flight.isDeleted());
                pstmt.setString(9, flight.getFlightClass());
                pstmt.setBoolean(10, flight.isReturnFlight());
                
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public Flight getFlightById(int flightId) throws SQLException, IOException, FlightBookingSystemException {
        String query = "SELECT * FROM flights WHERE id = ? AND deleted = false";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, flightId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String flightNumber = rs.getString("flight_number");
                    String origin = rs.getString("origin");
                    String destination = rs.getString("destination");
                    LocalDate departureDate = rs.getDate("departure_date").toLocalDate();
                    
                    Flight flight = new Flight(flightId, flightNumber, origin, destination, departureDate);
                    flight.setCapacity(rs.getInt("capacity"));
                    flight.setBasePrice(rs.getDouble("base_price"));
                    flight.setDeleted(rs.getBoolean("deleted"));
                    flight.setFlightClass(rs.getString("flight_class"));
                    flight.setReturnFlight(rs.getBoolean("is_return_flight"));
                    
                    return flight;
                }
            }
        }
        return null;
    }
    
    @Override
    public List<Flight> getAllFlights() throws SQLException, IOException, FlightBookingSystemException {
        List<Flight> flights = new ArrayList<>();
        String query = "SELECT * FROM flights WHERE deleted = false ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String flightNumber = rs.getString("flight_number");
                String origin = rs.getString("origin");
                String destination = rs.getString("destination");
                LocalDate departureDate = rs.getDate("departure_date").toLocalDate();
                
                Flight flight = new Flight(id, flightNumber, origin, destination, departureDate);
                flight.setCapacity(rs.getInt("capacity"));
                flight.setBasePrice(rs.getDouble("base_price"));
                flight.setDeleted(rs.getBoolean("deleted"));
                flight.setFlightClass(rs.getString("flight_class"));
                flight.setReturnFlight(rs.getBoolean("is_return_flight"));
                
                flights.add(flight);
            }
        }
        return flights;
    }
    
    @Override
    public boolean addFlight(Flight flight) throws SQLException, IOException {
        String query = "INSERT INTO flights (id, flight_number, origin, destination, departure_date, " +
                      "capacity, base_price, deleted, flight_class, is_return_flight) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, flight.getId());
            pstmt.setString(2, flight.getFlightNumber());
            pstmt.setString(3, flight.getOrigin());
            pstmt.setString(4, flight.getDestination());
            pstmt.setDate(5, Date.valueOf(flight.getDepartureDate()));
            pstmt.setInt(6, flight.getCapacity());
            pstmt.setDouble(7, flight.getBasePrice());
            pstmt.setBoolean(8, flight.isDeleted());
            pstmt.setString(9, flight.getFlightClass());
            pstmt.setBoolean(10, flight.isReturnFlight());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean updateFlight(Flight flight) throws SQLException, IOException {
        String query = "UPDATE flights SET flight_number = ?, origin = ?, destination = ?, " +
                      "departure_date = ?, capacity = ?, base_price = ?, deleted = ?, " +
                      "flight_class = ?, is_return_flight = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, flight.getFlightNumber());
            pstmt.setString(2, flight.getOrigin());
            pstmt.setString(3, flight.getDestination());
            pstmt.setDate(4, Date.valueOf(flight.getDepartureDate()));
            pstmt.setInt(5, flight.getCapacity());
            pstmt.setDouble(6, flight.getBasePrice());
            pstmt.setBoolean(7, flight.isDeleted());
            pstmt.setString(8, flight.getFlightClass());
            pstmt.setBoolean(9, flight.isReturnFlight());
            pstmt.setInt(10, flight.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean deleteFlight(int flightId) throws SQLException, IOException {
        // Soft delete - mark as deleted instead of removing from database
        String query = "UPDATE flights SET deleted = true WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, flightId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}