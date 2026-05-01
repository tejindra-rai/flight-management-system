package bcu.cmp5332.bookingsystem.dao.mysql;

import bcu.cmp5332.bookingsystem.dao.CustomerDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of CustomerDAO.
 * Handles all database operations for Customer entities.
 * 
 * @author Flight Booking System Team
 * @version 1.1 - Fixed to load all customers including deleted ones
 */
public class CustomerMySQLDAO implements CustomerDAO {
    
    private final DatabaseConnectionManager dbManager;
    
    /**
     * Constructor that initializes the database connection manager.
     */
    public CustomerMySQLDAO() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }
    
    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, SQLException {
        // FIXED: Removed WHERE deleted = false to load ALL customers (including deleted)
        // This is important for data migration and maintaining data integrity
        String query = "SELECT * FROM customers ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                
                Customer customer = new Customer(id, name, phone, email);
                customer.setDeleted(rs.getBoolean("deleted"));
                customer.setHasChildren(rs.getBoolean("has_children"));
                customer.setAgeGroup(rs.getString("age_group"));
                customer.setMealPreference(rs.getString("meal_preference"));
                
                fbs.addCustomer(customer);
            }
        }
    }
    
    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException, SQLException {
        String query = "INSERT INTO customers (id, name, phone, email, deleted, has_children, " +
                      "age_group, meal_preference) VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                      "ON DUPLICATE KEY UPDATE " +
                      "name = VALUES(name), " +
                      "phone = VALUES(phone), " +
                      "email = VALUES(email), " +
                      "deleted = VALUES(deleted), " +
                      "has_children = VALUES(has_children), " +
                      "age_group = VALUES(age_group), " +
                      "meal_preference = VALUES(meal_preference)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            for (Customer customer : fbs.getCustomers()) {
                pstmt.setInt(1, customer.getId());
                pstmt.setString(2, customer.getName());
                pstmt.setString(3, customer.getPhone());
                pstmt.setString(4, customer.getEmail());
                pstmt.setBoolean(5, customer.isDeleted());
                pstmt.setBoolean(6, customer.hasChildren());
                pstmt.setString(7, customer.getAgeGroup());
                pstmt.setString(8, customer.getMealPreference());
                
                pstmt.executeUpdate();
            }
        }
    }
    
    @Override
    public Customer getCustomerById(int customerId) throws SQLException, IOException, FlightBookingSystemException {
        // NOTE: For normal operations, we still filter out deleted customers
        // This method is used during regular application operations, not migration
        String query = "SELECT * FROM customers WHERE id = ? AND deleted = false";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, customerId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String phone = rs.getString("phone");
                    String email = rs.getString("email");
                    
                    Customer customer = new Customer(customerId, name, phone, email);
                    customer.setDeleted(rs.getBoolean("deleted"));
                    customer.setHasChildren(rs.getBoolean("has_children"));
                    customer.setAgeGroup(rs.getString("age_group"));
                    customer.setMealPreference(rs.getString("meal_preference"));
                    
                    return customer;
                }
            }
        }
        
        throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
    }
    
    @Override
    public boolean addCustomer(Customer customer) throws SQLException, IOException {
        String query = "INSERT INTO customers (id, name, phone, email, deleted, has_children, " +
                      "age_group, meal_preference) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, customer.getId());
            pstmt.setString(2, customer.getName());
            pstmt.setString(3, customer.getPhone());
            pstmt.setString(4, customer.getEmail());
            pstmt.setBoolean(5, customer.isDeleted());
            pstmt.setBoolean(6, customer.hasChildren());
            pstmt.setString(7, customer.getAgeGroup());
            pstmt.setString(8, customer.getMealPreference());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean updateCustomer(Customer customer) throws SQLException, IOException {
        String query = "UPDATE customers SET name = ?, phone = ?, email = ?, deleted = ?, " +
                      "has_children = ?, age_group = ?, meal_preference = ? WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getPhone());
            pstmt.setString(3, customer.getEmail());
            pstmt.setBoolean(4, customer.isDeleted());
            pstmt.setBoolean(5, customer.hasChildren());
            pstmt.setString(6, customer.getAgeGroup());
            pstmt.setString(7, customer.getMealPreference());
            pstmt.setInt(8, customer.getId());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public boolean deleteCustomer(int customerId) throws SQLException, IOException {
        // Soft delete - just mark as deleted
        String query = "UPDATE customers SET deleted = true WHERE id = ?";
        
        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, customerId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    @Override
    public List<Customer> getAllCustomers() throws SQLException, IOException, FlightBookingSystemException {
        List<Customer> customers = new ArrayList<>();
        // For normal operations, only return non-deleted customers
        String query = "SELECT * FROM customers WHERE deleted = false ORDER BY id";
        
        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String phone = rs.getString("phone");
                String email = rs.getString("email");
                
                Customer customer = new Customer(id, name, phone, email);
                customer.setDeleted(rs.getBoolean("deleted"));
                customer.setHasChildren(rs.getBoolean("has_children"));
                customer.setAgeGroup(rs.getString("age_group"));
                customer.setMealPreference(rs.getString("meal_preference"));
                
                customers.add(customer);
            }
        }
        
        return customers;
    }
}