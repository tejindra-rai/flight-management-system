package bcu.cmp5332.bookingsystem.gui.components;

import bcu.cmp5332.bookingsystem.utils.ColorScheme;
import bcu.cmp5332.bookingsystem.utils.FontAwesomeIcon;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern input field with icon, placeholder, and real-time validation.
 * Provides visual feedback for input validation and enhanced user experience.
 * 
 * Usage:
 *   ModernTextField emailField = new ModernTextField(
 *       "Enter your email",
 *       FontAwesomeIcon.ENVELOPE,
 *       text -> EmailValidator.isValidEmail(text),
 *       "Please enter a valid email address"
 *   );
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
public class ModernTextField extends JPanel {
    
    private JTextField textField;
    private JLabel iconLabel;
    private JLabel errorLabel;
    private JPanel inputPanel;
    private String placeholder;
    private ValidationRule validator;
    private boolean isValid = true;
    private boolean showErrorIcon = true;
    
    /**
     * Functional interface for validation rules.
     */
    @FunctionalInterface
    public interface ValidationRule {
        /**
         * Validates the input text.
         * 
         * @param text input to validate
         * @return true if valid, false otherwise
         */
        boolean validate(String text);
    }
    
    /**
     * Creates a modern text field without validation.
     * 
     * @param placeholder placeholder text
     * @param icon FontAwesome icon string
     */
    public ModernTextField(String placeholder, String icon) {
        this(placeholder, icon, null, null);
    }
    
    /**
     * Creates a modern text field with validation.
     * 
     * @param placeholder placeholder text
     * @param icon FontAwesome icon string
     * @param validator validation function
     * @param errorMessage error message to display when validation fails
     */
    public ModernTextField(String placeholder, String icon, ValidationRule validator, String errorMessage) {
        this.placeholder = placeholder;
        this.validator = validator;
        
        setLayout(new BorderLayout(0, 5));
        setBackground(ColorScheme.BACKGROUND);
        setMaximumSize(new Dimension(500, 85));
        
        // Input container with icon and text field
        inputPanel = new JPanel(new BorderLayout(12, 0));
        inputPanel.setBackground(Color.WHITE);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ColorScheme.BORDER_MEDIUM, 1, true),
            new EmptyBorder(12, 15, 12, 15)
        ));
        
        // Icon
        iconLabel = new JLabel(icon);
        iconLabel.setFont(FontAwesomeIcon.getFont(18));
        iconLabel.setForeground(ColorScheme.TEXT_SECONDARY);
        
        // Text field
        textField = new JTextField();
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        textField.setBorder(null);
        textField.setBackground(Color.WHITE);
        
        // Placeholder handling
        textField.setForeground(ColorScheme.TEXT_DISABLED);
        textField.setText(placeholder);
        
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(ColorScheme.TEXT_PRIMARY);
                }
                
                // Focus border
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorScheme.PRIMARY_MEDIUM, 2, true),
                    new EmptyBorder(11, 14, 11, 14)
                ));
                iconLabel.setForeground(ColorScheme.PRIMARY_MEDIUM);
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(ColorScheme.TEXT_DISABLED);
                }
                
                // Validate
                validateInput();
                
                // Reset border if valid
                if (isValid || getText().isEmpty()) {
                    inputPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ColorScheme.BORDER_MEDIUM, 1, true),
                        new EmptyBorder(12, 15, 12, 15)
                    ));
                    iconLabel.setForeground(ColorScheme.TEXT_SECONDARY);
                }
            }
        });
        
        // Real-time validation on typing
        textField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!textField.getText().equals(placeholder)) {
                    validateInput();
                }
            }
        });
        
        inputPanel.add(iconLabel, BorderLayout.WEST);
        inputPanel.add(textField, BorderLayout.CENTER);
        
        // Error label
        errorLabel = new JLabel(errorMessage != null ? errorMessage : " ");
        errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        errorLabel.setForeground(ColorScheme.DANGER);
        errorLabel.setVisible(false);
        
        add(inputPanel, BorderLayout.CENTER);
        add(errorLabel, BorderLayout.SOUTH);
    }
    
    /**
     * Validates the current input.
     */
    private void validateInput() {
        String text = getText();
        
        if (validator != null && !text.isEmpty() && !text.equals(placeholder)) {
            isValid = validator.validate(text);
            
            if (!isValid) {
                // Show error state
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorScheme.DANGER, 2, true),
                    new EmptyBorder(11, 14, 11, 14)
                ));
                iconLabel.setForeground(ColorScheme.DANGER);
                
                if (showErrorIcon) {
                    errorLabel.setText(FontAwesomeIcon.EXCLAMATION_CIRCLE + " " + errorLabel.getText().replace(FontAwesomeIcon.EXCLAMATION_CIRCLE, "").trim());
                }
                errorLabel.setVisible(true);
            } else {
                // Show success state
                inputPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ColorScheme.SUCCESS, 2, true),
                    new EmptyBorder(11, 14, 11, 14)
                ));
                iconLabel.setForeground(ColorScheme.SUCCESS);
                errorLabel.setVisible(false);
            }
        } else {
            isValid = true;
            errorLabel.setVisible(false);
        }
    }
    
    /**
     * Forces validation of the current input.
     * 
     * @return true if valid, false otherwise
     */
    public boolean validateField()  {
        validateInput();
        return isValid;
    }
    
    /**
     * Gets the text from the field (excluding placeholder).
     * 
     * @return the entered text, or empty string if placeholder is showing
     */
    public String getText() {
        String text = textField.getText();
        return text.equals(placeholder) ? "" : text;
    }
    
    /**
     * Sets the text in the field.
     * 
     * @param text text to set
     */
    public void setText(String text) {
        if (text == null || text.isEmpty()) {
            textField.setText(placeholder);
            textField.setForeground(ColorScheme.TEXT_DISABLED);
        } else {
            textField.setText(text);
            textField.setForeground(ColorScheme.TEXT_PRIMARY);
        }
    }
    
    /**
     * Checks if the current input is valid.
     * 
     * @return true if valid, false otherwise
     */
    public boolean isValid() {
        return isValid;
    }
    
    /**
     * Gets the underlying JTextField component.
     * 
     * @return the text field
     */
    public JTextField getTextField() {
        return textField;
    }
    
    /**
     * Sets whether to show error icon in error messages.
     * 
     * @param show true to show icon, false to hide
     */
    public void setShowErrorIcon(boolean show) {
        this.showErrorIcon = show;
    }
    
    /**
     * Sets a new error message.
     * 
     * @param errorMessage new error message
     */
    public void setErrorMessage(String errorMessage) {
        String currentText = errorLabel.getText();
        if (currentText.contains(FontAwesomeIcon.EXCLAMATION_CIRCLE)) {
            errorLabel.setText(FontAwesomeIcon.EXCLAMATION_CIRCLE + " " + errorMessage);
        } else {
            errorLabel.setText(errorMessage);
        }
    }
    
    /**
     * Adds an action listener to the text field.
     * 
     * @param listener action listener
     */
    public void addActionListener(ActionListener listener) {
        textField.addActionListener(listener);
    }
    
    /**
     * Sets whether the field is enabled.
     * 
     * @param enabled true to enable, false to disable
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        textField.setEnabled(enabled);
        inputPanel.setBackground(enabled ? Color.WHITE : ColorScheme.BACKGROUND);
        iconLabel.setForeground(enabled ? ColorScheme.TEXT_SECONDARY : ColorScheme.TEXT_DISABLED);
    }
}