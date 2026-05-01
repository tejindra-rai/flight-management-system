package bcu.cmp5332.bookingsystem.model;

/**
 * Represents a user in the system with authentication.
 * Users can be either Admin or Customer type.
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class User {
    
    private int id;
    private String username;
    private String password;
    private String role; // "ADMIN" or "CUSTOMER"
    private int linkedCustomerId; // For customer role, links to Customer ID
    
    /**
     * Constructs a new User.
     * 
     * @param id the user ID
     * @param username the username for login
     * @param password the password (should be hashed in production)
     * @param role the user role (ADMIN or CUSTOMER)
     */
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.linkedCustomerId = -1; // -1 means no linked customer
    }
    
    /**
     * Gets the user ID.
     * 
     * @return the user ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the user ID.
     * 
     * @param id the user ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the username.
     * 
     * @return the username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username.
     * 
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password.
     * 
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Gets the user role.
     * 
     * @return the role (ADMIN or CUSTOMER)
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the user role.
     * 
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the linked customer ID (for customer role).
     * 
     * @return the linked customer ID, or -1 if not linked
     */
    public int getLinkedCustomerId() {
        return linkedCustomerId;
    }
    
    /**
     * Sets the linked customer ID.
     * 
     * @param linkedCustomerId the customer ID to link
     */
    public void setLinkedCustomerId(int linkedCustomerId) {
        this.linkedCustomerId = linkedCustomerId;
    }
    
    /**
     * Checks if this user is an admin.
     * 
     * @return true if admin, false otherwise
     */
    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }
    
    /**
     * Checks if this user is a customer.
     * 
     * @return true if customer, false otherwise
     */
    public boolean isCustomer() {
        return "CUSTOMER".equalsIgnoreCase(role);
    }
    
    /**
     * Validates the password.
     * 
     * @param inputPassword the password to check
     * @return true if password matches, false otherwise
     */
    public boolean validatePassword(String inputPassword) {
        return this.password.equals(inputPassword);
    }
    
    @Override
    public String toString() {
        return "User{" +
               "id=" + id +
               ", username='" + username + '\'' +
               ", role='" + role + '\'' +
               '}';
    }
}