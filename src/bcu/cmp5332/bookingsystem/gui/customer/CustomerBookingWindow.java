package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.gui.components.LoadingOverlay;
import bcu.cmp5332.bookingsystem.gui.components.ModernTextField;
import bcu.cmp5332.bookingsystem.gui.components.Toast;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.SeatManager;
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
 * IMPROVED booking window for customers with professional design.
 * Now includes meal preference, seat selection, and modern components.
 * 
 * @author Tejindra Rai
 * @version 4.1 - FIXED: NullPointerException in updateFlightDetails() and updatePrice()
 */
public class CustomerBookingWindow extends JFrame implements ActionListener {

    private FlightBookingSystem fbs;
    private User currentUser;
    private CustomerMainWindow parentWindow;
    
    private JComboBox<String> flightCombo;
    private JComboBox<String> flightTypeFilter;
    private ModernTextField promoCodeField;  // CHANGED: ModernTextField
    private JComboBox<String> seatClassCombo;
    private JComboBox<String> mealPreferenceCombo;
    private JComboBox<String> seatSelectionCombo;
    private JTextArea flightDetailsArea;
    private JLabel priceLabel;  // NEW: Price display

    private JButton bookBtn;
    private JButton cancelBtn;
    
    private List<Flight> availableFlights;
    private List<Flight> allFlights;
    private Flight selectedFlight;

    public CustomerBookingWindow(MainWindow mw) {
        this.fbs = mw.getFlightBookingSystem();
        this.currentUser = fbs.getCurrentUser();
        this.parentWindow = null;
        initialize();
    }

    public CustomerBookingWindow(FlightBookingSystem fbs, User currentUser) {
        this(fbs, currentUser, null);
    }

