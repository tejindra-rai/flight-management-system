package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.common.LoginWindow;
import bcu.cmp5332.bookingsystem.model.Booking;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.utils.AppIcon;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.DataExportImportUtil;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import com.formdev.flatlaf.FlatLightLaf;

// NEW IMPORTS FOR USER MANAGEMENT
import bcu.cmp5332.bookingsystem.dao.UserDAO;
import bcu.cmp5332.bookingsystem.dao.mysql.UserMySQLDAO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;

/**
 * B & T Airlines Admin Control Panel - Complete Edition
 * Includes all admin features: Dashboard, Analytics, Flight Management, 
 * Customer Management, Booking Management, Passenger Manifest, Feedback, Data Export,
 * Admin Profile, and User Management
 * 
 * @author Tejindra Rai
 * @version 6.0 - Added Admin Profile and User Management features
 */
public class AdminMainWindow extends JFrame implements ActionListener {

    private FlightBookingSystem fbs;
    private User currentUser;
    private JPanel contentPanel;
    private JScrollPane scrollPane;
    private JPanel sidebar;
    private JButton menuToggleBtn;
    private boolean sidebarVisible = true;
    
    // NEW: UserDAO instance for database operations
    private UserDAO userDAO;

    public AdminMainWindow(FlightBookingSystem fbs) {
        this.fbs = fbs;
        this.currentUser = fbs.getCurrentUser();
        this.userDAO = new UserMySQLDAO(); // Initialize UserDAO
        initialize();
    }

    private void initialize() {
        try {
            FlatLightLaf.setup();
            AppIcon.setFrameIcon(this);
        } catch (Exception ignored) {}

        setTitle("B & T Airlines - Admin Control Panel");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Responsive window sizing
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = Math.min(1400, (int)(screenSize.width * 0.9));
        int height = Math.min(850, (int)(screenSize.height * 0.85));
        setSize(width, height);
        setMinimumSize(new Dimension(600, 500));
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ColorScheme.BACKGROUND);

