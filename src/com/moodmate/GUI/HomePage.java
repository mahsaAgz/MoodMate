package com.moodmate.GUI;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.swing.*;

import jess.Fact;
import jess.JessException;
import jess.QueryResult;
import jess.Rete;
import jess.ValueVector;

public class HomePage extends BaseHomePage {
    private static final int PADDING_X = 30; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields and button
    private static final int CORNER_RADIUS = 30; //
    private static final int MARGIN = 20; //
    private static final int MOTIVATION_HEIGHT = 80; //
    private static final int PART1_HEIGHT = 100; //
    private static final int PART2_HEIGHT = 450; //
//    private static final int PART3_HEIGHT = 300; 
    
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
        
        int currentY = 20;
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning
        // Background label
        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background_homePage.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentPanel.add(backgroundLabel);
        backgroundLabel.setLayout(null);

        // Create container for motivational text with rounded corners
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

        backgroundLabel.add(motivationContainer);

        currentY += MOTIVATION_HEIGHT + MARGIN;

        // Create container for Part One
        JPanel partOneContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS); // Rounded corners
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS); // Border
            }
        };

        partOneContainer.setLayout(null);
        partOneContainer.setOpaque(false);
        partOneContainer.setBounds(PADDING_X, currentY, FRAME_WIDTH - 60, PART1_HEIGHT);

        JLabel titleLabel = new JLabel("Tell Me what you feel right now", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 18));
        int titleWidth = partOneContainer.getWidth() - 40; // Width with some padding
        int titleHeight = FIELD_HEIGHT; // Fixed height for the label
        int titleX = (partOneContainer.getWidth() - titleWidth) / 2; // Center horizontally
        int titleY = (partOneContainer.getHeight() - FIELD_HEIGHT - MARGIN - FIELD_HEIGHT) / 2; // Center vertically
        titleLabel.setBounds(titleX, titleY, titleWidth, titleHeight);
        partOneContainer.add(titleLabel);

        JButton beginButton = new JButton("Begin");
        int buttonWidth = partOneContainer.getWidth() / 2; // Half the container's width
        int buttonHeight = FIELD_HEIGHT;
        int buttonX = (partOneContainer.getWidth() - buttonWidth) / 2; // Center horizontally
        int buttonY = FIELD_HEIGHT + MARGIN; // Position below the title
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

        currentY += PART1_HEIGHT + MARGIN;
        backgroundLabel.add(partOneContainer);
    

        // PART 2: Gallery-like page with tabs for real-time data (Daily, Weekly, Monthly)
        JPanel partTwoContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS); // Rounded corners
                g2.setColor(Color.WHITE);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS); // Border
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

     
        EmotionData emotionData = getEmotionDataFromJess();
        List<List<Integer>> dailyScores = emotionData.scores;
        List<Integer> hours = emotionData.hours;

        
     // Create tabs with the data
        JPanel dailyTab = new JPanel(new BorderLayout());
        dailyTab.setBackground(Color.WHITE);
        dailyTab.add(createGraphPanel("daily", dailyScores, hours), BorderLayout.CENTER);
        tabbedPane.addTab("Daily", dailyTab);

        JPanel weeklyTab = new JPanel(new BorderLayout());
        weeklyTab.setBackground(Color.WHITE);
        weeklyTab.add(createGraphPanel("weekly", dailyScores, hours), BorderLayout.CENTER);
        tabbedPane.addTab("Weekly", weeklyTab);

        JPanel monthlyTab = new JPanel(new BorderLayout());
        monthlyTab.setBackground(Color.WHITE);
        monthlyTab.add(createGraphPanel("monthly", dailyScores, hours), BorderLayout.CENTER);
        tabbedPane.addTab("Monthly", monthlyTab);

        partTwoContainer.add(tabbedPane, BorderLayout.CENTER);
        backgroundLabel.add(partTwoContainer);

        currentY += PART2_HEIGHT + MARGIN;

 
        currentY += MARGIN;

    
        contentPanel.setPreferredSize(new Dimension(contentWidth, currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }
    
    private EmotionData getEmotionDataFromJess() {
        Map<Integer, Map<String, Double>> hourlyEmotions = new HashMap<>();
        List<Integer> hours = new ArrayList<>();
        
        try {
            Rete engine = ReteEngineManager.getInstance();
            Iterator<?> facts = engine.listFacts();
            boolean hasEmotionData = false;
            
            while (facts.hasNext()) {
                Fact fact = (Fact) facts.next();
                if (fact.getName().equals("MAIN::normalized-emotion")) {
                    hasEmotionData = true;
                    try {
                        // Properly handle Value types from Jess
                        jess.Value hourValue = fact.getSlotValue("hour");
                        jess.Value emotionValue = fact.getSlotValue("emotion-name");
                        jess.Value percentageValue = fact.getSlotValue("percentage");
                        
                        // Convert to appropriate types
                        int hour = hourValue.intValue(null);
                        String emotionName = emotionValue.stringValue(null);
                        double percentage = percentageValue.floatValue(null);
                        
                        if (!hours.contains(hour)) {
                            hours.add(hour);
                        }
                        
                        hourlyEmotions.computeIfAbsent(hour, k -> new HashMap<>())
                                     .put(emotionName.toLowerCase(), percentage);
                                     
                        System.out.println("Processed emotion data: Hour=" + hour + 
                                         ", Emotion=" + emotionName + 
                                         ", Percentage=" + percentage);
                    } catch (JessException e) {
                        System.out.println("Error reading fact values: " + e.getMessage());
                    }
                }
            }
            
            if (!hasEmotionData) {
                System.out.println("No emotion data found, using default data");
                return createDefaultEmotionData();
            }
            
            Collections.sort(hours);
            
            List<List<Integer>> emotionScores = new ArrayList<>();
            String[] emotions = {"happy", "sad", "angry", "scared", "confused"};
            
            for (String emotion : emotions) {
                List<Integer> scores = new ArrayList<>();
                for (Integer hour : hours) {
                    Map<String, Double> hourData = hourlyEmotions.getOrDefault(hour, new HashMap<>());
                    double percentage = hourData.getOrDefault(emotion, 0.0);
                    scores.add((int) Math.round(percentage));
                }
                emotionScores.add(scores);
                System.out.println("Processed scores for " + emotion + ": " + scores);
            }
            
            return new EmotionData(emotionScores, hours);
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error in getEmotionDataFromJess: " + e.getMessage());
            return createDefaultEmotionData();
        }
    }
    // Helper method to create default data remains unchanged
    private EmotionData createDefaultEmotionData() {
        List<List<Integer>> emptyScores = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            emptyScores.add(Arrays.asList(0, 0, 0, 0));
        }
        return new EmotionData(emptyScores, Arrays.asList(8, 10, 12, 14));
    }
    private Component createGraphPanel(String timeframe, List<List<Integer>> scores, List<Integer> hours) {
        JPanel graphPanel = new JPanel();
        graphPanel.setLayout(new BorderLayout());
        graphPanel.setOpaque(false);

        // Graph section
        JPanel chartArea = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                String[] emotions = {"Happy", "Sad", "Angry", "Scared", "Confused"};
                Color[] colors = {Color.YELLOW, Color.BLUE, Color.RED, Color.PINK, Color.GREEN};

                int width = getWidth(), height = getHeight() - 50, margin = 40;
                int graphWidth = width - 2 * margin, graphHeight = height - 2 * margin;

                // Draw axes
                g2.setColor(Color.BLACK);
                g2.drawLine(margin, height - margin, margin, margin);
                g2.drawLine(margin, height - margin, width - margin, height - margin);

                // Skip drawing lines if we have less than 2 data points
                if (hours.size() >= 1) {
                    // Draw points and lines
                    for (int i = 0; i < scores.size(); i++) {
                        g2.setColor(colors[i]);
                        List<Integer> emotionScores = scores.get(i);
                        
                        // Draw points
                        for (int j = 0; j < hours.size(); j++) {
                            int x = margin + (j * graphWidth) / Math.max(1, hours.size() - 1);
                            int y = height - margin - (emotionScores.get(j) * graphHeight) / 100;
                            g2.fillOval(x - 3, y - 3, 6, 6);
                        }
                        
                        // Draw lines if we have more than one point
                        if (hours.size() > 1) {
                            for (int j = 0; j < hours.size() - 1; j++) {
                                int x1 = margin + (j * graphWidth) / (hours.size() - 1);
                                int y1 = height - margin - (emotionScores.get(j) * graphHeight) / 100;
                                int x2 = margin + ((j + 1) * graphWidth) / (hours.size() - 1);
                                int y2 = height - margin - (emotionScores.get(j + 1) * graphHeight) / 100;
                                g2.drawLine(x1, y1, x2, y2);
                            }
                        }
                    }
                }

                // Draw hour labels
                g2.setColor(Color.BLACK);
                g2.setFont(new Font(customFont, Font.PLAIN, 10));
                for (int i = 0; i < hours.size(); i++) {
                    int x = margin;
                    if (hours.size() > 1) {
                        x = margin + (i * graphWidth) / (hours.size() - 1);
                    }
                    g2.drawString(hours.get(i) + ":00", x - 10, height - margin + 20);
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
                g2.drawString("Time", width / 2, height - margin + 20);
                g2.drawString("Score (%)", margin - 30, height / 2);
            }
        };

        chartArea.setPreferredSize(new Dimension(400, 200));
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
        
        // Wrap suggestions panel in a scroll pane in case content is too long
        JScrollPane suggestionsScrollPane = new JScrollPane(suggestionsPanel);
        suggestionsScrollPane.setOpaque(false);
        suggestionsScrollPane.getViewport().setOpaque(false);
        suggestionsScrollPane.setBorder(null);
        suggestionsScrollPane.setPreferredSize(new Dimension(400, 120));  // Set fixed height for suggestions area
        
        graphPanel.add(suggestionsScrollPane, BorderLayout.SOUTH);
        return graphPanel;
    }


    private String[] generateSuggestions(String timeframe) {
        List<String> suggestions = new ArrayList<>();
        try {
            Rete engine = ReteEngineManager.getInstance();
            Iterator<?> facts = engine.listFacts();
            
            while (facts.hasNext()) {
                Fact fact = (Fact) facts.next();
                // Check for various types of recommendations
                if (fact.getName().equals("MAIN::recommendation") ||
                    fact.getName().equals("MAIN::food-recommendation") || 
                    fact.getName().equals("MAIN::sleep-recommendation") ||
                    fact.getName().equals("MAIN::physical-activity-recommendation")) {
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
                        suggestions.add("Take a 10-minute walk");
                        suggestions.add("Drink a glass of water");
                        suggestions.add("Write down 3 things you're grateful for");
                        break;
                    case "weekly":
                        suggestions.add("Review your week's achievements");
                        suggestions.add("Plan your meals for the week");
                        suggestions.add("Schedule a call with a loved one");
                        break;
                    case "monthly":
                        suggestions.add("Set your goals for the month");
                        suggestions.add("Declutter your workspace");
                        suggestions.add("Reflect on personal growth");
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
}
