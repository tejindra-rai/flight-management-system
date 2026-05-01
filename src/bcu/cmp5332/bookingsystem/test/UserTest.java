package bcu.cmp5332.bookingsystem.test;

import bcu.cmp5332.bookingsystem.model.User;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for User
 * Tests all methods and functionality of the User class
 * 
 * @author Tejindra Rai
 */
class UserTest {
    
    private User adminUser;
    private User customerUser;
    
    @BeforeEach
    void setUp() {
        adminUser = new User(1, "admin", "admin123", "ADMIN");
        customerUser = new User(2, "customer1", "pass123", "CUSTOMER");
    }
    
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {
        
        @Test
        @DisplayName("Should create admin user with correct details")
        void testAdminUserConstructor() {
            assertEquals(1, adminUser.getId());
            assertEquals("admin", adminUser.getUsername());
            assertEquals("admin123", adminUser.getPassword());
            assertEquals("ADMIN", adminUser.getRole());
        }
        
        @Test
        @DisplayName("Should create customer user with correct details")
        void testCustomerUserConstructor() {
            assertEquals(2, customerUser.getId());
            assertEquals("customer1", customerUser.getUsername());
            assertEquals("pass123", customerUser.getPassword());
            assertEquals("CUSTOMER", customerUser.getRole());
        }
        
        @Test
        @DisplayName("Should initialize linkedCustomerId to -1 by default")
        void testDefaultLinkedCustomerId() {
            assertEquals(-1, adminUser.getLinkedCustomerId());
            assertEquals(-1, customerUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should create user with various roles")
        void testVariousRoles() {
            User user1 = new User(3, "user1", "pass", "ADMIN");
            User user2 = new User(4, "user2", "pass", "CUSTOMER");
            User user3 = new User(5, "user3", "pass", "MANAGER");
            
            assertEquals("ADMIN", user1.getRole());
            assertEquals("CUSTOMER", user2.getRole());
            assertEquals("MANAGER", user3.getRole());
        }
    }
    
    @Nested
    @DisplayName("ID Tests")
    class IdTests {
        
        @Test
        @DisplayName("Should get ID correctly")
        void testGetId() {
            assertEquals(1, adminUser.getId());
            assertEquals(2, customerUser.getId());
        }
        
        @Test
        @DisplayName("Should set ID correctly")
        void testSetId() {
            adminUser.setId(10);
            assertEquals(10, adminUser.getId());
        }
        
        @Test
        @DisplayName("Should handle zero ID")
        void testZeroId() {
            adminUser.setId(0);
            assertEquals(0, adminUser.getId());
        }
        
        @Test
        @DisplayName("Should handle negative ID")
        void testNegativeId() {
            adminUser.setId(-1);
            assertEquals(-1, adminUser.getId());
        }
        
        @Test
        @DisplayName("Should update ID multiple times")
        void testUpdateId() {
            adminUser.setId(5);
            assertEquals(5, adminUser.getId());
            
            adminUser.setId(10);
            assertEquals(10, adminUser.getId());
        }
    }
    
    @Nested
    @DisplayName("Username Tests")
    class UsernameTests {
        
        @Test
        @DisplayName("Should get username correctly")
        void testGetUsername() {
            assertEquals("admin", adminUser.getUsername());
            assertEquals("customer1", customerUser.getUsername());
        }
        
        @Test
        @DisplayName("Should set username correctly")
        void testSetUsername() {
            adminUser.setUsername("newadmin");
            assertEquals("newadmin", adminUser.getUsername());
        }
        
        @Test
        @DisplayName("Should handle empty username")
        void testEmptyUsername() {
            adminUser.setUsername("");
            assertEquals("", adminUser.getUsername());
        }
        
        @Test
        @DisplayName("Should handle null username")
        void testNullUsername() {
            adminUser.setUsername(null);
            assertNull(adminUser.getUsername());
        }
        
        @Test
        @DisplayName("Should handle username with special characters")
        void testUsernameWithSpecialChars() {
            adminUser.setUsername("admin_123");
            assertEquals("admin_123", adminUser.getUsername());
            
            adminUser.setUsername("admin@example.com");
            assertEquals("admin@example.com", adminUser.getUsername());
        }
        
        @Test
        @DisplayName("Should handle long username")
        void testLongUsername() {
            String longUsername = "verylongusernamethatexceedstypiallimits123456789";
            adminUser.setUsername(longUsername);
            assertEquals(longUsername, adminUser.getUsername());
        }
    }
    
    @Nested
    @DisplayName("Password Tests")
    class PasswordTests {
        
        @Test
        @DisplayName("Should get password correctly")
        void testGetPassword() {
            assertEquals("admin123", adminUser.getPassword());
            assertEquals("pass123", customerUser.getPassword());
        }
        
        @Test
        @DisplayName("Should set password correctly")
        void testSetPassword() {
            adminUser.setPassword("newpass456");
            assertEquals("newpass456", adminUser.getPassword());
        }
        
        @Test
        @DisplayName("Should handle empty password")
        void testEmptyPassword() {
            adminUser.setPassword("");
            assertEquals("", adminUser.getPassword());
        }
        
        @Test
        @DisplayName("Should handle null password")
        void testNullPassword() {
            adminUser.setPassword(null);
            assertNull(adminUser.getPassword());
        }
        
        @Test
        @DisplayName("Should handle complex password")
        void testComplexPassword() {
            String complexPass = "P@ssw0rd!#$%123";
            adminUser.setPassword(complexPass);
            assertEquals(complexPass, adminUser.getPassword());
        }
        
        @Test
        @DisplayName("Should update password multiple times")
        void testUpdatePassword() {
            adminUser.setPassword("pass1");
            assertEquals("pass1", adminUser.getPassword());
            
            adminUser.setPassword("pass2");
            assertEquals("pass2", adminUser.getPassword());
        }
    }
    
    @Nested
    @DisplayName("Role Tests")
    class RoleTests {
        
        @Test
        @DisplayName("Should get role correctly")
        void testGetRole() {
            assertEquals("ADMIN", adminUser.getRole());
            assertEquals("CUSTOMER", customerUser.getRole());
        }
        
        @Test
        @DisplayName("Should set role correctly")
        void testSetRole() {
            adminUser.setRole("CUSTOMER");
            assertEquals("CUSTOMER", adminUser.getRole());
        }
        
        @Test
        @DisplayName("Should handle lowercase role")
        void testLowercaseRole() {
            adminUser.setRole("admin");
            assertEquals("admin", adminUser.getRole());
        }
        
        @Test
        @DisplayName("Should handle mixed case role")
        void testMixedCaseRole() {
            adminUser.setRole("Admin");
            assertEquals("Admin", adminUser.getRole());
        }
        
        @Test
        @DisplayName("Should handle custom role")
        void testCustomRole() {
            adminUser.setRole("MANAGER");
            assertEquals("MANAGER", adminUser.getRole());
        }
        
        @Test
        @DisplayName("Should handle null role")
        void testNullRole() {
            adminUser.setRole(null);
            assertNull(adminUser.getRole());
        }
        
        @Test
        @DisplayName("Should change role from admin to customer")
        void testChangeRoleAdminToCustomer() {
            assertTrue(adminUser.isAdmin());
            
            adminUser.setRole("CUSTOMER");
            assertFalse(adminUser.isAdmin());
            assertTrue(adminUser.isCustomer());
        }
        
        @Test
        @DisplayName("Should change role from customer to admin")
        void testChangeRoleCustomerToAdmin() {
            assertTrue(customerUser.isCustomer());
            
            customerUser.setRole("ADMIN");
            assertTrue(customerUser.isAdmin());
            assertFalse(customerUser.isCustomer());
        }
    }
    
    @Nested
    @DisplayName("Linked Customer ID Tests")
    class LinkedCustomerIdTests {
        
        @Test
        @DisplayName("Should get default linkedCustomerId as -1")
        void testDefaultLinkedCustomerId() {
            assertEquals(-1, adminUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should set linkedCustomerId correctly")
        void testSetLinkedCustomerId() {
            customerUser.setLinkedCustomerId(100);
            assertEquals(100, customerUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should handle zero linkedCustomerId")
        void testZeroLinkedCustomerId() {
            customerUser.setLinkedCustomerId(0);
            assertEquals(0, customerUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should handle negative linkedCustomerId")
        void testNegativeLinkedCustomerId() {
            customerUser.setLinkedCustomerId(-5);
            assertEquals(-5, customerUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should reset linkedCustomerId to -1")
        void testResetLinkedCustomerId() {
            customerUser.setLinkedCustomerId(100);
            assertEquals(100, customerUser.getLinkedCustomerId());
            
            customerUser.setLinkedCustomerId(-1);
            assertEquals(-1, customerUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should update linkedCustomerId multiple times")
        void testUpdateLinkedCustomerId() {
            customerUser.setLinkedCustomerId(50);
            assertEquals(50, customerUser.getLinkedCustomerId());
            
            customerUser.setLinkedCustomerId(100);
            assertEquals(100, customerUser.getLinkedCustomerId());
        }
    }
    
    @Nested
    @DisplayName("isAdmin Tests")
    class IsAdminTests {
        
        @Test
        @DisplayName("Should return true for ADMIN role")
        void testIsAdminUppercase() {
            assertTrue(adminUser.isAdmin());
        }
        
        @Test
        @DisplayName("Should return false for CUSTOMER role")
        void testIsAdminForCustomer() {
            assertFalse(customerUser.isAdmin());
        }
        
        @Test
        @DisplayName("Should be case-insensitive for admin")
        void testIsAdminCaseInsensitive() {
            User user1 = new User(3, "user1", "pass", "admin");
            User user2 = new User(4, "user2", "pass", "Admin");
            User user3 = new User(5, "user3", "pass", "ADMIN");
            
            assertTrue(user1.isAdmin());
            assertTrue(user2.isAdmin());
            assertTrue(user3.isAdmin());
        }
        
        @Test
        @DisplayName("Should return false for other roles")
        void testIsAdminForOtherRoles() {
            User manager = new User(6, "manager", "pass", "MANAGER");
            assertFalse(manager.isAdmin());
        }
        
        @Test
        @DisplayName("Should return false for null role")
        void testIsAdminForNullRole() {
            adminUser.setRole(null);
            assertFalse(adminUser.isAdmin());
        }
    }
    
    @Nested
    @DisplayName("isCustomer Tests")
    class IsCustomerTests {
        
        @Test
        @DisplayName("Should return true for CUSTOMER role")
        void testIsCustomerUppercase() {
            assertTrue(customerUser.isCustomer());
        }
        
        @Test
        @DisplayName("Should return false for ADMIN role")
        void testIsCustomerForAdmin() {
            assertFalse(adminUser.isCustomer());
        }
        
        @Test
        @DisplayName("Should be case-insensitive for customer")
        void testIsCustomerCaseInsensitive() {
            User user1 = new User(3, "user1", "pass", "customer");
            User user2 = new User(4, "user2", "pass", "Customer");
            User user3 = new User(5, "user3", "pass", "CUSTOMER");
            
            assertTrue(user1.isCustomer());
            assertTrue(user2.isCustomer());
            assertTrue(user3.isCustomer());
        }
        
        @Test
        @DisplayName("Should return false for other roles")
        void testIsCustomerForOtherRoles() {
            User manager = new User(6, "manager", "pass", "MANAGER");
            assertFalse(manager.isCustomer());
        }
        
        @Test
        @DisplayName("Should return false for null role")
        void testIsCustomerForNullRole() {
            customerUser.setRole(null);
            assertFalse(customerUser.isCustomer());
        }
    }
    
    @Nested
    @DisplayName("Password Validation Tests")
    class PasswordValidationTests {
        
        @Test
        @DisplayName("Should validate correct password")
        void testValidateCorrectPassword() {
            assertTrue(adminUser.validatePassword("admin123"));
            assertTrue(customerUser.validatePassword("pass123"));
        }
        
        @Test
        @DisplayName("Should reject incorrect password")
        void testValidateIncorrectPassword() {
            assertFalse(adminUser.validatePassword("wrongpass"));
            assertFalse(customerUser.validatePassword("wrong"));
        }
        
        @Test
        @DisplayName("Should be case-sensitive")
        void testPasswordCaseSensitive() {
            assertFalse(adminUser.validatePassword("ADMIN123"));
            assertFalse(adminUser.validatePassword("Admin123"));
        }
        
        @Test
        @DisplayName("Should reject empty password when password is not empty")
        void testValidateEmptyPassword() {
            assertFalse(adminUser.validatePassword(""));
        }
        
        @Test
        @DisplayName("Should validate empty password when password is empty")
        void testValidateEmptyPasswordMatch() {
            adminUser.setPassword("");
            assertTrue(adminUser.validatePassword(""));
        }
        
        @Test
        @DisplayName("Should handle null password validation")
        void testValidateNullPassword() {
            adminUser.setPassword("test");
            assertFalse(adminUser.validatePassword(null));
        }
        
        @Test
        @DisplayName("Should validate password with special characters")
        void testValidateComplexPassword() {
            String complexPass = "P@ssw0rd!#$%";
            adminUser.setPassword(complexPass);
            assertTrue(adminUser.validatePassword(complexPass));
            assertFalse(adminUser.validatePassword("P@ssw0rd!#$"));
        }
        
        @Test
        @DisplayName("Should validate after password change")
        void testValidateAfterPasswordChange() {
            assertTrue(adminUser.validatePassword("admin123"));
            
            adminUser.setPassword("newpass");
            assertFalse(adminUser.validatePassword("admin123"));
            assertTrue(adminUser.validatePassword("newpass"));
        }
    }
    
    @Nested
    @DisplayName("ToString Tests")
    class ToStringTests {
        
        @Test
        @DisplayName("Should return correct toString representation")
        void testToString() {
            String result = adminUser.toString();
            
            assertTrue(result.contains("User{"));
            assertTrue(result.contains("id=1"));
            assertTrue(result.contains("username='admin'"));
            assertTrue(result.contains("role='ADMIN'"));
        }
        
        @Test
        @DisplayName("Should not include password in toString")
        void testToStringDoesNotIncludePassword() {
            String result = adminUser.toString();
            assertFalse(result.contains("admin123"));
            assertFalse(result.contains("password"));
        }
        
        @Test
        @DisplayName("Should include all user details in toString")
        void testToStringCustomer() {
            String result = customerUser.toString();
            
            assertTrue(result.contains("id=2"));
            assertTrue(result.contains("username='customer1'"));
            assertTrue(result.contains("role='CUSTOMER'"));
        }
    }
    
    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {
        
        @Test
        @DisplayName("Should maintain data integrity across multiple operations")
        void testDataIntegrity() {
            User user = new User(10, "testuser", "testpass", "CUSTOMER");
            
            // Verify initial state
            assertEquals(10, user.getId());
            assertEquals("testuser", user.getUsername());
            assertEquals("testpass", user.getPassword());
            assertEquals("CUSTOMER", user.getRole());
            assertTrue(user.isCustomer());
            assertFalse(user.isAdmin());
            assertEquals(-1, user.getLinkedCustomerId());
            
            // Modify user
            user.setId(20);
            user.setUsername("newuser");
            user.setPassword("newpass");
            user.setRole("ADMIN");
            user.setLinkedCustomerId(100);
            
            // Verify modified state
            assertEquals(20, user.getId());
            assertEquals("newuser", user.getUsername());
            assertEquals("newpass", user.getPassword());
            assertEquals("ADMIN", user.getRole());
            assertTrue(user.isAdmin());
            assertFalse(user.isCustomer());
            assertEquals(100, user.getLinkedCustomerId());
            assertTrue(user.validatePassword("newpass"));
            assertFalse(user.validatePassword("testpass"));
        }
        
        @Test
        @DisplayName("Should handle complete user lifecycle")
        void testUserLifecycle() {
            // 1. Create user
            User user = new User(1, "john", "pass123", "CUSTOMER");
            assertTrue(user.isCustomer());
            assertEquals(-1, user.getLinkedCustomerId());
            
            // 2. Link to customer
            user.setLinkedCustomerId(50);
            assertEquals(50, user.getLinkedCustomerId());
            
            // 3. Change password
            user.setPassword("newpass456");
            assertTrue(user.validatePassword("newpass456"));
            
            // 4. Promote to admin
            user.setRole("ADMIN");
            assertTrue(user.isAdmin());
            assertFalse(user.isCustomer());
            
            // 5. Linked customer ID should remain
            assertEquals(50, user.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should handle admin user with linked customer")
        void testAdminWithLinkedCustomer() {
            adminUser.setLinkedCustomerId(25);
            
            assertTrue(adminUser.isAdmin());
            assertEquals(25, adminUser.getLinkedCustomerId());
        }
        
        @Test
        @DisplayName("Should handle customer user without linked customer")
        void testCustomerWithoutLinkedCustomer() {
            assertTrue(customerUser.isCustomer());
            assertEquals(-1, customerUser.getLinkedCustomerId());
        }
    }
    
    @Nested
    @DisplayName("Edge Cases and Special Scenarios")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle user with very long password")
        void testVeryLongPassword() {
            String longPassword = "a".repeat(1000);
            adminUser.setPassword(longPassword);
            assertTrue(adminUser.validatePassword(longPassword));
        }
        
        @Test
        @DisplayName("Should handle user with whitespace in username")
        void testWhitespaceInUsername() {
            adminUser.setUsername("user name");
            assertEquals("user name", adminUser.getUsername());
        }
        
        @Test
        @DisplayName("Should handle user with whitespace in password")
        void testWhitespaceInPassword() {
            String passWithSpace = "pass word";
            adminUser.setPassword(passWithSpace);
            assertTrue(adminUser.validatePassword(passWithSpace));
            assertFalse(adminUser.validatePassword("password"));
        }
        
        @Test
        @DisplayName("Should handle multiple role changes")
        void testMultipleRoleChanges() {
            User user = new User(1, "user", "pass", "CUSTOMER");
            
            assertTrue(user.isCustomer());
            assertFalse(user.isAdmin());
            
            user.setRole("ADMIN");
            assertTrue(user.isAdmin());
            assertFalse(user.isCustomer());
            
            user.setRole("CUSTOMER");
            assertTrue(user.isCustomer());
            assertFalse(user.isAdmin());
            
            user.setRole("MANAGER");
            assertFalse(user.isAdmin());
            assertFalse(user.isCustomer());
        }
        
        @Test
        @DisplayName("Should handle same username and password")
        void testSameUsernameAndPassword() {
            User user = new User(1, "test", "test", "CUSTOMER");
            assertEquals("test", user.getUsername());
            assertEquals("test", user.getPassword());
            assertTrue(user.validatePassword("test"));
        }
        
        @Test
        @DisplayName("Should handle numeric username")
        void testNumericUsername() {
            adminUser.setUsername("12345");
            assertEquals("12345", adminUser.getUsername());
        }
        
        @Test
        @DisplayName("Should handle special characters in role")
        void testSpecialCharsInRole() {
            adminUser.setRole("ADMIN-SPECIAL");
            assertEquals("ADMIN-SPECIAL", adminUser.getRole());
            assertFalse(adminUser.isAdmin()); // Should be false because it's not exactly "ADMIN"
        }
    }
}