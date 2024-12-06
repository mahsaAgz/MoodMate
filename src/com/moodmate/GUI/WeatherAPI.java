package com.moodmate.GUI;

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
import com.moodmate.GUI.SignInPage.GlobalVariable;

public class WeatherAPI {
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
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String formattedDate = sdf.format(date);
            
            double temperature = main.get("temp").getAsDouble() - 273.15;
            int humidity = main.get("humidity").getAsInt();
            String weatherCondition = weather.get("main").getAsString();
           
            System.out.println("\nWeather in " + CITY + ":");
            System.out.println("Date: " + formattedDate);
            System.out.println("Temperature: " + String.format("%.1f", temperature) + "°C");       
            System.out.println("Humidity: " + humidity + "%");
            System.out.println("Weather Condition: " + weatherCondition);
            
            Rete engine = ReteEngineManager.getInstance();
            engine.batch("src/com/moodmate/logic/templates_weather.clp");
            
            engine.eval("(assert (weather-input (condition \"" + weatherCondition + "\")))");
            engine.eval("(assert (temperature-input (value " + temperature + ")))");
            engine.eval("(assert (humidity-input (level " + humidity + ")))");
            engine.eval("(bind ?userId " + userId + ")");
            engine.eval("(run)");
            
            Iterator<Fact> facts = engine.listFacts();
            while (facts.hasNext()) {
                Fact fact = facts.next();
                String factName = fact.getName();
                
                if (factName.equals("MAIN::FinalEffect")) {
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

    // Main method for testing
    public static void main(String[] args) {
        getWeatherUpdate();
    }
}