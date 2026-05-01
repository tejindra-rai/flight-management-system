package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.utils.AirportValidator;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Admin window for viewing and exporting passenger manifests (boarding lists).
 * Used for boarding, emergencies, compliance, and record-keeping.
 * 
 * @author Tejindra Rai
 * @version 1.0 - Admin Enhancement Features
 */
public class PassengerManifestWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField flightIdText = new JTextField();
    private JButton loadBtn = new JButton("Load Manifest");
    private JButton exportCSVBtn;
    private JButton printBtn;
    private JButton closeBtn = new JButton("Close");
    
    private JTable manifestTable;
    private DefaultTableModel tableModel;
    private JLabel flightInfoLabel;
    private JPanel statsPanel;
    private JLabel statsLabel;
    
    private Flight currentFlight;

    public PassengerManifestWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Passenger Manifest");
        setSize(1000, 700);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = FontAwesomeIcon.createIcon(FontAwesomeIcon.LIST, 36, ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Passenger Manifest");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("View and export passenger boarding lists");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Load Panel
        JPanel loadPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        loadPanel.setBackground(ColorScheme.CARD_BG);
        
        JLabel flightIdLabel = new JLabel("Flight ID:");
        flightIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        flightIdLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        
        flightIdText.setPreferredSize(new Dimension(100, 30));
        
        loadBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        loadBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        loadBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loadBtn.setFocusPainted(false);
        loadBtn.addActionListener(this);
        
        loadPanel.add(flightIdLabel);
        loadPanel.add(flightIdText);
        loadPanel.add(loadBtn);
        
        // Flight Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout(0, 10));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        
        flightInfoLabel = new JLabel("<html><i>Load a flight to view passenger manifest</i></html>");
        flightInfoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        flightInfoLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        
        // Create stats panel with icon
        statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        statsPanel.setBackground(Color.WHITE);
        statsLabel = new JLabel("");
        statsLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        statsLabel.setForeground(ColorScheme.ADMIN_PRIMARY);
        
        infoPanel.add(flightInfoLabel, BorderLayout.NORTH);
        infoPanel.add(statsPanel, BorderLayout.CENTER);
        
        // Table
        String[] columnNames = {"#", "Name", "Email", "Phone", "Seat", "Meal", "Age Group", "Booking Date", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        manifestTable = new JTable(tableModel);
        manifestTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        manifestTable.setRowHeight(30);
        manifestTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        manifestTable.getTableHeader().setBackground(ColorScheme.ADMIN_PRIMARY);
        manifestTable.getTableHeader().setForeground(ColorScheme.TEXT_ON_DARK);
        manifestTable.setSelectionBackground(ColorScheme.withOpacity(ColorScheme.ADMIN_PRIMARY, 0.2));
        
        JScrollPane scrollPane = new JScrollPane(manifestTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1));
        
        // Center Panel
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBackground(ColorScheme.CARD_BG);
        centerPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        centerPanel.add(infoPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 20, 20, 20));
        buttonPanel.setBackground(ColorScheme.CARD_BG);
        
        // Create Export CSV button with icon
        exportCSVBtn = createIconButton(FontAwesomeIcon.DOWNLOAD, "Export CSV", ColorScheme.SUCCESS);
        exportCSVBtn.setEnabled(false);
        exportCSVBtn.addActionListener(this);
        
        // Create Print button with icon
        printBtn = createIconButton(FontAwesomeIcon.PRINT, "Print", ColorScheme.ADMIN_PRIMARY);
        printBtn.setEnabled(false);
        printBtn.addActionListener(this);
        
        closeBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        closeBtn.setBackground(ColorScheme.TEXT_SECONDARY);
        closeBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        closeBtn.setFocusPainted(false);
        closeBtn.addActionListener(this);
        
        buttonPanel.add(exportCSVBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.add(loadPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    private JButton createIconButton(String iconCode, String text, Color bgColor) {
        JPanel buttonContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        buttonContent.setOpaque(false);
        
        JLabel icon = FontAwesomeIcon.createIcon(iconCode, 13, Color.WHITE);
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        label.setForeground(Color.WHITE);
        
        buttonContent.add(icon);
        buttonContent.add(label);
        
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.add(buttonContent, BorderLayout.CENTER);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loadBtn) {
            loadManifest();
        } else if (ae.getSource() == exportCSVBtn) {
            exportToCSV();
        } else if (ae.getSource() == printBtn) {
            printManifest();
        } else if (ae.getSource() == closeBtn) {
            this.dispose();
        }
    }

    private void loadManifest() {
        try {
            int flightId = Integer.parseInt(flightIdText.getText().trim());
            currentFlight = mw.getFlightBookingSystem().getFlightByID(flightId);
            
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Get all passengers with bookings
            List<PassengerInfo> passengers = new ArrayList<>();
            for (Customer customer : mw.getFlightBookingSystem().getCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    if (booking.getFlight().getId() == flightId) {
                        passengers.add(new PassengerInfo(customer, booking));
                    }
                }
            }
            
            // Sort by seat number
            passengers.sort((p1, p2) -> {
                String seat1 = p1.booking.getSeatNumber() != null ? p1.booking.getSeatNumber() : "ZZZ";
                String seat2 = p2.booking.getSeatNumber() != null ? p2.booking.getSeatNumber() : "ZZZ";
                return seat1.compareTo(seat2);
            });
            
            // Populate table
            int rowNum = 1;
            int activeBookings = 0;
            for (PassengerInfo info : passengers) {
                String status = info.booking.isCancelled() ? "CANCELLED" : "CONFIRMED";
                if (!info.booking.isCancelled()) {
                    activeBookings++;
                }
                
                tableModel.addRow(new Object[]{
                    rowNum++,
                    info.customer.getName(),
                    info.customer.getEmail(),
                    info.customer.getPhone(),
                    info.booking.getSeatNumber() != null ? info.booking.getSeatNumber() : "N/A",
                    info.booking.getMealPreference(),
                    info.customer.getAgeGroup(),
                    info.booking.getBookingDate(),
                    status
                });
            }
            
            // Update flight info
            String originName = AirportValidator.getAirportName(currentFlight.getOrigin());
            String destName = AirportValidator.getAirportName(currentFlight.getDestination());
            
            flightInfoLabel.setText("<html>" +
                "<b>Flight " + currentFlight.getFlightNumber() + "</b> - " +
                originName + " (" + currentFlight.getOrigin() + ") → " +
                destName + " (" + currentFlight.getDestination() + ")<br>" +
                "Departure: " + currentFlight.getDepartureDate() + " | " +
                "Class: " + currentFlight.getFlightClass() +
                "</html>");
            
            // Update stats with icon
            int available = currentFlight.getAvailableSeats();
            double loadFactor = ((double)activeBookings / currentFlight.getCapacity()) * 100;
            
            statsPanel.removeAll();
            JLabel usersIcon = FontAwesomeIcon.createIcon(FontAwesomeIcon.USERS, 13, ColorScheme.ADMIN_PRIMARY);
            statsLabel.setText(String.format(
                " Total Bookings: %d | Active: %d | Cancelled: %d | Available Seats: %d | Load Factor: %.1f%%",
                passengers.size(), activeBookings, passengers.size() - activeBookings, available, loadFactor
            ));
            statsPanel.add(usersIcon);
            statsPanel.add(statsLabel);
            statsPanel.revalidate();
            statsPanel.repaint();
            
            exportCSVBtn.setEnabled(true);
            printBtn.setEnabled(true);
            
            if (passengers.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No passengers booked on this flight yet.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
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

    private void exportToCSV() {
        if (currentFlight == null) {
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Passenger Manifest");
        
        String filename = String.format("Manifest_%s_%s.csv",
            currentFlight.getFlightNumber(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        );
        fileChooser.setSelectedFile(new File(filename));
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(file)) {
                // Write header
                writer.write("PASSENGER MANIFEST\n");
                writer.write("Flight: " + currentFlight.getFlightNumber() + "\n");
                writer.write("Route: " + currentFlight.getOrigin() + " - " + currentFlight.getDestination() + "\n");
                writer.write("Date: " + currentFlight.getDepartureDate() + "\n");
                writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");
                writer.write("\n");
                
                // Write column headers
                writer.write("#,Name,Email,Phone,Seat,Meal Preference,Age Group,Booking Date,Status\n");
                
                // Write data
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        writer.write(value != null ? value.toString() : "");
                        if (j < tableModel.getColumnCount() - 1) {
                            writer.write(",");
                        }
                    }
                    writer.write("\n");
                }
                
                JOptionPane.showMessageDialog(this,
                    "Manifest exported successfully!\n\nFile: " + file.getAbsolutePath(),
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting manifest: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void printManifest() {
        try {
            boolean complete = manifestTable.print(
                JTable.PrintMode.FIT_WIDTH,
                null,
                null
            );
            
            if (complete) {
                JOptionPane.showMessageDialog(this,
                    "Print job sent successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error printing manifest: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Helper class to hold passenger and booking information together.
     */
    private static class PassengerInfo {
        Customer customer;
        Booking booking;
        
        PassengerInfo(Customer customer, Booking booking) {
            this.customer = customer;
            this.booking = booking;
        }
    }
}