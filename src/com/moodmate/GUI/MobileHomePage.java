package com.moodmate.GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MobileHomePage extends BasePage {

    public MobileHomePage() {
        super(); // Initialize BasePage

        // Set content area background
        contentArea.setLayout(null); // Use absolute layout for positioning
        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/mobileapppage.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentArea.add(backgroundLabel);

        // Create "Welcome to MoodMate" label
       
        setVisible(true); // Make the frame visible
    }

    public static void main(String[] args) {
        new MobileHomePage(); // Launch the WelcomePage
    }
}
