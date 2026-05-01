package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.dao.mysql.BookingMySQLDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

/**
 * Command to update an existing booking to a new flight and date.
 * This command handles both in-memory model updates and database persistence.
 * 
 * @author Tejindra Rai
 * @version 2.0 - MySQL DAO Integration
 */
public class UpdateBooking implements Command {
    
    private final int customerId;
    private final int oldFlightId;
    private final int newFlightId;
    private final LocalDate newDate;
    
    // DAO for database operations
    private final BookingMySQLDAO bookingDAO;

    /**
     * Constructor for UpdateBooking command
     * 
     * @param customerId The ID of the customer who owns the booking
     * @param oldFlightId The ID of the current flight to be updated
     * @param newFlightId The ID of the new flight
     * @param newDate The new departure date (not used in current implementation but kept for future use)
     */
    public UpdateBooking(int customerId, int oldFlightId, int newFlightId, LocalDate newDate) {
        this.customerId = customerId;
        this.oldFlightId = oldFlightId;
        this.newFlightId = newFlightId;
        this.newDate = newDate;
        this.bookingDAO = new BookingMySQLDAO();
    }

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        try {
            // ============================================================
            // STEP 1: VALIDATE ALL ENTITIES
            // ============================================================
            
            // Get customer
            Customer customer = flightBookingSystem.getCustomerByID(customerId);
            if (customer == null) {
                throw new FlightBookingSystemException("Customer with ID " + customerId + " not found.");
            }

            // Get old flight
            Flight oldFlight = flightBookingSystem.getFlightByID(oldFlightId);
            if (oldFlight == null) {
                throw new FlightBookingSystemException("Original flight with ID " + oldFlightId + " not found.");
            }

            // Get new flight
            Flight newFlight = flightBookingSystem.getFlightByID(newFlightId);
            if (newFlight == null) {
                throw new FlightBookingSystemException("New flight with ID " + newFlightId + " not found.");
            }

            // Check if new flight is deleted
            if (newFlight.isDeleted()) {
                throw new FlightBookingSystemException("Cannot update to a deleted flight.");
            }

            // Validate new date is not in the past
            if (newDate.isBefore(flightBookingSystem.getSystemDate())) {
                throw new FlightBookingSystemException("Cannot update to a date in the past.");
            }

            // ============================================================
            // STEP 2: FIND THE EXISTING BOOKING
            // ============================================================
            
            Booking existingBooking = null;
            for (Booking booking : customer.getBookings()) {
                if (booking.getFlight().getId() == oldFlightId && !booking.isCancelled()) {
                    existingBooking = booking;
                    break;
                }
            }

            if (existingBooking == null) {
                throw new FlightBookingSystemException(
                    "No active booking found for customer " + customer.getName() + 
                    " on flight " + oldFlight.getFlightNumber()
                );
            }

            // ============================================================
            // STEP 3: VALIDATE NEW FLIGHT AVAILABILITY
            // ============================================================
            
            // Check if customer already has a booking on the new flight
            for (Booking booking : customer.getBookings()) {
                if (booking.getFlight().getId() == newFlightId && !booking.isCancelled()) {
                    throw new FlightBookingSystemException(
                        "Customer already has an active booking on flight " + newFlight.getFlightNumber()
                    );
                }
            }

            // Check if new flight has available capacity
            int currentPassengers = 0;
            for (Customer c : newFlight.getPassengers()) {
                currentPassengers++;
            }
            
            if (currentPassengers >= newFlight.getCapacity()) {
                throw new FlightBookingSystemException(
                    "Flight " + newFlight.getFlightNumber() + " is fully booked (capacity: " + 
                    newFlight.getCapacity() + ")."
                );
            }

            // ============================================================
            // STEP 4: CANCEL OLD BOOKING IN DATABASE
            // ============================================================
            
            boolean cancelSuccess = bookingDAO.cancelBooking(customerId, oldFlightId);
            if (!cancelSuccess) {
                throw new FlightBookingSystemException("Failed to cancel old booking in database.");
            }

            // ============================================================
            // STEP 5: UPDATE IN-MEMORY MODEL - CANCEL OLD BOOKING
            // ============================================================
            
            existingBooking.setCancelled(true);
            
            // Remove customer from old flight's passenger list
            oldFlight.removePassenger(customer);

            // ============================================================
            // STEP 6: CREATE NEW BOOKING
            // ============================================================
            
            LocalDate bookingDate = flightBookingSystem.getSystemDate();
            Booking newBooking = new Booking(customer, newFlight, bookingDate);
            
            // Calculate dynamic price based on current date
            double dynamicPrice = newFlight.calculatePrice(bookingDate);
            newBooking.setBookingPrice(dynamicPrice);
            
            // Preserve meal preference and seat number if available
            if (existingBooking.getMealPreference() != null && !existingBooking.getMealPreference().equals("None")) {
                newBooking.setMealPreference(existingBooking.getMealPreference());
            }
            // Note: Seat number will be null for new booking - should be reassigned

            // ============================================================
            // STEP 7: ADD NEW BOOKING TO DATABASE
            // ============================================================
            
            boolean addSuccess = bookingDAO.addBooking(newBooking);
            if (!addSuccess) {
                // Rollback in-memory changes if database fails
                existingBooking.setCancelled(false);
                oldFlight.addPassenger(customer);
                throw new FlightBookingSystemException("Failed to add new booking to database. Changes rolled back.");
            }

            // ============================================================
            // STEP 8: UPDATE IN-MEMORY MODEL - ADD NEW BOOKING
            // ============================================================
            
            // Add new booking to customer
            customer.addBooking(newBooking);

            // Add customer to new flight's passenger list
            newFlight.addPassenger(customer);

            // ============================================================
            // STEP 9: SUCCESS MESSAGE
            // ============================================================
            
            System.out.println("╔════════════════════════════════════════════════════╗");
            System.out.println("║       ✅ BOOKING UPDATED SUCCESSFULLY!             ║");
            System.out.println("╚════════════════════════════════════════════════════╝");
            System.out.println("📋 Customer: " + customer.getName());
            System.out.println();
            System.out.println("❌ OLD BOOKING (Cancelled):");
            System.out.println("   Flight: " + oldFlight.getFlightNumber() + 
                             " (" + oldFlight.getOrigin() + " → " + oldFlight.getDestination() + ")");
            System.out.println("   Date: " + oldFlight.getDepartureDate());
            System.out.println();
            System.out.println("✅ NEW BOOKING (Active):");
            System.out.println("   Flight: " + newFlight.getFlightNumber() + 
                             " (" + newFlight.getOrigin() + " → " + newFlight.getDestination() + ")");
            System.out.println("   Booking Date: " + bookingDate);
            System.out.println("   Price: £" + String.format("%.2f", dynamicPrice));
            System.out.println("   Meal Preference: " + newBooking.getMealPreference());
            System.out.println("════════════════════════════════════════════════════");
            
        } catch (SQLException e) {
            throw new FlightBookingSystemException("Database error while updating booking: " + e.getMessage());
        } catch (IOException e) {
            throw new FlightBookingSystemException("I/O error while updating booking: " + e.getMessage());
        }
    }
}