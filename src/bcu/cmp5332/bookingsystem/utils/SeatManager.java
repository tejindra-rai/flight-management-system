package bcu.cmp5332.bookingsystem.utils;

import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class for managing flight seats.
 * Generates seat numbers and tracks availability.
 * 
 * @author Binay Chaudhary
 * @version 2.0 - Works without Flight.getBookings() method
 */
public class SeatManager {
    
    // Seat configuration based on flight class
    private static final String[] SEAT_LETTERS = {"A", "B", "C", "D", "E", "F"};
    
    /**
     * Generates all available seat numbers for a flight based on capacity.
     * 
     * @param flight the flight to generate seats for
     * @return list of all possible seat numbers
     */
    public static List<String> generateAllSeats(Flight flight) {
        List<String> allSeats = new ArrayList<>();
        int capacity = flight.getCapacity();
        int seatsPerRow = SEAT_LETTERS.length;
        int totalRows = (int) Math.ceil((double) capacity / seatsPerRow);
        
        for (int row = 1; row <= totalRows; row++) {
            for (String letter : SEAT_LETTERS) {
                allSeats.add(row + letter);
                if (allSeats.size() >= capacity) {
                    return allSeats;
                }
            }
        }
        
        return allSeats;
    }
    
    /**
     * Gets available seats for a flight (seats not yet booked).
     * 
     * @param flight the flight to check
     * @param fbs the flight booking system
     * @return sorted list of available seat numbers
     */
    public static List<String> getAvailableSeats(Flight flight, FlightBookingSystem fbs) {
        List<String> allSeats = generateAllSeats(flight);
        Set<String> bookedSeats = getBookedSeats(flight, fbs);
        
        List<String> availableSeats = new ArrayList<>();
        for (String seat : allSeats) {
            if (!bookedSeats.contains(seat)) {
                availableSeats.add(seat);
            }
        }
        
        return availableSeats;
    }
    
    /**
     * Gets all booked seats for a flight by checking all customers.
     * 
     * @param flight the flight to check
     * @param fbs the flight booking system
     * @return set of booked seat numbers
     */
    public static Set<String> getBookedSeats(Flight flight, FlightBookingSystem fbs) {
        Set<String> bookedSeats = new TreeSet<>();
        
        // Check all customers and their bookings
        for (Customer customer : fbs.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                // Check if this booking is for our flight
                if (booking.getFlight().getId() == flight.getId() && 
                    !booking.isCancelled() && 
                    booking.getSeatNumber() != null && 
                    !booking.getSeatNumber().isEmpty()) {
                    bookedSeats.add(booking.getSeatNumber());
                }
            }
        }
        
        return bookedSeats;
    }
    
    /**
     * Checks if a specific seat is available.
     * 
     * @param flight the flight to check
     * @param seatNumber the seat number to check
     * @param fbs the flight booking system
     * @return true if seat is available, false otherwise
     */
    public static boolean isSeatAvailable(Flight flight, String seatNumber, FlightBookingSystem fbs) {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return false;
        }
        
        Set<String> bookedSeats = getBookedSeats(flight, fbs);
        List<String> allSeats = generateAllSeats(flight);
        
        return allSeats.contains(seatNumber) && !bookedSeats.contains(seatNumber);
    }
    
    /**
     * Gets seat category (Economy, Business, First Class) based on seat number.
     * 
     * @param seatNumber the seat number (e.g., "12A")
     * @return seat category string
     */
    public static String getSeatCategory(String seatNumber) {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return "Unknown";
        }
        
        try {
            int rowNumber = Integer.parseInt(seatNumber.replaceAll("[^0-9]", ""));
            
            if (rowNumber <= 10) {
                return "Economy";
            } else if (rowNumber <= 15) {
                return "Business";
            } else {
                return "First Class";
            }
        } catch (NumberFormatException e) {
            return "Unknown";
        }
    }
    
    /**
     * Formats seat display with category information.
     * 
     * @param seatNumber the seat number
     * @return formatted string (e.g., "12A (Business)")
     */
    public static String formatSeatDisplay(String seatNumber) {
        if (seatNumber == null || seatNumber.isEmpty()) {
            return "Not assigned";
        }
        return seatNumber + " (" + getSeatCategory(seatNumber) + ")";
    }
}