package bcu.cmp5332.bookingsystem.gui.common;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.admin.AdminMainWindow;
import bcu.cmp5332.bookingsystem.gui.customer.CustomerMainWindow;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.AppIcon;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Responsive modern login window with adaptive layout.
 * Features responsive design that scales properly on different screen sizes.
 * 
 * @author Tejindra Rai
 * @version 6.0 - Enhanced responsive design with adaptive scaling
 */
public class LoginWindow extends JFrame implements ActionListener {

    private final FlightBookingSystem fbs;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginBtn;
    private JLabel messageLabel;
    
    private static final int MIN_WIDTH = 900;
    private static final int MIN_HEIGHT = 600;
    private static final int PREFERRED_WIDTH = 1200;
    private static final int PREFERRED_HEIGHT = 700;

    /**
     * Constructs a new responsive LoginWindow.
     */
    public LoginWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        initialize();
    }

    /**
     * Initialize the login window with responsive design.
     */
    private void initialize() {
        setTitle("B & T Airlines - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        
        // Calculate responsive window size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(PREFERRED_WIDTH, (int)(screenSize.width * 0.8));
        int height = Math.min(PREFERRED_HEIGHT, (int)(screenSize.height * 0.85));
        setSize(width, height);
        
        setLocationRelativeTo(null);

        try {
            FlatLightLaf.setup();
            AppIcon.setFrameIcon(this);
        } catch (Exception ignored) {}

        // Main responsive container
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints mainGbc = new GridBagConstraints();
        mainGbc.fill = GridBagConstraints.BOTH;
        mainGbc.weightx = 1.0;
        mainGbc.weighty = 1.0;

        // Split panel with responsive weights
        JPanel splitPanel = new JPanel(new GridBagLayout());
        
        // Left branding panel (40% width on large screens, 35% on smaller)
        GridBagConstraints leftGbc = new GridBagConstraints();
        leftGbc.gridx = 0;
        leftGbc.gridy = 0;
        leftGbc.fill = GridBagConstraints.BOTH;
        leftGbc.weightx = 0.4;
        leftGbc.weighty = 1.0;
        JPanel leftPanel = createResponsiveBrandingPanel();
        splitPanel.add(leftPanel, leftGbc);

        // Right form panel (60% width)
        GridBagConstraints rightGbc = new GridBagConstraints();
        rightGbc.gridx = 1;
        rightGbc.gridy = 0;
        rightGbc.fill = GridBagConstraints.BOTH;
        rightGbc.weightx = 0.6;
        rightGbc.weighty = 1.0;
        JPanel rightPanel = createResponsiveLoginFormPanel();
        splitPanel.add(rightPanel, rightGbc);

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
     * Creates responsive branding panel with gradient background.
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
                float scale = Math.min(getWidth() / 550f, getHeight() / 650f);
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

        // Airline icon - responsive size
        JLabel airlineIcon = new JLabel(FontAwesomeIcon.PLANE, SwingConstants.CENTER);
        airlineIcon.setFont(FontAwesomeIcon.getFont(getResponsiveSize(90)));
        airlineIcon.setForeground(ColorScheme.GOLD);
        airlineIcon.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(airlineIcon);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(35)));

        // Brand name - responsive font
        JLabel brandLabel = new JLabel("B & T Airlines");
        brandLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(46)));
        brandLabel.setForeground(Color.WHITE);
        brandLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(brandLabel);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));

        // Welcome message
        JLabel welcomeLabel = new JLabel("Nice to see you again");
        welcomeLabel.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(19)));
        welcomeLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.92));
        welcomeLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(welcomeLabel);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(12)));

        JLabel welcomeTitle = new JLabel("WELCOME BACK");
        welcomeTitle.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(38)));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(welcomeTitle);

        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(45)));

        // Features with responsive spacing
        contentPanel.add(createFeatureLabel(FontAwesomeIcon.CHECK, "Secure Login System"));
        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));
        contentPanel.add(createFeatureLabel(FontAwesomeIcon.PLANE, "Easy Flight Booking"));
        contentPanel.add(Box.createVerticalStrut(getResponsiveSize(18)));
        contentPanel.add(createFeatureLabel(FontAwesomeIcon.STAR, "Premium Service"));

        brandPanel.add(contentPanel, gbc);
        return brandPanel;
    }

    /**
     * Creates feature label with responsive sizing.
     */
    private JPanel createFeatureLabel(String icon, String text) {
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
     * Creates responsive login form panel.
     */
    private JPanel createResponsiveLoginFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setLayout(new GridBagLayout());
        
        // Responsive padding
        int padding = getResponsiveSize(60);
        formPanel.setBorder(new EmptyBorder(padding, padding, padding, padding));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(getResponsiveSize(12), 0, getResponsiveSize(12), 0);
        gbc.weightx = 1.0;

        int row = 0;

        // User icon at top
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(25), 0);
        JLabel userIconLabel = new JLabel(FontAwesomeIcon.USER, SwingConstants.CENTER);
        userIconLabel.setFont(FontAwesomeIcon.getFont(getResponsiveSize(55)));
        userIconLabel.setForeground(ColorScheme.PRIMARY_DARK);
        formPanel.add(userIconLabel, gbc);

        // Title
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(12), 0);
        JLabel titleLabel = new JLabel("Login Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(34)));
        titleLabel.setForeground(ColorScheme.PRIMARY_DARK);
        formPanel.add(titleLabel, gbc);

        gbc.insets = new Insets(getResponsiveSize(12), 0, getResponsiveSize(28), 0);
        gbc.gridy = row++;
        formPanel.add(Box.createVerticalStrut(getResponsiveSize(12)), gbc);

        // Username field
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        usernameLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        formPanel.add(usernameLabel, gbc);

        gbc.gridy = row++;
        usernameField = new JTextField(25);
        JPanel usernamePanel = createResponsiveInputPanel(usernameField, FontAwesomeIcon.USER);
        formPanel.add(usernamePanel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(18), 0, getResponsiveSize(6), 0);
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(15)));
        passwordLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        formPanel.add(passwordLabel, gbc);

        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(6), 0, getResponsiveSize(6), 0);
        passwordField = new JPasswordField(25);
        passwordField.addActionListener(this);
        JPanel passwordPanel = createResponsiveInputPanel(passwordField, FontAwesomeIcon.LOCK);
        formPanel.add(passwordPanel, gbc);

        // Forgot password link
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(8), 0, getResponsiveSize(6), 0);
        JLabel forgotPassword = new JLabel("<html><u>Forgot Password?</u></html>");
        forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(13)));
        forgotPassword.setForeground(ColorScheme.PRIMARY_MEDIUM);
        forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPassword.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(LoginWindow.this,
                    "Please contact the administrator to reset your password.",
                    "Password Recovery",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        });
        formPanel.add(forgotPassword, gbc);

        // Message label
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(18), 0);
        messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(ColorScheme.DANGER);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(14)));
        int msgHeight = getResponsiveSize(28);
        messageLabel.setPreferredSize(new Dimension(400, msgHeight));
        formPanel.add(messageLabel, gbc);

        // Login button
        gbc.gridy = row++;
        gbc.insets = new Insets(0, 0, getResponsiveSize(18), 0);
        loginBtn = createResponsiveStyledButton("LOG IN", ColorScheme.PRIMARY_DARK, e -> handleLogin());
        formPanel.add(loginBtn, gbc);

        // Register section
        gbc.gridy = row++;
        gbc.insets = new Insets(getResponsiveSize(12), 0, 0, 0);
        JPanel registerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        registerPanel.setBackground(Color.WHITE);
        
        JLabel notMemberLabel = new JLabel("Not a Member?");
        notMemberLabel.setFont(new Font("Segoe UI", Font.PLAIN, getResponsiveSize(14)));
        notMemberLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        
        JLabel joinNowLabel = new JLabel("<html><u>Join Now</u></html>");
        joinNowLabel.setFont(new Font("Segoe UI", Font.BOLD, getResponsiveSize(14)));
        joinNowLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);
        joinNowLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        joinNowLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterWindow(fbs, LoginWindow.this);
                setEnabled(false);
            }
        });
        
        registerPanel.add(notMemberLabel);
        registerPanel.add(joinNowLabel);
        formPanel.add(registerPanel, gbc);

        return formPanel;
    }

    /**
     * Creates responsive input panel with icon.
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
        // Force repaint of gradient backgrounds
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == passwordField) {
            handleLogin();
        }
    }

    /**
     * Handles login authentication and routing.
     */
    private void handleLogin() {
        messageLabel.setText(" ");
        messageLabel.setForeground(ColorScheme.DANGER);

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }

        try {
            User user = fbs.authenticate(username, password);

            if (user == null) {
                messageLabel.setText("Invalid username or password");
                passwordField.setText("");
                usernameField.requestFocus();
                usernameField.selectAll();
                return;
            }

            fbs.setCurrentUser(user);

            messageLabel.setForeground(ColorScheme.SUCCESS);
            messageLabel.setText("Login successful! Welcome, " + user.getUsername());

            Timer timer = new Timer(600, evt -> {
                dispose();
                
                if (user.isAdmin()) {
                    new AdminMainWindow(fbs);
                } else {
                    new CustomerMainWindow(fbs, user);
                }
            });
            timer.setRepeats(false);
            timer.start();

        } catch (Exception ex) {
            messageLabel.setText("Login failed: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Called from RegisterWindow after successful registration.
     */
    public void registrationComplete() {
        setEnabled(true);
        setVisible(true);
        messageLabel.setForeground(ColorScheme.SUCCESS);
        messageLabel.setText("Registration successful! You can now login");
        usernameField.requestFocus();
    }

    /**
     * Main method to launch the application.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();
                FlightBookingSystem fbs = FlightBookingSystemData.load();
                new LoginWindow(fbs);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to start application:\n" + ex.getMessage(),
                        "Startup Error",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}