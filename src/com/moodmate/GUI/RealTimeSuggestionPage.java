package com.moodmate.GUI;

import javax.swing.*;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import jess.*;

public class RealTimeSuggestionPage extends BaseHomePage {

    private static final int PADDING_X = 30; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields
    private static final int PIC_SIZE = 100; // Height for the input fields   
    private static final int MARGIN = 20; // Vertical margin between components
    
    private static final int userId = GlobalVariable.userId;


    
    int contentWidth = contentArea.getWidth();

    public RealTimeSuggestionPage() {
        super();
        Rete engine = ReteEngineManager.getInstance();
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning
        
        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel("MoodMate Suggestions", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN;

     // Icon mapping for different recommendation types
        HashMap<String, String> templateIcons = new HashMap<>();
        templateIcons.put("recommendation", "self_image.png");
        templateIcons.put("MAIN::recommendation", "self_image.png");
        templateIcons.put("sleep-recommendation", "sleep.png");
        templateIcons.put("MAIN::sleep-recommendation", "sleep.png");
        templateIcons.put("physical-activity-recommendation", "physical_activity.png");
        templateIcons.put("MAIN::physical-activity-recommendation", "physical_activity.png");
        templateIcons.put("food-recommendation", "food.png");
        templateIcons.put("MAIN::food-recommendation", "food.png");
        templateIcons.put("Recommendation", "weather.png");
        templateIcons.put("MAIN::Recommendation", "weather.png");
        templateIcons.put("weather-recommendation", "weather.png");


        try {
            // Define the list of templates to retrieve recommendations from
        	List<String> templates = Arrays.asList(
        		    "recommendation",
        		    "sleep-recommendation",
        		    "physical-activity-recommendation",
        		    "food-recommendation",
        		    "Recommendation",  // This catches general recommendations including weather
        		    "weather-recommendation"  // This is for specific weather recommendations
        		);

            for (String template : templates) {
                List<String> recommendations = getRecommendations(engine, template);
                for (String recommendation : recommendations) {
                    JPanel container = new JPanel();
                    container.setLayout(null);
                    container.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, PIC_SIZE + FIELD_HEIGHT + 10);

                    String iconPath = "assets/images/suggestions/" + templateIcons.getOrDefault(
                        template.replace("MAIN::", ""), 
                        "default.png"
                    );
                    JLabel iconLabel = new JLabel(new ImageIcon(iconPath));
                    iconLabel.setBounds(0, 0, PIC_SIZE, PIC_SIZE);
                    container.add(iconLabel);

                    JLabel textLabel = new JLabel("<html>" + recommendation + "</html>");
                    textLabel.setFont(new Font(customFont, Font.PLAIN, 14));
                    textLabel.setBounds(PIC_SIZE + 10, 0, contentWidth - PIC_SIZE - PADDING_X * 2, PIC_SIZE);
                    container.add(textLabel);

                    contentPanel.add(container);
                    currentY += PIC_SIZE + FIELD_HEIGHT + MARGIN;
                }
            }

        } catch (JessException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Error retrieving recommendations: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
        }

     

        currentY += MARGIN;

        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
        nextButton.setBackground(customGreen);
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        nextButton.addActionListener(e -> {
            addToNavigationStack();
            new HomePage();
            dispose();
        });


        contentPanel.add(nextButton);
        currentY += FIELD_HEIGHT + MARGIN;

        contentPanel.setPreferredSize(new Dimension(contentWidth, currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

    private List<String> getRecommendations(Rete engine, String templateName) throws JessException {
        List<String> recommendations = new ArrayList<>();
        Iterator<?> facts = engine.listFacts();

        while (facts.hasNext()) {
            Fact fact = (Fact) facts.next();
            String factName = fact.getName();
            
            // Skip facts that aren't recommendations
            if (!factName.contains("recommendation") && 
                !factName.equals("MAIN::Recommendation")) {
                continue;
            }

            try {
                // Handle general recommendations (including weather)
                if (templateName.equals("Recommendation") && 
                    factName.equals("MAIN::Recommendation")) {
                    Value messageValue = fact.getSlotValue("message");
                    if (!messageValue.equals(Funcall.NIL)) {
                        String message = messageValue.stringValue(null);
                        if (message.toLowerCase().contains("weather") || 
                            message.toLowerCase().contains("cloudy") ||
                            message.toLowerCase().contains("temperature")) {
                            recommendations.add(message);
                        }
                    }
                } 
                // Handle specific recommendations
                else if (factName.equals(templateName) || 
                         factName.equals("MAIN::" + templateName)) {
                    Value messageValue = fact.getSlotValue("message");
                    if (!messageValue.equals(Funcall.NIL)) {
                        if (templateName.equals("weather-recommendation")) {
                            recommendations.add(messageValue.stringValue(null));
                        } else {
                            Value userIdValue = fact.getSlotValue("user_id");
                            if (!userIdValue.equals(Funcall.NIL) && 
                                userIdValue.intValue(engine.getGlobalContext()) == userId) {
                                recommendations.add(messageValue.stringValue(null));
                            }
                        }
                    }
                }
            } catch (JessException e) {
                // Skip facts that don't have the expected slots
                continue;
            }
        }
        return recommendations;
    }

    public static void main(String[] args) {
        new RealTimeSuggestionPage();
    }
}
