package com.restaurant.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.restaurant.model.User;

/**
 * Filter to protect admin endpoints from unauthorized access
 */
@WebFilter(urlPatterns = {"/admin/*", "/jsp/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Get the session
        HttpSession session = httpRequest.getSession(false);

        // Check if user is logged in and has ADMIN role
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null && "ADMIN".equalsIgnoreCase(user.getAccountType())) {
                // User is authorized, continue
                chain.doFilter(request, response);
                return;
            }
        }

        // User is not authorized - redirect to login page
        httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
        httpResponse.sendRedirect(httpRequest.getContextPath() + "/jsp/login/login_page.jsp?error=unauthorized");
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
