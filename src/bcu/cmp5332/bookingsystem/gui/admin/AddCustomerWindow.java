package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.EmailValidator;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Admin window for adding new customers with complete account setup.
 * 
 * @author Tejindra Rai
 * @version 6.0 - Added username and password fields for customer login
 */
public class AddCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField nameText = new JTextField();
    private JTextField phoneText = new JTextField();
    private JTextField emailText = new JTextField();
    private JTextField usernameText = new JTextField();
    private JPasswordField passwordText = new JPasswordField();
    private JPasswordField confirmPasswordText = new JPasswordField();
    private JComboBox<String> ageGroupCombo = new JComboBox<>(new String[]{"Child", "Adult", "Senior"});
    private JCheckBox hasChildrenCheck = new JCheckBox();
    private JComboBox<String> mealPrefCombo = new JComboBox<>(new String[]{"None", "Vegetarian", "Non-Vegetarian", "Vegan"});

    private JButton addBtn = new JButton("Add Customer");
    private JButton cancelBtn = new JButton("Cancel");

    public AddCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Add Customer");
        setSize(600, 720);
        setLayout(new BorderLayout(0, 0));

        // Header Panel with maroon theme
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.USER);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Add New Customer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Register a new customer with login credentials");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Scrollable Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(12, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(ColorScheme.CARD_BG);
        
        // Personal Information Section Header
        JLabel personalInfoLabel = new JLabel("Personal Information");
        personalInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        personalInfoLabel.setForeground(ColorScheme.ADMIN_PRIMARY);
        formPanel.add(personalInfoLabel);
        formPanel.add(new JLabel("")); // Empty cell
        
        // Name
        formPanel.add(createLabel("Full Name:"));
        formPanel.add(nameText);
        
        // Phone
        formPanel.add(createLabel("Phone Number:"));
        formPanel.add(phoneText);
        
        // Email
        formPanel.add(createLabel("Email Address:"));
        formPanel.add(emailText);
        
        // Age Group
        formPanel.add(createLabel("Age Group:"));
        formPanel.add(ageGroupCombo);
        
        // Has Children
        formPanel.add(createLabel("Has Children:"));
        formPanel.add(hasChildrenCheck);
        
        // Meal Preference
        formPanel.add(createLabel("Meal Preference:"));
        formPanel.add(mealPrefCombo);
        
        // Account Information Section Header
        JLabel accountInfoLabel = new JLabel("Login Credentials");
        accountInfoLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        accountInfoLabel.setForeground(ColorScheme.ADMIN_PRIMARY);
        formPanel.add(accountInfoLabel);
        formPanel.add(new JLabel("")); // Empty cell
        
        // Username
        formPanel.add(createLabel("Username:"));
        formPanel.add(usernameText);
        
        // Password
        formPanel.add(createLabel("Password (min. 6 chars):"));
        formPanel.add(passwordText);
        
        // Confirm Password
        formPanel.add(createLabel("Confirm Password:"));
        formPanel.add(confirmPasswordText);
        
        // Info label
        JLabel infoLabel = new JLabel("All fields are required");
        infoLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        infoLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        formPanel.add(infoLabel);
        formPanel.add(new JLabel(""));

        // Wrap form in scroll pane
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

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
        add(scrollPane, BorderLayout.CENTER);
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
            addCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void addCustomer() {
        try {
            String name = nameText.getText().trim();
            String phone = phoneText.getText().trim();
            String email = emailText.getText().trim();
            String username = usernameText.getText().trim();
            String password = new String(passwordText.getPassword()).trim();
            String confirmPass = new String(confirmPasswordText.getPassword()).trim();
            
            // Validation
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty() || 
                username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
                throw new FlightBookingSystemException("All fields are required!");
            }
            
            if (name.length() < 2) {
                throw new FlightBookingSystemException("Name must be at least 2 characters long!");
            }
            
            if (username.length() < 3) {
                throw new FlightBookingSystemException("Username must be at least 3 characters long!");
            }
            
            if (password.length() < 6) {
                throw new FlightBookingSystemException("Password must be at least 6 characters long!");
            }
            
            if (!password.equals(confirmPass)) {
                throw new FlightBookingSystemException("Passwords do not match!");
            }
            
            if (!EmailValidator.isValidEmail(email)) {
                throw new FlightBookingSystemException(
                    "Invalid email format!\n\n" +
                    "Email must contain @ symbol and a valid domain.\n" +
                    "Example: user@example.com"
                );
            }
            
            if (!phone.matches("[0-9+\\-\\s()]+")) {
                throw new FlightBookingSystemException(
                    "Invalid phone number format!\n\n" +
                    "Use only numbers, +, -, (), and spaces."
                );
            }
            
            // Check if username already exists
            for (User u : mw.getFlightBookingSystem().getUsers()) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    throw new FlightBookingSystemException(
                        "Username '" + username + "' is already taken!\n\n" +
                        "Please choose a different username."
                    );
                }
            }
            
            // Check if email already exists
            for (Customer c : mw.getFlightBookingSystem().getCustomers()) {
                if (c.getEmail().equalsIgnoreCase(email)) {
                    throw new FlightBookingSystemException(
                        "Email '" + email + "' is already registered!\n\n" +
                        "Each customer must have a unique email address."
                    );
                }
            }
            
            // Create Customer
            AddCustomer addCustomerCommand = new AddCustomer(name, phone, email);
            addCustomerCommand.execute(mw.getFlightBookingSystem());
            
            Customer customer = addCustomerCommand.getCreatedCustomer();
            
            if (customer == null) {
                throw new FlightBookingSystemException("Failed to retrieve created customer.");
            }
            
            // Set additional customer details
            customer.setAgeGroup((String) ageGroupCombo.getSelectedItem());
            customer.setHasChildren(hasChildrenCheck.isSelected());
            customer.setMealPreference((String) mealPrefCombo.getSelectedItem());
            
            // Create User account for the customer
            int newUserId = mw.getFlightBookingSystem().getUsers().size() + 1;
            User user = new User(newUserId, username, password, "CUSTOMER");
            user.setLinkedCustomerId(customer.getId());
            mw.getFlightBookingSystem().addUser(user);
            
            // Save to database
            FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
            mw.displayCustomers();
            
            JOptionPane.showMessageDialog(this, 
                "Customer Added Successfully!\n\n" +
                "Customer Details:\n" +
                "  Customer ID: " + customer.getId() + "\n" +
                "  Name: " + name + "\n" +
                "  Email: " + email + "\n" +
                "  Phone: " + phone + "\n" +
                "  Age Group: " + customer.getAgeGroup() + "\n" +
                "  Meal Preference: " + customer.getMealPreference() + "\n\n" +
                "Login Credentials:\n" +
                "  Username: " + username + "\n" +
                "  Password: " + password + "\n\n" +
                "The customer can now login to the system.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}