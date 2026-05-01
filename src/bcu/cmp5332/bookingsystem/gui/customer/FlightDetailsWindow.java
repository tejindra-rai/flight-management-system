package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.gui.components.ModernTable;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.UIAnimations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * IMPROVED Modern popup window displaying detailed information about a flight.
 * NOW USES NAVY BLUE THEME (PRIMARY_DARK)
 * 
 * @author Binay Chaudhary
 * @version 5.1 - Updated to navy blue theme
 */
public class FlightDetailsWindow extends JFrame {

    private Flight flight;

    public FlightDetailsWindow(Flight flight) {
        this.flight = flight;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Flight Details");
        setSize(750, 650);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

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

        add(mainPanel);
        
        // ANIMATION: Fade in
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        // CHANGED: Use PRIMARY_DARK (navy blue) instead of PRIMARY_LIGHT
        headerPanel.setBackground(ColorScheme.PRIMARY_DARK);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.PLANE, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(50));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel flightLabel = new JLabel("Flight " + flight.getFlightNumber());
        flightLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        flightLabel.setForeground(Color.WHITE);
        flightLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel routeLabel = new JLabel(flight.getOrigin() + " → " + flight.getDestination());
        routeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        routeLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.9));
        routeLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(15));
        headerPanel.add(flightLabel);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(routeLabel);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Flight Information
        contentPanel.add(createInfoPanel());
        contentPanel.add(Box.createVerticalStrut(25));

        // Passengers
        contentPanel.add(createPassengersPanel());

        return contentPanel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 20, 18));
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));

        addInfoRow(panel, FontAwesomeIcon.PLANE, "Flight Number", flight.getFlightNumber());
        addInfoRow(panel, FontAwesomeIcon.MAP_MARKER, "Departure", flight.getOrigin());
        addInfoRow(panel, FontAwesomeIcon.MAP_MARKER, "Destination", flight.getDestination());
        addInfoRow(panel, FontAwesomeIcon.CALENDAR, "Departure Date", flight.getDepartureDate().toString());
        addInfoRow(panel, FontAwesomeIcon.TICKET, "Flight Class", flight.getFlightClass());
        addInfoRow(panel, FontAwesomeIcon.STAR, "Base Price", "£" + String.format("%.2f", flight.getBasePrice()));
        addInfoRow(panel, FontAwesomeIcon.USER, "Capacity", flight.getCapacity() + " seats");
        addInfoRow(panel, FontAwesomeIcon.CHECK, "Available", flight.getAvailableSeats() + " seats");
        addInfoRow(panel, FontAwesomeIcon.REFRESH, "Type", flight.isReturnFlight() ? "Return Flight" : "One-Way");
        addInfoRow(panel, FontAwesomeIcon.GLOBE, "Flight Type", flight.getFlightType().getDisplayName());

        return panel;
    }

    private void addInfoRow(JPanel panel, String icon, String label, String value) {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        labelPanel.setBackground(ColorScheme.CARD_BG);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(16));
        // CHANGED: Use PRIMARY_DARK (navy blue) for icons
        iconLabel.setForeground(ColorScheme.PRIMARY_DARK);

        JLabel textLabel = new JLabel(label);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        textLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        labelPanel.add(iconLabel);
        labelPanel.add(textLabel);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(ColorScheme.TEXT_SECONDARY);

        panel.add(labelPanel);
        panel.add(valueLabel);
    }

    private JPanel createPassengersPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(ColorScheme.CARD_BG);

        JLabel iconLabel = new JLabel(FontAwesomeIcon.USER);
        iconLabel.setFont(FontAwesomeIcon.getFont(18));
        iconLabel.setForeground(ColorScheme.SUCCESS);

        JLabel titleLabel = new JLabel("Passengers (" + flight.getPassengers().size() + " / " + flight.getCapacity() + ")");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // IMPROVED: Use ModernTable instead of JTable
        String[] columns = {"ID", "Name", "Phone Number", "Email"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Customer passenger : flight.getPassengers()) {
            Object[] row = {
                passenger.getId(),
                passenger.getName(),
                passenger.getPhone(),
                passenger.getEmail()
            };
            tableModel.addRow(row);
        }

        ModernTable table = new ModernTable(tableModel);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT));

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}