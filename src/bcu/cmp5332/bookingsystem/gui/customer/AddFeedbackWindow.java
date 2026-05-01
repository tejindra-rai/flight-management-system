package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.commands.AddFeedback;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.gui.components.LoadingOverlay;
import bcu.cmp5332.bookingsystem.gui.components.Toast;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
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
import java.util.ArrayList;
import java.util.List;

/**
 * IMPROVED Modern GUI window for adding feedback for completed flights.
 * 
 * @author Binay Chaudhary
 * @version 3.0 - Enhanced with Toast notifications and LoadingOverlay
 */
public class AddFeedbackWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private bcu.cmp5332.bookingsystem.model.FlightBookingSystem fbs;
    private JComboBox<String> flightCombo;
    private JSlider ratingSlider;
    private JLabel ratingLabel;
    private JTextArea commentArea;
    
    private JButton submitBtn;
    private JButton cancelBtn;
    
    private List<Flight> eligibleFlights = new ArrayList<>();
    private int customerId;

    public AddFeedbackWindow(MainWindow mw) {
        this.mw = mw;
        this.fbs = null;
        initialize();
    }

    public AddFeedbackWindow(bcu.cmp5332.bookingsystem.model.FlightBookingSystem fbs) {
        this.mw = null;
        this.fbs = fbs;
        initialize();
    }

    private bcu.cmp5332.bookingsystem.model.FlightBookingSystem getFlightBookingSystem() {
        return (mw != null) ? mw.getFlightBookingSystem() : fbs;
    }

    private void initialize() {
        setTitle("B & T Airlines - Submit Feedback");
        setSize(700, 750);  // FIXED: Increased width to prevent text cutoff
        setLayout(new BorderLayout());
        setLocationRelativeTo(mw);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // ADDED: Proper close operation

        User currentUser = getFlightBookingSystem().getCurrentUser();
        customerId = currentUser.isCustomer() ? currentUser.getLinkedCustomerId() : -1;

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Load eligible flights
        loadEligibleFlights();

        // Content
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // ANIMATION: Fade in
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ColorScheme.PRIMARY_DARK);  // Navy blue
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.STAR, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(50));
        iconLabel.setForeground(ColorScheme.GOLD);  // Yellow/gold star
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Rate Your Flight");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Share your experience with us");
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
        contentPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        if (eligibleFlights.isEmpty()) {
            contentPanel.add(createNoFlightsPanel());
        } else {
            contentPanel.add(createFlightSection());
            contentPanel.add(Box.createVerticalStrut(25));
            contentPanel.add(createRatingSection());
            contentPanel.add(Box.createVerticalStrut(25));
            contentPanel.add(createCommentsSection());
        }

        return contentPanel;
    }

    private JPanel createNoFlightsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setMaximumSize(new Dimension(620, 250));  // FIXED: Increased width

        JLabel iconLabel = new JLabel(FontAwesomeIcon.INFO_CIRCLE);
        iconLabel.setFont(FontAwesomeIcon.getFont(60));
        iconLabel.setForeground(ColorScheme.INFO);  // FIXED: Changed to INFO color
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel messageLabel = new JLabel("No completed flights to review");
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        messageLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        messageLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subLabel = new JLabel("Complete a flight to leave feedback");
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

    private JPanel createFlightSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        section.setMaximumSize(new Dimension(620, 80));  // FIXED: Increased width

        JLabel label = new JLabel(FontAwesomeIcon.PLANE + "  Select Completed Flight");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        flightCombo = new JComboBox<>();
        flightCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightCombo.setMaximumSize(new Dimension(620, 40));  // FIXED: Increased width
        flightCombo.setAlignmentX(LEFT_ALIGNMENT);

        for (Flight flight : eligibleFlights) {
            flightCombo.addItem(flight.getFlightNumber() + " - " + 
                              flight.getOrigin() + " to " + flight.getDestination() +
                              " (" + flight.getDepartureDate() + ")");
        }

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(flightCombo);

        return section;
    }

    private JPanel createRatingSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        section.setMaximumSize(new Dimension(620, 130));  // FIXED: Increased width and height

        JLabel label = new JLabel(FontAwesomeIcon.STAR + "  Your Rating");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        ratingSlider = new JSlider(1, 5, 5);
        ratingSlider.setMajorTickSpacing(1);
        ratingSlider.setPaintTicks(true);
        ratingSlider.setPaintLabels(true);
        ratingSlider.setSnapToTicks(true);
        ratingSlider.setMaximumSize(new Dimension(620, 60));  // FIXED: Increased width
        ratingSlider.setAlignmentX(LEFT_ALIGNMENT);
        ratingSlider.setBackground(ColorScheme.BACKGROUND);
        
        ratingLabel = new JLabel(getStars(5) + " Excellent");
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));  // FIXED: Increased font size
        ratingLabel.setForeground(ColorScheme.GOLD);  // Yellow stars
        ratingLabel.setAlignmentX(LEFT_ALIGNMENT);

        ratingSlider.addChangeListener(e -> {
            int rating = ratingSlider.getValue();
            ratingLabel.setText(getStars(rating) + " " + getRatingText(rating));
            ratingLabel.setForeground(ColorScheme.GOLD);  // Keep stars yellow
        });

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(ratingSlider);
        section.add(Box.createVerticalStrut(10));
        section.add(ratingLabel);

        return section;
    }

    private JPanel createCommentsSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        section.setMaximumSize(new Dimension(620, 220));  // FIXED: Increased width

        JLabel label = new JLabel(FontAwesomeIcon.COMMENT + "  Your Comments");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        commentArea = new JTextArea(6, 50);  // FIXED: Increased rows
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_MEDIUM, 1),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JScrollPane scrollPane = new JScrollPane(commentArea);
        scrollPane.setMaximumSize(new Dimension(620, 160));  // FIXED: Increased width and height
        scrollPane.setAlignmentX(LEFT_ALIGNMENT);

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(scrollPane);

        return section;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));  // FIXED: Increased gap
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(20, 40, 30, 40));

        submitBtn = createStyledButton(FontAwesomeIcon.CHECK + "  Submit Feedback", ColorScheme.SUCCESS);
        submitBtn.addActionListener(this);
        submitBtn.setEnabled(!eligibleFlights.isEmpty());

        cancelBtn = createStyledButton(FontAwesomeIcon.TIMES_CIRCLE + "  Cancel", ColorScheme.DANGER);
        cancelBtn.addActionListener(this);

        buttonPanel.add(submitBtn);
        buttonPanel.add(cancelBtn);

        return buttonPanel;
    }

    // FIXED: Better button sizing
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(14));  // FIXED: Slightly smaller font
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(240, 50));  // FIXED: Wider button
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(12, 30, 12, 30));  // FIXED: More padding

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
        if (ae.getSource() == submitBtn) {
            submitFeedback();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void loadEligibleFlights() {
        try {
            Customer customer = getFlightBookingSystem().getCustomerByID(customerId);
            if (customer == null) {  // ADDED: Null check
                return;
            }
            
            for (Booking booking : customer.getBookings()) {
                if (!booking.isCancelled() && !booking.getFlight().getDepartureDate().isAfter(
                        getFlightBookingSystem().getSystemDate())) {
                    eligibleFlights.add(booking.getFlight());
                }
            }
        } catch (FlightBookingSystemException e) {
            // Handle silently
        }
    }

    // IMPROVED: Submit feedback with loading state and toast
    private void submitFeedback() {
        if (flightCombo.getSelectedIndex() < 0) {
            Toast.showWarning(this, "Please select a flight");
            return;
        }

        String comment = commentArea.getText().trim();
        if (comment.isEmpty()) {
            Toast.showWarning(this, "Please enter your comments");
            UIAnimations.shake(commentArea, 10, 3);
            return;
        }

        // ADDED: Comment length validation
        if (comment.length() < 10) {
            Toast.showWarning(this, "Please provide at least 10 characters of feedback");
            UIAnimations.shake(commentArea, 10, 3);
            return;
        }

        Flight selectedFlight = eligibleFlights.get(flightCombo.getSelectedIndex());
        int rating = ratingSlider.getValue();

        LoadingOverlay loading = new LoadingOverlay("Submitting your feedback...");
        final JFrame parentFrame = this;  // FIXED: Store reference
        loading.show(parentFrame);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(800);  // FIXED: Increased delay for better UX
                
                Command addFeedback = new AddFeedback(customerId, selectedFlight.getId(), rating, comment);
                addFeedback.execute(getFlightBookingSystem());
                FlightBookingSystemData.safeStore(getFlightBookingSystem());
                
                return null;
            }

            @Override
            protected void done() {
                loading.hide(parentFrame);  // FIXED: Use stored reference

                try {
                    get();
                    
                    // IMPROVED: Toast notification
                    Toast.showSuccess(parentFrame, 
                        "Thank you for your feedback!");

                    Timer timer = new Timer(1500, e -> dispose());
                    timer.setRepeats(false);
                    timer.start();

                } catch (Exception ex) {
                    Toast.showError(parentFrame, 
                        "Failed to submit feedback: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private String getStars(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append(FontAwesomeIcon.STAR).append(" ");
        }
        return stars.toString().trim();
    }

    private String getRatingText(int rating) {
        switch (rating) {
            case 1: return "Poor";
            case 2: return "Fair";
            case 3: return "Good";
            case 4: return "Very Good";
            case 5: return "Excellent";
            default: return "";
        }
    }
}