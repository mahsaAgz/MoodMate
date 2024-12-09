package com.moodmate.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

import com.moodmate.database.DatabaseConnection;

import jess.Fact;
import jess.JessException;
import jess.Rete;
import java.time.LocalDate;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;


public class HomePage extends BaseHomePage {

    private static final int PADDING_X = 30; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields and button
    private static final int CORNER_RADIUS = 30; //
    private static final int MARGIN = 20; //
    private static final int MOTIVATION_HEIGHT = 80; //
    private static final int PART1_HEIGHT = 100; //

    private static final int CHART_HEIGHT = 300;

    private static int SUG_HEIGHT = 200;
    
    
    private static int PART2_HEIGHT = CHART_HEIGHT + SUG_HEIGHT + FIELD_HEIGHT + 150; //
    
    
    
    private static final Color joyColor = new Color(255, 165, 0);    // A warm orange
    private static final Color sadnessColor = new Color(70, 130, 180);     // A calming steel blue
    private static final Color angerColor = new Color(255, 99, 71);       // A warm tomato red
    private static final Color scaredColor = new Color(147, 112, 219);  // A soft lavender purple
    private static final Color confusedColor = new Color(102, 205, 170);   // A fresh light green

    int contentWidth= FRAME_WIDTH - 60;


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
	
	    // Create contentPanel with null layout
	    JPanel contentPanel = new JPanel();
	    contentPanel.setLayout(null);
	
	    // Create a fixed background label
	    JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background_homePage.png"));
	    backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
	    backgroundLabel.setLayout(null); // No layout needed for the background
	    contentPanel.add(backgroundLabel);
	
	    // Add content to contentPanel (not backgroundLabel)
	    JPanel motivationContainer = createMotivationContainer(currentY);
	    contentPanel.add(motivationContainer);
	    currentY += MOTIVATION_HEIGHT + MARGIN;
	
	    JPanel partOneContainer = createPartOneContainer(currentY);
	    contentPanel.add(partOneContainer);
	    currentY += PART1_HEIGHT + MARGIN;
	
	    JPanel partTwoContainer = createPartTwoContainer(currentY);
	    contentPanel.add(partTwoContainer);
	    currentY += PART2_HEIGHT + MARGIN;
	
	    // Ensure contentPanel height matches content
	    contentPanel.setPreferredSize(new Dimension(contentArea.getWidth(), currentY + 40));
	    contentPanel.setBounds(0, 0, contentArea.getWidth(), currentY + 40);
	
	    // Ensure backgroundLabel stays at the bottom
	    contentPanel.setComponentZOrder(backgroundLabel, contentPanel.getComponentCount() - 1);
	
	    // Add contentPanel to scrollPane
	    JScrollPane scrollPane = new JScrollPane(contentPanel);
	    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	
	    // Add scrollPane to contentArea
	    contentArea.add(scrollPane, BorderLayout.CENTER);
	
	    // Revalidate and repaint
	    contentPanel.revalidate();
	    contentPanel.repaint();
	    scrollPane.revalidate();
	    scrollPane.repaint();
	    contentArea.revalidate();
	    contentArea.repaint();
	}

	
	private JPanel createPartTwoContainer(int currentY) {
	    JPanel partTwoContainer = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2 = (Graphics2D) g;
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	            g2.setColor(new Color(255, 255, 255, 200)); // Semi-transparent white
	            g2.fillRoundRect(0, 0, getWidth(), getHeight(), CORNER_RADIUS, CORNER_RADIUS);
	            g2.setColor(Color.WHITE);
	            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, CORNER_RADIUS, CORNER_RADIUS);
	        }
	    };
	
	    partTwoContainer.setLayout(null); // Absolute positioning
	    partTwoContainer.setBounds(PADDING_X, currentY, FRAME_WIDTH - 60, PART2_HEIGHT);
	
	    // Title for Container Two
	    JLabel partTwoTitle = new JLabel("View Your Data", SwingConstants.CENTER);
	    partTwoTitle.setFont(new Font(customFont, Font.BOLD, 16));
	    partTwoTitle.setBounds(0, 0, FRAME_WIDTH - 80, 30); // Adjusted for proper alignment
	    partTwoContainer.add(partTwoTitle);
	
	    // Tabbed Pane for Daily, Weekly, Monthly
	    JTabbedPane tabbedPane = new JTabbedPane();
	    tabbedPane.setFont(new Font(customFont, Font.PLAIN, 14));
	    tabbedPane.setBounds(10, 30, FRAME_WIDTH - 80, PART2_HEIGHT - 60);
