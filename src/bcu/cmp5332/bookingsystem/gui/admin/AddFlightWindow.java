package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.utils.AirportValidator;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Modern admin window for adding flights with maroon theme.
 * 
 * @author Tejindra Rai
 * @version 5.0 - Admin maroon theme with FontAwesome
 */
public class AddFlightWindow extends JFrame implements ActionListener {
    
    private MainWindow mw;
    private JTextField flightNoText = new JTextField();
    private JTextField originText = new JTextField();
    private JTextField destinationText = new JTextField();
    private JTextField depDateText = new JTextField();
    private JTextField capacityText = new JTextField("100");
    private JTextField basePriceText = new JTextField("100.00");
    private JComboBox<String> flightClassCombo = new JComboBox<>(new String[]{"Economy", "Business", "First Class"});
    private JComboBox<String> flightTypeCombo = new JComboBox<>(new String[]{"Domestic", "International"});
    private JCheckBox isReturnFlightCheck = new JCheckBox();

    private JButton addBtn = new JButton("Add Flight");
    private JButton cancelBtn = new JButton("Cancel");

    public AddFlightWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Add Flight");
        setSize(550, 600);
        setLayout(new BorderLayout(0, 0));

        // Header Panel with maroon theme
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.PLANE);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Add New Flight");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Create a new flight route");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(10, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(ColorScheme.CARD_BG);
        
        // Flight Number
        formPanel.add(createLabel("Flight Number:"));
        formPanel.add(flightNoText);
        
        // Origin
        formPanel.add(createLabel("Origin Code:"));
        formPanel.add(originText);
        
        // Destination
        formPanel.add(createLabel("Destination Code:"));
        formPanel.add(destinationText);
        
        // Flight Type
        formPanel.add(createLabel("Flight Type:"));
        formPanel.add(flightTypeCombo);
        
        // Departure Date
        formPanel.add(createLabel("Departure Date:"));
        JPanel datePanel = new JPanel(new BorderLayout(5, 0));
        datePanel.setOpaque(false);
        datePanel.add(depDateText, BorderLayout.CENTER);
        JLabel dateHint = new JLabel("YYYY-MM-DD");
        dateHint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        dateHint.setForeground(ColorScheme.TEXT_SECONDARY);
        datePanel.add(dateHint, BorderLayout.EAST);
        formPanel.add(datePanel);
        
        // Capacity
        formPanel.add(createLabel("Capacity:"));
        formPanel.add(capacityText);
        
        // Base Price
        formPanel.add(createLabel("Base Price (£):"));
        formPanel.add(basePriceText);
        
        // Flight Class
        formPanel.add(createLabel("Flight Class:"));
        formPanel.add(flightClassCombo);
        
        // Return Flight
        formPanel.add(createLabel("Return Flight:"));
        formPanel.add(isReturnFlightCheck);
        
        // Info
        JLabel infoLabel = new JLabel("Examples: KTM, LHR, DXB");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        formPanel.add(infoLabel);
        formPanel.add(new JLabel(""));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 80, 20, 80));
        buttonPanel.setBackground(ColorScheme.CARD_BG);
        
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        addBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.addActionListener(this);
        
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(ColorScheme.TEXT_SECONDARY);
        cancelBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        cancelBtn.addActionListener(this);
        
        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
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
        if (ae.getSource() == addBtn) {
            addFlight();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void addFlight() {
        try {
            String flightNumber = flightNoText.getText().trim();
            String origin = originText.getText().trim().toUpperCase();
            String destination = destinationText.getText().trim().toUpperCase();
            
            if (flightNumber.isEmpty() || origin.isEmpty() || destination.isEmpty()) {
                throw new FlightBookingSystemException("Flight number, origin, and destination are required.");
            }
            
            if (!AirportValidator.isValidAirport(origin)) {
                throw new FlightBookingSystemException(
                    "Invalid origin airport code: '" + origin + "'.\n\n" +
                    "Please use valid 3-letter IATA codes.\n" +
                    "Examples: KTM (Kathmandu), LHR (London), DXB (Dubai)"
                );
            }
            
            if (!AirportValidator.isValidAirport(destination)) {
                throw new FlightBookingSystemException(
                    "Invalid destination airport code: '" + destination + "'.\n\n" +
                    "Please use valid 3-letter IATA codes.\n" +
                    "Examples: KTM (Kathmandu), LHR (London), DXB (Dubai)"
                );
            }
            
            if (origin.equals(destination)) {
                throw new FlightBookingSystemException("Origin and destination must be different!");
            }
            
            LocalDate departureDate = null;
            try {
                departureDate = LocalDate.parse(depDateText.getText().trim());
            } catch (DateTimeParseException dtpe) {
                throw new FlightBookingSystemException("Date must be in YYYY-MM-DD format\nExample: 2024-12-25");
            }
            
            if (departureDate.isBefore(mw.getFlightBookingSystem().getSystemDate())) {
                throw new FlightBookingSystemException("Departure date must be in the future.");
            }
            
            int capacity = Integer.parseInt(capacityText.getText().trim());
            double basePrice = Double.parseDouble(basePriceText.getText().trim());
            
            if (capacity <= 0) {
                throw new FlightBookingSystemException("Capacity must be greater than 0.");
            }
            if (basePrice <= 0) {
                throw new FlightBookingSystemException("Base price must be greater than 0.");
            }
            
            AddFlight addFlightCommand = new AddFlight(flightNumber, origin, destination, departureDate);
            addFlightCommand.execute(mw.getFlightBookingSystem());
            
            Flight flight = addFlightCommand.getCreatedFlight();
            
            if (flight == null) {
                throw new FlightBookingSystemException("Failed to retrieve created flight.");
            }
            
            flight.setCapacity(capacity);
            flight.setBasePrice(basePrice);
            flight.setFlightClass((String) flightClassCombo.getSelectedItem());
            flight.setReturnFlight(isReturnFlightCheck.isSelected());
            
            String selectedType = (String) flightTypeCombo.getSelectedItem();
            bcu.cmp5332.bookingsystem.model.FlightType flightType = 
                selectedType.equals("Domestic") ? 
                bcu.cmp5332.bookingsystem.model.FlightType.DOMESTIC : 
                bcu.cmp5332.bookingsystem.model.FlightType.INTERNATIONAL;
            flight.setFlightType(flightType);
            
            FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
            mw.displayFlights();
            
            String originName = AirportValidator.getAirportName(origin);
            String destName = AirportValidator.getAirportName(destination);
            
            JOptionPane.showMessageDialog(this, 
                "Flight Added Successfully!\n\n" +
                "Flight ID: " + flight.getId() + "\n" +
                "Flight Number: " + flightNumber + "\n" +
                "Route: " + originName + " (" + origin + ")\n" +
                "       → " + destName + " (" + destination + ")\n" +
                "Date: " + departureDate + "\n" +
                "Price: £" + String.format("%.2f", basePrice) + "\n" +
                "Capacity: " + capacity + " seats", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid number format!\n\nCapacity and price must be valid numbers.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}