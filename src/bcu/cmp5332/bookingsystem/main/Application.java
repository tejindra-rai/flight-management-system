package bcu.cmp5332.bookingsystem.main;

import bcu.cmp5332.bookingsystem.data.FlightBookingSystemData;
import bcu.cmp5332.bookingsystem.gui.common.LoginWindow;
import bcu.cmp5332.bookingsystem.gui.common.SplashScreen;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.SwingUtilities;

public class Application {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                FlatLightLaf.setup();

                // Show splash screen
                SplashScreen splash = new SplashScreen();
                splash.showSplash();

                // Load data in background
                new Thread(() -> {
                    try {
                        Thread.sleep(2000);

                        FlightBookingSystem fbs = FlightBookingSystemData.safeLoad();
                        SwingUtilities.invokeLater(() -> {
                            splash.closeSplash();
                            new LoginWindow(fbs);
                        });

                    } catch (Exception ex) {
                        ex.printStackTrace();
                        splash.closeSplash();
                    }
                }).start();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
