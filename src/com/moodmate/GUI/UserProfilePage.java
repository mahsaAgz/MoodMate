package com.moodmate.GUI;
import com.moodmate.GUI.SignInPage.GlobalVariable;
import jess.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.NumberFormatter;

import java.awt.*;
import java.text.NumberFormat;
import java.util.Hashtable;

public class UserProfilePage extends BasePage {

    private static final int PADDING_X = 60; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields
    private static final int MARGIN = 20; // Vertical margin between components
 // Declare the input fields and buttons as instance variables
    private JTextField usernameField;
    private JFormattedTextField ageField;
    private JRadioButton maleButton;
    private JRadioButton femaleButton;
    private JRadioButton preferNotToSayButton;
    private JSlider eiSlider, snSlider, tfSlider, jpSlider;
    
    public UserProfilePage(String username, String age, int gender) {

    	this();
        // Pre-fill the fields with the provided data
        usernameField.setText(username);
        ageField.setText(age); // This works because ageField is now a JFormattedTextField
        if (gender == 1) {
            maleButton.setSelected(true);
        } else if (gender == 2) {
            femaleButton.setSelected(true);
        } else if (gender == 0) {
            preferNotToSayButton.setSelected(true);
        }
    }

    
    public UserProfilePage() {
        super();

        // Create a contentPanel for all components
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(null); // Absolute positioning

        // Add background image (it's just a visual element, so we add it to contentPanel)
        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentPanel.add(backgroundLabel);
        backgroundLabel.setLayout(null);

        int currentY = 20; // Start Y position for components

        // Title Label
        JLabel titleLabel = new JLabel("Tell me about yourself ", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 20));
        titleLabel.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(titleLabel);

        currentY += FIELD_HEIGHT + MARGIN;

        // Description Labels
        JLabel description = new JLabel("Take a few moments to make your", SwingConstants.CENTER);
        description.setFont(new Font(customFont, Font.PLAIN, 12));
        description.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(description);

        currentY += FIELD_HEIGHT;

        JLabel description2 = new JLabel("profile for a customized experience", SwingConstants.CENTER);
        description2.setFont(new Font(customFont, Font.PLAIN, 12));
        description2.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        contentPanel.add(description2);

        currentY += FIELD_HEIGHT + MARGIN;

        // Username
        JLabel usernameLabel = new JLabel("My name is:");
        usernameLabel.setBounds(PADDING_X, currentY, 100, FIELD_HEIGHT);
        contentPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(PADDING_X + 110, currentY, contentArea.getWidth() - PADDING_X - 140, FIELD_HEIGHT);
        contentPanel.add(usernameField);

        currentY += FIELD_HEIGHT + MARGIN;

        // Age
//        JLabel ageLabel = new JLabel("My age is:");
//        ageLabel.setBounds(PADDING_X, currentY, 100, FIELD_HEIGHT);
//        contentPanel.add(ageLabel);

        JLabel ageLabel = new JLabel("My age is:");
        ageLabel.setBounds(PADDING_X, currentY, 100, FIELD_HEIGHT);
        contentPanel.add(ageLabel);

        // Create a NumberFormatter to restrict input to integers
        NumberFormat integerFormat = NumberFormat.getIntegerInstance();
        integerFormat.setGroupingUsed(false); // Disable thousand separators
        NumberFormatter numberFormatter = new NumberFormatter(integerFormat);
        numberFormatter.setValueClass(Integer.class); // Only allow integers
        numberFormatter.setAllowsInvalid(false); // Reject invalid input
        numberFormatter.setMinimum(1); // Optional: Set a minimum value for age

        ageField = new JFormattedTextField(numberFormatter);
        ageField.setBounds(PADDING_X + 110, currentY, contentArea.getWidth() - PADDING_X - 140, FIELD_HEIGHT);
        contentPanel.add(ageField);

        currentY += FIELD_HEIGHT + MARGIN;

