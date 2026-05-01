package bcu.cmp5332.bookingsystem.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * IMPROVED Font Awesome loader with comprehensive icon library and robust fallback
 * 
 * @author Tejindra Rai
 * @version 5.1 - Enhanced with better error handling and emoji fallbacks
 */
public class FontAwesomeIcon {
    
    private static Font fontAwesomeSolid;
    private static Font fontAwesomeRegular;
    private static Font fontAwesomeBrands;
    private static boolean debugMode = false; // Set to true for troubleshooting
    private static boolean fontsLoaded = false;
    
    // ============================================================================
    // FONT AWESOME ICON CODES - Organized by Category
    // ============================================================================
    
    // NAVIGATION & COMMON
    public static final String HOME = "\uf015";
    public static final String HOUSE = "\uf015";  // Alias
    public static final String BARS = "\uf0c9";
    public static final String ARROW_LEFT = "\uf060";
    public static final String ARROW_RIGHT = "\uf061";
    public static final String ARROW_UP = "\uf062";
    public static final String ARROW_DOWN = "\uf063";
    public static final String REFRESH = "\uf01e";
    public static final String ARROW_ROTATE_RIGHT = "\uf01e";  // Alias
    
    // FLIGHTS & TRAVEL
    public static final String PLANE = "\uf072";
    public static final String JET_FIGHTER = "\uf0fb";
    public static final String CALENDAR = "\uf133";
    public static final String CALENDAR_DAYS = "\uf073";
    public static final String CALENDAR_CHECK = "\uf274";
    public static final String CLOCK = "\uf017";
    public static final String CLOCK_ROTATE_LEFT = "\uf1da";
    public static final String MAP_MARKER = "\uf041";
    public static final String LOCATION_DOT = "\uf3c5";
    public static final String GLOBE = "\uf0ac";
    
    // USER & AUTHENTICATION
    public static final String USER = "\uf007";
    public static final String PERSON = "\uf183";
    public static final String USERS = "\uf0c0";
    public static final String USER_GROUP = "\uf500";
    public static final String PEOPLE_GROUP = "\ue533";
    public static final String SIGN_OUT = "\uf08b";
    public static final String RIGHT_FROM_BRACKET = "\uf08b";  // Alias
    public static final String LOCK = "\uf023";
    public static final String ID_CARD = "\uf2c2";
    public static final String SHIELD = "\uf132";
    
    // ACTIONS - ADD/CREATE
    public static final String PLUS_CIRCLE = "\uf055";
    public static final String CIRCLE_PLUS = "\uf055";  // Alias
    
    // ACTIONS - DELETE/REMOVE
    public static final String TIMES_CIRCLE = "\uf057";
    public static final String CIRCLE_XMARK = "\uf057";  // Alias
    public static final String XMARK = "\uf00d";
    public static final String TRASH = "\uf1f8";
    public static final String TRASH_CAN = "\uf2ed";
    
    // ACTIONS - EDIT/MODIFY
    public static final String EDIT = "\uf044";
    public static final String PEN_TO_SQUARE = "\uf044";  // Alias
    
    // ACTIONS - SAVE
    public static final String SAVE = "\uf0c7";
    public static final String FLOPPY_DISK = "\uf0c7";  // Alias
    
    // ACTIONS - SEARCH & FILTER
    public static final String SEARCH = "\uf002";
    public static final String MAGNIFYING_GLASS = "\uf002";  // Alias
    public static final String FILTER = "\uf0b0";
    public static final String SLIDERS = "\uf1de";
    
    // ACTIONS - CONFIRM
    public static final String CHECK = "\uf00c";
    public static final String CHECK_SQUARE = "\uf14a";
    public static final String SQUARE_CHECK = "\uf14a";  // Alias
    public static final String CIRCLE_CHECK = "\uf058";
    
    // INFORMATION & STATUS ICONS
    public static final String INFO_CIRCLE = "\uf05a";
    public static final String CIRCLE_INFO = "\uf05a";  // Alias
    public static final String EXCLAMATION_TRIANGLE = "\uf071";
    public static final String EXCLAMATION_CIRCLE = "\uf06a";
    public static final String CIRCLE_EXCLAMATION = "\uf06a";  // Alias
    public static final String QUESTION_CIRCLE = "\uf059";
    public static final String STAR = "\uf005";
    public static final String RANKING_STAR = "\ue561";
    public static final String BELL = "\uf0f3";
    
    // COMMUNICATION
    public static final String ENVELOPE = "\uf0e0";
    public static final String PHONE = "\uf095";
    public static final String COMMENT = "\uf075";
    
