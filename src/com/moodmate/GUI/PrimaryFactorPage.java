package com.moodmate.GUI;

import javax.swing.*;
import javax.swing.event.*;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import jess.Fact;
import jess.JessException;
import jess.Rete;

import java.awt.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrimaryFactorPage extends BaseHomePage {
    private static final int PADDING_X = 40;
    private static final int FIELD_HEIGHT = 30;
    private static final int MARGIN = 10;
    private static final int CORNER_RADIUS = 25;
    private static final int userId = GlobalVariable.userId;
    private JRadioButton yesButton, noButton;
    private final Map<Integer, ButtonGroup> questionGroups = new HashMap<>();

    public PrimaryFactorPage() {
        super();

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null);

        int currentY = 20;

        // Title
        JLabel titleLabel = new JLabel("Let's Discover together", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN;

        // Trigger question
        initializeTriggerQuestion(contentPanel, currentY);
        currentY += FIELD_HEIGHT * 2 + MARGIN * 2;

        // Self-image title
        JLabel selfImageTitle = new JLabel("Let me know how you feel about yourself", SwingConstants.CENTER);
        selfImageTitle.setFont(new Font(customFont, Font.BOLD, 18));
        selfImageTitle.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(selfImageTitle);

        currentY += FIELD_HEIGHT + MARGIN;

        // Questions array
        String[] questions = {
            "1. On the whole, I am satisfied with myself.",
            "2. At times I think I am no good at all.",
            "3. I feel that I have a number of good qualities.",
            "4. I am able to do things as well as most other people.",
            "5. I feel I do not have much to be proud of.",
            "6. I certainly feel useless at times.",
            "7. I feel that I'm a person of worth.",
            "8. I wish I could have more respect for myself.",
            "9. All in all, I am inclined to think that I am a failure.",
            "10. I take a positive attitude toward myself."
        };

        // Add questions with ratings
        for (int i = 0; i < questions.length; i++) {
            addQuestionWithRatings(contentPanel, currentY, questions[i], i + 1);
            currentY += FIELD_HEIGHT * 4 + MARGIN * 2;
        }

        // Next Button
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 10);
        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true));
        nextButton.setBackground(customGreen);
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        nextButton.addActionListener(e -> {
            if (!validateAnswers()) {
                JOptionPane.showMessageDialog(this, 
                    "Please answer all questions before proceeding.",
                    "Incomplete Form",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Initialize Jess engine
            	Rete engine = ReteEngineManager.getInstance();

                // Assert trigger status
                boolean hasTrigger = yesButton.isSelected();
                String triggerCommand = String.format(
                    "(assert (trigger-status (user_id %d) (has-trigger %s)))",
                    userId, hasTrigger
                );
                System.out.println("Asserting trigger: " + triggerCommand);
                engine.eval(triggerCommand);

                // Assert self-image answers
                for (int i = 1; i <= 10; i++) {
                    ButtonGroup group = questionGroups.get(i);
                    int answer = getAnswerValue(group);
                    String answerCommand = String.format(
                        "(assert (self-image-answer (user_id %d) (question_id %d) (answer %d)))",
                        userId, i, answer
                    );
                    System.out.println("Asserting answer: " + answerCommand);
                    engine.eval(answerCommand);
                }

                engine.run();
                // After engine.run();
             // Inside the nextButton action listener, after engine.run(), add this debugging code:
//                System.out.println("\n=== Debugging Facts ===");

                // Print all facts
//                Iterator<?> facts = engine.listFacts();
//                while (facts.hasNext()) {
//                    Fact fact = (Fact) facts.next();
//                    System.out.println("Fact name: " + fact.getName());
//                    try {
//                        // Print detailed information for specific facts
//                        if (fact.getName().equals("MAIN::recommendation") || 
//                            fact.getName().equals("recommendation")) {
//                            System.out.println("  user_id: " + fact.getSlotValue("user_id"));
//                            System.out.println("  message: " + fact.getSlotValue("message"));
//                        }
//                        if (fact.getName().equals("MAIN::rses-score") || 
//                            fact.getName().equals("rses-score")) {
//                            System.out.println("  user_id: " + fact.getSlotValue("user_id"));
//                            System.out.println("  score: " + fact.getSlotValue("score"));
//                            System.out.println("  level: " + fact.getSlotValue("level"));
//                        }
//                        if (fact.getName().equals("MAIN::trigger-status") || 
//                            fact.getName().equals("trigger-status")) {
//                            System.out.println("  user_id: " + fact.getSlotValue("user_id"));
//                            System.out.println("  has-trigger: " + fact.getSlotValue("has-trigger"));
//                        }
//                    } catch (JessException ex) {
//                        System.out.println("Error getting slot value: " + ex.getMessage());
//                    }
//                }


                String message = "<html><body style='width: 150px;'>" +
                        "In this app, we want to help you understand why you are experiencing this feeling.<br>" +
                        "Do you want to take a quick test?" +
                        "</body></html>";

                String[] options = {"OK", "Exit"};
                int choice = JOptionPane.showOptionDialog(this, message, "Self-help",
                    JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, options, options[0]);

                if (choice == 0) { // OK selected
                    try {
                        // Assert need-second-factors fact
                        String needFactors = String.format(
                            "(assert (need-second-factors (user_id %d) (need TRUE)))",
                            userId
                        );
                        System.out.println("Asserting need for second factors: " + needFactors);
                        engine.eval(needFactors);
                        engine.run();

                        // Then proceed to next page
                        addToNavigationStack();
                        new SleepPage();
                        dispose();
                    } catch (JessException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(this,
                            "Error processing second factors need: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else if (choice == 1) { // Exit selected
                    new HomePage();
                    dispose();
                }

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error processing answers: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        contentPanel.add(nextButton);
        currentY += FIELD_HEIGHT + MARGIN;

        contentPanel.setPreferredSize(new Dimension(contentArea.getWidth(), currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

    private void initializeTriggerQuestion(JPanel contentPanel, int currentY) {
        JLabel triggerQuestionLabel = new JLabel("Did anything happen that triggered you?", SwingConstants.LEFT);
        triggerQuestionLabel.setFont(new Font(customFont, Font.PLAIN, 16));
        triggerQuestionLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(triggerQuestionLabel);

        currentY += FIELD_HEIGHT + MARGIN;

        yesButton = new JRadioButton("Yes");
        noButton = new JRadioButton("No");
        yesButton.setBounds(PADDING_X, currentY, 60, FIELD_HEIGHT);
        noButton.setBounds(PADDING_X + 80, currentY, 60, FIELD_HEIGHT);

        ButtonGroup triggerGroup = new ButtonGroup();
        triggerGroup.add(yesButton);
        triggerGroup.add(noButton);

        contentPanel.add(yesButton);
        contentPanel.add(noButton);
    }

    private void addQuestionWithRatings(JPanel contentPanel, int currentY, String question, int questionId) {
        JLabel questionLabel = new JLabel("<html>" + question + "</html>", SwingConstants.LEFT);
        questionLabel.setFont(new Font(customFont, Font.PLAIN, 14));
        questionLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT * 2);
        contentPanel.add(questionLabel);

        currentY += FIELD_HEIGHT * 2 + MARGIN;

        JPanel ratingPanel = new JPanel();
        ratingPanel.setLayout(new GridLayout(2, 2, 10, 5));
        ratingPanel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT * 2);

        String[] ratings = {"Strongly Agree", "Disagree", "Agree", "Strongly Disagree"};
        ButtonGroup ratingGroup = new ButtonGroup();
        for (String rating : ratings) {
            JRadioButton ratingButton = new JRadioButton(rating);
            ratingButton.setFont(new Font(customFont, Font.PLAIN, 12));
            ratingGroup.add(ratingButton);
            ratingPanel.add(ratingButton);
        }

        questionGroups.put(questionId, ratingGroup);
        contentPanel.add(ratingPanel);
    }

    private boolean validateAnswers() {
        if (!yesButton.isSelected() && !noButton.isSelected()) {
            return false;
        }
        
        for (ButtonGroup group : questionGroups.values()) {
            if (group.getSelection() == null) {
                return false;
            }
        }
        return true;
    }

    private int getAnswerValue(ButtonGroup group) {
        ButtonModel selection = group.getSelection();
        if (selection == null) return 0;
        
        JRadioButton selectedButton = null;
        for (Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected()) {
                selectedButton = button;
                break;
            }
        }
        
        if (selectedButton == null) return 0;
        
        switch (selectedButton.getText()) {
            case "Strongly Agree": return 4;
            case "Agree": return 3;
            case "Disagree": return 2;
            case "Strongly Disagree": return 1;
            default: return 0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PrimaryFactorPage::new);
    }
}