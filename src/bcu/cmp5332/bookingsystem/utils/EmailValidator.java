package bcu.cmp5332.bookingsystem.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Utility class for validating email addresses.
 * Ensures emails meet proper formatting requirements.
 * 
 * @author Tejindra Rai
 * @version 2.0
 */
public class EmailValidator {
    
    // Regex pattern for email validation
    // Format: username@domain.extension
    private static final String EMAIL_PATTERN = 
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
        "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    
    private static final Pattern pattern = Pattern.compile(EMAIL_PATTERN);
    
    /**
     * Validates an email address.
     * 
     * @param email the email address to validate
     * @return true if email is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        Matcher matcher = pattern.matcher(email.trim());
        return matcher.matches();
    }
    
    /**
     * Validates an email address and throws exception if invalid.
     * 
     * @param email the email address to validate
     * @throws IllegalArgumentException if email is invalid
     */
    public static void validateEmailOrThrow(String email) throws IllegalArgumentException {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email address cannot be empty.");
        }
        
        String trimmedEmail = email.trim();
        
        // Check for @ symbol
        if (!trimmedEmail.contains("@")) {
            throw new IllegalArgumentException("Email must contain @ symbol.");
        }
        
        // Check for spaces
        if (trimmedEmail.contains(" ")) {
            throw new IllegalArgumentException("Email cannot contain spaces.");
        }
        
        // Check for domain
        String[] parts = trimmedEmail.split("@");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Email must have exactly one @ symbol.");
        }
        
        if (parts[0].isEmpty()) {
            throw new IllegalArgumentException("Email must have a username before @.");
        }
        
        if (parts[1].isEmpty()) {
            throw new IllegalArgumentException("Email must have a domain after @.");
        }
        
        // Check for extension
        if (!parts[1].contains(".")) {
            throw new IllegalArgumentException(
                "Email domain must have an extension (e.g., .com, .co.uk)."
            );
        }
        
        // Use regex for final validation
        if (!isValidEmail(trimmedEmail)) {
            throw new IllegalArgumentException(
                "Email format is invalid. Expected format: user@domain.extension"
            );
        }
    }
    
    /**
     * Gets a user-friendly validation message for an email.
     * 
     * @param email the email to validate
     * @return validation message (empty string if valid, error message if invalid)
     */
    public static String getValidationMessage(String email) {
        try {
            validateEmailOrThrow(email);
            return "";
        } catch (IllegalArgumentException e) {
            return e.getMessage();
        }
    }
    
    /**
     * Example usage and testing method.
     * 
     * @param args command line arguments (not used)
     */
    public static void main(String[] args) {
        String[] testEmails = {
            "john.doe@example.com",
            "user@domain.co.uk",
            "invalid.email",
            "user @domain.com",
            "@domain.com",
            "user@.com",
            "user@domain",
            "user@@domain.com",
            "jane_doe123@company.org"
        };
        
        System.out.println("Email Validation Tests:");
        System.out.println("═══════════════════════════════════════");
        
        for (String email : testEmails) {
            boolean valid = isValidEmail(email);
            String message = getValidationMessage(email);
            System.out.printf(
                "%-30s → %s%s%n",
                email,
                valid ? "✅ VALID" : "❌ INVALID",
                message.isEmpty() ? "" : " (" + message + ")"
            );
        }
    }
}
