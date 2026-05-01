package bcu.cmp5332.bookingsystem.model;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import java.time.LocalDate;
import java.util.*;

/**
 * Main model class for the Flight Booking System.
 * Manages flights, customers, bookings, users, and feedback.
 *
 * @author Tejindra Rai
 * @version 3.0
 */
public class FlightBookingSystem {

    private final LocalDate systemDate = LocalDate.parse("2020-11-11");

    private final Map<Integer, Customer> customers = new TreeMap<>();
    private final Map<Integer, Flight> flights = new TreeMap<>();
    private final Map<Integer, User> users = new TreeMap<>();
    private final Map<Integer, Feedback> feedbacks = new TreeMap<>(); // ADDED: Feedback storage
    private User currentUser; // Track currently logged-in user

    // ────────────────────────────────────────────────
    // THESE THREE SETTERS WERE MISSING – NOW ADDED
    // They are required so that FlightBookingSystemData can load data from DAOs

    /**
     * Sets/replaces all flights from loaded data (used by DAO loading).
     */
    public void setFlights(List<Flight> flightList) {
        this.flights.clear();
        if (flightList != null) {
            for (Flight flight : flightList) {
                this.flights.put(flight.getId(), flight);
            }
        }
    }

    /**
     * Sets/replaces all customers from loaded data (used by DAO loading).
     */
    public void setCustomers(List<Customer> customerList) {
        this.customers.clear();
        if (customerList != null) {
            for (Customer customer : customerList) {
                this.customers.put(customer.getId(), customer);
            }
        }
    }

    /**
     * Sets/replaces all users from loaded data (used by DAO loading).
     */
    public void setUsers(List<User> userList) {
        this.users.clear();
        if (userList != null) {
            for (User user : userList) {
                this.users.put(user.getId(), user);
            }
        }
    }

    // ────────────────────────────────────────────────
    // Rest of your existing code remains unchanged below
    // (getters, add methods, authenticate, etc.)

    /**
     * Gets the system date.
     *
     * @return the system date
     */
    public LocalDate getSystemDate() {
        return systemDate;
    }

    // ==================== FLIGHT METHODS ====================

    /**
     * Gets all flights in the system.
     *
     * @return unmodifiable list of all flights
     */
    public List<Flight> getFlights() {
        List<Flight> out = new ArrayList<>(flights.values());
        return Collections.unmodifiableList(out);
    }

    /**
     * Gets only active (non-deleted) flights.
     *
     * @return list of active flights
     */
    public List<Flight> getActiveFlights() {
        List<Flight> activeFlights = new ArrayList<>();
        for (Flight flight : flights.values()) {
            if (!flight.isDeleted()) {
                activeFlights.add(flight);
            }
        }
        return activeFlights;
    }

    /**
     * Gets only future flights that haven't departed yet.
     *
     * @return list of future flights
     */
    public List<Flight> getFutureFlights() {
        List<Flight> futureFlights = new ArrayList<>();
        for (Flight flight : flights.values()) {
            if (!flight.isDeleted() && !flight.getDepartureDate().isBefore(systemDate)) {
                futureFlights.add(flight);
            }
        }
        return futureFlights;
    }

    /**
     * Gets a flight by its ID.
     *
     * @param id the flight ID
     * @return the flight
     * @throws FlightBookingSystemException if flight not found
     */
    public Flight getFlightByID(int id) throws FlightBookingSystemException {
        if (!flights.containsKey(id)) {
            throw new FlightBookingSystemException("There is no flight with that ID.");
        }
        return flights.get(id);
    }

    /**
     * Adds a flight to the system.
     *
     * @param flight the flight to add
     * @throws FlightBookingSystemException if flight ID is duplicate or flight number already exists
     */
    public void addFlight(Flight flight) throws FlightBookingSystemException {
        if (flights.containsKey(flight.getId())) {
            throw new IllegalArgumentException("Duplicate flight ID.");
        }
        for (Flight existing : flights.values()) {
            if (existing.getFlightNumber().equals(flight.getFlightNumber())
                && existing.getDepartureDate().isEqual(flight.getDepartureDate())) {
                throw new FlightBookingSystemException("There is a flight with same "
                        + "number and departure date in the system");
            }
        }
        flights.put(flight.getId(), flight);
    }

    // ==================== CUSTOMER METHODS ====================

    /**
     * Gets a customer by ID.
     *
     * @param id the customer ID
     * @return the customer
     * @throws FlightBookingSystemException if customer not found
     */
    public Customer getCustomerByID(int id) throws FlightBookingSystemException {
        if (!customers.containsKey(id)) {
            throw new FlightBookingSystemException("There is no customer with that ID.");
        }
        return customers.get(id);
    }

    /**
     * Gets all customers in the system.
     *
     * @return unmodifiable list of all customers
     */
    public List<Customer> getCustomers() {
        List<Customer> out = new ArrayList<>(customers.values());
        return Collections.unmodifiableList(out);
    }

    /**
     * Gets only active (non-deleted) customers.
     *
     * @return list of active customers
     */
    public List<Customer> getActiveCustomers() {
        List<Customer> activeCustomers = new ArrayList<>();
        for (Customer customer : customers.values()) {
            if (!customer.isDeleted()) {
                activeCustomers.add(customer);
            }
        }
        return activeCustomers;
    }

    /**
     * Adds a customer to the system.
     *
     * @param customer the customer to add
     * @throws FlightBookingSystemException if customer ID is duplicate
     */
    public void addCustomer(Customer customer) throws FlightBookingSystemException {
        if (customers.containsKey(customer.getId())) {
            throw new FlightBookingSystemException("Duplicate customer ID.");
        }
        customers.put(customer.getId(), customer);
    }

