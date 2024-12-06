package com.moodmate.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.awt.*;
import javax.swing.*;

import jess.Fact;
import jess.JessException;
import jess.Rete;

public class HomePage extends BaseHomePage {

    private static final int PADDING_X = 30; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields and button
    private static final int CORNER_RADIUS = 30; //
    private static final int MARGIN = 20; //
    private static final int MOTIVATION_HEIGHT = 80; //
    private static final int PART1_HEIGHT = 100; //
    private static final int PART2_HEIGHT = 450; //
    
    private static final Color joyColor = new Color(255, 165, 0);    // A warm orange
    private static final Color sadnessColor = new Color(70, 130, 180);     // A calming steel blue
    private static final Color angerColor = new Color(255, 99, 71);       // A warm tomato red
    private static final Color scaredColor = new Color(147, 112, 219);  // A soft lavender purple
    private static final Color confusedColor = new Color(102, 205, 170);   // A fresh light green

  
    

    private static class EmotionData {
        List<List<Integer>> scores;
        List<Integer> hours;

        EmotionData(List<List<Integer>> scores, List<Integer> hours) {
            this.scores = scores;
            this.hours = hours;
        }
    }

    public HomePage() {
        super();
        WeatherScheduler.startWeatherUpdates();
        int currentY = 20;

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);

        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background_homePage.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentPanel.add(backgroundLabel);
        backgroundLabel.setLayout(null);

        JPanel motivationContainer = createMotivationContainer(currentY);
        backgroundLabel.add(motivationContainer);
        currentY += MOTIVATION_HEIGHT + MARGIN;

        JPanel partOneContainer = createPartOneContainer(currentY);
        backgroundLabel.add(partOneContainer);
        currentY += PART1_HEIGHT + MARGIN;

        JPanel partTwoContainer = createPartTwoContainer(currentY);
        backgroundLabel.add(partTwoContainer);
        currentY += PART2_HEIGHT + MARGIN;

        contentPanel.setPreferredSize(new Dimension(contentWidth, currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createMotivationContainer(int currentY) {
        JPanel motivationContainer = new JPanel();
        motivationContainer.setLayout(null);
        motivationContainer.setOpaque(false);
        motivationContainer.setBounds(PADDING_X, currentY, FRAME_WIDTH - 60, MOTIVATION_HEIGHT);

        JLabel motivationText = new JLabel(
                "<html>“Every day may not be good...<br>but there’s something good in every day.”</html>",
                SwingConstants.CENTER);
        motivationText.setFont(new Font(customFont, Font.ITALIC, 16));
        motivationText.setBounds(0, currentY, motivationContainer.getWidth() - 40, 40);
        motivationContainer.add(motivationText);
        return motivationContainer;
    }

    private JPanel createPartOneContainer(int currentY) {
        JPanel partOneContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        partOneContainer.setLayout(null);
        partOneContainer.setOpaque(false);
        partOneContainer.setBounds(PADDING_X, currentY, FRAME_WIDTH - 60, PART1_HEIGHT);

        JLabel titleLabel = new JLabel("Tell Me what you feel right now", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 18));
        int titleWidth = partOneContainer.getWidth() - 40;
        int titleHeight = FIELD_HEIGHT;
        int titleX = (partOneContainer.getWidth() - titleWidth) / 2;
        int titleY = (partOneContainer.getHeight() - FIELD_HEIGHT - MARGIN - FIELD_HEIGHT) / 2;
        titleLabel.setBounds(titleX, titleY, titleWidth, titleHeight);
        partOneContainer.add(titleLabel);

        JButton beginButton = new JButton("Begin");
        int buttonWidth = partOneContainer.getWidth() / 2;
        int buttonHeight = FIELD_HEIGHT;
        int buttonX = (partOneContainer.getWidth() - buttonWidth) / 2;
        int buttonY = FIELD_HEIGHT + MARGIN;
        beginButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        beginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        beginButton.setBackground(customGreen);
        beginButton.setForeground(Color.WHITE);
        beginButton.setBorder(BorderFactory.createLineBorder(customGreen, 1, true));
        beginButton.setOpaque(true);
        beginButton.addActionListener(e -> {
            addToNavigationStack();
            new EFTPage();
            dispose();
        });
        partOneContainer.add(beginButton);

        return partOneContainer;
    }

    private JPanel createPartTwoContainer(int currentY) {
        JPanel partTwoContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
            }
        };
        partTwoContainer.setLayout(new BorderLayout());
        partTwoContainer.setOpaque(false);
        partTwoContainer.setBounds(PADDING_X, currentY, FRAME_WIDTH - 60, PART2_HEIGHT);

        JLabel partTwoTitle = new JLabel("View Your Data", SwingConstants.CENTER);
        partTwoTitle.setFont(new Font(customFont, Font.BOLD, 16));
        partTwoTitle.setBorder(BorderFactory.createEmptyBorder(MARGIN - 20, 10, MARGIN - 20, 0));
        partTwoContainer.add(partTwoTitle, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font(customFont, Font.PLAIN, 14));

