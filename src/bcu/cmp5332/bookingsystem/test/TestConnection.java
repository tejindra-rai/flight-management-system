package bcu.cmp5332.bookingsystem.test;
import bcu.cmp5332.bookingsystem.dao.mysql.DatabaseConnectionManager;

public class TestConnection {
    public static void main(String[] args) {
        try {
            DatabaseConnectionManager dbManager = DatabaseConnectionManager.getInstance();
            if (dbManager.testConnection()) {
                System.out.println("✅ Connection successful!");
            } else {
                System.out.println(" Connection failed!");
            }
        } catch (Exception e) {
            System.out.println(" Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}