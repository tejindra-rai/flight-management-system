package bcu.cmp5332.bookingsystem.model;

import java.time.LocalDate;

public class Booking {

    private Customer customer;
    private Flight flight;
    private LocalDate bookingDate;
    private boolean cancelled;
    private double bookingPrice;
    private double cancellationFee;
    private String mealPreference;  // NEW FIELD
    private String seatNumber;      // NEW FIELD

    /**
     * Constructs a new Booking with the specified customer, flight, and booking date.
     *
     * @param customer the customer making the booking
     * @param flight the flight being booked
     * @param bookingDate the date when the booking was made
     */
    public Booking(Customer customer, Flight flight, LocalDate bookingDate) {
        this.customer = customer;
        this.flight = flight;
        this.bookingDate = bookingDate;
        this.cancelled = false;
        this.cancellationFee = 0.0;
        this.bookingPrice = 0.0;
        this.mealPreference = "None";  // Default value
        this.seatNumber = null;         // Will be assigned during booking
    }

    /**
     * Gets the customer associated with this booking.
     *
     * @return the customer who made the booking
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer for this booking.
     *
     * @param customer the customer to set
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the flight associated with this booking.
     *
     * @return the booked flight
     */
    public Flight getFlight() {
        return flight;
    }

    /**
     * Sets the flight for this booking.
     *
     * @param flight the flight to set
     */
    public void setFlight(Flight flight) {
        this.flight = flight;
    }

    /**
     * Gets the date when this booking was made.
     *
     * @return the booking date
     */
    public LocalDate getBookingDate() {
        return bookingDate;
    }

    /**
     * Sets the booking date.
     *
     * @param bookingDate the booking date to set
     */
    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    /**
     * Checks if this booking has been cancelled.
     *
     * @return true if the booking is cancelled, false otherwise
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Sets the cancellation status of this booking.
     *
     * @param cancelled true to mark as cancelled, false otherwise
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Gets the price of this booking.
     *
     * @return the booking price
     */
    public double getBookingPrice() {
        return bookingPrice;
    }

    /**
     * Sets the price for this booking.
     *
     * @param bookingPrice the price to set
     */
    public void setBookingPrice(double bookingPrice) {
        this.bookingPrice = bookingPrice;
    }

    /**
     * Gets the cancellation fee for this booking.
     *
     * @return the cancellation fee
     */
    public double getCancellationFee() {
        return cancellationFee;
    }

    /**
     * Sets the cancellation fee for this booking.
     *
     * @param cancellationFee the cancellation fee to set
     */
    public void setCancellationFee(double cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    /**
     * Gets the meal preference for this booking.
     *
     * @return the meal preference (e.g., "Vegetarian", "Non-Vegetarian", "Vegan")
     */
    public String getMealPreference() {
        return mealPreference;
    }

    /**
     * Sets the meal preference for this booking.
     *
     * @param mealPreference the meal preference to set
     */
    public void setMealPreference(String mealPreference) {
        this.mealPreference = mealPreference;
    }

    /**
     * Gets the seat number for this booking.
     *
     * @return the seat number (e.g., "12A", "5B")
     */
    public String getSeatNumber() {
        return seatNumber;
    }

    /**
     * Sets the seat number for this booking.
     *
     * @param seatNumber the seat number to set
     */
    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }
}