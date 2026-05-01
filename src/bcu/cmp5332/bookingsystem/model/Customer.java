package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a customer in the flight booking system.
 * Each customer has personal information and a list of bookings they have made.
 */
public class Customer {
    
    private int id;
    private String name;
    private String phone;
    private String email;
    private boolean deleted;
    private boolean hasChildren;
    private String ageGroup;
    private String mealPreference;
    private final List<Booking> bookings = new ArrayList<>();
    
    /**
     * Constructs a new Customer with the specified details.
     * 
     * @param id the unique identifier for the customer
     * @param name the customer's name
     * @param phone the customer's phone number
     * @param email the customer's email address
     */
    public Customer(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.deleted = false;
        this.hasChildren = false;
        this.ageGroup = "Adult";
        this.mealPreference = "None";
    }
    
    /**
     * Gets the customer's unique ID.
     * 
     * @return the customer ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the customer's ID.
     * 
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the customer's name.
     * 
     * @return the customer name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the customer's name.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the customer's phone number.
     * 
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }
    
    /**
     * Sets the customer's phone number.
     * 
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    /**
     * Gets the customer's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the customer's email address.
     * 
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Checks if this customer has been deleted (soft delete).
     * 
     * @return true if deleted, false otherwise
     */
    public boolean isDeleted() {
        return deleted;
    }
    
    /**
     * Sets the deleted status of this customer.
     * 
     * @param deleted true to mark as deleted, false otherwise
     */
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    /**
     * Checks if the customer is traveling with children.
     * 
     * @return true if has children, false otherwise
     */
    public boolean hasChildren() {
        return hasChildren;
    }
    
    /**
     * Sets whether the customer has children.
     * 
     * @param hasChildren true if traveling with children
     */
    public void setHasChildren(boolean hasChildren) {
        this.hasChildren = hasChildren;
    }
    
    /**
     * Gets the customer's age group.
     * 
     * @return the age group (Child, Adult, Senior)
     */
    public String getAgeGroup() {
        return ageGroup;
    }
    
    /**
     * Sets the customer's age group.
     * 
     * @param ageGroup the age group to set
     */
    public void setAgeGroup(String ageGroup) {
        this.ageGroup = ageGroup;
    }
    
    /**
     * Gets the customer's meal preference.
     * 
     * @return the meal preference (Veg/Non-Veg/None)
     */
    public String getMealPreference() {
        return mealPreference;
    }
    
    /**
     * Sets the customer's meal preference.
     * 
     * @param mealPreference the meal preference to set
     */
    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }
    
    /**
     * Gets the list of bookings made by this customer.
     * 
     * @return list of bookings
     */
    public List<Booking> getBookings() {
        return bookings;
    }
    
    /**
     * Adds a booking to this customer's list of bookings.
     * 
     * @param booking the booking to add
     */
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }
    
    /**
     * Gets a short summary of customer details.
     * 
     * @return a string with customer ID, name, phone, and number of bookings
     */
    public String getDetailsShort() {
        return "Customer #" + id + " - " + name + " - Phone: " + phone 
                + " - Bookings: " + bookings.size();
    }
    
    /**
     * Gets detailed information about the customer including all bookings.
     * 
     * @return a formatted string with complete customer details
     */
    public String getDetailsLong() {
        StringBuilder details = new StringBuilder();
        details.append("Customer #").append(id).append("\n");
        details.append("Name: ").append(name).append("\n");
        details.append("Phone: ").append(phone).append("\n");
        details.append("Email: ").append(email).append("\n");
        details.append("Age Group: ").append(ageGroup).append("\n");
        details.append("Has Children: ").append(hasChildren ? "Yes" : "No").append("\n");
        details.append("Meal Preference: ").append(mealPreference).append("\n");
        details.append("Bookings:\n");
        
        if (bookings.isEmpty()) {
            details.append("  No bookings found.\n");
        } else {
            for (Booking booking : bookings) {
                if (!booking.isCancelled()) {
                    Flight f = booking.getFlight();
                    details.append("  Flight #").append(f.getId())
                           .append(" - ").append(f.getFlightNumber())
                           .append(" (").append(f.getOrigin())
                           .append(" to ").append(f.getDestination())
                           .append(") on ").append(f.getDepartureDate())
                           .append(" - Price: \u00A3").append(String.format("%.2f", booking.getBookingPrice()));
                    
                    if (booking.getCancellationFee() > 0) {
                        details.append(" (Cancellation Fee: \u00A3")
                               .append(String.format("%.2f", booking.getCancellationFee()))
                               .append(")");
                    }
                    details.append("\n");
                }
            }
        }
        
        return details.toString();
    }
}