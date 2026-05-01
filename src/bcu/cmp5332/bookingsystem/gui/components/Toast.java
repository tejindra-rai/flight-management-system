package bcu.cmp5332.bookingsystem.gui.components;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern toast notification system for non-blocking user feedback.
 * Provides elegant, temporary notifications that don't interrupt user workflow.
 * 
 * Usage:
 *   Toast.show(this, "Booking confirmed!", Toast.Type.SUCCESS);
 *   Toast.show(this, "Flight not found", Toast.Type.ERROR);
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class Toast {
    
    /**
     * Types of toast notifications with associated colors and icons.
     */
    public enum Type {
        SUCCESS(ColorScheme.SUCCESS, FontAwesomeIcon.CIRCLE_CHECK, "Success"),
        ERROR(ColorScheme.DANGER, FontAwesomeIcon.TIMES_CIRCLE, "Error"),
        WARNING(ColorScheme.WARNING, FontAwesomeIcon.EXCLAMATION_TRIANGLE, "Warning"),
        INFO(ColorScheme.INFO, FontAwesomeIcon.INFO_CIRCLE, "Info");
        
        final Color color;
        final String icon;
        final String title;
        
        Type(Color color, String icon, String title) {
            this.color = color;
            this.icon = icon;
            this.title = title;
        }
    }
    
    /**
     * Shows a toast notification with default duration (3 seconds).
     * 
     * @param parent parent component for positioning
     * @param message notification message
     * @param type notification type (SUCCESS, ERROR, WARNING, INFO)
     */
    public static void show(Component parent, String message, Type type) {
        show(parent, message, type, 3000);
    }
    
    /**
     * Shows a toast notification with custom duration.
     * 
     * @param parent parent component for positioning
     * @param message notification message
     * @param type notification type
     * @param durationMs display duration in milliseconds
     */
    public static void show(Component parent, String message, Type type, int durationMs) {
        JWindow toast = new JWindow();
        toast.setAlwaysOnTop(true);
        
        // Main panel with colored background
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(type.color);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.darken(type.color, 0.2), 2, true),
            new EmptyBorder(15, 20, 15, 20)
        ));
        
        // Icon
        JLabel iconLabel = new JLabel(type.icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(24));
        iconLabel.setForeground(Color.WHITE);
        
        // Message area
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(type.title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel messageLabel = new JLabel("<html>" + message + "</html>");
        messageLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        messageLabel.setForeground(ColorScheme.withOpacity(Color.WHITE, 0.95));
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        messagePanel.add(titleLabel);
        messagePanel.add(Box.createVerticalStrut(3));
        messagePanel.add(messageLabel);
        
        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(messagePanel, BorderLayout.CENTER);
        
        toast.add(panel);
        toast.pack();
        
        // Position at bottom-right of parent (or screen if parent is null)
        positionToast(toast, parent);
        
        // Slide in animation
        slideIn(toast);
        
        toast.setVisible(true);
        
        // Auto-hide after duration
        Timer hideTimer = new Timer(durationMs, e -> {
            slideOut(toast, () -> {
                toast.setVisible(false);
                toast.dispose();
            });
        });
        hideTimer.setRepeats(false);
        hideTimer.start();
    }
    
    /**
     * Shows a simple toast with only a message (INFO type by default).
     * 
     * @param parent parent component
     * @param message notification message
     */
    public static void showInfo(Component parent, String message) {
        show(parent, message, Type.INFO);
    }
    
    /**
     * Shows a success toast.
     * 
     * @param parent parent component
     * @param message success message
     */
    public static void showSuccess(Component parent, String message) {
        show(parent, message, Type.SUCCESS);
    }
    
    /**
     * Shows an error toast.
     * 
     * @param parent parent component
     * @param message error message
     */
    public static void showError(Component parent, String message) {
        show(parent, message, Type.ERROR);
    }
    
    /**
     * Shows a warning toast.
     * 
     * @param parent parent component
     * @param message warning message
     */
    public static void showWarning(Component parent, String message) {
        show(parent, message, Type.WARNING);
    }
    
    /**
     * Positions the toast notification at the bottom-right of the parent.
     */
    private static void positionToast(JWindow toast, Component parent) {
        Dimension toastSize = toast.getSize();
        
        if (parent != null && parent.isShowing()) {
            Point parentLocation = parent.getLocationOnScreen();
            Dimension parentSize = parent.getSize();
            
            int x = parentLocation.x + parentSize.width - toastSize.width - 20;
            int y = parentLocation.y + parentSize.height - toastSize.height - 80;
            
            toast.setLocation(x, y + toastSize.height); // Start off-screen for slide-in
        } else {
            // Position at bottom-right of screen
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            GraphicsDevice gd = ge.getDefaultScreenDevice();
            Rectangle bounds = gd.getDefaultConfiguration().getBounds();
            
            int x = (int)(bounds.getMaxX() - toastSize.width - 20);
            int y = (int)(bounds.getMaxY() - toastSize.height - 80);
            
            toast.setLocation(x, y + toastSize.height);
        }
    }
    
    /**
     * Slide-in animation from bottom.
     */
    private static void slideIn(JWindow toast) {
        Point targetLocation = toast.getLocation();
        Dimension toastSize = toast.getSize();
        Point startLocation = new Point(targetLocation.x, targetLocation.y + toastSize.height);
        
        toast.setLocation(startLocation);
        
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            Point current = toast.getLocation();
            if (current.y <= targetLocation.y) {
                toast.setLocation(targetLocation);
                timer.stop();
            } else {
                toast.setLocation(current.x, current.y - 8);
            }
        });
        timer.start();
    }
    
    /**
     * Slide-out animation to bottom.
     */
    private static void slideOut(JWindow toast, Runnable onComplete) {
        Point startLocation = toast.getLocation();
        Dimension toastSize = toast.getSize();
        
        Timer timer = new Timer(10, null);
        timer.addActionListener(e -> {
            Point current = toast.getLocation();
            if (current.y >= startLocation.y + toastSize.height) {
                timer.stop();
                if (onComplete != null) {
                    onComplete.run();
                }
            } else {
                toast.setLocation(current.x, current.y + 8);
            }
        });
        timer.start();
    }
}