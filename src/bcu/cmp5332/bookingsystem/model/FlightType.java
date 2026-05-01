package bcu.cmp5332.bookingsystem.model;

/**
 * Enumeration representing the type of flight.
 * Used to distinguish between domestic and international flights.
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public enum FlightType {
    /**
     * Domestic flight - both origin and destination are in the same country
     */
    DOMESTIC("Domestic"),
    
    /**
     * International flight - origin and destination are in different countries
     */
    INTERNATIONAL("International");
    
    private final String displayName;
    
    /**
     * Constructs a FlightType with a display name.
     * 
     * @param displayName the human-readable name for this flight type
     */
    FlightType(String displayName) {
        this.displayName = displayName;
    }
    
    /**
     * Gets the display name of this flight type.
     * 
     * @return the display name
     */
    public String getDisplayName() {
        return displayName;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}