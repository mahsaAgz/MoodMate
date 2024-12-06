package com.moodmate.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class BaseHomePage extends BasePage {

    private boolean isMenuBarOpen = false; // Track the state of the menu bar
    int contentWidth = contentArea.getWidth();

    public BaseHomePage() {
        super();

        
        // -------- Top Panel --------
        topPanel = new JPanel(new BorderLayout());
        topPanel.setPreferredSize(new Dimension(FRAME_WIDTH, TOP_PANEL_HEIGHT));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));

        // Add Logo (Left)
        JLabel logoLabel = resizeIconMaintainAspect("assets/images/logo.png", ICON_SIZE);
        if (logoLabel != null) {
            topPanel.add(logoLabel, BorderLayout.WEST);
        } else {
            System.out.println("Logo image not found: assets/images/logo.png");
        }

        // Add Profile Icon (Right) with Menu Action
        JLabel profileIcon = resizeIcon("assets/images/profile.png", ICON_SIZE, ICON_SIZE);
        if (profileIcon != null) {
            profileIcon.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            profileIcon.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleMenuBar(); // Trigger sidebar toggle on click
                }
            });
            topPanel.add(profileIcon, BorderLayout.EAST);
        } else {
            System.out.println("Profile icon image not found: assets/images/profile.png");
        }

        // Add the topPanel to the containerPanel
        containerPanel.add(topPanel, BorderLayout.SOUTH);

        // Add the containerPanel to the NORTH of the frame
        add(containerPanel, BorderLayout.NORTH);
        
        
        //--------- END of Top Panel ---------------

        // -------- Layered Pane for Overlay --------
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT - STATUS_BAR_HEIGHT - NAV_BAR_HEIGHT));
        add(layeredPane, BorderLayout.CENTER);

        JLabel backgroundLabel = new JLabel(new ImageIcon("assets/images/background.png"));
        backgroundLabel.setBounds(0, 0, contentArea.getWidth(), contentArea.getHeight());
        contentArea.add(backgroundLabel);


    }
    
    private void toggleMenuBar() {
        int startX = isMenuBarOpen ? FRAME_WIDTH - MENU_BAR_WIDTH : FRAME_WIDTH;
        int endX = isMenuBarOpen ? FRAME_WIDTH : FRAME_WIDTH - MENU_BAR_WIDTH;

        new Thread(() -> {
            try {
                if (!isMenuBarOpen) { // Slide in
                    for (int x = startX; x >= endX; x -= 10) {
                        menuBar.setBounds(x, 0, MENU_BAR_WIDTH, FRAME_HEIGHT - STATUS_BAR_HEIGHT - NAV_BAR_HEIGHT);
                        menuBar.repaint();
                        Thread.sleep(10);
                    }
                } else { // Slide out
                    for (int x = startX; x <= endX; x += 10) {
                        menuBar.setBounds(x, 0, MENU_BAR_WIDTH, FRAME_HEIGHT - STATUS_BAR_HEIGHT - NAV_BAR_HEIGHT);
                        menuBar.repaint();
                        Thread.sleep(10);
                    }
                }
                isMenuBarOpen = !isMenuBarOpen; // Toggle the state
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        new BaseHomePage();
    }
}