        EmotionData dailyData = getEmotionDataFromJess("daily");
        JPanel dailyTab = createGraphPanel("daily", dailyData.scores, dailyData.hours);
        tabbedPane.addTab("Daily", dailyTab);

        EmotionData weeklyData = getEmotionDataFromJess("weekly");
        JPanel weeklyTab = createGraphPanel("weekly", weeklyData.scores, weeklyData.hours);
        tabbedPane.addTab("Weekly", weeklyTab);

        EmotionData monthlyData = getEmotionDataFromJess("monthly");
        JPanel monthlyTab = createGraphPanel("monthly", monthlyData.scores, monthlyData.hours);
        tabbedPane.addTab("Monthly", monthlyTab);

        partTwoContainer.add(tabbedPane, BorderLayout.CENTER);

        return partTwoContainer;
    }

    private EmotionData getEmotionDataFromJess(String timeframe) {
        List<List<Integer>> defaultScores = Arrays.asList(
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0)
        );
        List<Integer> defaultHours = Arrays.asList(8, 10, 12, 14, 16);

        Map<Integer, Map<String, Integer>> emotionData = new HashMap<>();
        List<Integer> hours = new ArrayList<>();

        try {
            Rete engine = ReteEngineManager.getInstance();
            Iterator<?> facts = engine.listFacts();

            while (facts.hasNext()) {
                Fact fact = (Fact) facts.next();

                if (fact.getName().equals("MAIN::user-emotion") && timeframe.equals("daily")) {
                    int hour = fact.getSlotValue("hour").intValue(null);
                    String emotionName = fact.getSlotValue("emotion-name").stringValue(null);
                    int intensity = fact.getSlotValue("intensity").intValue(null);

                    if (!hours.contains(hour)) {
                        hours.add(hour);
                    }

                    emotionData.computeIfAbsent(hour, k -> new HashMap<>()).put(emotionName.toLowerCase(), intensity);

                } else if (fact.getName().equals("MAIN::daily-emotion-summary") && timeframe.equals("weekly")) {
                    int day = fact.getSlotValue("day").intValue(null);
                    String emotionName = fact.getSlotValue("emotion-name").stringValue(null);
                    int avgPercentage = fact.getSlotValue("avg-percentage").intValue(null);

                    if (!hours.contains(day)) {
                        hours.add(day);
                    }

                    emotionData.computeIfAbsent(day, k -> new HashMap<>()).put(emotionName.toLowerCase(), avgPercentage);
                }
            }

            if (hours.isEmpty()) {
                return new EmotionData(defaultScores, defaultHours);
            }

            Collections.sort(hours);

            String[] emotions = {"happy", "sad", "angry", "scared", "confused"};
            List<List<Integer>> scores = new ArrayList<>();

            for (String emotion : emotions) {
                List<Integer> emotionScores = new ArrayList<>();
                for (Integer time : hours) {
                    emotionScores.add(emotionData.getOrDefault(time, new HashMap<>()).getOrDefault(emotion, 0));
                }
                scores.add(emotionScores);
            }

            return new EmotionData(scores, hours);

        } catch (JessException e) {
            e.printStackTrace();
            System.out.println("Error while fetching data from Jess: " + e.getMessage());
            return new EmotionData(defaultScores, defaultHours);
        }
    }



    private JPanel createGraphPanel(String timeframe, List<List<Integer>> scores, List<Integer> hours) {
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout());
        graphPanel.setOpaque(false);

        JPanel chartArea = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String[] emotions = {"Happy", "Sad", "Angry", "Scared", "Confused"};
                Color[] colors = {joyColor, sadnessColor, angerColor, scaredColor, confusedColor};

                int width = getWidth();
                int height = getHeight() - 50; // Reserve space for labels
                int margin = 40;
                int graphWidth = width - 2 * margin;
                int graphHeight = height - 2 * margin;

                // Draw axes
                g2.setColor(Color.BLACK); // Set color explicitly for axes
                g2.drawLine(margin, height - margin, margin, margin);
                g2.drawLine(margin, height - margin, width - margin, height - margin);

                // Skip drawing if no data
                if (scores.isEmpty() || hours.isEmpty()) {
                    g2.setColor(Color.BLACK); // Explicitly set color for "No data available"
                    g2.drawString("No data available", width / 2 - 50, height / 2);
                    return;
                }

                // Draw points and lines
                for (int i = 0; i < scores.size(); i++) {
                    g2.setColor(colors[i]); // Use colors for emotion lines
                    List<Integer> emotionScores = scores.get(i);

                    for (int j = 0; j < hours.size(); j++) {
                        int x = margin + (j * graphWidth) / (Math.max(1, hours.size() - 1));
                        int y = height - margin - (emotionScores.get(j) * graphHeight) / 100;
                        g2.fillOval(x - 3, y - 3, 6, 6);

                        if (j < hours.size() - 1) {
                            int nextX = margin + ((j + 1) * graphWidth) / (Math.max(1, hours.size() - 1));
                            int nextY = height - margin - (emotionScores.get(j + 1) * graphHeight) / 100;
                            g2.drawLine(x, y, nextX, nextY);
                        }
                    }
                }

                // Draw labels explicitly in black
                g2.setColor(Color.BLACK); // Reset to black for text labels
                g2.setFont(new Font(customFont, Font.PLAIN, 10));
                for (int i = 0; i < hours.size(); i++) {

                    int x = margin;
                    if (hours.size() > 1) {
                        x = margin + (i * graphWidth) / (hours.size() - 1);
                    }
                    // Format hour from 1213 to 12:13
                    int rawHour = hours.get(i);
                    String formattedHour = String.format("%02d:%02d", rawHour / 100, rawHour % 100);

                    // Draw formatted hour on the graph
                    g2.drawString(formattedHour, x - 10, height - margin + 20);
           
                }

                // Draw emotion labels
                int labelY = height - margin + 40;
                int labelXStart = margin;
                int labelSpacing = (graphWidth - (emotions.length * 50)) / Math.max(1, emotions.length - 1);

                for (int i = 0; i < emotions.length; i++) {
                    int labelX = labelXStart + i * (50 + labelSpacing);
                    g2.setColor(colors[i]);
                    g2.drawString(emotions[i], labelX, labelY);
                }

                // Draw axes labels
                g2.setColor(Color.BLACK);
