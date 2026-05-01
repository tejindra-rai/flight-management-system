package bcu.cmp5332.bookingsystem.utils;

import java.awt.Color;

/**
 * B & T Airlines Color Scheme - CSS-like color variables for consistent branding.
 * 
 * This class provides a centralized color palette similar to CSS custom properties.
 * All colors can be easily modified here to rebrand the entire application.
 * 
 * @author Tejindra Rai
 * @version 1.0 - CSS-like color variables
 */
public class ColorScheme {
    
    // ============================================================================
    // PRIMARY COLORS - Main brand colors (Deep Blue theme)
    // ============================================================================
    
    /** Primary Dark - Deep navy blue for headers and important elements */
    public static final Color PRIMARY_DARK = new Color(0x0A0054);      // #0A0054
    
    /** Primary Medium - Rich purple-blue for accents */
    public static final Color PRIMARY_MEDIUM = new Color(0x1C1175);    // #1C1175
    
    /** Primary Light - Bright blue for interactive elements */
    public static final Color PRIMARY_LIGHT = new Color(0x021382);     // #021382
    
    /** Primary - Default primary color (alias for PRIMARY_MEDIUM) */
    public static final Color PRIMARY = PRIMARY_MEDIUM;
    
    // ============================================================================
    // SECONDARY COLORS - Complementary colors
    // ============================================================================
    
    /** Secondary Dark - Deep teal */
    public static final Color SECONDARY_DARK = new Color(0x00695C);    // #00695C
    
    /** Secondary Medium - Teal accent */
    public static final Color SECONDARY_MEDIUM = new Color(0x00897B);  // #00897B
    
    /** Secondary Light - Light teal */
    public static final Color SECONDARY_LIGHT = new Color(0x4DB6AC);   // #4DB6AC
    
    /** Secondary - Default secondary color */
    public static final Color SECONDARY = SECONDARY_MEDIUM;
    
    // ============================================================================
    // ACCENT COLORS - Special purpose colors
    // ============================================================================
    
    /** Gold - Premium/luxury accent */
    public static final Color GOLD = new Color(0xFFD700);              // #FFD700
    
    /** Gold Light - Lighter gold for subtle accents */
    public static final Color GOLD_LIGHT = new Color(0xFFC107);        // #FFC107
    
    /** Gold Dark - Deeper gold for emphasis */
    public static final Color GOLD_DARK = new Color(0xFFA000);         // #FFA000
    
    // ============================================================================
    // ADMIN COLORS - Maroon theme for admin dashboard
    // ============================================================================
    
    /** Admin Primary - Deep maroon for admin header */
    public static final Color ADMIN_PRIMARY = new Color(0x800020);     // #800020
    
    /** Admin Dark - Darker maroon */
    public static final Color ADMIN_DARK = new Color(0x660019);        // #660019
    
    /** Admin Light - Lighter maroon for accents */
    public static final Color ADMIN_LIGHT = new Color(0xA0002A);       // #A0002A
    
    /** Admin Accent - Burgundy accent color */
    public static final Color ADMIN_ACCENT = new Color(0x9B1C31);      // #9B1C31
    
    // ============================================================================
    // SEMANTIC COLORS - Status and feedback colors
    // ============================================================================
    
    /** Success - Green for positive actions */
    public static final Color SUCCESS = new Color(0x2E7D32);           // #2E7D32
    
    /** Success Light - Lighter green */
    public static final Color SUCCESS_LIGHT = new Color(0x4CAF50);     // #4CAF50
    
    /** Warning - Orange for caution */
    public static final Color WARNING = new Color(0xED6C02);           // #ED6C02
    
    /** Warning Light - Lighter orange */
    public static final Color WARNING_LIGHT = new Color(0xFF9800);     // #FF9800
    
    /** Danger - Red for errors and critical actions */
    public static final Color DANGER = new Color(0xD32F2F);            // #D32F2F
    
    /** Danger Light - Lighter red */
    public static final Color DANGER_LIGHT = new Color(0xF44336);      // #F44336
    
    /** Info - Blue for informational messages */
    public static final Color INFO = new Color(0x0288D1);              // #0288D1
    
    /** Info Light - Lighter blue */
    public static final Color INFO_LIGHT = new Color(0x03A9F4);        // #03A9F4
    
    // ============================================================================
    // NEUTRAL COLORS - Backgrounds and text
    // ============================================================================
    
    /** Background - Main page background */
    public static final Color BACKGROUND = new Color(0xF5F7FA);        // #F5F7FA
    
