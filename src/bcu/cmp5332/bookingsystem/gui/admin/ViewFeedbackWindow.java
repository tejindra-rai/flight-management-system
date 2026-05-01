package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.Customer;
import bcu.cmp5332.bookingsystem.model.Feedback;
import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Admin window to view all feedback for a specific flight.
 * 
 * @author Tejindra Rai
 * @version 2.0 - Admin maroon theme with FontAwesome
 */
public class ViewFeedbackWindow extends JFrame {

    private Flight flight;
    private FlightBookingSystem fbs;

    public ViewFeedbackWindow(Flight flight, FlightBookingSystem fbs) {
        this.flight = flight;
        this.fbs = fbs;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Flight Reviews");
        setSize(750, 600);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.ADMIN_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.STAR);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(ColorScheme.GOLD);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Customer Reviews");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel flightLabel = new JLabel("Flight " + flight.getFlightNumber() + " - " + 
                                       flight.getOrigin() + " → " + flight.getDestination());
        flightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        flightLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.9));
        flightLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Calculate average rating
        double avgRating = fbs.getAverageRatingForFlight(flight.getId());
        String avgStars = getStarDisplay((int) Math.round(avgRating));
        JLabel avgLabel = new JLabel(avgStars + " (" + String.format("%.1f", avgRating) + " average)");
        avgLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        avgLabel.setForeground(ColorScheme.GOLD);
        avgLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(flightLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(avgLabel);

        // Feedback List Panel
        JPanel feedbackListPanel = new JPanel();
        feedbackListPanel.setLayout(new BoxLayout(feedbackListPanel, BoxLayout.Y_AXIS));
        feedbackListPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        feedbackListPanel.setBackground(ColorScheme.BACKGROUND);

        List<Feedback> feedbackList = fbs.getFeedbackForFlight(flight.getId());

        if (feedbackList.isEmpty()) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
            emptyPanel.setOpaque(false);
            emptyPanel.setBorder(new EmptyBorder(80, 0, 0, 0));
            
            JLabel noFeedbackIcon = new JLabel(FontAwesomeIcon.STAR);
            noFeedbackIcon.setFont(FontAwesomeIcon.getFont(48));
            noFeedbackIcon.setForeground(ColorScheme.TEXT_DISABLED);
            noFeedbackIcon.setAlignmentX(CENTER_ALIGNMENT);
            
            JLabel noFeedbackLabel = new JLabel("No reviews yet");
            noFeedbackLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
            noFeedbackLabel.setForeground(ColorScheme.TEXT_SECONDARY);
            noFeedbackLabel.setAlignmentX(CENTER_ALIGNMENT);
            
            JLabel encourageLabel = new JLabel("Be the first to review this flight!");
            encourageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            encourageLabel.setForeground(ColorScheme.TEXT_SECONDARY);
            encourageLabel.setAlignmentX(CENTER_ALIGNMENT);
            
            emptyPanel.add(noFeedbackIcon);
            emptyPanel.add(Box.createVerticalStrut(15));
            emptyPanel.add(noFeedbackLabel);
            emptyPanel.add(Box.createVerticalStrut(5));
            emptyPanel.add(encourageLabel);
            
            feedbackListPanel.add(emptyPanel);
        } else {
            for (Feedback feedback : feedbackList) {
                feedbackListPanel.add(createFeedbackCard(feedback));
                feedbackListPanel.add(Box.createVerticalStrut(12));
            }
        }

        JScrollPane scrollPane = new JScrollPane(feedbackListPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private JPanel createFeedbackCard(Feedback feedback) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(12, 12));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1),
            new EmptyBorder(18, 18, 18, 18)
        ));
        card.setBackground(ColorScheme.CARD_BG);
        card.setMaximumSize(new Dimension(700, 180));

        // Top section - customer name and rating
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        try {
            Customer customer = fbs.getCustomerByID(feedback.getCustomerId());
            JLabel nameLabel = new JLabel(FontAwesomeIcon.USER + " " + customer.getName());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            nameLabel.setForeground(ColorScheme.TEXT_PRIMARY);
            topPanel.add(nameLabel, BorderLayout.WEST);
        } catch (FlightBookingSystemException ex) {
            JLabel nameLabel = new JLabel(FontAwesomeIcon.USER + " Customer #" + feedback.getCustomerId());
            nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
            nameLabel.setForeground(ColorScheme.TEXT_PRIMARY);
            topPanel.add(nameLabel, BorderLayout.WEST);
        }

        JLabel ratingLabel = new JLabel(feedback.getStarRating());
        ratingLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ratingLabel.setForeground(ColorScheme.GOLD);
        topPanel.add(ratingLabel, BorderLayout.EAST);

        // Middle section - comment
        JTextArea commentArea = new JTextArea(feedback.getComment());
        commentArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        commentArea.setForeground(ColorScheme.TEXT_PRIMARY);
        commentArea.setLineWrap(true);
        commentArea.setWrapStyleWord(true);
        commentArea.setEditable(false);
        commentArea.setOpaque(false);
        commentArea.setBorder(new EmptyBorder(12, 0, 12, 0));

        // Bottom section - date
        JLabel dateLabel = new JLabel(FontAwesomeIcon.CALENDAR + " " + feedback.getFeedbackDate());
        dateLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        dateLabel.setForeground(ColorScheme.TEXT_SECONDARY);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(commentArea, BorderLayout.CENTER);
        card.add(dateLabel, BorderLayout.SOUTH);

        return card;
    }

    private String getStarDisplay(int rating) {
        StringBuilder stars = new StringBuilder();
        for (int i = 0; i < rating; i++) {
            stars.append("★");
        }
        for (int i = rating; i < 5; i++) {
            stars.append("☆");
        }
        return stars.toString();
    }
}