package bcu.cmp5332.bookingsystem.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import javax.imageio.ImageIO;

/**
 * Utility class for loading and managing images
 * 
 * @author Tejindra Rai
 */
public class ImageUtils {
    
    /**
     * Loads an image from the resources/images folder
     * 
     * @param imageName the name of the image file (e.g., "A1.jpg")
     * @return ImageIcon or null if not found
     */
    public static ImageIcon loadImage(String imageName) {
        try {
            // Try multiple paths to find the image
            
            // Method 1: Try as resource stream (works when running from JAR)
            InputStream is = ImageUtils.class.getResourceAsStream("/images/" + imageName);
            if (is != null) {
                Image img = ImageIO.read(is);
                is.close();
                System.out.println("✓ Image loaded from resources: " + imageName);
                return new ImageIcon(img);
            }
            
            // Method 2: Try direct file path (works during development)
            File imageFile = new File("resources/images/" + imageName);
            if (imageFile.exists()) {
                Image img = ImageIO.read(imageFile);
                System.out.println("✓ Image loaded from file: " + imageName);
                return new ImageIcon(img);
            }
            
            // Method 3: Try another common location
            imageFile = new File("./resources/images/" + imageName);
            if (imageFile.exists()) {
                Image img = ImageIO.read(imageFile);
                System.out.println("✓ Image loaded from file: " + imageName);
                return new ImageIcon(img);
            }
            
            // Method 4: Try from src folder
            imageFile = new File("src/resources/images/" + imageName);
            if (imageFile.exists()) {
                Image img = ImageIO.read(imageFile);
                System.out.println("✓ Image loaded from src: " + imageName);
                return new ImageIcon(img);
            }
            
            System.err.println("✗ Image not found: " + imageName);
            
            // Return a placeholder image
            return createPlaceholderImage(200, 200, imageName);
            
        } catch (Exception e) {
            System.err.println("Error loading image: " + imageName + " - " + e.getMessage());
            return createPlaceholderImage(200, 200, imageName);
        }
    }
    
    /**
     * Loads and scales an image to specified dimensions
     * 
     * @param imageName the name of the image file
     * @param width desired width
     * @param height desired height
     * @return scaled ImageIcon or placeholder if not found
     */
    public static ImageIcon loadScaledImage(String imageName, int width, int height) {
        ImageIcon icon = loadImage(imageName);
        if (icon != null && icon.getIconWidth() > 0) {
            Image image = icon.getImage();
            Image scaledImage = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return createPlaceholderImage(width, height, imageName);
    }
    
    /**
     * Creates a placeholder image when the actual image cannot be loaded
     */
    private static ImageIcon createPlaceholderImage(int width, int height, String label) {
        java.awt.image.BufferedImage placeholder = new java.awt.image.BufferedImage(
            width, height, java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = placeholder.createGraphics();
        
        // Enable anti-aliasing
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        // Draw gradient background
        GradientPaint gradient = new GradientPaint(
            0, 0, new Color(100, 120, 150),
            width, height, new Color(60, 80, 110)
        );
        g.setPaint(gradient);
        g.fillRect(0, 0, width, height);
        
        // Draw border
        g.setColor(new Color(200, 200, 200, 100));
        g.drawRect(0, 0, width - 1, height - 1);
        
        // Draw icon
        g.setColor(new Color(255, 255, 255, 150));
        g.setFont(new Font("Segoe UI", Font.BOLD, Math.min(width, height) / 4));
        String icon = "🖼";
        FontMetrics iconFm = g.getFontMetrics();
        int iconWidth = iconFm.stringWidth(icon);
        g.drawString(icon, (width - iconWidth) / 2, height / 2 - 20);
        
        // Draw text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics fm = g.getFontMetrics();
        
        String text = label.length() > 15 ? label.substring(0, 12) + "..." : label;
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();
        
        g.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2 + 20);
        
        g.dispose();
        return new ImageIcon(placeholder);
    }
    
    /**
     * Creates a rounded image label
     * 
     * @param imageName the name of the image file
     * @param width desired width
     * @param height desired height
     * @param cornerRadius radius for rounded corners
     * @return JLabel with rounded image
     */
    public static JLabel createRoundedImageLabel(String imageName, int width, int height, int cornerRadius) {
        ImageIcon icon = loadScaledImage(imageName, width, height);
        if (icon != null) {
            JLabel label = new JLabel(icon) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    if (getIcon() != null) {
                        g2d.setClip(new java.awt.geom.RoundRectangle2D.Float(
                            0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));
                        super.paintComponent(g2d);
                    }
                    g2d.dispose();
                }
            };
            label.setPreferredSize(new Dimension(width, height));
            return label;
        }
        JLabel errorLabel = new JLabel("Image not found");
        errorLabel.setPreferredSize(new Dimension(width, height));
        return errorLabel;
    }
    
    /**
     * Test method to check if images can be loaded
     */
    public static void testImageLoading() {
        System.out.println("\n═══ Testing Image Loading ═══");
        String[] testImages = {"A1.jpg", "A2.jpg", "A3.jpg", "A4.jpg", "A5.jpg","PKH.jpg,","PRS.jpg,","TKY.jpg,","UK.jpg,","USA.jpg,"};
        
        for (String img : testImages) {
            ImageIcon icon = loadImage(img);
            if (icon != null && icon.getIconWidth() > 0) {
                System.out.println("✓ " + img + " loaded successfully (" + 
                                 icon.getIconWidth() + "x" + icon.getIconHeight() + ")");
            } else {
                System.out.println("✗ " + img + " - using placeholder");
            }
        }
        System.out.println("═══════════════════════════════\n");
    }
}