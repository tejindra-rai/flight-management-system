package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.gui.MainWindow;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;

import javax.swing.SwingUtilities;
import java.io.*;

/**
 * Main entry point for the Flight Booking System.
 * Supports both console and GUI modes with user authentication.
 * Features interactive menu system for easy navigation.
 * 
 * @author Tejindra Rai & Binay Chaudhary
 * @version 3.0 - Enhanced with menu system and GUI launcher
 */
public class Main {

    private static final String VERSION = "3.0";
    private static FlightBookingSystem fbs;
    private static User currentUser;
    private static BufferedReader br;

    public static void main(String[] args) {
        try {
            br = new BufferedReader(new InputStreamReader(System.in));
            
            // Display welcome screen
            displayWelcomeScreen();
            
            // Load data
            System.out.println("Loading system data...\n");
            fbs = FlightBookingSystemData.load();
            
            // Show main menu
            mainMenu();
            
        } catch (Exception ex) {
            System.err.println("Critical error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Displays the welcome screen with ASCII art.
     */
    private static void displayWelcomeScreen() {
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("                                                          ");
        System.out.println("              FLIGHT BOOKING SYSTEM                       ");
        System.out.println("                                                          ");
        System.out.println("                    Version " + VERSION + "                          ");
        System.out.println("                                                          ");
        System.out.println("          Professional Flight Reservation System          ");
        System.out.println("                                                          ");
        System.out.println("==========================================================");
        System.out.println();
    }

    /**
     * Main menu - choose between GUI and Console mode.
     */
    private static void mainMenu() throws IOException {
        while (true) {
            System.out.println("\n==========================================================");
            System.out.println("                      MAIN MENU                           ");
            System.out.println("==========================================================");
            System.out.println();
            System.out.println("  1. Launch GUI (Graphical User Interface)");
            System.out.println("  2. Console Mode (Text-based Interface)");
            System.out.println("  3. About System");
            System.out.println("  4. System Statistics");
            System.out.println("  5. Exit");
            System.out.println();
            System.out.print("Select an option (1-5): ");
            
            String choice = br.readLine();
            if (choice == null) {
                continue;
            }
            choice = choice.trim();
            
            switch (choice) {
                case "1":
                case "gui":
                    launchGUI();
                    break;
                case "2":
                case "console":
                    consoleMode();
                    break;
                case "3":
                case "about":
                    showAbout();
                    break;
                case "4":
                case "stats":
                    showStatistics();
                    break;
                case "5":
                case "exit":
                case "quit":
                    exitSystem();
                    return;
                default:
                    System.out.println("Invalid option. Please enter 1-5.");
            }
        }
    }

    /**
     * Launches the GUI version.
     */
    private static void launchGUI() {
        System.out.println("\nLaunching GUI...");
        System.out.println("The graphical interface will open in a new window.");
        System.out.println("You will need to log in through the GUI.");
        System.out.println("Tip: You can minimize this console window.\n");
        
        SwingUtilities.invokeLater(() -> {
            try {
                // Launch the login window - it will handle showing MainWindow after login
                bcu.cmp5332.bookingsystem.gui.common.LoginWindow loginWindow = 
                    new bcu.cmp5332.bookingsystem.gui.common.LoginWindow(fbs);
                loginWindow.setVisible(true);
            } catch (Exception ex) {
                System.err.println("Failed to launch GUI: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
        
        System.out.println("GUI login window launched successfully!");
        System.out.println("Note: Please log in through the GUI window.");
        System.out.println("Console menu still available after GUI login.");
        pauseScreen();
    }

    /**
     * Console mode with login and command execution.
     */
    private static void consoleMode() throws IOException {
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("                    CONSOLE MODE                          ");
        System.out.println("==========================================================");
        System.out.println();
        
        // Login
        if (!login()) {
            return;
        }
        
        // Show user menu based on role
        if (currentUser.isAdmin()) {
            adminConsoleMenu();
        } else {
            customerConsoleMenu();
        }
    }

    /**
     * User login process.
     */
    private static boolean login() throws IOException {
        System.out.println("Please login to continue:\n");
        
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username: ");
            String username = br.readLine();
            if (username == null) {
                return false;
            }
            username = username.trim();
            
            if (username.equalsIgnoreCase("back")) {
                return false;
            }
            
            System.out.print("Password: ");
            String password = br.readLine();
            if (password == null) {
                return false;
            }
            
            currentUser = fbs.authenticate(username, password);
            
            if (currentUser != null) {
                System.out.println("\nLogin successful!");
                System.out.println("Welcome back, " + currentUser.getUsername() + "!");
                System.out.println("Role: " + currentUser.getRole());
                System.out.println();
                pauseScreen();
                return true;
            } else {
                attempts++;
                if (attempts < 3) {
                    System.out.println("Invalid credentials. Attempts remaining: " + (3 - attempts));
                    System.out.println("Tip: Type 'back' as username to return to main menu.\n");
                }
            }
        }
        
        System.out.println("\nToo many failed attempts. Returning to main menu...");
        pauseScreen();
        return false;
    }

    /**
     * Admin console menu.
     */
    private static void adminConsoleMenu() throws IOException {
        while (true) {
            clearScreen();
            System.out.println("==========================================================");
            System.out.println("                  ADMIN CONSOLE MENU                      ");
            System.out.println("==========================================================");
            System.out.println();
            System.out.println("  Flight Management:");
            System.out.println("    - addflight      : Add a new flight");
            System.out.println("    - listflights    : View all flights");
            System.out.println("    - showflight     : Show flight details");
            System.out.println("    - deleteflight   : Delete a flight");
            System.out.println();
            System.out.println("  Customer Management:");
            System.out.println("    - addcustomer    : Add a new customer");
            System.out.println("    - listcustomers  : View all customers");
            System.out.println("    - showcustomer   : Show customer details");
            System.out.println("    - deletecustomer : Delete a customer");
            System.out.println();
            System.out.println("  Navigation:");
            System.out.println("    - help           : Show all commands");
            System.out.println("    - mainmenu (or back) : Return to main menu");
            System.out.println("    - logout         : Logout");
            System.out.println("    - exit           : Save and exit");
            System.out.println();
            
            if (!executeCommands()) {
                break;
            }
        }
    }

    /**
     * Customer console menu.
     */
    private static void customerConsoleMenu() throws IOException {
        while (true) {
            clearScreen();
            System.out.println("==========================================================");
            System.out.println("                CUSTOMER CONSOLE MENU                     ");
            System.out.println("==========================================================");
            System.out.println();
            System.out.println("  Flight Operations:");
            System.out.println("    - listflights              : Browse available flights");
            System.out.println("    - showflight [flight id]   : View flight details");
            System.out.println();
            System.out.println("  Booking Operations:");
            System.out.println("    - addbooking [flight id]   : Book a flight");
            System.out.println("    - cancelbooking [flight id]: Cancel your booking");
            System.out.println("    - editbooking [flight id]  : Modify your booking");
            System.out.println("    - rebookflight [flight id] : Rebook a cancelled flight");
            System.out.println();
            System.out.println("  Account:");
            System.out.println("    - showcustomer             : View my profile");
            System.out.println("    - help                     : Show all commands");
            System.out.println();
            System.out.println("  Navigation:");
            System.out.println("    - mainmenu (or back)       : Return to main menu");
            System.out.println("    - logout                   : Logout");
            System.out.println("    - exit                     : Save and exit");
            System.out.println();
            
            if (!executeCommands()) {
                break;
            }
        }
    }

    /**
     * Execute commands in a loop.
     */
    private static boolean executeCommands() throws IOException {
        while (true) {
            System.out.print(currentUser.getRole() + " > ");
            String line = br.readLine();
            
            if (line == null) {
                continue;
            }
            
            line = line.trim();
            
            if (line.isEmpty()) {
                continue;
            }
            
            if (line.equalsIgnoreCase("exit")) {
                try {
                    FlightBookingSystemData.store(fbs);
                    System.out.println("Data saved successfully!");
                } catch (Exception ex) {
                    System.out.println("Warning: Failed to save data: " + ex.getMessage());
                }
                System.out.println("Goodbye, " + currentUser.getUsername() + "!");
                System.exit(0);
            }
            
            if (line.equalsIgnoreCase("logout")) {
                System.out.println("Logging out...");
                currentUser = null;
                pauseScreen();
                return false;
            }
            
            if (line.equalsIgnoreCase("mainmenu") || line.equalsIgnoreCase("back")) {
                System.out.println("Returning to main menu...");
                currentUser = null;
                pauseScreen();
                return false;
            }
            
            if (line.equalsIgnoreCase("menu")) {
                return false;
            }
            
            if (line.equalsIgnoreCase("clear") || line.equalsIgnoreCase("cls")) {
                if (currentUser.isAdmin()) {
                    adminConsoleMenu();
                } else {
                    customerConsoleMenu();
                }
                return true;
            }
            
            try {
                // Provide helpful hints for incomplete commands
                String lowerLine = line.toLowerCase().trim();
                if (lowerLine.equals("showflight")) {
                    System.out.println("ERROR: Missing flight ID.");
                    System.out.println("Usage: showflight [flight id]");
                    System.out.println("Example: showflight 1");
                    System.out.println("Tip: Use 'listflights' first to see available flight IDs.");
                    System.out.println();
                    continue;
                }
                if (lowerLine.equals("addbooking")) {
                    System.out.println("ERROR: Missing flight ID.");
                    System.out.println("Usage: addbooking [flight id]");
                    System.out.println("Example: addbooking 1");
                    System.out.println("Tip: Use 'listflights' first to see available flights.");
                    System.out.println();
                    continue;
                }
                if (lowerLine.equals("cancelbooking")) {
                    System.out.println("ERROR: Missing flight ID.");
                    System.out.println("Usage: cancelbooking [flight id]");
                    System.out.println("Example: cancelbooking 1");
                    System.out.println();
                    continue;
                }
                if (lowerLine.equals("editbooking")) {
                    System.out.println("ERROR: Missing flight ID.");
                    System.out.println("Usage: editbooking [old flight id]");
                    System.out.println("Example: editbooking 1");
                    System.out.println();
                    continue;
                }
                if (lowerLine.equals("rebookflight")) {
                    System.out.println("ERROR: Missing flight ID.");
                    System.out.println("Usage: rebookflight [flight id]");
                    System.out.println("Example: rebookflight 1");
                    System.out.println();
                    continue;
                }
                
                // Pre-process commands for customers to auto-insert their customer ID
                String processedLine = line;
                if (currentUser.isCustomer()) {
                    // Find the customer ID for the logged-in user by searching through customers
                    Integer customerId = null;
                    try {
                        for (bcu.cmp5332.bookingsystem.model.Customer customer : fbs.getCustomers()) {
                            if (customer.getName().equalsIgnoreCase(currentUser.getUsername()) || 
                                customer.getEmail().equalsIgnoreCase(currentUser.getUsername())) {
                                customerId = customer.getId();
                                break;
                            }
                        }
                        
                        if (customerId != null) {
                            // Auto-insert customer ID for booking commands
                            if (line.toLowerCase().startsWith("addbooking ")) {
                                // User typed: addbooking 1
                                // Convert to: addbooking 3 1 (where 3 is their customer ID)
                                String[] parts = line.split("\\s+");
                                if (parts.length == 2) {
                                    processedLine = parts[0] + " " + customerId + " " + parts[1];
                                }
                            } else if (line.toLowerCase().startsWith("cancelbooking ")) {
                                String[] parts = line.split("\\s+");
                                if (parts.length == 2) {
                                    processedLine = parts[0] + " " + customerId + " " + parts[1];
                                }
                            } else if (line.toLowerCase().startsWith("editbooking ")) {
                                String[] parts = line.split("\\s+");
                                if (parts.length == 2) {
                                    processedLine = parts[0] + " " + customerId + " " + parts[1];
                                }
                            } else if (line.toLowerCase().startsWith("rebookflight ")) {
                                String[] parts = line.split("\\s+");
                                if (parts.length == 2) {
                                    processedLine = parts[0] + " " + customerId + " " + parts[1];
                                }
                            } else if (line.toLowerCase().equals("showcustomer")) {
                                // If customer types just "showcustomer", show their own profile
                                processedLine = "showcustomer " + customerId;
                            }
                        }
                    } catch (Exception e) {
                        // If we can't find customer, use original command
                        processedLine = line;
                    }
                }
                
                Command command = CommandParser.parse(processedLine);
                command.execute(fbs);
                System.out.println();
            } catch (FlightBookingSystemException ex) {
                System.out.println("ERROR: " + ex.getMessage());
                System.out.println();
            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getMessage());
                System.out.println();
            }
        }
    }

    /**
     * Shows system information.
     */
    private static void showAbout() {
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("                  ABOUT THIS SYSTEM                       ");
        System.out.println("==========================================================");
        System.out.println();
        System.out.println("  System Name:    Flight Booking System");
        System.out.println("  Version:        " + VERSION);
        System.out.println("  Developer:      Tejindra Rai");
        System.out.println("  Release Date:   February 2026");
        System.out.println("  Institution:    Birmingham City University");
        System.out.println();
        System.out.println("  Key Features:");
        System.out.println("     - Dynamic pricing based on demand");
        System.out.println("     - User authentication & role-based access");
        System.out.println("     - Email & airport code validation");
        System.out.println("     - Booking management (book, cancel, update)");
        System.out.println("     - Feedback system");
        System.out.println("     - Both GUI and Console interfaces");
        System.out.println();
        System.out.println("  Technologies:");
        System.out.println("     - Java " + System.getProperty("java.version"));
        System.out.println("     - Swing GUI");
        System.out.println("     - FlatLaf Look & Feel");
        System.out.println("     - JUnit Testing");
        System.out.println();
        System.out.println("  Contact: tejindra.rai@student.bcu.ac.uk");
        System.out.println();
        pauseScreen();
    }

    /**
     * Shows system statistics.
     */
    private static void showStatistics() {
        clearScreen();
        System.out.println("==========================================================");
        System.out.println("                 SYSTEM STATISTICS                        ");
        System.out.println("==========================================================");
        System.out.println();
        
        try {
            // Count active items
            int activeFlights = 0;
            for (bcu.cmp5332.bookingsystem.model.Flight flight : fbs.getFlights()) {
                if (!flight.isDeleted()) activeFlights++;
            }
            
            int activeCustomers = 0;
            for (bcu.cmp5332.bookingsystem.model.Customer customer : fbs.getCustomers()) {
                if (!customer.isDeleted()) activeCustomers++;
            }
            
            int totalBookings = 0;
            int activeBookings = 0;
            int cancelledBookings = 0;
            
            for (bcu.cmp5332.bookingsystem.model.Customer customer : fbs.getCustomers()) {
                for (bcu.cmp5332.bookingsystem.model.Booking booking : customer.getBookings()) {
                    totalBookings++;
                    if (booking.isCancelled()) {
                        cancelledBookings++;
                    } else {
                        activeBookings++;
                    }
                }
            }
            
            System.out.println("  FLIGHTS:");
            System.out.println("     - Total:      " + fbs.getFlights().size());
            System.out.println("     - Active:     " + activeFlights);
            System.out.println("     - Deleted:    " + (fbs.getFlights().size() - activeFlights));
            System.out.println();
            
            System.out.println("  CUSTOMERS:");
            System.out.println("     - Total:      " + fbs.getCustomers().size());
            System.out.println("     - Active:     " + activeCustomers);
            System.out.println("     - Deleted:    " + (fbs.getCustomers().size() - activeCustomers));
            System.out.println();
            
            System.out.println("  BOOKINGS:");
            System.out.println("     - Total:      " + totalBookings);
            System.out.println("     - Active:     " + activeBookings);
            System.out.println("     - Cancelled:  " + cancelledBookings);
            System.out.println();
            
            System.out.println("  USERS:");
            System.out.println("     - Total:      " + fbs.getUsers().size());
            int adminCount = 0;
            int customerCount = 0;
            for (bcu.cmp5332.bookingsystem.model.User user : fbs.getUsers()) {
                if (user.isAdmin()) adminCount++;
                else customerCount++;
            }
            System.out.println("     - Admins:     " + adminCount);
            System.out.println("     - Customers:  " + customerCount);
            System.out.println();
            
            System.out.println("  FEEDBACK:");
            System.out.println("     - Total:      " + fbs.getAllFeedback().size());
            System.out.println();
            
            System.out.println("  SYSTEM:");
            System.out.println("     - Java:       " + System.getProperty("java.version"));
            System.out.println("     - OS:         " + System.getProperty("os.name"));
            System.out.println("     - Date:       " + fbs.getSystemDate());
            System.out.println();
        } catch (Exception ex) {
            System.out.println("  Error loading statistics: " + ex.getMessage());
        }
        
        pauseScreen();
    }

    /**
     * Exit the system gracefully.
     */
    private static void exitSystem() throws IOException {
        System.out.println("\nSaving all data...");
        
        try {
            FlightBookingSystemData.store(fbs);
            System.out.println("Data saved successfully!");
        } catch (Exception ex) {
            System.out.println("Warning: Some data may not have been saved.");
            System.out.println("Error: " + ex.getMessage());
            System.out.print("Continue exit anyway? (yes/no): ");
            String confirm = br.readLine();
            if (confirm == null) {
                return;
            }
            confirm = confirm.trim().toLowerCase();
            if (!confirm.equals("yes") && !confirm.equals("y")) {
                return;
            }
        }
        
        System.out.println();
        System.out.println("==========================================================");
        System.out.println("                                                          ");
        System.out.println("          Thank you for using our system!                 ");
        System.out.println("                                                          ");
        System.out.println("                    Goodbye!                              ");
        System.out.println("                                                          ");
        System.out.println("==========================================================");
        System.out.println();
        
        System.exit(0);
    }

    /**
     * Clears the screen (platform-independent).
     */
    private static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // If clear fails, just print newlines
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }

    /**
     * Pauses screen and waits for user input.
     */
    private static void pauseScreen() {
        try {
            System.out.print("\nPress Enter to continue...");
            br.readLine();
        } catch (IOException e) {
            // Ignore
        }
    }
}