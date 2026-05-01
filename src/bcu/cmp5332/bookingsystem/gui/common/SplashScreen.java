package bcu.cmp5332.bookingsystem.gui.common;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Modern responsive splash screen with FontAwesome icons.
 * Features improved responsiveness, smooth animations, and adaptive scaling.
 * 
 * @author Tejindra Rai
 * @version 3.0 - Enhanced responsive design with smooth animations
 */
public class SplashScreen extends JWindow {

    private JProgressBar progressBar;
    private JLabel statusLabel;
    private JLabel percentLabel;
    private static final int BASE_WIDTH = 700;
    private static final int BASE_HEIGHT = 500;
    
    // Animation variables
    private float iconOpacity = 0.0f;
    private Timer fadeInTimer;

    /**
     * Constructs a new responsive SplashScreen.
     */
    public SplashScreen() {
        initialize();
    }

    /**
     * Initialize the splash screen with responsive design.
     */
    private void initialize() {
        // Get screen dimensions for responsive sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(BASE_WIDTH, (int)(screenSize.width * 0.5));
        int height = Math.min(BASE_HEIGHT, (int)(screenSize.height * 0.6));
        
        setSize(width, height);
        setLayout(new BorderLayout());

        // Main panel with modern gradient background
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);

        // Center on screen
        setLocationRelativeTo(null);
        
