package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;

/**
 * Represents customer feedback for a flight.
 * Customers can rate flights and leave comments.
 * 
 * @author Binay Chaudhary
 * @version 1.0
 */
public class Feedback {
    
    private int id;
    private int customerId;
    private int flightId;
    private int rating; // 1-5 stars
    private String comment;
    private LocalDate feedbackDate;
    
    /**
     * Constructs a new Feedback.
     * 
     * @param id the feedback ID
     * @param customerId the customer who gave feedback
     * @param flightId the flight being reviewed
     * @param rating the rating (1-5 stars)
     * @param comment the feedback comment
     * @param feedbackDate the date feedback was given
     */
    public Feedback(int id, int customerId, int flightId, int rating, String comment, LocalDate feedbackDate) {
        this.id = id;
        this.customerId = customerId;
        this.flightId = flightId;
        this.rating = Math.max(1, Math.min(5, rating)); // Ensure rating is between 1-5
        this.comment = comment;
        this.feedbackDate = feedbackDate;
    }
    
    /**
     * Gets the feedback ID.
     * 
     * @return the feedback ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the feedback ID.
     * 
     * @param id the ID to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the customer ID.
     * 
     * @return the customer ID
     */
    public int getCustomerId() {
        return customerId;
    }
    
    /**
     * Sets the customer ID.
     * 
     * @param customerId the customer ID to set
     */
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    /**
     * Gets the flight ID.
     * 
     * @return the flight ID
     */
    public int getFlightId() {
        return flightId;
    }
    
    /**
     * Sets the flight ID.
     * 
     * @param flightId the flight ID to set
     */
    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }
    
    /**
     * Gets the rating.
     * 
     * @return the rating (1-5)
     */
    public int getRating() {
        return rating;
    }
    
    /**
     * Sets the rating.
     * 
     * @param rating the rating to set (1-5)
     */
    public void setRating(int rating) {
        this.rating = Math.max(1, Math.min(5, rating)); // Ensure between 1-5
    }
    
    /**
     * Gets the feedback comment.
     * 
     * @return the comment
     */
    public String getComment() {
        return comment;
    }
    
    /**
     * Sets the feedback comment.
     * 
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
    
    /**
     * Gets the feedback date.
     * 
     * @return the feedback date
     */
    public LocalDate getFeedbackDate() {
        return feedbackDate;
    }
    
    /**
     * Sets the feedback date.
     * 
     * @param feedbackDate the date to set
     */
    public void setFeedbackDate(LocalDate feedbackDate) {
        this.feedbackDate = feedbackDate;
    }
    
    /**
     * Gets a star rating display (e.g., "⭐⭐⭐⭐⭐").
     * 
     * @return star rating string
     */
    public String getStarRating() {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("⭐");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
    
    @Override
    public String toString() {
        return "Feedback{" +
               "id=" + id +
               ", customerId=" + customerId +
               ", flightId=" + flightId +
               ", rating=" + rating +
               ", date=" + feedbackDate +
               '}';
    }
}