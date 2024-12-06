package com.moodmate.logic;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import jess.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class WeatherAPI {
    public static void main(String[] args) {
        String city = "Daejeon";  // Example city
        String apiKey = "d15bc8c7724a3d65233de5301a550fba";  // Replace with your OpenWeatherMap API key
        // Database credentials
        String jdbcUrl = "jdbc:mysql://localhost:3306/moodmate";  // Change to your database URL
        String dbUser = "root";  // Change to your database username
        String dbPassword = "17Aug1993";  // Change to your database password
        try {
            // Create the API URL
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            //System.out.println("Raw JSON response: " + response.toString());

            // Parse the JSON response
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
         // Extract temperature, humidity, and weather condition
            JsonObject main = jsonResponse.getAsJsonObject("main");
            JsonArray weatherArray = jsonResponse.getAsJsonArray("weather");
            JsonObject weather = weatherArray.get(0).getAsJsonObject(); // Get the first element in the array
            
         // Extract the Unix timestamp (dt) for the date and time
            long unixTimestamp = jsonResponse.get("dt").getAsLong();
            
            // Convert Unix timestamp to human-readable date
            Date date = new Date(unixTimestamp * 1000); // Multiply by 1000 to convert to milliseconds
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Only include the date
            String formattedDate = sdf.format(date);
            
         // Convert temperature from Kelvin to Celsius
            double temperature = main.get("temp").getAsDouble() - 273.15;
            int humidity = main.get("humidity").getAsInt();
            String weatherCondition = weather.get("main").getAsString(); // Now this works correctly
           
            
            // Print the temperature
            System.out.println("Weather in " + city + ":");
            System.out.println("Date: " + formattedDate); // Only the date
            System.out.println("Temperature in is: " + temperature + "°C");       
            System.out.println("Humidity: " + humidity + " %");
            System.out.println("Weather Condition: " + weatherCondition);
            Rete engine= new Rete();
            engine.batch("src/com/moodmate/logic/rules_weather.clp");
            engine.eval("(assert (weather-input (condition \"" + weatherCondition + "\")))");
            engine.eval("(assert (temperature-input (value " + temperature + ")))");
            engine.eval("(assert (humidity-input (level " + humidity + ")))");
            engine.eval("(assert (finalize-score))");
            engine.eval("(run)");
            
            // Retrieve facts from Jess
            String temperatureValue = "";
            int weatherImpact = 0;
            String weatherCon = "";
            
         // Retrieve facts from working memory (example: weather-input, temperature-input)
            Iterator<Fact> facts = engine.listFacts();  // Get an iterator of all facts

            while (facts.hasNext()) {
                Fact fact = facts.next();
                String factName = fact.getName();  // Get the name of the fact
                if (factName.equals("MAIN::FinalEffect")) {
                    // Retrieve the condition value from the fact's slot
                    Value conditionValue = fact.getSlotValue("temperature");
                    Value scoreValue = fact.getSlotValue("final-mood-score");

                    if (conditionValue != null) {
                        System.out.println("Condition (temperature) value: " + conditionValue.stringValue(engine.getGlobalContext()));
                        temperatureValue = conditionValue.stringValue(engine.getGlobalContext());  // Set condition
                    } else {
                        System.out.println("Temperature slot not found in fact: " + fact);
                    }

                    if (scoreValue != null) {
                        System.out.println("Final mood score value: " + scoreValue.stringValue(engine.getGlobalContext()));
                        weatherImpact = Integer.parseInt(scoreValue.stringValue(engine.getGlobalContext()));  // Set finalMoodScore
                    } else {
                        System.out.println("Final-mood-score slot not found in fact: " + fact);
                    }
                }
                if (factName.equals("MAIN::Weather")) {
                	 // Retrieve the condition value from the fact's slot
                    Value weatherValue = fact.getSlotValue("condition");
                    if (weatherValue != null) {
                        System.out.println("Weather Condition: " + weatherValue.stringValue(engine.getGlobalContext()));
                        weatherCon = weatherValue.stringValue(engine.getGlobalContext());  // Set condition
                    } else {
                        System.out.println("Condition slot not found in fact: " + fact);
                    }

                	
                }
                
            
            }
            // Print out retrieved values
            System.out.println("Temperature value: " + temperatureValue);
            System.out.println("Final Mood Score: " + weatherImpact);
            saveWeatherDataToDatabase(temperatureValue, weatherCon, jdbcUrl, dbUser, dbPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
private static void saveWeatherDataToDatabase(String temperatureValue, String weatherCon, String jdbcUrl, String dbUser, String dbPassword) 
{
// Database connection and insertion logic
Connection conn = null;
PreparedStatement stmt = null;

try {
// Establish a connection to the database
conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);

// SQL insert query
String query = "INSERT INTO daily_record (user_id, weather_temperature, weather_condition) VALUES (39, ?, ?)";
stmt = conn.prepareStatement(query);

// Set parameters for the query
stmt.setString(1,temperatureValue);
stmt.setString(2, weatherCon);

// Execute the query
stmt.executeUpdate();

System.out.println("Weather data saved to the database!");
}catch (SQLException e) {
    e.printStackTrace();
} finally {
    // Close the database resources
    try {
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
}
}