//                g2.drawString("Time", width / 2, height - margin + 20);
                g2.drawString("Score (%)", margin - 30, height / 2);

            }
        };

        chartArea.setPreferredSize(new Dimension(400, 10));
        graphPanel.add(chartArea, BorderLayout.CENTER);


     // Modified Suggestions section
        JPanel suggestionsPanel = new JPanel();
        suggestionsPanel.setLayout(new BoxLayout(suggestionsPanel, BoxLayout.Y_AXIS));
        suggestionsPanel.setOpaque(false);
        suggestionsPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Add padding

        JLabel suggestionsTitle = new JLabel("Suggestions");
        suggestionsTitle.setFont(new Font(customFont, Font.BOLD, 14));
        suggestionsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        suggestionsPanel.add(suggestionsTitle);
        suggestionsPanel.add(Box.createVerticalStrut(5));  // Add spacing
     
        String[] suggestions = generateSuggestions(timeframe);
        for (String suggestion : suggestions) {
            // Create a panel for each suggestion with proper wrapping
            JPanel suggestionItemPanel = new JPanel();
            suggestionItemPanel.setLayout(new BorderLayout());
            suggestionItemPanel.setOpaque(false);
            suggestionItemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));  // Allow height to expand
            suggestionItemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Wrap the suggestion text in HTML for proper wrapping
            JLabel suggestionLabel = new JLabel("<html><body style='width: " + 
                (contentArea.getWidth() - 100) + "px'>&bull; " + suggestion + "</body></html>");
            suggestionLabel.setFont(new Font(customFont, Font.PLAIN, 12));
            suggestionItemPanel.add(suggestionLabel, BorderLayout.CENTER);
            
            suggestionsPanel.add(suggestionItemPanel);
            suggestionsPanel.add(Box.createVerticalStrut(5));  // Add spacing between items
        }
        suggestionsPanel.add(Box.createVerticalGlue());
        

        graphPanel.add(suggestionsPanel, BorderLayout.SOUTH);

        return graphPanel;
    }

    private String[] generateSuggestions(String timeframe) {
        List<String> suggestions = new ArrayList<>();

//        suggestions.add("Default suggestions for " + timeframe);

        try {
            Rete engine = ReteEngineManager.getInstance();
            Iterator<?> facts = engine.listFacts();
            
            while (facts.hasNext()) {
                Fact fact = (Fact) facts.next();
                
                // Check for various types of recommendations
                if (fact.getName().equals("MAIN::recommendation") ||
                    fact.getName().equals("MAIN::food-recommendation") || 
                    fact.getName().equals("MAIN::sleep-recommendation") ||
                    fact.getName().equals("MAIN::physical-activity-recommendation") || 
                    fact.getName().equals("MAIN::weather-recommendation")) {
                    try {
                        jess.Value messageValue = fact.getSlotValue("message");
                        String message = messageValue.stringValue(null);
                        if (message != null && !message.isEmpty()) {
                            suggestions.add(message);
                        }
                    } catch (JessException e) {
                        System.out.println("Error reading recommendation message: " + e.getMessage());
                    }
                }
            }
            
            // If no recommendations found, provide default suggestions
            if (suggestions.isEmpty()) {
                switch (timeframe.toLowerCase()) {
                    case "daily":
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personolized suggestions");
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personolized suggestions");
                        break;
                    case "weekly":
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personolized suggestions");
                        break;
                    case "monthly":
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personolized suggestions");
                        break;
                }
            }
            
            // Limit to 3 suggestions to avoid overcrowding
            if (suggestions.size() > 3) {
                suggestions = suggestions.subList(0, 3);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            // Return default suggestions if there's an error
            return new String[]{
                "Take care of yourself",
                "Consider talking to someone you trust",
                "Remember to stay hydrated"
            };
        }

        return suggestions.toArray(new String[0]);
    }

    public static void main(String[] args) {
        new HomePage();
    }

    @Override
    public void dispose() {
        WeatherScheduler.stopWeatherUpdates();
        super.dispose();
    }
}
