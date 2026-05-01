package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.gui.customer.FlightDetailsWindow;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.FlightType;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin flight browsing window with advanced filtering and management options.
 * 
 * @author Tejindra Rai
 * @version 1.0 - Professional admin flight browser
 */
public class AdminBrowseFlightsWindow extends JFrame {

    private FlightBookingSystem fbs;
    private JTable flightsTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JTextField searchField;
    private JCheckBox showDeletedCheck;
    private List<Flight> allFlights;
    private List<Flight> filteredFlights;

    public AdminBrowseFlightsWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        this.allFlights = fbs.getFlights();
        this.filteredFlights = new ArrayList<>();
        // Initially show only active flights
        for (Flight f : allFlights) {
            if (!f.isDeleted()) {
                filteredFlights.add(f);
            }
        }
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Admin Flight Browser");
        setSize(1300, 750);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Filter Panel
        JPanel filterPanel = createFilterPanel();

        // Table Panel
        JPanel tablePanel = createTablePanel();

        // Combine filter and table
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(filterPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY); // Purple for admin
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = FontAwesomeIcon.createIcon(FontAwesomeIcon.PLANE, 50, ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Admin Flight Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Browse, filter, and manage all flights in the system");
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

        // Filter label with icon
        JPanel filterLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        filterLabelPanel.setBackground(ColorScheme.CARD_BG);
        JLabel filterIcon = FontAwesomeIcon.createIcon(FontAwesomeIcon.FILTER, 14, ColorScheme.TEXT_PRIMARY);
        JLabel filterLabel = new JLabel(" Filter:");
        filterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        filterLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        filterLabelPanel.add(filterIcon);
        filterLabelPanel.add(filterLabel);

        // Filter combo box
        String[] filterOptions = {"All Active Flights", "Domestic Only", "International Only", 
                                  "Past Flights", "Future Flights", "Economy Class", 
                                  "Business Class", "First Class"};
        filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(180, 35));
        filterCombo.addActionListener(e -> applyFilters());

