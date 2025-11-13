package com.restaurant.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class EncodingFilter implements Filter {

    private String encoding = "UTF-8";

    @Override
    public void init(FilterConfig filterConfig) {
        String cfg = filterConfig.getInitParameter("encoding");
        if (cfg != null && !cfg.isBlank()) {
            encoding = cfg;
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // only set if not already set
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(encoding);
        }
        response.setCharacterEncoding(encoding);

        // (optional) force content type for text responses
        if (response.getContentType() == null && response instanceof HttpServletResponse) {
            response.setContentType("text/html; charset=" + encoding);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() { /* no-op */ }
} 
