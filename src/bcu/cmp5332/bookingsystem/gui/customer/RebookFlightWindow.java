package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.commands.RebookFlightCommand;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.gui.components.LoadingOverlay;
import bcu.cmp5332.bookingsystem.gui.components.ModernTable;
import bcu.cmp5332.bookingsystem.gui.components.Toast;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.UIAnimations;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * IMPROVED Modern GUI window for rebooking cancelled flights.
 * 
 * @author Binay Chaudhary
 * @version 3.0 - Enhanced with ModernTable, Toast, and LoadingOverlay
 */
public class RebookFlightWindow extends JFrame implements ActionListener {

    private final MainWindow mainWindow;
    private final FlightBookingSystem fbs;
    
    private JComboBox<String> customerComboBox;
    private ModernTable cancelledBookingsTable;  // CHANGED: ModernTable
    private DefaultTableModel tableModel;
    private JRadioButton rebookSameFlightRadio;
    private JRadioButton rebookDifferentFlightRadio;
    private JComboBox<String> newFlightComboBox;
    private JButton rebookButton;
    private JButton cancelButton;
    
    private List<Customer> customers;
    private List<Booking> cancelledBookings;
    private List<Flight> availableFlights;

    public RebookFlightWindow(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        this.fbs = mainWindow.getFlightBookingSystem();
        initialize();
    }

