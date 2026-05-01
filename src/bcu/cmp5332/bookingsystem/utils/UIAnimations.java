package bcu.cmp5332.bookingsystem.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Utility class for smooth UI animations and transitions.
 * Adds professional polish to the application through smooth visual effects.
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class UIAnimations {
    
    /**
     * Fade in a component smoothly from transparent to opaque.
     * 
     * @param component the component to fade in
     * @param durationMs duration of the animation in milliseconds
     */
    public static void fadeIn(JComponent component, int durationMs) {
        component.setVisible(true);
        
        Timer timer = new Timer(20, new ActionListener() {
            float opacity = 0.0f;
            float step = 1.0f / (durationMs / 20.0f);
            
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += step;
                if (opacity >= 1.0f) {
                    opacity = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                component.repaint();
            }
        });
        timer.start();
    }
    
    /**
     * Fade out a component smoothly from opaque to transparent.
     * 
     * @param component the component to fade out
     * @param durationMs duration of the animation in milliseconds
     */
    public static void fadeOut(JComponent component, int durationMs) {
        Timer timer = new Timer(20, new ActionListener() {
            float opacity = 1.0f;
            float step = 1.0f / (durationMs / 20.0f);
            
            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= step;
                if (opacity <= 0.0f) {
                    opacity = 0.0f;
                    component.setVisible(false);
                    ((Timer)e.getSource()).stop();
                }
                component.repaint();
            }
        });
        timer.start();
    }
    
    /**
     * Slide down a panel smoothly (reveal animation).
     * 
     * @param panel the panel to slide down
     * @param targetHeight final height of the panel
     * @param durationMs duration of the animation in milliseconds
     */
    public static void slideDown(JPanel panel, int targetHeight, int durationMs) {
        panel.setPreferredSize(new Dimension(panel.getWidth(), 0));
        panel.setVisible(true);
        
        Timer timer = new Timer(20, new ActionListener() {
            int currentHeight = 0;
            int step = Math.max(1, targetHeight / (durationMs / 20));
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentHeight += step;
                if (currentHeight >= targetHeight) {
                    currentHeight = targetHeight;
                    ((Timer)e.getSource()).stop();
                }
                panel.setPreferredSize(new Dimension(panel.getWidth(), currentHeight));
                panel.revalidate();
                panel.repaint();
            }
        });
        timer.start();
    }
    
    /**
     * Slide up a panel smoothly (hide animation).
     * 
     * @param panel the panel to slide up
     * @param durationMs duration of the animation in milliseconds
     */
    public static void slideUp(JPanel panel, int durationMs) {
        int initialHeight = panel.getHeight();
        
        Timer timer = new Timer(20, new ActionListener() {
            int currentHeight = initialHeight;
            int step = Math.max(1, initialHeight / (durationMs / 20));
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentHeight -= step;
                if (currentHeight <= 0) {
                    currentHeight = 0;
                    panel.setVisible(false);
                    ((Timer)e.getSource()).stop();
                }
                panel.setPreferredSize(new Dimension(panel.getWidth(), currentHeight));
                panel.revalidate();
                panel.repaint();
            }
        });
        timer.start();
    }
    
    /**
     * Smooth color transition for buttons and panels.
     * 
     * @param component the component to animate
     * @param from starting color
     * @param to ending color
     * @param durationMs duration of the animation in milliseconds
     */
    public static void smoothColorTransition(JComponent component, Color from, Color to, int durationMs) {
        Timer timer = new Timer(10, new ActionListener() {
            int steps = durationMs / 10;
            int currentStep = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentStep++;
                if (currentStep >= steps) {
                    component.setBackground(to);
                    ((Timer)e.getSource()).stop();
                    return;
                }
                
                float ratio = (float)currentStep / steps;
                int r = (int)(from.getRed() + ratio * (to.getRed() - from.getRed()));
                int g = (int)(from.getGreen() + ratio * (to.getGreen() - from.getGreen()));
                int b = (int)(from.getBlue() + ratio * (to.getBlue() - from.getBlue()));
                int a = (int)(from.getAlpha() + ratio * (to.getAlpha() - from.getAlpha()));
                
                component.setBackground(new Color(r, g, b, a));
            }
        });
        timer.start();
    }
    
    /**
     * Pulse animation for attention-grabbing elements.
     * Makes the component briefly change color to draw attention.
     * 
     * @param component the component to pulse
     * @param highlightColor the highlight color for the pulse
     * @param cycles number of pulse cycles
     */
    public static void pulse(JComponent component, Color highlightColor, int cycles) {
        Color originalBg = component.getBackground();
        Timer timer = new Timer(100, new ActionListener() {
            int cycle = 0;
            boolean growing = true;
            float alpha = 0.0f;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (growing) {
                    alpha += 0.1f;
                    if (alpha >= 1.0f) {
                        alpha = 1.0f;
                        growing = false;
                    }
                } else {
                    alpha -= 0.1f;
                    if (alpha <= 0.0f) {
                        alpha = 0.0f;
                        growing = true;
                        cycle++;
                    }
                }
                
                if (cycle >= cycles) {
                    component.setBackground(originalBg);
                    ((Timer)e.getSource()).stop();
                    return;
                }
                
                int r = (int)(originalBg.getRed() + alpha * (highlightColor.getRed() - originalBg.getRed()));
                int g = (int)(originalBg.getGreen() + alpha * (highlightColor.getGreen() - originalBg.getGreen()));
                int b = (int)(originalBg.getBlue() + alpha * (highlightColor.getBlue() - originalBg.getBlue()));
                
                component.setBackground(new Color(r, g, b));
            }
        });
        timer.start();
    }
    
    /**
     * Shake animation for error feedback.
     * 
     * @param component the component to shake
     * @param intensity shake intensity in pixels
     * @param cycles number of shake cycles
     */
    public static void shake(JComponent component, int intensity, int cycles) {
        Point originalLocation = component.getLocation();
        
        Timer timer = new Timer(50, new ActionListener() {
            int cycle = 0;
            boolean right = true;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (cycle >= cycles * 2) {
                    component.setLocation(originalLocation);
                    ((Timer)e.getSource()).stop();
                    return;
                }
                
                int offset = right ? intensity : -intensity;
                component.setLocation(originalLocation.x + offset, originalLocation.y);
                
                right = !right;
                if (right) {
                    cycle++;
                }
            }
        });
        timer.start();
    }
    
    /**
     * Bounce animation for success feedback.
     * 
     * @param component the component to bounce
     * @param bounceHeight height of the bounce in pixels
     * @param durationMs duration of one bounce in milliseconds
     */
    public static void bounce(JComponent component, int bounceHeight, int durationMs) {
        Point originalLocation = component.getLocation();
        
        Timer timer = new Timer(20, new ActionListener() {
            int currentY = 0;
            int step = bounceHeight / (durationMs / 40);
            boolean goingUp = true;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                if (goingUp) {
                    currentY -= step;
                    if (currentY <= -bounceHeight) {
                        goingUp = false;
                    }
                } else {
                    currentY += step;
                    if (currentY >= 0) {
                        currentY = 0;
                        component.setLocation(originalLocation);
                        ((Timer)e.getSource()).stop();
                        return;
                    }
                }
                
                component.setLocation(originalLocation.x, originalLocation.y + currentY);
            }
        });
        timer.start();
    }
    
    /**
     * Zoom in animation.
     * 
     * @param component the component to zoom
     * @param durationMs duration of the animation in milliseconds
     */
    public static void zoomIn(JComponent component, int durationMs) {
        Dimension originalSize = component.getSize();
        component.setSize(0, 0);
        component.setVisible(true);
        
        Timer timer = new Timer(20, new ActionListener() {
            int currentWidth = 0;
            int currentHeight = 0;
            int stepW = originalSize.width / (durationMs / 20);
            int stepH = originalSize.height / (durationMs / 20);
            
            @Override
            public void actionPerformed(ActionEvent e) {
                currentWidth += stepW;
                currentHeight += stepH;
                
                if (currentWidth >= originalSize.width) {
                    component.setSize(originalSize);
                    ((Timer)e.getSource()).stop();
                    return;
                }
                
                component.setSize(currentWidth, currentHeight);
                component.revalidate();
            }
        });
        timer.start();
    }
}