package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.commands.UpdateBooking;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Customer Update Booking Window - Professional Modern UI
 * 
 * @author Binay Chaudhary
 * @version 3.0 - Professional redesign with consistent icons and modern styling
 */
public class CustomerUpdateBookingWindow extends JFrame {

    private FlightBookingSystem fbs;
    private Customer customer;
    private CustomerMainWindow parentWindow;
    
    private JComboBox<String> bookingComboBox;
    private JComboBox<String> newFlightComboBox;
    private JSpinner dateSpinner;
    private JLabel priceLabel;
    private JButton updateBtn;
    
    private List<Booking> customerBookings;
    private List<Flight> availableFlights;
    
    // Modern color palette
    private static final Color FIELD_BG = new Color(248, 249, 250);
    private static final Color FIELD_BORDER = new Color(206, 212, 218);
    private static final Color FIELD_FOCUS = new Color(13, 110, 253);
    private static final Color ICON_COLOR = new Color(108, 117, 125);
    private static final Color LABEL_COLOR = new Color(33, 37, 41);

    public CustomerUpdateBookingWindow(FlightBookingSystem fbs, Customer customer, CustomerMainWindow parentWindow) {
        this.fbs = fbs;
        this.customer = customer;
        this.parentWindow = parentWindow;
        
        initialize();
        loadData();
    }

