package com.restaurant.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.restaurant.util.CSRFUtil;

/**
 * Filter to protect against CSRF attacks on POST, PUT, DELETE requests
 */
public class CSRFFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest  = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String path   = httpRequest.getRequestURI();
        String method = httpRequest.getMethod();
        String ctx    = httpRequest.getContextPath();
        
        if (path.startsWith(ctx + "/images/")
        	     || path.startsWith(ctx + "/css/")
        	     || path.startsWith(ctx + "/js/")
        	     || path.endsWith(".jpg")
        	     || path.endsWith(".jpeg")
        	     || path.endsWith(".png")
        	     || path.endsWith(".gif")
        	     || path.endsWith(".css")
        	     || path.endsWith(".js")) {

        	        chain.doFilter(request, response);
        	        return;
        	    }
        if (!path.startsWith(httpRequest.getContextPath() + "/admin")) {
            chain.doFilter(request, response);
            return;
        }
        
        
        // 1) Always ensure there is a CSRF token in the session
        //    and make it available to JSPs as "csrfToken"
        String csrfToken = CSRFUtil.getOrCreateToken(httpRequest);
        httpRequest.setAttribute("csrfToken", csrfToken);

       

        // 2) Only validate CSRF token for state-changing operations
        if ("POST".equalsIgnoreCase(method) ||
            "PUT".equalsIgnoreCase(method)  ||
            "DELETE".equalsIgnoreCase(method)) {

            // Skip CSRF validation for login and register endpoints
            if (path.endsWith("/login") || path.endsWith("/register")) {
                chain.doFilter(request, response);
                return;
            }

            // Get the CSRF token from the request (field name must match the form)
            String submittedToken = httpRequest.getParameter("csrf_token");

            // Validate the token
            if (!CSRFUtil.validateToken(httpRequest, submittedToken)) {
                httpResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
                httpResponse.getWriter().write(
                    "CSRF token validation failed. Please refresh the page and try again."
                );
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
