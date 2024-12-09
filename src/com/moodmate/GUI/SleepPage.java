package com.moodmate.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import jess.JessException;
import jess.Rete;

import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SleepPage extends BaseHomePage {

    private static final int PADDING_X = 40; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for input fields
    private static final int MARGIN = 10; // Vertical margin between components
    private static final int userId = GlobalVariable.userId;
    
    public SleepPage() {
        super();
        Rete engine = ReteEngineManager.getInstance();
        
        // Set the background of the page to an image
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning

        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel("Let's Discover Together", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN * 2;

        // Ask if the user is sleepy
        JLabel sleepyLabel = new JLabel("Are you sleepy?", SwingConstants.CENTER);
        sleepyLabel.setFont(new Font(customFont, Font.BOLD, 18));
        sleepyLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(sleepyLabel);

        currentY += FIELD_HEIGHT + MARGIN;
        int buttonWidth = 100;
        int buttonHeight = 100;
        int gap = 20; // Gap between buttons
        int totalWidth = (2 * buttonWidth) + gap;
        int startX = (contentArea.getWidth() - totalWidth) / 2;
        
        // Create custom panels to wrap radio buttons
        JPanel yesPanel = new JPanel(new BorderLayout());
        JPanel noPanel = new JPanel(new BorderLayout());
        yesPanel.setBackground(Color.WHITE);
        noPanel.setBackground(Color.WHITE);
        
        // Radio buttons with pictures
        JRadioButton yesButton = new JRadioButton(new ImageIcon("assets/images/sleep_yes.png"));
        JRadioButton noButton = new JRadioButton(new ImageIcon("assets/images/sleep_no.png"));
     // Add buttons to panels
        yesPanel.add(yesButton, BorderLayout.CENTER);
        noPanel.add(noButton, BorderLayout.CENTER);
        
        // Set initial borders
        yesPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));
        noPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));

        ButtonGroup sleepyGroup = new ButtonGroup();
        
        sleepyGroup.add(yesButton);
        sleepyGroup.add(noButton);
        
        yesButton.setBounds(startX, currentY, buttonWidth, buttonHeight);
        noButton.setBounds(startX + buttonWidth + gap, currentY, buttonWidth, buttonHeight);
        yesPanel.setBounds(startX, currentY, buttonWidth, buttonHeight);
        noPanel.setBounds(startX + buttonWidth + gap, currentY, buttonWidth, buttonHeight);

        // Add selection effect listeners
        yesButton.addItemListener(e -> {
            if (yesButton.isSelected()) {
                yesPanel.setBorder(BorderFactory.createLineBorder(customGreen, 3, true));
                yesPanel.setBackground(new Color(232, 245, 233));  // Light green background
            } else {
                yesPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));
                yesPanel.setBackground(Color.WHITE);
            }
        });

        noButton.addItemListener(e -> {
            if (noButton.isSelected()) {
                noPanel.setBorder(BorderFactory.createLineBorder(customGreen, 3, true));
                noPanel.setBackground(new Color(232, 245, 233));  // Light green background
            } else {
                noPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true));
                noPanel.setBackground(Color.WHITE);
            }
        });

//        contentPanel.add(yesButton);
//        contentPanel.add(noButton);
        contentPanel.add(yesPanel);
        contentPanel.add(noPanel);
        currentY += 100 + MARGIN;

        // Panel for additional questions if "Yes" is selected
        JPanel sleepDetailsPanel = new JPanel();
        sleepDetailsPanel.setLayout(new GridLayout(6, 1, MARGIN, MARGIN)); // Padding and layout
        int panelPadding = 10;
        sleepDetailsPanel.setBorder(new EmptyBorder(panelPadding, panelPadding, panelPadding, panelPadding)); // Padding around the panel
        sleepDetailsPanel.setVisible(false); // Initially hidden
        sleepDetailsPanel.setBounds(
            PADDING_X,
            currentY,
            contentArea.getWidth() - 2 * PADDING_X,
            FIELD_HEIGHT * 10
        );

        // Sleep quality question
        JLabel qualityLabel = new JLabel("Rate the quality of your sleep:", SwingConstants.LEFT);
        qualityLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        sleepDetailsPanel.add(qualityLabel);

        // Radio buttons for sleep quality with text labels
        JRadioButton veryGoodButton = new JRadioButton("Very Good");
        JRadioButton fairlyGoodButton = new JRadioButton("Fairly Good");