        // Gender
        JLabel genderLabel = new JLabel("Gender:");
        genderLabel.setBounds(PADDING_X, currentY, 100, FIELD_HEIGHT);
        contentPanel.add(genderLabel);

        maleButton = new JRadioButton("Male");
        maleButton.setBounds(PADDING_X + 110, currentY, 140, FIELD_HEIGHT);
        contentPanel.add(maleButton);

        currentY += FIELD_HEIGHT;

        femaleButton = new JRadioButton("Female");
        femaleButton.setBounds(PADDING_X + 110, currentY, 140, FIELD_HEIGHT);
        contentPanel.add(femaleButton);

        currentY += FIELD_HEIGHT;

        preferNotToSayButton = new JRadioButton("Prefer not to say");
        preferNotToSayButton.setBounds(PADDING_X + 110, currentY, 140, FIELD_HEIGHT);
        contentPanel.add(preferNotToSayButton);

        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        genderGroup.add(preferNotToSayButton);

        currentY += FIELD_HEIGHT + MARGIN;

        // MBTI Preferences using sliders
        JLabel mbtiLabel = new JLabel("MBTI Preferences:");
        mbtiLabel.setFont(new Font(customFont, Font.BOLD, 16));
        mbtiLabel.setBounds(PADDING_X, currentY, 300, FIELD_HEIGHT);
        contentPanel.add(mbtiLabel);
        
        currentY += FIELD_HEIGHT;
        
        
        //MBTI TEST button
        JLabel mbtiTestLabel = new JLabel("Take a short test here:");
        mbtiTestLabel.setFont(new Font(customFont, Font.PLAIN, 12));
        mbtiTestLabel.setBounds(PADDING_X, currentY, 300, FIELD_HEIGHT);
        contentPanel.add(mbtiTestLabel);
        int test= contentArea.getWidth();
    
        JButton mbtiTestButton = new JButton("Click Here");
        mbtiTestButton.setBounds(test - (PADDING_X * 2) , currentY, 100, FIELD_HEIGHT );
//        mbtiTestButton.setBorder(BorderFactory.createEmptyBorder(0,,0,0));
//        mbtiTestButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
        mbtiTestButton.setBackground(customGreen);
        mbtiTestButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));    
        
        mbtiTestButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String ageText = ageField.getText().trim();
            int age = -1;

            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                // Show error if age is not a valid integer
                JOptionPane.showMessageDialog(
                    contentPanel,
                    "Please enter a valid age.",
                    "Invalid Age",
                    JOptionPane.ERROR_MESSAGE
                );
                return; // Stop further processing
            }

            int gender = maleButton.isSelected() ? 1 : 
                femaleButton.isSelected() ? 2 : 
                preferNotToSayButton.isSelected() ? 0 : -1;

            if (username.isEmpty() || gender == -1) {
                JOptionPane.showMessageDialog(
                    contentPanel,
                    "Please fill out all required fields (Name, Age, and Gender).",
                    "Incomplete Profile",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
              	try {
            		Rete engine = new Rete();
            		
            		engine.reset();
                    engine.batch("src/com/moodmate/logic/templates.clp");
                    engine.batch("src/com/moodmate/logic/user_profile_rules.clp");
                    
                    Fact userInput = new Fact("profile-input", engine);
                    userInput.setSlotValue("user_id", new Value( GlobalVariable.userId, RU.INTEGER));
                    userInput.setSlotValue("name", new Value(username, RU.STRING));
                    userInput.setSlotValue("gender", new Value(gender, RU.INTEGER));
                    userInput.setSlotValue("age", new Value(age, RU.INTEGER));
                    engine.assertFact(userInput);
                    engine.run();
                    
                    
            	}catch(JessException ex) {ex.printStackTrace();}
                addToNavigationStack();
                
                // Pass data to MbtiTestPage
                MbtiTestPage mbtiTestPage = new MbtiTestPage(GlobalVariable.userId, username, age, gender);
                mbtiTestPage.setVisible(true);
                dispose();
            }
        });

        

        

        contentPanel.add(mbtiTestButton);
        
        currentY += FIELD_HEIGHT + MARGIN;

        // Create Sliders for MBTI preferences
        currentY = addMBTISlider(contentPanel, currentY, "Introvert (I)","Extrovert (E)", PADDING_X);
        currentY = addMBTISlider(contentPanel, currentY, "Sensing (S)","Intuition (N)", PADDING_X);
        currentY = addMBTISlider(contentPanel, currentY, "Thinking (T)","Feeling (F)", PADDING_X);
        currentY = addMBTISlider(contentPanel, currentY, "Judging (J)","Perceiving (P)", PADDING_X);

        currentY += MARGIN;

        // next Button
        // next Button
        
        JButton nextButton = new JButton("Next");
        nextButton.setBounds(PADDING_X, currentY, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 10);

        nextButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
