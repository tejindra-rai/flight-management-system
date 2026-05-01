package bcu.cmp5332.bookingsystem.dao.mysql;

import bcu.cmp5332.bookingsystem.dao.BookingDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BookingMySQLDAO implements BookingDAO {
    
    private final DatabaseConnectionManager dbManager;
    
    public BookingMySQLDAO() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, SQLException {
        String query = "SELECT * FROM bookings ORDER BY customer_id, flight_id";
        
        int loadedCount = 0;
        int skippedCount = 0;
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int customerId = rs.getInt("customer_id");
                int flightId = rs.getInt("flight_id");
                LocalDate bookingDate = rs.getDate("booking_date").toLocalDate();
                
                try {
                    Customer customer = fbs.getCustomerByID(customerId);
                    Flight flight = fbs.getFlightByID(flightId);
                    
                    if (customer != null && flight != null) {
                        Booking booking = new Booking(customer, flight, bookingDate);
                        booking.setCancelled(rs.getBoolean("cancelled"));
                        booking.setBookingPrice(rs.getDouble("booking_price"));
                        booking.setCancellationFee(rs.getDouble("cancellation_fee"));
                        
                        // NEW FIELDS
                        String mealPref = rs.getString("meal_preference");
                        if (mealPref != null && !mealPref.isEmpty()) {
                            booking.setMealPreference(mealPref);
                        }
                        
                        String seatNum = rs.getString("seat_number");
                        if (seatNum != null && !seatNum.isEmpty()) {
                            booking.setSeatNumber(seatNum);
                        }
                        
                        customer.addBooking(booking);
                        
                        if (!booking.isCancelled()) {
                            flight.addPassenger(customer);
                        }
                        
                        loadedCount++;
                    } else {
                        System.out.println("  ⚠ Skipping booking: Customer or Flight is null");
                        skippedCount++;
                    }
                } catch (FlightBookingSystemException e) {
                    System.out.println("  ⚠ Skipping booking: " + e.getMessage());
                    skippedCount++;
                }
            }
        }
        
        if (skippedCount > 0) {
            System.out.println("  ℹ Loaded " + loadedCount + " bookings, skipped " + skippedCount);
        }
    }
    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException, SQLException {
        String query = "INSERT INTO bookings (customer_id, flight_id, booking_date, cancelled, " +
                      "booking_price, cancellation_fee, meal_preference, seat_number) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "booking_date = VALUES(booking_date), " +
                      "cancelled = VALUES(cancelled), " +
                      "booking_price = VALUES(booking_price), " +
                      "cancellation_fee = VALUES(cancellation_fee), " +
                      "meal_preference = VALUES(meal_preference), " +
                      "seat_number = VALUES(seat_number)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            for (Customer customer : fbs.getCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    pstmt.setInt(1, customer.getId());
                    pstmt.setInt(2, booking.getFlight().getId());
                    pstmt.setDate(3, Date.valueOf(booking.getBookingDate()));
                    pstmt.setBoolean(4, booking.isCancelled());
                    pstmt.setDouble(5, booking.getBookingPrice());
                    pstmt.setDouble(6, booking.getCancellationFee());
                    pstmt.setString(7, booking.getMealPreference());
                    pstmt.setString(8, booking.getSeatNumber());
                    
                    pstmt.executeUpdate();
                }
            }
        }
    }
    
    @Override
    public List<Booking> getBookingsByCustomerId(int customerId) throws SQLException, IOException, FlightBookingSystemException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, c.*, f.* " +
                      "FROM bookings b " +
                      "JOIN customers c ON b.customer_id = c.id " +
                      "JOIN flights f ON b.flight_id = f.id " +
                      "WHERE b.customer_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Create Customer
                    Customer customer = new Customer(
                        rs.getInt("c.id"),
                        rs.getString("c.name"),
                        rs.getString("c.phone"),
                        rs.getString("c.email")
                    );
                    customer.setDeleted(rs.getBoolean("c.deleted"));
                    customer.setHasChildren(rs.getBoolean("c.has_children"));
                    customer.setAgeGroup(rs.getString("c.age_group"));
                    customer.setMealPreference(rs.getString("c.meal_preference"));
                    
                    // Create Flight
                    Flight flight = new Flight(
                        rs.getInt("f.id"),
                        rs.getString("f.flight_number"),
                        rs.getString("f.origin"),
                        rs.getString("f.destination"),
                        rs.getDate("f.departure_date").toLocalDate()
                    );
                    flight.setCapacity(rs.getInt("f.capacity"));
                    flight.setBasePrice(rs.getDouble("f.base_price"));
                    flight.setDeleted(rs.getBoolean("f.deleted"));
                    flight.setFlightClass(rs.getString("f.flight_class"));
                    flight.setReturnFlight(rs.getBoolean("f.is_return_flight"));
                    
                    // Create Booking
                    Booking booking = new Booking(
                        customer,
                        flight,
                        rs.getDate("b.booking_date").toLocalDate()
                    );
                    booking.setCancelled(rs.getBoolean("b.cancelled"));
                    booking.setBookingPrice(rs.getDouble("b.booking_price"));
                    booking.setCancellationFee(rs.getDouble("b.cancellation_fee"));
                    
                    String mealPref = rs.getString("b.meal_preference");
                    if (mealPref != null && !mealPref.isEmpty()) {
                        booking.setMealPreference(mealPref);
                    }
                    
                    String seatNum = rs.getString("b.seat_number");
                    if (seatNum != null && !seatNum.isEmpty()) {
                        booking.setSeatNumber(seatNum);
                    }
                    
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }
    
    @Override
    public List<Booking> getBookingsByFlightId(int flightId) throws SQLException, IOException, FlightBookingSystemException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, c.*, f.* " +
                      "FROM bookings b " +
                      "JOIN customers c ON b.customer_id = c.id " +
                      "JOIN flights f ON b.flight_id = f.id " +
                      "WHERE b.flight_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, flightId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    // Create Customer
                    Customer customer = new Customer(
                        rs.getInt("c.id"),
                        rs.getString("c.name"),
                        rs.getString("c.phone"),
                        rs.getString("c.email")
                    );
                    customer.setDeleted(rs.getBoolean("c.deleted"));
                    customer.setHasChildren(rs.getBoolean("c.has_children"));
                    customer.setAgeGroup(rs.getString("c.age_group"));
                    customer.setMealPreference(rs.getString("c.meal_preference"));
                    
                    // Create Flight
                    Flight flight = new Flight(
                        rs.getInt("f.id"),
                        rs.getString("f.flight_number"),
                        rs.getString("f.origin"),
                        rs.getString("f.destination"),
                        rs.getDate("f.departure_date").toLocalDate()
                    );
                    flight.setCapacity(rs.getInt("f.capacity"));
                    flight.setBasePrice(rs.getDouble("f.base_price"));
                    flight.setDeleted(rs.getBoolean("f.deleted"));
                    flight.setFlightClass(rs.getString("f.flight_class"));
                    flight.setReturnFlight(rs.getBoolean("f.is_return_flight"));
                    
                    // Create Booking
                    Booking booking = new Booking(
                        customer,
                        flight,
                        rs.getDate("b.booking_date").toLocalDate()
                    );
                    booking.setCancelled(rs.getBoolean("b.cancelled"));
                    booking.setBookingPrice(rs.getDouble("b.booking_price"));
                    booking.setCancellationFee(rs.getDouble("b.cancellation_fee"));
                    
                    String mealPref = rs.getString("b.meal_preference");
                    if (mealPref != null && !mealPref.isEmpty()) {
                        booking.setMealPreference(mealPref);
                    }
                    
                    String seatNum = rs.getString("b.seat_number");
                    if (seatNum != null && !seatNum.isEmpty()) {
                        booking.setSeatNumber(seatNum);
                    }
                    
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }
    
    @Override
    public List<Booking> getAllBookings() throws SQLException, IOException, FlightBookingSystemException {
        List<Booking> bookings = new ArrayList<>();
        String query = "SELECT b.*, c.*, f.* " +
                      "FROM bookings b " +
                      "JOIN customers c ON b.customer_id = c.id " +
                      "JOIN flights f ON b.flight_id = f.id " +
                      "ORDER BY b.customer_id, b.flight_id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                // Create Customer
                Customer customer = new Customer(
                    rs.getInt("c.id"),
                    rs.getString("c.name"),
                    rs.getString("c.phone"),
                    rs.getString("c.email")
                );
                customer.setDeleted(rs.getBoolean("c.deleted"));
                customer.setHasChildren(rs.getBoolean("c.has_children"));
                customer.setAgeGroup(rs.getString("c.age_group"));
                customer.setMealPreference(rs.getString("c.meal_preference"));
                
                // Create Flight
                Flight flight = new Flight(
                    rs.getInt("f.id"),
                    rs.getString("f.flight_number"),
                    rs.getString("f.origin"),
                    rs.getString("f.destination"),
                    rs.getDate("f.departure_date").toLocalDate()
                );
                flight.setCapacity(rs.getInt("f.capacity"));
                flight.setBasePrice(rs.getDouble("f.base_price"));
                flight.setDeleted(rs.getBoolean("f.deleted"));
                flight.setFlightClass(rs.getString("f.flight_class"));
                flight.setReturnFlight(rs.getBoolean("f.is_return_flight"));
                
                // Create Booking
                Booking booking = new Booking(
                    customer,
                    flight,
                    rs.getDate("b.booking_date").toLocalDate()
                );
                booking.setCancelled(rs.getBoolean("b.cancelled"));
                booking.setBookingPrice(rs.getDouble("b.booking_price"));
                booking.setCancellationFee(rs.getDouble("b.cancellation_fee"));
                
                String mealPref = rs.getString("b.meal_preference");
                if (mealPref != null && !mealPref.isEmpty()) {
                    booking.setMealPreference(mealPref);
                }
                
                String seatNum = rs.getString("b.seat_number");
                if (seatNum != null && !seatNum.isEmpty()) {
                    booking.setSeatNumber(seatNum);
                }
                
                bookings.add(booking);
            }
        }
        return bookings;
    }
    
    @Override
    public boolean addBooking(Booking booking) throws SQLException, IOException {
        String query = "INSERT INTO bookings (customer_id, flight_id, booking_date, cancelled, " +
                      "booking_price, cancellation_fee, meal_preference, seat_number) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, booking.getCustomer().getId());
            pstmt.setInt(2, booking.getFlight().getId());
            pstmt.setDate(3, Date.valueOf(booking.getBookingDate()));
            pstmt.setBoolean(4, booking.isCancelled());
            pstmt.setDouble(5, booking.getBookingPrice());
            pstmt.setDouble(6, booking.getCancellationFee());
            pstmt.setString(7, booking.getMealPreference());
            pstmt.setString(8, booking.getSeatNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean updateBooking(Booking booking) throws SQLException, IOException {
        String query = "UPDATE bookings SET booking_date = ?, cancelled = ?, " +
                      "booking_price = ?, cancellation_fee = ?, " +
                      "meal_preference = ?, seat_number = ? " +
                      "WHERE customer_id = ? AND flight_id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setDate(1, Date.valueOf(booking.getBookingDate()));
            pstmt.setBoolean(2, booking.isCancelled());
            pstmt.setDouble(3, booking.getBookingPrice());
            pstmt.setDouble(4, booking.getCancellationFee());
            pstmt.setString(5, booking.getMealPreference());
            pstmt.setString(6, booking.getSeatNumber());
            pstmt.setInt(7, booking.getCustomer().getId());
            pstmt.setInt(8, booking.getFlight().getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean cancelBooking(int customerId, int flightId) throws SQLException, IOException {
        // First check if booking exists and is not already cancelled
        String checkQuery = "SELECT cancelled FROM bookings WHERE customer_id = ? AND flight_id = ?";
        String updateQuery = "UPDATE bookings SET cancelled = true WHERE customer_id = ? AND flight_id = ? AND cancelled = false";
        
        try (Connection conn = dbManager.getConnection()) {
            // Check if booking exists and get current cancelled status
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setInt(1, customerId);
                checkStmt.setInt(2, flightId);
                
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (!rs.next()) {
                        // Booking doesn't exist
                        return false;
                    }
                    
                    boolean alreadyCancelled = rs.getBoolean("cancelled");
                    if (alreadyCancelled) {
                        // Already cancelled
                        return false;
                    }
                }
            }
            
            // Update to cancelled
            try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, customerId);
                updateStmt.setInt(2, flightId);
                
                int rowsAffected = updateStmt.executeUpdate();
                return rowsAffected > 0;
            }
        }
    }
}