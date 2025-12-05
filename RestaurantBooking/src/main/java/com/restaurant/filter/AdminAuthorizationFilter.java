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
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminAuthorizationFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest  = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpSession session = httpRequest.getSession(false);

        if (session != null) {
            User user = (User) session.getAttribute("user");

            if (user != null) {
                String role = user.getAccountType();

                // ✅ Allow both ADMIN and MANAGER
                if (role != null &&
                        (role.equalsIgnoreCase("ADMIN") ||
                         role.equalsIgnoreCase("MANAGER"))) {

                    chain.doFilter(request, response);
                    return;
                }
            }
        }

        // ❌ Not logged in or not admin/manager → redirect to login
        String ctx = httpRequest.getContextPath();
        httpResponse.sendRedirect(ctx + "/login?error=unauthorized");
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}