//        JRadioButton needsImprovementButton = new JRadioButton("Needs Improvement");
        JRadioButton needsImprovementButton = new JRadioButton("Could be better");
        JRadioButton poorButton = new JRadioButton("Poor");

        ButtonGroup qualityGroup = new ButtonGroup();
        qualityGroup.add(veryGoodButton);
        qualityGroup.add(fairlyGoodButton);
        qualityGroup.add(needsImprovementButton);
        qualityGroup.add(poorButton);

        JPanel qualityPanel = new JPanel(new GridLayout(2, 2, MARGIN, MARGIN));
        qualityPanel.add(veryGoodButton);
        qualityPanel.add(fairlyGoodButton);
        qualityPanel.add(needsImprovementButton);
        qualityPanel.add(poorButton);

        sleepDetailsPanel.add(qualityPanel);

        // Sleep and wake time inputs
        JLabel sleepTimeLabel = new JLabel("Time you went to sleep:");
        sleepTimeLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        sleepDetailsPanel.add(sleepTimeLabel);

        JSpinner sleepTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor sleepEditor = new JSpinner.DateEditor(sleepTimeSpinner, "hh:mm a");
        sleepTimeSpinner.setEditor(sleepEditor);
        sleepDetailsPanel.add(sleepTimeSpinner);

        JLabel wakeTimeLabel = new JLabel("Time you woke up:");
        wakeTimeLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        sleepDetailsPanel.add(wakeTimeLabel);

        JSpinner wakeTimeSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor wakeEditor = new JSpinner.DateEditor(wakeTimeSpinner, "hh:mm a");
        wakeTimeSpinner.setEditor(wakeEditor);
        sleepDetailsPanel.add(wakeTimeSpinner);

        contentPanel.add(sleepDetailsPanel);

        // Listener for "Yes" button to show details panel
        yesButton.addActionListener(e -> {
            try {
                // Assert sleepiness fact
                String sleepinessCommand = String.format(
                    "(assert (sleepiness (user_id %d) (sleepy TRUE)))",
                    userId
                );
                System.out.println("Asserting sleepiness: " + sleepinessCommand);
                engine.eval(sleepinessCommand);
                engine.run();

                // Show the details panel
                sleepDetailsPanel.setVisible(true);

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing sleepiness status: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        noButton.addActionListener(e -> {
            try {
                String sleepinessCommand = String.format(
                    "(assert (sleepiness (user_id %d) (sleepy FALSE)))",
                    userId
                );
                System.out.println("Asserting sleepiness: " + sleepinessCommand);
                engine.eval(sleepinessCommand);
                engine.run();

                // Hide the details panel
                sleepDetailsPanel.setVisible(false);

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing sleepiness status: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        currentY += sleepDetailsPanel.getHeight() + MARGIN;

        // Next Button
        JButton nextButton = new JButton("Next");

        nextButton.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        nextButton.setBackground(customGreen); // Custom green color
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        nextButton.addActionListener(e -> {
            try {
                // Check if sleep option is selected
                if (!yesButton.isSelected() && !noButton.isSelected()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please indicate if you are sleepy.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                // If yes is selected, validate sleep details
                if (yesButton.isSelected()) {
                    if (!veryGoodButton.isSelected() && !fairlyGoodButton.isSelected() && 
                        !needsImprovementButton.isSelected() && !poorButton.isSelected()) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Please rate your sleep quality.",
                            "Input Required",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }
                }

                if (yesButton.isSelected()) {
                    // Get satisfaction level
                    int satisfaction;
                    if (veryGoodButton.isSelected()) satisfaction = 3;
                    else if (fairlyGoodButton.isSelected()) satisfaction = 2;
                    else if (needsImprovementButton.isSelected()) satisfaction = 1;
                    else satisfaction = 0;

                    // Get times from spinners
                    Date sleepTime = (Date) sleepTimeSpinner.getValue();
                    Date wakeTime = (Date) wakeTimeSpinner.getValue();

                    // Format times as strings (HH:mm)
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    String sleepTimeStr = sdf.format(sleepTime);
                    String wakeTimeStr = sdf.format(wakeTime);

                    // Convert times to decimal
                    double sleepDecimal = convertTimeToDecimal(sleepTime);
                    double wakeDecimal = convertTimeToDecimal(wakeTime);

                    // Assert sleep quality fact
                    String sleepQualityCommand = String.format(
                        "(assert (sleep-quality " +
                        "(user_id %d) " +
                        "(satisfaction %d) " +
                        "(sleep-time \"%s\") " +
                        "(wake-time \"%s\") " +
                        "(sleep-decimal %.2f) " +
                        "(wake-decimal %.2f)))",
                        userId, satisfaction, sleepTimeStr, wakeTimeStr, 
                        sleepDecimal, wakeDecimal
                    );

                    System.out.println("Asserting sleep quality: " + sleepQualityCommand);
                    engine.eval(sleepQualityCommand);
                    engine.run();
                }

                // Continue to next page
                addToNavigationStack();
                new PhysicalActivityPage();
                dispose();

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing sleep data: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        

        contentPanel.add(nextButton);

        contentPanel.setPreferredSize(new Dimension(contentArea.getWidth(), currentY + FIELD_HEIGHT + 40));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }
    // Add helper method to convert time to decimal
    private double convertTimeToDecimal(Date time) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        return hours + (minutes / 60.0);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SleepPage::new);
    }
}
