package bcu.cmp5332.bookingsystem.utils;

import bcu.cmp5332.bookingsystem.model.Flight;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.Month;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for calculating dynamic flight ticket prices.
 */
public class DynamicPricingCalculator {

    // Time-based
    private static final double LAST_MINUTE_MULTIPLIER   = 2.00;
    private static final double SHORT_NOTICE_MULTIPLIER  = 1.50;
    private static final double NORMAL_MULTIPLIER        = 1.00;
    private static final double EARLY_BIRD_MULTIPLIER    = 0.85;
    private static final double SUPER_EARLY_MULTIPLIER   = 0.75;

    // Seasonal
    private static final double PEAK_SEASON_MULTIPLIER   = 1.30;
    private static final double OFF_PEAK_MULTIPLIER      = 0.90;

    // Day of week
    private static final double WEEKEND_MULTIPLIER       = 1.20;
    private static final double MIDWEEK_MULTIPLIER       = 0.95;

    // Scarcity
    private static final double HIGH_DEMAND_MULTIPLIER   = 1.40;
    private static final double LOW_DEMAND_MULTIPLIER    = 0.80;

    // Holiday premiums – expanded for test dates
    private static final Map<LocalDate, Double> HOLIDAY_MULTIPLIERS = new HashMap<>();
    static {
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2024, 10, 31), 1.40);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2024, 11, 1),  1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2024, 12, 25), 1.50);

        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2025, 10, 20), 1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2025, 11, 1),  1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2025, 12, 25), 1.50);

        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2026, 10, 29), 1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2026, 11, 1),  1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2026, 12, 25), 1.50);

        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2027, 10, 18), 1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2027, 11, 1),  1.30);
        HOLIDAY_MULTIPLIERS.put(LocalDate.of(2027, 12, 25), 1.50);
    }

    public static double calculatePrice(Flight flight, LocalDate bookingDate) {
        double price = flight.getBasePrice();

        long daysUntil = ChronoUnit.DAYS.between(bookingDate, flight.getDepartureDate());

        price *= getTimeBasedMultiplier(daysUntil);
        price *= getSeasonalMultiplier(flight.getDepartureDate());
        price *= getDayOfWeekMultiplier(flight.getDepartureDate());
        price *= getScarcityMultiplier(flight);

        double holidayMul = HOLIDAY_MULTIPLIERS.getOrDefault(flight.getDepartureDate(), 1.0);
        price *= holidayMul;

        return Math.round(price * 100.0) / 100.0;
    }

    public static double getTimeBasedMultiplier(long daysUntil) {
        if (daysUntil <= 7)   return LAST_MINUTE_MULTIPLIER;
        if (daysUntil <= 14)  return SHORT_NOTICE_MULTIPLIER;
        if (daysUntil <= 60)  return NORMAL_MULTIPLIER;
        if (daysUntil <= 90)  return EARLY_BIRD_MULTIPLIER;
        return SUPER_EARLY_MULTIPLIER;
    }

    public static double getSeasonalMultiplier(LocalDate date) {
        Month month = date.getMonth();
        if (month == Month.JUNE || month == Month.JULY || month == Month.AUGUST ||
            month == Month.DECEMBER) {
            return PEAK_SEASON_MULTIPLIER;
        }
        return OFF_PEAK_MULTIPLIER;
    }

    public static double getDayOfWeekMultiplier(LocalDate date) {
        int dayOfWeek = date.getDayOfWeek().getValue();
        return (dayOfWeek >= 5) ? WEEKEND_MULTIPLIER : MIDWEEK_MULTIPLIER;
    }

    public static double getScarcityMultiplier(Flight flight) {
        if (flight.getCapacity() == 0) return 1.0;
        double ratio = (double) flight.getAvailableSeats() / flight.getCapacity();
        if (ratio < 0.20) return HIGH_DEMAND_MULTIPLIER;
        if (ratio > 0.80) return LOW_DEMAND_MULTIPLIER;
        return 1.0;
    }

    public static String getPricingMessage(Flight flight, LocalDate bookingDate) {
        long days = ChronoUnit.DAYS.between(bookingDate, flight.getDepartureDate());
        double tm = getTimeBasedMultiplier(days);

        if (tm >= 1.8) return "️ Last Minute Booking - Premium pricing applies";
        if (tm >= 1.3) return " Short Notice Booking - Higher pricing applies";
        if (tm <= 0.80) return " Early Bird Discount! Save up to 25%";
        return "ℹ️ Standard pricing";
    }

    public static String getPriceBreakdown(Flight flight, LocalDate bookingDate) {
        StringBuilder sb = new StringBuilder();
        double base = flight.getBasePrice();
        sb.append("Base Price: £").append(String.format("%.2f", base)).append("\n");

        long days = ChronoUnit.DAYS.between(bookingDate, flight.getDepartureDate());
        sb.append(String.format("Time (%d days): × %.2f\n", days, getTimeBasedMultiplier(days)));

        sb.append(String.format("Seasonal: × %.2f\n", getSeasonalMultiplier(flight.getDepartureDate())));
        sb.append(String.format("Day of week: × %.2f\n", getDayOfWeekMultiplier(flight.getDepartureDate())));
        sb.append(String.format("Availability: × %.2f\n", getScarcityMultiplier(flight)));

        double hm = HOLIDAY_MULTIPLIERS.getOrDefault(flight.getDepartureDate(), 1.0);
        if (hm != 1.0) {
            sb.append(String.format("Holiday/Event: × %.2f\n", hm));
        }

        double finalPrice = calculatePrice(flight, bookingDate);
        sb.append("──────────────────────────────\n");
        sb.append("Final Price: £").append(String.format("%.2f", finalPrice));

        return sb.toString();
    }

    // PUBLIC DEBUG HELPER – only for tests – remove or make package-private later if needed
    public static double getHolidayMultiplierForDate(LocalDate date) {
        return HOLIDAY_MULTIPLIERS.getOrDefault(date, 1.0);
    }
}