    // ==================== USER METHODS ====================

    /**
     * Gets all users in the system.
     *
     * @return unmodifiable list of users
     */
    public List<User> getUsers() {
        List<User> out = new ArrayList<>(users.values());
        return Collections.unmodifiableList(out);
    }

    /**
     * Gets a user by ID.
     *
     * @param id the user ID
     * @return the user
     * @throws FlightBookingSystemException if user not found
     */
    public User getUserByID(int id) throws FlightBookingSystemException {
        if (!users.containsKey(id)) {
            throw new FlightBookingSystemException("There is no user with that ID.");
        }
        return users.get(id);
    }

    /**
     * Gets a user by username.
     *
     * @param username the username
     * @return the user, or null if not found
     */
    public User getUserByUsername(String username) {
        for (User user : users.values()) {
            if (user.getUsername().equalsIgnoreCase(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Adds a user to the system.
     *
     * @param user the user to add
     * @throws FlightBookingSystemException if user ID already exists or username is taken
     */
    public void addUser(User user) throws FlightBookingSystemException {
        if (users.containsKey(user.getId())) {
            throw new FlightBookingSystemException("Duplicate user ID.");
        }

        // Check for duplicate username
        for (User existingUser : users.values()) {
            if (existingUser.getUsername().equalsIgnoreCase(user.getUsername())) {
                throw new FlightBookingSystemException("Username already exists.");
            }
        }

        users.put(user.getId(), user);
    }

    /**
     * Authenticates a user with username and password.
     *
     * @param username the username
     * @param password the password
     * @return the authenticated user, or null if authentication fails
     */
    public User authenticate(String username, String password) {
        User user = getUserByUsername(username);
        if (user != null && user.validatePassword(password)) {
            currentUser = user;
            return user;
        }
        return null;
    }

    /**
     * Gets the currently logged-in user.
     *
     * @return the current user, or null if no one is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user.
     *
     * @param user the user to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Logs out the current user.
     */
    public void logout() {
        currentUser = null;
    }

    // ==================== FEEDBACK METHODS ====================

    /**
     * Gets all feedback in the system.
     *
     * @return unmodifiable list of all feedback
     */
    public List<Feedback> getAllFeedback() {
        List<Feedback> out = new ArrayList<>(feedbacks.values());
        return Collections.unmodifiableList(out);
    }

    /**
     * Gets a feedback by its ID.
     *
     * @param id the feedback ID
     * @return the feedback
     * @throws FlightBookingSystemException if feedback not found
     */
    public Feedback getFeedbackByID(int id) throws FlightBookingSystemException {
        if (!feedbacks.containsKey(id)) {
            throw new FlightBookingSystemException("There is no feedback with that ID.");
        }
        return feedbacks.get(id);
    }

    /**
     * Adds feedback to the system.
     *
     * @param feedback the feedback to add
     * @throws FlightBookingSystemException if feedback ID is duplicate
     */
    public void addFeedback(Feedback feedback) throws FlightBookingSystemException {
        if (feedbacks.containsKey(feedback.getId())) {
            throw new FlightBookingSystemException("Duplicate feedback ID.");
        }
        feedbacks.put(feedback.getId(), feedback);
    }

    /**
     * Gets all feedback for a specific flight.
     *
     * @param flightId the flight ID
     * @return list of feedback for the flight (empty list if no feedback)
     */
    public List<Feedback> getFeedbackForFlight(int flightId) {
        List<Feedback> flightFeedback = new ArrayList<>();
        for (Feedback feedback : feedbacks.values()) {
            if (feedback.getFlightId() == flightId) {
                flightFeedback.add(feedback);
            }
        }
        return flightFeedback;
    }

    /**
     * Gets all feedback from a specific customer.
     *
     * @param customerId the customer ID
     * @return list of feedback from the customer (empty list if no feedback)
     */
    public List<Feedback> getFeedbackFromCustomer(int customerId) {
        List<Feedback> customerFeedback = new ArrayList<>();
        for (Feedback feedback : feedbacks.values()) {
            if (feedback.getCustomerId() == customerId) {
                customerFeedback.add(feedback);
            }
        }
        return customerFeedback;
    }

    /**
     * Gets the average rating for a specific flight.
     *
     * @param flightId the flight ID
     * @return average rating (0.0 if no feedback exists)
     */
    public double getAverageRatingForFlight(int flightId) {
        List<Feedback> flightFeedback = getFeedbackForFlight(flightId);
        if (flightFeedback.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (Feedback feedback : flightFeedback) {
            sum += feedback.getRating();
        }
        return sum / flightFeedback.size();
    }

    /**
     * Checks if a customer has already given feedback for a flight.
     * This can be used to prevent duplicate feedback.
     *
     * @param customerId the customer ID
     * @param flightId the flight ID
     * @return true if customer has already given feedback, false otherwise
     */
    public boolean hasCustomerReviewedFlight(int customerId, int flightId) {
        for (Feedback feedback : feedbacks.values()) {
            if (feedback.getCustomerId() == customerId && feedback.getFlightId() == flightId) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes feedback from the system.
     *
     * @param feedbackId the feedback ID to delete
     * @throws FlightBookingSystemException if feedback not found
     */
    public void deleteFeedback(int feedbackId) throws FlightBookingSystemException {
        if (!feedbacks.containsKey(feedbackId)) {
            throw new FlightBookingSystemException("There is no feedback with that ID.");
        }
        feedbacks.remove(feedbackId);
    }
}