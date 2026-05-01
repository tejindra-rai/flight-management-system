package bcu.cmp5332.bookingsystem.dao.mysql;

import bcu.cmp5332.bookingsystem.dao.UserDAO;
import bcu.cmp5332.bookingsystem.main.FlightBookingSystemException;
import bcu.cmp5332.bookingsystem.model.FlightBookingSystem;
import bcu.cmp5332.bookingsystem.model.User;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL implementation of UserDAO.
 */
public class UserMySQLDAO implements UserDAO {

    private final DatabaseConnectionManager dbManager;

    public UserMySQLDAO() {
        this.dbManager = DatabaseConnectionManager.getInstance();
    }

    @Override
    public void loadData(FlightBookingSystem fbs) throws IOException, FlightBookingSystemException, SQLException {
        String query = "SELECT * FROM users ORDER BY id";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                User user = new User(id, username, password, role);

                if (rs.getObject("linked_customer_id") != null) {
                    user.setLinkedCustomerId(rs.getInt("linked_customer_id"));
                }

                fbs.addUser(user);
            }
        }
    }

    @Override
    public void storeData(FlightBookingSystem fbs) throws IOException, SQLException {
        // Bulk store not typically used – per-object update is preferred
    }

    @Override
    public User getUserById(int userId) throws SQLException, IOException, FlightBookingSystemException {
        String query = "SELECT * FROM users WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    if (rs.getObject("linked_customer_id") != null) {
                        user.setLinkedCustomerId(rs.getInt("linked_customer_id"));
                    }
                    return user;
                }
                return null;
            }
        }
    }

    @Override
    public User getUserByUsername(String username) throws SQLException, IOException, FlightBookingSystemException {
        String query = "SELECT * FROM users WHERE username = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                    if (rs.getObject("linked_customer_id") != null) {
                        user.setLinkedCustomerId(rs.getInt("linked_customer_id"));
                    }
                    return user;
                }
                return null;
            }
        }
    }

    @Override
    public boolean addUser(User user) throws SQLException, IOException {
        String query = "INSERT INTO users (id, username, password, role, linked_customer_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, user.getId());
            pstmt.setString(2, user.getUsername());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole());

            if (user.getLinkedCustomerId() > 0) {
                pstmt.setInt(5, user.getLinkedCustomerId());
            } else {
                pstmt.setNull(5, Types.INTEGER);
            }

            return pstmt.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateUser(User user) throws SQLException, IOException {
        String query = "UPDATE users SET username = ?, password = ?, role = ?, linked_customer_id = ? WHERE id = ?";

        try (Connection conn = dbManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getRole());

            if (user.getLinkedCustomerId() > 0) {
                pstmt.setInt(4, user.getLinkedCustomerId());
            } else {
                pstmt.setNull(4, Types.INTEGER);
            }

            pstmt.setInt(5, user.getId());

            return pstmt.executeUpdate() > 0;
        }
    }

    // ────────────────────────────────────────────────
    // ADDED: Implementation for getAllUsers() – fixes line 50 error
    @Override
    public List<User> getAllUsers() throws SQLException, IOException, FlightBookingSystemException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users ORDER BY id";

        try (Connection conn = dbManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("role");

                User user = new User(id, username, password, role);

                if (rs.getObject("linked_customer_id") != null) {
                    user.setLinkedCustomerId(rs.getInt("linked_customer_id"));
                }

                users.add(user);
            }
        }
        return users;
    }
}