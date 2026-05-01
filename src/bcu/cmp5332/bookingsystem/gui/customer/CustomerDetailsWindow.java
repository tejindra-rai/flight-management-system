package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.components.ModernTable;
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

/**
 * FIXED Modern popup window displaying detailed information about a customer.
 * Fixed button fonts to display FontAwesome icons properly
 * 
 * @author Tejindra Rai
 * @version 6.2 - FIXED all button fonts to use FontAwesome
 */
public class CustomerDetailsWindow extends JFrame {

    private Customer customer;
    private FlightBookingSystem fbs;
    private boolean editMode = false;
    
    // Edit fields
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JComboBox<String> ageGroupCombo;
    private JComboBox<String> hasChildrenCombo;
    private JComboBox<String> mealPrefCombo;
    
    // Panels to refresh
    private JPanel contentPanel;
    private JScrollPane mainScrollPane;

    public CustomerDetailsWindow(Customer customer) {
        this.customer = customer;
        this.fbs = null;
        initialize();
    }

    public CustomerDetailsWindow(Customer customer, FlightBookingSystem fbs) {
        this.customer = customer;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Customer Profile");
        setSize(1000, 750);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, 0));
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Header with EDIT button
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Content
        contentPanel = createContentPanel();
        mainScrollPane = new JScrollPane(contentPanel);
        mainScrollPane.setBorder(null);
        mainScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(mainScrollPane, BorderLayout.CENTER);

        // Footer with Close button
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        
        // ANIMATION: Fade in
        UIAnimations.fadeIn(mainPanel, 300);
        
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.PRIMARY_DARK);
        headerPanel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Left side - Profile info
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(FontAwesomeIcon.USER);
        iconLabel.setFont(FontAwesomeIcon.getFont(50));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel nameLabel = new JLabel(customer.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel idLabel = new JLabel("Customer #" + customer.getId());
        idLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        idLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.9));
        idLabel.setAlignmentX(LEFT_ALIGNMENT);

        leftPanel.add(iconLabel);
        leftPanel.add(Box.createVerticalStrut(15));
        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(8));
        leftPanel.add(idLabel);

        // Right side - Edit button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);

        JButton editButton = new JButton(FontAwesomeIcon.EDIT + " Edit Profile");
        editButton.setFont(FontAwesomeIcon.getFont(14)); // FIXED: Use FontAwesome font
        editButton.setForeground(ColorScheme.PRIMARY_DARK);
        editButton.setBackground(ColorScheme.GOLD);
        editButton.setBorder(new EmptyBorder(12, 25, 12, 25));
        editButton.setFocusPainted(false);
        editButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        editButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                editButton.setBackground(ColorScheme.getHoverColor(ColorScheme.GOLD));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                editButton.setBackground(ColorScheme.GOLD);
            }
        });
        
        editButton.addActionListener(e -> toggleEditMode());
        rightPanel.add(editButton);

        headerPanel.add(leftPanel, BorderLayout.WEST);
        headerPanel.add(rightPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(ColorScheme.BACKGROUND);
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        // Customer Information
        panel.add(createInfoPanel());
        panel.add(Box.createVerticalStrut(25));

        // Bookings
        panel.add(createBookingsPanel());

        return panel;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(30, 30, 30, 30)
        ));

        if (editMode) {
            // EDIT MODE - Use GridBagLayout for proper spacing
            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // Name
            nameField = new JTextField(customer.getName());
            addEditRow(panel, gbc, row++, FontAwesomeIcon.USER, "Full Name", nameField);
            
            // Phone
            phoneField = new JTextField(customer.getPhone());
            addEditRow(panel, gbc, row++, FontAwesomeIcon.PHONE, "Phone Number", phoneField);
            
            // Email
            emailField = new JTextField(customer.getEmail());
            addEditRow(panel, gbc, row++, FontAwesomeIcon.ENVELOPE, "Email Address", emailField);
            
            // Age Group
            String[] ageGroups = {"18-25", "26-35", "36-45", "46-55", "56-65", "65+"};
            ageGroupCombo = new JComboBox<>(ageGroups);
            ageGroupCombo.setSelectedItem(customer.getAgeGroup());
            styleComboBox(ageGroupCombo);
            addEditRow(panel, gbc, row++, FontAwesomeIcon.CALENDAR, "Age Group", ageGroupCombo);
            
            // Has Children
            String[] yesNo = {"Yes", "No"};
            hasChildrenCombo = new JComboBox<>(yesNo);
            hasChildrenCombo.setSelectedItem(customer.hasChildren() ? "Yes" : "No");
            styleComboBox(hasChildrenCombo);
            addEditRow(panel, gbc, row++, FontAwesomeIcon.USERS, "Has Children", hasChildrenCombo);
            
            // Meal Preference
            String[] meals = {"Vegetarian", "Non-Vegetarian", "Vegan", "Gluten-Free", "Halal", "Kosher"};
            mealPrefCombo = new JComboBox<>(meals);
            mealPrefCombo.setSelectedItem(customer.getMealPreference());
            styleComboBox(mealPrefCombo);
            addEditRow(panel, gbc, row++, FontAwesomeIcon.UTENSILS, "Meal Preference", mealPrefCombo);
            
        } else {
            // VIEW MODE - Use GridBagLayout for better layout
            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(12, 15, 12, 15);
            gbc.anchor = GridBagConstraints.WEST;
            
            int row = 0;
            
            // Name
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.USER, "Full Name", customer.getName());
            
            // Phone
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.PHONE, "Phone Number", customer.getPhone());
            
            // Email
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.ENVELOPE, "Email Address", customer.getEmail());
            
            // Age Group
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.CALENDAR, "Age Group", customer.getAgeGroup());
            
            // Has Children
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.USERS, "Has Children", 
                customer.hasChildren() ? "Yes" : "No");
            
            // Meal Preference
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.UTENSILS, "Meal Preference", 
                customer.getMealPreference());
            
            // Total Bookings
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.TICKET, "Total Bookings", 
                String.valueOf(customer.getBookings().size()));
            
            // Active Bookings
            long activeBookings = customer.getBookings().stream()
                .filter(b -> !b.isCancelled()).count();
            addInfoRow(panel, gbc, row++, FontAwesomeIcon.CHECK_SQUARE, "Active Bookings", 
                String.valueOf(activeBookings));
        }

        return panel;
    }
    
    /**
     * Add a field row in edit mode using GridBagLayout
     */
    private void addEditRow(JPanel panel, GridBagConstraints gbc, int row, 
                            String iconCode, String labelText, JComponent field) {
        // Icon column (column 0)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        
        JLabel iconLabel = new JLabel(iconCode);
        iconLabel.setFont(FontAwesomeIcon.getFont(18));
        iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);
        panel.add(iconLabel, gbc);
        
        // Label column (column 1)
        gbc.gridx = 1;
        gbc.weightx = 0.25;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorScheme.TEXT_PRIMARY);
        panel.add(label, gbc);
        
        // Field column (column 2)
        gbc.gridx = 2;
        gbc.weightx = 0.75;
        
        if (field instanceof JTextField) {
            styleTextField((JTextField) field);
        }
        panel.add(field, gbc);
    }
    
    /**
     * Add an info row in view mode using GridBagLayout
     */
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row,
                           String iconCode, String labelText, String value) {
        // Icon column (column 0)
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.0;
        gbc.gridwidth = 1;
        
        JLabel iconLabel = new JLabel(iconCode);
        iconLabel.setFont(FontAwesomeIcon.getFont(18));
        iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);
        panel.add(iconLabel, gbc);
        
        // Label column (column 1)
        gbc.gridx = 1;
        gbc.weightx = 0.25;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(ColorScheme.TEXT_SECONDARY);
        panel.add(label, gbc);
        
        // Value column (column 2)
        gbc.gridx = 2;
        gbc.weightx = 0.75;
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        panel.add(valueLabel, gbc);
    }

    private void styleTextField(JTextField field) {
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(ColorScheme.TEXT_PRIMARY);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY_LIGHT, 2),
            new EmptyBorder(10, 12, 10, 12)
        ));
        field.setPreferredSize(new Dimension(400, 40));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setForeground(ColorScheme.TEXT_PRIMARY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY_LIGHT, 2),
            new EmptyBorder(5, 8, 5, 8)
        ));
        combo.setPreferredSize(new Dimension(400, 40));
    }

    private void toggleEditMode() {
        editMode = !editMode;
        refreshContent();
    }

    private void refreshContent() {
        // Remove old content
        mainScrollPane.setViewportView(null);
        
        // Create new content
        contentPanel = createContentPanel();
        mainScrollPane.setViewportView(contentPanel);
        
        // Update footer
        Component[] components = getContentPane().getComponents();
        for (Component c : components) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                Component[] children = panel.getComponents();
                for (int i = 0; i < children.length; i++) {
                    if (children[i] instanceof JPanel) {
                        try {
                            BorderLayout layout = (BorderLayout) panel.getLayout();
                            if (layout.getLayoutComponent(BorderLayout.SOUTH) == children[i]) {
                                panel.remove(children[i]);
                                panel.add(createFooterPanel(), BorderLayout.SOUTH);
                                break;
                            }
                        } catch (Exception e) {
                            // Not a BorderLayout, continue
                        }
                    }
                }
            }
        }
        
        revalidate();
        repaint();
    }

    private void saveChanges() {
        // Validation
        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter a name.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Email validation
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid email address.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Phone validation (basic)
        if (!phone.matches("^[0-9+\\-\\s()]+$")) {
            JOptionPane.showMessageDialog(this,
                "Please enter a valid phone number.",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update customer object
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setAgeGroup((String) ageGroupCombo.getSelectedItem());
        customer.setHasChildren(hasChildrenCombo.getSelectedItem().equals("Yes"));
        customer.setMealPreference((String) mealPrefCombo.getSelectedItem());
        
        // Save to database
        if (fbs != null) {
            try {
                FlightBookingSystemData.safeStore(fbs);
                
                // Show success message
                JOptionPane.showMessageDialog(this,
                    "Profile updated and saved to database successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Profile updated in memory, but failed to save to database:\n" + e.getMessage(),
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // Show success message (without database save)
            JOptionPane.showMessageDialog(this,
                "Profile updated successfully!\n(Note: Changes are in memory only - will be saved on logout)",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
        
        // Exit edit mode and refresh
        editMode = false;
        
        // Fully reinitialize to update header with new name
        getContentPane().removeAll();
        initialize();
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        footerPanel.setBackground(ColorScheme.BACKGROUND);
        footerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        if (editMode) {
            // SAVE button - FIXED: Use FontAwesome font
            JButton saveBtn = new JButton(FontAwesomeIcon.CHECK + " Save Changes");
            saveBtn.setFont(FontAwesomeIcon.getFont(14)); // FIXED!
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setBackground(ColorScheme.SUCCESS);
            saveBtn.setBorder(new EmptyBorder(12, 25, 12, 25));
            saveBtn.setFocusPainted(false);
            saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    saveBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.SUCCESS));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    saveBtn.setBackground(ColorScheme.SUCCESS);
                }
            });
            
            saveBtn.addActionListener(e -> saveChanges());

            // CANCEL button - FIXED: Use FontAwesome font
            JButton cancelBtn = new JButton(FontAwesomeIcon.TIMES_CIRCLE + " Cancel");
            cancelBtn.setFont(FontAwesomeIcon.getFont(14)); // FIXED!
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setBackground(ColorScheme.DANGER);
            cancelBtn.setBorder(new EmptyBorder(12, 25, 12, 25));
            cancelBtn.setFocusPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            cancelBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    cancelBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.DANGER));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    cancelBtn.setBackground(ColorScheme.DANGER);
                }
            });
            
            cancelBtn.addActionListener(e -> {
                editMode = false;
                refreshContent();
            });

            footerPanel.add(saveBtn);
            footerPanel.add(cancelBtn);
        } else {
            // CLOSE button - FIXED: Use FontAwesome font
            JButton closeBtn = new JButton(FontAwesomeIcon.TIMES_CIRCLE + " Close");
            closeBtn.setFont(FontAwesomeIcon.getFont(14)); // FIXED!
            closeBtn.setForeground(Color.WHITE);
            closeBtn.setBackground(ColorScheme.PRIMARY_MEDIUM);
            closeBtn.setBorder(new EmptyBorder(12, 30, 12, 30));
            closeBtn.setFocusPainted(false);
            closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    closeBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.PRIMARY_MEDIUM));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    closeBtn.setBackground(ColorScheme.PRIMARY_MEDIUM);
                }
            });
            
            closeBtn.addActionListener(e -> dispose());

            footerPanel.add(closeBtn);
        }

        return footerPanel;
    }

    private JPanel createBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setBackground(ColorScheme.CARD_BG);

        JLabel iconLabel = new JLabel(FontAwesomeIcon.CALENDAR);
        iconLabel.setFont(FontAwesomeIcon.getFont(18));
        iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);

        JLabel titleLabel = new JLabel("Booking History (" + customer.getBookings().size() + " total)");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        titlePanel.add(iconLabel);
        titlePanel.add(titleLabel);

        // Use ModernTable
        String[] columns = {"Flight No", "Origin", "Destination", "Date", "Price", "Status"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Booking booking : customer.getBookings()) {
            Flight flight = booking.getFlight();
            double totalPrice = booking.getBookingPrice() + booking.getCancellationFee();
            
            Object[] row = {
                flight.getFlightNumber(),
                flight.getOrigin(),
                flight.getDestination(),
                flight.getDepartureDate().toString(),
                "£" + String.format("%.2f", totalPrice),
                booking.isCancelled() ? "Cancelled" : "Active"
            };
            tableModel.addRow(row);
        }

        ModernTable table = new ModernTable(tableModel);
        table.setPriceColumn(4);
        table.setStatusColumn(5);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT));
        scrollPane.setPreferredSize(new Dimension(0, 250));

        panel.add(titlePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }
}