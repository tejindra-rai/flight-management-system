package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.utils.EmailValidator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for EmailValidator utility
 * Tests email validation logic, edge cases, and error handling
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
class EmailValidatorTest {
    
    @Nested
    @DisplayName("Valid Email Tests")
    class ValidEmailTests {
        
        @Test
        @DisplayName("Should accept simple valid email")
        void testSimpleValidEmail() {
            assertTrue(EmailValidator.isValidEmail("user@example.com"));
        }
        
        @Test
        @DisplayName("Should accept email with dots in username")
        void testEmailWithDotsInUsername() {
            assertTrue(EmailValidator.isValidEmail("john.doe@example.com"));
            assertTrue(EmailValidator.isValidEmail("first.middle.last@example.com"));
        }
        
        @Test
        @DisplayName("Should accept email with numbers")
        void testEmailWithNumbers() {
            assertTrue(EmailValidator.isValidEmail("user123@example.com"));
            assertTrue(EmailValidator.isValidEmail("123user@example.com"));
            assertTrue(EmailValidator.isValidEmail("user@example123.com"));
        }
        
        @Test
        @DisplayName("Should accept email with underscores")
        void testEmailWithUnderscores() {
            assertTrue(EmailValidator.isValidEmail("user_name@example.com"));
            assertTrue(EmailValidator.isValidEmail("first_last@example.com"));
        }
        
        @Test
        @DisplayName("Should accept email with plus sign")
        void testEmailWithPlusSign() {
            assertTrue(EmailValidator.isValidEmail("user+tag@example.com"));
            assertTrue(EmailValidator.isValidEmail("john+spam@example.com"));
        }
        
        @Test
        @DisplayName("Should accept email with hyphens in domain")
        void testEmailWithHyphensInDomain() {
            assertTrue(EmailValidator.isValidEmail("user@my-domain.com"));
            assertTrue(EmailValidator.isValidEmail("user@example-company.org"));
        }
        
        @Test
        @DisplayName("Should accept email with subdomain")
        void testEmailWithSubdomain() {
            assertTrue(EmailValidator.isValidEmail("user@mail.example.com"));
            assertTrue(EmailValidator.isValidEmail("user@subdomain.company.co.uk"));
        }
        
        @Test
        @DisplayName("Should accept various TLD extensions")
        void testVariousTLDExtensions() {
            assertTrue(EmailValidator.isValidEmail("user@example.com"));
            assertTrue(EmailValidator.isValidEmail("user@example.org"));
            assertTrue(EmailValidator.isValidEmail("user@example.net"));
            assertTrue(EmailValidator.isValidEmail("user@example.co.uk"));
            assertTrue(EmailValidator.isValidEmail("user@example.edu"));
            assertTrue(EmailValidator.isValidEmail("user@example.gov"));
            assertTrue(EmailValidator.isValidEmail("user@example.io"));
        }
        
        @Test
        @DisplayName("Should accept email with ampersand")
        void testEmailWithAmpersand() {
            assertTrue(EmailValidator.isValidEmail("user&name@example.com"));
        }
        
        @Test
        @DisplayName("Should accept email with asterisk")
        void testEmailWithAsterisk() {
            assertTrue(EmailValidator.isValidEmail("user*name@example.com"));
        }
        
        @Test
        @DisplayName("Should accept email with hyphen in username")
        void testEmailWithHyphenInUsername() {
            assertTrue(EmailValidator.isValidEmail("user-name@example.com"));
            assertTrue(EmailValidator.isValidEmail("first-last@example.com"));
        }
    }
    
    @Nested
    @DisplayName("Invalid Email Tests")
    class InvalidEmailTests {
        
        @Test
        @DisplayName("Should reject null email")
        void testNullEmail() {
            assertFalse(EmailValidator.isValidEmail(null));
        }
        
        @Test
        @DisplayName("Should reject empty email")
        void testEmptyEmail() {
            assertFalse(EmailValidator.isValidEmail(""));
            assertFalse(EmailValidator.isValidEmail("   "));
        }
        
