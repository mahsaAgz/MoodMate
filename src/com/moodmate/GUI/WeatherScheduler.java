package com.moodmate.GUI;

import java.util.Timer;
import java.util.TimerTask;

public class WeatherScheduler {
    private static Timer timer;
    private static final int HOUR = 3600000; // 1 hour in milliseconds
    
    public static void startWeatherUpdates() {
        if (timer != null) {
            timer.cancel();
        }
        
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                WeatherAPI.getWeatherUpdate();
            }
        }, 0, HOUR); // Run immediately and then every hour
    }
    
    public static void stopWeatherUpdates() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}