        JPanel navbar = createProfessionalNavbar();
        sidebar = createSidebar();
        
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ColorScheme.BACKGROUND);
        
        scrollPane = new JScrollPane(contentPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        showDashboard();

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

    private JPanel createProfessionalNavbar() {
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(ColorScheme.ADMIN_PRIMARY);
        navbar.setPreferredSize(new Dimension(0, 70));
        navbar.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, ColorScheme.GOLD));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);
        
        // Menu toggle button
        menuToggleBtn = new JButton(FontAwesomeIcon.BARS + " MENU");
        menuToggleBtn.setFont(FontAwesomeIcon.getFont(13));
        menuToggleBtn.setForeground(Color.WHITE);
        menuToggleBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
        menuToggleBtn.setBorder(new EmptyBorder(12, 15, 12, 15));
        menuToggleBtn.setFocusPainted(false);
        menuToggleBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        menuToggleBtn.addActionListener(e -> toggleSidebar());
        menuToggleBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                menuToggleBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.ADMIN_DARK));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                menuToggleBtn.setBackground(ColorScheme.ADMIN_PRIMARY);
            }
        });
        
        JLabel logoIcon = new JLabel(FontAwesomeIcon.SHIELD);
        logoIcon.setFont(FontAwesomeIcon.getFont(32));
        logoIcon.setForeground(ColorScheme.GOLD);
        logoIcon.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        JLabel titleLabel = new JLabel("B & T Airlines Admin");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        JLabel tagline = new JLabel("Complete Control Panel");
        tagline.setFont(new Font("Arial", Font.ITALIC, 12));
        tagline.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.8));
        tagline.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        leftPanel.add(menuToggleBtn);
        leftPanel.add(logoIcon);
        leftPanel.add(titleLabel);
        leftPanel.add(tagline);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        
        JLabel userIcon = new JLabel(FontAwesomeIcon.USER);
        userIcon.setFont(FontAwesomeIcon.getFont(18));
        userIcon.setForeground(ColorScheme.GOLD);
        userIcon.setBorder(new EmptyBorder(19, 0, 19, 0));
        
        JLabel welcomeLabel = new JLabel("Admin: " + currentUser.getUsername());
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 15));
        welcomeLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        welcomeLabel.setBorder(new EmptyBorder(19, 0, 19, 10));
        
        JButton logoutBtn = createNavButton(FontAwesomeIcon.SIGN_OUT + " Logout", ColorScheme.DANGER, e -> logout());
        
        rightPanel.add(userIcon);
        rightPanel.add(welcomeLabel);
        rightPanel.add(logoutBtn);

        navbar.add(leftPanel, BorderLayout.WEST);
        navbar.add(rightPanel, BorderLayout.EAST);

        return navbar;
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
            menuToggleBtn.setText(FontAwesomeIcon.BARS + " MENU");
        }
        // Revalidate to expand content area
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    private JPanel createSidebar() {
        JPanel sidebarContent = new JPanel();
        sidebarContent.setLayout(new BoxLayout(sidebarContent, BoxLayout.Y_AXIS));
        sidebarContent.setBackground(ColorScheme.CARD_BG);

        sidebarContent.add(Box.createVerticalStrut(30));
        
        // OVERVIEW
        addSectionLabel(sidebarContent, "OVERVIEW");
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.HOME, "Dashboard", ColorScheme.ADMIN_PRIMARY, e -> showDashboard()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.CHART_LINE, "Statistics", ColorScheme.INFO, e -> showStatistics()));

        sidebarContent.add(Box.createVerticalStrut(15));
        
        // FLIGHT MANAGEMENT
        addSectionLabel(sidebarContent, "FLIGHT MANAGEMENT");
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PLANE, "Browse Flights", ColorScheme.PRIMARY_LIGHT, e -> showFlights()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PLUS_CIRCLE, "Add Flight", ColorScheme.SUCCESS_LIGHT, e -> openAddFlight()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.TIMES_CIRCLE, "Delete Flight", ColorScheme.DANGER, e -> openDeleteFlight()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.INFO_CIRCLE, "Flight Status", ColorScheme.INFO, e -> openFlightStatus()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.USERS, "Passenger Manifest", ColorScheme.WARNING, e -> openPassengerManifest()));

        sidebarContent.add(Box.createVerticalStrut(15));
        
        // CUSTOMER MANAGEMENT
        addSectionLabel(sidebarContent, "CUSTOMER MANAGEMENT");
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.USER, "Browse Customers", ColorScheme.SUCCESS, e -> showCustomers()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PLUS_CIRCLE, "Add Customer", ColorScheme.SUCCESS_LIGHT, e -> openAddCustomer()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.TIMES_CIRCLE, "Delete Customer", ColorScheme.DANGER, e -> openDeleteCustomer()));

        sidebarContent.add(Box.createVerticalStrut(15));
        
        // BOOKING MANAGEMENT
        addSectionLabel(sidebarContent, "BOOKING MANAGEMENT");
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.TICKET, "All Bookings", ColorScheme.WARNING, e -> showBookings()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PLUS_CIRCLE, "Issue Booking", ColorScheme.WARNING_LIGHT, e -> openIssueBooking()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.EDIT, "Modify Booking", ColorScheme.INFO, e -> openModifyBooking()));

        sidebarContent.add(Box.createVerticalStrut(15));
        
        // FEEDBACK & DATA
        addSectionLabel(sidebarContent, "SYSTEM");
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.STAR, "View Feedback", ColorScheme.GOLD, e -> openViewFeedback()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.DOWNLOAD, "Export Data", ColorScheme.PRIMARY_LIGHT, e -> openDataExport()));

        sidebarContent.add(Box.createVerticalStrut(15));
        
        // NEW: ADMIN SECTION
        addSectionLabel(sidebarContent, "ADMIN");
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.PERSON, "My Profile", ColorScheme.ADMIN_PRIMARY, e -> showAdminProfile()));
        sidebarContent.add(createSidebarButton(FontAwesomeIcon.USERS, "User Management", ColorScheme.INFO, e -> showUserManagement()));

        sidebarContent.add(Box.createVerticalGlue());

        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(ColorScheme.BACKGROUND);
        datePanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel dateLabel = new JLabel("<html><center>System Date<br><b>" + 
            fbs.getSystemDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) + 
            "</b></center></html>");
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        dateLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        dateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        datePanel.add(dateLabel);
        
        sidebarContent.add(datePanel);
        sidebarContent.add(Box.createVerticalStrut(20));

        // Wrap the sidebar content in a JScrollPane
        JScrollPane sidebarScrollPane = new JScrollPane(sidebarContent);
        sidebarScrollPane.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, ColorScheme.BORDER_LIGHT));
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        // Create a wrapper panel to hold the scroll pane with fixed width
        JPanel sidebarWrapper = new JPanel(new BorderLayout());
        sidebarWrapper.setPreferredSize(new Dimension(270, 0));
        sidebarWrapper.add(sidebarScrollPane, BorderLayout.CENTER);

        return sidebarWrapper;
    }

    private void addSectionLabel(JPanel sidebar, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 11));
        label.setForeground(ColorScheme.TEXT_SECONDARY);
        label.setBorder(new EmptyBorder(10, 20, 5, 20));
        label.setMaximumSize(new Dimension(270, 30));
        sidebar.add(label);
    }

    private JButton createSidebarButton(String icon, String text, Color accentColor, java.awt.event.ActionListener listener) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout(15, 0));
        btn.setMaximumSize(new Dimension(270, 50));
        btn.setPreferredSize(new Dimension(270, 50));
        btn.setBackground(ColorScheme.CARD_BG);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, ColorScheme.BORDER_LIGHT),
            new EmptyBorder(10, 20, 10, 20)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setContentAreaFilled(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.addActionListener(listener);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(18));
        iconLabel.setForeground(accentColor);
        
        JLabel textLabel = new JLabel(text);
        textLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        textLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        
        JPanel contentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        contentPanel.setOpaque(false);
        contentPanel.add(iconLabel);
        contentPanel.add(textLabel);
        
        btn.add(contentPanel, BorderLayout.WEST);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.HOVER_BG);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.CARD_BG);
            }
        });
        
        return btn;
    }

    private JButton createNavButton(String text, Color bgColor, java.awt.event.ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(14));
        btn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorderPainted(false);
        btn.addActionListener(listener);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            Color original = bgColor;
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.getHoverColor(bgColor));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(original);
            }
        });
        
        return btn;
    }

    private void showDashboard() {
        contentPanel.removeAll();
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel dashboard = new JPanel();
        dashboard.setLayout(new BoxLayout(dashboard, BoxLayout.Y_AXIS));
        dashboard.setOpaque(false);

        JLabel welcomeHeader = new JLabel("Admin Dashboard");
        welcomeHeader.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeHeader.setForeground(ColorScheme.TEXT_PRIMARY);
        welcomeHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel subtitle = new JLabel("Comprehensive Analytics & Complete System Management");
        subtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitle.setForeground(ColorScheme.TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        dashboard.add(welcomeHeader);
        dashboard.add(Box.createVerticalStrut(5));
        dashboard.add(subtitle);
        dashboard.add(Box.createVerticalStrut(30));

        // KPI Cards
        dashboard.add(createKPICardsPanel());
        dashboard.add(Box.createVerticalStrut(25));

        // Charts Row
        JPanel chartsRow = new JPanel(new GridLayout(1, 2, 25, 0));
        chartsRow.setOpaque(false);
        chartsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        chartsRow.add(createBookingStatusPanel());
        chartsRow.add(createRevenueByRoutePanel());
        dashboard.add(chartsRow);
        dashboard.add(Box.createVerticalStrut(25));

        // Tables Row
        JPanel tablesRow = new JPanel(new GridLayout(1, 2, 25, 0));
        tablesRow.setOpaque(false);
        tablesRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        tablesRow.add(createUpcomingFlightsPanel());
        tablesRow.add(createTopCustomersPanel());
        dashboard.add(tablesRow);

        wrapper.add(dashboard, BorderLayout.NORTH);
        contentPanel.add(wrapper, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private JPanel createKPICardsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        DashboardMetrics metrics = calculateMetrics();

        panel.add(createKPICard(
            FontAwesomeIcon.CIRCLE,
            "Total Revenue",
            "£" + String.format("%,.2f", metrics.totalRevenue),
            ColorScheme.SUCCESS,
            metrics.activeBookings + " active bookings"
        ));

        panel.add(createKPICard(
            FontAwesomeIcon.TICKET,
            "Active Bookings",
            String.valueOf(metrics.activeBookings),
            ColorScheme.ADMIN_PRIMARY,
            metrics.totalBookings + " total bookings"
        ));

        panel.add(createKPICard(
            FontAwesomeIcon.USERS,
            "Total Customers",
            String.valueOf(metrics.totalCustomers),
            ColorScheme.WARNING,
            metrics.newCustomersThisMonth + " new this month"
        ));

        panel.add(createKPICard(
            FontAwesomeIcon.PLANE,
            "Avg Load Factor",
            String.format("%.1f%%", metrics.avgLoadFactor),
            ColorScheme.INFO,
            "Across all flights"
        ));

        return panel;
    }

    private JPanel createKPICard(String icon, String title, String value, Color color, String subtitle) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(15, 10));
        card.setBackground(ColorScheme.CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(color);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 28));
        valueLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        subtitleLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(5));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(3));
        contentPanel.add(subtitleLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(contentPanel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createBookingStatusPanel() {
        JPanel panel = createChartPanel("Booking Status Distribution");
        DashboardMetrics metrics = calculateMetrics();

        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth() - 60;
                int height = getHeight() - 40;
                int barWidth = 80;
                int spacing = 60;

                if (metrics.totalBookings > 0) {
                    // Active bookings bar
                    int activeHeight = (int) ((metrics.activeBookings / (double) metrics.totalBookings) * height);
                    g2d.setColor(ColorScheme.SUCCESS);
                    g2d.fillRoundRect(spacing, height - activeHeight + 20, barWidth, activeHeight, 8, 8);
                    g2d.setColor(ColorScheme.TEXT_PRIMARY);
                    g2d.setFont(new Font("Arial", Font.BOLD, 12));
                    g2d.drawString("Active", spacing + 15, height + 35);
                    g2d.drawString(String.valueOf(metrics.activeBookings), spacing + 25, height - activeHeight + 10);

                    // Cancelled bookings bar
                    int cancelledHeight = (int) ((metrics.cancelledBookings / (double) metrics.totalBookings) * height);
                    g2d.setColor(ColorScheme.DANGER);
                    g2d.fillRoundRect(spacing + barWidth + spacing, height - cancelledHeight + 20, barWidth, cancelledHeight, 8, 8);
                    g2d.setColor(ColorScheme.TEXT_PRIMARY);
                    g2d.drawString("Cancelled", spacing + barWidth + spacing + 5, height + 35);
                    g2d.drawString(String.valueOf(metrics.cancelledBookings),
                        spacing + barWidth + spacing + 25, height - cancelledHeight + 10);
                }
            }
        };
        chartArea.setPreferredSize(new Dimension(300, 180));
        chartArea.setBackground(ColorScheme.CARD_BG);

        panel.add(chartArea, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRevenueByRoutePanel() {
        JPanel panel = createChartPanel("Top 5 Routes by Revenue");
        Map<String, Double> routeRevenue = calculateRouteRevenue();
        List<Map.Entry<String, Double>> sortedRoutes = new ArrayList<>(routeRevenue.entrySet());
        sortedRoutes.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(ColorScheme.CARD_BG);
        listPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        int count = 0;
        for (Map.Entry<String, Double> entry : sortedRoutes) {
            if (count++ >= 5) break;

            JPanel routePanel = new JPanel(new BorderLayout(10, 0));
            routePanel.setOpaque(false);
            routePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            // Create a sub-panel for icon + route text
            JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
            leftPanel.setOpaque(false);

            JLabel iconLabel = new JLabel(FontAwesomeIcon.PLANE);
            iconLabel.setFont(FontAwesomeIcon.getFont(12));
            iconLabel.setForeground(ColorScheme.PRIMARY);

            JLabel routeLabel = new JLabel(entry.getKey());
            routeLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            leftPanel.add(iconLabel);
            leftPanel.add(routeLabel);

            JLabel revenueLabel = new JLabel("£" + String.format("%,.2f", entry.getValue()));
            revenueLabel.setFont(new Font("Arial", Font.BOLD, 12));
            revenueLabel.setForeground(ColorScheme.SUCCESS);

            routePanel.add(leftPanel, BorderLayout.WEST);
            routePanel.add(revenueLabel, BorderLayout.EAST);

            listPanel.add(routePanel);
            listPanel.add(Box.createVerticalStrut(8));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createUpcomingFlightsPanel() {
        JPanel panel = createChartPanel("Upcoming Flights (Next 7 Days)");
        List<Flight> upcomingFlights = new ArrayList<>();
        LocalDate today = fbs.getSystemDate();
        LocalDate weekFromNow = today.plusDays(7);

        for (Flight flight : fbs.getFlights()) {
            if (!flight.isDeleted() &&
                !flight.getDepartureDate().isBefore(today) &&
                !flight.getDepartureDate().isAfter(weekFromNow)) {
                upcomingFlights.add(flight);
            }
        }

        upcomingFlights.sort(Comparator.comparing(Flight::getDepartureDate));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(ColorScheme.CARD_BG);
        listPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        if (upcomingFlights.isEmpty()) {
            JLabel noFlightsLabel = new JLabel("No upcoming flights in the next 7 days");
            noFlightsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            noFlightsLabel.setForeground(ColorScheme.TEXT_SECONDARY);
            listPanel.add(noFlightsLabel);
        } else {
            for (Flight flight : upcomingFlights) {
                long daysAway = ChronoUnit.DAYS.between(today, flight.getDepartureDate());

                JPanel flightPanel = new JPanel(new BorderLayout(5, 0));
                flightPanel.setOpaque(false);
                flightPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));

                JLabel flightLabel = new JLabel(FontAwesomeIcon.PLANE + " " + flight.getFlightNumber() +
                    " (" + flight.getOrigin() + " → " + flight.getDestination() + ")");
                flightLabel.setFont(new Font("Arial", Font.PLAIN, 11));

                JLabel dateLabel = new JLabel(flight.getDepartureDate() + " (" + daysAway + " days)");
                dateLabel.setFont(new Font("Arial", Font.PLAIN, 11));
                dateLabel.setForeground(ColorScheme.TEXT_SECONDARY);

                flightPanel.add(flightLabel, BorderLayout.WEST);
                flightPanel.add(dateLabel, BorderLayout.EAST);

                listPanel.add(flightPanel);
                listPanel.add(Box.createVerticalStrut(5));
            }
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTopCustomersPanel() {
        JPanel panel = createChartPanel("Top Customers by Spending");
        Map<Customer, Double> customerSpending = new HashMap<>();

        for (Customer customer : fbs.getCustomers()) {
            if (customer.isDeleted()) continue;

            double totalSpent = 0;
            for (Booking booking : customer.getBookings()) {
                if (!booking.isCancelled()) {
                    totalSpent += booking.getBookingPrice();
                }
            }
            if (totalSpent > 0) {
                customerSpending.put(customer, totalSpent);
            }
        }

        List<Map.Entry<Customer, Double>> sortedCustomers = new ArrayList<>(customerSpending.entrySet());
        sortedCustomers.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(ColorScheme.CARD_BG);
        listPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        int rank = 1;
        for (Map.Entry<Customer, Double> entry : sortedCustomers) {
            if (rank > 10) break;

            JPanel customerPanel = new JPanel(new BorderLayout(10, 0));
            customerPanel.setOpaque(false);
            customerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

            String medal = rank == 1 ? "🥇" : rank == 2 ? "🥈" : rank == 3 ? "🥉" : rank + ".";
            JLabel nameLabel = new JLabel(medal + " " + entry.getKey().getName());
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));

            JLabel spendingLabel = new JLabel("£" + String.format("%,.2f", entry.getValue()));
            spendingLabel.setFont(new Font("Arial", Font.BOLD, 12));
            spendingLabel.setForeground(ColorScheme.GOLD);

            customerPanel.add(nameLabel, BorderLayout.WEST);
            customerPanel.add(spendingLabel, BorderLayout.EAST);

            listPanel.add(customerPanel);
            listPanel.add(Box.createVerticalStrut(8));
            rank++;
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createChartPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(ColorScheme.CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(ColorScheme.ADMIN_PRIMARY);

        panel.add(titleLabel, BorderLayout.NORTH);
        return panel;
    }

    private void showFlights() {
        new AdminBrowseFlightsWindow(fbs);
    }

    private void showCustomers() {
        new AdminBrowseCustomersWindow(fbs);
    }

    private void showBookings() {
        contentPanel.removeAll();
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        JLabel title = new JLabel("All Bookings");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        title.setBorder(new EmptyBorder(0, 0, 20, 0));

        int totalBookings = 0;
        for (Customer c : fbs.getCustomers()) {
            totalBookings += c.getBookings().size();
        }

        String[] columns = {"Customer", "Flight", "Route", "Date", "Price", "Status"};
        Object[][] data = new Object[totalBookings][6];
        
        int row = 0;
        for (Customer c : fbs.getCustomers()) {
            for (Booking b : c.getBookings()) {
                Flight f = b.getFlight();
                data[row][0] = c.getName();
                data[row][1] = f.getFlightNumber();
                data[row][2] = f.getOrigin() + " → " + f.getDestination();
                data[row][3] = f.getDepartureDate();
                data[row][4] = "£" + String.format("%.2f", b.getBookingPrice());
                data[row][5] = b.isCancelled() ? "Cancelled" : "Active";
                row++;
            }
        }

        JTable table = new JTable(data, columns);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setFillsViewportHeight(true);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(ColorScheme.WARNING);
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
        
        JScrollPane tableScrollPane = new JScrollPane(table);
        tableScrollPane.setBorder(new EmptyBorder(10, 0, 0, 0));

        panel.add(title, BorderLayout.NORTH);
        panel.add(tableScrollPane, BorderLayout.CENTER);

        wrapper.add(panel, BorderLayout.CENTER);
        contentPanel.add(wrapper, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showStatistics() {
        contentPanel.removeAll();
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(ColorScheme.BACKGROUND);
        wrapper.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel title = new JLabel("System Statistics");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(ColorScheme.TEXT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        title.setBorder(new EmptyBorder(0, 0, 30, 0));

        panel.add(title);

        JTextArea statsArea = new JTextArea(20, 60);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        statsArea.setEditable(false);
        statsArea.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        StringBuilder stats = new StringBuilder();
        stats.append("═══════════════════════════════════════════════════\n");
        stats.append("         B & T AIRLINES STATISTICS REPORT          \n");
        stats.append("═══════════════════════════════════════════════════\n\n");
        
        stats.append("FLIGHTS:\n");
        stats.append("   Total Flights: ").append(fbs.getFlights().size()).append("\n");
        int activeFlights = 0;
        for (Flight f : fbs.getFlights()) {
            if (!f.isDeleted()) activeFlights++;
        }
        stats.append("   Active Flights: ").append(activeFlights).append("\n\n");
        
        stats.append("CUSTOMERS:\n");
        stats.append("   Total Customers: ").append(fbs.getCustomers().size()).append("\n");
        stats.append("   Active Customers: ").append(fbs.getActiveCustomers().size()).append("\n\n");
        
        stats.append("BOOKINGS:\n");
        int total = 0, active = 0, cancelled = 0;
        for (Customer c : fbs.getCustomers()) {
            for (Booking b : c.getBookings()) {
                total++;
                if (b.isCancelled()) cancelled++;
                else active++;
            }
        }
        stats.append("   Total Bookings: ").append(total).append("\n");
        stats.append("   Active Bookings: ").append(active).append("\n");
        stats.append("   Cancelled Bookings: ").append(cancelled).append("\n\n");
        
        stats.append("USERS:\n");
        stats.append("   Total Users: ").append(fbs.getUsers().size()).append("\n");
        
        stats.append("\nFEEDBACK:\n");
        stats.append("   Total Reviews: ").append(fbs.getAllFeedback().size()).append("\n\n");
        
        stats.append("═══════════════════════════════════════════════════\n");
        stats.append("System Date: ").append(fbs.getSystemDate()).append("\n");
        stats.append("═══════════════════════════════════════════════════\n");
        
        statsArea.setText(stats.toString());
        
        JScrollPane scroll = new JScrollPane(statsArea);
        scroll.setBorder(BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(scroll);

        wrapper.add(panel, BorderLayout.CENTER);
        contentPanel.add(wrapper, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAdminProfile() {
        contentPanel.removeAll();

        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBackground(ColorScheme.BACKGROUND);
        profilePanel.setBorder(new EmptyBorder(40, 55, 40, 55));

        // HEADER Section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.BACKGROUND);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        headerPanel.setBorder(new EmptyBorder(0, 0, 35, 0));

        // Title with icon
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        titlePanel.setOpaque(false);

        JLabel titleIcon = new JLabel(FontAwesomeIcon.PERSON);
        titleIcon.setFont(FontAwesomeIcon.getFont(38));
        titleIcon.setForeground(ColorScheme.ADMIN_PRIMARY);

        JLabel titleLabel = new JLabel("My Admin Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        titlePanel.add(titleIcon);
        titlePanel.add(titleLabel);

        // Subtitle
        JLabel subtitleLabel = new JLabel("View and manage your administrator account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        subtitleLabel.setForeground(ColorScheme.TEXT_SECONDARY);

        JPanel subtitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        subtitlePanel.setOpaque(false);
        subtitlePanel.add(subtitleLabel);

        JPanel headerContent = new JPanel();
        headerContent.setLayout(new BoxLayout(headerContent, BoxLayout.Y_AXIS));
        headerContent.setOpaque(false);
        headerContent.add(titlePanel);
        headerContent.add(subtitlePanel);

        headerPanel.add(headerContent, BorderLayout.WEST);

        profilePanel.add(headerPanel);

        // MAIN PROFILE CARD
        JPanel profileCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Soft shadow effect
                g2d.setColor(new Color(0, 0, 0, 8));
                g2d.fillRoundRect(0, 4, getWidth(), getHeight() - 4, 16, 16);
                g2d.setColor(new Color(0, 0, 0, 6));
                g2d.fillRoundRect(0, 2, getWidth(), getHeight() - 2, 16, 16);
            }
        };
        profileCard.setLayout(new BoxLayout(profileCard, BoxLayout.Y_AXIS));
        profileCard.setBackground(ColorScheme.CARD_BG);
        profileCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(50, 60, 50, 60)
        ));
        profileCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Profile Icon with styled background
        JPanel iconPanel = new JPanel();
        iconPanel.setOpaque(false);
        iconPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        iconPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JPanel iconCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Outer glow
                for (int i = 0; i < 8; i++) {
                    int alpha = 8 - i;
                    g2d.setColor(new Color(
                        ColorScheme.ADMIN_PRIMARY.getRed(),
                        ColorScheme.ADMIN_PRIMARY.getGreen(),
                        ColorScheme.ADMIN_PRIMARY.getBlue(),
                        alpha * 3
                    ));
                    g2d.fillOval(15 - i, 15 - i, 120 + (i * 2), 120 + (i * 2));
                }

                // Main circle with gradient
                GradientPaint gradient = new GradientPaint(
                    20, 20, ColorScheme.withOpacity(ColorScheme.ADMIN_PRIMARY, 0.1),
                    140, 140, ColorScheme.withOpacity(ColorScheme.ADMIN_PRIMARY, 0.2)
                );
                g2d.setPaint(gradient);
                g2d.fillOval(15, 15, 120, 120);
                
                // Border ring
                g2d.setColor(ColorScheme.withOpacity(ColorScheme.ADMIN_PRIMARY, 0.3));
                g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2d.drawOval(15, 15, 120, 120);
            }
        };
        iconCircle.setOpaque(false);
        iconCircle.setPreferredSize(new Dimension(150, 150));
        iconCircle.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 35));

        JLabel profileIcon = new JLabel(FontAwesomeIcon.PERSON);
        profileIcon.setFont(FontAwesomeIcon.getFont(75));
        profileIcon.setForeground(ColorScheme.ADMIN_PRIMARY);

        iconCircle.add(profileIcon);
        iconPanel.add(iconCircle);

        profileCard.add(iconPanel);
        profileCard.add(Box.createVerticalStrut(35));

        // ACCOUNT DETAILS SECTION
        JPanel detailsSection = createProfileSection("Account Details");
        detailsSection.add(createEnhancedProfileField("User ID", String.valueOf(currentUser.getId()), ColorScheme.INFO));
        detailsSection.add(createEnhancedProfileField("Username", currentUser.getUsername(), ColorScheme.PRIMARY));
        detailsSection.add(createEnhancedProfileField("Role", currentUser.getRole(), ColorScheme.ADMIN_PRIMARY));
        detailsSection.add(createEnhancedProfileField("Account Type", "Administrator", ColorScheme.GOLD));

        profileCard.add(detailsSection);
        profileCard.add(Box.createVerticalStrut(35));

        // Elegant divider
        JPanel divider = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(0, 0, 0, 0),
                    getWidth() / 2, 0, ColorScheme.BORDER_MEDIUM,
                    true
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        divider.setOpaque(false);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        divider.setPreferredSize(new Dimension(800, 2));
        
        profileCard.add(divider);
        profileCard.add(Box.createVerticalStrut(35));

        // STATISTICS SECTION
        DashboardMetrics metrics = calculateMetrics();

        JPanel statsSection = createProfileSection("Your Statistics");

        // Statistics Grid - FIXED SIZING
        JPanel statsGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        statsGrid.setOpaque(false);
        statsGrid.setPreferredSize(new Dimension(900, 360));
        statsGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 360));

        statsGrid.add(createStatCard(FontAwesomeIcon.MONEY_BILL, "System Revenue",
            String.format("£%.2f", metrics.totalRevenue), ColorScheme.SUCCESS));
        statsGrid.add(createStatCard(FontAwesomeIcon.TICKET, "Total Bookings",
            String.valueOf(metrics.totalBookings), ColorScheme.WARNING));
        statsGrid.add(createStatCard(FontAwesomeIcon.USERS, "Active Customers",
            String.valueOf(metrics.totalCustomers), ColorScheme.INFO));
        statsGrid.add(createStatCard(FontAwesomeIcon.PLANE, "Total Flights",
            String.valueOf(fbs.getFlights().size()), ColorScheme.ADMIN_PRIMARY));

        statsSection.add(statsGrid);
        profileCard.add(statsSection);
        profileCard.add(Box.createVerticalStrut(20));

        profilePanel.add(profileCard);
        profilePanel.add(Box.createVerticalStrut(30));

        // ACTION BUTTONS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JButton changePasswordBtn = createProfileActionButton(
            FontAwesomeIcon.LOCK,
            "Change Password",
            ColorScheme.WARNING,
            e -> showChangePasswordDialog()
        );

        JButton refreshBtn = createProfileActionButton(
            FontAwesomeIcon.REFRESH,
            "Refresh Stats",
            ColorScheme.INFO,
            e -> showAdminProfile()
        );

        buttonPanel.add(changePasswordBtn);
        buttonPanel.add(refreshBtn);

        profilePanel.add(buttonPanel);
        profilePanel.add(Box.createVerticalGlue());

        contentPanel.add(profilePanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();

        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }


    private JPanel createProfileSection(String sectionTitle) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setOpaque(false);
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // Section header
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel headerIcon = new JLabel(FontAwesomeIcon.CIRCLE);
        headerIcon.setFont(FontAwesomeIcon.getFont(12));
        headerIcon.setForeground(ColorScheme.ADMIN_PRIMARY);

        JLabel headerLabel = new JLabel(sectionTitle);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setForeground(ColorScheme.TEXT_PRIMARY);

        headerPanel.add(headerIcon);
        headerPanel.add(headerLabel);

        section.add(headerPanel);
        section.add(Box.createVerticalStrut(22));

        return section;
    }

    private JPanel createEnhancedProfileField(String label, String value, Color accentColor) {
        JPanel fieldPanel = new JPanel(new BorderLayout(20, 0));
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        fieldPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Label section
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        labelPanel.setOpaque(false);

        JLabel iconLabel = new JLabel(FontAwesomeIcon.CIRCLE);
        iconLabel.setFont(FontAwesomeIcon.getFont(10));
        iconLabel.setForeground(accentColor);

        JLabel labelText = new JLabel(label + ":");
        labelText.setFont(new Font("Arial", Font.BOLD, 16));
        labelText.setForeground(ColorScheme.TEXT_SECONDARY);
        labelText.setPreferredSize(new Dimension(190, 30));

        labelPanel.add(iconLabel);
        labelPanel.add(labelText);

        // Value section with custom painting
        JPanel valuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, ColorScheme.withOpacity(accentColor, 0.06),
                    getWidth(), getHeight(), ColorScheme.withOpacity(accentColor, 0.12)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        valuePanel.setOpaque(false);
        valuePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.withOpacity(accentColor, 0.25), 1, true),
            new EmptyBorder(8, 18, 8, 18)
        ));

        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Arial", Font.BOLD, 16));
        valueText.setForeground(ColorScheme.TEXT_PRIMARY);

        valuePanel.add(valueText);

        fieldPanel.add(labelPanel, BorderLayout.WEST);
        fieldPanel.add(valuePanel, BorderLayout.CENTER);

        return fieldPanel;
    }

    private JPanel createStatCard(String icon, String label, String value, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, ColorScheme.withOpacity(color, 0.08),
                    getWidth(), getHeight(), ColorScheme.withOpacity(color, 0.15)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                
                // Subtle inner glow
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillRoundRect(1, 1, getWidth() - 2, getHeight() / 2, 14, 14);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.withOpacity(color, 0.3), 2, true),
            new EmptyBorder(24, 26, 24, 26)
        ));

        // Icon with circular background
        JPanel iconWrapper = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Outer glow
                for (int i = 0; i < 4; i++) {
                    g2d.setColor(ColorScheme.withOpacity(color, (4 - i) * 0.02));
                    g2d.fillOval(8 - i, 2 - i, 56 + (i * 2), 56 + (i * 2));
                }
                
                // Main circle
                g2d.setColor(ColorScheme.withOpacity(color, 0.2));
                g2d.fillOval(8, 2, 56, 56);
            }
        };
        iconWrapper.setOpaque(false);
        iconWrapper.setPreferredSize(new Dimension(72, 60));
        iconWrapper.setMaximumSize(new Dimension(72, 60));
        iconWrapper.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 14));
        iconWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(32));
        iconLabel.setForeground(color);
        
        iconWrapper.add(iconLabel);

        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
        valueLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Label
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Arial", Font.PLAIN, 14));
        labelText.setForeground(ColorScheme.TEXT_SECONDARY);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(iconWrapper);
        card.add(Box.createVerticalStrut(14));
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(labelText);

        return card;
    }

    private JButton createProfileActionButton(String icon, String text, Color bgColor, ActionListener listener) {
        JButton btn = new JButton("  " + icon + "  " + text.toUpperCase()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Button shadow
                g2d.setColor(new Color(0, 0, 0, 15));
                g2d.fillRoundRect(0, 3, getWidth(), getHeight() - 3, 12, 12);
                
                // Button background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight() - 3, 12, 12);
                
                super.paintComponent(g);
            }
        };
        
        btn.setFont(FontAwesomeIcon.getFont(15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(18, 35, 18, 35));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(ColorScheme.getHoverColor(bgColor));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });

        return btn;
    }

    private JPanel createProfileField(String label, String value) {
        JPanel fieldPanel = new JPanel(new BorderLayout(10, 0));
        fieldPanel.setOpaque(false);
        fieldPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        fieldPanel.setBorder(new EmptyBorder(8, 0, 8, 0));
        
        JLabel labelText = new JLabel(label + ":");
        labelText.setFont(new Font("Arial", Font.BOLD, 14));
        labelText.setForeground(ColorScheme.TEXT_SECONDARY);
        labelText.setPreferredSize(new Dimension(200, 25));
        
        JLabel valueText = new JLabel(value);
        valueText.setFont(new Font("Arial", Font.PLAIN, 14));
        valueText.setForeground(ColorScheme.TEXT_PRIMARY);
        
        fieldPanel.add(labelText, BorderLayout.WEST);
        fieldPanel.add(valueText, BorderLayout.CENTER);
        
        return fieldPanel;
    }


 private void showChangePasswordDialog() {
     JDialog dialog = new JDialog(this, "Change Password", true);
     dialog.setSize(600, 580);
     dialog.setLocationRelativeTo(this);
     dialog.setLayout(new BorderLayout());
     dialog.setResizable(false);
     
     // HEADER PANEL
     JPanel headerPanel = new JPanel();
     headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
     headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
     headerPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
     
     JLabel iconLabel = new JLabel(FontAwesomeIcon.LOCK);
     iconLabel.setFont(FontAwesomeIcon.getFont(52));
     iconLabel.setForeground(ColorScheme.GOLD);
     iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel titleLabel = new JLabel("Change Your Password");
     titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
     titleLabel.setForeground(Color.WHITE);
     titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel subtitleLabel = new JLabel("Enter your current password and choose a new one");
     subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 13));
     subtitleLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.9));
     subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     headerPanel.add(iconLabel);
     headerPanel.add(Box.createVerticalStrut(15));
     headerPanel.add(titleLabel);
     headerPanel.add(Box.createVerticalStrut(8));
     headerPanel.add(subtitleLabel);
     
     // SCROLLABLE CONTENT PANEL
     JPanel contentPanel = new JPanel();
     contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
     contentPanel.setBorder(new EmptyBorder(35, 50, 35, 50));
     contentPanel.setBackground(Color.WHITE);
     
     // Password fields
     JPasswordField currentPasswordField = new JPasswordField();
     JPasswordField newPasswordField = new JPasswordField();
     JPasswordField confirmPasswordField = new JPasswordField();
     
     Font fieldFont = new Font("Arial", Font.PLAIN, 15);
     Color borderColor = new Color(200, 200, 200);
     Color focusBorderColor = ColorScheme.ADMIN_PRIMARY;
     
     // Style all fields the same
     currentPasswordField.setFont(fieldFont);
     currentPasswordField.setPreferredSize(new Dimension(460, 42));
     currentPasswordField.setMaximumSize(new Dimension(460, 42));
     currentPasswordField.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLineBorder(borderColor, 1, true),
         new EmptyBorder(10, 15, 10, 15)
     ));
     
     newPasswordField.setFont(fieldFont);
     newPasswordField.setPreferredSize(new Dimension(460, 42));
     newPasswordField.setMaximumSize(new Dimension(460, 42));
     newPasswordField.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLineBorder(borderColor, 1, true),
         new EmptyBorder(10, 15, 10, 15)
     ));
     
     confirmPasswordField.setFont(fieldFont);
     confirmPasswordField.setPreferredSize(new Dimension(460, 42));
     confirmPasswordField.setMaximumSize(new Dimension(460, 42));
     confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
         BorderFactory.createLineBorder(borderColor, 1, true),
         new EmptyBorder(10, 15, 10, 15)
     ));
     
     // Add focus highlights
     addPasswordFieldFocusHighlight(currentPasswordField, borderColor, focusBorderColor);
     addPasswordFieldFocusHighlight(newPasswordField, borderColor, focusBorderColor);
     addPasswordFieldFocusHighlight(confirmPasswordField, borderColor, focusBorderColor);
     
     // Add fields with CENTER alignment
     contentPanel.add(createCenteredPasswordField("Current Password:", currentPasswordField));
     contentPanel.add(Box.createVerticalStrut(20));
     contentPanel.add(createCenteredPasswordField("New Password:", newPasswordField));
     contentPanel.add(Box.createVerticalStrut(20));
     contentPanel.add(createCenteredPasswordField("Confirm New Password:", confirmPasswordField));
     contentPanel.add(Box.createVerticalStrut(15));
     
     // Password hint - CENTERED
     JPanel hintPanel = new JPanel();
     hintPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
     hintPanel.setOpaque(false);
     hintPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
     
     JLabel hintIcon = new JLabel(FontAwesomeIcon.INFO_CIRCLE);
     hintIcon.setFont(FontAwesomeIcon.getFont(13));
     hintIcon.setForeground(ColorScheme.INFO);
     
     JLabel hintText = new JLabel("Password must be at least 4 characters long");
     hintText.setFont(new Font("Arial", Font.ITALIC, 12));
     hintText.setForeground(ColorScheme.TEXT_SECONDARY);
     
     hintPanel.add(hintIcon);
     hintPanel.add(hintText);
     contentPanel.add(hintPanel);
     
     // Wrap content in scroll pane
     JScrollPane scrollPane = new JScrollPane(contentPanel);
     scrollPane.setBorder(null);
     scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
     scrollPane.getVerticalScrollBar().setUnitIncrement(16);
     
     // BUTTON PANEL
     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 20));
     buttonPanel.setBackground(Color.WHITE);
     buttonPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
     
     // Save Button
     JButton saveBtn = new JButton("  " + FontAwesomeIcon.CHECK + "  SAVE PASSWORD");
     saveBtn.setFont(FontAwesomeIcon.getFont(14));
     saveBtn.setBackground(ColorScheme.SUCCESS);
     saveBtn.setForeground(Color.WHITE);
     saveBtn.setFocusPainted(false);
     saveBtn.setBorderPainted(false);
     saveBtn.setPreferredSize(new Dimension(200, 48));
     saveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
     
     saveBtn.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseEntered(java.awt.event.MouseEvent evt) {
             saveBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.SUCCESS));
         }
         public void mouseExited(java.awt.event.MouseEvent evt) {
             saveBtn.setBackground(ColorScheme.SUCCESS);
         }
     });
     
     saveBtn.addActionListener(e -> {
         String currentPass = new String(currentPasswordField.getPassword());
         String newPass = new String(newPasswordField.getPassword());
         String confirmPass = new String(confirmPasswordField.getPassword());
         
         if (currentPass.trim().isEmpty() || newPass.trim().isEmpty() || confirmPass.trim().isEmpty()) {
             showPasswordErrorDialog(dialog, "All fields are required!", "Validation Error");
             return;
         }
         
         if (!currentUser.validatePassword(currentPass)) {
             showPasswordErrorDialog(dialog, "Current password is incorrect!", "Authentication Failed");
             currentPasswordField.setText("");
             currentPasswordField.requestFocus();
             return;
         }
         
         if (newPass.length() < 4) {
             showPasswordErrorDialog(dialog, "New password must be at least 4 characters long!", "Password Too Short");
             newPasswordField.setText("");
             confirmPasswordField.setText("");
             newPasswordField.requestFocus();
             return;
         }
         
         if (!newPass.equals(confirmPass)) {
             showPasswordErrorDialog(dialog, "New passwords do not match!", "Password Mismatch");
             confirmPasswordField.setText("");
             confirmPasswordField.requestFocus();
             return;
         }
         
         if (currentPass.equals(newPass)) {
             showPasswordErrorDialog(dialog, "New password must be different from current password!", "Same Password");
             newPasswordField.setText("");
             confirmPasswordField.setText("");
             newPasswordField.requestFocus();
             return;
         }
         
         try {
             currentUser.setPassword(newPass);
             userDAO.updateUser(currentUser);
             showPasswordSuccessDialog(dialog, "Your password has been changed successfully!", "Success");
             dialog.dispose();
         } catch (Exception ex) {
             showPasswordErrorDialog(dialog, "Error changing password: " + ex.getMessage(), "Database Error");
         }
     });
     
     // Cancel Button
     JButton cancelBtn = new JButton("  " + FontAwesomeIcon.TIMES_CIRCLE + "  CANCEL");
     cancelBtn.setFont(FontAwesomeIcon.getFont(14));
     cancelBtn.setBackground(ColorScheme.DANGER);
     cancelBtn.setForeground(Color.WHITE);
     cancelBtn.setFocusPainted(false);
     cancelBtn.setBorderPainted(false);
     cancelBtn.setPreferredSize(new Dimension(200, 48));
     cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
     
     cancelBtn.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseEntered(java.awt.event.MouseEvent evt) {
             cancelBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.DANGER));
         }
         public void mouseExited(java.awt.event.MouseEvent evt) {
             cancelBtn.setBackground(ColorScheme.DANGER);
         }
     });
     
     cancelBtn.addActionListener(e -> dialog.dispose());
     
     buttonPanel.add(saveBtn);
     buttonPanel.add(cancelBtn);
     
     // Assemble dialog
     dialog.add(headerPanel, BorderLayout.NORTH);
     dialog.add(scrollPane, BorderLayout.CENTER);
     dialog.add(buttonPanel, BorderLayout.SOUTH);
     
     dialog.setVisible(true);
 }


 private JPanel createCenteredPasswordField(String labelText, JPasswordField field) {
     JPanel panel = new JPanel();
     panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
     panel.setOpaque(false);
     panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
     
     // Label - CENTERED
     JLabel label = new JLabel(labelText);
     label.setFont(new Font("Arial", Font.BOLD, 14));
     label.setForeground(ColorScheme.TEXT_PRIMARY);
     label.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     // Field - CENTERED
     field.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     panel.add(label);
     panel.add(Box.createVerticalStrut(8));
     panel.add(field);
     
     return panel;
 }

 
 private JPanel createLabeledPasswordField(String labelText, JPasswordField field) {
     JPanel panel = new JPanel();
     panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
     panel.setOpaque(false);
     panel.setMaximumSize(new Dimension(450, 70));
     
     JLabel label = new JLabel(labelText);
     label.setFont(new Font("Arial", Font.BOLD, 14));
     label.setForeground(ColorScheme.TEXT_PRIMARY);
     label.setAlignmentX(Component.LEFT_ALIGNMENT);
     
     field.setAlignmentX(Component.LEFT_ALIGNMENT);
     
     panel.add(label);
     panel.add(Box.createVerticalStrut(8));
     panel.add(field);
     
     return panel;
 }

 private void addPasswordFieldFocusHighlight(JPasswordField field, Color normalColor, Color focusColor) {
     field.addFocusListener(new java.awt.event.FocusAdapter() {
         public void focusGained(java.awt.event.FocusEvent evt) {
             field.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createLineBorder(focusColor, 2, true),
                 new EmptyBorder(10, 15, 10, 15)
             ));
         }
         public void focusLost(java.awt.event.FocusEvent evt) {
             field.setBorder(BorderFactory.createCompoundBorder(
                 BorderFactory.createLineBorder(normalColor, 1, true),
                 new EmptyBorder(10, 15, 10, 15)
             ));
         }
     });
 }

 private void showPasswordErrorDialog(JDialog parent, String message, String title) {
     JDialog errorDialog = new JDialog(parent, title, true);
     errorDialog.setSize(440, 240);
     errorDialog.setLocationRelativeTo(parent);
     errorDialog.setLayout(new BorderLayout());
     errorDialog.setResizable(false);
     
     JPanel contentPanel = new JPanel();
     contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
     contentPanel.setBackground(Color.WHITE);
     contentPanel.setBorder(new EmptyBorder(35, 35, 25, 35));
     
     JLabel icon = new JLabel(FontAwesomeIcon.EXCLAMATION_CIRCLE);
     icon.setFont(FontAwesomeIcon.getFont(60));
     icon.setForeground(ColorScheme.DANGER);
     icon.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel titleLabel = new JLabel(title);
     titleLabel.setFont(new Font("Arial", Font.BOLD, 19));
     titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
     titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
     messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
     messageLabel.setForeground(ColorScheme.TEXT_SECONDARY);
     messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
     
     contentPanel.add(icon);
     contentPanel.add(Box.createVerticalStrut(18));
     contentPanel.add(titleLabel);
     contentPanel.add(Box.createVerticalStrut(12));
     contentPanel.add(messageLabel);
     
     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
     buttonPanel.setBackground(Color.WHITE);
     buttonPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
     
     JButton okBtn = new JButton("OK");
     okBtn.setFont(new Font("Arial", Font.BOLD, 15));
     okBtn.setForeground(Color.WHITE);
     okBtn.setBackground(ColorScheme.DANGER);
     okBtn.setPreferredSize(new Dimension(130, 42));
     okBtn.setFocusPainted(false);
     okBtn.setBorderPainted(false);
     okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
     okBtn.addActionListener(e -> errorDialog.dispose());
     
     okBtn.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseEntered(java.awt.event.MouseEvent evt) {
             okBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.DANGER));
         }
         public void mouseExited(java.awt.event.MouseEvent evt) {
             okBtn.setBackground(ColorScheme.DANGER);
         }
     });
     
     buttonPanel.add(okBtn);
     
     errorDialog.add(contentPanel, BorderLayout.CENTER);
     errorDialog.add(buttonPanel, BorderLayout.SOUTH);
     errorDialog.setVisible(true);
 }

 private void showPasswordSuccessDialog(JDialog parent, String message, String title) {
     JDialog successDialog = new JDialog(parent, title, true);
     successDialog.setSize(440, 240);
     successDialog.setLocationRelativeTo(parent);
     successDialog.setLayout(new BorderLayout());
     successDialog.setResizable(false);
     
     JPanel contentPanel = new JPanel();
     contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
     contentPanel.setBackground(Color.WHITE);
     contentPanel.setBorder(new EmptyBorder(35, 35, 25, 35));
     
     JLabel icon = new JLabel(FontAwesomeIcon.CIRCLE_CHECK);
     icon.setFont(FontAwesomeIcon.getFont(60));
     icon.setForeground(ColorScheme.SUCCESS);
     icon.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel titleLabel = new JLabel(title);
     titleLabel.setFont(new Font("Arial", Font.BOLD, 19));
     titleLabel.setForeground(ColorScheme.TEXT_PRIMARY);
     titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     
     JLabel messageLabel = new JLabel("<html><center>" + message + "</center></html>");
     messageLabel.setFont(new Font("Arial", Font.PLAIN, 14));
     messageLabel.setForeground(ColorScheme.TEXT_SECONDARY);
     messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
     messageLabel.setHorizontalAlignment(SwingConstants.CENTER);
     
     contentPanel.add(icon);
     contentPanel.add(Box.createVerticalStrut(18));
     contentPanel.add(titleLabel);
     contentPanel.add(Box.createVerticalStrut(12));
     contentPanel.add(messageLabel);
     
     JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
     buttonPanel.setBackground(Color.WHITE);
     buttonPanel.setBorder(new EmptyBorder(15, 0, 15, 0));
     
     JButton okBtn = new JButton("OK");
     okBtn.setFont(new Font("Arial", Font.BOLD, 15));
     okBtn.setForeground(Color.WHITE);
     okBtn.setBackground(ColorScheme.SUCCESS);
     okBtn.setPreferredSize(new Dimension(130, 42));
     okBtn.setFocusPainted(false);
     okBtn.setBorderPainted(false);
     okBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
     okBtn.addActionListener(e -> successDialog.dispose());
     
     okBtn.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseEntered(java.awt.event.MouseEvent evt) {
             okBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.SUCCESS));
         }
         public void mouseExited(java.awt.event.MouseEvent evt) {
             okBtn.setBackground(ColorScheme.SUCCESS);
         }
     });
     
     buttonPanel.add(okBtn);
     
     successDialog.add(contentPanel, BorderLayout.CENTER);
     successDialog.add(buttonPanel, BorderLayout.SOUTH);
     successDialog.setVisible(true);
 }


    private JPanel createPasswordField(String label, JPasswordField field) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Arial", Font.BOLD, 13));
        labelText.setForeground(ColorScheme.TEXT_SECONDARY);
        
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 35));
        
        panel.add(labelText, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NEW FEATURE: USER MANAGEMENT
    // ═══════════════════════════════════════════════════════════════════════
    
    private void showUserManagement() {
        contentPanel.removeAll();
        
        JPanel userMgmtPanel = new JPanel();
        userMgmtPanel.setLayout(new BoxLayout(userMgmtPanel, BoxLayout.Y_AXIS));
        userMgmtPanel.setBackground(ColorScheme.BACKGROUND);
        userMgmtPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ColorScheme.BACKGROUND);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        
        JLabel titleLabel = new JLabel(FontAwesomeIcon.USERS + " User Management");
        titleLabel.setFont(FontAwesomeIcon.getFont(28));
        titleLabel.setForeground(ColorScheme.ADMIN_PRIMARY);
        
        JButton refreshBtn = createStyledButton(
            FontAwesomeIcon.REFRESH + " Refresh", 
            ColorScheme.INFO, 
            e -> showUserManagement()
        );
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(refreshBtn, BorderLayout.EAST);
        
        userMgmtPanel.add(headerPanel);
        userMgmtPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(ColorScheme.INFO_LIGHT);
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.INFO, 1),
            new EmptyBorder(15, 20, 15, 20)
        ));
        infoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JLabel infoTitle = new JLabel(FontAwesomeIcon.INFO_CIRCLE + " About User Management");
        infoTitle.setFont(FontAwesomeIcon.getFont(16));
        infoTitle.setForeground(ColorScheme.INFO);
        infoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel infoText = new JLabel("<html>Use this panel to manage user roles. You can promote regular users to administrators " +
            "or demote admins to regular users. Changes are saved immediately to the database.</html>");
        infoText.setFont(new Font("Arial", Font.PLAIN, 13));
        infoText.setForeground(ColorScheme.TEXT_SECONDARY);
        infoText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        infoPanel.add(infoTitle);
        infoPanel.add(Box.createRigidArea(new Dimension(0, 8)));
        infoPanel.add(infoText);
        
        userMgmtPanel.add(infoPanel);
        userMgmtPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        // Users Table
        try {
            List<User> allUsers = userDAO.getAllUsers();
            
            String[] columnNames = {"ID", "Username", "Role", "Linked Customer", "Actions"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4;
                }
            };
            
            for (User user : allUsers) {
                Object[] rowData = {
                    user.getId(),
                    user.getUsername(),
                    user.getRole(),
                    user.getLinkedCustomerId() > 0 ? user.getLinkedCustomerId() : "N/A",
                    "Actions"
                };
                tableModel.addRow(rowData);
            }
            
            JTable usersTable = new JTable(tableModel);
            usersTable.setFont(new Font("Arial", Font.PLAIN, 13));
            usersTable.setRowHeight(45);
            usersTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
            usersTable.getTableHeader().setBackground(ColorScheme.ADMIN_PRIMARY);
            usersTable.getTableHeader().setForeground(Color.WHITE);
            usersTable.setSelectionBackground(ColorScheme.INFO_LIGHT);
            usersTable.setSelectionForeground(ColorScheme.TEXT_PRIMARY);
            usersTable.setGridColor(ColorScheme.withOpacity(ColorScheme.ADMIN_PRIMARY, 0.2));
            
            // Center align cells
            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for (int i = 0; i < usersTable.getColumnCount() - 1; i++) {
                usersTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
            
            // Add button renderer for Actions column
            usersTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
            usersTable.getColumnModel().getColumn(4).setCellEditor(
                new ButtonEditor(new JCheckBox(), usersTable, tableModel, allUsers)
            );
            
            JScrollPane tableScrollPane = new JScrollPane(usersTable);
            tableScrollPane.setPreferredSize(new Dimension(900, 400));
            tableScrollPane.setBorder(BorderFactory.createLineBorder(
                ColorScheme.withOpacity(ColorScheme.ADMIN_PRIMARY, 0.3), 1
            ));
            
            JPanel tablePanel = new JPanel(new BorderLayout());
            tablePanel.setOpaque(false);
            tablePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 450));
            tablePanel.add(tableScrollPane, BorderLayout.CENTER);
            
            userMgmtPanel.add(tablePanel);
            
        } catch (Exception ex) {
            JLabel errorLabel = new JLabel(FontAwesomeIcon.EXCLAMATION_TRIANGLE + " Error loading users: " + ex.getMessage());
            errorLabel.setFont(FontAwesomeIcon.getFont(16));
            errorLabel.setForeground(ColorScheme.DANGER);
            userMgmtPanel.add(errorLabel);
        }
        
        userMgmtPanel.add(Box.createVerticalGlue());

        contentPanel.add(userMgmtPanel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    /**
     * Button Renderer for table cells
     */
    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }

        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "Actions" : value.toString());
            setFont(FontAwesomeIcon.getFont(12));
            setBackground(ColorScheme.GOLD);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            return this;
        }
    }

    /**
     * Button Editor for table cells - handles role changes
     */
    class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private JTable table;
        private DefaultTableModel tableModel;
        private List<User> users;

        public ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel tableModel, List<User> users) {
            super(checkBox);
            this.table = table;
            this.tableModel = tableModel;
            this.users = users;
            button = new JButton();
            button.setOpaque(true);
            button.setFont(FontAwesomeIcon.getFont(12));
            button.setBackground(ColorScheme.GOLD);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.addActionListener(e -> fireEditingStopped());
        }

        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            label = (value == null) ? "Actions" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        public Object getCellEditorValue() {
            if (isPushed) {
                int row = table.getSelectedRow();
                if (row >= 0 && row < users.size()) {
                    User selectedUser = users.get(row);
                    showRoleChangeDialog(selectedUser, row);
                }
            }
            isPushed = false;
            return label;
        }

        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }

        private void showRoleChangeDialog(User user, int row) {
            // Don't allow changing own role
            if (user.getId() == currentUser.getId()) {
                JOptionPane.showMessageDialog(AdminMainWindow.this,
                    "You cannot change your own role!",
                    "Not Allowed",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String currentRole = user.getRole();
            String newRole = currentRole.equals("ADMIN") ? "CUSTOMER" : "ADMIN";
            String actionText = currentRole.equals("ADMIN") ? "demote to Customer" : "promote to Admin";
            
            int confirm = JOptionPane.showConfirmDialog(AdminMainWindow.this,
                String.format("Do you want to %s user '%s'?\n\nCurrent Role: %s\nNew Role: %s",
                    actionText, user.getUsername(), currentRole, newRole),
                "Confirm Role Change",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    user.setRole(newRole);
                    boolean success = userDAO.updateUser(user);
                    
                    if (success) {
                        tableModel.setValueAt(newRole, row, 2);
                        JOptionPane.showMessageDialog(AdminMainWindow.this,
                            String.format("User '%s' has been %s successfully!",
                                user.getUsername(), 
                                newRole.equals("ADMIN") ? "promoted to Admin" : "demoted to Customer"),
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(AdminMainWindow.this,
                            "Failed to update user role in database!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(AdminMainWindow.this,
                        "Error updating user: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private JButton createStyledButton(String text, Color bgColor, ActionListener listener) {
        JButton btn = new JButton(text);
        btn.setFont(FontAwesomeIcon.getFont(13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(bgColor);
        btn.setBorder(new EmptyBorder(12, 20, 12, 20));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.addActionListener(listener);
        
        Color hoverColor = ColorScheme.getHoverColor(bgColor);
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
        });
        
        return btn;
    }

    // ===== EXISTING FEATURES =====

    private void openPassengerManifest() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new PassengerManifestWindow(adapter);
        adapter.dispose();
    }

    private void openModifyBooking() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new ModifyBookingWindow(adapter);
        adapter.dispose();
    }

    private void openFlightStatus() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new FlightStatusUpdateWindow(adapter);
        adapter.dispose();
    }

    private void openViewFeedback() {
        // First, prompt for flight ID
        String flightIdStr = JOptionPane.showInputDialog(this,
            "Enter Flight ID to view feedback:",
            "View Flight Feedback",
            JOptionPane.QUESTION_MESSAGE);
        
        if (flightIdStr != null && !flightIdStr.trim().isEmpty()) {
            try {
                int flightId = Integer.parseInt(flightIdStr.trim());
                Flight flight = fbs.getFlightByID(flightId);
                new ViewFeedbackWindow(flight, fbs);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Invalid Flight ID format!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openDataExport() {
        DataExportImportUtil.showExportDialog(fbs, this);
    }

    private void openAddFlight() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new AddFlightWindow(adapter);
        adapter.dispose();
    }

    private void openDeleteFlight() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new DeleteFlightWindow(adapter);
        adapter.dispose();
    }

    private void openAddCustomer() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new AddCustomerWindow(adapter);
        adapter.dispose();
    }

    private void openDeleteCustomer() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new DeleteCustomerWindow(adapter);
        adapter.dispose();
    }

    private void openIssueBooking() {
        MainWindowAdapter adapter = new MainWindowAdapter(fbs);
        new IssueBookingWindow(adapter);
        adapter.dispose();
    }

    private void logout() {
        // Create custom dialog
        JDialog confirmDialog = new JDialog(this, "Confirm Logout", true);
        confirmDialog.setSize(500, 280);
        confirmDialog.setLocationRelativeTo(this);
        confirmDialog.setLayout(new BorderLayout());
        confirmDialog.setUndecorated(false);

        // Header Panel with icon and title
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 25));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setPreferredSize(new Dimension(500, 90));
        
        JLabel iconLabel = new JLabel(FontAwesomeIcon.SIGN_OUT);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        
        JLabel titleLabel = new JLabel("Confirm Logout");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        
        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);

        // Message Panel
        JPanel messagePanel = new JPanel();
        messagePanel.setBackground(Color.WHITE);
        messagePanel.setBorder(new EmptyBorder(35, 40, 35, 40));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        
        JLabel messageLabel = new JLabel("Are you sure you want to logout?");
        messageLabel.setFont(new Font("Arial", Font.BOLD, 17));
        messageLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subMessageLabel = new JLabel("Your session will be ended and you'll return to the login screen.");
        subMessageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        subMessageLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        subMessageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        messagePanel.add(messageLabel);
        messagePanel.add(Box.createVerticalStrut(12));
        messagePanel.add(subMessageLabel);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 25));
        buttonPanel.setBackground(Color.WHITE);
        
        // No Button - styled better
        JButton noBtn = new JButton();
        noBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 8));
        noBtn.setBackground(ColorScheme.GOLD);
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
                noBtn.setBackground(ColorScheme.getHoverColor(ColorScheme.GOLD_DARK));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                noBtn.setBackground(ColorScheme.GOLD);
            }
        });
        
        noBtn.addActionListener(e -> confirmDialog.dispose());
        
        // Yes Button - styled better
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

        // Add components to dialog
        confirmDialog.add(headerPanel, BorderLayout.NORTH);
        confirmDialog.add(messagePanel, BorderLayout.CENTER);
        confirmDialog.add(buttonPanel, BorderLayout.SOUTH);
        
        confirmDialog.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle actions
    }

    public FlightBookingSystem getFlightBookingSystem() {
        return fbs;
    }

    private DashboardMetrics calculateMetrics() {
        DashboardMetrics metrics = new DashboardMetrics();

        for (Customer customer : fbs.getCustomers()) {
            if (!customer.isDeleted()) {
                metrics.totalCustomers++;
            }
        }

        for (Customer customer : fbs.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                metrics.totalBookings++;
                if (booking.isCancelled()) {
                    metrics.cancelledBookings++;
                } else {
                    metrics.activeBookings++;
                    metrics.totalRevenue += booking.getBookingPrice();
                }
            }
        }

        int totalFlights = 0;
        double totalLoadFactor = 0;
        for (Flight flight : fbs.getFlights()) {
            if (!flight.isDeleted()) {
                totalFlights++;
                double loadFactor = ((double) flight.getPassengers().size() / flight.getCapacity()) * 100;
                totalLoadFactor += loadFactor;
            }
        }
        metrics.avgLoadFactor = totalFlights > 0 ? totalLoadFactor / totalFlights : 0;

        metrics.newCustomersThisMonth = Math.min(5, metrics.totalCustomers);

        return metrics;
    }

    private Map<String, Double> calculateRouteRevenue() {
        Map<String, Double> routeRevenue = new HashMap<>();
        for (Customer customer : fbs.getCustomers()) {
            for (Booking booking : customer.getBookings()) {
                if (!booking.isCancelled()) {
                    Flight flight = booking.getFlight();
                    String route = flight.getOrigin() + " → " + flight.getDestination();
                    routeRevenue.merge(route, booking.getBookingPrice(), Double::sum);
                }
            }
        }
        return routeRevenue;
    }

    private static class DashboardMetrics {
        double totalRevenue = 0;
        int totalBookings = 0;
        int activeBookings = 0;
        int cancelledBookings = 0;
        int totalCustomers = 0;
        int newCustomersThisMonth = 0;
        double avgLoadFactor = 0;
    }

    private static class MainWindowAdapter extends bcu.cmp5332.bookingsystem.gui.MainWindow {
        public MainWindowAdapter(FlightBookingSystem fbs) {
            super(fbs);
            setVisible(false);
        }
    }
}