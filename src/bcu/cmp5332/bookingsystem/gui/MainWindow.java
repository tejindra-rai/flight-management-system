package bcu.cmp5332.bookingsystem.gui;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.admin.AddCustomerWindow;
import bcu.cmp5332.bookingsystem.gui.admin.AddFlightWindow;
import bcu.cmp5332.bookingsystem.gui.admin.DeleteCustomerWindow;
import bcu.cmp5332.bookingsystem.gui.admin.DeleteFlightWindow;
import bcu.cmp5332.bookingsystem.gui.admin.IssueBookingWindow;
import bcu.cmp5332.bookingsystem.gui.admin.ViewFeedbackWindow;
import bcu.cmp5332.bookingsystem.gui.common.LoginWindow;
import bcu.cmp5332.bookingsystem.gui.common.SplashScreen;
import bcu.cmp5332.bookingsystem.gui.customer.AddFeedbackWindow;
import bcu.cmp5332.bookingsystem.gui.customer.CancelBookingWindow;
import bcu.cmp5332.bookingsystem.gui.customer.CustomerBookingWindow;
import bcu.cmp5332.bookingsystem.gui.customer.CustomerDetailsWindow;
import bcu.cmp5332.bookingsystem.gui.customer.FlightDetailsWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import com.formdev.flatlaf.FlatLightLaf;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.io.IOException;
import java.util.List;
import javax.swing.*;

/**
 * Main window with role-based access control.
 * Admins get full access, Customers get limited access.
 * 
 * @author Tejindra Rai
 * @version 3.1 - FIXED: Update booking for admin
 */
public class MainWindow extends JFrame implements ActionListener {

    private JMenuBar menuBar;
    private JMenu adminMenu;
    private JMenu flightsMenu;
    private JMenu bookingsMenu;
    private JMenu customersMenu;
    private JMenu accountMenu;
    private JMenu feedbackMenu;

    private JMenuItem adminExit;
    private JMenuItem adminLogout;

    private JMenuItem flightsView;
    private JMenuItem flightsAdd;
    private JMenuItem flightsDel;
    
    private JMenuItem bookingsIssue;
    private JMenuItem bookingsUpdate;
    private JMenuItem bookingsCancel;

    private JMenuItem custView;
    private JMenuItem custAdd;
    private JMenuItem custDel;
    
    private JMenuItem accountProfile;
    private JMenuItem accountMyBookings;

    private FlightBookingSystem fbs;
    private User currentUser;

