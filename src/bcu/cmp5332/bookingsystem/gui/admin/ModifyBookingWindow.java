package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.SeatManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Admin window for modifying or cancelling existing bookings.
 * Supports:
 * - Changing seat assignment
 * - Changing meal preference
 * - Cancelling with refund calculation (80% if >7 days before flight)
 * 
 * @author Tejindra Rai
 * @version 1.0 - Admin Enhancement Features
 */
public class ModifyBookingWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField customerIdText = new JTextField();
    private JTextField flightIdText = new JTextField();
    private JButton loadBtn = new JButton("Load Booking");
    
    // Booking details display
    private JLabel currentDetailsLabel;
    private JPanel editPanel;
    
    // Edit fields
    private JComboBox<String> seatCombo = new JComboBox<>();
    private JComboBox<String> mealCombo = new JComboBox<>(new String[]{
        "None", "Vegetarian", "Non-Vegetarian", "Vegan", "Gluten-Free", "Halal", "Kosher"
    });
    
    private JButton saveBtn = new JButton("Save Changes");
    private JButton cancelBookingBtn = new JButton("Cancel Booking");
    private JButton closeBtn = new JButton("Close");
    
    private Booking currentBooking;
    private Customer currentCustomer;
    private Flight currentFlight;

    public ModifyBookingWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Modify Booking");
        setSize(650, 750);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.EDIT);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Modify Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Edit or cancel existing flight reservations");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Main Content Panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(ColorScheme.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Load Section
        JPanel loadPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        loadPanel.setBackground(ColorScheme.CARD_BG);
        
        loadPanel.add(createLabel("Customer ID:"));
        loadPanel.add(customerIdText);
        
        loadPanel.add(createLabel("Flight ID:"));
        loadPanel.add(flightIdText);
        
        loadBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        loadBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loadBtn.setFocusPainted(false);
        loadBtn.addActionListener(this);
        loadPanel.add(new JLabel(""));
        loadPanel.add(loadBtn);
        
        mainPanel.add(loadPanel, BorderLayout.NORTH);
        
        // Current Details Section (hidden initially)
        currentDetailsLabel = new JLabel("<html><i>Load a booking to view details</i></html>");
        currentDetailsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        currentDetailsLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        currentDetailsLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        currentDetailsLabel.setBackground(Color.WHITE);
        currentDetailsLabel.setOpaque(true);
        
        mainPanel.add(currentDetailsLabel, BorderLayout.CENTER);
        
        // Edit Panel (hidden initially)
        editPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        editPanel.setBackground(ColorScheme.CARD_BG);
        editPanel.setVisible(false);
        
        editPanel.add(createLabel("New Seat:"));
        editPanel.add(seatCombo);
        
        editPanel.add(createLabel("Meal Preference:"));
        editPanel.add(mealCombo);
        
        mainPanel.add(editPanel, BorderLayout.SOUTH);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 30, 20, 30));
        buttonPanel.setBackground(ColorScheme.CARD_BG);
        
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        saveBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        saveBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        saveBtn.setFocusPainted(false);
        saveBtn.setEnabled(false);
        saveBtn.addActionListener(this);
        
        cancelBookingBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        cancelBookingBtn.setBackground(ColorScheme.DANGER);
        cancelBookingBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        cancelBookingBtn.setFocusPainted(false);
        cancelBookingBtn.setEnabled(false);
        cancelBookingBtn.addActionListener(this);
        
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(ColorScheme.TEXT_SECONDARY);
        closeBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(this);
        
        buttonPanel.add(saveBtn);
        buttonPanel.add(cancelBookingBtn);
        buttonPanel.add(closeBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loadBtn) {
            loadBooking();
        } else if (ae.getSource() == saveBtn) {
            saveChanges();
        } else if (ae.getSource() == cancelBookingBtn) {
            cancelBooking();
        } else if (ae.getSource() == closeBtn) {
            this.dispose();
        }
    }

    private void loadBooking() {
        try {
            int customerId = Integer.parseInt(customerIdText.getText().trim());
            int flightId = Integer.parseInt(flightIdText.getText().trim());
            
            currentCustomer = mw.getFlightBookingSystem().getCustomerByID(customerId);
            currentFlight = mw.getFlightBookingSystem().getFlightByID(flightId);
            
            // Find the booking
            currentBooking = null;
            for (Booking b : currentCustomer.getBookings()) {
                if (b.getFlight().getId() == flightId) {
                    currentBooking = b;
                    break;
                }
            }
            
            if (currentBooking == null) {
                throw new FlightBookingSystemException("No booking found for this customer and flight!");
            }
            
            if (currentBooking.isCancelled()) {
                throw new FlightBookingSystemException("This booking has already been cancelled!");
            }
            
            // Display booking details
            displayBookingDetails();
            
            // Load available seats
            loadAvailableSeats();
            
            // Enable edit controls
            editPanel.setVisible(true);
            saveBtn.setEnabled(true);
            cancelBookingBtn.setEnabled(true);
            
            // Set current values
            mealCombo.setSelectedItem(currentBooking.getMealPreference());
            if (currentBooking.getSeatNumber() != null) {
                for (int i = 0; i < seatCombo.getItemCount(); i++) {
                    if (seatCombo.getItemAt(i).startsWith(currentBooking.getSeatNumber())) {
                        seatCombo.setSelectedIndex(i);
                        break;
                    }
                }
            }
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid ID format! Please enter numbers only.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayBookingDetails() {
        long daysUntilFlight = ChronoUnit.DAYS.between(
            mw.getFlightBookingSystem().getSystemDate(), 
            currentFlight.getDepartureDate()
        );
        
        String details = "<html>" +
            "<div style='font-family: Segoe UI; padding: 5px;'>" +
            "<h3 style='color: #7D1935; margin: 0 0 10px 0;'>Booking Details</h3>" +
            "<table cellpadding='3' style='font-size: 13px;'>" +
            "<tr><td><b>Customer:</b></td><td>" + currentCustomer.getName() + "</td></tr>" +
            "<tr><td><b>Flight:</b></td><td>" + currentFlight.getFlightNumber() + 
            " (" + currentFlight.getOrigin() + " → " + currentFlight.getDestination() + ")</td></tr>" +
            "<tr><td><b>Date:</b></td><td>" + currentFlight.getDepartureDate() + 
            " (" + daysUntilFlight + " days away)</td></tr>" +
            "<tr><td><b>Booked on:</b></td><td>" + currentBooking.getBookingDate() + "</td></tr>" +
            "<tr><td><b>Seat:</b></td><td>" + 
            (currentBooking.getSeatNumber() != null ? currentBooking.getSeatNumber() : "Not assigned") + 
            "</td></tr>" +
            "<tr><td><b>Meal:</b></td><td>" + currentBooking.getMealPreference() + "</td></tr>" +
            "<tr><td><b>Price Paid:</b></td><td style='color: #D97706; font-weight: bold;'>£" + 
            String.format("%.2f", currentBooking.getBookingPrice()) + "</td></tr>" +
            "</table></div></html>";
        
        currentDetailsLabel.setText(details);
    }

    private void loadAvailableSeats() {
        List<String> availableSeats = SeatManager.getAvailableSeats(currentFlight, mw.getFlightBookingSystem());
        
        // Add current seat back to available seats if it exists
        if (currentBooking.getSeatNumber() != null && !currentBooking.getSeatNumber().isEmpty()) {
            if (!availableSeats.contains(currentBooking.getSeatNumber())) {
                availableSeats.add(0, currentBooking.getSeatNumber());
            }
        }
        
        seatCombo.removeAllItems();
        for (String seat : availableSeats) {
            seatCombo.addItem(seat + " - " + SeatManager.getSeatCategory(seat));
        }
    }

    private void saveChanges() {
        try {
            // Get new values
            String selectedSeat = (String) seatCombo.getSelectedItem();
            if (selectedSeat != null) {
                selectedSeat = selectedSeat.split(" - ")[0];
            }
            String selectedMeal = (String) mealCombo.getSelectedItem();
            
            // Check if anything changed
            boolean changed = false;
            String changes = "";
            
            if (!selectedSeat.equals(currentBooking.getSeatNumber())) {
                currentBooking.setSeatNumber(selectedSeat);
                changes += "Seat changed to: " + selectedSeat + "\n";
                changed = true;
            }
            
            if (!selectedMeal.equals(currentBooking.getMealPreference())) {
                currentBooking.setMealPreference(selectedMeal);
                changes += "Meal preference changed to: " + selectedMeal + "\n";
                changed = true;
            }
            
            if (!changed) {
                JOptionPane.showMessageDialog(this, 
                    "No changes were made.", 
                    "Info", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Save to database
            FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
            
            JOptionPane.showMessageDialog(this, 
                "Booking Updated Successfully!\n\n" +
                changes +
                "\nCustomer: " + currentCustomer.getName() +
                "\nFlight: " + currentFlight.getFlightNumber(), 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh display
            displayBookingDetails();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error saving changes: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelBooking() {
        try {
            long daysUntilFlight = ChronoUnit.DAYS.between(
                mw.getFlightBookingSystem().getSystemDate(), 
                currentFlight.getDepartureDate()
            );
            
            // Calculate refund
            double refundPercentage = daysUntilFlight >= 7 ? 0.80 : 0.50;
            double refundAmount = currentBooking.getBookingPrice() * refundPercentage;
            double cancellationFee = currentBooking.getBookingPrice() - refundAmount;
            
            String message = "Are you sure you want to cancel this booking?\n\n" +
                "Customer: " + currentCustomer.getName() + "\n" +
                "Flight: " + currentFlight.getFlightNumber() + "\n" +
                "Route: " + currentFlight.getOrigin() + " → " + currentFlight.getDestination() + "\n" +
                "Date: " + currentFlight.getDepartureDate() + "\n\n" +
                "REFUND DETAILS:\n" +
                "Original Price: £" + String.format("%.2f", currentBooking.getBookingPrice()) + "\n" +
                "Days until flight: " + daysUntilFlight + " days\n" +
                "Refund Rate: " + (int)(refundPercentage * 100) + "%\n" +
                "Refund Amount: £" + String.format("%.2f", refundAmount) + "\n" +
                "Cancellation Fee: £" + String.format("%.2f", cancellationFee) + "\n\n" +
                "This action cannot be undone!";
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                message, 
                "Confirm Cancellation", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Cancel the booking
                currentBooking.setCancelled(true);
                currentBooking.setCancellationFee(cancellationFee);
                
                // Remove passenger from flight
                currentFlight.removePassenger(currentCustomer);
                
                // Save to database
                FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
                
                JOptionPane.showMessageDialog(this, 
                    "Booking Cancelled Successfully!\n\n" +
                    "Refund of £" + String.format("%.2f", refundAmount) + " processed.\n" +
                    "Cancellation fee: £" + String.format("%.2f", cancellationFee), 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Disable edit controls
                editPanel.setVisible(false);
                saveBtn.setEnabled(false);
                cancelBookingBtn.setEnabled(false);
                
                // Update display
                currentDetailsLabel.setText("<html><i>This booking has been cancelled</i></html>");
                
                this.dispose();
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error cancelling booking: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}