        @Test
        @DisplayName("Should reject email without @ symbol")
        void testEmailWithoutAtSymbol() {
            assertFalse(EmailValidator.isValidEmail("userexample.com"));
            assertFalse(EmailValidator.isValidEmail("user.example.com"));
        }
        
        @Test
        @DisplayName("Should reject email with multiple @ symbols")
        void testEmailWithMultipleAtSymbols() {
            assertFalse(EmailValidator.isValidEmail("user@@example.com"));
            assertFalse(EmailValidator.isValidEmail("user@domain@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email with spaces")
        void testEmailWithSpaces() {
            assertFalse(EmailValidator.isValidEmail("user @example.com"));
            assertFalse(EmailValidator.isValidEmail("user@ example.com"));
            assertFalse(EmailValidator.isValidEmail("user@example .com"));
            assertFalse(EmailValidator.isValidEmail("user name@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email without username")
        void testEmailWithoutUsername() {
            assertFalse(EmailValidator.isValidEmail("@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email without domain")
        void testEmailWithoutDomain() {
            assertFalse(EmailValidator.isValidEmail("user@"));
            assertFalse(EmailValidator.isValidEmail("user@.com"));
        }
        
        @Test
        @DisplayName("Should reject email without extension")
        void testEmailWithoutExtension() {
            assertFalse(EmailValidator.isValidEmail("user@domain"));
        }
        
        @Test
        @DisplayName("Should reject email with only dots")
        void testEmailWithOnlyDots() {
            assertFalse(EmailValidator.isValidEmail("...@example.com"));
            assertFalse(EmailValidator.isValidEmail("user@..."));
        }
        
        @Test
        @DisplayName("Should reject email starting with dot")
        void testEmailStartingWithDot() {
            assertFalse(EmailValidator.isValidEmail(".user@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email ending with dot before @")
        void testEmailEndingWithDotBeforeAt() {
            assertFalse(EmailValidator.isValidEmail("user.@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email with consecutive dots")
        void testEmailWithConsecutiveDots() {
            assertFalse(EmailValidator.isValidEmail("user..name@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email with special characters")
        void testEmailWithInvalidSpecialCharacters() {
            assertFalse(EmailValidator.isValidEmail("user#name@example.com"));
            assertFalse(EmailValidator.isValidEmail("user$name@example.com"));
            assertFalse(EmailValidator.isValidEmail("user%name@example.com"));
            assertFalse(EmailValidator.isValidEmail("user!name@example.com"));
        }
        
        @Test
        @DisplayName("Should reject email with comma")
        void testEmailWithComma() {
            assertFalse(EmailValidator.isValidEmail("user,name@example.com"));
        }
    }
    
    @Nested
    @DisplayName("ValidateEmailOrThrow Tests")
    class ValidateEmailOrThrowTests {
        
        @Test
        @DisplayName("Should not throw exception for valid email")
        void testValidEmailNoException() {
            assertDoesNotThrow(() -> 
                EmailValidator.validateEmailOrThrow("user@example.com")
            );
        }
        
        @Test
        @DisplayName("Should throw exception for null email")
        void testNullEmailThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow(null)
            );
            assertEquals("Email address cannot be empty.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for empty email")
        void testEmptyEmailThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("")
            );
            assertEquals("Email address cannot be empty.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for email without @ symbol")
        void testEmailWithoutAtThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("userexample.com")
            );
            assertEquals("Email must contain @ symbol.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for email with spaces")
        void testEmailWithSpacesThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("user @example.com")
            );
            assertEquals("Email cannot contain spaces.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for email with multiple @ symbols")
        void testEmailWithMultipleAtThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("user@@example.com")
            );
            assertEquals("Email must have exactly one @ symbol.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for email without username")
        void testEmailWithoutUsernameThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("@example.com")
            );
            assertEquals("Email must have a username before @.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for email without domain")
        void testEmailWithoutDomainThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("user@")
            );
            // When splitting "user@" by "@", it only produces 1 part, not 2
            assertEquals("Email must have exactly one @ symbol.", exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for email without extension")
        void testEmailWithoutExtensionThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("user@domain")
            );
            assertEquals("Email domain must have an extension (e.g., .com, .co.uk).", 
                        exception.getMessage());
        }
        
        @Test
        @DisplayName("Should throw exception for invalid email format")
        void testInvalidFormatThrowsException() {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow("user..name@example.com")
            );
            assertEquals("Email format is invalid. Expected format: user@domain.extension", 
                        exception.getMessage());
        }
    }
    
    @Nested
    @DisplayName("GetValidationMessage Tests")
    class GetValidationMessageTests {
        
        @Test
        @DisplayName("Should return empty string for valid email")
        void testValidEmailReturnsEmptyMessage() {
            String message = EmailValidator.getValidationMessage("user@example.com");
            assertEquals("", message);
        }
        
        @Test
        @DisplayName("Should return error message for null email")
        void testNullEmailReturnsMessage() {
            String message = EmailValidator.getValidationMessage(null);
            assertEquals("Email address cannot be empty.", message);
        }
        
        @Test
        @DisplayName("Should return error message for empty email")
        void testEmptyEmailReturnsMessage() {
            String message = EmailValidator.getValidationMessage("");
            assertEquals("Email address cannot be empty.", message);
        }
        
        @Test
        @DisplayName("Should return error message for email without @")
        void testEmailWithoutAtReturnsMessage() {
            String message = EmailValidator.getValidationMessage("userexample.com");
            assertEquals("Email must contain @ symbol.", message);
        }
        
        @Test
        @DisplayName("Should return error message for email with spaces")
        void testEmailWithSpacesReturnsMessage() {
            String message = EmailValidator.getValidationMessage("user @example.com");
            assertEquals("Email cannot contain spaces.", message);
        }
        
        @Test
        @DisplayName("Should return error message for email with multiple @")
        void testEmailWithMultipleAtReturnsMessage() {
            String message = EmailValidator.getValidationMessage("user@@example.com");
            assertEquals("Email must have exactly one @ symbol.", message);
        }
        
        @Test
        @DisplayName("Should return error message for email without username")
        void testEmailWithoutUsernameReturnsMessage() {
            String message = EmailValidator.getValidationMessage("@example.com");
            assertEquals("Email must have a username before @.", message);
        }
        
        @Test
        @DisplayName("Should return error message for email without domain")
        void testEmailWithoutDomainReturnsMessage() {
            String message = EmailValidator.getValidationMessage("user@");
            // When splitting "user@" by "@", it only produces 1 part, not 2
            assertEquals("Email must have exactly one @ symbol.", message);
        }
        
        @Test
        @DisplayName("Should return error message for email without extension")
        void testEmailWithoutExtensionReturnsMessage() {
            String message = EmailValidator.getValidationMessage("user@domain");
            assertEquals("Email domain must have an extension (e.g., .com, .co.uk).", message);
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle email with leading whitespace")
        void testEmailWithLeadingWhitespace() {
            assertTrue(EmailValidator.isValidEmail("  user@example.com"));
        }
        
        @Test
        @DisplayName("Should handle email with trailing whitespace")
        void testEmailWithTrailingWhitespace() {
            assertTrue(EmailValidator.isValidEmail("user@example.com  "));
        }
        
        @Test
        @DisplayName("Should handle email with both leading and trailing whitespace")
        void testEmailWithBothWhitespace() {
            assertTrue(EmailValidator.isValidEmail("  user@example.com  "));
        }
        
        @Test
        @DisplayName("Should handle very long email")
        void testVeryLongEmail() {
            String longUsername = "a".repeat(50);
            String longEmail = longUsername + "@example.com";
            assertTrue(EmailValidator.isValidEmail(longEmail));
        }
        
        @Test
        @DisplayName("Should handle email with short extension")
        void testEmailWithShortExtension() {
            assertTrue(EmailValidator.isValidEmail("user@example.co"));
        }
        
        @Test
        @DisplayName("Should handle email with maximum length extension")
        void testEmailWithMaxLengthExtension() {
            assertTrue(EmailValidator.isValidEmail("user@example.museum"));
        }
        
        @Test
        @DisplayName("Should reject email with extension too long")
        void testEmailWithTooLongExtension() {
            assertFalse(EmailValidator.isValidEmail("user@example.verylongext"));
        }
        
        @Test
        @DisplayName("Should handle single character username")
        void testSingleCharacterUsername() {
            assertTrue(EmailValidator.isValidEmail("a@example.com"));
        }
        
        @Test
        @DisplayName("Should handle single character domain")
        void testSingleCharacterDomain() {
            assertTrue(EmailValidator.isValidEmail("user@a.com"));
        }
    }
    
    @Nested
    @DisplayName("Real-World Email Examples Tests")
    class RealWorldEmailTests {
        
        @Test
        @DisplayName("Should accept common email providers")
        void testCommonEmailProviders() {
            assertTrue(EmailValidator.isValidEmail("user@gmail.com"));
            assertTrue(EmailValidator.isValidEmail("user@yahoo.com"));
            assertTrue(EmailValidator.isValidEmail("user@outlook.com"));
            assertTrue(EmailValidator.isValidEmail("user@hotmail.com"));
            assertTrue(EmailValidator.isValidEmail("user@icloud.com"));
        }
        
        @Test
        @DisplayName("Should accept corporate email formats")
        void testCorporateEmailFormats() {
            assertTrue(EmailValidator.isValidEmail("john.doe@company.com"));
            assertTrue(EmailValidator.isValidEmail("j.doe@company.co.uk"));
            assertTrue(EmailValidator.isValidEmail("johndoe@company-name.com"));
        }
        
        @Test
        @DisplayName("Should accept educational email formats")
        void testEducationalEmailFormats() {
            assertTrue(EmailValidator.isValidEmail("student@university.edu"));
            assertTrue(EmailValidator.isValidEmail("professor@school.ac.uk"));
        }
        
        @Test
        @DisplayName("Should accept government email formats")
        void testGovernmentEmailFormats() {
            assertTrue(EmailValidator.isValidEmail("contact@agency.gov"));
            assertTrue(EmailValidator.isValidEmail("info@department.gov.uk"));
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should validate multiple emails consistently")
        void testMultipleEmailsConsistently() {
            String[] validEmails = {
                "user@example.com",
                "john.doe@company.org",
                "jane_smith@mail.co.uk"
            };
            
            for (String email : validEmails) {
                assertTrue(EmailValidator.isValidEmail(email),
                    "Email " + email + " should be valid");
                assertEquals("", EmailValidator.getValidationMessage(email),
                    "Email " + email + " should have no error message");
            }
        }
        
        @Test
        @DisplayName("Should reject multiple invalid emails consistently")
        void testMultipleInvalidEmailsConsistently() {
            String[] invalidEmails = {
                "invalid.email",
                "user @domain.com",
                "@domain.com",
                "user@",
                "user@@domain.com"
            };
            
            for (String email : invalidEmails) {
                assertFalse(EmailValidator.isValidEmail(email),
                    "Email " + email + " should be invalid");
                assertFalse(EmailValidator.getValidationMessage(email).isEmpty(),
                    "Email " + email + " should have an error message");
            }
        }
        
        @Test
        @DisplayName("Should handle validation with all three methods consistently")
        void testAllMethodsConsistent() {
            String validEmail = "user@example.com";
            
            // All methods should agree it's valid
            assertTrue(EmailValidator.isValidEmail(validEmail));
            assertDoesNotThrow(() -> EmailValidator.validateEmailOrThrow(validEmail));
            assertEquals("", EmailValidator.getValidationMessage(validEmail));
            
            String invalidEmail = "invalid.email";
            
            // All methods should agree it's invalid
            assertFalse(EmailValidator.isValidEmail(invalidEmail));
            assertThrows(IllegalArgumentException.class,
                () -> EmailValidator.validateEmailOrThrow(invalidEmail));
            assertFalse(EmailValidator.getValidationMessage(invalidEmail).isEmpty());
        }
    }
}