package com.moodmate.GUI;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;

import javax.swing.*;

public class WelcomePage extends BasePage {

    public WelcomePage() {
        super();

//        JLabel backgroundLabel = new JLabel();
        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background_welcome.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentArea.add(backgroundLabel);

        JLabel welcomeLabel = new JLabel("Welcome to MoodMate", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font(customFont, Font.BOLD, 24));
        welcomeLabel.setBounds(30, 50, contentArea.getWidth() - 60, 40);
        backgroundLabel.add(welcomeLabel);

        JLabel welcomeText = new JLabel("Do you have an account?", SwingConstants.CENTER);
        welcomeText.setFont(new Font(customFont, Font.PLAIN,14));
        welcomeText.setBounds(30, 150, contentArea.getWidth() - 60, 40);
        backgroundLabel.add(welcomeText);

        
        
        JButton signInButton = new JButton("Sign In");
        signInButton.setBounds((contentArea.getWidth() - 120) / 2, 200, 120, 40);
        signInButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signInButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
        signInButton.setBackground(customGreen);
        signInButton.setOpaque(true);

        signInButton.addActionListener(e -> {
            addToNavigationStack();
            new SignInPage();
            dispose();
        }); 
        backgroundLabel.add(signInButton);
        
        
        JLabel signupText = new JLabel("If you don't have an account,", SwingConstants.CENTER);
        signupText.setFont(new Font(customFont, Font.PLAIN, 14));
        signupText.setBounds(30, 270, contentArea.getWidth() - 60, 40);
        signupText.setForeground(Color.GRAY);  // Set text color to grey
        backgroundLabel.add(signupText);

        // Sign Up Button
        JButton signUpButton = new JButton("Sign Up Here");
        signUpButton.setBounds((contentArea.getWidth() - 120) / 2, 300, 120, 40);
        signUpButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
        signUpButton.addActionListener(e -> {
            addToNavigationStack();
            new SignUpPage();
            dispose();
        });
        backgroundLabel.add(signUpButton);
//        signUpButton.setBounds(PADDING_X, 380, contentArea.getWidth() - 2 * PADDING_X, FIELD_HEIGHT + 30);
        signUpButton.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 0, true));
//        signUpButton.setBackground(Color.decode("#45C78A"));
//        signUpButton.setBackground(getForeground());
        signUpButton.setOpaque(true);

        backgroundLabel.add(signUpButton);
    }

    public static void main(String[] args) {
        new WelcomePage();
    }
}
