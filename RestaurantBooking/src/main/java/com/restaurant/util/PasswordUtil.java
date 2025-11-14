package com.restaurant.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Utility class for secure password hashing and verification using BCrypt
 */
public class PasswordUtil {

    // BCrypt work factor (log2 of iterations). 12 is a good balance between security and performance
    private static final int WORK_FACTOR = 12;

    /**
     * Hash a plaintext password using BCrypt
     * @param plainTextPassword The password to hash
     * @return The hashed password
     */
    public static String hashPassword(String plainTextPassword) {
        if (plainTextPassword == null || plainTextPassword.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Verify a plaintext password against a hashed password
     * @param plainTextPassword The password to verify
     * @param hashedPassword The hashed password to check against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainTextPassword, String hashedPassword) {
        if (plainTextPassword == null || hashedPassword == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            // Invalid hash format
            return false;
        }
    }

    /**
     * Validate password strength
     * @param password The password to validate
     * @return true if password meets minimum requirements, false otherwise
     */
    public static boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }

        // Require at least 3 out of 4 character types
        int typesPresent = (hasUpper ? 1 : 0) + (hasLower ? 1 : 0) +
                          (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        return typesPresent >= 3;
    }

    /**
     * Get password strength requirements message
     * @return String describing password requirements
     */
    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters long and contain at least 3 of the following: " +
               "uppercase letters, lowercase letters, numbers, special characters.";
    }
}
