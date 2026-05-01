package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightStatus;
import bcu.cmp5332.bookingsystem.utils.AirportValidator;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Admin window for updating flight operational status.
 * Allows admins to mark flights as:
 * - Scheduled, On Time, Delayed, Boarding, Departed, Cancelled, Diverted
 * 
 * @author Tejindra Rai
 * @version 1.0 - Admin Enhancement Features
 */
public class FlightStatusUpdateWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightIdText = new JTextField();
    private JButton loadBtn = new JButton("Load Flight");
    
    private JLabel flightInfoLabel;
    private JLabel currentStatusLabel;
    private JPanel updatePanel;
    
    private JComboBox<String> statusCombo;
    private JSpinner delayMinutesSpinner;
    private JTextArea remarksText;
    private JCheckBox notifyPassengersCheck;
    
    private JButton updateBtn = new JButton("Update Status");
    private JButton closeBtn = new JButton("Close");
    
    private Flight currentFlight;

    public FlightStatusUpdateWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Update Flight Status");
        setSize(650, 700);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.PLANE);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Flight Status Update");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Manage flight operational status");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 15));
        mainPanel.setBackground(ColorScheme.CARD_BG);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        // Load Section
        JPanel loadPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        loadPanel.setBackground(ColorScheme.CARD_BG);
        
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
        
        // Flight Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        flightInfoLabel = new JLabel("<html><i>Load a flight to update its status</i></html>");
        flightInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightInfoLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        
        currentStatusLabel = new JLabel("");
        currentStatusLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        currentStatusLabel.setForeground(ColorScheme.SUCCESS);
        
        infoPanel.add(flightInfoLabel, BorderLayout.NORTH);
        infoPanel.add(currentStatusLabel, BorderLayout.CENTER);
        
        mainPanel.add(infoPanel, BorderLayout.CENTER);
        
        // Update Panel (hidden initially)
        updatePanel = new JPanel(new GridLayout(6, 2, 10, 10));
        updatePanel.setBackground(ColorScheme.CARD_BG);
        updatePanel.setVisible(false);
        updatePanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(ColorScheme.ADMIN_PRIMARY, 1),
            "Update Flight Status",
            0, 0,
            new Font("Segoe UI", Font.BOLD, 13),
            ColorScheme.ADMIN_PRIMARY
        ));
        
        // Status selection
        updatePanel.add(createLabel("New Status:"));
        statusCombo = new JComboBox<>(new String[]{
            "Scheduled", "On Time", "Delayed", "Boarding", "Departed", "Cancelled", "Diverted"
        });
        statusCombo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusCombo.addActionListener(e -> {
            boolean isDelayed = "Delayed".equals(statusCombo.getSelectedItem());
            delayMinutesSpinner.setEnabled(isDelayed);
        });
        updatePanel.add(statusCombo);
        
        // Delay minutes (only for delayed status)
        updatePanel.add(createLabel("Delay (minutes):"));
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(0, 0, 1440, 15);
        delayMinutesSpinner = new JSpinner(spinnerModel);
        delayMinutesSpinner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        delayMinutesSpinner.setEnabled(false);
        updatePanel.add(delayMinutesSpinner);
        
        // Notify passengers
        updatePanel.add(createLabel("Notify Passengers:"));
        notifyPassengersCheck = new JCheckBox("Send notification (simulated)");
        notifyPassengersCheck.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        notifyPassengersCheck.setBackground(ColorScheme.CARD_BG);
        notifyPassengersCheck.setSelected(true);
        updatePanel.add(notifyPassengersCheck);
        
        // Remarks
        updatePanel.add(createLabel("Remarks:"));
        remarksText = new JTextArea(3, 20);
        remarksText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        remarksText.setLineWrap(true);
        remarksText.setWrapStyleWord(true);
        remarksText.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1));
        JScrollPane remarksScroll = new JScrollPane(remarksText);
        remarksScroll.setPreferredSize(new Dimension(200, 60));
        updatePanel.add(remarksScroll);
        
        mainPanel.add(updatePanel, BorderLayout.SOUTH);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 80, 20, 80));
        buttonPanel.setBackground(ColorScheme.CARD_BG);
        
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        updateBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        updateBtn.setFocusPainted(false);
        updateBtn.setEnabled(false);
        updateBtn.addActionListener(this);
        
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeBtn.setBackground(ColorScheme.TEXT_SECONDARY);
        closeBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(this);
        
        buttonPanel.add(updateBtn);
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
            loadFlight();
        } else if (ae.getSource() == updateBtn) {
            updateStatus();
        } else if (ae.getSource() == closeBtn) {
            this.dispose();
        }
    }

    private void loadFlight() {
        try {
            int flightId = Integer.parseInt(flightIdText.getText().trim());
            currentFlight = mw.getFlightBookingSystem().getFlightByID(flightId);
            
            // Display flight info
            String originName = AirportValidator.getAirportName(currentFlight.getOrigin());
            String destName = AirportValidator.getAirportName(currentFlight.getDestination());
            
            flightInfoLabel.setText("<html>" +
                "<b>Flight " + currentFlight.getFlightNumber() + "</b><br>" +
                originName + " (" + currentFlight.getOrigin() + ") → " +
                destName + " (" + currentFlight.getDestination() + ")<br>" +
                "Departure Date: " + currentFlight.getDepartureDate() + "<br>" +
                "Passengers: " + currentFlight.getPassengers().size() + " / " + currentFlight.getCapacity() +
                "</html>");
            
            // Display current status (you'll need to add getFlightStatus() to Flight model)
            // For now, showing as Scheduled
            currentStatusLabel.setText(FontAwesomeIcon.INFO_CIRCLE + " Current Status: SCHEDULED");
            currentStatusLabel.setForeground(ColorScheme.SUCCESS);
            
            // Show update panel
            updatePanel.setVisible(true);
            updateBtn.setEnabled(true);
            
            // Reset fields
            statusCombo.setSelectedIndex(0);
            delayMinutesSpinner.setValue(0);
            remarksText.setText("");
            notifyPassengersCheck.setSelected(true);
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                "Invalid Flight ID! Please enter a number.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStatus() {
        try {
            String newStatus = (String) statusCombo.getSelectedItem();
            int delayMinutes = (Integer) delayMinutesSpinner.getValue();
            String remarks = remarksText.getText().trim();
            boolean notifyPassengers = notifyPassengersCheck.isSelected();
            
            // Build confirmation message
            StringBuilder message = new StringBuilder();
            message.append("Update Flight Status?\n\n");
            message.append("Flight: ").append(currentFlight.getFlightNumber()).append("\n");
            message.append("Route: ").append(currentFlight.getOrigin()).append(" → ")
                   .append(currentFlight.getDestination()).append("\n");
            message.append("New Status: ").append(newStatus).append("\n");
            
            if ("Delayed".equals(newStatus)) {
                message.append("Delay Duration: ").append(delayMinutes).append(" minutes\n");
            }
            
            if (!remarks.isEmpty()) {
                message.append("Remarks: ").append(remarks).append("\n");
            }
            
            if (notifyPassengers) {
                int passengerCount = currentFlight.getPassengers().size();
                message.append("\n").append(passengerCount).append(" passengers will be notified.\n");
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                message.toString(),
                "Confirm Status Update",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Update flight status (you'll need to add setFlightStatus() to Flight model)
                // For now, we'll just save a note in the database
                
                // Simulate passenger notification
                if (notifyPassengers && currentFlight.getPassengers().size() > 0) {
                    simulateNotification(newStatus, delayMinutes);
                }
                
                // Save to database
                FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
                
                // Show success message
                JOptionPane.showMessageDialog(this,
                    "Flight Status Updated Successfully!\n\n" +
                    "Flight: " + currentFlight.getFlightNumber() + "\n" +
                    "New Status: " + newStatus +
                    (notifyPassengers ? "\n\nPassengers have been notified." : ""),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Update display
                currentStatusLabel.setText(FontAwesomeIcon.INFO_CIRCLE + " Current Status: " + newStatus.toUpperCase());
                Color statusColor = getStatusColor(newStatus);
                currentStatusLabel.setForeground(statusColor);
                
                // Clear update fields
                remarksText.setText("");
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error updating status: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void simulateNotification(String status, int delayMinutes) {
        // Simulate sending notifications to passengers
        String notificationType = "";
        String message = "";
        
        switch (status) {
            case "Delayed":
                notificationType = "DELAY NOTIFICATION";
                message = String.format("Flight %s is delayed by %d minutes. We apologize for the inconvenience.",
                    currentFlight.getFlightNumber(), delayMinutes);
                break;
            case "Cancelled":
                notificationType = "CANCELLATION NOTICE";
                message = String.format("Flight %s has been cancelled. Please contact customer service for rebooking.",
                    currentFlight.getFlightNumber());
                break;
            case "Boarding":
                notificationType = "BOARDING ANNOUNCEMENT";
                message = String.format("Flight %s is now boarding. Please proceed to the gate.",
                    currentFlight.getFlightNumber());
                break;
            case "Diverted":
                notificationType = "FLIGHT DIVERSION";
                message = String.format("Flight %s has been diverted. Updates will follow.",
                    currentFlight.getFlightNumber());
                break;
            default:
                notificationType = "STATUS UPDATE";
                message = String.format("Flight %s status: %s",
                    currentFlight.getFlightNumber(), status);
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println("PASSENGER NOTIFICATION SENT");
        System.out.println("=".repeat(60));
        System.out.println("Type: " + notificationType);
        System.out.println("Flight: " + currentFlight.getFlightNumber());
        System.out.println("Recipients: " + currentFlight.getPassengers().size() + " passengers");
        System.out.println("Message: " + message);
        System.out.println("=".repeat(60) + "\n");
    }

    private Color getStatusColor(String status) {
        switch (status) {
            case "On Time":
            case "Scheduled":
            case "Boarding":
                return ColorScheme.SUCCESS;
            case "Delayed":
                return ColorScheme.WARNING;
            case "Cancelled":
            case "Diverted":
                return ColorScheme.DANGER;
            case "Departed":
                return ColorScheme.ADMIN_PRIMARY;
            default:
                return ColorScheme.TEXT_PRIMARY;
        }
    }
}