        // Search label with icon
        JPanel searchLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        searchLabelPanel.setBackground(ColorScheme.CARD_BG);
        JLabel searchIcon = FontAwesomeIcon.createIcon(FontAwesomeIcon.SEARCH, 14, ColorScheme.TEXT_PRIMARY);
        JLabel searchLabel = new JLabel(" Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        searchLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        searchLabelPanel.add(searchIcon);
        searchLabelPanel.add(searchLabel);

        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(250, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_MEDIUM, 1),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                applyFilters();
            }
        });

        // Show deleted checkbox
        showDeletedCheck = new JCheckBox("Show Deleted Flights");
        showDeletedCheck.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        showDeletedCheck.setForeground(ColorScheme.TEXT_PRIMARY);
        showDeletedCheck.setBackground(ColorScheme.CARD_BG);
        showDeletedCheck.addActionListener(e -> applyFilters());

        // Refresh button
        JButton refreshBtn = FontAwesomeIcon.createIconButton(FontAwesomeIcon.REFRESH, "Refresh", 13);
        refreshBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setBorder(new EmptyBorder(8, 20, 8, 20));
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.addActionListener(e -> refreshFlights());

        filterPanel.add(filterLabelPanel);
        filterPanel.add(filterCombo);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(searchLabelPanel);
        filterPanel.add(searchField);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(showDeletedCheck);
        filterPanel.add(Box.createHorizontalStrut(15));
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

        JPanel infoLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
        infoLabelPanel.setBackground(ColorScheme.BACKGROUND);
        JLabel infoIcon = FontAwesomeIcon.createIcon(FontAwesomeIcon.INFO_CIRCLE, 14, ColorScheme.ADMIN_PRIMARY);
        JLabel infoLabel = new JLabel(" " + filteredFlights.size() + " flights shown");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoLabel.setForeground(ColorScheme.ADMIN_PRIMARY);
        infoLabelPanel.add(infoIcon);
        infoLabelPanel.add(infoLabel);

        JLabel hintLabel = new JLabel("Double-click a row to view flight details | Right-click to view feedback");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(ColorScheme.TEXT_SECONDARY);

        infoPanel.add(infoLabelPanel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(hintLabel);

        // Table
        String[] columns = {"ID", "Flight No.", "Origin", "Destination", "Departure", 
                           "Type", "Class", "Price", "Capacity", "Occupancy", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        flightsTable = new JTable(tableModel);
        flightsTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        flightsTable.setRowHeight(35);
        flightsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        flightsTable.setFillsViewportHeight(true);
        flightsTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        flightsTable.getTableHeader().setBackground(ColorScheme.ADMIN_PRIMARY);
        flightsTable.getTableHeader().setForeground(Color.WHITE);
        flightsTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Add double-click listener to view flight details
        flightsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
                    int selectedRow = flightsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Flight selectedFlight = filteredFlights.get(selectedRow);
                        new FlightDetailsWindow(selectedFlight);
                    }
                } else if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                    int selectedRow = flightsTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Flight selectedFlight = filteredFlights.get(selectedRow);
                        new ViewFeedbackWindow(selectedFlight, fbs);
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
        JButton viewDetailsBtn = createIconButton(FontAwesomeIcon.INFO_CIRCLE, "View Details", ColorScheme.ADMIN_PRIMARY);
        viewDetailsBtn.addActionListener(e -> viewSelectedFlight());

        // View Feedback button
        JButton feedbackBtn = createIconButton(FontAwesomeIcon.STAR, "View Feedback", ColorScheme.GOLD);
        feedbackBtn.addActionListener(e -> viewFeedback());

        // Close button
        JButton closeBtn = createIconButton(FontAwesomeIcon.TIMES_CIRCLE, "Close", ColorScheme.DANGER);
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(viewDetailsBtn);
        buttonPanel.add(feedbackBtn);
        buttonPanel.add(closeBtn);

        return buttonPanel;
    }

    private JButton createIconButton(String iconCode, String text, Color bgColor) {
        JPanel buttonContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        buttonContent.setOpaque(false);
        
        JLabel icon = FontAwesomeIcon.createIcon(iconCode, 14, Color.WHITE);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        
        buttonContent.add(icon);
        buttonContent.add(label);
        
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.add(buttonContent, BorderLayout.CENTER);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            Color original = bgColor;
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ColorScheme.getHoverColor(bgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });

        return btn;
    }

    private JButton createButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setPreferredSize(new Dimension(180, 45));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));

        btn.addMouseListener(new MouseAdapter() {
            Color original = bgColor;
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(ColorScheme.getHoverColor(bgColor));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(original);
            }
        });

        return btn;
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        
        for (Flight flight : filteredFlights) {
            Object[] row = {
                flight.getId(),
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate().toString(),
                flight.getFlightType().getDisplayName(),
                flight.getFlightClass(),
                "£" + String.format("%.2f", flight.getBasePrice()),
                flight.getCapacity(),
                flight.getPassengers().size() + " / " + flight.getCapacity(),
                flight.isDeleted() ? "🗑️ Deleted" : "✅ Active"
            };
            tableModel.addRow(row);
        }
    }

    private void applyFilters() {
        filteredFlights.clear();
        String selectedFilter = (String) filterCombo.getSelectedItem();
        String searchText = searchField.getText().toLowerCase().trim();
        boolean showDeleted = showDeletedCheck.isSelected();

        for (Flight flight : allFlights) {
            boolean matchesFilter = true;
            boolean matchesSearch = true;
            boolean matchesDeletedStatus = showDeleted || !flight.isDeleted();

            // Apply filter
            if (selectedFilter.equals("Domestic Only") && flight.getFlightType() != FlightType.DOMESTIC) {
                matchesFilter = false;
            } else if (selectedFilter.equals("International Only") && flight.getFlightType() != FlightType.INTERNATIONAL) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Past Flights") && !flight.getDepartureDate().isBefore(fbs.getSystemDate())) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Future Flights") && !flight.getDepartureDate().isAfter(fbs.getSystemDate())) {
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
                matchesSearch = String.valueOf(flight.getId()).contains(searchText) ||
                               flight.getFlightNumber().toLowerCase().contains(searchText) ||
                               flight.getOrigin().toLowerCase().contains(searchText) ||
                               flight.getDestination().toLowerCase().contains(searchText);
            }

            if (matchesFilter && matchesSearch && matchesDeletedStatus) {
                filteredFlights.add(flight);
            }
        }

        populateTable();
    }

    private void refreshFlights() {
        allFlights = fbs.getFlights();
        showDeletedCheck.setSelected(false);
        filterCombo.setSelectedIndex(0);
        searchField.setText("");
        applyFilters();
        
        JOptionPane.showMessageDialog(this,
            "Flight list refreshed!\n" + filteredFlights.size() + " flights shown.",
            "Refreshed",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewSelectedFlight() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Flight selectedFlight = filteredFlights.get(selectedRow);
            new FlightDetailsWindow(selectedFlight);
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a flight to view details.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void viewFeedback() {
        int selectedRow = flightsTable.getSelectedRow();
        if (selectedRow >= 0) {
            Flight selectedFlight = filteredFlights.get(selectedRow);
            new ViewFeedbackWindow(selectedFlight, fbs);
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a flight to view feedback.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
}