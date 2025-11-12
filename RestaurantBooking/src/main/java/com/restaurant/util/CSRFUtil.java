package com.restaurant.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for CSRF token generation and validation
 */
public class CSRFUtil {

    private static final String CSRF_TOKEN_ATTRIBUTE = "CSRF_TOKEN";
    private static final int TOKEN_LENGTH = 32;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Generate a new CSRF token and store it in the session
     * @param session The HTTP session
     * @return The generated CSRF token
     */
    public static String generateToken(HttpSession session) {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        session.setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
        return token;
    }

    /**
     * Get the CSRF token from the session, or generate a new one if it doesn't exist
     * @param request The HTTP request
     * @return The CSRF token
     */
    public static String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        if (token == null) {
            token = generateToken(session);
        }
        return token;
    }

    /**
     * Validate a CSRF token against the token stored in the session
     * @param request The HTTP request
     * @param submittedToken The token submitted with the form
     * @return true if the token is valid, false otherwise
     */
    public static boolean validateToken(HttpServletRequest request, String submittedToken) {
        if (submittedToken == null || submittedToken.isEmpty()) {
            return false;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        if (sessionToken == null) {
            return false;
        }

        // Use constant-time comparison to prevent timing attacks
        return constantTimeEquals(sessionToken, submittedToken);
    }

    /**
     * Constant-time string comparison to prevent timing attacks
     * @param a First string
     * @param b Second string
     * @return true if strings are equal, false otherwise
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }

        byte[] aBytes = a.getBytes();
        byte[] bBytes = b.getBytes();

        if (aBytes.length != bBytes.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < aBytes.length; i++) {
            result |= aBytes[i] ^ bBytes[i];
        }
        return result == 0;
    }
}
