package com.moodmate.utility;

import java.util.regex.Pattern;
public class PasswordValidator {
    // Method to enforce password policy
    public static boolean isValidPassword(String password) {
        // Password must be at least 8 characters long
        // Must contain at least one uppercase letter, one lowercase letter, and one digit
        String passwordRegex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).{8,}$";
        return Pattern.matches(passwordRegex, password);

    }
}
