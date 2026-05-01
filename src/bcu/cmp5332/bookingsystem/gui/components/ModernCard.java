package bcu.cmp5332.bookingsystem.gui.components;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Modern card component with subtle shadow and hover effects.
 * Provides visual depth and interactivity to UI elements.
 * 
 * Usage:
 *   ModernCard card = new ModernCard();
 *   card.setLayout(new BorderLayout());
 *   card.add(content);
 *   // Optional: make clickable
 *   card.setClickable(true);
 *   card.addClickListener(() -> doSomething());
 * 
 * @author Binay Chaudhary
 * @version 1.0
 */
public class ModernCard extends JPanel {
    
    private boolean isHovered = false;
    private boolean isClickable = false;
    private int shadowSize = 4;
    private Color shadowColor = new Color(0, 0, 0, 15);
    private Runnable clickListener;
    
    /**
     * Creates a modern card with default settings.
     */
    public ModernCard() {
        this(20);
    }
    
    /**
     * Creates a modern card with specified padding.
     * 
     * @param padding padding in pixels
     */
    public ModernCard(int padding) {
        setBackground(ColorScheme.CARD_BG);
        setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(shadowSize, shadowColor),
            new EmptyBorder(padding, padding, padding, padding)
        ));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (isClickable) {
                    isHovered = true;
                    setBorder(BorderFactory.createCompoundBorder(
                        new ShadowBorder(8, new Color(0, 0, 0, 25)),
                        new EmptyBorder(padding, padding, padding, padding)
                    ));
                    setCursor(new Cursor(Cursor.HAND_CURSOR));
                    repaint();
                }
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (isClickable) {
                    isHovered = false;
                    setBorder(BorderFactory.createCompoundBorder(
                        new ShadowBorder(shadowSize, shadowColor),
                        new EmptyBorder(padding, padding, padding, padding)
                    ));
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    repaint();
                }
            }
            
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isClickable && clickListener != null) {
                    clickListener.run();
                }
            }
        });
    }
    
    /**
     * Sets whether the card is clickable.
     * 
     * @param clickable true to make clickable, false otherwise
     */
    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
        if (!clickable) {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }
    
    /**
     * Sets the click listener for the card.
     * 
     * @param listener action to perform on click
     */
    public void addClickListener(Runnable listener) {
        this.clickListener = listener;
        setClickable(true);
    }
    
    /**
     * Sets the shadow size.
     * 
     * @param size shadow size in pixels
     */
    public void setShadowSize(int size) {
        this.shadowSize = size;
        Insets insets = getInsets();
        setBorder(BorderFactory.createCompoundBorder(
            new ShadowBorder(shadowSize, shadowColor),
            new EmptyBorder(insets.top, insets.left, insets.bottom, insets.right)
        ));
    }
    
    /**
     * Custom border that draws a soft shadow around the component.
     */
    private static class ShadowBorder extends AbstractBorder {
        private int shadowSize;
        private Color shadowColor;
        private Insets insets;
        
        public ShadowBorder(int shadowSize, Color shadowColor) {
            this.shadowSize = shadowSize;
            this.shadowColor = shadowColor;
            this.insets = new Insets(shadowSize, shadowSize, shadowSize, shadowSize);
        }
        
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw shadow layers for smooth gradient effect
            for (int i = 0; i < shadowSize; i++) {
                int alpha = (int)((shadowSize - i) * (shadowColor.getAlpha() / (float)shadowSize));
                g2d.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), 
                                      shadowColor.getBlue(), alpha));
                g2d.drawRoundRect(
                    x + i, 
                    y + i, 
                    width - (i * 2) - 1, 
                    height - (i * 2) - 1, 
                    12, 
                    12
                );
            }
            
            // Draw white card background with rounded corners
            g2d.setColor(ColorScheme.CARD_BG);
            g2d.fillRoundRect(
                x + shadowSize, 
                y + shadowSize, 
                width - (shadowSize * 2), 
                height - (shadowSize * 2), 
                8, 
                8
            );
            
            g2d.dispose();
        }
        
        @Override
        public Insets getBorderInsets(Component c) {
            return insets;
        }
        
        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = shadowSize;
            return insets;
        }
        
        @Override
        public boolean isBorderOpaque() {
            return false;
        }
    }
}