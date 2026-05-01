package bcu.cmp5332.bookingsystem.gui.common;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.EmailValidator;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern responsive registration window with enhanced design and validation.
 * Features responsive sizing, smooth scrolling, and improved user experience.
 * 
 * @author Tejindra Rai
 * @version 5.0 - Added age group, has children, and meal preference fields
 */
public class RegisterWindow extends JFrame implements ActionListener {

    private final FlightBookingSystem fbs;
    private final LoginWindow parentLogin;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    
    // New fields for customer preferences
    private JComboBox<String> ageGroupCombo;
    private JCheckBox hasChildrenCheck;
    private JComboBox<String> mealPrefCombo;

    private JButton registerBtn;
    private JButton backBtn;
    private JLabel messageLabel;
    
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 700;
    private static final int PREFERRED_WIDTH = 1200;
    private static final int PREFERRED_HEIGHT = 850;

    public RegisterWindow(FlightBookingSystem fbs, LoginWindow parentLogin) {
        this.fbs = fbs;
        this.parentLogin = parentLogin;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Create Account");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        
        // Calculate responsive window size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(PREFERRED_WIDTH, (int)(screenSize.width * 0.8));
        int height = Math.min(PREFERRED_HEIGHT, (int)(screenSize.height * 0.85));
        setSize(width, height);
        
        setLocationRelativeTo(parentLogin);

        // Main responsive container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        
        // Split panel with BorderLayout for exact proportions
        JPanel splitPanel = new JPanel(new BorderLayout());
        
        // Create a container with precise width control
        JPanel leftContainer = new JPanel(new BorderLayout());
        JPanel leftPanel = createResponsiveBrandingPanel();
        leftContainer.add(leftPanel, BorderLayout.CENTER);
        leftContainer.setPreferredSize(new Dimension((int)(width * 0.38), height));
        leftContainer.setMinimumSize(new Dimension((int)(MIN_WIDTH * 0.38), MIN_HEIGHT));
        
        JPanel rightPanel = createResponsiveRegistrationFormPanel();
        
        splitPanel.add(leftContainer, BorderLayout.WEST);
        splitPanel.add(rightPanel, BorderLayout.CENTER);

        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;
        mainPanel.add(splitPanel, mainGbc);

        add(mainPanel);
        
        // Add component listener for responsive adjustments
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponentsForSize();
            }
        });
        
        setVisible(true);
    }

    /**
     * Creates responsive branding panel with gradient background - MATCHES LoginWindow.
     */
    private JPanel createResponsiveBrandingPanel() {
        JPanel brandPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                // Responsive gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, ColorScheme.PRIMARY_DARK,
                    0, getHeight(), ColorScheme.PRIMARY_LIGHT
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Responsive decorative circles
                float scale = Math.min(getWidth() / 550f, getHeight() / 850f);
                int size1 = (int)(220 * scale);
                int size2 = (int)(200 * scale);
                int size3 = (int)(160 * scale);
                
                g2d.setColor(ColorScheme.withOpacity(Color.WHITE, 0.08));
                g2d.fillOval(-size1/2, -size1/2, size1, size1);
                g2d.fillOval(getWidth() - size2/2, getHeight() - size2/2, size2, size2);
                g2d.fillOval((int)(100 * scale), getHeight() - size3, size3, size3);
            }
        };
        
        brandPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setBorder(new EmptyBorder(40, 30, 40, 30));

        // Icon - responsive size (MATCHING LoginWindow style)
        JLabel iconLabel = new JLabel(FontAwesomeIcon.PLANE, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(getResponsiveSize(90)));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(iconLabel);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(35)));

        // Title - MATCHING LoginWindow size
        JLabel titleLabel = new JLabel("B & T Airlines");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(46)));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));

        JLabel subtitleLabel = new JLabel("Join our family today");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(19)));
        subtitleLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.92));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(12)));

        JLabel welcomeTitle = new JLabel("CREATE ACCOUNT");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(38)));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(welcomeTitle);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(45)));

        // Benefits
        contentPanel.add(createBenefitLabel(FontAwesomeIcon.CHECK, "Easy flight booking"));
        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));
        contentPanel.add(createBenefitLabel(FontAwesomeIcon.CALENDAR, "Manage your bookings"));
        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));
        contentPanel.add(createBenefitLabel(FontAwesomeIcon.STAR, "Exclusive member offers"));
        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));
        contentPanel.add(createBenefitLabel(FontAwesomeIcon.GLOBE, "Worldwide destinations"));

        brandPanel.add(contentPanel, gbc);
        return brandPanel;
    }

    /**
     * Creates benefit label with responsive sizing.
     */
    private JPanel createBenefitLabel(String icon, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(450, getResponsiveSize(35)));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(getResponsiveSize(18)));
        iconLabel.setForeground(ColorScheme.GOLD);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(16)));
        textLabel.setForeground(Color.WHITE);

        panel.add(iconLabel);
        panel.add(textLabel);

        return panel;
    }

    /**
     * Creates responsive registration form panel with scrolling.
     */
    private JPanel createResponsiveRegistrationFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());

        // Create wrapper for centering
        JPanel wrapperPanel = new JPanel(new GridBagLayout());
        wrapperPanel.setBackground(Color.WHITE);
        
        // Scrollable content
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        
        int padding = getResponsiveSize(50);  // Reduced from 60 to match login
        contentPanel.setBorder(new EmptyBorder(padding, padding, padding, padding));
        
        // Set max width to prevent over-expansion - keep form compact
        contentPanel.setMaximumSize(new Dimension(550, Integer.MAX_VALUE));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 1.0;

        int row = 0;

        // User icon at top - MATCHING LoginWindow
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(25), 0);
        JLabel userIconLabel = new JLabel(FontAwesomeIcon.USER, SwingConstants.CENTER);
        userIconLabel.setFont(FontAwesomeIcon.getFont(getResponsiveSize(55)));
        userIconLabel.setForeground(ColorScheme.PRIMARY_DARK);
        contentPanel.add(userIconLabel, gbc);

        // Header - MATCHING LoginWindow size
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(12), 0);
        JLabel headerLabel = new JLabel("Create Account", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(34)));
        headerLabel.setForeground(ColorScheme.PRIMARY_DARK);
        contentPanel.add(headerLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(12), 0, getResponsiveSize(28), 0);
        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(12)), gbc);

        // Full Name
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        JLabel nameLabel = new JLabel("Full Name");
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        nameLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(nameLabel, gbc);

        gbc.gridy = row++;
        nameField = new JTextField(25);
        contentPanel.add(createResponsiveInputPanel(nameField, FontAwesomeIcon.USER), gbc);

        // Email
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel emailLabel = new JLabel("Email Address");
        emailLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        emailLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(emailLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        emailField = new JTextField(25);
        contentPanel.add(createResponsiveInputPanel(emailField, FontAwesomeIcon.ENVELOPE), gbc);

        // Phone
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        phoneLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(phoneLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        phoneField = new JTextField(25);
        contentPanel.add(createResponsiveInputPanel(phoneField, FontAwesomeIcon.PHONE), gbc);

        // Age Group
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel ageLabel = new JLabel("Age Group");
        ageLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        ageLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(ageLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        ageGroupCombo = new JComboBox<>(new String[]{"Child", "Adult", "Senior"});
        ageGroupCombo.setSelectedItem("Adult");
        contentPanel.add(createResponsiveComboPanel(ageGroupCombo, FontAwesomeIcon.USER), gbc);

        // Has Children Checkbox
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(12), 0, getResponsiveSize(6), 0);
        hasChildrenCheck = new JCheckBox("I have children");
        hasChildrenCheck.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(15)));
        hasChildrenCheck.setBackground(Color.WHITE);
        hasChildrenCheck.setForeground(ColorScheme.TEXT_PRIMARY);
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkboxPanel.setBackground(Color.WHITE);
        checkboxPanel.add(hasChildrenCheck);
        contentPanel.add(checkboxPanel, gbc);

        // Meal Preference
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel mealLabel = new JLabel("Meal Preference");
        mealLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        mealLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(mealLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        mealPrefCombo = new JComboBox<>(new String[]{"None", "Vegetarian", "Non-Vegetarian", "Vegan"});
        contentPanel.add(createResponsiveComboPanel(mealPrefCombo, FontAwesomeIcon.UTENSILS), gbc);

        // Username
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        usernameLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(usernameLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        usernameField = new JTextField(25);
        contentPanel.add(createResponsiveInputPanel(usernameField, FontAwesomeIcon.USER), gbc);

        // Password
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel passwordLabel = new JLabel("Password (min. 6 characters)");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        passwordLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(passwordLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        passwordField = new JPasswordField(25);
        contentPanel.add(createResponsiveInputPanel(passwordField, FontAwesomeIcon.LOCK), gbc);

        // Confirm Password
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel confirmLabel = new JLabel("Confirm Password");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        confirmLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        contentPanel.add(confirmLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        confirmPasswordField = new JPasswordField(25);
        contentPanel.add(createResponsiveInputPanel(confirmPasswordField, FontAwesomeIcon.LOCK), gbc);

        // Message label
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(18), 0);
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(ColorScheme.DANGER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(14)));
        int msgHeight = getResponsiveSize(28);
        messageLabel.setPreferredSize(new Dimension(400, msgHeight));
        contentPanel.add(messageLabel, gbc);

        // Register button
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(18), 0);
        registerBtn = createResponsiveStyledButton("CREATE ACCOUNT", ColorScheme.PRIMARY_DARK, e -> handleRegister());
        contentPanel.add(registerBtn, gbc);

        // Back to login link
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(12), 0, 0, 0);
        JPanel backPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        backPanel.setBackground(Color.WHITE);
        
        JLabel alreadyLabel = new JLabel("Already have an account?");
        alreadyLabel.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(14)));
        alreadyLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        
        JLabel backLabel = new JLabel("<html><u>Back to Login</u></html>");
        backLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(14)));
        backLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);
        backLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (parentLogin != null) {
                    parentLogin.setEnabled(true);
                    parentLogin.setVisible(true);
                }
                dispose();
            }
        });
        
        backPanel.add(alreadyLabel);
        backPanel.add(backLabel);
        contentPanel.add(backPanel, gbc);

        // Wrap content in scroll pane
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getViewport().setBackground(Color.WHITE);

        // Center the scroll pane in the wrapper
        GridBagConstraints wrapperGbc = new GridBagConstraints();
        wrapperGbc.gridx = 0;
        wrapperGbc.gridy = 0;
        wrapperGbc.weightx = 1.0;
        wrapperGbc.weighty = 1.0;
        wrapperGbc.fill = GridBagConstraints.BOTH;
        wrapperPanel.add(scrollPane, wrapperGbc);

        // Add wrapper to form panel
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.weightx = 1.0;
        formGbc.weighty = 1.0;
        formGbc.fill = GridBagConstraints.BOTH;
        formPanel.add(wrapperPanel, formGbc);

        return formPanel;
    }

    /**
     * Creates responsive input panel - MATCHING LoginWindow style.
     */
    private JPanel createResponsiveInputPanel(JTextField field, String icon) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        
        int borderWidth = Math.max(2, getResponsiveSize(2));
        int padding = getResponsiveSize(12);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY_MEDIUM, borderWidth, true),
            new EmptyBorder(padding, padding + 3, padding, padding + 3)
        ));
        
        int panelHeight = getResponsiveSize(52);
        panel.setPreferredSize(new Dimension(400, panelHeight));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelHeight));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(getResponsiveSize(20)));
        iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);

        // Field
        field.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(16)));
        field.setBorder(null);
        field.setBackground(Color.WHITE);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);

        // Hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorScheme.PRIMARY_LIGHT, borderWidth, true),
                    new EmptyBorder(padding, padding + 3, padding, padding + 3)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorScheme.PRIMARY_MEDIUM, borderWidth, true),
                    new EmptyBorder(padding, padding + 3, padding, padding + 3)
                ));
            }
        });

        return panel;
    }

    /**
     * Creates responsive combo box panel - MATCHING LoginWindow style.
     */
    private JPanel createResponsiveComboPanel(JComboBox<String> combo, String icon) {
        JPanel panel = new JPanel(new BorderLayout(12, 0));
        panel.setBackground(Color.WHITE);
        
        int borderWidth = Math.max(2, getResponsiveSize(2));
        int padding = getResponsiveSize(12);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.PRIMARY_MEDIUM, borderWidth, true),
            new EmptyBorder(padding, padding + 3, padding, padding + 3)
        ));
        
        int panelHeight = getResponsiveSize(52);
        panel.setPreferredSize(new Dimension(400, panelHeight));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelHeight));

        // Icon
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(getResponsiveSize(20)));
        iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);

        // Combo box
        combo.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(16)));
        combo.setBackground(Color.WHITE);
        combo.setBorder(null);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(combo, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates responsive styled button.
     */
    private JButton createResponsiveStyledButton(String text, Color bgColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(17)));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        
        int btnHeight = getResponsiveSize(54);
        btn.setPreferredSize(new Dimension(400, btnHeight));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, btnHeight));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        int padding = getResponsiveSize(14);
        btn.setBorder(new EmptyBorder(padding, padding * 2, padding, padding * 2));
        btn.addActionListener(listener);

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

    /**
     * Calculates responsive size based on window dimensions.
     */
    private int getResponsiveSize(int baseSize) {
        int width = getWidth();
        int height = getHeight();
        
        if (width == 0 || height == 0) {
            return baseSize;
        }
        
        float scaleX = Math.min(1.0f, width / (float)PREFERRED_WIDTH);
        float scaleY = Math.min(1.0f, height / (float)PREFERRED_HEIGHT);
        float scale = Math.min(scaleX, scaleY);
        
        // Don't scale too small
        scale = Math.max(0.75f, scale);
        
        return Math.max(1, (int)(baseSize * scale));
    }

    /**
     * Adjusts components when window is resized.
     */
    private void adjustComponentsForSize() {
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == registerBtn) {
            handleRegister();
        }
    }

    /**
     * Handles the registration process with comprehensive validation.
     */
    private void handleRegister() {
        messageLabel.setText(" ");
        messageLabel.setForeground(ColorScheme.DANGER);

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String confirmPass = new String(confirmPasswordField.getPassword()).trim();
        String ageGroup = (String) ageGroupCombo.getSelectedItem();
        boolean hasChildren = hasChildrenCheck.isSelected();
        String mealPreference = (String) mealPrefCombo.getSelectedItem();

        // Validation
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || 
            username.isEmpty() || password.isEmpty() || confirmPass.isEmpty()) {
            messageLabel.setText("Please fill in all fields");
            return;
        }

        if (name.length() < 2) {
            messageLabel.setText("Name must be at least 2 characters");
            return;
        }

        if (username.length() < 3) {
            messageLabel.setText("Username must be at least 3 characters");
            return;
        }

        if (password.length() < 6) {
            messageLabel.setText("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPass)) {
            messageLabel.setText("Passwords do not match");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocus();
            return;
        }

        if (!EmailValidator.isValidEmail(email)) {
            messageLabel.setText("Invalid email address format");
            emailField.requestFocus();
            emailField.selectAll();
            return;
        }

        if (!phone.matches("[0-9+\\-\\s()]+")) {
            messageLabel.setText("Invalid phone number format");
            phoneField.requestFocus();
            phoneField.selectAll();
            return;
        }

        try {
            // Check username exists
            for (User u : fbs.getUsers()) {
                if (u.getUsername().equalsIgnoreCase(username)) {
                    messageLabel.setText("Username '" + username + "' is already taken");
                    usernameField.requestFocus();
                    usernameField.selectAll();
                    return;
                }
            }

            // Check email exists
            for (Customer c : fbs.getCustomers()) {
                if (c.getEmail().equalsIgnoreCase(email)) {
                    messageLabel.setText("Email already registered");
                    emailField.requestFocus();
                    emailField.selectAll();
                    return;
                }
            }

            // Create Customer with all details
            int newCustId = fbs.getCustomers().size() + 1;
            Customer customer = new Customer(newCustId, name, phone, email);
            customer.setAgeGroup(ageGroup);
            customer.setHasChildren(hasChildren);
            customer.setMealPreference(mealPreference);
            fbs.addCustomer(customer);

            // Create User
            int newUserId = fbs.getUsers().size() + 1;
            User user = new User(newUserId, username, password, "CUSTOMER");
            user.setLinkedCustomerId(newCustId);
            fbs.addUser(user);

            // Save
         // Save to database using DAO
            try {
                FlightBookingSystemData.safeStore(fbs);
            } catch (Exception dbEx) {
                System.err.println("Database save failed: " + dbEx.getMessage());
                // Optional: fallback to file if you want
                // FlightBookingSystemData.safeStore(fbs); // already called
            }

            // Success message
            JOptionPane.showMessageDialog(this,
                    "Registration Successful!\n\n" +
                    "Welcome, " + name + "!\n\n" +
                    "Account Details:\n" +
                    "  Username: " + username + "\n" +
                    "  Email: " + email + "\n" +
                    "  Age Group: " + ageGroup + "\n" +
                    "  Meal Preference: " + mealPreference + "\n\n" +
                    "You can now login with your credentials.",
                    "Registration Complete",
                    JOptionPane.INFORMATION_MESSAGE);

            // Return to login
            if (parentLogin != null) {
                parentLogin.registrationComplete();
            }

            dispose();

        } catch (FlightBookingSystemException ex) {
            messageLabel.setText("Error: " + ex.getMessage());
        }
    }
}