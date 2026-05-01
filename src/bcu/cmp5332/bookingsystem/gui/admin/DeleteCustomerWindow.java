package bcu.cmp5332.bookingsystem.gui.admin;

import bcu.cmp5332.bookingsystem.commands.DeleteCustomer;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Admin window for deleting customers with maroon theme.
 * 
 * @author Tejindra Rai
 * @version 4.0 - Admin maroon theme with FontAwesome
 */
public class DeleteCustomerWindow extends JFrame implements ActionListener {

    private MainWindow mw;
    private JTextField customerIdText = new JTextField();

    private JButton deleteBtn = new JButton("Delete Customer");
    private JButton cancelBtn = new JButton("Cancel");

    public DeleteCustomerWindow(MainWindow mw) {
        this.mw = mw;
        initialize();
    }

    private void initialize() {
        setTitle("B & T Airlines - Delete Customer");
        setSize(500, 320);
        setLayout(new BorderLayout(0, 0));

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(ColorScheme.DANGER);
        headerPanel.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel iconLabel = new JLabel(FontAwesomeIcon.TIMES_CIRCLE);
        iconLabel.setFont(FontAwesomeIcon.getFont(36));
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel titleLabel = new JLabel("Delete Customer");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setForeground(ColorScheme.TEXT_ON_DARK);
        titleLabel.setAlignmentX(CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Remove customer from system");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitleLabel.setForeground(ColorScheme.withOpacity(ColorScheme.TEXT_ON_DARK, 0.9));
        subtitleLabel.setAlignmentX(CENTER_ALIGNMENT);

        headerPanel.add(iconLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(subtitleLabel);
        
        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        formPanel.setBackground(ColorScheme.CARD_BG);
        
        // Customer ID
        JLabel customerIdLabel = new JLabel("Customer ID:");
        customerIdLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        customerIdLabel.setForeground(ColorScheme.TEXT_PRIMARY);
        formPanel.add(customerIdLabel);
        formPanel.add(customerIdText);
        
        // Warning label
        JLabel warningLabel = new JLabel("<html><b>Warning:</b> Customer will be hidden from system</html>");
        warningLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        warningLabel.setForeground(ColorScheme.WARNING);
        formPanel.add(warningLabel);
        formPanel.add(new JLabel(""));

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2, 15, 0));
        buttonPanel.setBorder(new EmptyBorder(15, 80, 20, 80));
        buttonPanel.setBackground(ColorScheme.CARD_BG);
        
        deleteBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        deleteBtn.setBackground(ColorScheme.DANGER);
        deleteBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        deleteBtn.setFocusPainted(false);
        deleteBtn.setBorderPainted(false);
        
        cancelBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelBtn.setBackground(ColorScheme.TEXT_SECONDARY);
        cancelBtn.setForeground(ColorScheme.TEXT_ON_PRIMARY);
        cancelBtn.setFocusPainted(false);
        cancelBtn.setBorderPainted(false);
        
        buttonPanel.add(deleteBtn);
        buttonPanel.add(cancelBtn);

        deleteBtn.addActionListener(this);
        cancelBtn.addActionListener(this);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setLocationRelativeTo(mw);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == deleteBtn) {
            deleteCustomer();
        } else if (ae.getSource() == cancelBtn) {
            this.dispose();
        }
    }

    private void deleteCustomer() {
        try {
            String customerIdStr = customerIdText.getText().trim();
            
            if (customerIdStr.isEmpty()) {
                throw new FlightBookingSystemException("Customer ID is required!");
            }
            
            int customerId = Integer.parseInt(customerIdStr);
            
            bcu.cmp5332.bookingsystem.model.Customer customer = 
                mw.getFlightBookingSystem().getCustomerByID(customerId);
            
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this customer?\n\n" +
                "Name: " + customer.getName() + "\n" +
                "Email: " + customer.getEmail() + "\n" +
                "Phone: " + customer.getPhone() + "\n" +
                "Bookings: " + customer.getBookings().size() + "\n\n" +
                "They will be hidden from the system.\n" +
                "This action cannot be undone!", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                Command deleteCustomer = new DeleteCustomer(customerId);
                deleteCustomer.execute(mw.getFlightBookingSystem());
                
                FlightBookingSystemData.safeStore(mw.getFlightBookingSystem());
                mw.displayCustomers();
                
                JOptionPane.showMessageDialog(this, 
                    "Customer Deleted Successfully!\n\n" +
                    "Customer #" + customerId + " (" + customer.getName() + ") has been removed.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                this.dispose();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, 
                "Invalid ID format!\n\nPlease enter a number.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        } catch (FlightBookingSystemException ex) {
            JOptionPane.showMessageDialog(this, 
                ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}