package com.moodmate.GUI;
import com.moodmate.GUI.SignInPage.GlobalVariable;
import javax.swing.*;

import javax.swing.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jess.JessException;
import jess.Rete;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Hashtable;

public class NotificationSettingPage extends BasePage {

    private static final int PADDING_X = 60; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields
    private static final int MARGIN = 20; // Vertical margin between components
    int contentWidth = contentArea.getWidth();
    private String username;
    private int age;
    private String gender;
    private String mbtiResult;
    private String hobbies;
    private int userID;
    int frequency =0 ;
    
    public NotificationSettingPage(int userID,String username, int age, int gender, String mbtiResult, String hobbies) {
        super();
        this.userID = userID;
        this.username = username;
        this.age = age;
        this.gender = gender == 1 ? "Male" : gender == 2 ? "Female" : "Prefer not to say";
        this.mbtiResult = mbtiResult;
        this.hobbies = hobbies;

        // Create a contentPanel for all components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning
        contentPanel.setBackground(customBackgroundColor);

        // Add background image (it's just a visual element, so we add it to contentPanel)
        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background.png"));
        backgroundLabel.setBounds(0, 0, contentWidth, contentArea.getHeight());
        contentPanel.add(backgroundLabel);
        backgroundLabel.setLayout(null);

        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel(" Notification Settings", SwingConstants.LEFT);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN;

        // Informational Text
        JLabel infoLabel = new JLabel("<html><div>"
                + "<b>Why Set Your Emotion Check-in Frequency?</b><br><br>"
                + "Tracking your emotions regularly helps you understand and manage how you feel.<br>"
                + "You only need to tell us the rate each 5 key feelings: Joy, Sadness, Anger, Fear, and Disgust.<br><br>"
                + "<ul>"
                + "<li><b>Understand your emotions</b></li>"
                + "<li><b>Spot patterns over time</b></li>"
                + "<li><b>Receive support to feel balanced</b></li>"
                + "</ul>"
                + "Choose a frequency that works for you and start your journey to emotional well-being!"
                + "</div></html>");
        infoLabel.setFont(new Font(customFont, Font.PLAIN, 14));
        infoLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, 300);
        contentPanel.add(infoLabel);

        currentY += 300 + MARGIN;

        // Radio Buttons for Frequency Selection
        JLabel frequencyLabel = new JLabel("Check-in Frequency:");
        frequencyLabel.setFont(new Font(customFont, Font.BOLD, 16));
        frequencyLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(frequencyLabel);

        currentY += FIELD_HEIGHT + MARGIN;

        JRadioButton oneHourButton = new JRadioButton("1 Hour");
        JRadioButton twoHourButton = new JRadioButton("2 Hours");
        JRadioButton threeHourButton = new JRadioButton("3 Hours");

        ButtonGroup frequencyGroup = new ButtonGroup();
        frequencyGroup.add(oneHourButton);
        frequencyGroup.add(twoHourButton);
        frequencyGroup.add(threeHourButton);

        oneHourButton.setBounds(PADDING_X, currentY, 100, FIELD_HEIGHT);
        twoHourButton.setBounds(PADDING_X + 110, currentY, 100, FIELD_HEIGHT);
        threeHourButton.setBounds(PADDING_X + 220, currentY, 100, FIELD_HEIGHT);

        contentPanel.add(oneHourButton);
        contentPanel.add(twoHourButton);
        contentPanel.add(threeHourButton);

        currentY += FIELD_HEIGHT + MARGIN;

        // Next Button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
        nextButton.setBackground(customGreen);
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        

        nextButton.addActionListener(e -> {
            // Check if any radio button is selected
            if (oneHourButton.isSelected() || twoHourButton.isSelected() || threeHourButton.isSelected()) {
                try {
                    // Get selected frequency
                    frequency = 1; // Default to 1 hour
                    if (twoHourButton.isSelected()) {
                        frequency = 2;
                    } else if (threeHourButton.isSelected()) {
                        frequency = 3;
                    }

                    // Initialize Jess engine
                    Rete engine = new Rete();
                    engine.reset();
                    engine.batch("src/com/moodmate/logic/templates.clp");
                    engine.batch("src/com/moodmate/logic/user_profile_rules.clp");

                    // Assert profile with all information including notification frequency
                    String assertCommand = String.format(
                    	    "(assert (profile-input " +
                    	    "(user_id %d) " +
                    	    "(name \"%s\") " +
                    	    "(gender %d) " +
                    	    "(age %d) " +
                    	    "(mbti \"%s\") " +
                    	    "(hobbies \"%s\") " +
                    	    "(notification-frequency %d)))",
                    	    GlobalVariable.userId, username, gender, age, mbtiResult, hobbies, frequency
                    	);
                    
                    System.out.println("Updating profile with notification frequency: " + assertCommand);
                    
                    engine.eval(assertCommand);
                    engine.run();

                    // Proceed to next page
                    addToNavigationStack();
                    new SignInPage();
                    dispose();

                } catch (JessException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        this,
                        "Error updating notification settings: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } else {
                // Show a warning dialog if no option is selected
                JOptionPane.showMessageDialog(
                    contentPanel,
                    "Please select a frequency before proceeding.",
                    "Selection Required",
                    JOptionPane.WARNING_MESSAGE
                );
            }
        
            try (Connection connection = DriverManager.getConnection(
            		"jdbc:mysql://localhost:3306/moodMate", "root", "002915")) {
                // Prepare SQL query
                String sql = "INSERT INTO user_info (user_id, name, age, gender, mbti, hobbies, notification) "
                           + "VALUES (?, ?, ?, ?, ?, ?, ?) "
                           + "ON DUPLICATE KEY UPDATE "
                           + "user_id = VALUES(user_id), name = VALUES(name), age = VALUES(age), gender = VALUES(gender), "
                           + "mbti = VALUES(mbti), hobbies = VALUES(hobbies), notification = VALUES(notification)";
               // int genderString= gender =="Male" ? 1  : gender == "Female" ? 2 : "Prefer not to say";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setInt(1, userID);
                    statement.setString(2, username);
                    statement.setInt(3, age);
                    statement.setInt(4, gender);
                    statement.setString(5, mbtiResult);
                    statement.setString(6, hobbies);
                    statement.setInt(7, frequency);
                    // Execute update
                    int rowsAffected = statement.executeUpdate();
                    System.out.println(rowsAffected + " row(s) updated in the database.");
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error saving notification settings to the database: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        contentPanel.add(nextButton);

        currentY += MARGIN;

        contentPanel.setPreferredSize(new Dimension(contentWidth, currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

    public NotificationSettingPage() {
    	this(0,"", 0, 0, "", "");
    }
}
