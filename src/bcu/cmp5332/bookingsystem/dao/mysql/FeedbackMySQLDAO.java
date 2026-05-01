package bcu.cmp5332.bookingsystem.dao.mysql;

import bcu.cmp5332.bookingsystem.dao.DataAccessObject;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Feedback;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FeedbackMySQLDAO implements DataAccessObject {
    
    private final DatabaseConnectionManager dbManager;
    
    public FeedbackMySQLDAO() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, SQLException {
        String query = "SELECT * FROM feedback ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                int flightId = rs.getInt("flight_id");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                LocalDate feedbackDate = rs.getDate("feedback_date").toLocalDate();
                
                Feedback feedback = new Feedback(id, customerId, flightId, rating, comment, feedbackDate);
                fbs.addFeedback(feedback);
            }
        }
    }
    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException, SQLException {
        String query = "INSERT INTO feedback (id, customer_id, flight_id, rating, comment, feedback_date) " +
                      "VALUES (?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "rating = VALUES(rating), " +
                      "comment = VALUES(comment), " +
                      "feedback_date = VALUES(feedback_date)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            for (Feedback feedback : fbs.getAllFeedback()) {
                pstmt.setInt(1, feedback.getId());
                pstmt.setInt(2, feedback.getCustomerId());
                pstmt.setInt(3, feedback.getFlightId());
                pstmt.setInt(4, feedback.getRating());
                pstmt.setString(5, feedback.getComment());
                pstmt.setDate(6, Date.valueOf(feedback.getFeedbackDate()));
                
                pstmt.executeUpdate();
            }
        }
    }
    
    public List<Feedback> getAllFeedback() throws SQLException, IOException, FlightBookingSystemException {
        List<Feedback> feedbacks = new ArrayList<>();
        String query = "SELECT * FROM feedback ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                int flightId = rs.getInt("flight_id");
                int rating = rs.getInt("rating");
                String comment = rs.getString("comment");
                LocalDate feedbackDate = rs.getDate("feedback_date").toLocalDate();
                
                Feedback feedback = new Feedback(id, customerId, flightId, rating, comment, feedbackDate);
                feedbacks.add(feedback);
            }
        }
        return feedbacks;
    }
    
    public boolean addFeedback(Feedback feedback) throws SQLException, IOException {
        String query = "INSERT INTO feedback (id, customer_id, flight_id, rating, comment, feedback_date) " +
                      "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, feedback.getId());
            pstmt.setInt(2, feedback.getCustomerId());
            pstmt.setInt(3, feedback.getFlightId());
            pstmt.setInt(4, feedback.getRating());
            pstmt.setString(5, feedback.getComment());
            pstmt.setDate(6, Date.valueOf(feedback.getFeedbackDate()));
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteFeedback(int feedbackId) throws SQLException, IOException {
        String query = "DELETE FROM feedback WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, feedbackId);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
}