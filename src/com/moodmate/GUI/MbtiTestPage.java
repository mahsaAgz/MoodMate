package com.moodmate.GUI;
import javax.swing.*;
import javax.swing.event.*;

import com.moodmate.GUI.SignInPage.GlobalVariable;

import jess.*;
import java.util.Iterator;
import jess.Fact;
import jess.Rete;

import java.awt.*;
import java.util.Hashtable;
import java.util.IllegalFormatConversionException;

public class MbtiTestPage extends BasePage {

    private static final int PADDING_X = 60; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields
    private static final int MARGIN = 20; // Vertical margin between components
    private JSlider[] mbtiSliders = new JSlider[8];
    private int userId;
    private String username;
    private int age;
    private int gender;

    
    int contentWidth= contentArea.getWidth();
    public MbtiTestPage(int userId, String username, int age, int gender) {
        super(); // Call BasePage setup
        this.userId = userId;
        this.username = username;
        this.age = age;
        this.gender = gender;
        System.out.println("Navigated to MbtiTestPage with User ID: " + userId);
        System.out.println("GlobalVariable.userId: " + GlobalVariable.userId);
        System.out.println("username: " + username);
        System.out.println("age: " + age);
        System.out.println("gender: " + gender);
        // Create a contentPanel for all components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning
        contentPanel.setBackground(customBackgroundColor);
        // Add background image (it's just a visual element, so we add it to contentPanel)
        JLabel backgroundLabel = new JLabel();
        backgroundLabel.setBounds(0, 0, contentWidth, contentArea.getHeight());
        contentPanel.add(backgroundLabel);
        backgroundLabel.setLayout(null);

        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel("Short MBTI Test", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN;


        // Create Sliders for MBTI preferences
        currentY = addMBTISlider(contentPanel, currentY, 
                "I feel energized when I'm in a group of people or social setting.", 0);
        currentY = addMBTISlider(contentPanel, currentY, 
                "I often seek external activities or discussions to recharge after a long day.", 1);
        currentY = addMBTISlider(contentPanel, currentY, 
                "I prefer concrete facts and practical details over abstract ideas.", 2);
        currentY = addMBTISlider(contentPanel, currentY, 
                "I trust my direct experiences more than my instincts or hunches.", 3);
        currentY = addMBTISlider(contentPanel, currentY, 
                "I make decisions based on logic and objective analysis rather than emotions.", 4);
        currentY = addMBTISlider(contentPanel, currentY,
                "When resolving conflicts, I focus more on fairness and rules than on harmony and relationships.", 5);
        currentY = addMBTISlider(contentPanel, currentY, 
                "I prefer planning and organization to flexibility and spontaneity.", 6);
        currentY = addMBTISlider(contentPanel, currentY, 
                "I feel more comfortable making decisions early rather than keeping options open.", 7);

        currentY += MARGIN;

        // next Button
        // next Button
        
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X, FIELD_HEIGHT + 10);

        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
//        signUpButton.setBackground(Color.decode("#45C78A"));
        nextButton.setBackground(customGreen);
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));    
        

        nextButton.addActionListener(e -> {
            try {
                // Initialize Jess engine
                Rete engine = new Rete();
                engine.reset();
                engine.batch("src/com/moodmate/logic/templates.clp");
                engine.batch("src/com/moodmate/logic/user_profile_rules.clp");
                // Assert profile input
                try {
                    String profileAssert = String.format(
                        "(assert (profile-input (user_id %d) (name \"%s\") (age %d) (gender %d) (mbti \"unknown\")))",
                        GlobalVariable.userId, // Ensure this is an integer
                        username, // Ensure this is a string
                        age, // Ensure this is an integer
                        gender // Ensure this is an integer
                    );
                    System.out.println("Profile Assert Command: " + profileAssert);
                    engine.eval(profileAssert);
                } catch (IllegalFormatConversionException ex) {
                    System.err.println("Error in String.format: " + ex.getMessage());
                    ex.printStackTrace();
                }
               
                // Assert mbti-answer facts using the exact format
                String[] dimensions = {"EI", "EI", "SN", "SN", "TF", "TF", "JP", "JP"};
                for (int i = 0; i < mbtiSliders.length; i++) {
                	String assertCommand = String.format(
                		    "(assert (mbti-answer (user_id %d) (dimension \"%s\") (question_id %d) (score %d)))",
                		    GlobalVariable.userId,      // First %d - user_id (integer)
                		    dimensions[i],             // %s - dimension (string)
                		    (i % 2) + 1,              // %d - question_id (integer)
                		    mbtiSliders[i].getValue()  // %d - score (integer)
                		);
                    engine.eval(assertCommand);
                    
                    // Debug print
                    System.out.println("Asserting: " + assertCommand);
                }

                // Run the rules
                engine.run();

                // Get the MBTI result
                String mbtiResult = "UNKNOWN";
                Iterator<?> facts = engine.listFacts();
                while (facts.hasNext()) {
                    Fact fact = (Fact) facts.next();
                    if (fact.getName().equals("MAIN::profile-input")) {
                        mbtiResult = fact.getSlotValue("mbti").stringValue(null);
                        break;
                    }
                }

                // Show results dialog
                String message = String.format(
                    "Here are your details:\nName: %s\nAge: %d\nGender: %s\nMBTI Type: %s",
                    username, age, gender, mbtiResult
                );

                String[] options = {"OK", "Edit"};
                int choice = JOptionPane.showOptionDialog(
                    this,
                    message,
                    "Your MBTI Result",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE,
                    null,
                    options,
                    options[0]
                );

                if (choice == 1) {
                    new UserProfilePage(username, String.valueOf(age), gender);
                    dispose();
                } else if (choice == 0) {
                    addToNavigationStack();
                    new HobbiesPage(username, age, gender, mbtiResult);
                    dispose();
                }

            } catch (JessException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(
                    this,
                    "Error calculating MBTI result: " + ex.getMessage(),
                    "Error",
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

    private int addMBTISlider(JPanel contentPanel, int currentY, String question, int index) {
    	
    	JLabel questionLabel1 = new JLabel("<html>" + question + "</html>");
    	questionLabel1.setHorizontalAlignment(SwingConstants.CENTER); // Center align text
    	questionLabel1.setBounds(PADDING_X, currentY, contentWidth - 2 * PADDING_X , 2 * FIELD_HEIGHT);
    	questionLabel1.setBorder(BorderFactory.createMatteBorder(
    		    1, 0, 0, 0, // Top border only
    		    new Color(200, 200, 200) // Very light grey color
    		));
    	contentPanel.add(questionLabel1);

    	currentY += 2 * FIELD_HEIGHT;

    	
        JSlider mbtiSlider = new JSlider(-5, 5, -5); 
        mbtiSliders[index] = mbtiSlider;
        mbtiSlider.setBounds(PADDING_X -20, currentY, 300, FIELD_HEIGHT * 2); 
//        mbtiSlider.setMajorTickSpacing(2);
//        mbtiSlider.setMinorTickSpacing(1);

        // Create label table with updated font size
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        Font customFont2 = new Font(customFont, Font.PLAIN, 9); // Customize the font and size

        JLabel labelStronglyDisagree = new JLabel("Strongly Disagree");
        labelStronglyDisagree.setFont(customFont2);
        labels.put(-5, labelStronglyDisagree);


        JLabel labelStronglyAgree = new JLabel("Strongly Agree");
        labelStronglyAgree.setFont(customFont2);
        labels.put(5, labelStronglyAgree);

        // Apply the label table
        mbtiSlider.setLabelTable(labels);  
        mbtiSlider.setPaintLabels(true);

        // Add the slider to the content panel
        contentPanel.add(mbtiSlider);



        currentY += FIELD_HEIGHT + MARGIN*2;

        return currentY;
    }

    public static void main(String[] args) {
        new MbtiTestPage(0,"test",0,0);
    }
}