    public RebookFlightWindow(FlightBookingSystem fbs) {
        this.mainWindow = null;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Rebook Flight");
        setSize(1000, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        JPanel contentPanel = new JPanel(new BorderLayout(20, 20));
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        contentPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Customer selection
        JPanel topPanel = createCustomerSelectionPanel();
        contentPanel.add(topPanel, BorderLayout.NORTH);

        // Table
        JPanel centerPanel = createCancelledBookingsPanel();
        contentPanel.add(centerPanel, BorderLayout.CENTER);

        // Options
        JPanel bottomPanel = createRebookingOptionsPanel();
        contentPanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);
        
        loadCustomers();
        
        // ANIMATION: Fade in
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(ColorScheme.PRIMARY_MEDIUM);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.REFRESH, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(50));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Rebook Cancelled Flight");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Restore or change your cancelled booking");
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

    private JPanel createCustomerSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel label = new JLabel(FontAwesomeIcon.USER + "  Select Customer:");
        label.setFont(FontAwesomeIcon.getFont(14));
        label.setForeground(ColorScheme.TEXT_PRIMARY);

        customerComboBox = new JComboBox<>();
        customerComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customerComboBox.setPreferredSize(new Dimension(300, 35));
        customerComboBox.addActionListener(e -> loadCancelledBookings());

        panel.add(label);
        panel.add(customerComboBox);

        return panel;
    }

    private JPanel createCancelledBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(ColorScheme.BACKGROUND);

        JLabel titleLabel = new JLabel(FontAwesomeIcon.LIST + "  Cancelled Bookings");
        titleLabel.setFont(FontAwesomeIcon.getFont(16));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        String[] columns = {"Flight No", "Origin", "Destination", "Date", "Price", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // IMPROVED: Use ModernTable
        cancelledBookingsTable = new ModernTable(tableModel);
        cancelledBookingsTable.setPriceColumn(4);
        cancelledBookingsTable.setStatusColumn(5);

        JScrollPane scrollPane = new JScrollPane(cancelledBookingsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createRebookingOptionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1),
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Radio buttons
        ButtonGroup group = new ButtonGroup();
        
        rebookSameFlightRadio = new JRadioButton("Rebook same flight");
        rebookSameFlightRadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rebookSameFlightRadio.setBackground(ColorScheme.CARD_BG);
        rebookSameFlightRadio.setSelected(true);
        rebookSameFlightRadio.addActionListener(e -> newFlightComboBox.setEnabled(false));
        
        rebookDifferentFlightRadio = new JRadioButton("Rebook different flight");
        rebookDifferentFlightRadio.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        rebookDifferentFlightRadio.setBackground(ColorScheme.CARD_BG);
        rebookDifferentFlightRadio.addActionListener(e -> {
            newFlightComboBox.setEnabled(true);
            loadAvailableFlights();
        });

        group.add(rebookSameFlightRadio);
        group.add(rebookDifferentFlightRadio);

        // New flight selection
        newFlightComboBox = new JComboBox<>();
        newFlightComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        newFlightComboBox.setEnabled(false);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 15));
        buttonPanel.setBackground(ColorScheme.CARD_BG);

        rebookButton = createStyledButton(FontAwesomeIcon.CHECK + "  Rebook Flight", ColorScheme.SUCCESS);
        rebookButton.addActionListener(this);

        cancelButton = createStyledButton(FontAwesomeIcon.TIMES_CIRCLE + "  Cancel", ColorScheme.DANGER);
        cancelButton.addActionListener(this);

        buttonPanel.add(rebookButton);
        buttonPanel.add(cancelButton);

        panel.add(rebookSameFlightRadio);
        panel.add(Box.createVerticalStrut(10));
        panel.add(rebookDifferentFlightRadio);
        panel.add(Box.createVerticalStrut(10));
        panel.add(newFlightComboBox);
        panel.add(Box.createVerticalStrut(15));
        panel.add(buttonPanel);

        return panel;
    }

    //  Styled button with animations
    private JButton createStyledButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(14));  // Use FontAwesome font for icons
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

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == rebookButton) {
            rebookFlight();
        } else if (ae.getSource() == cancelButton) {
            this.dispose();
        }
    }

    private void loadCustomers() {
        customers = new ArrayList<>(fbs.getCustomers());
        customerComboBox.removeAllItems();
        for (Customer customer : customers) {
            customerComboBox.addItem(customer.getId() + " - " + customer.getName());
        }
    }

    private void loadCancelledBookings() {
        tableModel.setRowCount(0);
        cancelledBookings = new ArrayList<>();

        int selectedIndex = customerComboBox.getSelectedIndex();
        if (selectedIndex >= 0) {
            Customer customer = customers.get(selectedIndex);
            for (Booking booking : customer.getBookings()) {
                if (booking.isCancelled()) {
                    cancelledBookings.add(booking);
                    Flight flight = booking.getFlight();
                    tableModel.addRow(new Object[]{
                        flight.getFlightNumber(),
                        flight.getOrigin(),
                        flight.getDestination(),
                        flight.getDepartureDate(),
                        "£" + String.format("%.2f", booking.getBookingPrice()),
                        "Cancelled"
                    });
                }
            }
        }
    }

    private void loadAvailableFlights() {
        newFlightComboBox.removeAllItems();
        availableFlights = fbs.getFutureFlights();
        for (Flight flight : availableFlights) {
            newFlightComboBox.addItem(flight.getFlightNumber() + " - " + 
                                     flight.getOrigin() + " to " + flight.getDestination());
        }
    }

    // IMPROVED: Rebook with loading state and toast
    private void rebookFlight() {
        int selectedRow = cancelledBookingsTable.getSelectedRow();
        if (selectedRow < 0) {
            Toast.showWarning(this, "Please select a cancelled booking");
            return;
        }

        Booking selectedBooking = cancelledBookings.get(selectedRow);
        int customerId = customers.get(customerComboBox.getSelectedIndex()).getId();
        int oldFlightId = selectedBooking.getFlight().getId();
        int newFlightId;

        if (rebookSameFlightRadio.isSelected()) {
            newFlightId = oldFlightId;
        } else {
            if (newFlightComboBox.getSelectedIndex() < 0) {
                Toast.showWarning(this, "Please select a new flight");
                return;
            }
            newFlightId = availableFlights.get(newFlightComboBox.getSelectedIndex()).getId();
        }

        LoadingOverlay loading = new LoadingOverlay("Rebooking your flight...");
        final JFrame parentFrame = this;  // Store reference to the frame
        loading.show(parentFrame);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(800);
                
                Command rebookCmd = new RebookFlightCommand(customerId, oldFlightId, newFlightId, fbs.getSystemDate());
                rebookCmd.execute(fbs);
                
                return null;
            }

            @Override
            protected void done() {
                loading.hide(parentFrame);  // Use the stored reference

                try {
                    get();
                    
                    Toast.showSuccess(parentFrame, 
                        "Flight rebooked successfully!");
                    
                    loadCancelledBookings();

                } catch (Exception ex) {
                    Toast.showError(parentFrame, 
                        "Rebooking failed: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }
}