//        signUpButton.setBackground(Color.decode("#45C78A"));
        nextButton.setBackground(customGreen);
        nextButton.setOpaque(true);
        nextButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));    
        
     // Modify the nextButton action listener
        nextButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String ageText = ageField.getText().trim();
            int age = -1;

            try {
                age = Integer.parseInt(ageText);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                    contentPanel,
                    "Please enter a valid age.",
                    "Invalid Age",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            int gender = maleButton.isSelected() ? 1 : 
                         femaleButton.isSelected() ? 2 : 
                         preferNotToSayButton.isSelected() ? 0 : -1;

            if (username.isEmpty() || gender == -1) {
                JOptionPane.showMessageDialog(
                    contentPanel,
                    "Please fill out all required fields (Name, Age, and Gender).",
                    "Incomplete Profile",
                    JOptionPane.WARNING_MESSAGE
                );
            } else {
                String mbtiResult = calculateMBTI();
//                addToNavigationStack();
                new HobbiesPage(username, age, gender, mbtiResult);
                dispose();
            }
        });
 
        contentPanel.add(nextButton);
        currentY += MARGIN;
        contentPanel.setPreferredSize(new Dimension(contentArea.getWidth(), currentY + 100));
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        contentArea.add(scrollPane, BorderLayout.CENTER);
    }

 // Modify the addMBTISlider method to store slider references
    private int addMBTISlider(JPanel contentPanel, int currentY, String label0, String label100, int paddingX) {
        JSlider mbtiSlider = new JSlider(0, 100, 50);
        mbtiSlider.setBounds(paddingX, currentY, 250, FIELD_HEIGHT*2);
        mbtiSlider.setMajorTickSpacing(20);
        mbtiSlider.setMinorTickSpacing(5);
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        labels.put(0, new JLabel(label0));
        labels.put(100, new JLabel(label100));
        mbtiSlider.setLabelTable(labels);  
        mbtiSlider.setPaintLabels(true);

        // Store the slider reference
        if (label0.contains("Introvert")) {
            eiSlider = mbtiSlider;
        } else if (label0.contains("Sensing")) {
            snSlider = mbtiSlider;
        } else if (label0.contains("Thinking")) {
            tfSlider = mbtiSlider;
        } else if (label0.contains("Judging")) {
            jpSlider = mbtiSlider;
        }

        contentPanel.add(mbtiSlider);
        currentY += FIELD_HEIGHT + MARGIN;
        return currentY;
    }
    
 // Add helper method to calculate MBTI
    private String calculateMBTI() {
        StringBuilder mbti = new StringBuilder();
        
        // E/I preference
        mbti.append(eiSlider.getValue() > 50 ? "E" : "I");
        
        // S/N preference
        mbti.append(snSlider.getValue() > 50 ? "N" : "S");
        
        // T/F preference
        mbti.append(tfSlider.getValue() > 50 ? "F" : "T");
        
        // J/P preference
        mbti.append(jpSlider.getValue() > 50 ? "P" : "J");
        
        return mbti.toString();
    }

    public static void main(String[] args) {
        new UserProfilePage();
    }

}