package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.gui.customer.CustomerDetailsWindow;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
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
 * Admin customer browsing window with advanced filtering and management options.
 * 
 * @author Tejindra Rai
 * @version 1.0 - Professional admin customer browser
 */
public class AdminBrowseCustomersWindow extends JFrame {

    private FlightBookingSystem fbs;
    private JTable customersTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> filterCombo;
    private JTextField searchField;
    private JCheckBox showDeletedCheck;
    private List<Customer> allCustomers;
    private List<Customer> filteredCustomers;

    public AdminBrowseCustomersWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        this.allCustomers = fbs.getCustomers();
        this.filteredCustomers = new ArrayList<>();
        // Initially show only active customers
        for (Customer c : allCustomers) {
            if (!c.isDeleted()) {
                filteredCustomers.add(c);
            }
        }
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Admin Customer Browser");
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
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY); // Maroon for admin
        headerPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));

        JLabel iconLabel = FontAwesomeIcon.createIcon(FontAwesomeIcon.USER, 50, ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Admin Customer Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Browse, filter, and manage all customers in the system");
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
        String[] filterOptions = {"All Active Customers", "Customers with Bookings", 
                                  "Customers without Bookings", "Adult", "Child", "Senior",
                                  "Vegetarian", "Non-Vegetarian", "Vegan"};
        filterCombo = new JComboBox<>(filterOptions);
        filterCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        filterCombo.setPreferredSize(new Dimension(220, 35));
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
        showDeletedCheck = new JCheckBox("Show Deleted Customers");
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
        refreshBtn.addActionListener(e -> refreshCustomers());

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
        JLabel infoLabel = new JLabel(" " + filteredCustomers.size() + " customers shown");
        infoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        infoLabel.setForeground(ColorScheme.ADMIN_PRIMARY);
        infoLabelPanel.add(infoIcon);
        infoLabelPanel.add(infoLabel);

        JLabel hintLabel = new JLabel("Double-click a row to view customer profile");
        hintLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hintLabel.setForeground(ColorScheme.TEXT_SECONDARY);

        infoPanel.add(infoLabelPanel);
        infoPanel.add(Box.createHorizontalStrut(20));
        infoPanel.add(hintLabel);

        // Table
        String[] columns = {"ID", "Name", "Email", "Phone", "Age Group", "Has Children", 
                           "Meal Pref", "Bookings", "Active Bookings", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        customersTable = new JTable(tableModel);
        customersTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        customersTable.setRowHeight(35);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.setFillsViewportHeight(true);
        customersTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        customersTable.getTableHeader().setBackground(ColorScheme.ADMIN_PRIMARY);
        customersTable.getTableHeader().setForeground(Color.WHITE);
        customersTable.getTableHeader().setPreferredSize(new Dimension(0, 40));

        // Add double-click listener to view customer details
        customersTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int selectedRow = customersTable.getSelectedRow();
                    if (selectedRow >= 0) {
                        Customer selectedCustomer = filteredCustomers.get(selectedRow);
                        new CustomerDetailsWindow(selectedCustomer);
                    }
                }
            }
        });

        populateTable();

        JScrollPane scrollPane = new JScrollPane(customersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1));

        tablePanel.add(infoPanel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        return tablePanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        buttonPanel.setBackground(ColorScheme.BACKGROUND);
        buttonPanel.setBorder(new EmptyBorder(15, 20, 20, 20));

        // View Profile button
        JButton viewProfileBtn = createIconButton(FontAwesomeIcon.USER, "View Profile", ColorScheme.ADMIN_PRIMARY);
        viewProfileBtn.addActionListener(e -> viewSelectedCustomer());

        // Close button
        JButton closeBtn = createIconButton(FontAwesomeIcon.TIMES_CIRCLE, "Close", ColorScheme.DANGER);
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(viewProfileBtn);
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
        
        for (Customer customer : filteredCustomers) {
            int activeBookings = (int) customer.getBookings().stream()
                .filter(b -> !b.isCancelled())
                .count();
            
            Object[] row = {
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAgeGroup(),
                customer.hasChildren() ? "Yes" : "No",
                customer.getMealPreference(),
                customer.getBookings().size(),
                activeBookings,
                customer.isDeleted() ? "🗑️ Deleted" : "✅ Active"
            };
            tableModel.addRow(row);
        }
    }

    private void applyFilters() {
        filteredCustomers.clear();
        String selectedFilter = (String) filterCombo.getSelectedItem();
        String searchText = searchField.getText().toLowerCase().trim();
        boolean showDeleted = showDeletedCheck.isSelected();

        for (Customer customer : allCustomers) {
            boolean matchesFilter = true;
            boolean matchesSearch = true;
            boolean matchesDeletedStatus = showDeleted || !customer.isDeleted();

            // Apply filter
            if (selectedFilter.equals("Customers with Bookings") && customer.getBookings().isEmpty()) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Customers without Bookings") && !customer.getBookings().isEmpty()) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Adult") && !customer.getAgeGroup().equalsIgnoreCase("Adult")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Child") && !customer.getAgeGroup().equalsIgnoreCase("Child")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Senior") && !customer.getAgeGroup().equalsIgnoreCase("Senior")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Vegetarian") && !customer.getMealPreference().equalsIgnoreCase("Vegetarian")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Non-Vegetarian") && !customer.getMealPreference().equalsIgnoreCase("Non-Vegetarian")) {
                matchesFilter = false;
            } else if (selectedFilter.equals("Vegan") && !customer.getMealPreference().equalsIgnoreCase("Vegan")) {
                matchesFilter = false;
            }

            // Apply search
            if (!searchText.isEmpty()) {
                matchesSearch = String.valueOf(customer.getId()).contains(searchText) ||
                               customer.getName().toLowerCase().contains(searchText) ||
                               customer.getEmail().toLowerCase().contains(searchText) ||
                               customer.getPhone().contains(searchText);
            }

            if (matchesFilter && matchesSearch && matchesDeletedStatus) {
                filteredCustomers.add(customer);
            }
        }

        populateTable();
    }

    private void refreshCustomers() {
        allCustomers = fbs.getCustomers();
        showDeletedCheck.setSelected(false);
        filterCombo.setSelectedIndex(0);
        searchField.setText("");
        applyFilters();
        
        JOptionPane.showMessageDialog(this,
            "Customer list refreshed!\n" + filteredCustomers.size() + " customers shown.",
            "Refreshed",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewSelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        if (selectedRow >= 0) {
            Customer selectedCustomer = filteredCustomers.get(selectedRow);
            new CustomerDetailsWindow(selectedCustomer);
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a customer to view profile.",
                "No Selection",
                JOptionPane.WARNING_MESSAGE);
        }
    }
}