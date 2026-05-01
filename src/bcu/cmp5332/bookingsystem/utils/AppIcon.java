package bcu.cmp5332.bookingsystem.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility class for creating application icons and branding.
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class AppIcon {
    
    /**
     * Creates a custom application icon with airplane symbol.
     * 
     * @return ImageIcon for the application
     */
    public static ImageIcon createAppIcon() {
        int size = 64;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        // Enable anti-aliasing for smooth graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw gradient background circle
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(70, 130, 180),  // Steel blue
            size, size, new Color(100, 149, 237)  // Cornflower blue
        );
        g2d.setPaint(gradient);
        g2d.fillOval(2, 2, size - 4, size - 4);
        
        // Draw white airplane symbol
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(3f));
        
        // Airplane body (horizontal line)
        g2d.drawLine(15, 32, 49, 32);
        
        // Airplane wings (angled lines)
        g2d.drawLine(25, 32, 20, 22);  // Left wing
        g2d.drawLine(25, 32, 20, 42);  // Left wing bottom
        g2d.drawLine(39, 32, 44, 22);  // Right wing
        
        // Airplane tail
        g2d.drawLine(15, 32, 12, 25);  // Tail fin
        
        // Airplane nose (small circle)
        g2d.fillOval(47, 30, 6, 4);
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Creates a small icon for menu items and buttons.
     * 
     * @return ImageIcon for menu items
     */
    public static ImageIcon createSmallIcon() {
        int size = 16;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw small airplane
        g2d.setColor(new Color(70, 130, 180));
        g2d.setStroke(new BasicStroke(2f));
        
        // Simple airplane shape
        g2d.drawLine(3, 8, 13, 8);   // Body
        g2d.drawLine(6, 8, 5, 5);    // Left wing
        g2d.drawLine(10, 8, 11, 5);  // Right wing
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Creates a loading spinner icon.
     * 
     * @return ImageIcon for loading indicator
     */
    public static ImageIcon createLoadingIcon() {
        int size = 32;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw rotating circle segments
        g2d.setStroke(new BasicStroke(3f));
        for (int i = 0; i < 8; i++) {
            int alpha = 255 - (i * 30);
            g2d.setColor(new Color(70, 130, 180, Math.max(50, alpha)));
            g2d.drawArc(4, 4, size - 8, size - 8, i * 45, 45);
        }
        
        g2d.dispose();
        
        return new ImageIcon(image);
    }
    
    /**
     * Sets the application icon for a frame.
     * 
     * @param frame the frame to set icon for
     */
    public static void setFrameIcon(JFrame frame) {
        frame.setIconImage(createAppIcon().getImage());
    }
}