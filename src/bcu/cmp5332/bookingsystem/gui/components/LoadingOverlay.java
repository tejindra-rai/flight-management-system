package bcu.cmp5332.bookingsystem.gui.components;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Full-screen loading overlay with spinner and message.
 * Blocks user interaction while displaying loading feedback.
 * 
 * Usage:
 *   LoadingOverlay loading = new LoadingOverlay("Loading flights...");
 *   loading.show(parentPanel);
 *   // ... perform operation ...
 *   loading.hide(parentPanel);
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class LoadingOverlay extends JPanel {
    private LoadingSpinner spinner;
    private JLabel messageLabel;
    private String message;
    
    /**
     * Creates a loading overlay with the specified message.
     * 
     * @param message loading message to display
     */
    public LoadingOverlay(String message) {
        this.message = message;
        initialize();
    }
    
    /**
     * Creates a loading overlay with a default message.
     */
    public LoadingOverlay() {
        this("Loading...");
    }
    
    private void initialize() {
        setLayout(new GridBagLayout());
        setBackground(ColorScheme.withOpacity(Color.BLACK, 0.5));
        setOpaque(true);
        
        // Content panel (white card with spinner and message)
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_LIGHT, 1, true),
            new EmptyBorder(40, 60, 40, 60)
        ));
        
        // Spinner
        spinner = new LoadingSpinner(60);
        spinner.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Message
        messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        messageLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Optional subtitle
        JLabel subtitleLabel = new JLabel("Please wait...");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        contentPanel.add(spinner);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(messageLabel);
        contentPanel.add(Box.createVerticalStrut(8));
        contentPanel.add(subtitleLabel);
        
        add(contentPanel);
    }
    
    /**
     * Shows the loading overlay on the specified parent panel.
     * 
     * @param parent panel to overlay
     */
    public void show(JPanel parent) {
        // Ensure parent uses OverlayLayout or add this as glass pane
        parent.add(this);
        this.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        this.setVisible(true);
        spinner.start();
        parent.revalidate();
        parent.repaint();
    }
    
    /**
     * Shows the loading overlay on a JFrame.
     * 
     * @param frame frame to overlay
     */
    public void show(JFrame frame) {
        Component glassPane = frame.getGlassPane();
        if (glassPane instanceof JPanel) {
            JPanel glass = (JPanel) glassPane;
            glass.setLayout(null);
            glass.add(this);
            this.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        }
        frame.getGlassPane().setVisible(true);
        this.setVisible(true);
        spinner.start();
    }
    
    /**
     * Hides the loading overlay from the specified parent panel.
     * 
     * @param parent panel to remove overlay from
     */
    public void hide(JPanel parent) {
        spinner.stop();
        this.setVisible(false);
        parent.remove(this);
        parent.revalidate();
        parent.repaint();
    }
    
    /**
     * Hides the loading overlay from a JFrame.
     * 
     * @param frame frame to remove overlay from
     */
    public void hide(JFrame frame) {
        spinner.stop();
        frame.getGlassPane().setVisible(false);
        this.setVisible(false);
        
        Component glassPane = frame.getGlassPane();
        if (glassPane instanceof JPanel) {
            ((JPanel) glassPane).remove(this);
        }
    }
    
    /**
     * Updates the loading message.
     * 
     * @param newMessage new message to display
     */
    public void setMessage(String newMessage) {
        this.message = newMessage;
        messageLabel.setText(newMessage);
    }
    
    /**
     * Gets the current loading message.
     * 
     * @return current message
     */
    public String getMessage() {
        return message;
    }
}