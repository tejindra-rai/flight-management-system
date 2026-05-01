package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Feedback;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.time.LocalDate;

/**
 * Command to add feedback for a flight.
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class AddFeedback implements Command {

    private final int customerId;
    private final int flightId;
    private final int rating;
    private final String comment;

    /**
     * Constructs an AddFeedback command.
     * 
     * @param customerId the customer ID
     * @param flightId the flight ID
     * @param rating the rating (1-5)
     * @param comment the feedback comment
     */
    public AddFeedback(int customerId, int flightId, int rating, String comment) {
        this.customerId = customerId;
        this.flightId = flightId;
        this.rating = rating;
        this.comment = comment;
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Validate customer exists
        flightBookingSystem.getCustomerByID(customerId);
        
        // Validate flight exists
        flightBookingSystem.getFlightByID(flightId);
        
        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new FlightBookingSystemException("Rating must be between 1 and 5 stars.");
        }
        
        // Generate new feedback ID
        int maxId = 0;
        if (flightBookingSystem.getAllFeedback().size() > 0) {
            for (Feedback f : flightBookingSystem.getAllFeedback()) {
                if (f.getId() > maxId) {
                    maxId = f.getId();
                }
            }
        }
        
        // Create feedback
        Feedback feedback = new Feedback(
            ++maxId,
            customerId,
            flightId,
            rating,
            comment,
            flightBookingSystem.getSystemDate()
        );
        
        flightBookingSystem.addFeedback(feedback);
        
        System.out.println("✅ Feedback submitted successfully!");
        System.out.println("Rating: " + feedback.getStarRating());
        System.out.println("Thank you for your feedback!");
    }
}