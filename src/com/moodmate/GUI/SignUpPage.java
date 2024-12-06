package com.moodmate.GUI;
import com.moodmate.GUI.SignInPage.GlobalVariable;
import jess.*;
import javax.swing.*;
import com.moodmate.database.DatabaseConnection;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Iterator;

public class SignUpPage extends BasePage {

    // Constants for easy adjustments
    private static final int PADDING_X = 60; // Horizontal padding for fields
    private static final int FIELD_HEIGHT = 30; // Height for the input fields and button
    private static final String customFont = "Arial"; // Default font
    private static final Color customGreen = new Color(69, 199, 138); // Define custom color

    public SignUpPage() {
        super();

        // Add background image
        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentArea.add(backgroundLabel);
        backgroundLabel.setLayout(null);

        // Title Label
        JLabel titleLabel = new JLabel("Sign Up", SwingConstants.CENTER);
        titleLabel.setFont(new Font(customFont, Font.BOLD, 24));
        titleLabel.setBounds(PADDING_X, 50, contentArea.getWidth() - 2 * PADDING_X, 40);
        backgroundLabel.add(titleLabel);

        // Username Field
        JTextField usernameField = createInputField("Username", 120);
        backgroundLabel.add(usernameField);

        // Email Field
        JTextField emailField = createInputField("Email", 180);
        backgroundLabel.add(emailField);

        // Password Field
        JPasswordField passwordField = createPasswordField("Password", 240);
        backgroundLabel.add(passwordField);

        // Confirm Password Field
        JPasswordField confirmPasswordField = createPasswordField("Confirm Password", 300);
        backgroundLabel.add(confirmPasswordField);

        // Sign Up Button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setBounds(PADDING_X, 380, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 30);
        signUpButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        signUpButton.setBackground(customGreen);
        signUpButton.setOpaque(true);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signUpButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (username.isEmpty() || username.equals("Username") ||
                email.isEmpty() || email.equals("Email") ||
                password.isEmpty() || password.equals("Password") ||
                confirmPassword.isEmpty() || confirmPassword.equals("Confirm Password")) {

                JOptionPane.showMessageDialog(
                    this,
                    "All fields are required. Please fill in all the details.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
                );
            } else if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match. Please confirm your password correctly.",
                    "Password Mismatch",
                    JOptionPane.WARNING_MESSAGE
                );
            } else if (username.length() < 3) {
                JOptionPane.showMessageDialog(
                    this,
                    "Username must be at least 3 characters long.",
                    "Invalid Username",
                    JOptionPane.WARNING_MESSAGE
                );
            }  else{
                // Check if the username already exists
                boolean usernameExists = DatabaseConnection.usernameExists(username);
                if (usernameExists) {
                    JOptionPane.showMessageDialog(
                        this,
                        "This username is already taken. Please choose a different one.",
                        "Username Exists",
                        JOptionPane.WARNING_MESSAGE
                    );}
                else {
                try {
                    Rete engine = new Rete();
                    engine.reset();
                    engine.batch("src/com/moodmate/logic/templates.clp");
                    engine.batch("src/com/moodmate/logic/rules.clp");

                    Fact userInput = new Fact("user-input", engine);
                    userInput.setSlotValue("username", new Value(username, RU.STRING));
                    userInput.setSlotValue("email", new Value(email, RU.STRING));
                    userInput.setSlotValue("password", new Value(password, RU.STRING));
                    engine.assertFact(userInput);

                    engine.run();

                    boolean isValid = true;
                    StringBuilder validationMessages = new StringBuilder();

                    Iterator<?> facts = engine.listFacts();
                    while (facts.hasNext()) {
                        Fact fact = (Fact) facts.next();
                        String name = fact.getName();
                        
                        // Debug print to see what facts we're getting
                        System.out.println("Processing fact: " + name);
                        
                        if (name.equals("MAIN::validation-result") || 
                            name.equals("MAIN::password-validation-result") || 
                            name.equals("MAIN::email-validation-result")) {
                            
                            boolean valid = fact.getSlotValue("valid").equals(Funcall.TRUE);
                            String message = fact.getSlotValue("message").stringValue(null);
                            
                            // Debug print
                            System.out.println("Validation result: valid=" + valid + ", message=" + message);
                            
                            if (!valid) {
                                isValid = false;
                                validationMessages.append("- ").append(message).append("\n");
                            }
                        }
                    }

                    if (isValid) {
                        if (DatabaseConnection.insertUser(username, password, email)) {
                            JOptionPane.showMessageDialog(
                                this,
                                "Success: User registered successfully!",
                                "Registration Success",
                                JOptionPane.INFORMATION_MESSAGE
                            );
                            GlobalVariable.userId = DatabaseConnection.getUserIdByUsername(username);
                            System.out.println("User ID for username '" + username + "': " + GlobalVariable.userId);
                            addToNavigationStack();
                            new UserProfilePage();
                            dispose();
                        }
                    } else {
                        JOptionPane.showMessageDialog(
                            this,
                            "Registration failed. Please check the following:\n\n" + validationMessages.toString(),
                            "Validation Failed",
                            JOptionPane.WARNING_MESSAGE
                        );
                    }
                } catch (JessException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(
                        this,
                        "An error occurred during registration. Please try again.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            }});

        backgroundLabel.add(signUpButton);
    }

    private JTextField createInputField(String placeholder, int yPosition) {
        JTextField field = new JTextField(placeholder);
        field.setBounds(PADDING_X, yPosition, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        field.setFont(new Font(customFont, Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                }
            }
        });
        return field;
    }

    private JPasswordField createPasswordField(String placeholder, int yPosition) {
        JPasswordField field = new JPasswordField(placeholder);
        field.setBounds(PADDING_X, yPosition, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT);
        field.setFont(new Font(customFont, Font.PLAIN, 14));
        field.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        field.setEchoChar((char) 0);
        field.setForeground(Color.GRAY);
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    field.setEchoChar('*');
                }
            }

            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setForeground(Color.GRAY);
                    field.setText(placeholder);
                    field.setEchoChar((char) 0);
                }
            }
        });
        return field;
    }

    public static void main(String[] args) {
        new SignUpPage();
    }
}
