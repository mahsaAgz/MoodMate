# MoodMate: 
A Knowledge-Based Emotion Tracker for Self-Regulation and Early Detection of Mental Health Risks.


## Features
* User-friendly Java GUI
* Powerful inference engine using Jess rule-based system
* Persistent data storage with MySQL and JDBC connectivity
* Real-time emotion tracking and analysis
* Historical data visualization
* Personalized suggestions for mood regulation
* Interactive and customizable user profiles

  
## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Database Setup](#database-setup)
- [Test Cases](#test-cases)


## Introduction
MoodMate is an intelligent system designed to help users track and manage their emotions through:
- Daily mood analysis
- Early detection of potential mental health risks
- Personalized suggestions to enhance mental well-being


## Project Structure
```plaintext
├── src/                     
│   ├── module.java          # Entry point for package usage
│   ├── gui/                 # GUI components
│   ├── logic/               # Jess rules and templates
│   ├── database/            # Database connection and queries
│   └── utilities/           # Helper functions and utilities
├── assets/                  # Configuration files and templates
└── README.md                # Project documentation
```

## Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/qiulij/MoodMate.git
   ```
   ```bash
   cd MoodMate
   ```
2. **Set Up Required Libraries:** \
   Download the following .jar files and add them to your project's classpath:
    * jess.jar
    * jsr94.jar
    * [gson-2.8.8.jar](https://search.maven.org/artifact/com.google.code.gson/gson/2.8.8/jar)
    * [mysql-connector-j-9.1.0.jar](https://dev.mysql.com/downloads/connector/j/?os=26)
    You can also find these in our repository.
3. **Set up MySQL:** \
   Install MySQL Server. Set up the database using the provided SQL scripts (see Database Setup).  


## Database Setup
1. Create Tables  \
   Run the provided SQL scripts in MySQL to set up the necessary tables:
    ```bash
    -- Authentication table
    CREATE TABLE IF NOT EXISTS Authentication (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
    );

    -- User_Info table
    CREATE TABLE IF NOT EXISTS User_Info (
    user_id INT PRIMARY KEY,
    name VARCHAR(50),
    gender TINYINT,
    age INT,
    mbti VARCHAR(4),
    hobbies TEXT
    );

    -- Daily_Record table
    CREATE TABLE IF NOT EXISTS Daily_Record (
    user_id INT NOT NULL,
    record_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    happy_score DECIMAL(5,2),
    sad_score DECIMAL(5,2),
    angry_score DECIMAL(5,2),
    confused_score DECIMAL(5,2),
    scared_score DECIMAL(5,2),
    sleep_score DECIMAL(5,2),
    physical_activity_score DECIMAL(5,2),
    food_score DECIMAL(5,2),
    weather_condition VARCHAR(50),
    weather_temperature VARCHAR(50),
    PRIMARY KEY (user_id, record_date)
    );
    ```
## Test Case
### Testing Real-Time Suggestions
1. Input emotional and physical data directly through the GUI.
2. Receive personalized recommendations based on the Jess inference engine.

### Testing Long-Term Suggestions
1. Insert mock data into the Daily_Record table for long-term analysis:
2. Use SQL scripts provided in the repository for scenarios like bipolar, depression, anxiety, and eating disorders.
3. Ensure previously added data with the same dates is removed to avoid conflicts. \
Verify that the correct user_id matches the one in the Authentication table. 

### Notice 
When testing: \
Remove old data before adding new data for the same date. \
Use the user_id from the Authentication table to avoid errors. \


### Example SQL for Bipolar Test
  ```bash
  INSERT INTO daily_record (user_id, record_date, happy_score, sad_score, angry_score, confused_score, scared_score, sleep_score, physical_activity_score, food_score, weather_condition, weather_temperature) VALUES
  (30, '2024-11-14 12:00:00', 15.00, 45.00, 20.00, 10.00, 10.00, 40.00, 35.00, 30.00, 'cloudy', 'cold'),
  (30, '2024-11-15 12:00:00', 12.00, 48.00, 20.00, 10.00, 10.00, 38.00, 33.00, 28.00, 'rainy', 'cold'),
  (30, '2024-11-16 12:00:00', 10.00, 50.00, 20.00, 10.00, 10.00, 35.00, 30.00, 25.00, 'cloudy', 'cold'),
  (30, '2024-11-17 12:00:00', 8.00, 52.00, 20.00, 10.00, 10.00, 33.00, 28.00, 23.00, 'cloudy', 'cold'),
  (30, '2024-11-18 12:00:00', 5.00, 55.00, 20.00, 10.00, 10.00, 30.00, 25.00, 20.00, 'rainy', 'cold'),
  (30, '2024-11-19 12:00:00', 7.00, 53.00, 20.00, 10.00, 10.00, 28.00, 23.00, 18.00, 'cloudy', 'cold'),
  (30, '2024-11-20 12:00:00', 10.00, 50.00, 20.00, 10.00, 10.00, 25.00, 20.00, 15.00, 'cloudy', 'mild'),
  (30, '2024-11-21 12:00:00', 12.00, 48.00, 20.00, 10.00, 10.00, 23.00, 18.00, 13.00, 'sunny', 'mild'),
  (30, '2024-11-22 12:00:00', 8.00, 52.00, 20.00, 10.00, 10.00, 20.00, 15.00, 10.00, 'cloudy', 'cold'),
  (30, '2024-11-23 12:00:00', 6.00, 54.00, 20.00, 10.00, 10.00, 18.00, 13.00, 8.00, 'rainy', 'cold'),
  (30, '2024-11-24 12:00:00', 5.00, 55.00, 20.00, 10.00, 10.00, 15.00, 10.00, 5.00, 'rainy', 'cold'),
  (30, '2024-11-25 12:00:00', 7.00, 53.00, 20.00, 10.00, 10.00, 13.00, 8.00, 3.00, 'cloudy', 'cold'),
  (30, '2024-11-26 12:00:00', 9.00, 51.00, 20.00, 10.00, 10.00, 10.00, 5.00, 0.00, 'cloudy', 'mild'),
  (30, '2024-11-27 12:00:00', 11.00, 49.00, 20.00, 10.00, 10.00, 8.00, 3.00, 0.00, 'sunny', 'mild'),
  (30, '2024-11-28 12:00:00', 13.00, 47.00, 20.00, 10.00, 10.00, 5.00, 0.00, 0.00, 'sunny', 'mild'),
  (30, '2024-11-29 12:00:00', 45.00, 15.00, 20.00, 10.00, 10.00, 60.00, 55.00, 50.00, 'sunny', 'mild'),
  (30, '2024-11-30 12:00:00', 47.00, 13.00, 20.00, 10.00, 10.00, 65.00, 60.00, 55.00, 'sunny', 'warm'),
  (30, '2024-12-01 12:00:00', 50.00, 10.00, 20.00, 10.00, 10.00, 70.00, 65.00, 60.00, 'sunny', 'warm'),
  (30, '2024-12-02 12:00:00', 86.00, 0.00, 10.00, 4.00, 0.00, 75.00, 70.00, 65.00, 'sunny', 'warm'),
  (30, '2024-12-03 12:00:00', 85.00, 0.00, 5.00, 10.00, 0.00, 80.00, 75.00, 70.00, 'sunny', 'warm'),
  (30, '2024-12-04 12:00:00', 82.00, 10.00, 0.00, 3.00, 5.00, 75.00, 70.00, 65.00, 'sunny', 'warm'),
  (30, '2024-12-05 12:00:00', 88.00, 2.00, 10.00, 0.00, 0.00, 70.00, 65.00, 60.00, 'sunny', 'warm'),
  (30, '2024-12-06 12:00:00', 87.00, 10.00, 0.00, 0.00, 3.00, 65.00, 60.00, 55.00, 'sunny', 'mild'),
  (30, '2024-12-07 12:00:00', 84.00, 0.00, 0.00, 16.00, 0.00, 60.00, 55.00, 50.00, 'sunny', 'mild'),
  (30, '2024-12-08 12:00:00', 86.00, 4.00, 10.00, 0.00, 0.00, 65.00, 60.00, 55.00, 'sunny', 'warm'),
  (30, '2024-12-09 12:00:00', 80.00, 0.00, 0.00, 10.00, 10.00, 60.00, 55.00, 50.00, 'sunny', 'warm');
```
3. Add data to daily_record for testing depression and SAD
```bash
INSERT INTO daily_record (user_id, record_date, happy_score, sad_score, angry_score, confused_score, scared_score, sleep_score, physical_activity_score, food_score, weather_condition, weather_temperature)
VALUES
(30, '2024-11-14 12:00:00', 12.00, 63.00, 12.00, 8.00, 5.00, 82.00, 65.00, 78.00, 'cloudy', 'cold'),
(30, '2024-11-15 12:00:00', 10.00, 65.00, 15.00, 5.00, 5.00, 75.00, 58.00, 71.00, 'cloudy', 'cold'),
(30, '2024-11-16 12:00:00', 8.00, 68.00, 10.00, 7.00, 7.00, 68.00, 52.00, 65.00, 'cloudy', 'cold'),
(30, '2024-11-17 12:00:00', 11.00, 64.00, 8.00, 12.00, 5.00, 62.00, 45.00, 58.00, 'cloudy', 'cold'),
(30, '2024-11-18 12:00:00', 7.00, 70.00, 11.00, 6.00, 6.00, 55.00, 38.00, 52.00, 'cloudy', 'cold'),
(30, '2024-11-19 12:00:00', 13.00, 62.00, 13.00, 7.00, 5.00, 48.00, 32.00, 45.00, 'cloudy', 'cold'),
(30, '2024-11-20 12:00:00', 9.00, 66.00, 10.00, 8.00, 7.00, 42.00, 25.00, 38.00, 'cloudy', 'mild'),
(30, '2024-11-21 12:00:00', 14.00, 61.00, 12.00, 8.00, 5.00, 35.00, 18.00, 32.00, 'sunny', 'mild'),
(30, '2024-11-22 12:00:00', 8.00, 67.00, 11.00, 9.00, 5.00, 28.00, 12.00, 25.00, 'cloudy', 'cold'),
(30, '2024-11-23 12:00:00', 12.00, 64.00, 9.00, 10.00, 5.00, 22.00, 8.00, 18.00, 'cloudy', 'cold'),
(30, '2024-11-24 12:00:00', 10.00, 65.00, 13.00, 6.00, 6.00, 15.00, 5.00, 12.00, 'cloudy', 'cold'),
(30, '2024-11-25 12:00:00', 11.00, 63.00, 12.00, 8.00, 6.00, 12.00, 3.00, 8.00, 'cloudy', 'cold'),
(30, '2024-11-26 12:00:00', 9.00, 66.00, 10.00, 9.00, 6.00, 8.00, 2.00, 5.00, 'cloudy', 'mild'),
(30, '2024-11-27 12:00:00', 13.00, 62.00, 11.00, 8.00, 6.00, 5.00, 1.00, 3.00, 'sunny', 'mild'),
(30, '2024-11-28 12:00:00', 12.00, 64.00, 10.00, 9.00, 5.00, 3.00, 1.00, 2.00, 'sunny', 'mild'),
(30, '2024-11-29 12:00:00', 10.00, 65.00, 12.00, 7.00, 6.00, 85.00, 78.00, 82.00, 'sunny', 'mild'),
(30, '2024-11-30 12:00:00', 11.00, 63.00, 13.00, 8.00, 5.00, 88.00, 82.00, 85.00, 'sunny', 'warm'),
(30, '2024-12-01 12:00:00', 9.00, 67.00, 11.00, 7.00, 6.00, 92.00, 85.00, 88.00, 'sunny', 'warm'),
(30, '2024-12-02 12:00:00', 12.00, 65.00, 10.00, 8.00, 5.00, 95.00, 88.00, 92.00, 'sunny', 'warm'),
(30, '2024-12-03 12:00:00', 11.00, 64.00, 12.00, 7.00, 6.00, 98.00, 92.00, 95.00, 'sunny', 'warm'),
(30, '2024-12-04 12:00:00', 13.00, 62.00, 11.00, 9.00, 5.00, 95.00, 88.00, 92.00, 'sunny', 'warm'),
(30, '2024-12-05 12:00:00', 10.00, 66.00, 13.00, 6.00, 5.00, 92.00, 85.00, 88.00, 'sunny', 'warm'),
(30, '2024-12-06 12:00:00', 8.00, 68.00, 12.00, 7.00, 5.00, 88.00, 82.00, 85.00, 'sunny', 'mild'),
(30, '2024-12-07 12:00:00', 12.00, 63.00, 11.00, 8.00, 6.00, 85.00, 78.00, 82.00, 'sunny', 'mild'),
(30, '2024-12-08 12:00:00', 11.00, 64.00, 13.00, 7.00, 5.00, 88.00, 82.00, 85.00, 'sunny', 'warm'),
(30, '2024-12-09 12:00:00', 10.00, 65.00, 12.00, 8.00, 5.00, 85.00, 78.00, 82.00, 'sunny', 'warm');
```
4. Add data to daily_record for testing anxiety and sleep disorder
```bash
INSERT INTO daily_record (user_id, record_date, happy_score, sad_score, angry_score, confused_score, scared_score, sleep_score, physical_activity_score, food_score, weather_condition, weather_temperature)
VALUES
(30, '2024-11-14 12:00:00', 5.00, 15.00, 10.00, 32.00, 38.00, 65.00, 45.00, 38.00, 'cloudy', 'cold'),
(30, '2024-11-15 12:00:00', 4.00, 14.00, 8.00, 35.00, 39.00, 58.00, 42.00, 35.00, 'rainy', 'cold'),
(30, '2024-11-16 12:00:00', 6.00, 12.00, 9.00, 33.00, 40.00, 52.00, 38.00, 42.00, 'cloudy', 'cold'),
(30, '2024-11-17 12:00:00', 5.00, 13.00, 7.00, 36.00, 39.00, 48.00, 35.00, 28.00, 'cloudy', 'cold'),
(30, '2024-11-18 12:00:00', 4.00, 15.00, 8.00, 34.00, 39.00, 45.00, 32.00, 25.00, 'cloudy', 'cold'),
(30, '2024-11-19 12:00:00', 5.00, 12.00, 9.00, 35.00, 39.00, 42.00, 28.00, 32.00, 'cloudy', 'cold'),
(30, '2024-11-20 12:00:00', 4.00, 14.00, 8.00, 34.00, 40.00, 38.00, 25.00, 28.00, 'cloudy', 'mild'),
(30, '2024-11-21 12:00:00', 6.00, 13.00, 7.00, 35.00, 39.00, 35.00, 22.00, 18.00, 'sunny', 'mild'),
(30, '2024-11-22 12:00:00', 5.00, 15.00, 8.00, 33.00, 39.00, 32.00, 18.00, 25.00, 'cloudy', 'cold'),
(30, '2024-11-23 12:00:00', 4.00, 14.00, 9.00, 34.00, 39.00, 28.00, 15.00, 22.00, 'cloudy', 'cold'),
(30, '2024-11-24 12:00:00', 5.00, 12.00, 8.00, 36.00, 39.00, 25.00, 12.00, 18.00, 'cloudy', 'cold'),
(30, '2024-11-25 12:00:00', 4.00, 13.00, 7.00, 35.00, 41.00, 22.00, 8.00, 15.00, 'cloudy', 'cold'),
(30, '2024-11-26 12:00:00', 6.00, 15.00, 8.00, 32.00, 39.00, 18.00, 12.00, 8.00, 'cloudy', 'mild'),
(30, '2024-11-27 12:00:00', 5.00, 14.00, 9.00, 33.00, 39.00, 15.00, 8.00, 12.00, 'sunny', 'mild'),
(30, '2024-11-28 12:00:00', 4.00, 13.00, 8.00, 35.00, 40.00, 12.00, 5.00, 8.00, 'sunny', 'mild'),
(30, '2024-11-29 12:00:00', 5.00, 15.00, 7.00, 34.00, 39.00, 8.00, 5.00, 10.00, 'sunny', 'mild'),
(30, '2024-11-30 12:00:00', 4.00, 14.00, 8.00, 35.00, 39.00, 15.00, 8.00, 12.00, 'sunny', 'warm'),
(30, '2024-12-01 12:00:00', 6.00, 12.00, 9.00, 33.00, 40.00, 18.00, 12.00, 15.00, 'sunny', 'warm'),
(30, '2024-12-02 12:00:00', 5.00, 13.00, 8.00, 35.00, 39.00, 22.00, 15.00, 18.00, 'sunny', 'warm'),
(30, '2024-12-03 12:00:00', 4.00, 15.00, 7.00, 34.00, 40.00, 25.00, 18.00, 22.00, 'sunny', 'warm'),
(30, '2024-12-04 12:00:00', 5.00, 14.00, 8.00, 33.00, 40.00, 28.00, 22.00, 25.00, 'sunny', 'warm'),
(30, '2024-12-05 12:00:00', 4.00, 13.00, 9.00, 35.00, 39.00, 32.00, 25.00, 28.00, 'sunny', 'warm'),
(30, '2024-12-06 12:00:00', 6.00, 12.00, 8.00, 34.00, 40.00, 35.00, 28.00, 32.00, 'sunny', 'mild'),
(30, '2024-12-07 12:00:00', 5.00, 14.00, 7.00, 35.00, 39.00, 38.00, 32.00, 35.00, 'sunny', 'mild'),
(30, '2024-12-08 12:00:00', 4.00, 15.00, 8.00, 33.00, 40.00, 42.00, 35.00, 38.00, 'sunny', 'warm'),
(30, '2024-12-09 12:00:00', 5.00, 13.00, 9.00, 34.00, 39.00, 45.00, 38.00, 42.00, 'sunny', 'warm');
```
5. Add data to daily_record for testing eating disorder
```bash
INSERT INTO daily_record (user_id, record_date, happy_score, sad_score, angry_score, confused_score, scared_score, sleep_score, physical_activity_score, food_score, weather_condition, weather_temperature)
VALUES
(30, '2024-11-24 12:00:00', 55.00, 22.00, 8.00, 6.00, 9.00, 75.00, 12.00, 18.00, 'cloudy', 'cold'),
(30, '2024-11-25 12:00:00', 54.00, 13.00, 14.00, 15.00, 4.00, 80.00, 8.00, 15.00, 'cloudy', 'cold'),
(30, '2024-11-26 12:00:00', 66.00, 15.00, 8.00, 2.00, 9.00, 78.00, 12.00, 8.00, 'cloudy', 'mild'),
(30, '2024-11-27 12:00:00', 55.00, 14.00, 9.00, 13.00, 9.00, 55.00, 8.00, 12.00, 'sunny', 'mild'),
(30, '2024-11-28 12:00:00', 4.00, 13.00, 8.00, 35.00, 40.00, 82.00, 5.00, 8.00, 'sunny', 'mild'),
(30, '2024-11-29 12:00:00', 5.00, 15.00, 7.00, 34.00, 39.00, 68.00, 5.00, 10.00, 'sunny', 'mild'),
(30, '2024-11-30 12:00:00', 54.00, 14.00, 8.00, 15.00, 19.00, 76.00, 8.00, 12.00, 'sunny', 'warm'),
(30, '2024-12-01 12:00:00', 55.00, 12.00, 9.00, 13.00, 0.00, 83.00, 12.00, 15.00, 'sunny', 'warm'),
(30, '2024-12-02 12:00:00', 55.00, 13.00, 8.00, 15.00, 9.00, 67.00, 15.00, 18.00, 'sunny', 'warm'),
(30, '2024-12-03 12:00:00', 54.00, 25.00, 7.00, 4.00, 10.00, 77.00, 18.00, 22.00, 'sunny', 'warm'),
(30, '2024-12-04 12:00:00', 55.00, 14.00, 18.00, 3.00, 10.00, 88.00, 22.00, 25.00, 'sunny', 'warm'),
(30, '2024-12-05 12:00:00', 54.00, 13.00, 9.00, 15.00, 19.00, 75.00, 25.00, 28.00, 'sunny', 'warm'),
(30, '2024-12-06 12:00:00', 66.00, 12.00, 18.00, 3.00, 0.00, 69.00, 28.00, 32.00, 'sunny', 'mild'),
(30, '2024-12-07 12:00:00', 55.00, 14.00, 7.00, 15.00, 9.00, 59.00, 32.00, 35.00, 'sunny', 'mild'),
(30, '2024-12-08 12:00:00', 54.00, 15.00, 8.00, 3.00, 20.00, 73.00, 35.00, 38.00, 'sunny', 'warm'),
(30, '2024-12-09 12:00:00', 55.00, 13.00, 9.00, 4.00, 19.00, 92.00, 38.00, 42.00, 'sunny', 'warm');
```