    public CustomerBookingWindow(FlightBookingSystem fbs, User currentUser, CustomerMainWindow parentWindow) {
        this.fbs = fbs;
        this.currentUser = currentUser;
        this.parentWindow = parentWindow;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Book Flight");
        setSize(900, 950);  // Increased width from 700 to 900 for better layout
        setLayout(new BorderLayout());
        setLocationRelativeTo(parentWindow);
        setMinimumSize(new Dimension(700, 800));  // Add minimum size for responsiveness

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

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
        headerPanel.setBackground(ColorScheme.PRIMARY_MEDIUM);  // Changed from SUCCESS to PRIMARY_MEDIUM
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.PLUS_CIRCLE, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(50));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Book Your Flight");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Find and reserve your perfect journey");
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
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));  // Reduced horizontal padding for responsiveness

        // Get flights
        allFlights = fbs.getFutureFlights();
        availableFlights = new ArrayList<>(allFlights);

        // Flight Type Filter
        contentPanel.add(createFilterSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Flight Selection
        contentPanel.add(createFlightSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Seat Class
        contentPanel.add(createClassSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Seat Selection
        contentPanel.add(createSeatSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // Meal Preference
        contentPanel.add(createMealSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // NEW: Price Display
        contentPanel.add(createPriceSection());
        contentPanel.add(Box.createVerticalStrut(20));

        // IMPROVED: Promo Code with ModernTextField
        contentPanel.add(createPromoSection());
        contentPanel.add(Box.createVerticalStrut(25));

        // Flight Details
        contentPanel.add(createDetailsSection());

        return contentPanel;
    }

    private JPanel createFilterSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        // Removed fixed maxWidth for responsiveness

        JLabel label = new JLabel(FontAwesomeIcon.GLOBE + "  Flight Type");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        String[] filterOptions = {"All Flights", "Domestic Only", "International Only"};
        flightTypeFilter = new JComboBox<>(filterOptions);
        flightTypeFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightTypeFilter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));  // Full width with max height
        flightTypeFilter.setAlignmentX(LEFT_ALIGNMENT);
        flightTypeFilter.addActionListener(e -> filterFlights());

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(flightTypeFilter);

        return section;
    }

    private JPanel createFlightSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        // Removed fixed maxWidth for responsiveness

        JLabel label = new JLabel(FontAwesomeIcon.PLANE + "  Select Flight");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        flightCombo = new JComboBox<>();
        flightCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));  // Full width with max height
        flightCombo.setAlignmentX(LEFT_ALIGNMENT);
        flightCombo.addActionListener(e -> {
            updateFlightDetails();
            updateAvailableSeats();
            updatePrice();  // NEW
        });
        updateFlightComboBox();

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(flightCombo);

        return section;
    }

    private JPanel createClassSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        // Removed fixed maxWidth for responsiveness

        JLabel label = new JLabel(FontAwesomeIcon.STAR + "  Seat Class");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        seatClassCombo = new JComboBox<>(new String[]{"Economy", "Business", "First Class"});
        seatClassCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        seatClassCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));  // Full width with max height
        seatClassCombo.setAlignmentX(LEFT_ALIGNMENT);

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(seatClassCombo);

        return section;
    }

    private JPanel createSeatSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        // Removed fixed maxWidth for responsiveness

        JLabel label = new JLabel(FontAwesomeIcon.CHECK + "  Choose Your Seat");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        seatSelectionCombo = new JComboBox<>();
        seatSelectionCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        seatSelectionCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));  // Full width with max height
        seatSelectionCombo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hintLabel = new JLabel("Window seats: A, F  |  Aisle seats: C, D  |  Middle seats: B, E");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        hintLabel.setAlignmentX(LEFT_ALIGNMENT);

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(seatSelectionCombo);
        section.add(Box.createVerticalStrut(5));
        section.add(hintLabel);

        return section;
    }

    private JPanel createMealSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        // Removed fixed maxWidth for responsiveness

        JLabel label = new JLabel(FontAwesomeIcon.STAR + "  Meal Preference");
        label.setFont(FontAwesomeIcon.getFont(15));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        label.setAlignmentX(LEFT_ALIGNMENT);

        mealPreferenceCombo = new JComboBox<>(new String[]{
            "None", "Vegetarian", "Non-Vegetarian", "Vegan", "Gluten-Free", "Halal", "Kosher"
        });
        mealPreferenceCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mealPreferenceCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));  // Full width with max height
        mealPreferenceCombo.setAlignmentX(LEFT_ALIGNMENT);

        JLabel hintLabel = new JLabel("Select your dietary preference for in-flight meals");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hintLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        hintLabel.setAlignmentX(LEFT_ALIGNMENT);

        section.add(label);
        section.add(Box.createVerticalStrut(8));
        section.add(mealPreferenceCombo);
        section.add(Box.createVerticalStrut(5));
        section.add(hintLabel);

        return section;
    }

    // NEW: Price display section
    private JPanel createPriceSection() {
        JPanel section = new JPanel();
        section.setBackground(ColorScheme.withOpacity(ColorScheme.SUCCESS_LIGHT, 0.2));
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.SUCCESS, 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        // Removed fixed maxWidth for responsiveness
        section.setAlignmentX(LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("Estimated Price");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);

        priceLabel = new JLabel("Select a flight to see price");
        priceLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        priceLabel.setForeground(ColorScheme.SUCCESS);
        priceLabel.setAlignmentX(LEFT_ALIGNMENT);

        section.add(titleLabel);
        section.add(Box.createVerticalStrut(5));
        section.add(priceLabel);

        return section;
    }

    // IMPROVED: Promo Code with ModernTextField
    private JPanel createPromoSection() {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(ColorScheme.BACKGROUND);
        // Removed fixed maxWidth for responsiveness

        promoCodeField = new ModernTextField(
            "Enter promo code (optional)",
            FontAwesomeIcon.TICKET,
            text -> {
                if (text.isEmpty()) return true;
                return text.matches("[A-Z0-9]{5,10}");
            },
            "Promo code must be 5-10 uppercase letters/numbers"
        );
        promoCodeField.setAlignmentX(LEFT_ALIGNMENT);

        section.add(promoCodeField);

        return section;
    }

    private JPanel createDetailsSection() {
        JPanel section = new JPanel(new BorderLayout(0, 12));
        section.setBackground(ColorScheme.CARD_BG);
        section.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        // Removed fixed maxWidth for responsiveness

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(ColorScheme.CARD_BG);

        JLabel iconLabel = new JLabel(FontAwesomeIcon.PLANE);
        iconLabel.setFont(FontAwesomeIcon.getFont(16));
        iconLabel.setForeground(ColorScheme.PRIMARY_LIGHT);

        JLabel titleLabel = new JLabel("Flight Details");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        flightDetailsArea = new JTextArea(10, 30);
        flightDetailsArea.setEditable(false);
        flightDetailsArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        flightDetailsArea.setBackground(ColorScheme.BACKGROUND);
        flightDetailsArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        flightDetailsArea.setLineWrap(true);
        flightDetailsArea.setWrapStyleWord(true);

        JScrollPane scrollPane = new JScrollPane(flightDetailsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT));

        section.add(titlePanel, BorderLayout.NORTH);
        section.add(scrollPane, BorderLayout.CENTER);

        if (availableFlights.size() > 0) {
            updateFlightDetails();
            updateAvailableSeats();
        } else {
            flightDetailsArea.setText("No flights available at the moment.\nPlease check back later.");
            bookBtn.setEnabled(false);
        }

        return section;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(20, 30, 30, 30));  // Reduced horizontal padding

        bookBtn = createStyledButton(FontAwesomeIcon.CHECK + "  Book Flight", ColorScheme.SUCCESS);
        bookBtn.addActionListener(this);

        cancelBtn = createStyledButton(FontAwesomeIcon.TIMES_CIRCLE + "  Cancel", ColorScheme.DANGER);
        cancelBtn.addActionListener(this);

        buttonPanel.add(bookBtn);
        buttonPanel.add(cancelBtn);

        return buttonPanel;
    }

    // IMPROVED: Styled button with animations
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(200, 50));
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
        if (ae.getSource() == bookBtn) {
            bookFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void filterFlights() {
        String selected = (String) flightTypeFilter.getSelectedItem();
        availableFlights.clear();

        for (Flight flight : allFlights) {
            if (selected.equals("All Flights")) {
                availableFlights.add(flight);
            } else if (selected.equals("Domestic Only") && flight.getFlightType() == FlightType.DOMESTIC) {
                availableFlights.add(flight);
            } else if (selected.equals("International Only") && flight.getFlightType() == FlightType.INTERNATIONAL) {
                availableFlights.add(flight);
            }
        }

        updateFlightComboBox();
    }

    private void updateFlightComboBox() {
        flightCombo.removeAllItems();
        for (Flight flight : availableFlights) {
            flightCombo.addItem(flight.getFlightNumber() + " - " + 
                              flight.getOrigin() + " to " + flight.getDestination());
        }
    }

    private void updateFlightDetails() {
        // FIX: Check if flightDetailsArea is initialized to avoid NullPointerException
        if (flightDetailsArea == null) {
            return; // Skip update if details area not created yet
        }
        
        int index = flightCombo.getSelectedIndex();
        if (index >= 0 && index < availableFlights.size()) {
            selectedFlight = availableFlights.get(index);
            
            String details = String.format(
                "Flight: %s\n" +
                "Route: %s → %s\n" +
                "Date: %s\n" +
                "Type: %s\n" +
                "Available Seats: %d / %d\n",
                selectedFlight.getFlightNumber(),
                selectedFlight.getOrigin(),
                selectedFlight.getDestination(),
                selectedFlight.getDepartureDate(),
                selectedFlight.getFlightType().getDisplayName(),
                selectedFlight.getAvailableSeats(),
                selectedFlight.getCapacity()
            );
            
            flightDetailsArea.setText(details);
        }
    }

    // NEW: Update price display
    private void updatePrice() {
        // FIX: Check if priceLabel is initialized to avoid NullPointerException
        if (priceLabel == null) {
            return; // Skip update if price label not created yet
        }
        
        if (selectedFlight != null) {
            double price = selectedFlight.calculatePrice(fbs.getSystemDate());
            priceLabel.setText("£" + String.format("%.2f", price));
        }
    }

    private void updateAvailableSeats() {
        // FIX: Check if seatSelectionCombo is initialized to avoid NullPointerException
        if (seatSelectionCombo == null) {
            return; // Skip update if seat combo not created yet
        }
        
        seatSelectionCombo.removeAllItems();
        
        if (selectedFlight != null && selectedFlight.getAvailableSeats() > 0) {
            char[] rows = {'A', 'B', 'C', 'D', 'E', 'F'};
            int maxRows = Math.min(20, selectedFlight.getAvailableSeats());
            
            for (int i = 1; i <= maxRows; i++) {
                for (char row : rows) {
                    seatSelectionCombo.addItem(i + row + "");
                }
            }
        } else {
            seatSelectionCombo.addItem("No seats available");
        }
    }

    // IMPROVED: Book flight with validation and loading state
    private void bookFlight() {
        // Validate promo code if entered
    	String promoCode = promoCodeField.getText().trim();
    	if (!promoCode.isEmpty() && !promoCode.matches("[A-Z0-9]{5,10}")) {
    	    Toast.showError(this, "Please enter a valid promo code");
    	    UIAnimations.shake(promoCodeField, 10, 3);
    	    return;
    	}

        if (selectedFlight == null) {
            Toast.showWarning(this, "Please select a flight");
            return;
        }

        if (selectedFlight.getAvailableSeats() <= 0) {
            Toast.showError(this, "No seats available on this flight");
            return;
        }

        try {
            int customerId = currentUser.getLinkedCustomerId();
            int flightId = selectedFlight.getId();
            
            // IMPROVED: Show loading overlay
            LoadingOverlay loading = new LoadingOverlay("Processing your booking...");
            loading.show(this);
            
            // Use SwingWorker for background processing
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    Thread.sleep(1000);
                    
                    Command addBooking = new AddBooking(customerId, flightId);
                    addBooking.execute(fbs);
                    FlightBookingSystemData.safeStore(fbs);
                    
                    return null;
                }
                
                @Override
                protected void done() {
                    loading.hide(CustomerBookingWindow.this);
                    
                    try {
                        get();
                        
                        // IMPROVED: Toast notification
                        Toast.showSuccess(CustomerBookingWindow.this, 
                            "Booking confirmed! Flight " + selectedFlight.getFlightNumber());
                        
                        // Close window after short delay
                        Timer timer = new Timer(1500, e -> {
                            dispose();
                            if (parentWindow != null) {
                                parentWindow.showDashboard();
                            }
                        });
                        timer.setRepeats(false);
                        timer.start();
                        
                    } catch (Exception ex) {
                        Toast.showError(CustomerBookingWindow.this, 
                            "Booking failed: " + ex.getMessage());
                    }
                }
            };
            worker.execute();
            
        } catch (Exception ex) {
            Toast.showError(this, "Booking error: " + ex.getMessage());
        }
    }
}