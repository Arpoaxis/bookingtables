package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");

        // ✅ Use the ServletContext-based constructor
        UserDao dao = new UserDao(getServletContext());

        User fresh = dao.getUserById(sessionUser.getUserId());
        if (fresh == null) {
            // Fallback so the JSP still has something
            fresh = sessionUser;
        }

        session.setAttribute("user", fresh);
        request.setAttribute("user", fresh);

        request.getRequestDispatcher("/WEB-INF/jsp/profile/profile_page.jsp")
               .forward(request, response);
    }
}