    // DOCUMENTS & FILES
    public static final String FILE = "\uf15b";
    public static final String DOWNLOAD = "\uf019";
    public static final String UPLOAD = "\uf093";
    public static final String PRINT = "\uf02f";
    public static final String FILE_EXPORT = "\uf56e";
    public static final String FILE_IMPORT = "\uf56f";
    
    // LISTS & ORGANIZATION
    public static final String LIST = "\uf03a";
    public static final String TABLE_LIST = "\uf00b";
    public static final String CLIPBOARD_LIST = "\uf46d";
    public static final String SORT = "\uf0dc";
    public static final String SORT_UP = "\uf0de";
    public static final String SORT_DOWN = "\uf0dd";
    public static final String CIRCLE = "\uf111";
    public static final String GRID = "\ue195";
    
    // BOOKING & SERVICES
    public static final String TICKET = "\uf145";
    public static final String UTENSILS = "\uf2e7";
    public static final String BOWL_FOOD = "\ue4c6";
    public static final String CHAIR = "\uf6c0";
    
    // FINANCIAL
    public static final String MONEY_BILL = "\uf0d6";
    public static final String STERLING_SIGN = "\uf154";
    
    // CHARTS & ANALYTICS
    public static final String CHART_LINE = "\uf201";
    public static final String CHART_BAR = "\uf080";
    public static final String CHART_PIE = "\uf200";
    public static final String ARROW_TREND_UP = "\ue098";
    public static final String ARROW_TREND_DOWN = "\ue097";
    
    // UI ELEMENTS
    public static final String EYE = "\uf06e";
    public static final String EYE_SLASH = "\uf070";
    public static final String HEART = "\uf004";
    public static final String COG = "\uf013";
    public static final String GEAR = "\uf013";  // Alias
    
    // BUILDINGS & LOCATIONS
    public static final String BUILDING = "\uf1ad";
    
