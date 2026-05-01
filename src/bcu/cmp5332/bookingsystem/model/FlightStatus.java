package bcu.cmp5332.bookingsystem.model;

/**
 * Enumeration of possible flight statuses.
 * Used for operational management and passenger notifications.
 * 
 * @author Tejindra Rai
 * @version 1.0 - Admin Enhancement Features
 */
public enum FlightStatus {
    SCHEDULED("Scheduled", "Flight is scheduled as planned"),
    ON_TIME("On Time", "Flight is on time for departure"),
    DELAYED("Delayed", "Flight has been delayed"),
    BOARDING("Boarding", "Passengers are boarding"),
    DEPARTED("Departed", "Flight has departed"),
    CANCELLED("Cancelled", "Flight has been cancelled"),
    DIVERTED("Diverted", "Flight has been diverted");
    
    private final String displayName;
    private final String description;
    
    FlightStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    /**
     * Gets the human-readable display name.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * Gets the description of this status.
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Converts string to FlightStatus enum.
     * 
     * @param status the status string
     * @return the corresponding FlightStatus enum
     */
    public static FlightStatus fromString(String status) {
        if (status == null) {
            return SCHEDULED;
        }
        
        for (FlightStatus fs : FlightStatus.values()) {
            if (fs.displayName.equalsIgnoreCase(status) || 
                fs.name().equalsIgnoreCase(status)) {
                return fs;
            }
        }
        return SCHEDULED;
    }
    
    /**
     * Checks if this status means the flight is still operational.
     */
    public boolean isOperational() {
        return this != CANCELLED && this != DEPARTED;
    }
    
    /**
     * Checks if this status requires passenger notification.
     */
    public boolean requiresNotification() {
        return this == DELAYED || this == CANCELLED || this == DIVERTED;
    }
}