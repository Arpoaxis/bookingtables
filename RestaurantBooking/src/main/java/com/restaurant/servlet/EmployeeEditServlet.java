package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/admin/employee/edit")
public class EmployeeEditServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !( "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role) )) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        Integer restaurantId = (Integer) session.getAttribute("restaurantId");
        if (restaurantId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No restaurant associated with this user.");
            return;
        }

        String employeeIdStr = request.getParameter("employeeId");
        if (employeeIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/employees");
            return;
        }

        try {
            int employeeId = Integer.parseInt(employeeIdStr);
            UserDao userDao = new UserDao(getServletContext());
            User target = userDao.getUserById(employeeId);

            if (target == null ||
                target.getRestaurantId() == null ||
                !restaurantId.equals(target.getRestaurantId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Employee not found or not in your restaurant.");
                return;
            }

            request.setAttribute("employee", target);
            request.getRequestDispatcher("/WEB-INF/jsp/admin/employee_edit.jsp")
                   .forward(request, response);

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/employees");
        }

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String role = (String) session.getAttribute("role");
        if (role == null || !( "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role) )) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }

        Integer restaurantId = (Integer) session.getAttribute("restaurantId");
        if (restaurantId == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No restaurant associated with this user.");
            return;
        }

        String employeeIdStr = request.getParameter("employeeId");
        String csrfTokenForm  = request.getParameter("csrf_token");
        String csrfTokenSess  = (String) session.getAttribute("csrf_token");

        // very simple CSRF check, mirroring the rest of your app
        if (csrfTokenSess != null && !csrfTokenSess.equals(csrfTokenForm)) {
            session.setAttribute("error", "CSRF token validation failed. Please try again.");
            response.sendRedirect(request.getContextPath() + "/admin/employees");
            return;
        }

        if (employeeIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/admin/employees");
            return;
        }

        String firstName = request.getParameter("first_name");
        String lastName  = request.getParameter("last_name");
        String email     = request.getParameter("email");
        String phone     = request.getParameter("phoneNumber");
        String roleName  = request.getParameter("role");

        if (firstName == null || firstName.isBlank() ||
            lastName == null  || lastName.isBlank()  ||
            email == null     || email.isBlank()     ||
            phone == null     || phone.isBlank()) {

            session.setAttribute("error", "All fields are required to update an employee.");
            response.sendRedirect(request.getContextPath() + "/admin/employees");
            return;
        }

        String phoneDigits = phone.replaceAll("[^0-9]", "");
        String normalizedRole = roleName == null ? "EMPLOYEE" : roleName.toUpperCase();

        try {
            int employeeId = Integer.parseInt(employeeIdStr);
            UserDao userDao = new UserDao(getServletContext());
            User target = userDao.getUserById(employeeId);

            if (target == null ||
                target.getRestaurantId() == null ||
                !restaurantId.equals(target.getRestaurantId())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Employee not found or not in your restaurant.");
                return;
            }

            try {
                userDao.updateEmployeeDetails(employeeId, firstName, lastName, email, phoneDigits, normalizedRole);
                session.setAttribute("success", "Employee updated successfully.");
            } catch (SQLException ex) {
                ex.printStackTrace();
                String msg = ex.getMessage();
                if (msg != null && msg.contains("users.email")) {
                    session.setAttribute("error", "Email is already in use by another user.");
                } else if (msg != null && msg.contains("users.phone_number")) {
                    session.setAttribute("error", "Phone number is already in use by another user.");
                } else {
                    session.setAttribute("error", "Could not update employee. Please check details.");
                }
            }

            response.sendRedirect(request.getContextPath() + "/admin/employees");

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/admin/employees");
        }

    }
}
