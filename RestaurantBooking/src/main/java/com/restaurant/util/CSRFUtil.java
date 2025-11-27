package com.restaurant.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.security.SecureRandom;
import java.util.Base64;

public class CSRFUtil {

    private static final String CSRF_ATTR = "csrf_token";
    private static final SecureRandom random = new SecureRandom();

    private static String generateToken() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /** Get existing token from session or create a new one */
    public static String getOrCreateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_ATTR);
        if (token == null) {
            token = generateToken();
            session.setAttribute(CSRF_ATTR, token);
        }
        return token;
    }

    /** Validate submitted token against session token */
    public static boolean validateToken(HttpServletRequest request, String submittedToken) {
        if (submittedToken == null || submittedToken.isEmpty()) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_ATTR);
        return sessionToken != null && sessionToken.equals(submittedToken);
    }
}
