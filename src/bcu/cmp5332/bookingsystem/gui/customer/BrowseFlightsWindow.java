package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.gui.components.ModernTable;
import bcu.cmp5332.bookingsystem.gui.components.ModernTextField;
import bcu.cmp5332.bookingsystem.gui.components.Toast;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.UIAnimations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * FIXED Modern Browse Flights window
 * - Fixed icon rendering on macOS
 * - Fixed search field interaction
 * 
 * @author Tejindra Rai
 * @version 2.2 - Fixed search field clickability and interaction
 */
public class BrowseFlightsWindow extends JFrame {

    private FlightBookingSystem fbs;
    private User currentUser;
    private ModernTable flightsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JTextField searchField;  // CHANGED: Using plain JTextField instead of ModernTextField
    private List<Flight> allFlights;
    private List<Flight> filteredFlights;
    private JLabel resultCountLabel;

    public BrowseFlightsWindow(FlightBookingSystem fbs, User currentUser) {
        this.fbs = fbs;
        this.currentUser = currentUser;
        this.allFlights = fbs.getFutureFlights();
        this.filteredFlights = new ArrayList<>(allFlights);
        
        // Enable font anti-aliasing for better rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");
        
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Browse Flights");
        setSize(1200, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Combined container for filter and table
        JPanel centerContainer = new JPanel(new BorderLayout(0, 0));
        centerContainer.setBackground(ColorScheme.BACKGROUND);
        
        // Filter Panel
        JPanel filterPanel = createFilterPanel();
        centerContainer.add(filterPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        centerContainer.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerContainer, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // ANIMATION: Fade in the window
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ColorScheme.PRIMARY_LIGHT);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        // FIXED: Create icon label with proper font rendering
        JLabel iconLabel = createIconLabel(FontAwesomeIcon.PLANE, 50, ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Browse Available Flights");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Explore our destinations and find your perfect flight");
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

    private JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        filterPanel.setBackground(ColorScheme.CARD_BG);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BORDER_LIGHT),
            new EmptyBorder(15, 20, 15, 20)
        ));

        // Filter label
        JLabel filterLabel = createTextWithIcon(FontAwesomeIcon.FILTER, "Filter:", 14);
        filterLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        // Filter combo box
        String[] filterOptions = {"All Flights", "Domestic Only", "International Only", 
                                   "Economy Class", "Business Class", "First Class"};
        filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(180, 35));
        filterCombo.addActionListener(e -> applyFilters());

        // Search label
        JLabel searchLabel = createTextWithIcon(FontAwesomeIcon.SEARCH, "Search:", 14);
        searchLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        // FIXED: Using simple JTextField instead of ModernTextField for better compatibility
        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.setToolTipText("Search by flight number, origin, or destination");
        
        // FIXED: Real-time search with KeyListener
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent evt) {
                applyFilters();
            }
        });

        // Refresh button
        JButton refreshBtn = createStyledButtonWithIcon(
            FontAwesomeIcon.REFRESH,
            "Refresh", 
            ColorScheme.PRIMARY_MEDIUM
        );
        refreshBtn.addActionListener(e -> refreshFlights());

        filterPanel.add(filterLabel);
        filterPanel.add(filterCombo);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(refreshBtn);

        return filterPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(ColorScheme.BACKGROUND);
        tablePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Info panel
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        infoPanel.setBackground(ColorScheme.BACKGROUND);

        // Result count label
        resultCountLabel = createTextWithIcon(
            FontAwesomeIcon.INFO_CIRCLE, 
            allFlights.size() + " flights available", 
            14
        );
        resultCountLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);

        JLabel hintLabel = new JLabel("Double-click a row to view flight details");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(ColorScheme.TEXT_SECONDARY);

        infoPanel.add(resultCountLabel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(hintLabel);

        // ModernTable with enhanced styling
        String[] columns = {"Flight No", "Origin", "Destination", "Date", "Type", 
                           "Class", "Price", "Available Seats"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightsTable = new ModernTable(tableModel);
        flightsTable.setPriceColumn(6);

        // Enhanced double-click with animation
        flightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = flightsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Flight selectedFlight = filteredFlights.get(selectedRow);
                        
                        UIAnimations.pulse(flightsTable, ColorScheme.PRIMARY_LIGHT, 1);
                        
                        Timer timer = new Timer(200, evt -> {
                            new FlightDetailsWindow(selectedFlight);
                        });
                        timer.setRepeats(false);
                        timer.start();
                    }
                }
            }
        });

        populateTable();

        JScrollPane scrollPane = new JScrollPane(flightsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1));

        tablePanel.add(infoPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(15, 20, 20, 20));

        // View Details button
        JButton viewDetailsBtn = createStyledButtonWithIcon(
            FontAwesomeIcon.INFO_CIRCLE,
            "View Details", 
            ColorScheme.PRIMARY_MEDIUM
        );
        viewDetailsBtn.addActionListener(e -> viewSelectedFlight());

        // Book Flight button
        JButton bookBtn = createStyledButtonWithIcon(
            FontAwesomeIcon.TICKET,
            "Book Flight", 
            ColorScheme.SUCCESS
        );
        bookBtn.addActionListener(e -> bookFlight());

        // Close button
        JButton closeBtn = createStyledButtonWithIcon(
            FontAwesomeIcon.TIMES_CIRCLE,
            "Close", 
            ColorScheme.DANGER
        );
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(bookBtn);
        buttonPanel.add(closeBtn);

        return buttonPanel;
    }

    /**
     * Helper method to create icon labels with proper rendering
     */
    private JLabel createIconLabel(String iconCode, float size, Color color) {
        JLabel label = new JLabel(iconCode);
        label.setFont(FontAwesomeIcon.getFont(size));
        label.setForeground(color);
        label.putClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING, 
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return label;
    }

    /**
     * Helper method to create text with icon
     */
    private JLabel createTextWithIcon(String iconCode, String text, float fontSize) {
        JLabel label = new JLabel(iconCode + " " + text);
        label.setFont(FontAwesomeIcon.getFont(fontSize));
        label.putClientProperty(RenderingHints.KEY_TEXT_ANTIALIASING, 
                               RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return label;
    }

    /**
     * Create styled button with icon
     */
    private JButton createStyledButtonWithIcon(String iconCode, String text, Color bgColor) {
        JButton btn = new JButton(iconCode + "  " + text);
        btn.setFont(FontAwesomeIcon.getFont(14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

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

    private void populateTable() {
        tableModel.setRowCount(0);
        
        for (Flight flight : filteredFlights) {
            Object[] row = {
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate().toString(),
                flight.getFlightType().getDisplayName(),  // This will now show correct type
                flight.getFlightClass(),
                "£" + String.format("%.2f", flight.calculatePrice(fbs.getSystemDate())),
                flight.getAvailableSeats() + " / " + flight.getCapacity()
            };
            tableModel.addRow(row);
        }
        
        updateResultCount();
    }

    private void applyFilters() {
        filteredFlights.clear();
        String selectedFilter = (String) filterCombo.getSelectedItem();
        String searchText = searchField.getText().toLowerCase().trim();

        for (Flight flight : allFlights) {
            boolean matchesFilter = true;
            boolean matchesSearch = true;

            // Apply filter
            if (selectedFilter.equals("Domestic Only") && flight.getFlightType() != FlightType.DOMESTIC) {
                matchesFilter = false;
            } else if (selectedFilter.equals("International Only") && flight.getFlightType() != FlightType.INTERNATIONAL) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Economy Class") && !flight.getFlightClass().equalsIgnoreCase("Economy")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Business Class") && !flight.getFlightClass().equalsIgnoreCase("Business")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("First Class") && !flight.getFlightClass().equalsIgnoreCase("First Class")) {
                matchesFilter = false;
            }

            // Apply search
            if (!searchText.isEmpty()) {
                matchesSearch = flight.getFlightNumber().toLowerCase().contains(searchText) ||
                               flight.getOrigin().toLowerCase().contains(searchText) ||
                               flight.getDestination().toLowerCase().contains(searchText);
            }

            if (matchesFilter && matchesSearch) {
                filteredFlights.add(flight);
            }
        }

        populateTable();
        
        if (filteredFlights.isEmpty() && !searchText.isEmpty()) {
            Toast.showWarning(this, "No flights found matching your search");
        }
    }

    private void updateResultCount() {
        int count = filteredFlights.size();
        String newText = FontAwesomeIcon.INFO_CIRCLE + " " + count + 
                        (count == 1 ? " flight" : " flights") + " found";
        resultCountLabel.setText(newText);
    }

    private void refreshFlights() {
        allFlights = fbs.getFutureFlights();
        filteredFlights = new ArrayList<>(allFlights);
        filterCombo.setSelectedIndex(0);
        searchField.setText("");
        populateTable();
        
        Toast.showSuccess(this, "Flight list refreshed! " + allFlights.size() + " flights available.");
    }

    private void viewSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Flight selectedFlight = filteredFlights.get(selectedRow);
            new FlightDetailsWindow(selectedFlight);
        } else {
            Toast.showWarning(this, "Please select a flight to view details");
        }
    }

    private void bookFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Flight selectedFlight = filteredFlights.get(selectedRow);
            Toast.showInfo(this, "Opening booking for " + selectedFlight.getFlightNumber());
            
            Timer timer = new Timer(500, e -> {
                this.dispose();
                new CustomerBookingWindow(fbs, currentUser, null);
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            Toast.showInfo(this, "Opening booking window...");
            
            Timer timer = new Timer(500, e -> {
                this.dispose();
                new CustomerBookingWindow(fbs, currentUser, null);
            });
            timer.setRepeats(false);
            timer.start();
        }
    }
}