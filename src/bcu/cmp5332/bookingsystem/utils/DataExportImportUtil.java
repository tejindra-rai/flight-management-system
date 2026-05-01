package bcu.cmp5332.bookingsystem.utils;

import bcu.cmp5332.bookingsystem.model.*;

import javax.swing.*;
import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Utility class for exporting and importing system data to/from CSV and JSON formats.
 * Used for backups, data migration, and bulk operations.
 * 
 * @author Tejindra Rai
 * @version 1.0 - Admin Enhancement Features
 */
public class DataExportImportUtil {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Exports customers to CSV file.
     */
    public static void exportCustomersToCSV(List<Customer> customers, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("ID,Name,Phone,Email,Age Group,Meal Preference,Has Children,Deleted");
            
            // Write customer data
            for (Customer customer : customers) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\",%b,%b%n",
                    customer.getId(),
                    escapeCSV(customer.getName()),
                    escapeCSV(customer.getPhone()),
                    escapeCSV(customer.getEmail()),
                    escapeCSV(customer.getAgeGroup()),
                    escapeCSV(customer.getMealPreference()),
                    customer.hasChildren(),
                    customer.isDeleted()
                );
            }
        }
    }

    /**
     * Exports flights to CSV file.
     */
    public static void exportFlightsToCSV(List<Flight> flights, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("ID,Flight Number,Origin,Destination,Departure Date,Capacity,Base Price,Flight Class,Return Flight,Deleted,Passengers");
            
            // Write flight data
            for (Flight flight : flights) {
                writer.printf("%d,\"%s\",\"%s\",\"%s\",%s,%d,%.2f,\"%s\",%b,%b,%d%n",
                    flight.getId(),
                    escapeCSV(flight.getFlightNumber()),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureDate().format(DATE_FORMATTER),
                    flight.getCapacity(),
                    flight.getBasePrice(),
                    escapeCSV(flight.getFlightClass()),
                    flight.isReturnFlight(),
                    flight.isDeleted(),
                    flight.getPassengers().size()
                );
            }
        }
    }

    /**
     * Exports bookings to CSV file.
     */
    public static void exportBookingsToCSV(FlightBookingSystem fbs, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            // Write header
            writer.println("Customer ID,Customer Name,Flight ID,Flight Number,Booking Date,Seat,Meal,Price,Cancelled,Cancellation Fee");
            
            // Write booking data
            for (Customer customer : fbs.getCustomers()) {
                for (Booking booking : customer.getBookings()) {
                    writer.printf("%d,\"%s\",%d,\"%s\",%s,\"%s\",\"%s\",%.2f,%b,%.2f%n",
                        customer.getId(),
                        escapeCSV(customer.getName()),
                        booking.getFlight().getId(),
                        escapeCSV(booking.getFlight().getFlightNumber()),
                        booking.getBookingDate().format(DATE_FORMATTER),
                        escapeCSV(booking.getSeatNumber() != null ? booking.getSeatNumber() : "N/A"),
                        escapeCSV(booking.getMealPreference()),
                        booking.getBookingPrice(),
                        booking.isCancelled(),
                        booking.getCancellationFee()
                    );
                }
            }
        }
    }

    /**
     * Exports complete system data to JSON format (simplified version).
     */
    public static void exportSystemToJSON(FlightBookingSystem fbs, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
            writer.println("{");
            writer.println("  \"exportDate\": \"" + LocalDate.now() + "\",");
            writer.println("  \"systemDate\": \"" + fbs.getSystemDate() + "\",");
            
            // Flights
            writer.println("  \"flights\": [");
            List<Flight> flights = fbs.getFlights();
            for (int i = 0; i < flights.size(); i++) {
                Flight f = flights.get(i);
                writer.printf("    {\"id\": %d, \"number\": \"%s\", \"origin\": \"%s\", \"destination\": \"%s\", \"date\": \"%s\", \"capacity\": %d, \"price\": %.2f}%s%n",
                    f.getId(), f.getFlightNumber(), f.getOrigin(), f.getDestination(),
                    f.getDepartureDate(), f.getCapacity(), f.getBasePrice(),
                    i < flights.size() - 1 ? "," : ""
                );
            }
            writer.println("  ],");
            
            // Customers
            writer.println("  \"customers\": [");
            List<Customer> customers = fbs.getCustomers();
            for (int i = 0; i < customers.size(); i++) {
                Customer c = customers.get(i);
                writer.printf("    {\"id\": %d, \"name\": \"%s\", \"email\": \"%s\", \"phone\": \"%s\"}%s%n",
                    c.getId(), escapeJSON(c.getName()), escapeJSON(c.getEmail()), escapeJSON(c.getPhone()),
                    i < customers.size() - 1 ? "," : ""
                );
            }
            writer.println("  ]");
            
            writer.println("}");
        }
    }

    /**
     * Shows export dialog and exports data to selected format.
     */
    public static void showExportDialog(FlightBookingSystem fbs, JFrame parent) {
        String[] options = {"Customers (CSV)", "Flights (CSV)", "Bookings (CSV)", "Complete System (JSON)", "Cancel"};
        int choice = JOptionPane.showOptionDialog(parent,
            "Select data to export:",
            "Export Data",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice >= 0 && choice < 4) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save Export File");
            
            // Set default filename based on choice
            String defaultName = "";
            switch (choice) {
                case 0: defaultName = "customers_export.csv"; break;
                case 1: defaultName = "flights_export.csv"; break;
                case 2: defaultName = "bookings_export.csv"; break;
                case 3: defaultName = "system_export.json"; break;
            }
            fileChooser.setSelectedFile(new File(defaultName));
            
            int result = fileChooser.showSaveDialog(parent);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                
                try {
                    switch (choice) {
                        case 0:
                            exportCustomersToCSV(fbs.getCustomers(), file);
                            break;
                        case 1:
                            exportFlightsToCSV(fbs.getFlights(), file);
                            break;
                        case 2:
                            exportBookingsToCSV(fbs, file);
                            break;
                        case 3:
                            exportSystemToJSON(fbs, file);
                            break;
                    }
                    
                    JOptionPane.showMessageDialog(parent,
                        "Data exported successfully!\n\nFile: " + file.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                        
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(parent,
                        "Error exporting data:\n" + ex.getMessage(),
                        "Export Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    /**
     * Escapes special characters in CSV values.
     */
    private static String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        // Escape quotes by doubling them
        return value.replace("\"", "\"\"");
    }

    /**
     * Escapes special characters in JSON values.
     */
    private static String escapeJSON(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r")
                    .replace("\t", "\\t");
    }

    /**
     * Validates CSV file format for import.
     */
    public static boolean validateCSVFormat(File file, String expectedHeader) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String header = reader.readLine();
            return header != null && header.equals(expectedHeader);
        }
    }

    /**
     * Shows import confirmation dialog.
     */
    public static boolean confirmImport(JFrame parent, String dataType, int recordCount) {
        int result = JOptionPane.showConfirmDialog(parent,
            "Import " + recordCount + " " + dataType + " records?\n\n" +
            "WARNING: This will add new records to the system.\n" +
            "Duplicate IDs may cause errors.",
            "Confirm Import",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        return result == JOptionPane.YES_OPTION;
    }
}