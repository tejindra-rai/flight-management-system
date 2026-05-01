package bcu.cmp5332.bookingsystem.gui.customer;

import bcu.cmp5332.bookingsystem.gui.customer.CustomerUpdateBookingWindow;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.common.LoginWindow;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.AppIcon;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import bcu.cmp5332.bookingsystem.utils.ImageUtils;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.time.format.DateTimeFormatter;

/**
 * RESPONSIVE B & T Airlines Customer Portal - Adapts to all screen sizes
 * 
 * @author Tejindra Rai
 * @version 11.0 - Fully responsive with collapsible sidebar
 */
public class CustomerMainWindow extends JFrame {

    private FlightBookingSystem fbs;
    private User currentUser;
    private Customer customer;
    
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JPanel sidebar;
    private JButton menuToggleBtn;
    private boolean sidebarVisible = true;

    public CustomerMainWindow(FlightBookingSystem fbs, User user) {
        this.fbs = fbs;
        this.currentUser = user;
        
        try {
            this.customer = fbs.getCustomerByID(user.getLinkedCustomerId());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, 
                "Error loading customer profile: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        initialize();
    }

    private void initialize() {
        try {
            FlatLightLaf.setup();
            AppIcon.setFrameIcon(this);
        } catch (Exception ignored) {}

        setTitle("B & T Airlines - Customer Portal");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Responsive window sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1400, (int)(screenSize.width * 0.9));
        int height = Math.min(850, (int)(screenSize.height * 0.85));
        setSize(width, height);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);

        // Main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        // Navigation Bar
        JPanel navbar = createResponsiveNavbar();
        
        // Sidebar that can be shown/hidden
        sidebar = createSidebar();
        
        // Content Area with Scroll Support
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        showDashboard();

