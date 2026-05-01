package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.SeatManager;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Admin window for issuing bookings with maroon theme.
 * Now includes meal preference and seat selection features.
 * 
 * @author Tejindra Rai
 * @version 5.0 - Added meal preference and seat selection
 */
public class IssueBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField customerIdText = new JTextField();
    private JTextField flightIdText = new JTextField();
    private JTextField promoCodeText = new JTextField();
    private JComboBox<String> seatClassCombo = new JComboBox<>(new String[]{"Economy", "Business", "First Class"});
    private JComboBox<String> mealPreferenceCombo = new JComboBox<>(new String[]{"None", "Vegetarian", "Non-Vegetarian", "Vegan", "Gluten-Free", "Halal", "Kosher"});
    private JComboBox<String> seatSelectionCombo = new JComboBox<>();

    private JButton bookBtn = new JButton("Book Now");
    private JButton cancelBtn = new JButton("Cancel");
    private JButton loadSeatsBtn = new JButton("Load Seats");

    public IssueBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Issue Booking");
        setSize(550, 650);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.TICKET);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Issue New Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Create flight reservation for customer");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(10, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(ColorScheme.CARD_BG);
        
        // Customer ID
        formPanel.add(createLabel("Customer ID:"));
        formPanel.add(customerIdText);
        
        // Flight ID
        formPanel.add(createLabel("Flight ID:"));
        JPanel flightPanel = new JPanel(new BorderLayout(5, 0));
        flightPanel.setBackground(ColorScheme.CARD_BG);
        flightPanel.add(flightIdText, BorderLayout.CENTER);
        loadSeatsBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        loadSeatsBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        loadSeatsBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        loadSeatsBtn.setFocusPainted(false);
        loadSeatsBtn.addActionListener(e -> loadAvailableSeats());
        flightPanel.add(loadSeatsBtn, BorderLayout.EAST);
        formPanel.add(flightPanel);
        
        // Seat Class
        formPanel.add(createLabel("Seat Class:"));
        formPanel.add(seatClassCombo);
        
        // Seat Selection
        formPanel.add(createLabel("Seat Number:"));
        formPanel.add(seatSelectionCombo);
        
        // Meal Preference
        formPanel.add(createLabel("Meal Preference:"));
        formPanel.add(mealPreferenceCombo);
        
        // Promo Code
        formPanel.add(createLabel("Promo Code (Optional):"));
        formPanel.add(promoCodeText);
        
        // Info labels
        JLabel infoLabel1 = new JLabel("Valid codes: SAVE10, SAVE20, FIRST50");
        infoLabel1.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel1.setForeground(ColorScheme.TEXT_SECONDARY);
        formPanel.add(infoLabel1);
        
        JLabel infoLabel2 = new JLabel("Use View menu to see IDs");
        infoLabel2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel2.setForeground(ColorScheme.TEXT_SECONDARY);
        formPanel.add(infoLabel2);
        
        // Seat info
        JLabel seatInfo1 = new JLabel("Window: A, F | Aisle: C, D");
        seatInfo1.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        seatInfo1.setForeground(ColorScheme.TEXT_SECONDARY);
        formPanel.add(seatInfo1);
        
        JLabel seatInfo2 = new JLabel("Click 'Load Seats' after entering Flight ID");
        seatInfo2.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        seatInfo2.setForeground(ColorScheme.TEXT_SECONDARY);
        formPanel.add(seatInfo2);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 80, 20, 80));
        buttonPanel.setBackground(ColorScheme.CARD_BG);
        
        bookBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bookBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        bookBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        bookBtn.setFocusPainted(false);
        bookBtn.setBorderPainted(false);
        
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(ColorScheme.TEXT_SECONDARY);
        cancelBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        
        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);

        bookBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        return label;
    }

    private void loadAvailableSeats() {
        try {
            String flightIdStr = flightIdText.getText().trim();
            if (flightIdStr.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a Flight ID first!",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            int flightId = Integer.parseInt(flightIdStr);
            Flight flight = mw.getFlightBookingSystem().getFlightByID(flightId);
            
            List<String> availableSeats = SeatManager.getAvailableSeats(flight, mw.getFlightBookingSystem());
            
            seatSelectionCombo.removeAllItems();
            for (String seat : availableSeats) {
                seatSelectionCombo.addItem(seat + " - " + SeatManager.getSeatCategory(seat));
            }
            
            if (availableSeats.isEmpty()) {
                seatSelectionCombo.addItem("No seats available");
                JOptionPane.showMessageDialog(this,
                    "No seats available for this flight!",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Loaded " + availableSeats.size() + " available seats for flight " + flight.getFlightNumber(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid Flight ID format!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == bookBtn) {
            issueBooking();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void issueBooking() {
        try {
            String customerIdStr = customerIdText.getText().trim();
            String flightIdStr = flightIdText.getText().trim();
            
            if (customerIdStr.isEmpty() || flightIdStr.isEmpty()) {
                throw new FlightBookingSystemException("Customer ID and Flight ID are required!");
            }
            
            int customerId = Integer.parseInt(customerIdStr);
            int flightId = Integer.parseInt(flightIdStr);
            
            // Get selected seat
            String selectedSeatDisplay = (String) seatSelectionCombo.getSelectedItem();
            if (selectedSeatDisplay == null || selectedSeatDisplay.equals("No seats available")) {
                throw new FlightBookingSystemException("Please select a valid seat!\nClick 'Load Seats' button first.");
            }
            String selectedSeat = selectedSeatDisplay.split(" - ")[0];
            
            // Get meal preference
            String mealPreference = (String) mealPreferenceCombo.getSelectedItem();
            
            bcu.cmp5332.bookingsystem.model.Flight flight = 
                mw.getFlightBookingSystem().getFlightByID(flightId);
            String selectedClass = (String) seatClassCombo.getSelectedItem();
            flight.setFlightClass(selectedClass);
            
            String promoCode = promoCodeText.getText().trim();
            double discount = 0.0;
            if (!promoCode.isEmpty()) {
                if (promoCode.equalsIgnoreCase("SAVE10")) {
                    discount = 0.10;
                } else if (promoCode.equalsIgnoreCase("SAVE20")) {
                    discount = 0.20;
                } else if (promoCode.equalsIgnoreCase("FIRST50")) {
                    discount = 0.50;
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Invalid promo code!\n\nValid codes: SAVE10, SAVE20, FIRST50", 
                        "Warning", 
                        JOptionPane.WARNING_MESSAGE);
                }
            }
            
            Command addBooking = new AddBooking(customerId, flightId);
            addBooking.execute(mw.getFlightBookingSystem());
            
            bcu.cmp5332.bookingsystem.model.Customer customer = 
                mw.getFlightBookingSystem().getCustomerByID(customerId);
            double originalPrice = 0.0;
            double finalPrice = 0.0;
            
            // Set booking details including meal preference and seat
            for (bcu.cmp5332.bookingsystem.model.Booking booking : customer.getBookings()) {
                if (booking.getFlight().getId() == flightId && !booking.isCancelled()) {
                    originalPrice = booking.getBookingPrice();
                    finalPrice = discount > 0.0 ? originalPrice * (1 - discount) : originalPrice;
                    booking.setBookingPrice(finalPrice);
                    booking.setMealPreference(mealPreference);
                    booking.setSeatNumber(selectedSeat);
                    break;
                }
            }
            
            FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
            
            String message = "Booking Issued Successfully!\n\n" +
                           "Customer: " + customer.getName() + "\n" +
                           "Flight: " + flight.getFlightNumber() + "\n" +
                           "Route: " + flight.getOrigin() + " → " + flight.getDestination() + "\n" +
                           "Class: " + selectedClass + "\n" +
                           "Seat: " + SeatManager.formatSeatDisplay(selectedSeat) + "\n" +
                           "Meal: " + mealPreference + "\n";
            
            if (discount > 0.0) {
                message += "\nPrice Breakdown:\n" +
                          "Original Price: £" + String.format("%.2f", originalPrice) + "\n" +
                          "Discount (" + (int)(discount * 100) + "%): -£" + 
                          String.format("%.2f", (originalPrice - finalPrice)) + "\n" +
                          "Final Price: £" + String.format("%.2f", finalPrice);
            } else {
                message += "\nPrice: £" + String.format("%.2f", finalPrice);
            }
            
            message += "\n\nHave a great flight!";
            
            JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid ID format!\n\nPlease enter numbers only.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}