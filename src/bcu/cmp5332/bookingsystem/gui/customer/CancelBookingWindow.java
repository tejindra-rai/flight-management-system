package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.commands.CancelBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.gui.components.LoadingOverlay;
import bcu.cmp5332.bookingsystem.gui.components.Toast;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.UIAnimations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

/**
 * IMPROVED Modern GUI window for canceling an existing booking.
 * 
 * @author Binay Chaudhary
 * @version 6.0 - Enhanced with Toast notifications and LoadingOverlay
 */
public class CancelBookingWindow extends JFrame implements ActionListener {

    private FlightBookingSystem fbs;
    private User currentUser;
    private Customer customer;
    private JComboBox<String> bookingCombo;
    private List<Booking> activeBookings;
    private boolean isAdminMode = false;

    private JButton cancelBookingBtn;
    private JButton closeBtn;

    public CancelBookingWindow(FlightBookingSystem fbs, User currentUser) {
        this.fbs = fbs;
        this.currentUser = currentUser;
        this.isAdminMode = false;
        
        try {
            this.customer = fbs.getCustomerByID(currentUser.getLinkedCustomerId());
        } catch (Exception e) {
            Toast.showError(null, "Error loading customer profile: " + e.getMessage());
        }
        
        initialize();
    }

    public CancelBookingWindow(MainWindow mw) {
        this.fbs = mw.getFlightBookingSystem();
        this.currentUser = fbs.getCurrentUser();
        this.isAdminMode = true;
        this.customer = null;
        
        initializeAdminMode();
    }

