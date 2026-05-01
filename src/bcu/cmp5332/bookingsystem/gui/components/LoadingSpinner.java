package bcu.cmp5332.bookingsystem.gui.components;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import javax.swing.*;
import java.awt.*;

/**
 * Modern loading spinner component with smooth rotation animation.
 * Can be used standalone or as part of LoadingOverlay.
 *
 * @author Tejindra Rai
 * @version 1.0
 */
public class LoadingSpinner extends JPanel {

    private Timer timer;
    private int angle = 0;
    private Color spinnerColor;
    private int arcSize;

    /**
     * Creates a loading spinner with default size (50px) and primary color.
     */
    public LoadingSpinner() {
        this(50, ColorScheme.PRIMARY_MEDIUM);
    }

    /**
     * Creates a loading spinner with specified size.
     *
     * @param size diameter of the spinner in pixels
     */
    public LoadingSpinner(int size) {
        this(size, ColorScheme.PRIMARY_MEDIUM);
    }

    /**
     * Creates a loading spinner with specified size and color.
     *
     * @param size diameter of the spinner in pixels
     * @param color color of the spinner
     */
    public LoadingSpinner(int size, Color color) {
        this.spinnerColor = color;
        this.arcSize = (int)(size * 0.9);

        setPreferredSize(new Dimension(size, size));
        setOpaque(false);

        timer = new Timer(30, e -> {
            angle = (angle + 12) % 360;
            repaint();
        });
    }

    /**
     * Starts the spinning animation.
     */
    public void start() {
        timer.start();
        setVisible(true);
    }

    /**
     * Stops the spinning animation.
     */
    public void stop() {
        timer.stop();
        setVisible(false);
    }

    /**
     * Checks if the spinner is currently running.
     *
     * @return true if spinning, false otherwise
     */
    public boolean isRunning() {
        return timer.isRunning();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int x = (width - arcSize) / 2;
        int y = (height - arcSize) / 2;

        // Draw rotating arc
        g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2d.setColor(spinnerColor);
        g2d.drawArc(x, y, arcSize, arcSize, angle, 280);

        // Optional: Draw secondary arc for better visual effect
        g2d.setColor(ColorScheme.withOpacity(spinnerColor, 0.3));
        g2d.drawArc(x, y, arcSize, arcSize, angle + 290, 60);

        g2d.dispose();
    }
}