    private void initialize() {
        setTitle("B & T Airlines - Update Booking");
        
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(680, (int)(screenSize.width * 0.9)); // 90% of screen width, max 680
        int height = Math.min(800, (int)(screenSize.height * 0.85)); // 85% of screen height, max 800
        
        setSize(width, height);
        setMinimumSize(new Dimension(400, 500)); // Minimum usable size
        setLocationRelativeTo(parentWindow);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // Add header (fixed at top)
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        
        // Add scrollable form panel
        JPanel formPanel = createFormPanel();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel (fixed at bottom)
        mainPanel.add(createButtonPanel(), BorderLayout.SOUTH);

        add(mainPanel);
        
        // Add component listener for responsive adjustments
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustForWindowSize();
            }
        });
        
        setVisible(true);
    }
    
    /**
     * Adjust component sizes based on window size for responsiveness
     */
    private void adjustForWindowSize() {
        int windowWidth = getWidth();
        
        // Adjust form panel padding based on window width
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                adjustPanelPadding((JPanel) comp, windowWidth);
            }
        }
    }
    
    private void adjustPanelPadding(JPanel panel, int windowWidth) {
        // For small screens, reduce padding
        if (panel.getBorder() instanceof EmptyBorder) {
            int padding = windowWidth < 500 ? 20 : (windowWidth < 600 ? 30 : 50);
            // Don't change the form panel padding here as it's already set
        }
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 35));
        headerPanel.setBackground(ColorScheme.PRIMARY_DARK);
        
        // Responsive header height
        int headerHeight = 110;
        headerPanel.setPreferredSize(new Dimension(getWidth(), headerHeight));
        headerPanel.setMinimumSize(new Dimension(400, 80));
        
        JLabel iconLabel = new JLabel(FontAwesomeIcon.EDIT);
        iconLabel.setFont(FontAwesomeIcon.getFont(44));
        iconLabel.setForeground(ColorScheme.GOLD);
        
        JLabel titleLabel = new JLabel("Update Booking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 34));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);
        
        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        
        // Responsive padding - less on smaller screens
        int horizontalPadding = 50;
        int verticalPadding = 40;
        formPanel.setBorder(new EmptyBorder(verticalPadding, horizontalPadding, verticalPadding, horizontalPadding));

        // Info banner
        formPanel.add(createInfoBanner());
        formPanel.add(Box.createVerticalStrut(35));

        // Booking selection field
        formPanel.add(createInputField(
            FontAwesomeIcon.TICKET,
            "Select Booking to Update",
            bookingComboBox = createStyledComboBox()
        ));
        bookingComboBox.addActionListener(e -> onBookingSelected());
        formPanel.add(Box.createVerticalStrut(28));

        // Flight selection field
        formPanel.add(createInputField(
            FontAwesomeIcon.PLANE,
            "Select New Flight",
            newFlightComboBox = createStyledComboBox()
        ));
        newFlightComboBox.addActionListener(e -> updatePrice());
        formPanel.add(Box.createVerticalStrut(28));

        // Date selection field
        dateSpinner = createStyledDateSpinner();
        formPanel.add(createInputField(
            FontAwesomeIcon.CALENDAR,
            "Select New Departure Date",
            dateSpinner
        ));
        formPanel.add(Box.createVerticalStrut(35));

        // Price display
        formPanel.add(createPricePanel());
        formPanel.add(Box.createVerticalStrut(20)); // Add bottom space for scrolling

        return formPanel;
    }

    private JPanel createInfoBanner() {
        JPanel banner = new JPanel(new BorderLayout(15, 0));
        banner.setBackground(new Color(207, 226, 255));
        banner.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(66, 153, 225), 2, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        
        JLabel icon = new JLabel(FontAwesomeIcon.INFO_CIRCLE);
        icon.setFont(FontAwesomeIcon.getFont(32));
        icon.setForeground(new Color(49, 130, 206));
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel title = new JLabel("Update Your Booking");
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setForeground(new Color(26, 32, 44));
        
        JLabel subtitle = new JLabel("Select a booking and choose a new flight and departure date.");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitle.setForeground(new Color(45, 55, 72));
        
        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);
        
        banner.add(icon, BorderLayout.WEST);
        banner.add(textPanel, BorderLayout.CENTER);
        
        return banner;
    }

    private JPanel createInputField(String iconCode, String labelText, JComponent inputComponent) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Label with icon
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        labelPanel.setBackground(Color.WHITE);
        labelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel icon = new JLabel(iconCode);
        icon.setFont(FontAwesomeIcon.getFont(16));
        icon.setForeground(ICON_COLOR);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 15));
        label.setForeground(LABEL_COLOR);
        
        labelPanel.add(icon);
        labelPanel.add(label);
        
        fieldPanel.add(labelPanel);
        fieldPanel.add(Box.createVerticalStrut(10));
        fieldPanel.add(inputComponent);
        
        return fieldPanel;
    }

    private JComboBox<String> createStyledComboBox() {
        JComboBox<String> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("Arial", Font.PLAIN, 15));
        
        // Flexible sizing
        comboBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        comboBox.setPreferredSize(new Dimension(200, 48)); // Minimum width
        comboBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        comboBox.setBackground(FIELD_BG);
        
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FIELD_BORDER, 2, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // Hover and focus effects
        comboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean hasFocus = false;
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (comboBox.isEnabled() && !hasFocus) {
                    comboBox.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_FOCUS, 2, true),
                        new EmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!hasFocus) {
                    comboBox.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_BORDER, 2, true),
                        new EmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
        });
        
        comboBox.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(FIELD_FOCUS, 2, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
            
            public void focusLost(java.awt.event.FocusEvent evt) {
                comboBox.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(FIELD_BORDER, 2, true),
                    new EmptyBorder(10, 15, 10, 15)
                ));
            }
        });
        
        return comboBox;
    }

    private JSpinner createStyledDateSpinner() {
        SpinnerDateModel dateModel = new SpinnerDateModel(
            new Date(),
            new Date(),
            null,
            java.util.Calendar.DAY_OF_MONTH
        );
        JSpinner spinner = new JSpinner(dateModel);
        spinner.setFont(new Font("Arial", Font.PLAIN, 15));
        
        // Flexible sizing
        spinner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        spinner.setPreferredSize(new Dimension(200, 48)); // Minimum width
        spinner.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
        spinner.setEditor(dateEditor);
        
        spinner.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(FIELD_BORDER, 2, true),
            new EmptyBorder(10, 15, 10, 15)
        ));
        
        // Style the internal text field
        JComponent editor = spinner.getEditor();
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().setFont(new Font("Arial", Font.PLAIN, 15));
            spinnerEditor.getTextField().setBackground(FIELD_BG);
            spinnerEditor.getTextField().setBorder(null);
        }
        
        // Hover and focus effects
        spinner.addMouseListener(new java.awt.event.MouseAdapter() {
            private boolean hasFocus = false;
            
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (spinner.isEnabled() && !hasFocus) {
                    spinner.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_FOCUS, 2, true),
                        new EmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!hasFocus) {
                    spinner.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_BORDER, 2, true),
                        new EmptyBorder(10, 15, 10, 15)
                    ));
                }
            }
        });
        
        // Add focus listener to editor text field
        if (editor instanceof JSpinner.DefaultEditor) {
            JSpinner.DefaultEditor spinnerEditor = (JSpinner.DefaultEditor) editor;
            spinnerEditor.getTextField().addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    spinner.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_FOCUS, 2, true),
                        new EmptyBorder(10, 15, 10, 15)
                    ));
                }
                
                public void focusLost(java.awt.event.FocusEvent evt) {
                    spinner.setBorder(BorderFactory.createCompoundBorder(
                        new LineBorder(FIELD_BORDER, 2, true),
                        new EmptyBorder(10, 15, 10, 15)
                    ));
                }
            });
        }
        
        return spinner;
    }

    private JPanel createPricePanel() {
        JPanel pricePanel = new JPanel(new BorderLayout(15, 0));
        pricePanel.setBackground(new Color(255, 251, 235));
        pricePanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(237, 201, 81), 2, true),
            new EmptyBorder(22, 22, 22, 22)
        ));
        pricePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel icon = new JLabel(FontAwesomeIcon.STERLING_SIGN);
        icon.setFont(FontAwesomeIcon.getFont(32));
        icon.setForeground(new Color(217, 119, 6));
        
        priceLabel = new JLabel("New Booking Price: £0.00");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 22));
        priceLabel.setForeground(new Color(26, 32, 44));
        
        pricePanel.add(icon, BorderLayout.WEST);
        pricePanel.add(priceLabel, BorderLayout.CENTER);
        
        return pricePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        
        // Responsive layout - will stack vertically if needed
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        // Responsive button size
        Dimension buttonSize = new Dimension(200, 50);
        
        // Update Button
        updateBtn = createModernButton(
            FontAwesomeIcon.CHECK,
            "Update Booking",
            new Color(34, 197, 94),
            new Color(22, 163, 74),
            buttonSize
        );
        updateBtn.addActionListener(e -> updateBooking());
        
        // Cancel Button
        JButton cancelBtn = createModernButton(
            FontAwesomeIcon.TIMES_CIRCLE,
            "Cancel",
            new Color(239, 68, 68),
            new Color(220, 38, 38),
            buttonSize
        );
        cancelBtn.addActionListener(e -> dispose());
        
        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);
        
        return buttonPanel;
    }

    private JButton createModernButton(String iconCode, String text, Color bgColor, Color hoverColor, Dimension size) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(size);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        contentPanel.setOpaque(false);
        
        JLabel icon = new JLabel(iconCode);
        icon.setFont(FontAwesomeIcon.getFont(20));
        icon.setForeground(Color.WHITE);
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 17));
        label.setForeground(Color.WHITE);
        
        contentPanel.add(icon);
        contentPanel.add(label);
        button.add(contentPanel, BorderLayout.CENTER);
        
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (button.isEnabled()) {
                    button.setBackground(hoverColor);
                }
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }

    private void loadData() {
        customerBookings = new ArrayList<>();
        for (Booking booking : customer.getBookings()) {
            if (!booking.isCancelled()) {
                customerBookings.add(booking);
            }
        }
        
        bookingComboBox.removeAllItems();
        if (customerBookings.isEmpty()) {
            bookingComboBox.addItem("No active bookings available");
            newFlightComboBox.setEnabled(false);
            dateSpinner.setEnabled(false);
            updateBtn.setEnabled(false);
        } else {
            for (Booking booking : customerBookings) {
                Flight flight = booking.getFlight();
                String bookingText = String.format("Flight %s: %s → %s (Date: %s, Price: £%.2f)",
                    flight.getFlightNumber(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureDate().toString(),
                    booking.getBookingPrice()
                );
                bookingComboBox.addItem(bookingText);
            }
            updateBtn.setEnabled(true);
        }
        
        availableFlights = new ArrayList<>();
        for (Flight flight : fbs.getFlights()) {
            if (!flight.isDeleted() && flight.getDepartureDate().isAfter(fbs.getSystemDate())) {
                availableFlights.add(flight);
            }
        }
        
        newFlightComboBox.removeAllItems();
        if (availableFlights.isEmpty()) {
            newFlightComboBox.addItem("No flights available");
            newFlightComboBox.setEnabled(false);
            updateBtn.setEnabled(false);
        } else {
            for (Flight flight : availableFlights) {
                double price = flight.calculatePrice(fbs.getSystemDate());
                String flightText = String.format("%s: %s → %s (£%.2f)",
                    flight.getFlightNumber(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    price
                );
                newFlightComboBox.addItem(flightText);
            }
        }
    }

    private void onBookingSelected() {
        if (bookingComboBox.getSelectedIndex() >= 0 && !customerBookings.isEmpty()) {
            Booking selectedBooking = customerBookings.get(bookingComboBox.getSelectedIndex());
            Flight currentFlight = selectedBooking.getFlight();
            
            Date date = Date.from(currentFlight.getDepartureDate().atStartOfDay(ZoneId.systemDefault()).toInstant());
            dateSpinner.setValue(date);
            
            updatePrice();
        }
    }

    private void updatePrice() {
        if (newFlightComboBox.getSelectedIndex() >= 0 && !availableFlights.isEmpty()) {
            Flight selectedFlight = availableFlights.get(newFlightComboBox.getSelectedIndex());
            double price = selectedFlight.calculatePrice(fbs.getSystemDate());
            priceLabel.setText(String.format("New Booking Price: £%.2f", price));
        }
    }

    private void updateBooking() {
        try {
            if (customerBookings.isEmpty()) {
                showDialog("No Bookings", "You don't have any active bookings to update.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (bookingComboBox.getSelectedIndex() < 0) {
                showDialog("Selection Required", "Please select a booking to update.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (newFlightComboBox.getSelectedIndex() < 0) {
                showDialog("Selection Required", "Please select a new flight.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Date selectedDate = (Date) dateSpinner.getValue();
            if (selectedDate == null) {
                showDialog("Date Required", "Please select a departure date.", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            Booking oldBooking = customerBookings.get(bookingComboBox.getSelectedIndex());
            Flight oldFlight = oldBooking.getFlight();
            Flight newFlight = availableFlights.get(newFlightComboBox.getSelectedIndex());
            
            LocalDate newDate = selectedDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            
            if (newDate.isBefore(fbs.getSystemDate())) {
                showDialog("Invalid Date", "Cannot select a date in the past.", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double newFlightPrice = newFlight.calculatePrice(fbs.getSystemDate());
            
            String message = String.format(
                "<html><div style='padding: 15px; font-family: Arial;'>" +
                "<div style='font-size: 14px; margin-bottom: 15px;'>" +
                "<b style='font-size: 15px;'>Current Booking:</b><br/>" +
                "Flight <b>%s</b> (%s → %s)<br/>" +
                "</div>" +
                "<div style='font-size: 14px;'>" +
                "<b style='font-size: 15px;'>New Booking:</b><br/>" +
                "Flight <b>%s</b> (%s → %s)<br/>" +
                "Date: <b>%s</b><br/>" +
                "Price: <b style='color: #15803d;'>£%.2f</b><br/>" +
                "</div>" +
                "<div style='margin-top: 15px; font-size: 14px;'>" +
                "Do you want to confirm this update?" +
                "</div></div></html>",
                oldFlight.getFlightNumber(), oldFlight.getOrigin(), oldFlight.getDestination(),
                newFlight.getFlightNumber(), newFlight.getOrigin(), newFlight.getDestination(),
                newDate.toString(), newFlightPrice
            );
            
            int confirm = JOptionPane.showConfirmDialog(
                this,
                message,
                "Confirm Update",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                UpdateBooking updateCmd = new UpdateBooking(
                    customer.getId(),
                    oldFlight.getId(),
                    newFlight.getId(),
                    newDate
                );
                updateCmd.execute(fbs);
                
                showDialog("Success", "Booking updated successfully!", JOptionPane.INFORMATION_MESSAGE);
                
                dispose();
                if (parentWindow != null) {
                    parentWindow.showDashboard();
                }
            }
            
        } catch (FlightBookingSystemException ex) {
            showDialog("Error", "Error updating booking: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            showDialog("Error", "Unexpected error: " + ex.getMessage(), JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private void showDialog(String title, String message, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
}