package com.restaurant.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.UUID;

public class CSRFUtil {

    private static final String CSRF_ATTR = "csrf_token";

    /** Create a token if missing, store in session, and return it */
    public static String getOrCreateToken(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String token = (String) session.getAttribute(CSRF_ATTR);
        if (token == null) {
            token = UUID.randomUUID().toString();
            session.setAttribute(CSRF_ATTR, token);
        }
        return token;
    }

    /** Validate the submitted token against the one in the session */
    public static boolean validateToken(HttpServletRequest request, String submittedToken) {
        if (submittedToken == null) {
            return false;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String expected = (String) session.getAttribute(CSRF_ATTR);
        return expected != null && expected.equals(submittedToken);
    }
}
