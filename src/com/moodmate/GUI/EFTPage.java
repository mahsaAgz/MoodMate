package com.moodmate.GUI;

import javax.swing.*;
import javax.swing.event.*;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import jess.Fact;
import jess.JessException;
import jess.Rete;

import java.awt.*;
import java.util.Hashtable;
import java.util.Iterator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class EFTPage extends BaseHomePage {

    private static final int PADDING_X = 40; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for input fields
    private static final int MARGIN = 5; // Vertical margin between components
    private static final int BOX_HEIGHT = 250; // Height for each emotion box
    private static final int CORNER_RADIUS = 25; // Rounded corner radius

    private Hashtable<String, String[]> emotionAdj;
    private final Hashtable<String, JSlider> emotionSliders = new Hashtable<>();
    private static final int userId = GlobalVariable.userId; // You might want to pass this from previous pages
    private static int day;
    private static int hour;
    
    public EFTPage() {
        super();
        LocalDateTime now = LocalDateTime.now();
        day = Integer.parseInt(now.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        hour = Integer.parseInt(now.format(DateTimeFormatter.ofPattern("HHmm")));
        
        System.out.println("Current Date: " + day); // e.g., 20241206
        System.out.println("Current Time: " + hour); // e.g., 1016
    

        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentArea.add(backgroundLabel);
        
        // Define emotions and adjectives
        emotionAdj = new Hashtable<String, String[]> ();
        emotionAdj.put("Happy", new String[]{"Loved", "Cheerful", "Joyful", "Optimistic", "Content", "Relaxed", "Proud", "Thrilled", "Grateful", "Elated", "Peaceful", "Jubilant", "Satisfied", "Delighted", "Energized"});
        emotionAdj.put("Sad", new String[]{"Lonely", "Miserable", "Heartbroken", "Hopeless", "Ashamed", "Regretful", "Disappointed", "Rejected", "Depressed", "Crushed", "Sorrowful", "Exhausted", "Isolated", "Grieving", "Forsaken"});
        emotionAdj.put("Angry", new String[]{"Betrayed", "Furious", "Frustrated", "Irritated", "Resentful", "Enraged", "Outraged", "Humiliated", "Mad", "Agitated", "Annoyed", "Hostile", "Defensive", "Bitter", "Indignant"});
        emotionAdj.put("Scared", new String[]{"Nervous", "Anxious", "Fearful", "Timid", "Alarmed", "Intimidated", "Terrified", "Apprehensive", "Shaken", "Shocked", "Suspicious", "Uneasy", "Overwhelmed", "Threatened", "Vulnerable"});
        emotionAdj.put("Confused", new String[]{"Baffled", "Perplexed", "Puzzled", "Hesitant", "Flustered", "Awkward", "Distracted", "Misunderstood", "Torn", "Doubtful", "Uncertain", "Unsettled", "Bewildered", "Lost", "Stunned"});

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning

        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel("Tell me how you feel now", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN;

        // Add emotion boxes dynamically
        for (String emotion : emotionAdj.keySet()) {
            currentY = addEmotionBox(contentPanel, currentY, emotion, emotionAdj.get(emotion));
        }

        currentY += MARGIN;

        // Next Button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        nextButton.setBackground(customGreen); // Custom green color
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

     // Modify the nextButton action listener
        nextButton.addActionListener(e -> {
            try {
                // Initialize Jess engine
            	 Rete engine = ReteEngineManager.getInstance();

                // Assert each emotion's intensity
                for (String emotion : emotionAdj.keySet()) {
                    JSlider slider = emotionSliders.get(emotion.toLowerCase());
                    String assertCommand = String.format(
                        "(assert (user-emotion (user_id %d) (day %d) (hour %d) " +
                        "(emotion-name \"%s\") (intensity %d)))",
                        userId, day, hour, emotion.toLowerCase(), slider.getValue()
                    );
                    System.out.println("Asserting: " + assertCommand); // Debug print
                    engine.eval(assertCommand);
                }

                // Run the rules
                engine.run();

                // Print normalized emotions from Jess (optional)
                Iterator<?> facts = engine.listFacts();
                while (facts.hasNext()) {
                    Fact fact = (Fact) facts.next();
                    if (fact.getName().equals("MAIN::normalized-emotion")) {
                        System.out.println("Normalized emotion: " + 
                            fact.getSlotValue("emotion-name") + " = " +
                            fact.getSlotValue("percentage") + "%");
                    }
                }

                // Navigate to next page
                addToNavigationStack();
                new PrimaryFactorPage();
                dispose();

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing emotions: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        contentPanel.add(nextButton);
        currentY += FIELD_HEIGHT + MARGIN;

        contentPanel.setPreferredSize(new Dimension(contentArea.getWidth(), currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

    private int addEmotionBox(JPanel contentPanel, int currentY, String emotion, String[] adjectives) {
        int AdjHeight = 100;

        // Create a rounded JPanel
        RoundedPanel emotionBox = new RoundedPanel(CORNER_RADIUS, Color.white);
        emotionBox.setLayout(null);
        emotionBox.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, BOX_HEIGHT);

        // Internal Y position for components within the RoundedPanel
        int innerY = 20;

        // Emotion Title
        JLabel emotionLabel = new JLabel(emotion, SwingConstants.LEFT);
        emotionLabel.setFont(new Font(customFont, Font.BOLD, 16));
        emotionLabel.setBounds(20, innerY, emotionBox.getWidth() - PADDING_X, FIELD_HEIGHT);
        emotionBox.add(emotionLabel);
        innerY += FIELD_HEIGHT + MARGIN;

        // Emotion Description
        StringBuilder adjectivesText = new StringBuilder("Adjectives that describe this emotion:\n ");
        
        for (String adj : adjectives) {
            adjectivesText.append(adj).append(", ");
        }
        JLabel descriptionLabel = new JLabel("<html>" + adjectivesText.substring(0, adjectivesText.length() - 2) + "</html>");
        descriptionLabel.setFont(new Font(customFont, Font.PLAIN, 12));
        descriptionLabel.setBounds(20, innerY, emotionBox.getWidth() - 40, AdjHeight);
        emotionBox.add(descriptionLabel);
        innerY += AdjHeight + MARGIN;

        // Intensity Slider
        JSlider intensitySlider = new JSlider(0, 100, 50); // Default intensity 50
        intensitySlider.setBounds(20, innerY, emotionBox.getWidth() - 40, FIELD_HEIGHT + 20);
        intensitySlider.setPaintTicks(true);
        intensitySlider.setPaintLabels(true);
        
        // Store the slider reference
        emotionSliders.put(emotion.toLowerCase(), intensitySlider);
        
        // Custom labels for slider
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel("Low"));
        labels.put(100, new JLabel("High"));
        intensitySlider.setLabelTable(labels);
        emotionBox.add(intensitySlider);

        // Add the RoundedPanel to the content panel
        contentPanel.add(emotionBox);

        // Update the global currentY to account for the height of the RoundedPanel and margin
        currentY += BOX_HEIGHT + MARGIN;
        
        return currentY;
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(EFTPage::new);
    }

    // Custom Rounded Panel Class
    private static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;

        public RoundedPanel(int cornerRadius, Color backgroundColor) {
            this.cornerRadius = cornerRadius;
            this.backgroundColor = backgroundColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }
}