    /** Background Dark - Darker background variant */
    public static final Color BACKGROUND_DARK = new Color(0xE8EAF0);   // #E8EAF0
    
    /** Card Background - White card/panel background */
    public static final Color CARD_BG = Color.WHITE;                   // #FFFFFF
    
    /** Card Shadow - Subtle shadow color */
    public static final Color CARD_SHADOW = new Color(0, 0, 0, 20);    // rgba(0,0,0,0.08)
    
    // ============================================================================
    // TEXT COLORS
    // ============================================================================
    
    /** Text Primary - Main text color (dark gray) */
    public static final Color TEXT_PRIMARY = new Color(0x212121);      // #212121
    
    /** Text Secondary - Lighter text for secondary content */
    public static final Color TEXT_SECONDARY = new Color(0x757575);    // #757575
    
    /** Text Disabled - Grayed out text */
    public static final Color TEXT_DISABLED = new Color(0xBDBDBD);     // #BDBDBD
    
    /** Text On Primary - White text for use on primary colored backgrounds */
    public static final Color TEXT_ON_PRIMARY = Color.WHITE;           // #FFFFFF
    
    /** Text On Dark - White text for dark backgrounds */
    public static final Color TEXT_ON_DARK = Color.WHITE;              // #FFFFFF
    
    // ============================================================================
    // BORDER COLORS
    // ============================================================================
    
    /** Border Light - Light gray border */
    public static final Color BORDER_LIGHT = new Color(0xE0E0E0);      // #E0E0E0
    
    /** Border Medium - Medium gray border */
    public static final Color BORDER_MEDIUM = new Color(0xBDBDBD);     // #BDBDBD
    
    /** Border Dark - Darker border */
    public static final Color BORDER_DARK = new Color(0x9E9E9E);       // #9E9E9E
    
    // ============================================================================
    // HOVER & INTERACTION STATES
    // ============================================================================
    
    /** Hover Background - Light background on hover */
    public static final Color HOVER_BG = new Color(0xF5F5F5);          // #F5F5F5
    
    /** Active Background - Background when element is active/pressed */
    public static final Color ACTIVE_BG = new Color(0xEEEEEE);         // #EEEEEE
    
    // ============================================================================
    // UTILITY METHODS
    // ============================================================================
    
    /**
     * Creates a lighter version of a color (similar to CSS lighten())
     * 
     * @param color the base color
     * @param factor lightening factor (0.0 to 1.0)
     * @return lighter color
     */
    public static Color lighten(Color color, double factor) {
        int r = Math.min(255, (int)(color.getRed() + (255 - color.getRed()) * factor));
        int g = Math.min(255, (int)(color.getGreen() + (255 - color.getGreen()) * factor));
        int b = Math.min(255, (int)(color.getBlue() + (255 - color.getBlue()) * factor));
        return new Color(r, g, b, color.getAlpha());
    }
    
    /**
     * Creates a darker version of a color (similar to CSS darken())
     * 
     * @param color the base color
     * @param factor darkening factor (0.0 to 1.0)
     * @return darker color
     */
    public static Color darken(Color color, double factor) {
        int r = (int)(color.getRed() * (1 - factor));
        int g = (int)(color.getGreen() * (1 - factor));
        int b = (int)(color.getBlue() * (1 - factor));
        return new Color(r, g, b, color.getAlpha());
    }
    
    /**
     * Creates a color with specified opacity (similar to CSS rgba())
     * 
     * @param color the base color
     * @param opacity opacity value (0.0 to 1.0)
     * @return color with opacity
     */
    public static Color withOpacity(Color color, double opacity) {
        int alpha = (int)(255 * Math.max(0, Math.min(1, opacity)));
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    /**
     * Gets a hover state color (slightly darker)
     * 
     * @param color the base color
     * @return hover state color
     */
    public static Color getHoverColor(Color color) {
        return darken(color, 0.1);
    }
    
    /**
     * Gets an active/pressed state color (even darker)
     * 
     * @param color the base color
     * @return active state color
     */
    public static Color getActiveColor(Color color) {
        return darken(color, 0.2);
    }
    
    // ============================================================================
    // PRESET GRADIENTS (for future use)
    // ============================================================================
    
    /**
     * Gets colors for a primary gradient
     * @return array of [start, end] colors
     */
    public static Color[] getPrimaryGradient() {
        return new Color[] { PRIMARY_MEDIUM, PRIMARY_LIGHT };
    }
    
    /**
     * Gets colors for a gold gradient
     * @return array of [start, end] colors
     */
    public static Color[] getGoldGradient() {
        return new Color[] { GOLD_DARK, GOLD_LIGHT };
    }
}