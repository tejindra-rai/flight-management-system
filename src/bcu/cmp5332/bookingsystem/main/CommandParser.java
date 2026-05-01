package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.commands.LoadGUI;
import bcu.cmp5332.bookingsystem.commands.ShowCustomer;
import bcu.cmp5332.bookingsystem.commands.ShowFlight;
import bcu.cmp5332.bookingsystem.commands.ListFlights;
import bcu.cmp5332.bookingsystem.commands.ListCustomers;
import bcu.cmp5332.bookingsystem.commands.AddBooking;
import bcu.cmp5332.bookingsystem.commands.AddCustomer;
import bcu.cmp5332.bookingsystem.commands.AddFlight;
import bcu.cmp5332.bookingsystem.commands.CancelBooking;
import bcu.cmp5332.bookingsystem.commands.Command;
import bcu.cmp5332.bookingsystem.commands.DeleteCustomer;
import bcu.cmp5332.bookingsystem.commands.DeleteFlight;
import bcu.cmp5332.bookingsystem.commands.EditBooking;
import bcu.cmp5332.bookingsystem.commands.RebookFlight;
import bcu.cmp5332.bookingsystem.commands.Help;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CommandParser {
    
    public static Command parse(String line) throws IOException, FlightBookingSystemException {
        try {
            String[] parts = line.split(" ", 3);
            String cmd = parts[0];

            
            if (cmd.equals("addflight")) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                System.out.print("Flight Number: ");
                String flighNumber = reader.readLine();
                System.out.print("Origin: ");
                String origin = reader.readLine();
                System.out.print("Destination: ");
                String destination = reader.readLine();

                LocalDate departureDate = parseDateWithAttempts(reader);

                return new AddFlight(flighNumber, origin, destination, departureDate);
                
             // Add customer command
            } else if (cmd.equals("addcustomer")) {
            	 BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                 System.out.print("Customer Name: ");
                 String name = reader.readLine();
                 System.out.print("Phone Number: ");
                 String phone = reader.readLine();
                 System.out.print("Email: ");
                 String email = reader.readLine();
                 
                 return new AddCustomer(name, phone, email);
                
              // Load GUI command
            } else if (cmd.equals("loadgui")) {
                return new LoadGUI();
                
             // Commands with no parameters
            } else if (parts.length == 1) {
                if (line.equals("listflights")) {
                    return new ListFlights();
                } else if (line.equals("listcustomers")) {
                    return new ListCustomers();  // FIXED: This was missing
                } else if (line.equals("help")) {
                    return new Help();
                }
                
             // Commands with one parameter (ID)
            } else if (parts.length == 2) {
                int id = Integer.parseInt(parts[1]);
                
                if (cmd.equals("showflight")) {
                    return new ShowFlight(id);
                } else if (cmd.equals("showcustomer")) {
                    return new ShowCustomer(id);
                } else if (cmd.equals("deleteflight")) {
                    return new DeleteFlight(id);
                } else if (cmd.equals("deletecustomer")) {
                    return new DeleteCustomer(id);
                }
                    
             // Commands with two parameters
            } else if (parts.length == 3) {
                String[] ids = parts[1].split(" ");
                int firstId = Integer.parseInt(ids[0]);
                int secondId = Integer.parseInt(parts[2]);
                
                if (cmd.equals("addbooking")) {
                    return new AddBooking(firstId, secondId);
                } else if (cmd.equals("rebook") || cmd.equals("rebookflight")) {
                    return new RebookFlight(firstId, secondId);
                } else if (cmd.equals("editbooking")) {
                    // For edit booking, we need three IDs: customerId, oldFlightId, newFlightId
                    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                    System.out.print("Enter new flight ID: ");
                    int newFlightId = Integer.parseInt(reader.readLine());
                    return new EditBooking(firstId, secondId, newFlightId);
                } else if (cmd.equals("cancelbooking")) {
                    return new CancelBooking(firstId, secondId);
                }
            }
        } catch (NumberFormatException ex) {
        	throw new FlightBookingSystemException("Invalid number format in command.");
        }

        throw new FlightBookingSystemException("Invalid command.");
    }
    
    private static LocalDate parseDateWithAttempts(BufferedReader br, int attempts) throws IOException, FlightBookingSystemException {
        if (attempts < 1) {
            throw new IllegalArgumentException("Number of attempts should be higher that 0");
        }
        while (attempts > 0) {
            attempts--;
            System.out.print("Departure Date (\"YYYY-MM-DD\" format): ");
            try {
                LocalDate departureDate = LocalDate.parse(br.readLine());
                return departureDate;
            } catch (DateTimeParseException dtpe) {
                System.out.println("Date must be in YYYY-MM-DD format. " + attempts + " attempts remaining...");
            }
        }
        
        throw new FlightBookingSystemException("Incorrect departure date provided. Cannot create flight.");
    }
    
    private static LocalDate parseDateWithAttempts(BufferedReader br) throws IOException, FlightBookingSystemException {
        return parseDateWithAttempts(br, 3);
    }
}