    /**
     * STRATEGY 1: Try loading from classpath resources
     */
    private static InputStream tryLoadFromClasspath(String filename) {
        if (debugMode) {
            System.out.println("[FontAwesome] Attempting classpath load: " + filename);
        }
        
        String[] paths = {
            "/fonts/otfs/" + filename,
            "/resources/fonts/otfs/" + filename,
            "/fonts/" + filename,
            "/resources/fonts/" + filename,
            "/" + filename
        };
        
        for (String path : paths) {
            try {
                InputStream stream = FontAwesomeIcon.class.getResourceAsStream(path);
                if (stream != null) {
                    if (debugMode) {
                        System.out.println("  ✓ Found at: " + path);
                    }
                    return stream;
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        return null;
    }
    
    /**
     * STRATEGY 2: Try loading from file system (IDE development)
     */
    private static InputStream tryLoadFromFileSystem(String filename) {
        if (debugMode) {
            System.out.println("[FontAwesome] Attempting filesystem load: " + filename);
        }
        
        String[] basePaths = {
            "src/resources/fonts/otfs/",
            "resources/fonts/otfs/",
            "fonts/otfs/",
            "src/fonts/otfs/",
            "bin/resources/fonts/otfs/",
            "out/production/resources/fonts/otfs/"
        };
        
        for (String basePath : basePaths) {
            try {
                File file = new File(basePath + filename);
                if (file.exists()) {
                    if (debugMode) {
                        System.out.println("  ✓ Found at: " + file.getAbsolutePath());
                    }
                    return new java.io.FileInputStream(file);
                }
            } catch (Exception e) {
                // Continue to next path
            }
        }
        
        return null;
    }
    
    /**
     * Main font loading method with fallback strategies
     */
    private static Font loadFont(String filename, String fontName) {
        InputStream stream = null;
        
        // Strategy 1: Classpath
        stream = tryLoadFromClasspath(filename);
        
        // Strategy 2: File system (if classpath failed)
        if (stream == null) {
            stream = tryLoadFromFileSystem(filename);
        }
        
        // Load the font if found
        if (stream != null) {
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, stream);
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(font);
                
                if (debugMode) {
                    System.out.println("✓ " + fontName + " loaded successfully");
                }
                
                stream.close();
                return font;
                
            } catch (Exception e) {
                if (debugMode) {
                    System.err.println("✗ Error loading " + fontName + ": " + e.getMessage());
                }
            }
        }
        
        return null;
    }
    
    /**
     * Static initializer - loads all fonts
     */
    static {
        try {
            // Load fonts silently
            fontAwesomeSolid = loadFont("Font Awesome 7 Free-Solid-900.otf", "FontAwesome Solid");
            fontAwesomeRegular = loadFont("Font Awesome 7 Free-Regular-400.otf", "FontAwesome Regular");
            fontAwesomeBrands = loadFont("Font Awesome 7 Brands-Regular-400.otf", "FontAwesome Brands");
            
            fontsLoaded = (fontAwesomeSolid != null);
            
            if (debugMode) {
                System.out.println("\n=== Font Awesome Status ===");
                System.out.println("Solid:   " + (fontAwesomeSolid != null ? "✓" : "✗"));
                System.out.println("Regular: " + (fontAwesomeRegular != null ? "✓" : "✗"));
                System.out.println("Brands:  " + (fontAwesomeBrands != null ? "✓" : "✗"));
                
                if (!fontsLoaded) {
                    System.out.println("\n⚠ FontAwesome not loaded - using emoji fallbacks");
                }
            }
        } catch (Exception e) {
            System.err.println("FontAwesome initialization error: " + e.getMessage());
            fontsLoaded = false;
        }
    }
    
    /**
     * Gets the Font Awesome Solid font at the specified size.
     * Falls back to system emoji font if FontAwesome is not loaded.
     */
    public static Font getFont(float size) {
        if (fontAwesomeSolid != null) {
            return fontAwesomeSolid.deriveFont(Font.PLAIN, size);
        } else {
            // Fallback to emoji-capable font
            return getEmojiFont(size);
        }
    }
    
    /**
     * Gets an emoji-capable system font as fallback
     */
    private static Font getEmojiFont(float size) {
        // Try different emoji-capable fonts based on OS
        String os = System.getProperty("os.name").toLowerCase();
        String fontName;
        
        if (os.contains("win")) {
            fontName = "Segoe UI Emoji";
        } else if (os.contains("mac")) {
            fontName = "Apple Color Emoji";
        } else {
            fontName = "Noto Color Emoji";
        }
        
        Font font = new Font(fontName, Font.PLAIN, (int)size);
        
        // If that font doesn't exist, fallback to Arial
        if (!font.getFamily().equals(fontName)) {
            font = new Font("Arial Unicode MS", Font.PLAIN, (int)size);
        }
        
        return font;
    }
    
    /**
     * Gets the Font Awesome Regular font at the specified size.
     */
    public static Font getRegularFont(float size) {
        if (fontAwesomeRegular != null) {
            return fontAwesomeRegular.deriveFont(Font.PLAIN, size);
        } else {
            return getFont(size); // Fallback to solid
        }
    }
    
    /**
     * Gets the Font Awesome Brands font at the specified size.
     */
    public static Font getBrandsFont(float size) {
        if (fontAwesomeBrands != null) {
            return fontAwesomeBrands.deriveFont(Font.PLAIN, size);
        } else {
            return getFont(size); // Fallback to solid
        }
    }
    
    /**
     * Creates a JLabel with a Font Awesome icon.
     * Automatically uses emoji fallback if FontAwesome not loaded.
     */
    public static JLabel createIcon(String iconCode, float size, Color color) {
        JLabel label = new JLabel(iconCode);
        label.setFont(getFont(size));
        label.setForeground(color);
        return label;
    }
    
    /**
     * Creates a JButton with a Font Awesome icon and text.
     */
    public static JButton createIconButton(String iconCode, String text, float iconSize) {
        JButton button = new JButton(iconCode + "  " + text);
        button.setFont(getFont(iconSize));
        return button;
    }
    
    /**
     * Sets Font Awesome icon to an existing JLabel.
     */
    public static void setIcon(JLabel label, String iconCode, float size) {
        label.setText(iconCode);
        label.setFont(getFont(size));
    }
    
    /**
     * Checks if Font Awesome is loaded.
     */
    public static boolean isLoaded() {
        return fontsLoaded;
    }
    
    /**
     * Gets an icon string with fallback text.
     * Use this when you want to provide emoji alternatives.
     */
    public static String getIcon(String fontAwesomeCode, String fallback) {
        return isLoaded() ? fontAwesomeCode : fallback;
    }
    
    /**
     * Gets an icon with automatic emoji fallback
     */
    public static String getIconWithEmoji(String fontAwesomeCode, String emojiCode) {
        // If FontAwesome is loaded, use it; otherwise use emoji
        return isLoaded() ? fontAwesomeCode : emojiCode;
    }
    
    /**
     * Enable or disable debug mode
     */
    public static void setDebugMode(boolean debug) {
        debugMode = debug;
    }
    
    /**
     * Get status message about font loading
     */
    public static String getStatusMessage() {
        if (fontsLoaded) {
            return "FontAwesome loaded successfully";
        } else {
            return "FontAwesome not loaded - using emoji fallbacks";
        }
    }
}