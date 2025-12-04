package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.dao.WaitlistDao;
import com.restaurant.model.User;
import com.restaurant.util.PasswordUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/staff/waitlist/new")
public class StaffWaitlistServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            // remember where to return to after login
            session = request.getSession(true);
            session.setAttribute("returnAfterLogin", "/staff/waitlist/new");
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User staff = (User) session.getAttribute("user");
        String role = (String) session.getAttribute("role");
        if (role == null ||
                !(role.equalsIgnoreCase("HOST")
                  || role.equalsIgnoreCase("MANAGER")
                  || role.equalsIgnoreCase("ADMIN"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // ensure restaurantId in session
        Integer restaurantId = (Integer) session.getAttribute("restaurantId");
        if (restaurantId == null && staff.getRestaurantId() != 0) {
            restaurantId = staff.getRestaurantId();
            session.setAttribute("restaurantId", restaurantId);
        }

        // optional: search existing customers
        String q = request.getParameter("q");
        if (q != null && !q.isBlank()) {
            try {
                UserDao userDao = new UserDao(getServletContext());
                List<User> matches = userDao.searchCustomers(q.trim());
                request.setAttribute("customerResults", matches);
                request.setAttribute("searchQuery", q.trim());
            } catch (Exception e) {
                e.printStackTrace();
                request.setAttribute("error", "Could not search customers.");
            }
        }

        // optional: customer selected from search
        String userIdParam = request.getParameter("userId");
        if (userIdParam != null && !userIdParam.isBlank()) {
            try {
                int uid = Integer.parseInt(userIdParam);
                UserDao userDao = new UserDao(getServletContext());
                User selected = userDao.getUserById(uid);
                request.setAttribute("selectedUser", selected);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        request.getRequestDispatcher("/WEB-INF/jsp/staff/waitlist_new.jsp")
               .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User staff = (User) session.getAttribute("user");
        Integer restaurantId = (Integer) session.getAttribute("restaurantId");
        if (restaurantId == null && staff.getRestaurantId() != 0) {
            restaurantId = staff.getRestaurantId();
            session.setAttribute("restaurantId", restaurantId);
        }

        if (restaurantId == null) {
            session.setAttribute("error", "No restaurant is associated with this staff account.");
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
            return;
        }

        String firstName = request.getParameter("first_name");
        String lastName  = request.getParameter("last_name");
        String phone     = request.getParameter("phone");
        String partyStr  = request.getParameter("party_size");
        String notes     = request.getParameter("special_requests");
        String email     = request.getParameter("email");
        String createAccount = request.getParameter("create_account");
        String existingUserIdParam = request.getParameter("userId");

        int partySize = 0;
        String error = null;

        if (firstName == null || firstName.isBlank()) {
            error = "First name is required.";
        } else if (lastName == null || lastName.isBlank()) {
            error = "Last name is required.";
        } else if (partyStr == null || partyStr.isBlank()) {
            error = "Party size is required.";
        } else {
            try {
                partySize = Integer.parseInt(partyStr);
                if (partySize < 1) {
                    error = "Party size must be at least 1.";
                }
            } catch (NumberFormatException e) {
                error = "Invalid party size.";
            }
        }

        if (error != null) {
            request.setAttribute("error", error);
            request.setAttribute("first_name", firstName);
            request.setAttribute("last_name", lastName);
            request.setAttribute("phone", phone);
            request.setAttribute("party_size", partyStr);
            request.setAttribute("email", email);
            doGet(request, response);  // redisplay form with errors
            return;
        }

        try {
            UserDao userDao = new UserDao(getServletContext());
            Integer userId = null;

            // 1) If an existing user was selected from search, use that
            if (existingUserIdParam != null && !existingUserIdParam.isBlank()) {
                userId = Integer.valueOf(existingUserIdParam);
            }
            // 2) Or, if host checked "create account" and provided email, create a new user
            else if ("on".equalsIgnoreCase(createAccount) && email != null && !email.isBlank()) {
                String tempPassword = "changeme"; // they can change it later
                String hash = PasswordUtil.hashPassword(tempPassword);
                User newCustomer = userDao.createCustomer(
                        firstName.trim(),
                        lastName.trim(),
                        email.trim().toLowerCase(),
                        phone != null ? phone.trim() : "",
                        hash
                );
                userId = newCustomer.getUserId();
                // optional: email them the temp password using your EmailService if you want
            }

            String fullName = (firstName + " " + lastName).trim();
            if (phone == null) phone = "";

            WaitlistDao wlDao = new WaitlistDao(getServletContext());
            wlDao.addToWaitlist(
                    restaurantId,
                    userId,
                    fullName,
                    phone.trim(),
                    partySize,
                    notes != null ? notes.trim() : "",
                    staff.getUserId()
            );

            session.setAttribute("success", "Guest added to waitlist.");
            response.sendRedirect(request.getContextPath() + "/staff/dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not add guest to waitlist. Please try again.");
            doGet(request, response);
        }
    }
}