        // Start fade-in animation
        startFadeInAnimation();
    }

    /**
     * Creates the main panel with responsive gradient background.
     */
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                
                // Enable anti-aliasing for smooth rendering
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                
                // Dynamic gradient based on panel size
                GradientPaint gradient = new GradientPaint(
                    0, 0, ColorScheme.PRIMARY_DARK,
                    getWidth(), getHeight(), ColorScheme.PRIMARY_LIGHT,
                    true
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Responsive decorative circles with proper scaling
                float scale = Math.min(getWidth() / (float)BASE_WIDTH, getHeight() / (float)BASE_HEIGHT);
                int circleSize1 = (int)(350 * scale);
                int circleSize2 = (int)(250 * scale);
                int circleSize3 = (int)(300 * scale);
                
                g2d.setColor(ColorScheme.withOpacity(ColorScheme.GOLD, 0.12));
                g2d.fillOval(-circleSize1/2, -circleSize1/2, circleSize1, circleSize1);
                g2d.fillOval(getWidth() - circleSize2/2, getHeight() - circleSize2/2, circleSize2, circleSize2);
                
                g2d.setColor(ColorScheme.withOpacity(Color.WHITE, 0.06));
                g2d.fillOval(getWidth()/2 - circleSize3/2, getHeight()/2 - circleSize3/2, circleSize3, circleSize3);
            }
        };
        
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        
        JPanel contentPanel = createContentPanel();
        mainPanel.add(contentPanel, gbc);

        return mainPanel;
    }

    /**
     * Creates the content panel with all splash screen elements.
     */
    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        contentPanel.setMaximumSize(new Dimension(600, 450));

        // Airline Icon with fade-in animation
        final JLabel airlineIcon = new JLabel(FontAwesomeIcon.PLANE, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, iconOpacity));
                super.paintComponent(g2d);
                g2d.dispose();
            }
        };
        
        // Responsive font sizing
        int iconSize = (int)(100 * getScaleFactor());
        airlineIcon.setFont(FontAwesomeIcon.getFont(iconSize));
        airlineIcon.setForeground(ColorScheme.GOLD);
        airlineIcon.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(airlineIcon);

        contentPanel.add(Box.createVerticalStrut(getScaledSize(25)));

        // Application title - responsive font
        JLabel titleLabel = new JLabel("B & T Airlines");
        int titleSize = (int)(48 * getScaleFactor());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, titleSize));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(titleLabel);

        contentPanel.add(Box.createVerticalStrut(getScaledSize(10)));

        // Subtitle
        JLabel subtitleLabel = new JLabel("Flight Booking System");
        int subtitleSize = (int)(22 * getScaleFactor());
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, subtitleSize));
        subtitleLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.92));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(subtitleLabel);

        contentPanel.add(Box.createVerticalStrut(getScaledSize(15)));

        // Version with icon
        JPanel versionPanel = createVersionPanel();
        contentPanel.add(versionPanel);

        contentPanel.add(Box.createVerticalStrut(getScaledSize(45)));

        // Progress section
        JPanel progressPanel = createProgressPanel();
        contentPanel.add(progressPanel);

        contentPanel.add(Box.createVerticalStrut(getScaledSize(50)));

        // Features section - responsive grid
        JPanel featuresPanel = createFeaturesPanel();
        contentPanel.add(featuresPanel);

        contentPanel.add(Box.createVerticalStrut(getScaledSize(30)));

        // Footer
        JLabel footerLabel = new JLabel("© 2026 B & T Airlines | Developed by Tejindra Rai");
        int footerSize = (int)(12 * getScaleFactor());
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, footerSize));
        footerLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.75));
        footerLabel.setAlignmentX(CENTER_ALIGNMENT);
        contentPanel.add(footerLabel);

        return contentPanel;
    }

    /**
     * Creates the version panel with icon.
     */
    private JPanel createVersionPanel() {
        JPanel versionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        versionPanel.setOpaque(false);
        versionPanel.setAlignmentX(CENTER_ALIGNMENT);
        
        int iconSize = (int)(16 * getScaleFactor());
        JLabel versionIcon = new JLabel(FontAwesomeIcon.STAR);
        versionIcon.setFont(FontAwesomeIcon.getFont(iconSize));
        versionIcon.setForeground(ColorScheme.GOLD);
        
        int textSize = (int)(16 * getScaleFactor());
        JLabel versionLabel = new JLabel("Version 3.0 - Enhanced");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, textSize));
        versionLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.88));
        
        versionPanel.add(versionIcon);
        versionPanel.add(versionLabel);
        
        return versionPanel;
    }

    /**
     * Creates the modern progress panel with percentage display.
     */
    private JPanel createProgressPanel() {
        JPanel progressPanel = new JPanel();
        progressPanel.setLayout(new BoxLayout(progressPanel, BoxLayout.Y_AXIS));
        progressPanel.setOpaque(false);
        progressPanel.setAlignmentX(CENTER_ALIGNMENT);

        // Progress bar with custom styling
        progressBar = new JProgressBar(0, 100) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background track
                g2d.setColor(ColorScheme.withOpacity(Color.WHITE, 0.25));
                g2d.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 20, 20));
                
                // Progress fill with gradient
                if (getValue() > 0) {
                    int progressWidth = (int) ((getValue() / 100.0) * getWidth());
                    GradientPaint gradient = new GradientPaint(
                        0, 0, ColorScheme.GOLD,
                        progressWidth, 0, ColorScheme.GOLD.brighter()
                    );
                    g2d.setPaint(gradient);
                    g2d.fill(new RoundRectangle2D.Float(0, 0, progressWidth, getHeight(), 20, 20));
                }
                
                g2d.dispose();
            }
        };
        
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(false);
        int barWidth = (int)(550 * getScaleFactor());
        int barHeight = (int)(14 * getScaleFactor());
        progressBar.setMaximumSize(new Dimension(barWidth, barHeight));
        progressBar.setPreferredSize(new Dimension(barWidth, barHeight));
        progressBar.setAlignmentX(CENTER_ALIGNMENT);
        progressBar.setOpaque(false);
        
        progressPanel.add(progressBar);
        progressPanel.add(Box.createVerticalStrut(getScaledSize(20)));

        // Percentage and status in one line
        JPanel statusContainer = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        statusContainer.setOpaque(false);
        statusContainer.setAlignmentX(CENTER_ALIGNMENT);
        
        int statusSize = (int)(16 * getScaleFactor());
        
        percentLabel = new JLabel("0%");
        percentLabel.setFont(new Font("Segoe UI", Font.BOLD, statusSize));
        percentLabel.setForeground(ColorScheme.GOLD);
        
        JLabel separator = new JLabel("•");
        separator.setFont(new Font("Segoe UI", Font.PLAIN, statusSize));
        separator.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.6));
        
        JLabel statusIcon = new JLabel(FontAwesomeIcon.REFRESH);
        statusIcon.setFont(FontAwesomeIcon.getFont((int)(15 * getScaleFactor())));
        statusIcon.setForeground(ColorScheme.GOLD);
        
        statusLabel = new JLabel("Initializing...");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, statusSize));
        statusLabel.setForeground(Color.WHITE);
        
        statusContainer.add(percentLabel);
        statusContainer.add(separator);
        statusContainer.add(statusIcon);
        statusContainer.add(statusLabel);
        
        progressPanel.add(statusContainer);

        return progressPanel;
    }

    /**
     * Creates the responsive features panel.
     */
    private JPanel createFeaturesPanel() {
        JPanel featuresPanel = new JPanel(new GridLayout(1, 3, 25, 0));
        featuresPanel.setOpaque(false);
        int panelWidth = (int)(600 * getScaleFactor());
        featuresPanel.setMaximumSize(new Dimension(panelWidth, getScaledSize(70)));
        featuresPanel.setAlignmentX(CENTER_ALIGNMENT);

        featuresPanel.add(createFeatureLabel(FontAwesomeIcon.SHIELD, "Secure"));
        featuresPanel.add(createFeatureLabel(FontAwesomeIcon.GLOBE, "Global"));
        featuresPanel.add(createFeatureLabel(FontAwesomeIcon.CHECK, "Reliable"));

        return featuresPanel;
    }

    /**
     * Creates a feature label with icon and text.
     */
    private JPanel createFeatureLabel(String icon, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        int iconSize = (int)(32 * getScaleFactor());
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(iconSize));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        int textSize = (int)(14 * getScaleFactor());
        JLabel textLabel = new JLabel(text, SwingConstants.CENTER);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, textSize));
        textLabel.setForeground(Color.WHITE);
        textLabel.setAlignmentX(CENTER_ALIGNMENT);

        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(getScaledSize(10)));
        panel.add(textLabel);

        return panel;
    }

    /**
     * Calculates scale factor for responsive sizing.
     */
    private float getScaleFactor() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        float widthScale = Math.min(1.0f, getWidth() / (float)BASE_WIDTH);
        float heightScale = Math.min(1.0f, getHeight() / (float)BASE_HEIGHT);
        return Math.min(widthScale, heightScale);
    }

    /**
     * Gets scaled size for responsive spacing.
     */
    private int getScaledSize(int baseSize) {
        return (int)(baseSize * getScaleFactor());
    }

    /**
     * Starts the fade-in animation for smooth appearance.
     */
    private void startFadeInAnimation() {
        fadeInTimer = new Timer(30, e -> {
            iconOpacity += 0.05f;
            if (iconOpacity >= 1.0f) {
                iconOpacity = 1.0f;
                fadeInTimer.stop();
            }
            repaint();
        });
        fadeInTimer.start();
    }

    /**
     * Updates the progress bar with smooth animation.
     * 
     * @param targetValue the target progress value (0-100)
     * @param status the status message
     */
    public void updateProgress(int targetValue, String status) {
        // Smooth progress animation
        Timer smoothProgressTimer = new Timer(10, null);
        smoothProgressTimer.addActionListener(e -> {
            int currentValue = progressBar.getValue();
            if (currentValue < targetValue) {
                progressBar.setValue(currentValue + 1);
                percentLabel.setText(progressBar.getValue() + "%");
            } else {
                smoothProgressTimer.stop();
            }
        });
        
        statusLabel.setText(status);
        smoothProgressTimer.start();
    }

    /**
     * Shows the splash screen with a realistic loading simulation.
     */
    public void showSplash() {
        setVisible(true);

        // Realistic loading simulation
        new Thread(() -> {
            try {
                updateProgress(0, "Starting up...");
                Thread.sleep(300);

                updateProgress(12, "Loading resources...");
                Thread.sleep(450);

                updateProgress(28, "Connecting to database...");
                Thread.sleep(500);

                updateProgress(45, "Loading flight data...");
                Thread.sleep(550);

                updateProgress(62, "Loading customer data...");
                Thread.sleep(450);

                updateProgress(78, "Loading bookings...");
                Thread.sleep(400);

                updateProgress(92, "Finalizing setup...");
                Thread.sleep(350);

                updateProgress(100, "Ready to fly!");
                Thread.sleep(700);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Closes the splash screen with fade-out effect.
     */
    public void closeSplash() {
        // Optional fade-out animation
        Timer fadeOutTimer = new Timer(20, null);
        fadeOutTimer.addActionListener(e -> {
            float opacity = getOpacity();
            if (opacity > 0.0f) {
                setOpacity(Math.max(0.0f, opacity - 0.05f));
            } else {
                fadeOutTimer.stop();
                setVisible(false);
                dispose();
            }
        });
        fadeOutTimer.start();
    }

    /**
     * Test method for the splash screen.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();

            // Auto-close after loading completes
            new Timer(4500, e -> {
                splash.closeSplash();
                System.exit(0);
            }).start();
        });
    }
}