        // Layout
        mainPanel.add(navbar, BorderLayout.NORTH);
        mainPanel.add(sidebar, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Add component listener for responsive adjustments
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                adjustLayoutForSize();
            }
        });

        add(mainPanel);
        adjustLayoutForSize(); // Initial adjustment
        setVisible(true);
    }

    /**
     * Adjusts layout based on window size
     */
    private void adjustLayoutForSize() {
        int width = getWidth();
        
        // Auto-hide sidebar on small screens and show/hide menu button
        if (width < 900) {
            // Small screen - show menu button
            if (menuToggleBtn != null) {
                menuToggleBtn.setVisible(true);
            }
            if (sidebarVisible) {
                hideSidebar();
            }
        } else {
            // Large screen - hide menu button
            if (menuToggleBtn != null) {
                menuToggleBtn.setVisible(false);
            }
        }
    }

    /**
     * Toggle sidebar visibility
     */
    private void toggleSidebar() {
        if (sidebarVisible) {
            hideSidebar();
        } else {
            showSidebar();
        }
    }

    private void showSidebar() {
        sidebar.setVisible(true);
        sidebarVisible = true;
        if (menuToggleBtn != null) {
            menuToggleBtn.setText(FontAwesomeIcon.TIMES_CIRCLE + " Hide");
        }
        // Revalidate to adjust content area
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private void hideSidebar() {
        sidebar.setVisible(false);
        sidebarVisible = false;
        if (menuToggleBtn != null) {
            menuToggleBtn.setText(FontAwesomeIcon.BARS + " Menu");
        }
        // Revalidate to expand content area
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    /**
     * Creates responsive navigation bar
     */
    private JPanel createResponsiveNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(ColorScheme.PRIMARY_DARK);
        navbar.setPreferredSize(new Dimension(0, 70));
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ColorScheme.GOLD));

        // Left: Menu toggle + Logo and Title
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        // Menu toggle button
        menuToggleBtn = new JButton(FontAwesomeIcon.BARS + " Menu");
        menuToggleBtn.setFont(FontAwesomeIcon.getFont(13));
        menuToggleBtn.setForeground(Color.WHITE);
        menuToggleBtn.setBackground(ColorScheme.PRIMARY_MEDIUM);
        menuToggleBtn.setBorder(new EmptyBorder(12, 15, 12, 15));
        menuToggleBtn.setFocusPainted(false);
        menuToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuToggleBtn.addActionListener(e -> toggleSidebar());
        menuToggleBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuToggleBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.PRIMARY_MEDIUM));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuToggleBtn.setBackground(ColorScheme.PRIMARY_MEDIUM);
            }
        });
        
        JLabel logoIcon = new JLabel(FontAwesomeIcon.PLANE);
        logoIcon.setFont(FontAwesomeIcon.getFont(28));
        logoIcon.setForeground(ColorScheme.GOLD);
        logoIcon.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        JLabel titleLabel = new JLabel("B & T Airlines");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        leftPanel.add(menuToggleBtn);
        leftPanel.add(logoIcon);
        leftPanel.add(titleLabel);

        // Right: User info and logout
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        
        JLabel userIcon = new JLabel(FontAwesomeIcon.USER);
        userIcon.setFont(FontAwesomeIcon.getFont(16));
        userIcon.setForeground(ColorScheme.GOLD);
        userIcon.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        JLabel welcomeLabel = new JLabel(customer.getName());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        welcomeLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        welcomeLabel.setBorder(new EmptyBorder(19, 0, 19, 8));
        
        JButton logoutBtn = createNavButton(FontAwesomeIcon.SIGN_OUT + " Logout", ColorScheme.DANGER, e -> logout());
        
        rightPanel.add(userIcon);
        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutBtn);

        navbar.add(leftPanel, BorderLayout.WEST);
        navbar.add(rightPanel, BorderLayout.EAST);

        return navbar;
    }

    /**
     * Creates sidebar navigation
     */
    private JPanel createSidebar() {
        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(ColorScheme.CARD_BG);

        sidebarContent.add(Box.createVerticalStrut(20));
        
        // Navigation Items
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.HOME, "Home", ColorScheme.PRIMARY_MEDIUM, e -> showDashboard()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PLANE, "Browse Flights", ColorScheme.PRIMARY_LIGHT, e -> showFlights()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.CALENDAR, "My Bookings", ColorScheme.SUCCESS, e -> showMyBookings()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PLUS_CIRCLE, "Book Flight", ColorScheme.SUCCESS_LIGHT, e -> bookFlight()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.TIMES_CIRCLE, "Cancel Booking", ColorScheme.WARNING, e -> cancelBooking()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.EDIT, "Update Booking", ColorScheme.PRIMARY_LIGHT, e -> updateBooking()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.REFRESH, "Rebook Flight", ColorScheme.WARNING_LIGHT, e -> rebookFlight()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.STAR, "Give Feedback", ColorScheme.GOLD, e -> giveFeedback()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.USER, "My Profile", ColorScheme.PRIMARY_MEDIUM, e -> showProfile()));

        sidebarContent.add(Box.createVerticalGlue());

        // System date at bottom
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(ColorScheme.BACKGROUND);
        datePanel.setBorder(new EmptyBorder(12, 12, 12, 12));
        
        JLabel dateLabel = new JLabel("<html><center>System Date<br><b>" + 
            fbs.getSystemDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + 
            "</b></center></html>");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        dateLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        datePanel.add(dateLabel);
        
        sidebarContent.add(datePanel);
        sidebarContent.add(Box.createVerticalStrut(15));

        // Wrap in scroll pane
        JScrollPane sidebarScrollPane = new JScrollPane(sidebarContent);
        sidebarScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorScheme.BORDER_LIGHT));
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setPreferredSize(new Dimension(240, 0));
        sidebarWrapper.add(sidebarScrollPane, BorderLayout.CENTER);

        return sidebarWrapper;
    }

    private JButton createSidebarButton(String icon, String text, Color accentColor, java.awt.event.ActionListener listener) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(12, 0));
        btn.setMaximumSize(new Dimension(240, 45));
        btn.setPreferredSize(new Dimension(240, 45));
        btn.setBackground(ColorScheme.CARD_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BORDER_LIGHT),
            new EmptyBorder(8, 15, 8, 15)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(16));
        iconLabel.setForeground(accentColor);

        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        textLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        btn.add(iconLabel, BorderLayout.WEST);
        btn.add(textLabel, BorderLayout.CENTER);

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.HOVER_BG);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 1, 0, accentColor),
                    new EmptyBorder(8, 12, 8, 15)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.CARD_BG);
                btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BORDER_LIGHT),
                    new EmptyBorder(8, 15, 8, 15)
                ));
            }
        });

        btn.addActionListener(listener);
        return btn;
    }

    private JButton createNavButton(String text, Color bgColor, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(12));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(10, 18, 10, 18));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.getHoverColor(bgColor));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        btn.addActionListener(listener);
        return btn;
    }

    /**
     * RESPONSIVE: Shows dashboard with adaptive layout
     */
    public void showDashboard() {
        contentPanel.removeAll();
        
        int windowWidth = getWidth();
        int padding = windowWidth < 800 ? 15 : (windowWidth < 1000 ? 20 : 30);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(padding, padding, padding, padding));
        
        JPanel homePage = new JPanel();
        homePage.setLayout(new BoxLayout(homePage, BoxLayout.Y_AXIS));
        homePage.setOpaque(false);

        // Responsive Hero Banner
        JPanel heroWithImage = createResponsiveHeroBanner();
        heroWithImage.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(heroWithImage);
        homePage.add(Box.createVerticalStrut(25));

        // Responsive Statistics Cards
        JPanel statsPanel = createResponsiveStatsPanel();
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(statsPanel);
        homePage.add(Box.createVerticalStrut(25));

        // Destination Gallery
        JLabel galleryHeader = new JLabel("Popular Destinations");
        galleryHeader.setFont(new Font("Arial", Font.BOLD, windowWidth < 800 ? 20 : 24));
        galleryHeader.setForeground(ColorScheme.TEXT_PRIMARY);
        galleryHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(galleryHeader);
        homePage.add(Box.createVerticalStrut(12));

        JPanel destinationGallery = createDestinationGalleryWithScroll();
        destinationGallery.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(destinationGallery);
        homePage.add(Box.createVerticalStrut(25));

        // Features Section
        JLabel featuresHeader = new JLabel("Why Choose B & T Airlines?");
        featuresHeader.setFont(new Font("Arial", Font.BOLD, windowWidth < 800 ? 20 : 24));
        featuresHeader.setForeground(ColorScheme.TEXT_PRIMARY);
        featuresHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(featuresHeader);
        homePage.add(Box.createVerticalStrut(12));

        JPanel featuresPanel = createResponsiveFeaturesPanel();
        featuresPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(featuresPanel);
        homePage.add(Box.createVerticalStrut(25));

        // Quick Actions
        JLabel actionsHeader = new JLabel("Quick Actions");
        actionsHeader.setFont(new Font("Arial", Font.BOLD, windowWidth < 800 ? 20 : 24));
        actionsHeader.setForeground(ColorScheme.TEXT_PRIMARY);
        actionsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(actionsHeader);
        homePage.add(Box.createVerticalStrut(15));

        JPanel actionsPanel = createResponsiveActionsPanel();
        actionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        homePage.add(actionsPanel);
        homePage.add(Box.createVerticalStrut(25));

        wrapper.add(homePage, BorderLayout.NORTH);
        contentPanel.add(wrapper, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    /**
     * Creates responsive hero banner
     */
    private JPanel createResponsiveHeroBanner() {
        int windowWidth = getWidth();
        int heroHeight = windowWidth < 700 ? 300 : (windowWidth < 1000 ? 400 : 550);
        
        JPanel heroContainer = new JPanel(new BorderLayout()) {
            private ImageIcon heroImage = ImageUtils.loadScaledImage("A1.jpg", 1200, heroHeight);
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, ColorScheme.PRIMARY_DARK,
                    getWidth(), getHeight(), ColorScheme.PRIMARY_MEDIUM
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                if (heroImage != null && heroImage.getIconWidth() > 0) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(heroImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
                
                g2d.setColor(new Color(0, 0, 0, 100));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };

        heroContainer.setPreferredSize(new Dimension(1200, heroHeight));
        heroContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, heroHeight));

        // Responsive text
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        int textPadding = windowWidth < 700 ? 20 : (windowWidth < 1000 ? 30 : 50);
        textPanel.setBorder(new EmptyBorder(textPadding, textPadding, textPadding, textPadding));

        int titleSize = windowWidth < 700 ? 24 : (windowWidth < 1000 ? 32 : 42);
        int subtitleSize = windowWidth < 700 ? 14 : (windowWidth < 1000 ? 16 : 20);
        int promoSize = windowWidth < 700 ? 13 : (windowWidth < 1000 ? 15 : 18);

        JLabel welcomeLabel = new JLabel("Welcome Back, " + customer.getName() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, titleSize));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subLabel = new JLabel("Discover your next adventure");
        subLabel.setFont(new Font("Arial", Font.PLAIN, subtitleSize));
        subLabel.setForeground(new Color(255, 255, 255, 230));
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel promoLabel = new JLabel("→ Book now and enjoy exclusive benefits!");
        promoLabel.setFont(new Font("Arial", Font.BOLD, promoSize));
        promoLabel.setForeground(ColorScheme.GOLD);
        promoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(welcomeLabel);
        textPanel.add(Box.createVerticalStrut(15));
        textPanel.add(subLabel);
        textPanel.add(Box.createVerticalStrut(20));
        textPanel.add(promoLabel);

        heroContainer.add(textPanel, BorderLayout.WEST);
        
        return heroContainer;
    }

    /**
     * Creates responsive stats panel
     */
    private JPanel createResponsiveStatsPanel() {
        int windowWidth = getWidth();
        int columns = windowWidth < 700 ? 1 : (windowWidth < 900 ? 2 : 3);
        int rows = windowWidth < 700 ? 3 : (windowWidth < 900 ? 2 : 1);
        
        JPanel statsPanel = new JPanel(new GridLayout(rows, columns, 15, 15));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, windowWidth < 700 ? 450 : 140));

        int availableFlights = 0;
        for (Flight flight : fbs.getFlights()) {
            if (!flight.isDeleted() && flight.getDepartureDate().isAfter(fbs.getSystemDate())) {
                availableFlights++;
            }
        }

        int activeBookings = 0;
        for (Booking booking : customer.getBookings()) {
            if (!booking.isCancelled()) {
                activeBookings++;
            }
        }

        statsPanel.add(createStatCard("Available Flights", String.valueOf(availableFlights), 
                       ColorScheme.PRIMARY_MEDIUM, FontAwesomeIcon.PLANE, "Book your next journey"));
        statsPanel.add(createStatCard("My Bookings", String.valueOf(customer.getBookings().size()), 
                       ColorScheme.SUCCESS, FontAwesomeIcon.TICKET, "Total reservations"));
        statsPanel.add(createStatCard("Active Trips", String.valueOf(activeBookings), 
                       ColorScheme.WARNING, FontAwesomeIcon.CHECK, "Upcoming flights"));

        return statsPanel;
    }

    /**
     * Creates responsive features panel
     */
    private JPanel createResponsiveFeaturesPanel() {
        int windowWidth = getWidth();
        int columns = windowWidth < 600 ? 1 : (windowWidth < 900 ? 2 : 4);
        int rows = windowWidth < 600 ? 4 : (windowWidth < 900 ? 2 : 1);
        
        JPanel featuresPanel = new JPanel(new GridLayout(rows, columns, 12, 12));
        featuresPanel.setOpaque(false);
        int panelHeight = windowWidth < 600 ? 520 : (windowWidth < 900 ? 260 : 130);
        featuresPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelHeight));

        featuresPanel.add(createFeatureCard(FontAwesomeIcon.SHIELD, "Safe & Secure", 
                         "Your safety is our priority"));
        featuresPanel.add(createFeatureCard(FontAwesomeIcon.CLOCK, "On-Time Performance", 
                         "We value your time"));
        featuresPanel.add(createFeatureCard(FontAwesomeIcon.STAR, "Premium Service", 
                         "Excellence in every detail"));
        featuresPanel.add(createFeatureCard(FontAwesomeIcon.GLOBE, "Global Network", 
                         "Connecting the world"));

        return featuresPanel;
    }

    /**
     * Creates responsive actions panel
     */
    private JPanel createResponsiveActionsPanel() {
        int windowWidth = getWidth();
        int columns = windowWidth < 700 ? 1 : 2;
        int rows = windowWidth < 700 ? 4 : 2;
        
        JPanel actionsPanel = new JPanel(new GridLayout(rows, columns, 15, 15));
        actionsPanel.setOpaque(false);
        int panelHeight = windowWidth < 700 ? 480 : 240;
        actionsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelHeight));

        actionsPanel.add(createActionCard(FontAwesomeIcon.PLANE, "Browse Flights", 
                         "Explore available flights", ColorScheme.PRIMARY_MEDIUM, e -> showFlights()));
        actionsPanel.add(createActionCard(FontAwesomeIcon.PLUS_CIRCLE, "Book a Flight", 
                         "Make a new reservation", ColorScheme.SUCCESS, e -> bookFlight()));
        actionsPanel.add(createActionCard(FontAwesomeIcon.CALENDAR, "View Bookings", 
                         "Manage your reservations", ColorScheme.WARNING, e -> showMyBookings()));
        actionsPanel.add(createActionCard(FontAwesomeIcon.STAR, "Give Feedback", 
                         "Share your experience", ColorScheme.GOLD, e -> giveFeedback()));

        return actionsPanel;
    }

    private JPanel createDestinationGalleryWithScroll() {
        JPanel container = new JPanel(new BorderLayout());
        container.setOpaque(false);
        container.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

        JPanel gallery = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        gallery.setOpaque(false);

        String[] destinations = {"Pokhara", "Paris", "New York", "Tokyo", "London"};
        String[] images = {"PKH.jpg", "PRS.jpg", "USA.jpg", "TKY.jpg", "UK.jpg"};

        for (int i = 0; i < destinations.length; i++) {
            gallery.add(createDestinationCard(destinations[i], images[i]));
        }

        // Enable horizontal scrolling for additional destinations
        JScrollPane innerScrollPane = new JScrollPane(gallery);
        innerScrollPane.setBorder(null);
        innerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        innerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        innerScrollPane.setPreferredSize(new Dimension(1100, 250));
        innerScrollPane.getHorizontalScrollBar().setUnitIncrement(16);
        
        // Custom scrollbar styling for better visibility
        innerScrollPane.getHorizontalScrollBar().setBackground(new Color(240, 240, 240));
        innerScrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 12));

        // Mouse wheel listener for better scrolling
        innerScrollPane.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                if (e.isShiftDown()) {
                    // Horizontal scroll with SHIFT key
                    innerScrollPane.getHorizontalScrollBar().setValue(
                        innerScrollPane.getHorizontalScrollBar().getValue() + e.getUnitsToScroll() * 20
                    );
                } else {
                    // Pass to parent for vertical scrolling
                    scrollPane.dispatchEvent(SwingUtilities.convertMouseEvent(innerScrollPane, e, scrollPane));
                }
            }
        });

        container.add(innerScrollPane, BorderLayout.CENTER);
        
        // Add hint label for horizontal scrolling
        JLabel scrollHint = new JLabel("← Scroll for more destinations (or hold SHIFT + scroll) →");
        scrollHint.setFont(new Font("Arial", Font.ITALIC, 11));
        scrollHint.setForeground(ColorScheme.TEXT_SECONDARY);
        scrollHint.setHorizontalAlignment(SwingConstants.CENTER);
        scrollHint.setBorder(new EmptyBorder(8, 0, 0, 0));
        container.add(scrollHint, BorderLayout.SOUTH);
        
        return container;
    }

    private JPanel createDestinationCard(String destination, String imageName) {
        JPanel card = new JPanel(new BorderLayout()) {
            private ImageIcon image = ImageUtils.loadScaledImage(imageName, 230, 210);
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                
                Color color1 = ColorScheme.PRIMARY_MEDIUM;
                Color color2 = ColorScheme.PRIMARY_LIGHT;
                GradientPaint gradient = new GradientPaint(0, 0, color1, getWidth(), getHeight(), color2);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                if (image != null && image.getIconWidth() > 0) {
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.drawImage(image.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
                
                GradientPaint overlayGradient = new GradientPaint(
                    0, getHeight() - 70, new Color(0, 0, 0, 180),
                    0, getHeight(), new Color(0, 0, 0, 220)
                );
                g2d.setPaint(overlayGradient);
                g2d.fillRect(0, getHeight() - 70, getWidth(), 70);
                g2d.dispose();
            }
        };

        card.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setPreferredSize(new Dimension(230, 210));
        card.setMinimumSize(new Dimension(230, 210));
        card.setMaximumSize(new Dimension(230, 210));

        JLabel destLabel = new JLabel(destination, SwingConstants.CENTER);
        destLabel.setFont(new Font("Arial", Font.BOLD, 16));
        destLabel.setForeground(Color.WHITE);
        destLabel.setBorder(new EmptyBorder(0, 0, 18, 0));
        
        card.add(destLabel, BorderLayout.SOUTH);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(ColorScheme.GOLD, 3, true));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true));
            }
        });

        return card;
    }

    private JPanel createFeatureCard(String icon, String title, String description) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(ColorScheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(18, 12, 18, 12)
        ));

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(28));
        iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel descLabel = new JLabel("<html><center>" + description + "</center></html>");
        descLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        descLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(titleLabel);
        card.add(Box.createVerticalStrut(4));
        card.add(descLabel);

        return card;
    }

    private JPanel createStatCard(String title, String value, Color accentColor, String icon, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(15, 12));
        card.setBackground(ColorScheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new BorderLayout());
        iconPanel.setPreferredSize(new Dimension(70, 70));
        
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(accentColor);
        iconPanel.add(iconLabel);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        subtitleLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(valueLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(subtitleLabel);

        card.add(iconPanel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createActionCard(String icon, String title, String description, 
                                    Color accentColor, java.awt.event.ActionListener listener) {
        JPanel card = new JPanel(new BorderLayout(12, 8));
        card.setBackground(ColorScheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(18, 18, 18, 18)
        ));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(32));
        iconLabel.setForeground(accentColor);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        descLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(descLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listener.actionPerformed(null);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBackground(ColorScheme.HOVER_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(accentColor, 2, true),
                    new EmptyBorder(17, 17, 17, 17)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBackground(ColorScheme.CARD_BG);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
                    new EmptyBorder(18, 18, 18, 18)
                ));
            }
        });

        return card;
    }

    private void showFlights() {
        new BrowseFlightsWindow(fbs, currentUser);
    }

    private void showMyBookings() {
        JDialog bookingsDialog = new JDialog(this, "My Bookings", true);
        bookingsDialog.setSize(900, 600);
        bookingsDialog.setLocationRelativeTo(this);
        bookingsDialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 25));
        headerPanel.setBackground(ColorScheme.PRIMARY_DARK);
        
        JLabel iconLabel = new JLabel(FontAwesomeIcon.CALENDAR);
        iconLabel.setFont(FontAwesomeIcon.getFont(28));
        iconLabel.setForeground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("My Bookings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        String[] columns = {"Booking ID", "Flight No", "Origin", "Destination", "Date", "Price", "Status"};
        Object[][] data = new Object[customer.getBookings().size()][7];
        
        int i = 0;
        for (Booking booking : customer.getBookings()) {
            Flight flight = booking.getFlight();
            data[i][0] = "B-" + booking.hashCode();
            data[i][1] = flight.getFlightNumber();
            data[i][2] = flight.getOrigin();
            data[i][3] = flight.getDestination();
            data[i][4] = flight.getDepartureDate().toString();
            data[i][5] = "£" + String.format("%.2f", booking.getBookingPrice());
            data[i][6] = booking.isCancelled() ? "Cancelled" : "Active";
            i++;
        }

        JTable table = new JTable(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(ColorScheme.PRIMARY_DARK);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        JButton closeBtn = new JButton();
        JPanel btnContent = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        btnContent.setOpaque(false);
        
        JLabel btnIcon = new JLabel(FontAwesomeIcon.TIMES_CIRCLE);
        btnIcon.setFont(FontAwesomeIcon.getFont(14));
        btnIcon.setForeground(Color.WHITE);
        
        JLabel btnText = new JLabel("Close");
        btnText.setFont(new Font("Arial", Font.BOLD, 14));
        btnText.setForeground(Color.WHITE);
        
        btnContent.add(btnIcon);
        btnContent.add(btnText);
        
        closeBtn.setLayout(new BorderLayout());
        closeBtn.add(btnContent, BorderLayout.CENTER);
        closeBtn.setBackground(ColorScheme.DANGER);
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFocusPainted(false);
        closeBtn.setBorder(new EmptyBorder(12, 25, 12, 25));
        closeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> bookingsDialog.dispose());
        buttonPanel.add(closeBtn);

        bookingsDialog.add(headerPanel, BorderLayout.NORTH);
        bookingsDialog.add(tableScrollPane, BorderLayout.CENTER);
        bookingsDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        bookingsDialog.setVisible(true);
    }

    private void bookFlight() {
        SwingUtilities.invokeLater(() -> {
            new CustomerBookingWindow(fbs, currentUser, this);
        });
    }

    private void cancelBooking() {
        new CancelBookingWindow(fbs, currentUser);
    }

    private void updateBooking() {
        new CustomerUpdateBookingWindow(fbs, customer, this);
    }
    
    private void rebookFlight() {
        new RebookFlightWindow(fbs);
    }

    private void giveFeedback() {
        new AddFeedbackWindow(fbs);
    }

    private void showProfile() {
        new CustomerDetailsWindow(customer, fbs);
    }

    private void logout() {
        JDialog confirmDialog = new JDialog(this, "Confirm Logout", true);
        confirmDialog.setSize(500, 280);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 25));
        headerPanel.setBackground(ColorScheme.PRIMARY_DARK);
        headerPanel.setPreferredSize(new Dimension(500, 90));
        
        JLabel iconLabel = new JLabel(FontAwesomeIcon.SIGN_OUT);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        
        JLabel titleLabel = new JLabel("Confirm Logout");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(new EmptyBorder(35, 40, 35, 40));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        
        JLabel messageLabel = new JLabel("Are you sure you want to logout?");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 17));
        messageLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subMessageLabel = new JLabel("Your session will be ended.");
        subMessageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subMessageLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        subMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(12));
        messagePanel.add(subMessageLabel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton noBtn = new JButton();
        noBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
        noBtn.setBackground(ColorScheme.PRIMARY_MEDIUM);
        noBtn.setFocusPainted(false);
        noBtn.setBorderPainted(false);
        noBtn.setPreferredSize(new Dimension(160, 45));
        noBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel noIcon = new JLabel(FontAwesomeIcon.TIMES_CIRCLE);
        noIcon.setFont(FontAwesomeIcon.getFont(16));
        noIcon.setForeground(Color.WHITE);
        
        JLabel noText = new JLabel("No, Stay");
        noText.setFont(new Font("Arial", Font.BOLD, 15));
        noText.setForeground(Color.WHITE);
        
        noBtn.add(noIcon);
        noBtn.add(noText);
        
        noBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                noBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.PRIMARY_MEDIUM));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                noBtn.setBackground(ColorScheme.PRIMARY_MEDIUM);
            }
        });
        
        noBtn.addActionListener(e -> confirmDialog.dispose());
        
        JButton yesBtn = new JButton();
        yesBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
        yesBtn.setBackground(ColorScheme.DANGER);
        yesBtn.setFocusPainted(false);
        yesBtn.setBorderPainted(false);
        yesBtn.setPreferredSize(new Dimension(160, 45));
        yesBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel yesIcon = new JLabel(FontAwesomeIcon.CHECK);
        yesIcon.setFont(FontAwesomeIcon.getFont(16));
        yesIcon.setForeground(Color.WHITE);
        
        JLabel yesText = new JLabel("Yes, Logout");
        yesText.setFont(new Font("Arial", Font.BOLD, 15));
        yesText.setForeground(Color.WHITE);
        
        yesBtn.add(yesIcon);
        yesBtn.add(yesText);
        
        yesBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                yesBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.DANGER));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                yesBtn.setBackground(ColorScheme.DANGER);
            }
        });
        
        yesBtn.addActionListener(e -> {
            confirmDialog.dispose();
            try {
                FlightBookingSystemData.safeStore(fbs);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
            this.dispose();
            new LoginWindow(fbs);
        });
        
        buttonPanel.add(noBtn);
        buttonPanel.add(yesBtn);

        confirmDialog.add(headerPanel, BorderLayout.NORTH);
        confirmDialog.add(messagePanel, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmDialog.setVisible(true);
    }
}