    /**
     * Constructs a new MainWindow.
     * 
     * @param fbs the flight booking system
     */
    public MainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        this.currentUser = fbs.getCurrentUser();
        initialize();
    }
    
    /**
     * Gets the flight booking system.
     * 
     * @return the flight booking system
     */
    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    /**
     * Initialize the contents of the frame with role-based menus.
     */
    private void initialize() {

        // Apply FlatLaf theme
        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Failed to setup FlatLaf");
        }

        // Set title with user info
        String roleDisplay = currentUser.isAdmin() ? "Admin" : "Customer";
        setTitle("✈️ Flight Booking System - " + roleDisplay + ": " + currentUser.getUsername());

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        // ==================== ADMIN MENU ====================
        adminMenu = new JMenu("⚙️ System");
        menuBar.add(adminMenu);

        adminLogout = new JMenuItem("🚪 Logout");
        adminExit = new JMenuItem("❌ Exit");
        adminMenu.add(adminLogout);
        adminMenu.addSeparator();
        adminMenu.add(adminExit);
        
        adminLogout.addActionListener(this);
        adminExit.addActionListener(this);

        // ==================== FLIGHTS MENU ====================
        flightsMenu = new JMenu("✈️ Flights");
        menuBar.add(flightsMenu);

        flightsView = new JMenuItem("📋 View All Flights");
        flightsMenu.add(flightsView);
        flightsView.addActionListener(this);
        
        // Admin-only options for flights
        if (currentUser.isAdmin()) {
            flightsAdd = new JMenuItem("➕ Add Flight");
            flightsDel = new JMenuItem("🗑️ Delete Flight");
            flightsMenu.add(flightsAdd);
            flightsMenu.add(flightsDel);
            flightsAdd.addActionListener(this);
            flightsDel.addActionListener(this);
        }
        
        // ==================== BOOKINGS MENU ====================
        bookingsMenu = new JMenu("📅 Bookings");
        menuBar.add(bookingsMenu);
        
        if (currentUser.isAdmin()) {
            // Admin can book for any customer
            bookingsIssue = new JMenuItem("🎫 Issue Booking");
            bookingsUpdate = new JMenuItem("✏️ Update Booking");
            bookingsCancel = new JMenuItem("❌ Cancel Booking");
            bookingsMenu.add(bookingsIssue);
            bookingsMenu.add(bookingsUpdate);
            bookingsMenu.add(bookingsCancel);
            bookingsIssue.addActionListener(this);
            bookingsUpdate.addActionListener(this);
            bookingsCancel.addActionListener(this);
        } else {
            // Customer can only manage their own bookings
            bookingsIssue = new JMenuItem("🎫 Book a Flight");
            bookingsCancel = new JMenuItem("❌ Cancel My Booking");
            bookingsMenu.add(bookingsIssue);
            bookingsMenu.add(bookingsCancel);
            bookingsIssue.addActionListener(this);
            bookingsCancel.addActionListener(this);
        }

        // ==================== CUSTOMERS MENU (Admin Only) ====================
        if (currentUser.isAdmin()) {
            customersMenu = new JMenu("👥 Customers");
            menuBar.add(customersMenu);

            custView = new JMenuItem("📋 View All Customers");
            custAdd = new JMenuItem("➕ Add Customer");
            custDel = new JMenuItem("🗑️ Delete Customer");

            customersMenu.add(custView);
            customersMenu.add(custAdd);
            customersMenu.add(custDel);
            
            custView.addActionListener(this);
            custAdd.addActionListener(this);
            custDel.addActionListener(this);
        }
        
        // ==================== MY ACCOUNT MENU (Customer Only) ====================
        if (currentUser.isCustomer()) {
            accountMenu = new JMenu("👤 My Account");
            menuBar.add(accountMenu);
            
            accountProfile = new JMenuItem("📝 My Profile");
            accountMyBookings = new JMenuItem("📅 My Bookings");
            
            accountMenu.add(accountProfile);
            accountMenu.add(accountMyBookings);
            
            accountProfile.addActionListener(this);
            accountMyBookings.addActionListener(this);
        }
        
        // ==================== FEEDBACK MENU ====================
        feedbackMenu = new JMenu("⭐ Feedback");
        menuBar.add(feedbackMenu);

        if (currentUser.isCustomer()) {
            JMenuItem submitFeedback = new JMenuItem("📝 Submit Feedback");
            feedbackMenu.add(submitFeedback);
            submitFeedback.addActionListener(e -> new AddFeedbackWindow(this));
        }

        JMenuItem viewAllFeedback = new JMenuItem("👁️ View All Reviews");
        feedbackMenu.add(viewAllFeedback);
        viewAllFeedback.addActionListener(e -> {
            // Show dialog to select flight
            List<Flight> flights = fbs.getFlights();
            if (flights.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No flights available.", "Info", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String[] flightOptions = new String[flights.size()];
            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);
                flightOptions[i] = f.getFlightNumber() + " - " + f.getOrigin() + " → " + f.getDestination();
            }
            
            String selected = (String) JOptionPane.showInputDialog(
                this,
                "Select a flight to view reviews:",
                "View Flight Reviews",
                JOptionPane.QUESTION_MESSAGE,
                null,
                flightOptions,
                flightOptions[0]
            );
            
            if (selected != null) {
                int index = java.util.Arrays.asList(flightOptions).indexOf(selected);
                Flight selectedFlight = flights.get(index);
                new ViewFeedbackWindow(selectedFlight, fbs);
            }
        });
        
        setSize(900, 600);
        setVisible(true);
        setAutoRequestFocus(true);
        toFront();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Show welcome dashboard
        displayWelcomeDashboard();
    }	

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == adminExit) {
            System.exit(0);
            
        } else if (ae.getSource() == adminLogout) {
            handleLogout();
            
        } else if (ae.getSource() == flightsView) {
            displayFlights();
            
        } else if (ae.getSource() == flightsAdd && currentUser.isAdmin()) {
            new AddFlightWindow(this);
            
        } else if (ae.getSource() == flightsDel && currentUser.isAdmin()) {
            new DeleteFlightWindow(this);
            
        } else if (ae.getSource() == bookingsIssue) {
            if (currentUser.isCustomer()) {
                // Customer booking - simplified
                new CustomerBookingWindow(this);
            } else {
                // Admin booking - full featured
                new IssueBookingWindow(this);
            }
            
        } else if (ae.getSource() == bookingsUpdate && currentUser.isAdmin()) {
            // FIXED: Admin update booking - show informational dialog
            handleAdminUpdateBooking();
            
        } else if (ae.getSource() == bookingsCancel) {
            new CancelBookingWindow(this);
            
        } else if (ae.getSource() == custView && currentUser.isAdmin()) {
            displayCustomers();
            
        } else if (ae.getSource() == custAdd && currentUser.isAdmin()) {
            new AddCustomerWindow(this);
            
        } else if (ae.getSource() == custDel && currentUser.isAdmin()) {
            new DeleteCustomerWindow(this);
            
        } else if (ae.getSource() == accountProfile && currentUser.isCustomer()) {
            showCustomerProfile();
            
        } else if (ae.getSource() == accountMyBookings && currentUser.isCustomer()) {
            showMyBookings();
        }
    }

    /**
     * FIXED: Handles admin update booking request.
     * Shows informational dialog since CustomerUpdateBookingWindow is designed for customer portal.
     */
    private void handleAdminUpdateBooking() {
        JOptionPane.showMessageDialog(this,
            "Update Booking is only available in the Customer Portal.\n\n" +
            "As an admin, to modify a booking:\n" +
            "1. Cancel the existing booking (Bookings → Cancel Booking)\n" +
            "2. Create a new booking (Bookings → Issue Booking)\n\n" +
            "Note: This preserves the audit trail of changes.",
            "Update Booking - Admin Info",
            JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Handles user logout.
     */
    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            fbs.logout();
            this.dispose();
            new LoginWindow(fbs);
        }
    }
    
    /**
     * Shows customer profile information.
     */
    private void showCustomerProfile() {
        try {
            if (currentUser.getLinkedCustomerId() > 0) {
                Customer customer = fbs.getCustomerByID(currentUser.getLinkedCustomerId());
                new CustomerDetailsWindow(customer);
            } else {
                JOptionPane.showMessageDialog(this,
                    "No customer profile linked to this account.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Shows customer's bookings.
     */
    private void showMyBookings() {
        try {
            if (currentUser.getLinkedCustomerId() > 0) {
                Customer customer = fbs.getCustomerByID(currentUser.getLinkedCustomerId());
                
                // Create a window showing only this customer's bookings
                JFrame bookingsFrame = new JFrame("📅 My Bookings");
                bookingsFrame.setSize(700, 400);
                bookingsFrame.setLayout(new BorderLayout());
                
                String[] columns = {"Flight No", "Origin", "Destination", "Date", "Price", "Status"};
                Object[][] data = new Object[customer.getBookings().size()][6];
                
                int i = 0;
                for (bcu.cmp5332.bookingsystem.model.Booking booking : customer.getBookings()) {
                    Flight flight = booking.getFlight();
                    data[i][0] = flight.getFlightNumber();
                    data[i][1] = flight.getOrigin();
                    data[i][2] = flight.getDestination();
                    data[i][3] = flight.getDepartureDate();
                    data[i][4] = "£" + String.format("%.2f", booking.getBookingPrice());
                    data[i][5] = booking.isCancelled() ? "❌ Cancelled" : "✅ Active";
                    i++;
                }
                
                JTable table = new JTable(data, columns);
                table.setRowHeight(25);
                table.setFont(new Font("Arial", Font.PLAIN, 12));
                
                bookingsFrame.add(new JScrollPane(table), BorderLayout.CENTER);
                bookingsFrame.setLocationRelativeTo(this);
                bookingsFrame.setVisible(true);
            }
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays the welcome dashboard with system statistics.
     */
    public void displayWelcomeDashboard() {
        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));
        
        // Title with user greeting
        String greeting = currentUser.isAdmin() ? "Admin Dashboard" : "Welcome";
        JLabel titleLabel = new JLabel("✈️ " + greeting + ", " + currentUser.getUsername() + "!");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomePanel.add(titleLabel);
        
        welcomePanel.add(Box.createVerticalStrut(30));
        
        // Subtitle
        String subtitle = currentUser.isAdmin() ? 
            "Manage flights, customers, and bookings efficiently" :
            "Book flights and manage your travel plans";
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(100, 100, 100));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomePanel.add(subtitleLabel);
        
        welcomePanel.add(Box.createVerticalStrut(50));
        
        // Statistics Panel
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new GridLayout(1, 3, 30, 0));
        statsPanel.setMaximumSize(new Dimension(800, 150));
        
        // Total Flights Card
        JPanel flightsCard = createStatCard("✈️", "Total Flights", 
            String.valueOf(fbs.getFlights().size()), new Color(70, 130, 180));
        statsPanel.add(flightsCard);
        
        if (currentUser.isAdmin()) {
            // Admin sees customer stats
            JPanel customersCard = createStatCard("👥", "Total Customers", 
                String.valueOf(fbs.getActiveCustomers().size()), new Color(60, 179, 113));
            statsPanel.add(customersCard);
            
            // Active Bookings Card
            int totalBookings = 0;
            for (Customer customer : fbs.getCustomers()) {
                for (bcu.cmp5332.bookingsystem.model.Booking booking : customer.getBookings()) {
                    if (!booking.isCancelled()) {
                        totalBookings++;
                    }
                }
            }
            JPanel bookingsCard = createStatCard("📅", "Active Bookings", 
                String.valueOf(totalBookings), new Color(255, 140, 0));
            statsPanel.add(bookingsCard);
        } else {
            // Customer sees their own stats
            try {
                if (currentUser.getLinkedCustomerId() > 0) {
                    Customer customer = fbs.getCustomerByID(currentUser.getLinkedCustomerId());
                    
                    int myBookings = customer.getBookings().size();
                    int activeBookings = 0;
                    for (bcu.cmp5332.bookingsystem.model.Booking b : customer.getBookings()) {
                        if (!b.isCancelled()) activeBookings++;
                    }
                    
                    JPanel myBookingsCard = createStatCard("📅", "My Bookings", 
                        String.valueOf(myBookings), new Color(60, 179, 113));
                    statsPanel.add(myBookingsCard);
                    
                    JPanel activeCard = createStatCard("✅", "Active", 
                        String.valueOf(activeBookings), new Color(255, 140, 0));
                    statsPanel.add(activeCard);
                }
            } catch (Exception ex) {
                // If customer not linked, show placeholder
                statsPanel.add(createStatCard("📅", "My Bookings", "0", new Color(60, 179, 113)));
                statsPanel.add(createStatCard("✅", "Active", "0", new Color(255, 140, 0)));
            }
        }
        
        welcomePanel.add(statsPanel);
        
        welcomePanel.add(Box.createVerticalStrut(50));
        
        // Quick Actions Label
        JLabel actionsLabel = new JLabel("Quick Actions:");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        actionsLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomePanel.add(actionsLabel);
        
        welcomePanel.add(Box.createVerticalStrut(20));
        
        // Quick Action Buttons
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 15, 0));
        
        JButton viewFlightsBtn = new JButton("✈️ View Flights");
        Font buttonFont = new Font("Arial", Font.BOLD, 14);
        Dimension buttonSize = new Dimension(150, 40);
        viewFlightsBtn.setFont(buttonFont);
        viewFlightsBtn.setPreferredSize(buttonSize);
        viewFlightsBtn.addActionListener(e -> displayFlights());
        buttonsPanel.add(viewFlightsBtn);
        
        if (currentUser.isAdmin()) {
            JButton addFlightBtn = new JButton("➕ Add Flight");
            addFlightBtn.setFont(buttonFont);
            addFlightBtn.setPreferredSize(buttonSize);
            addFlightBtn.addActionListener(e -> new AddFlightWindow(this));
            buttonsPanel.add(addFlightBtn);
            
            JButton viewCustomersBtn = new JButton("👥 Customers");
            viewCustomersBtn.setFont(buttonFont);
            viewCustomersBtn.setPreferredSize(buttonSize);
            viewCustomersBtn.addActionListener(e -> displayCustomers());
            buttonsPanel.add(viewCustomersBtn);
        } else {
            JButton bookFlightBtn = new JButton("🎫 Book Flight");
            bookFlightBtn.setFont(buttonFont);
            bookFlightBtn.setPreferredSize(buttonSize);
            bookFlightBtn.addActionListener(e -> new CustomerBookingWindow(this));
            buttonsPanel.add(bookFlightBtn);
            
            JButton myBookingsBtn = new JButton("📅 My Bookings");
            myBookingsBtn.setFont(buttonFont);
            myBookingsBtn.setPreferredSize(buttonSize);
            myBookingsBtn.addActionListener(e -> showMyBookings());
            buttonsPanel.add(myBookingsBtn);
        }
        
        JButton newBookingBtn = new JButton("🎫 New Booking");
        newBookingBtn.setFont(buttonFont);
        newBookingBtn.setPreferredSize(buttonSize);
        newBookingBtn.addActionListener(e -> {
            if (currentUser.isCustomer()) {
                new CustomerBookingWindow(this);
            } else {
                new IssueBookingWindow(this);
            }
        });
        buttonsPanel.add(newBookingBtn);
        
        welcomePanel.add(buttonsPanel);
        
        // Footer
        welcomePanel.add(Box.createVerticalStrut(50));
        JLabel footerLabel = new JLabel("System Date: " + fbs.getSystemDate() + 
            " | Logged in as: " + currentUser.getRole());
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(new Color(150, 150, 150));
        footerLabel.setAlignmentX(CENTER_ALIGNMENT);
        welcomePanel.add(footerLabel);
        
        this.getContentPane().removeAll();
        this.getContentPane().add(welcomePanel);
        this.revalidate();
        this.repaint();
    }

    /**
     * Creates a statistics card for the dashboard.
     * 
     * @param icon the emoji icon
     * @param label the label text
     * @param value the value to display
     * @param color the card color
     * @return the created card panel
     */
    private JPanel createStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Arial", Font.PLAIN, 48));
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(iconLabel);
        
        card.add(Box.createVerticalStrut(10));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(valueLabel);
        
        card.add(Box.createVerticalStrut(5));
        
        JLabel labelLabel = new JLabel(label);
        labelLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        labelLabel.setForeground(new Color(100, 100, 100));
        labelLabel.setAlignmentX(CENTER_ALIGNMENT);
        card.add(labelLabel);
        
        return card;
    }

    /**
     * Displays all flights in a table.
     */
    public void displayFlights() {
        List<Flight> flightsList = fbs.getFlights();
        String[] columns = new String[]{"ID", "Flight No", "Origin", "Destination", "Date", "Price", "Available Seats", "Class"};

        Object[][] data = new Object[flightsList.size()][8];
        for (int i = 0; i < flightsList.size(); i++) {
            Flight flight = flightsList.get(i);
            double price = flight.calculatePrice(fbs.getSystemDate());
            
            data[i][0] = flight.getId();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate();
            data[i][5] = "£" + String.format("%.2f", price);
            data[i][6] = flight.getAvailableSeats() + "/" + flight.getCapacity();
            data[i][7] = flight.getFlightClass();
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int flightId = (int) table.getValueAt(row, 0);
                        try {
                            Flight flight = fbs.getFlightByID(flightId);
                            new FlightDetailsWindow(flight);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        
        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
        this.repaint();
    }
    
    /**
     * Displays all customers in a table (Admin only).
     */
    public void displayCustomers() {
        List<Customer> customersList = fbs.getActiveCustomers();
        String[] columns = new String[]{"ID", "Name", "Phone", "Email", "Age Group", "Bookings", "Has Children"};

        Object[][] data = new Object[customersList.size()][7];
        for (int i = 0; i < customersList.size(); i++) {
            Customer customer = customersList.get(i);
            
            data[i][0] = customer.getId();
            data[i][1] = customer.getName();
            data[i][2] = customer.getPhone();
            data[i][3] = customer.getEmail();
            data[i][4] = customer.getAgeGroup();
            data[i][5] = customer.getBookings().size();
            data[i][6] = customer.hasChildren() ? "Yes" : "No";
        }

        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row != -1) {
                        int customerId = (int) table.getValueAt(row, 0);
                        try {
                            Customer customer = fbs.getCustomerByID(customerId);
                            new CustomerDetailsWindow(customer);
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        
        this.getContentPane().removeAll();
        this.getContentPane().add(new JScrollPane(table));
        this.revalidate();
        this.repaint();
    }
}