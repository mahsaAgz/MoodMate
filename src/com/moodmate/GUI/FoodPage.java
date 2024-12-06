package com.moodmate.GUI;

import javax.swing.*;
import javax.swing.event.*;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import jess.Fact;
import jess.JessException;
import jess.Rete;
import jess.Value;

import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class FoodPage extends BaseHomePage {

    private static final int PADDING_X = 30; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields
    private static final int PIC_SIZE = 100; // Height for the input fields   
    private static final int MARGIN = 20; // Vertical margin between components
    private static final int userId = GlobalVariable.userId;
    private Map<String, JSlider> nutrientSliders = new HashMap<>();
    int foodScore;
    int pAScore;
    int sleepScore;
    int happy = 0;
    int sad = 0;
    int angry = 0;
    int scared = 0;
    int confused = 0;
    String weatherCon;
    String weatherTemp;
    int percentage;
    String emotionName;
    
 // Database credentials
    String jdbcUrl = "jdbc:mysql://localhost:3306/moodmate";  // Change to your database URL
    String dbUser = "root";  // Change to your database username
    String dbPassword = "17Aug1993";  // Change to your database password
    
    private static final Map<String, String> APPETITE_CODES;
    static {
        APPETITE_CODES = new LinkedHashMap<>();
        APPETITE_CODES.put("I have no appetite at all.", "0a");
        APPETITE_CODES.put("My appetite is much less than before.", "1a");
        APPETITE_CODES.put("My appetite is somewhat less than usual.", "2a");
        APPETITE_CODES.put("I have not experienced any change in my appetite.", "3");
        APPETITE_CODES.put("My appetite is somewhat greater than usual.", "2b");
        APPETITE_CODES.put("My appetite is much greater than usual.", "1b");
        APPETITE_CODES.put("I crave food all the time.", "0b");
    }
    
    
    int contentWidth = contentArea.getWidth();

    public FoodPage() {
        super();
//        Rete reteInstance = new Rete();
        Rete engine = ReteEngineManager.getInstance();
     // Fetch weather data and assert into Jess
        WeatherAPI weatherAPI = new WeatherAPI(engine);
        weatherAPI.getWeatherUpdate();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning

        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel("Monitor Your Nutrition", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);
        

        currentY += FIELD_HEIGHT + MARGIN;
        

     // Meal Count Question
        JLabel mealCountLabel = new JLabel("How many meals did you have today?", SwingConstants.LEFT);
        mealCountLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        mealCountLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(mealCountLabel);

        currentY += FIELD_HEIGHT;

        // Spinner for Meal Count Input
        JSpinner mealCountSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1)); // Min: 0, Max: 10, Step: 1
        mealCountSpinner.setBounds(PADDING_X, currentY, FIELD_HEIGHT * 3, FIELD_HEIGHT);
        contentPanel.add(mealCountSpinner);

        currentY += FIELD_HEIGHT + MARGIN;


     // Appetite Question with Radio Buttons
        JLabel appetiteLabel = new JLabel("How has your appetite been?", SwingConstants.LEFT);
        appetiteLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        appetiteLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(appetiteLabel);

        currentY += FIELD_HEIGHT;

        // Create a transparent panel for the radio buttons
        JPanel appetitePanel = new JPanel();
        appetitePanel.setLayout(new GridLayout(7, 1, 5, 5)); // 7 options, vertical layout
        appetitePanel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT * 7);
        appetitePanel.setOpaque(false); // No background for the panel

        ButtonGroup appetiteGroup = new ButtonGroup(); // Group for radio buttons
        Map<JRadioButton, String> buttonToCode = new HashMap<>();
        
        String[] appetiteOptions = {
            "I have no appetite at all.",
            "My appetite is much less than before.",
            "My appetite is somewhat less than usual.",
            "I have not experienced any change in my appetite.",
            "My appetite is somewhat greater than usual.",
            "My appetite is much greater than usual.",
            "I crave food all the time."
        };

        for (Map.Entry<String, String> option : APPETITE_CODES.entrySet()) {
            JRadioButton radioButton = new JRadioButton(option.getKey());
            radioButton.setFont(new Font(customFont, Font.PLAIN, 14));
            radioButton.setOpaque(false);
            appetiteGroup.add(radioButton);
            appetitePanel.add(radioButton);
            buttonToCode.put(radioButton, option.getValue());

            // Add action listener to assert fact when option is selected
            radioButton.addActionListener(e -> {
                try {
                    String appetiteCommand = String.format(
                        "(assert (appetite-status (user_id %d) (option \"%s\")))",
                        userId, option.getValue()
                    );
                    System.out.println("Asserting appetite: " + appetiteCommand);
                    engine.eval(appetiteCommand);
                    engine.eval("(assert (meal-info (user_id 1) (meals-per-day 2)))");
 
                    engine.run();
                   

                } catch (JessException ex) {
                    ex.printStackTrace();
                }
            });
        }

        // Add the panel to the content panel
        contentPanel.add(appetitePanel);

        currentY += FIELD_HEIGHT * 7 + MARGIN;
        // Title Label
        JLabel title2Label = new JLabel("Monitor Your Nutrition", SwingConstants.CENTER);
        title2Label.setFont(new Font(customFont, Font.BOLD, 20));
        title2Label.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(title2Label);

        currentY += FIELD_HEIGHT + MARGIN;


        // Macronutrient Sliders

        String[] categories = {"Carbs", "Protein", "Fat", "Minerals", "Vitamins", "Water"};
        String[] icons = {"carbs.png", "protein.png", "fat.png", "minerals.png", "vitamins.png", "water.png"};

      
        for (int i = 0; i < categories.length; i++) {
            // Container Panel
            JPanel container = new JPanel();
            container.setLayout(null);
            container.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, PIC_SIZE + FIELD_HEIGHT + 10);

            // Icon Label;
            String path =  "assets/images/foodIcons/" + icons[i];
            JLabel iconLabel = new JLabel(new ImageIcon(path));
            iconLabel.setBounds(0, 0, PIC_SIZE, PIC_SIZE);
            container.add(iconLabel);

            // Slider Title
            JLabel sliderTitle = new JLabel(categories[i]);
            sliderTitle.setFont(new Font(customFont, Font.BOLD, 16));
            sliderTitle.setBounds(PIC_SIZE + 10, 0, contentWidth - PIC_SIZE - PADDING_X * 2, FIELD_HEIGHT);
            container.add(sliderTitle);

            // Slider
            JSlider slider = new JSlider(0, 100);
            slider.setBounds(PIC_SIZE + 10, FIELD_HEIGHT , contentWidth - PIC_SIZE - PADDING_X * 3, FIELD_HEIGHT * 3);
            slider.setMajorTickSpacing(20);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);

            // Custom labels for the slider
           
            Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

            labelTable.put(0, new JLabel("0"));
            labelTable.put(50, new JLabel("50"));
            labelTable.put(100, new JLabel("100"));

            slider.setLabelTable(labelTable);

            nutrientSliders.put(categories[i], slider);
            container.add(slider);

            // Add container to content panel
            contentPanel.add(container);

            currentY += PIC_SIZE + FIELD_HEIGHT + MARGIN;
        }


        currentY += MARGIN;

        // Next Button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
        nextButton.setBackground(customGreen);
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        nextButton.addActionListener(e -> {
            try {
                // Validate meal count input
                int mealsToday = (int) mealCountSpinner.getValue();
                if (mealsToday < 1) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please enter how many meals you had today.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Assert meal count fact
                String mealCountCommand = String.format(
                    "(assert (meal-info (user_id %d) (meals-per-day %d)))",
                    userId, mealsToday
                );

                System.out.println("Asserting meal count: " + mealCountCommand);
                engine.eval(mealCountCommand);

                // Validate appetite selection
                if (appetiteGroup.getSelection() == null) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please select your appetite status.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // Assert macronutrient intake
                String macroCommand = String.format(
                    "(assert (macronutrient-intake " +
                    "(user_id %d) " +
                    "(carbs %d) " +
                    "(protein %d) " +
                    "(fat %d) " +
                    "(minerals %d) " +
                    "(vitamins %d) " +
                    "(water %d)))",
                    userId,
                    nutrientSliders.get("Carbs").getValue(),
                    nutrientSliders.get("Protein").getValue(),
                    nutrientSliders.get("Fat").getValue(),
                    nutrientSliders.get("Minerals").getValue(),
                    nutrientSliders.get("Vitamins").getValue(),
                    nutrientSliders.get("Water").getValue()
                );

                System.out.println("Asserting macronutrients: " + macroCommand);
                engine.eval(macroCommand);

                // Run the engine to process the rules
                engine.run();

                // Debugging: Print all facts in the Rete engine
                Iterator<?> facts = engine.listFacts();
                System.out.println("Facts in the engine:");
                while (facts.hasNext()) {
                       
                    	Fact fact = (Fact) facts.next();
                    	System.out.println("Processing fact: " + fact);
                        if (fact.getName().equals("MAIN::food-score")) {
                        	 foodScore = fact.getSlotValue("total-score").intValue(null);
                        	 //percentage = fact.getSlotValue("percentage").intValue(null);
                        System.out.println("Food Score: "+foodScore);
                    }
                        else if (fact.getName().equals("MAIN::normalized-emotion")) {
                       	 emotionName = fact.getSlotValue("emotion-name").stringValue(null);
                       	 percentage = fact.getSlotValue("percentage").intValue(null);
                       	// Assign the percentage to the corresponding emotion variable
                            switch (emotionName.toLowerCase()) {
                                case "happy":
                                    happy = percentage;
                                    break;
                                case "sad":
                                    sad = percentage;
                                    break;
                                case "angry":
                                    angry = percentage;
                                    break;
                                case "scared":
                                    scared = percentage;
                                    break;
                                case "confused":
                                    confused = percentage;
                                    break;
                                default:
                                    System.out.println("Unknown emotion: " + emotionName);
                                    break;
                            }
                            System.out.println("Emotion Score: "+happy + sad + angry + scared + confused);
                    }
                        else if (fact.getName().equals("MAIN::sleep-quality")) {
                       	 sleepScore = fact.getSlotValue("score").intValue(null);
                       	 //percentage = fact.getSlotValue("percentage").intValue(null);
                       	System.out.println("Sleep Score: "+sleepScore);
                    
                   }
                        else   if (fact.getName().equals("MAIN::physical-activity")) {
                       	 pAScore = fact.getSlotValue("score").intValue(null);
                       	System.out.println("Physical Activity Score: "+pAScore);
                   }
                        else   if (fact.getName().equals("MAIN::FinalEffect")) {
                            // Retrieve the condition value from the fact's slot
                            Value conditionValue = fact.getSlotValue("temperature");
                            //Value scoreValue = fact.getSlotValue("final-mood-score");
                            if (conditionValue != null) {
                                System.out.println("Condition (temperature) value: " + conditionValue.stringValue(engine.getGlobalContext()));
                                weatherTemp = conditionValue.stringValue(engine.getGlobalContext());  // Set condition
                            } else {
                                System.out.println("Temperature slot not found in fact: " + fact);
                            }
                            /*if (scoreValue != null) {
                                System.out.println("Final mood score value: " + scoreValue.stringValue(engine.getGlobalContext()));
                                weatherImpact = Integer.parseInt(scoreValue.stringValue(engine.getGlobalContext()));  // Set finalMoodScore
                            } else {
                                System.out.println("Final-mood-score slot not found in fact: " + fact);
                            }*/
                        }
                        else if (fact.getName().equals("MAIN::Weather")) {
                        	 // Retrieve the condition value from the fact's slot
                            Value weatherValue = fact.getSlotValue("condition");
                            if (weatherValue != null) {
                                System.out.println("Weather Condition: " + weatherValue.stringValue(engine.getGlobalContext()));
                                weatherCon = weatherValue.stringValue(engine.getGlobalContext());  // Set condition
                            } else {
                                System.out.println("Condition slot not found in fact: " + fact);
                            }
                        	
                        }
              

            }   saveDataToDatabase(userId, happy, sad, angry, confused, scared, sleepScore, pAScore, foodScore, weatherCon, weatherTemp, jdbcUrl, dbUser, dbPassword);
            // Navigate to the next page
            addToNavigationStack();
            new RealTimeSuggestionPage();
            dispose();
                }catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing nutrition data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        contentPanel.add(nextButton);
        currentY += FIELD_HEIGHT + MARGIN;

        contentPanel.setPreferredSize(new Dimension(contentWidth, currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        new FoodPage();
    }
    private static void saveDataToDatabase(int userId, int hs, int ss, int as, int cs, int scs, int slps, int pas, int fs, String weatherCon, String weatherTemp, String jdbcUrl, String dbUser, String dbPassword) 
    {
    // Database connection and insertion logic
    Connection conn = null;
    PreparedStatement stmt = null;
    try {
    // Establish a connection to the database
    conn = DriverManager.getConnection(jdbcUrl, dbUser, dbPassword);
    // SQL insert query
    String query = "INSERT INTO daily_record (user_id, happy_score, sad_score, angry_score, confused_score, scared_score, sleep_score, physical_activity_score, food_score, weather_condition, weather_temperature) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    stmt = conn.prepareStatement(query);
    // Set parameters for the query
    stmt.setInt(1, userId);
    stmt.setInt(2, hs);
    stmt.setInt(3, ss);
    stmt.setInt(4, as);
    stmt.setInt(5, cs);
    stmt.setInt(6, scs);
    stmt.setInt(7, slps);
    stmt.setInt(8, pas);
    stmt.setInt(9, fs);
    stmt.setString(10,weatherCon);
    stmt.setString(11, weatherTemp);
    // Execute the query
    stmt.executeUpdate();
    System.out.println("Data saved to the database!");
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
