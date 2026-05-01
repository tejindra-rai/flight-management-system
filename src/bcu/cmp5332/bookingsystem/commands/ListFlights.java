package bcu.cmp5332.bookingsystem.commands;

import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;

import bcu.cmp5332.bookingsystem.model.Flight;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;

import java.util.List;

public class ListFlights implements Command {

    @Override
    public void execute(FlightBookingSystem flightBookingSystem) throws FlightBookingSystemException {
        // Get all flights
        List<Flight> flights = flightBookingSystem.getFlights();
        
        // Print table header
        System.out.println("\n╔════════════════════════════════════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                                           AVAILABLE FLIGHTS                                                ║");
        System.out.println("╠═════╦══════════╦════════╦═════════════╦══════════════╦═══════════╦════════════════╦═══════════════════════╣");
        System.out.println("║  ID ║ Flight # ║ Origin ║ Destination ║     Date     ║   Price   ║     Seats      ║         Class         ║");
        System.out.println("╠═════╬══════════╬════════╬═════════════╬══════════════╬═══════════╬════════════════╬═══════════════════════╣");
        
        // Display each flight in table format
        int count = 0;
        for (Flight flight : flights) {
            if (!flight.isDeleted()) {
                double currentPrice = flight.calculatePrice(flightBookingSystem.getSystemDate());
                String seats = flight.getAvailableSeats() + "/" + flight.getCapacity();
                
                System.out.printf("║ %3d ║ %-8s ║ %-6s ║ %-11s ║ %-12s ║ £%-8.2f ║ %-14s ║ %-21s ║%n",
                    flight.getId(),
                    flight.getFlightNumber(),
                    flight.getOrigin(),
                    flight.getDestination(),
                    flight.getDepartureDate(),
                    currentPrice,
                    seats,
                    flight.getFlightClass()
                );
                count++;
            }
        }
        
        // Print table footer
        System.out.println("╚═════╩══════════╩════════╩═════════════╩══════════════╩═══════════╩════════════════╩═══════════════════════╝");
        System.out.println("Total: " + count + " flight(s)");
    }
}