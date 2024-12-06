package com.moodmate.GUI;
import com.moodmate.GUI.SignInPage.GlobalVariable;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import jess.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.SimpleDateFormat;

public class WeatherAPI {
	Rete engine = ReteEngineManager.getInstance();
	
	public WeatherAPI(Rete engine) {
		this.engine = engine;		
	}
        String city = "Daejeon";  // Example city
        String apiKey = "d15bc8c7724a3d65233de5301a550fba";  // Replace with your OpenWeatherMap API key
    private static final String CITY = "Daejeon";
    private static final String API_KEY = "d15bc8c7724a3d65233de5301a550fba";
    private static final int userId = GlobalVariable.userId;
    public static void getWeatherUpdate() {
        try {
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY;
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            JsonObject main = jsonResponse.getAsJsonObject("main");
            JsonArray weatherArray = jsonResponse.getAsJsonArray("weather");
            JsonObject weather = weatherArray.get(0).getAsJsonObject();

            long unixTimestamp = jsonResponse.get("dt").getAsLong();
            Date date = new Date(unixTimestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd"); // Updated format
            String formattedDate = sdf.format(date);

            double temperature = main.get("temp").getAsDouble() - 273.15;
            String weatherCondition = weather.get("main").getAsString();
            int humidity = main.get("humidity").getAsInt();
            
            System.out.println("\nWeather in " + CITY + ":");
            System.out.println("Date: " + formattedDate);
            System.out.println("Temperature: " + String.format("%.1f", temperature) + "°C");
            System.out.println("Humidity: " + humidity + " %");
            System.out.println("Weather Condition: " + weatherCondition);
            Rete engine = ReteEngineManager.getInstance();
            engine.batch("src/com/moodmate/logic/rules_weather.clp");

            
            engine.batch("src/com/moodmate/logic/templates_weather.clp");

            // Assert `weather-input` facts
            engine.eval("(assert (weather-input (condition \"" + weatherCondition + "\")))");
            engine.eval("(assert (temperature-input (value " + temperature + ")))");
            engine.eval("(assert (humidity-input (level " + humidity + ")))");

            // Assert the `daily-weather` fact
            String dailyWeatherFact = String.format(
                "(assert (daily-weather (user_id %d) (day \"%s\") (condition \"%s\") (temperature %.1f)))",
                userId, formattedDate, weatherCondition, temperature
            );
            System.out.println("Asserting fact: " + dailyWeatherFact);
            engine.eval(dailyWeatherFact);

            // Run the engine
            engine.eval("(assert (user-id (userId " + userId + ")))");
            engine.eval("(run)");
            
            // Retrieve facts from Jess
            String temperatureValue = "";
            //int weatherImpact = 0;
            //String weatherCon = "";
            
         // Retrieve facts from working memory (example: weather-input, temperature-input)
            Iterator<Fact> facts = engine.listFacts();  // Get an iterator of all facts

            //Iterator<Fact> facts = engine.listFacts();
            while (facts.hasNext()) {
                Fact fact = facts.next();
                String factName = fact.getName();

                if (factName.equals("MAIN::FinalEffect")) {
                    // Retrieve the condition value from the fact's slot
                    Value conditionValue = fact.getSlotValue("temperature");
                    //Value scoreValue = fact.getSlotValue("final-mood-score");
                    if (conditionValue != null) {
                        System.out.println("Condition (temperature) value: " + conditionValue.stringValue(engine.getGlobalContext()));
                        temperatureValue = conditionValue.stringValue(engine.getGlobalContext());  // Set condition
                    } else {
                        System.out.println("Temperature slot not found in fact: " + fact);
                    }
                    /*if (scoreValue != null) {
                        System.out.println("Final mood score value: " + scoreValue.stringValue(engine.getGlobalContext()));
                        weatherImpact = Integer.parseInt(scoreValue.stringValue(engine.getGlobalContext()));  // Set finalMoodScore
                    } else {
                        System.out.println("Final-mood-score slot not found in fact: " + fact);
                    }*/
                /*}
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
            saveWeatherDataToDatabase(temperatureValue, weatherCon, jdbcUrl, dbUser, dbPassword);*/
                    Value tempCondition = fact.getSlotValue("temperature");
                    Value scoreValue = fact.getSlotValue("final-mood-score");
                    Value suggestionValue = fact.getSlotValue("suggestion");

                    System.out.println("\nWeather Analysis Results:");
                    if (tempCondition != null) {
                        System.out.println("Temperature Condition: " +
                            tempCondition.stringValue(engine.getGlobalContext()));
                    }
                    if (scoreValue != null) {
                        System.out.println("Weather Impact Score: " +
                            scoreValue.stringValue(engine.getGlobalContext()) + "/100");
                    }
                    if (suggestionValue != null) {
                        System.out.println("Recommendation: " +
                            suggestionValue.stringValue(engine.getGlobalContext()));
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Error getting weather update: " + e.getMessage());
        }
    }
    
/*private static void saveWeatherDataToDatabase(String temperatureValue, String weatherCon, String jdbcUrl, String dbUser, String dbPassword) 
{
// Database connection and insertion logic
Connection conn = null;
PreparedStatement stmt = null;
try {
// Establish a connection to the database
conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
// SQL insert query
String query = "INSERT INTO daily_record (user_id, weather_temperature, weather_condition) VALUES (?, ?, ?)";
stmt = conn.prepareStatement(query);
// Set parameters for the query
stmt.setInt(1, GlobalVariable.userId);
stmt.setString(2,temperatureValue);
stmt.setString(3, weatherCon);
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
}*/
    // Main method for testing
    public static void main(String[] args) {
        getWeatherUpdate();
    }
}