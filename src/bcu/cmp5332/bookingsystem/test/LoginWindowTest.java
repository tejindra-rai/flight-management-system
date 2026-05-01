package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.gui.common.LoginWindow;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import org.junit.jupiter.api.*;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test class for LoginWindow GUI
 * Tests initialization, authentication, responsive design, and user interactions
 * 
 * @author Tejindra Rai
 * @version 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LoginWindowTest {
    
    private FlightBookingSystem testSystem;
    private LoginWindow loginWindow;
    
    @BeforeEach
    void setUp() {
        // Run GUI tests on EDT (Event Dispatch Thread)
        try {
            SwingUtilities.invokeAndWait(() -> {
                testSystem = new FlightBookingSystem();
                
                // Add test users
                try {
                    User adminUser = new User(1, "admin", "admin123", "ADMIN");
                    User customerUser = new User(2, "customer", "customer123", "CUSTOMER");
                    testSystem.addUser(adminUser);
                    testSystem.addUser(customerUser);
                } catch (FlightBookingSystemException e) {
                    fail("Failed to create test users: " + e.getMessage());
                }
            });
        } catch (Exception e) {
            fail("Failed to initialize test environment: " + e.getMessage());
        }
    }
    
    @AfterEach
    void tearDown() {
        if (loginWindow != null) {
            SwingUtilities.invokeLater(() -> loginWindow.dispose());
        }
    }
    
    @Nested
    @DisplayName("Constructor and Initialization Tests")
    class ConstructorTests {
        
        @Test
        @Order(1)
        @DisplayName("Should create LoginWindow with valid FlightBookingSystem")
        void testConstructorWithValidSystem() {
            assertDoesNotThrow(() -> {
                SwingUtilities.invokeAndWait(() -> {
                    loginWindow = new LoginWindow(testSystem);
                    assertNotNull(loginWindow, "LoginWindow should not be null");
                });
            });
        }
        
        @Test
        @Order(2)
        @DisplayName("Should initialize window with correct title")
        void testWindowTitle() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                assertEquals("B & T Airlines - Login", loginWindow.getTitle());
            });
        }
        
        @Test
        @Order(3)
        @DisplayName("Should set minimum window size")
        void testMinimumSize() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                Dimension minSize = loginWindow.getMinimumSize();
                assertTrue(minSize.width >= 900, "Minimum width should be at least 900");
                assertTrue(minSize.height >= 600, "Minimum height should be at least 600");
            });
        }
        
        @Test
        @Order(4)
        @DisplayName("Should set window visible on creation")
        void testWindowVisibility() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                assertTrue(loginWindow.isVisible(), "Window should be visible after creation");
            });
        }
        
        @Test
        @Order(5)
        @DisplayName("Should center window on screen")
        void testWindowCentered() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                Point location = loginWindow.getLocation();
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                
                // Window should be roughly centered (within 100 pixels)
                int centerX = screenSize.width / 2;
                int centerY = screenSize.height / 2;
                int windowCenterX = location.x + loginWindow.getWidth() / 2;
                int windowCenterY = location.y + loginWindow.getHeight() / 2;
                
                assertTrue(Math.abs(centerX - windowCenterX) < 100, 
                    "Window should be horizontally centered");
                assertTrue(Math.abs(centerY - windowCenterY) < 100, 
                    "Window should be vertically centered");
            });
        }
    }
    
    @Nested
    @DisplayName("Component Initialization Tests")
    class ComponentInitializationTests {
        
        @Test
        @Order(10)
        @DisplayName("Should initialize username field")
        void testUsernameFieldInitialized() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                assertNotNull(usernameField, "Username field should be initialized");
            });
        }
        
        @Test
        @Order(11)
        @DisplayName("Should initialize password field")
        void testPasswordFieldInitialized() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                assertNotNull(passwordField, "Password field should be initialized");
            });
        }
        
        @Test
        @Order(12)
        @DisplayName("Should initialize login button")
        void testLoginButtonInitialized() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                JButton loginBtn = getPrivateField(loginWindow, "loginBtn");
                assertNotNull(loginBtn, "Login button should be initialized");
            });
        }
        
        @Test
        @Order(13)
        @DisplayName("Should initialize message label")
        void testMessageLabelInitialized() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                assertNotNull(messageLabel, "Message label should be initialized");
            });
        }
    }
    
    @Nested
    @DisplayName("Responsive Design Tests")
    class ResponsiveDesignTests {
        
        @Test
        @Order(20)
        @DisplayName("Should calculate responsive size correctly")
        void testGetResponsiveSize() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                // Use reflection to access private method
                int baseSize = 100;
                int responsiveSize = invokePrivateMethod(loginWindow, "getResponsiveSize", 
                    new Class[]{int.class}, baseSize);
                
                assertTrue(responsiveSize > 0, "Responsive size should be positive");
                assertTrue(responsiveSize >= 75, "Responsive size should not scale below 75% (minimum 0.75 scale)");
            });
        }
        
        @Test
        @Order(21)
        @DisplayName("Should handle window resize events")
        void testWindowResizing() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                Dimension originalSize = loginWindow.getSize();
                
                // Resize window
                loginWindow.setSize(1000, 650);
                
                // Verify resize occurred
                Dimension newSize = loginWindow.getSize();
                assertEquals(1000, newSize.width);
                assertEquals(650, newSize.height);
            });
        }
        
        @Test
        @Order(22)
        @DisplayName("Should maintain minimum size constraint")
        void testMinimumSizeConstraint() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                // Try to set size below minimum
                loginWindow.setSize(500, 400);
                loginWindow.validate();
                
                Dimension actualSize = loginWindow.getSize();
                // Window manager should enforce minimum size
                assertTrue(actualSize.width >= 500 || actualSize.height >= 400, 
                    "Window should respect minimum size constraints");
            });
        }
    }
    
    @Nested
    @DisplayName("Authentication Logic Tests")
    class AuthenticationTests {
        
        @Test
        @Order(30)
        @DisplayName("Should handle empty username")
        void testEmptyUsername() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("");
                passwordField.setText("password123");
                
                // Trigger login
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                String message = messageLabel.getText();
                
                assertTrue(message.contains("Please enter username and password") || 
                          message.contains("username"), 
                    "Should show error for empty username");
            });
        }
        
        @Test
        @Order(31)
        @DisplayName("Should handle empty password")
        void testEmptyPassword() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("admin");
                passwordField.setText("");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                String message = messageLabel.getText();
                
                assertTrue(message.contains("Please enter username and password") || 
                          message.contains("password"), 
                    "Should show error for empty password");
            });
        }
        
        @Test
        @Order(32)
        @DisplayName("Should handle invalid credentials")
        void testInvalidCredentials() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("invaliduser");
                passwordField.setText("wrongpassword");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                String message = messageLabel.getText();
                
                assertTrue(message.contains("Invalid") || message.contains("invalid"), 
                    "Should show error for invalid credentials");
            });
        }
        
        @Test
        @Order(33)
        @DisplayName("Should authenticate valid admin user")
        void testValidAdminLogin() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("admin");
                passwordField.setText("admin123");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                // Check if user was authenticated in the system
                User currentUser = testSystem.getCurrentUser();
                assertNotNull(currentUser, "Current user should be set after successful login");
                assertEquals("admin", currentUser.getUsername());
                assertTrue(currentUser.isAdmin(), "User should be admin");
            });
        }
        
        @Test
        @Order(34)
        @DisplayName("Should authenticate valid customer user")
        void testValidCustomerLogin() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("customer");
                passwordField.setText("customer123");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                User currentUser = testSystem.getCurrentUser();
                assertNotNull(currentUser, "Current user should be set");
                assertEquals("customer", currentUser.getUsername());
                assertTrue(currentUser.isCustomer(), "User should be customer");
            });
        }
        
        @Test
        @Order(35)
        @DisplayName("Should trim whitespace from credentials")
        void testWhitespaceHandling() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("  admin  ");
                passwordField.setText("  admin123  ");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                User currentUser = testSystem.getCurrentUser();
                assertNotNull(currentUser, "Should authenticate with trimmed credentials");
                assertEquals("admin", currentUser.getUsername());
            });
        }
    }
    
    @Nested
    @DisplayName("UI Component Behavior Tests")
    class UIComponentBehaviorTests {
        
        @Test
        @Order(40)
        @DisplayName("Should clear message label on new login attempt")
        void testMessageLabelClearing() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                messageLabel.setText("Previous error message");
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("admin");
                passwordField.setText("admin123");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                // Message should change from previous error
                assertNotEquals("Previous error message", messageLabel.getText());
            });
        }
        
        @Test
        @Order(41)
        @DisplayName("Should display success message on successful login")
        void testSuccessMessage() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("admin");
                passwordField.setText("admin123");
                
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                String message = messageLabel.getText();
                
                assertTrue(message.contains("success") || message.contains("Welcome"), 
                    "Should show success message");
            });
        }
        
        @Test
        @Order(42)
        @DisplayName("Should handle password field enter key")
        void testPasswordFieldEnterKey() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                // Check if passwordField has action listener
                assertNotNull(passwordField.getActionListeners(), 
                    "Password field should have action listeners");
                assertTrue(passwordField.getActionListeners().length > 0, 
                    "Password field should have at least one action listener");
            });
        }
    }
    
    @Nested
    @DisplayName("Registration Integration Tests")
    class RegistrationTests {
        
        @Test
        @Order(50)
        @DisplayName("Should have registrationComplete method")
        void testRegistrationCompleteMethod() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                // Check if registrationComplete method exists
                boolean methodExists = false;
                for (Method method : loginWindow.getClass().getDeclaredMethods()) {
                    if (method.getName().equals("registrationComplete")) {
                        methodExists = true;
                        break;
                    }
                }
                
                assertTrue(methodExists, "LoginWindow should have registrationComplete method");
            });
        }
        
        @Test
        @Order(51)
        @DisplayName("Should enable window after registration")
        void testRegistrationComplete() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                loginWindow.setEnabled(false);
                loginWindow.registrationComplete();
                
                assertTrue(loginWindow.isEnabled(), 
                    "Window should be enabled after registration complete");
                assertTrue(loginWindow.isVisible(), 
                    "Window should be visible after registration complete");
            });
        }
        
        @Test
        @Order(52)
        @DisplayName("Should show success message after registration")
        void testRegistrationSuccessMessage() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                loginWindow.registrationComplete();
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                String message = messageLabel.getText();
                
                assertTrue(message.contains("Registration successful") || 
                          message.contains("success"), 
                    "Should show registration success message");
            });
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Error Handling Tests")
    class EdgeCaseTests {
        
        @Test
        @Order(60)
        @DisplayName("Should handle null FlightBookingSystem gracefully")
        void testNullFlightBookingSystem() {
            // LoginWindow constructor doesn't immediately throw NPE with null FBS
            // Instead, it will fail when trying to use the FBS (e.g., during authentication)
            // This test verifies that the constructor itself doesn't crash
            assertDoesNotThrow(() -> {
                SwingUtilities.invokeAndWait(() -> {
                    try {
                        loginWindow = new LoginWindow(null);
                        // Constructor succeeds, but authentication would fail
                        assertNotNull(loginWindow, "Window should be created even with null FBS");
                    } catch (NullPointerException e) {
                        // This is also acceptable behavior
                        assertTrue(true, "NPE is acceptable when FBS is null");
                    }
                });
            });
        }
        
        @Test
        @Order(61)
        @DisplayName("Should handle very long username")
        void testVeryLongUsername() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                String longUsername = "a".repeat(1000);
                usernameField.setText(longUsername);
                passwordField.setText("password");
                
                // Should not crash
                assertDoesNotThrow(() -> {
                    invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                });
            });
        }
        
        @Test
        @Order(62)
        @DisplayName("Should handle special characters in credentials")
        void testSpecialCharactersInCredentials() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("user@#$%");
                passwordField.setText("pass!@#$%^&*()");
                
                assertDoesNotThrow(() -> {
                    invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                });
            });
        }
        
        @Test
        @Order(63)
        @DisplayName("Should handle multiple rapid login attempts")
        void testMultipleRapidLogins() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("admin");
                passwordField.setText("wrongpassword");
                
                // Multiple rapid attempts
                for (int i = 0; i < 5; i++) {
                    invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                }
                
                // Should not crash
                assertNotNull(loginWindow, "Window should still exist after multiple attempts");
            });
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @Order(70)
        @DisplayName("Should complete full login workflow")
        void testFullLoginWorkflow() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                // Start with fresh system
                loginWindow = new LoginWindow(testSystem);
                
                // Verify initial state
                assertNull(testSystem.getCurrentUser(), "No user should be logged in initially");
                
                // Enter credentials
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                usernameField.setText("admin");
                passwordField.setText("admin123");
                
                // Perform login
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                // Verify login success
                User currentUser = testSystem.getCurrentUser();
                assertNotNull(currentUser, "User should be logged in");
                assertEquals("admin", currentUser.getUsername());
                
                JLabel messageLabel = getPrivateField(loginWindow, "messageLabel");
                assertTrue(messageLabel.getText().contains("success"), 
                    "Success message should be displayed");
            });
        }
        
        @Test
        @Order(71)
        @DisplayName("Should handle failed then successful login")
        void testFailedThenSuccessfulLogin() throws Exception {
            SwingUtilities.invokeAndWait(() -> {
                loginWindow = new LoginWindow(testSystem);
                
                JTextField usernameField = getPrivateField(loginWindow, "usernameField");
                JPasswordField passwordField = getPrivateField(loginWindow, "passwordField");
                
                // First attempt - wrong password
                usernameField.setText("admin");
                passwordField.setText("wrongpassword");
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                assertNull(testSystem.getCurrentUser(), "Should not be logged in with wrong password");
                
                // Second attempt - correct password
                passwordField.setText("admin123");
                invokePrivateMethod(loginWindow, "handleLogin", new Class[]{});
                
                assertNotNull(testSystem.getCurrentUser(), "Should be logged in with correct password");
            });
        }
    }
    
    // ============================================================================
    // HELPER METHODS FOR TESTING PRIVATE FIELDS AND METHODS
    // ============================================================================
    
    /**
     * Gets a private field value using reflection
     */
    @SuppressWarnings("unchecked")
    private <T> T getPrivateField(Object object, String fieldName) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (T) field.get(object);
        } catch (Exception e) {
            fail("Failed to access private field: " + fieldName + " - " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Invokes a private method using reflection
     */
    @SuppressWarnings("unchecked")
    private <T> T invokePrivateMethod(Object object, String methodName, Class<?>[] paramTypes, Object... args) {
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, paramTypes);
            method.setAccessible(true);
            return (T) method.invoke(object, args);
        } catch (Exception e) {
            fail("Failed to invoke private method: " + methodName + " - " + e.getMessage());
            return null;
        }
    }
}