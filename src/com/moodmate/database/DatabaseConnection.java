
package com.moodmate.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.moodmate.logic.User;

public class DatabaseConnection {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/moodMate";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "17Aug1993"; // Replace with your MySQL password

    // Method to get a database connection
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    // Method to check if a username exists in the Authentication table
    public static boolean usernameExists(String username) {
        String query = "SELECT COUNT(*) FROM Authentication WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0; // Return true if username exists
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
 

    // Method to insert a new user into the Authentication table
     public static boolean insertUser(String username, String password, String email) {
       // Check if the username already exists
        if (usernameExists(username)) {
            return false; // Return false if duplicate username
        }
        String query = "INSERT INTO Authentication (username, password, email) VALUES (?, ?, ?)";
        try (Connection connection = getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.executeUpdate();
            return true; // Return true if insertion is a success
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false if insertion fails
        }
     }
    
 // Method to retrieve user_id for a given username
    public static Integer getUserIdByUsername(String username) {
        String query = "SELECT user_id FROM Authentication WHERE username = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("user_id"); // Return the user_id if found
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no user_id is found
    }
        //Get username and password from database
    
        public static List<User> fetchAllUsers() {
            List<User> users = new ArrayList<>();
            String query_signin = "SELECT username, password FROM Authentication";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query_signin);
                 ResultSet rs = stmt.executeQuery()) {

                while (rs.next()) {
                    users.add(new User(rs.getString("username"), rs.getString("password")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return users;
        }
        public static void main(String[] args) {
            String testUsername = "test_user"; // Replace with an actual username from your database
            Integer userId = getUserIdByUsername(testUsername);
            if (userId != null) {
                System.out.println("User ID for username '" + testUsername + "': " + userId);
            } else {
                System.out.println("User not found for username: " + testUsername);
            }
        }
    }






