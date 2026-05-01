-- Flight Booking System Database Schema
-- MySQL Database Setup Script
-- Version 1.0

-- Create database
CREATE DATABASE IF NOT EXISTS flight_booking_system;
USE flight_booking_system;

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS feedback;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS flights;
DROP TABLE IF EXISTS users;

-- Create flights table
CREATE TABLE flights (
    id INT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL,
    origin VARCHAR(3) NOT NULL,
    destination VARCHAR(3) NOT NULL,
    departure_date DATE NOT NULL,
    capacity INT DEFAULT 100,
    base_price DECIMAL(10, 2) DEFAULT 100.00,
    deleted BOOLEAN DEFAULT FALSE,
    flight_class VARCHAR(20) DEFAULT 'Economy',
    is_return_flight BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_origin (origin),
    INDEX idx_destination (destination),
    INDEX idx_departure_date (departure_date),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create customers table
CREATE TABLE customers (
    id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    has_children BOOLEAN DEFAULT FALSE,
    age_group VARCHAR(20) DEFAULT 'Adult',
    meal_preference VARCHAR(50) DEFAULT 'None',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_email (email),
    INDEX idx_deleted (deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create bookings table
CREATE TABLE bookings (
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    booking_date DATE NOT NULL,
    cancelled BOOLEAN DEFAULT FALSE,
    booking_price DECIMAL(10, 2) DEFAULT 0.00,
    cancellation_fee DECIMAL(10, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (customer_id, flight_id),
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE,
    INDEX idx_booking_date (booking_date),
    INDEX idx_cancelled (cancelled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create users table for authentication
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'customer',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create feedback table
CREATE TABLE feedback (
    id INT PRIMARY KEY AUTO_INCREMENT,
    customer_id INT NOT NULL,
    flight_id INT NOT NULL,
    rating INT CHECK (rating BETWEEN 1 AND 5),
    comments TEXT,
    feedback_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    FOREIGN KEY (flight_id) REFERENCES flights(id) ON DELETE CASCADE,
    INDEX idx_rating (rating),
    INDEX idx_feedback_date (feedback_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert some sample data for testing

-- Sample Flights
INSERT INTO flights (id, flight_number, origin, destination, departure_date, capacity, base_price, flight_class) VALUES
(1, 'BA101', 'LHR', 'JFK', '2026-03-15', 200, 450.00, 'Economy'),
(2, 'AA202', 'JFK', 'LAX', '2026-03-20', 180, 320.00, 'Economy'),
(3, 'UA303', 'LAX', 'SFO', '2026-03-22', 150, 180.00, 'Business'),
(4, 'DL404', 'SFO', 'SEA', '2026-03-25', 120, 220.00, 'Economy'),
(5, 'SW505', 'SEA', 'DEN', '2026-03-28', 160, 280.00, 'Economy');

-- Sample Customers
INSERT INTO customers (id, name, phone, email, age_group, meal_preference) VALUES
(1, 'John Smith', '+1-555-0101', 'john.smith@email.com', 'Adult', 'None'),
(2, 'Sarah Johnson', '+1-555-0102', 'sarah.j@email.com', 'Adult', 'Vegetarian'),
(3, 'Michael Brown', '+1-555-0103', 'michael.b@email.com', 'Senior', 'Non-Vegetarian'),
(4, 'Emily Davis', '+1-555-0104', 'emily.d@email.com', 'Adult', 'Vegan'),
(5, 'Robert Wilson', '+1-555-0105', 'robert.w@email.com', 'Adult', 'None');

-- Sample Bookings
INSERT INTO bookings (customer_id, flight_id, booking_date, booking_price) VALUES
(1, 1, '2026-02-01', 450.00),
(1, 2, '2026-02-01', 320.00),
(2, 3, '2026-02-05', 180.00),
(3, 1, '2026-02-10', 450.00),
(4, 4, '2026-02-12', 220.00);

-- Sample Users (password should be hashed in production)
INSERT INTO users (username, password, role) VALUES
('admin', 'admin123', 'admin'),
('customer1', 'cust123', 'customer');

-- Display table statistics
SELECT 'Flights Created:' as Status, COUNT(*) as Count FROM flights
UNION ALL
SELECT 'Customers Created:', COUNT(*) FROM customers
UNION ALL
SELECT 'Bookings Created:', COUNT(*) FROM bookings
UNION ALL
SELECT 'Users Created:', COUNT(*) FROM users;

-- Grant privileges (optional - adjust username as needed)
-- GRANT ALL PRIVILEGES ON flight_booking_system.* TO 'your_username'@'localhost';
-- FLUSH PRIVILEGES;

SELECT 'Database setup completed successfully!' as Status;