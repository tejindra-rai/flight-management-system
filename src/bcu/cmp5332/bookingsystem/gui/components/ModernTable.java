package bcu.cmp5332.bookingsystem.gui.components;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

/**
 * Modern styled table component with alternating rows, hover effects,
 * and status badge rendering.
 * 
 * Usage:
 *   DefaultTableModel model = new DefaultTableModel(columns, 0);
 *   ModernTable table = new ModernTable(model);
 *   table.setStatusColumn(5); // Mark column 5 as status column
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class ModernTable extends JTable {
    
    /**
     * Creates a modern table with the specified data model.
     * 
     * @param model table data model
     */
    public ModernTable(DefaultTableModel model) {
        super(model);
        setupModernStyling();
    }
    
    /**
     * Creates a modern table with column names and empty data.
     * 
     * @param columnNames array of column names
     */
    public ModernTable(String[] columnNames) {
        super(new DefaultTableModel(columnNames, 0));
        setupModernStyling();
    }
    
    /**
     * Sets up modern styling for the table.
     */
    private void setupModernStyling() {
        // Font and sizing
        setFont(new Font("Segoe UI", Font.PLAIN, 13));
        setRowHeight(40);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setFillsViewportHeight(true);
        
        // Header styling
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(ColorScheme.PRIMARY_MEDIUM);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(0, 45));
        header.setReorderingAllowed(false);
        
        // Custom header renderer for centered text
        ((DefaultTableCellRenderer)header.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
        
        // Custom cell renderer for alternating rows
        setDefaultRenderer(Object.class, new AlternatingRowRenderer());
        
        // Selection colors
        setSelectionBackground(ColorScheme.PRIMARY_LIGHT);
        setSelectionForeground(Color.WHITE);
        
        // Grid appearance
        setGridColor(ColorScheme.BORDER_LIGHT);
    }
    
    /**
     * Sets a column to use status badge rendering.
     * Displays "Active", "Cancelled", "Pending" etc. as colored badges.
     * 
     * @param columnIndex index of the status column
     */
    public void setStatusColumn(int columnIndex) {
        getColumnModel().getColumn(columnIndex).setCellRenderer(new StatusBadgeRenderer());
    }
    
    /**
     * Sets a column to use price rendering (with currency symbol).
     * 
     * @param columnIndex index of the price column
     */
    public void setPriceColumn(int columnIndex) {
        getColumnModel().getColumn(columnIndex).setCellRenderer(new PriceRenderer());
    }
    
    /**
     * Sets a column to use date rendering.
     * 
     * @param columnIndex index of the date column
     */
    public void setDateColumn(int columnIndex) {
        getColumnModel().getColumn(columnIndex).setCellRenderer(new AlternatingRowRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, 
                                                                  isSelected, hasFocus, row, column);
                if (c instanceof JLabel) {
                    ((JLabel) c).setHorizontalAlignment(CENTER);
                }
                return c;
            }
        });
    }
    
    /**
     * Custom renderer for alternating row colors.
     */
    private class AlternatingRowRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                                                              isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(ColorScheme.PRIMARY_LIGHT);
                c.setForeground(Color.WHITE);
            } else {
                c.setBackground(row % 2 == 0 ? Color.WHITE : ColorScheme.BACKGROUND);
                c.setForeground(ColorScheme.TEXT_PRIMARY);
            }
            
            if (c instanceof JLabel) {
                ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            }
            
            return c;
        }
    }
    
    /**
     * Custom renderer for status badges.
     */
    private class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, 
                                                                        isSelected, hasFocus, row, column);
            
            String status = value != null ? value.toString() : "";
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            label.setHorizontalAlignment(CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            // Color coding based on status
            if (status.equalsIgnoreCase("Active") || 
                status.equalsIgnoreCase("Confirmed") ||
                status.equalsIgnoreCase("Success") ||
                status.equalsIgnoreCase("Completed")) {
                
                label.setBackground(ColorScheme.SUCCESS_LIGHT);
                label.setForeground(Color.WHITE);
                
            } else if (status.equalsIgnoreCase("Cancelled") ||
                       status.equalsIgnoreCase("Failed") ||
                       status.equalsIgnoreCase("Error")) {
                
                label.setBackground(ColorScheme.DANGER_LIGHT);
                label.setForeground(Color.WHITE);
                
            } else if (status.equalsIgnoreCase("Pending") ||
                       status.equalsIgnoreCase("Processing") ||
                       status.equalsIgnoreCase("Warning")) {
                
                label.setBackground(ColorScheme.WARNING_LIGHT);
                label.setForeground(Color.WHITE);
                
            } else if (status.equalsIgnoreCase("Info") ||
                       status.equalsIgnoreCase("Available")) {
                
                label.setBackground(ColorScheme.INFO_LIGHT);
                label.setForeground(Color.WHITE);
                
            } else {
                // Default styling for unknown status
                if (!isSelected) {
                    label.setBackground(row % 2 == 0 ? Color.WHITE : ColorScheme.BACKGROUND);
                    label.setForeground(ColorScheme.TEXT_PRIMARY);
                }
            }
            
            return label;
        }
    }
    
    /**
     * Custom renderer for price values.
     */
    private class PriceRenderer extends AlternatingRowRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, 
                                                              isSelected, hasFocus, row, column);
            
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                label.setHorizontalAlignment(RIGHT);
                label.setFont(new Font("Segoe UI", Font.BOLD, 13));
                
                // Ensure price starts with £ symbol
                String text = value != null ? value.toString() : "";
                if (!text.startsWith("£") && !text.isEmpty()) {
                    label.setText("£" + text);
                }
            }
            
            return c;
        }
    }
}