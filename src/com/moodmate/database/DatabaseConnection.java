
package com.moodmate.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jess.*;
import java.sql.*;
import java.util.*;

import com.moodmate.logic.User;

public class DatabaseConnection {

	private static final String DB_URL = "jdbc:mysql://localhost:3306/moodMate";
    private static final String DB_USER = "root"; // Replace with your MySQL username
    private static final String DB_PASSWORD = "002915"; // Replace with your MySQL password

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

     // Method to fetch user information from the user-info table using user_id
        public static Map<String, Object> fetchUserInfoById(int userId) {
            String query = "SELECT * FROM `user_info` WHERE user_id = ?";
            Map<String, Object> userInfo = new HashMap<>();
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                
                // Set the user_id parameter
                statement.setInt(1, userId);
                
                // Execute the query
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Populate the map with user information
                    userInfo.put("user_id", resultSet.getInt("user_id"));
                    userInfo.put("name", resultSet.getString("name"));
                    userInfo.put("gender", resultSet.getByte("gender"));
                    userInfo.put("age", resultSet.getInt("age"));
                    userInfo.put("mbti", resultSet.getString("mbti"));
                    userInfo.put("hobbies", resultSet.getString("hobbies"));
                    userInfo.put("notification", resultSet.getInt("notification"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return userInfo; // Return the map (empty if no data found)
        }
        
        public static Map<String, List<Object>> fetchEmotionDataByUserId(int userId) {
            Map<String, List<Object>> emotionData = new HashMap<>();
            String query = "SELECT record_date, happy_score, sad_score, angry_score, confused_score, scared_score " +
                          "FROM daily_record WHERE user_id = ?";
            
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                // Initialize lists to store data
                List<Object> dates = new ArrayList<>();
                List<Object> happyScores = new ArrayList<>();
                List<Object> sadScores = new ArrayList<>();
                List<Object> angryScores = new ArrayList<>();
                List<Object> confusedScores = new ArrayList<>();
                List<Object> scaredScores = new ArrayList<>();
                
                while (resultSet.next()) {
                    dates.add(resultSet.getDate("record_date"));
                    happyScores.add(resultSet.getDouble("happy_score"));
                    sadScores.add(resultSet.getDouble("sad_score"));
                    angryScores.add(resultSet.getDouble("angry_score"));
                    confusedScores.add(resultSet.getDouble("confused_score"));
                    scaredScores.add(resultSet.getDouble("scared_score"));
                }
                
                // Store all lists in the map
                emotionData.put("dates", dates);
                emotionData.put("happy", happyScores);
                emotionData.put("sad", sadScores);
                emotionData.put("angry", angryScores);
                emotionData.put("confused", confusedScores);
                emotionData.put("scared", scaredScores);
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return emotionData;
        }
        public static Map<String, List<Object>> fetchFoodDataByUserId(int userId) {
            Map<String, List<Object>> foodData = new HashMap<>();
            String query = "SELECT record_date, food_score " +
                          "FROM daily_record WHERE user_id = ?";
            
            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {
                
                statement.setInt(1, userId);
                ResultSet resultSet = statement.executeQuery();
                
                // Initialize lists to store data
                List<Object> dates = new ArrayList<>();
                List<Object> foodScores = new ArrayList<>();
                
                while (resultSet.next()) {
                    dates.add(resultSet.getDate("record_date"));
                    foodScores.add(resultSet.getDouble("food_score"));
                }
                
                // Store all lists in the map
                foodData.put("date", dates);
                foodData.put("total-scores", foodScores);

                
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return foodData;
        }
    }






