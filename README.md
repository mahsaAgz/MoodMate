# MoodMate: 
A Knowledge-Based Emotion Tracker for Self-Regulation and Early Detection of Mental Health Risks. The system features:

* User-friendly Java GUI
* Powerful inference engine powered by Jess rule-based system
* MySQL database with JDBC connectivity for persistent data storage and retrieval

## Table of Contents
- [MoodMate:](#moodmate)
  - [Table of Contents](#table-of-contents)
  - [Features](#features)
  - [Project Structure](#project-structure)
  - [Installation](#installation)
  - [Database Setup](#database-setup)

## Features

* Real-time emotion tracking and analysis
* Rule-based inference for mental health risk detection and suggestions
* Secure data storage and retrieval
* Interactive graphical user interface
* Personalized mood tracking and analysis
* Historical data visualization
* Customizable user profiles

## Project Structure
    ├── src/                     # Source code
    │   ├── module.java          # Packages usage
    │   ├── gui/                 # GUI components
    │   ├── logic/               # Jess rules and templates
    │   ├── database/            # Database connection and queries
    │   └── utilities/           # Helper functions and utilities
    ├── assets/                  # Configuration files and templates
    └── README.md                # Project documentation


## Installation
1. **Clone the repository:**
   ```bash
   git clone https://github.com/qiulij/MoodMate.git
   cd MoodMate
2. **Set Up Required Libraries:** \
   Download the following .jar files and add them to your project's classpath:
    * jess.jar
    * jsr94.jar
    * [gson-2.8.8.jar](https://search.maven.org/artifact/com.google.code.gson/gson/2.8.8/jar)
    * [mysql-connector-j-9.1.0.jar](https://dev.mysql.com/downloads/connector/j/?os=26)
3. **Set up MySQL:** \
   Install MySQL Server. Set up the database using the provided SQL scripts (see Database Setup).  


## Database Setup
1. Run the SQL Scripts in MySQL
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
    hobbies TEXT,
    self_image_score DECIMAL(5,2)
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
    self_image_score DECIMAL(5,2),
    sleep_score DECIMAL(5,2),
    physical_activity_score DECIMAL(5,2),
    food_score DECIMAL(5,2),
    weather_condition VARCHAR(50),
    weather_temperature DECIMAL(5,2),
    triggers_of_day TEXT,
    PRIMARY KEY (user_id, record_date)
    );
