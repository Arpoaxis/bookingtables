package com.restaurant.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.restaurant.util.CSRFUtil;

/**
 * Filter to protect against CSRF attacks on POST, PUT, DELETE requests
 */
@WebFilter(urlPatterns = {"/*"})
public class CSRFFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String method = httpRequest.getMethod();

        // Only validate CSRF token for state-changing operations
        if ("POST".equalsIgnoreCase(method) ||
            "PUT".equalsIgnoreCase(method) ||
            "DELETE".equalsIgnoreCase(method)) {

            // Skip CSRF validation for login and register endpoints (they need to work without prior session)
            String path = httpRequest.getRequestURI();
            if (path.endsWith("/login") || path.endsWith("/register")) {
                chain.doFilter(request, response);
                return;
            }

            // Get the CSRF token from the request
            String submittedToken = httpRequest.getParameter("csrf_token");

            // Validate the token
            if (!CSRFUtil.validateToken(httpRequest, submittedToken)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write("CSRF token validation failed. Please refresh the page and try again.");
                return;
            }
        }

        // Continue with the request
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