    private void initialize() {
        setTitle("B & T Airlines - Cancel Booking");
        setSize(700, 550);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Get active bookings
        activeBookings = customer.getBookings().stream()
            .filter(b -> !b.isCancelled())
            .collect(Collectors.toList());

        // FIX: Create buttons BEFORE content panel so cancelBookingBtn exists
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Content (now buttons exist)
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        
        // ANIMATION: Fade in
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private void initializeAdminMode() {
        setTitle("B & T Airlines - Cancel Booking (Admin)");
        setSize(700, 550);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // FIX: Create buttons BEFORE content panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel contentPanel = createAdminContentPanel();
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        add(mainPanel);
        
        // ANIMATION: Fade in
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ColorScheme.DANGER);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.TIMES_CIRCLE, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(50));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Cancel Booking");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Cancel your flight reservation");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.9));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(subtitleLabel);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(40, 50, 30, 50));

        if (activeBookings.isEmpty()) {
            // No active bookings
            JPanel noBookingsPanel = createNoBookingsPanel();
            contentPanel.add(noBookingsPanel);
            cancelBookingBtn.setEnabled(false);
        } else {
            // Booking selection
            contentPanel.add(createBookingSection());
            contentPanel.add(Box.createVerticalStrut(25));

            // Warning panel
            contentPanel.add(createWarningPanel());
        }

        return contentPanel;
    }

    private JPanel createAdminContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(40, 50, 30, 50));

        JLabel infoLabel = new JLabel("Admin mode: Use main customer selection");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        infoLabel.setAlignmentX(CENTER_ALIGNMENT);

        contentPanel.add(Box.createVerticalStrut(50));
        contentPanel.add(infoLabel);

        return contentPanel;
    }

    private JPanel createNoBookingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setMaximumSize(new Dimension(600, 200));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.INFO_CIRCLE);
        iconLabel.setFont(FontAwesomeIcon.getFont(60));
        iconLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("No active bookings");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("You don't have any bookings to cancel");
        subLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        subLabel.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(Box.createVerticalStrut(40));
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(messageLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(subLabel);

        return panel;
    }

    private JPanel createBookingSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        section.setMaximumSize(new Dimension(600, 80));

        JLabel label = new JLabel(FontAwesomeIcon.TICKET + "  Select Booking to Cancel");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        bookingCombo = new JComboBox<>();
        bookingCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookingCombo.setMaximumSize(new Dimension(600, 40));
        bookingCombo.setAlignmentX(LEFT_ALIGNMENT);

        for (Booking booking : activeBookings) {
            Flight flight = booking.getFlight();
            bookingCombo.addItem(
                flight.getFlightNumber() + " - " + 
                flight.getOrigin() + " to " + flight.getDestination() + 
                " (" + flight.getDepartureDate() + ")"
            );
        }

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(bookingCombo);

        return section;
    }

    private JPanel createWarningPanel() {
        JPanel warningPanel = new JPanel();
        warningPanel.setBackground(ColorScheme.withOpacity(ColorScheme.DANGER_LIGHT, 0.2));
        warningPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.DANGER, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        warningPanel.setLayout(new BoxLayout(warningPanel, BoxLayout.Y_AXIS));
        warningPanel.setMaximumSize(new Dimension(600, 140));
        warningPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel warningIcon = new JLabel(FontAwesomeIcon.EXCLAMATION_TRIANGLE, SwingConstants.LEFT);
        warningIcon.setFont(FontAwesomeIcon.getFont(24));
        warningIcon.setForeground(ColorScheme.DANGER);
        warningIcon.setAlignmentX(LEFT_ALIGNMENT);

        JLabel warningTitle = new JLabel("Cancellation Policy");
        warningTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        warningTitle.setForeground(ColorScheme.TEXT_PRIMARY);
        warningTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel warningText = new JLabel("<html>A cancellation fee of <b>£10.00</b> will be charged. This action cannot be undone.</html>");
        warningText.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        warningText.setForeground(ColorScheme.TEXT_SECONDARY);
        warningText.setAlignmentX(LEFT_ALIGNMENT);

        warningPanel.add(warningIcon);
        warningPanel.add(Box.createVerticalStrut(8));
        warningPanel.add(warningTitle);
        warningPanel.add(Box.createVerticalStrut(5));
        warningPanel.add(warningText);

        return warningPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(20, 40, 30, 40));

        cancelBookingBtn = createStyledButton(
            FontAwesomeIcon.TIMES_CIRCLE + "  Cancel Booking", 
            ColorScheme.DANGER
        );
        cancelBookingBtn.addActionListener(this);

        closeBtn = createStyledButton(
            FontAwesomeIcon.ARROW_LEFT + "  Go Back", 
            ColorScheme.TEXT_SECONDARY
        );
        closeBtn.addActionListener(this);

        buttonPanel.add(cancelBookingBtn);
        buttonPanel.add(closeBtn);

        return buttonPanel;
    }

    // IMPROVED: Styled button with animations
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(220, 50));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 25, 12, 25));

        Color hoverColor = ColorScheme.getHoverColor(bgColor);
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                UIAnimations.smoothColorTransition(btn, bgColor, hoverColor, 150);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                UIAnimations.smoothColorTransition(btn, btn.getBackground(), bgColor, 150);
            }
        });

        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == cancelBookingBtn) {
            cancelBooking();
        } else if (ae.getSource() == closeBtn) {
            this.dispose();
        }
    }

    // IMPROVED: Cancel booking with loading state and toast
    private void cancelBooking() {
        if (activeBookings.isEmpty()) {
            Toast.showWarning(this, "No active bookings to cancel");
            return;
        }

        int selectedIndex = bookingCombo.getSelectedIndex();
        if (selectedIndex < 0) {
            Toast.showWarning(this, "Please select a booking to cancel");
            return;
        }

        Booking selectedBooking = activeBookings.get(selectedIndex);
        Flight flight = selectedBooking.getFlight();

        // Confirm cancellation
        int confirm = JOptionPane.showConfirmDialog(this,
            "Confirm Cancellation\n\n" +
            "Customer: " + customer.getName() + "\n" +
            "Flight: " + flight.getFlightNumber() + "\n" +
            "Route: " + flight.getOrigin() + " → " + flight.getDestination() + "\n\n" +
            "Cancellation Fee: £10.00\n\n" +
            "This action cannot be undone. Continue?",
            "Confirm Cancellation",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // IMPROVED: Show loading overlay
            LoadingOverlay loading = new LoadingOverlay("Cancelling your booking...");
            loading.show(this);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(500);
                    
                    Command cancelCmd = new CancelBooking(customer.getId(), flight.getId());
                    cancelCmd.execute(fbs);
                    FlightBookingSystemData.safeStore(fbs);
                    
                    return null;
                }

                @Override
                protected void done() {
                    loading.hide(CancelBookingWindow.this);

                    try {
                        get();
                        
                        // IMPROVED: Toast instead of JOptionPane
                        Toast.showSuccess(CancelBookingWindow.this,
                            "Booking cancelled successfully");

                        Timer timer = new Timer(1500, e -> dispose());
                        timer.setRepeats(false);
                        timer.start();

                    } catch (Exception ex) {
                        Toast.showError(CancelBookingWindow.this,
                            "Cancellation failed: " + ex.getMessage());
                    }
                }
            };
            worker.execute();
        }
    }
}