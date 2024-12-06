package com.moodmate.GUI;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import jess.JessException;
import jess.Rete;

import java.awt.*;

public class PhysicalActivityPage extends BaseHomePage {

    private static final int PADDING_X = 40; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for input fields
    private static final int MARGIN = 10; // Vertical margin between components
    private static final int userId = GlobalVariable.userId;

    
    public PhysicalActivityPage() {
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

        // Ask if the user engaged in physical activity
        JLabel activityLabel = new JLabel("Did you engage in physical activity?", SwingConstants.CENTER);
        activityLabel.setFont(new Font(customFont, Font.BOLD, 18));
        activityLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(activityLabel);

        currentY += FIELD_HEIGHT + MARGIN;
        int buttonWidth = 100;
        int buttonHeight = 100;
        int gap = 20; // Gap between buttons
        int totalWidth = (2 * buttonWidth) + gap;
        int startX = (contentArea.getWidth() - totalWidth) / 2;

        // Radio buttons with pictures
        JRadioButton yesButton = new JRadioButton(new ImageIcon("assets/images/physical_yes.png"));
        JRadioButton noButton = new JRadioButton(new ImageIcon("assets/images/physical_no.png"));

        ButtonGroup activityGroup = new ButtonGroup();
        activityGroup.add(yesButton);
        activityGroup.add(noButton);

        yesButton.setBounds(startX, currentY, buttonWidth, buttonHeight);
        noButton.setBounds(startX + buttonWidth + gap, currentY, buttonWidth, buttonHeight);

        contentPanel.add(yesButton);
        contentPanel.add(noButton);

        currentY += buttonHeight + MARGIN;

        // Panel for additional details if "Yes" is selected
        JPanel activityDetailsPanel = new JPanel();
        activityDetailsPanel.setLayout(new GridLayout(6, 1, MARGIN, MARGIN)); // Padding and layout
        int panelPadding = 10;
        activityDetailsPanel.setBorder(new EmptyBorder(panelPadding, panelPadding, panelPadding, panelPadding)); // Padding around the panel
        activityDetailsPanel.setVisible(false); // Initially hidden
        activityDetailsPanel.setBounds(
            PADDING_X,
            currentY,
            contentArea.getWidth() - 2 * PADDING_X,
            FIELD_HEIGHT * 6
        );

        // Duration of activity
        JLabel durationLabel = new JLabel("How long did you exercise (in minutes)?", SwingConstants.LEFT);
        durationLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        activityDetailsPanel.add(durationLabel);

        JSpinner durationSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1440, 1)); // Spinner for duration
        activityDetailsPanel.add(durationSpinner);

        // Intensity level question
        JLabel intensityLabel = new JLabel("Intensity of activity:", SwingConstants.LEFT);
        intensityLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        activityDetailsPanel.add(intensityLabel);

        JRadioButton lightIntensityButton = new JRadioButton("Light");
        JRadioButton moderateIntensityButton = new JRadioButton("Moderate");
        JRadioButton highIntensityButton = new JRadioButton("High");

        ButtonGroup intensityGroup = new ButtonGroup();
        intensityGroup.add(lightIntensityButton);
        intensityGroup.add(moderateIntensityButton);
        intensityGroup.add(highIntensityButton);

        JPanel intensityPanel = new JPanel(new GridLayout(1, 3, MARGIN, MARGIN));
        intensityPanel.add(lightIntensityButton);
        intensityPanel.add(moderateIntensityButton);
        intensityPanel.add(highIntensityButton);

        activityDetailsPanel.add(intensityPanel);

        contentPanel.add(activityDetailsPanel);

        // Listener for "Yes" button to show details panel
        yesButton.addActionListener(e -> {
            try {
                // Assert activity fact
                String activityCommand = String.format(
                    "(assert (activity (user_id %d) (has-activity TRUE)))",
                    userId
                );
                System.out.println("Asserting activity: " + activityCommand);
                engine.eval(activityCommand);
                engine.run();

                // Show details panel
                activityDetailsPanel.setVisible(true);

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing activity status: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });
        noButton.addActionListener(e -> {
            try {
                // Assert activity fact with FALSE
                String activityCommand = String.format(
                    "(assert (activity (user_id %d) (has-activity FALSE)))",
                    userId
                );
                System.out.println("Asserting activity: " + activityCommand);
                engine.eval(activityCommand);
                engine.run();

                // Hide details panel
                activityDetailsPanel.setVisible(false);

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing activity status: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
            }
        });

        currentY += activityDetailsPanel.getHeight() + MARGIN;

        // Next Button
        JButton nextButton = new JButton("Next");

        nextButton.setBounds(PADDING_X, currentY + 100, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 1, true));
        nextButton.setBackground(customGreen); // Custom green color
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        nextButton.addActionListener(e -> {
            try {
                // Validate input
                if (!yesButton.isSelected() && !noButton.isSelected()) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Please indicate if you engaged in physical activity.",
                        "Input Required",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if (yesButton.isSelected()) {
                    if (!lightIntensityButton.isSelected() && 
                        !moderateIntensityButton.isSelected() && 
                        !highIntensityButton.isSelected()) {
                        JOptionPane.showMessageDialog(
                            this,
                            "Please select activity intensity level.",
                            "Input Required",
                            JOptionPane.WARNING_MESSAGE
                        );
                        return;
                    }

                    // Get intensity level
                    String intensity;
                    if (lightIntensityButton.isSelected()) intensity = "light";
                    else if (moderateIntensityButton.isSelected()) intensity = "moderate";
                    else intensity = "high";

                    // Get duration
                    int duration = (Integer) durationSpinner.getValue();

                    // Assert physical activity fact
                    String physicalActivityCommand = String.format(
                        "(assert (physical-activity " +
                        "(user_id %d) " +
                        "(has-activity TRUE) " +
                        "(duration %d) " +
                        "(intensity \"%s\")))",
                        userId, duration, intensity
                    );

                    System.out.println("Asserting physical activity: " + physicalActivityCommand);
                    engine.eval(physicalActivityCommand);
                    engine.run();
                }

                // Continue to next page
                addToNavigationStack();
                new FoodPage();
                dispose();

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error processing physical activity data: " + ex.getMessage(),
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PhysicalActivityPage::new);
    }
}