//	    tabbedPane.setBackground(Color.black);
	    tabbedPane.setOpaque(false);

	    // Create and add tabs with graphs
	    EmotionData dailyData = getEmotionDataFromJess("daily");
	    JPanel dailyTab = createGraphPanel("daily", dailyData.scores, dailyData.hours);
	    tabbedPane.addTab("Daily", dailyTab);
	
	    EmotionData weeklyData = getEmotionDataFromJess("weekly");
	    JPanel weeklyTab = createGraphPanel("weekly", weeklyData.scores, weeklyData.hours);
	    tabbedPane.addTab("Weekly", weeklyTab);
	
	    EmotionData monthlyData = getEmotionDataFromJess("monthly");
	    JPanel monthlyTab = createGraphPanel("monthly", monthlyData.scores, monthlyData.hours);
	    tabbedPane.addTab("Monthly", monthlyTab);
	    
	    


	    partTwoContainer.add(tabbedPane);
	    
	    partTwoContainer.revalidate();
	    partTwoContainer.repaint();

	    

	    return partTwoContainer;
	}
	
			
	private JPanel createGraphPanel(String timeframe, List<List<Integer>> scores, List<Integer> hours) {
	    JPanel graphPanel = new JPanel();
	    graphPanel.setLayout(null); // Absolute positioning
	    graphPanel.setOpaque(true);
	
	    // Chart Area with Custom Painting
	    JPanel chartArea = new JPanel() {
	        @Override
	        protected void paintComponent(Graphics g) {
	            super.paintComponent(g);
	            Graphics2D g2 = (Graphics2D) g;
	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	
	            String[] emotions = {"Happy", "Sad", "Angry", "Scared", "Confused"};
	            Color[] colors = {joyColor, sadnessColor, angerColor, scaredColor, confusedColor};
	
	            int width = getWidth();
	            int height = getHeight() - 50; // Leave space for labels
	            int margin = 40;
	            int graphWidth = width - 2 * margin;
	            int graphHeight = height - 2 * margin;
	
	            // Draw Axes
	            g2.setColor(Color.BLACK);
	            g2.drawLine(margin, height - margin, margin, margin); // Y-axis
	            g2.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
	
	            if (scores.isEmpty() || hours.isEmpty()) {
	                g2.setColor(Color.BLACK);
	                g2.drawString("No data available", width / 2 - 50, height / 2);
	                return;
	            }
	
	            // Plot Points and Draw Lines
	            for (int i = 0; i < scores.size(); i++) {
	                g2.setColor(colors[i]);
	                List<Integer> emotionScores = scores.get(i);
	
	                for (int j = 0; j < hours.size(); j++) {
	                    int x = margin + (j * graphWidth) / (Math.max(1, hours.size() - 1));
	                    int y = height - margin - (emotionScores.get(j) * graphHeight) / 100;
	                    g2.fillOval(x - 3, y - 3, 6, 6); // Draw point
	
	                    if (j < hours.size() - 1) {
	                        int nextX = margin + ((j + 1) * graphWidth) / (Math.max(1, hours.size() - 1));
	                        int nextY = height - margin - (emotionScores.get(j + 1) * graphHeight) / 100;
	                        g2.drawLine(x, y, nextX, nextY); // Draw line to next point
	                    }
	                }
	            }
	
	            // Draw Labels for Time and Emotions
	            g2.setColor(Color.BLACK);
	            g2.setFont(new Font(customFont, Font.PLAIN, 10));
	
	            for (int j = 0; j < hours.size(); j++) {
	                int x = margin;
	                if (hours.size() > 1) {
	                    x = margin + (j * graphWidth) / (hours.size() - 1);
	                }
	
	                String label;
	                if (timeframe.equals("weekly") || timeframe.equals("monthly")) {
	                    int rawHour = hours.get(j);
	                    label = String.valueOf(rawHour);
	                } else {
	                    int rawHour = hours.get(j);
	                    label = String.format("%02d:%02d", rawHour / 100, rawHour % 100);
	                }
	
	                g2.drawString(label, x - 10, height - margin + 20);
	            }
	
	            // Draw Emotion Labels Below the Chart
	            int labelY = height - margin + 40;
	            int labelXStart = margin;
	            int labelSpacing = (graphWidth - (emotions.length * 50)) / Math.max(1, emotions.length - 1);
	
	            for (int i = 0; i < emotions.length; i++) {
	                int labelX = labelXStart + i * (50 + labelSpacing);
	                g2.setColor(colors[i]);
	                g2.drawString(emotions[i], labelX, labelY);
	            }
	        }
	    };
	
	    // Chart Area Bounds
	    chartArea.setBounds(0, 10, FRAME_WIDTH - 80, CHART_HEIGHT);
	    chartArea.setOpaque(false);
	    graphPanel.add(chartArea);
	
	    // Suggestions Panel
	    JPanel suggestionsPanel = new JPanel();
	    suggestionsPanel.setLayout(new BoxLayout(suggestionsPanel, BoxLayout.Y_AXIS));
	    suggestionsPanel.setOpaque(false);
	
	    // Suggestions logic
	    String[] suggestions = generateSuggestions(timeframe);
	  
	    String suggestionString = "<html><body style='width: 230px;'>";

  
	    for (String suggestion : suggestions) {
	    	suggestionString+= "<li>" + suggestion+ "</li>";
	     }
	    suggestionString += "</body></html>";
	    JLabel suggestionLabel = new JLabel(suggestionString);
        suggestionLabel.setFont(new Font(customFont, Font.PLAIN, 12));
        suggestionsPanel.add(suggestionLabel);

	
	    suggestionsPanel.setBounds(0, CHART_HEIGHT + 20, FRAME_WIDTH - 80, SUG_HEIGHT);

	    
	    graphPanel.add(suggestionsPanel);
	    
	    Dimension suggestionPreferredSize = suggestionLabel.getPreferredSize();
//	    System.out.println("suggestionLabel preferred height: " + suggestionPreferredSize.height);

	    if (suggestionPreferredSize.height > SUG_HEIGHT) {
	    	
		    // TODO
		    // read more button that has all the suggestions and goes to SuggestionPage.java
		    // Add "Read More" button
		    JButton readMoreButton = new JButton("Read More ...");
		    readMoreButton.setBounds(20, CHART_HEIGHT + SUG_HEIGHT + 20, 120, FIELD_HEIGHT);
		    readMoreButton.setBackground(customGreen);
		    readMoreButton.setForeground(Color.black);
		    readMoreButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
//		    readMoreButton.setBorder(BorderFactory.createLineBorder(customGreen, 1, true));
		    readMoreButton.setOpaque(false);
		    
		    readMoreButton.addActionListener(e -> {
		        // Generate the suggestions
		        String[] suggestions2 = generateSuggestions(timeframe);

		        // Combine all suggestions into a single HTML-formatted string
		        StringBuilder suggestionHtml = new StringBuilder("<html><body style='width: 250px;'><ul>");
		        for (String suggestion : suggestions2) {
		            suggestionHtml.append("<li>").append(suggestion).append("</li>");
		        }
		        suggestionHtml.append("</ul></body></html>");

		        // Create a JLabel with the formatted suggestions
		        JLabel suggestionsLabel = new JLabel(suggestionHtml.toString());

		        // Wrap the JLabel in a JScrollPane
		        JScrollPane scrollPane = new JScrollPane(suggestionsLabel);
		        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		        scrollPane.setPreferredSize(new Dimension(350, 300)); // Set fixed width and height

		        // Display the JScrollPane inside a JOptionPane
		        JOptionPane.showMessageDialog(
		                graphPanel, // Parent component
		                scrollPane, // Message (the scrollable content)
		                "All Suggestions", // Title
		                JOptionPane.PLAIN_MESSAGE // Removes the icon
		        );
		    });




		    graphPanel.add(readMoreButton);


	    }


	    
	    graphPanel.revalidate();
	    graphPanel.repaint();
	
	    return graphPanel;
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

    private EmotionData getEmotionDataFromJess(String timeframe) {
        List<List<Integer>> defaultScores = Arrays.asList(
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0),
            Arrays.asList(0, 0, 0, 0, 0)
        );
        List<Integer> defaultHours;
        LocalDate now = LocalDate.now();
        
     // Set appropriate default values based on timeframe
        switch (timeframe) {
            case "daily":
                defaultHours = Arrays.asList(800, 1000, 1200, 1400, 1600);
                break;
            case "weekly":
                // Create dates for the last 5 days in YYYYMMDD format
                defaultHours = new ArrayList<>();
                for (int i = 4; i >= 0; i--) {
                    LocalDate date = now.minusDays(i);
                    int dateNum = date.getYear() * 10000 + 
                                date.getMonthValue() * 100 + 
                                date.getDayOfMonth();
                    defaultHours.add(dateNum);
                }
                break;
            case "monthly":
                // Create months for the last 5 months in YYYYMM format
                defaultHours = new ArrayList<>();
                for (int i = 4; i >= 0; i--) {
                    LocalDate date = now.minusMonths(i);
                    int monthNum = date.getYear() * 100 + 
                                 date.getMonthValue();
                    defaultHours.add(monthNum);
                }
                break;
            default:
                defaultHours = Arrays.asList(800, 1000, 1200, 1400, 1600);
        }
        
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
                } else if (fact.getName().equals("MAIN::monthly-emotion-summary") && timeframe.equals("monthly")) {
                    int month = fact.getSlotValue("month").intValue(null);
                    String emotionName = fact.getSlotValue("emotion-name").stringValue(null);
                    int avgPercentage = fact.getSlotValue("avg-percentage").intValue(null);

                    if (!hours.contains(month)) {
                        hours.add(month);
                    }

                    emotionData.computeIfAbsent(month, k -> new HashMap<>())
                              .put(emotionName.toLowerCase(), avgPercentage);
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

    private String[] generateSuggestions(String timeframe) {
        List<String> suggestions = new ArrayList<>();

//        suggestions.add("Default suggestions for " + timeframe);

        try {
            Rete engine = ReteEngineManager.getInstance();
            Iterator<?> facts = engine.listFacts();
            
            while (facts.hasNext()) {
                Fact fact = (Fact) facts.next();
                
                if (timeframe.equals("daily")) {
                    // For daily tab, keep the existing recommendations
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
                } else {
                    // For weekly and monthly tabs, show therapy suggestions and all assessments
                	try {
                        String factName = fact.getName();
                        switch(factName) {
                            case "MAIN::anxiety-assessment":
                                String anxietyRec = fact.getSlotValue("recommendation").stringValue(null);
                                if (anxietyRec != null && !anxietyRec.isEmpty()) {
                                    suggestions.add("Anxiety Assessment: " + anxietyRec);
                                }
                                break;
                                
                            case "MAIN::depression-assessment":
                                String depressionRec = fact.getSlotValue("recommendation").stringValue(null);
                                if (depressionRec != null && !depressionRec.isEmpty()) {
                                    suggestions.add("Depression Assessment: " + depressionRec);
                                }
                                break;
                                
                            case "MAIN::bipolar-assessment":
                                String bipolarRec = fact.getSlotValue("recommendation").stringValue(null);
                                if (bipolarRec != null && !bipolarRec.isEmpty()) {
                                    suggestions.add("Bipolar Assessment: " + bipolarRec);
                                }
                                break;
                                
                            case "MAIN::sad-assessment":
                                String sadRec = fact.getSlotValue("recommendation").stringValue(null);
                                if (sadRec != null && !sadRec.isEmpty()) {
                                    suggestions.add("Seasonal Affective Disorder Assessment: " + sadRec);
                                }
                                break;
                                
                            case "MAIN::eating-disorder-assessment":
                                String edRec = fact.getSlotValue("recommendation").stringValue(null);
                                String eat_patternType = fact.getSlotValue("pattern-type").stringValue(null);
                                if (edRec != null && !edRec.isEmpty()) {
                                    suggestions.add("Eating Pattern Assessment (" + eat_patternType + "): " + edRec);
                                }
                                break;
                                
                            case "MAIN::sleep-disorder-assessment":    // Add this new case
                                String sleepRec = fact.getSlotValue("recommendation").stringValue(null);
                                String sleep_patternType = fact.getSlotValue("pattern-type").stringValue(null);
                                if (sleepRec != null && !sleepRec.isEmpty()) {
                                    suggestions.add("Sleep Pattern Assessment (" + sleep_patternType + "): " + sleepRec);
                                }
                                break;
                                
                            case "MAIN::therapy-suggestion":
                                String condition = fact.getSlotValue("condition").stringValue(null);
                                String activityType = fact.getSlotValue("activity_type").stringValue(null);
                                String severity = fact.getSlotValue("severity").stringValue(null);
                                
                                // Format activity type to be more readable
                                activityType = activityType.replace("-", " ");
                                
                                // Create suggestion based on condition
                                String suggestion;
                                switch(condition) {
                                    case "anxiety":
                                        suggestion = String.format("Consider %s to help manage anxiety (Risk level: %s)", 
                                            activityType, severity);
                                        break;
                                    case "depression":
                                        suggestion = String.format("Try %s to improve mood and energy (Risk level: %s)", 
                                            activityType, severity);
                                        break;
                                    case "bipolar":
                                        suggestion = String.format("Explore %s to help stabilize emotions (Risk level: %s)", 
                                            activityType, severity);
                                        break;
                                    case "SAD":
                                        suggestion = String.format("Engage in %s to combat seasonal effects (Risk level: %s)", 
                                            activityType, severity);
                                        break;
                                    case "eating-disorder":
                                        suggestion = String.format("Practice %s to develop healthy coping mechanisms (Risk level: %s)", 
                                            activityType, severity);
                                        break;
                                    case "wellness":
                                        suggestion = String.format("Maintain wellbeing through regular %s", 
                                            activityType);
                                        break;
                                    default:
                                        suggestion = String.format("Try %s for mental health support", 
                                            activityType);
                                }
                                suggestions.add(suggestion);
                                break;
                        }
                    } catch (JessException e) {
                        System.out.println("Error reading fact data: " + e.getMessage());
                    }
                }
            }


            // Default suggestions remain the same
            if (suggestions.isEmpty()) {
                switch (timeframe.toLowerCase()) {
                    case "daily":
                        suggestions.add("No data for now No data for now No data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for now");
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personalized suggestions");
                        suggestions.add("No data for now No data for now No data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for nowNo data for now");
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personalized suggestions");
                     
                        break;
                    case "weekly":
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personalized suggestions");
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personalized suggestions");
                        break;
                    case "monthly":
                        suggestions.add("No data for now");
                        suggestions.add("Press Begin");
                        suggestions.add("and get personalized suggestions");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
    
    
    
    
//	
//
//	private JPanel createGraphPanel2(String timeframe, List<List<Integer>> scores, List<Integer> hours) {
//	    JPanel graphPanel = new JPanel();
//	    graphPanel.setLayout(null); // Absolute positioning
//	    graphPanel.setOpaque(true);
//
//	    // Chart Area with Custom Painting
//	    JPanel chartArea = new JPanel() {
//	        @Override
//	        protected void paintComponent(Graphics g) {
//	            super.paintComponent(g);
//	            Graphics2D g2 = (Graphics2D) g;
//	            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//
//	            String[] emotions = {"Happy", "Sad", "Angry", "Scared", "Confused"};
//	            Color[] colors = {joyColor, sadnessColor, angerColor, scaredColor, confusedColor};
//
//	            int width = getWidth();
//	            int height = getHeight() - 50; // Leave space for labels
//	            int margin = 40;
//	            int graphWidth = width - 2 * margin;
//	            int graphHeight = height - 2 * margin;
//
//	            // Draw Axes
//	            g2.setColor(Color.BLACK);
//	            g2.drawLine(margin, height - margin, margin, margin); // Y-axis
//	            g2.drawLine(margin, height - margin, width - margin, height - margin); // X-axis
//
//	            if (scores.isEmpty() || hours.isEmpty()) {
//	                g2.setColor(Color.BLACK);
//	                g2.drawString("No data available", width / 2 - 50, height / 2);
//	                return;
//	            }
//
//	            // Plot Points and Draw Lines
//	            for (int i = 0; i < scores.size(); i++) {
//	                g2.setColor(colors[i]);
//	                List<Integer> emotionScores = scores.get(i);
//
//	                for (int j = 0; j < hours.size(); j++) {
//	                    int x = margin + (j * graphWidth) / (Math.max(1, hours.size() - 1));
//	                    int y = height - margin - (emotionScores.get(j) * graphHeight) / 100;
//	                    g2.fillOval(x - 3, y - 3, 6, 6); // Draw point
//
//	                    if (j < hours.size() - 1) {
//	                        int nextX = margin + ((j + 1) * graphWidth) / (Math.max(1, hours.size() - 1));
//	                        int nextY = height - margin - (emotionScores.get(j + 1) * graphHeight) / 100;
//	                        g2.drawLine(x, y, nextX, nextY); // Draw line to next point
//	                    }
//	                }
//	            }
//
//	            // Draw Labels for Time and Emotions
//	            g2.setColor(Color.BLACK);
//	            g2.setFont(new Font(customFont, Font.PLAIN, 10));
//
//	            for (int j = 0; j < hours.size(); j++) {
//	                int x = margin;
//	                if (hours.size() > 1) {
//	                    x = margin + (j * graphWidth) / (hours.size() - 1);
//	                }
//
//	                String label;
//	                if (timeframe.equals("weekly") || timeframe.equals("monthly")) {
//	                    int rawHour = hours.get(j);
//	                    label = String.valueOf(rawHour);
//	                } else {
//	                    int rawHour = hours.get(j);
//	                    label = String.format("%02d:%02d", rawHour / 100, rawHour % 100);
//	                }
//
//	                g2.drawString(label, x - 10, height - margin + 20);
//	            }
//
//	            // Draw Emotion Labels Below the Chart
//	            int labelY = height - margin + 40;
//	            int labelXStart = margin;
//	            int labelSpacing = (graphWidth - (emotions.length * 50)) / Math.max(1, emotions.length - 1);
//
//	            for (int i = 0; i < emotions.length; i++) {
//	                int labelX = labelXStart + i * (50 + labelSpacing);
//	                g2.setColor(colors[i]);
//	                g2.drawString(emotions[i], labelX, labelY);
//	            }
//	        }
//	    };
//
//	    // Chart Area Bounds
//	    chartArea.setBounds(0, 10, FRAME_WIDTH - 80, CHART_HEIGHT);
//	    chartArea.setOpaque(false);
//	    
////	    chartArea.setBackground(Color.green);
//	    graphPanel.add(chartArea);
//
//	    // Suggestions Panel
//	    JPanel suggestionsPanel = new JPanel();
//	    suggestionsPanel.setLayout(new BoxLayout(suggestionsPanel, BoxLayout.Y_AXIS));
//	    suggestionsPanel.setOpaque(false);
//	    
////	    suggestionsPanel.setBounds(10, CHART_HEIGHT + 20, FRAME_WIDTH - 80, SUG_HEIGHT);
//	    suggestionsPanel.setBounds(0, CHART_HEIGHT + 20, FRAME_WIDTH - 80, SUG_HEIGHT + 200);
//
//	    JLabel suggestionsTitle = new JLabel("Suggestions");
//	    suggestionsTitle.setFont(new Font(customFont, Font.BOLD, 14));
////	    suggestionsPanel.setOpaque(false);
//	    suggestionsPanel.add(suggestionsTitle);
//
////	    suggestionsPanel.add(Box.createVerticalStrut(10));
//
//
//	    
//	    String suggestionString = "<html><body style='width: 230px;'>";
//
//	    String[] suggestions = generateSuggestions(timeframe);
//	    for (String suggestion : suggestions) {
//	    	suggestionString+= "<li>" + suggestion + "</li>";
//	    	
//	    }
//	    suggestionString += "</body></html>";
//	    JLabel suggestionLabel = new JLabel(suggestionString);
//	    suggestionLabel.setFont(new Font(customFont, Font.PLAIN, 12));
//	    
//	    suggestionsPanel.add(suggestionLabel);
//	    
//	    suggestionsPanel.setOpaque(false);
//
//	    graphPanel.add(suggestionsPanel);
//	    graphPanel.setBounds(0,0,FRAME_WIDTH - 80, CHART_HEIGHT + SUG_HEIGHT + 60);
//	    
//	    graphPanel.revalidate();
//	    graphPanel.repaint();
//
//	    return graphPanel;
//	}
//
//    
}
