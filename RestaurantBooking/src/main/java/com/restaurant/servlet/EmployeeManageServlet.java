package com.restaurant.servlet;

import com.restaurant.dao.UserDao;
import com.restaurant.model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin/employees")
public class EmployeeManageServlet extends HttpServlet {
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

        // Optionally, enforce admin role
        String role = (String) session.getAttribute("role");
        if (role == null ||
            !( "ADMIN".equalsIgnoreCase(role) || "MANAGER".equalsIgnoreCase(role) )) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
            return;
        }


        Integer restaurantId = (Integer) session.getAttribute("restaurantId");
        if (restaurantId == null) {
            request.setAttribute("error", "You are not associated with a restaurant.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")
                   .forward(request, response);
            return;
        }

        try {
            UserDao userDao = new UserDao(getServletContext());
            List<User> employees = userDao.findEmployeesByRestaurant(restaurantId);

            // ---- Filtering ----
            String q           = request.getParameter("q");       // search by name/email
            String roleFilter  = request.getParameter("role");    // EMPLOYEE/HOST/MANAGER/ALL
            String activeParam = request.getParameter("active");  // active/inactive/all

            if (q != null && !q.isBlank()) {
                String lower = q.toLowerCase();
                List<User> filtered = new java.util.ArrayList<>();
                for (User u : employees) {
                    String fullName = (u.getFirstName() + " " + u.getLastName()).toLowerCase();
                    String email = u.getEmail() == null ? "" : u.getEmail().toLowerCase();
                    if (fullName.contains(lower) || email.contains(lower)) {
                        filtered.add(u);
                    }
                }
                employees = filtered;
            }

            if (roleFilter != null && !roleFilter.isBlank() && !"ALL".equalsIgnoreCase(roleFilter)) {
                String wanted = roleFilter.toUpperCase();
                List<User> filtered = new java.util.ArrayList<>();
                for (User u : employees) {
                    if (u.getAccountType() != null &&
                        u.getAccountType().equalsIgnoreCase(wanted)) {
                        filtered.add(u);
                    }
                }
                employees = filtered;
            }

            if (activeParam != null && !activeParam.isBlank() && !"all".equalsIgnoreCase(activeParam)) {
                boolean wantActive = "active".equalsIgnoreCase(activeParam);
                List<User> filtered = new java.util.ArrayList<>();
                for (User u : employees) {
                    if (u.isActive() == wantActive) {
                        filtered.add(u);
                    }
                }
                employees = filtered;
            }

            // ---- Sorting ----
            String sort = request.getParameter("sort"); // id, name, email, role, active
            String dir  = request.getParameter("dir");  // asc/desc
            boolean desc = "desc".equalsIgnoreCase(dir);

            java.util.Comparator<User> cmp = null;
            if ("name".equalsIgnoreCase(sort)) {
                cmp = java.util.Comparator
                        .comparing((User u) -> u.getLastName() == null ? "" : u.getLastName())
                        .thenComparing(u -> u.getFirstName() == null ? "" : u.getFirstName());
            } else if ("email".equalsIgnoreCase(sort)) {
                cmp = java.util.Comparator.comparing(
                        (User u) -> u.getEmail() == null ? "" : u.getEmail(),
                        String.CASE_INSENSITIVE_ORDER);
            } else if ("role".equalsIgnoreCase(sort)) {
                cmp = java.util.Comparator.comparing(
                        (User u) -> u.getAccountType() == null ? "" : u.getAccountType(),
                        String.CASE_INSENSITIVE_ORDER);
            } else if ("active".equalsIgnoreCase(sort)) {
                cmp = java.util.Comparator.comparing(User::isActive); // false < true
            } else if ("id".equalsIgnoreCase(sort)) {
                cmp = java.util.Comparator.comparingInt(User::getUserId);
            }

            if (cmp != null) {
                if (desc) {
                    cmp = cmp.reversed();
                }
                employees.sort(cmp);
            }

            // expose filter/sort state to JSP
            request.setAttribute("employees", employees);
            request.setAttribute("q", q);
            request.setAttribute("roleFilter", roleFilter);
            request.setAttribute("activeFilter", activeParam);
            request.setAttribute("sort", sort);
            request.setAttribute("dir", dir);

            request.getRequestDispatcher("/WEB-INF/jsp/admin/employees.jsp")
                   .forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Could not load employees.");
            request.getRequestDispatcher("/WEB-INF/jsp/error.jsp")
                   .forward(request, response);
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

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/admin/employees");
            return;
        }

        try {
            UserDao userDao = new UserDao(getServletContext());

            // ---------- CREATE NEW EMPLOYEE ----------
            if ("create".equals(action)) {
                String firstName = request.getParameter("first_name");
                String lastName  = request.getParameter("last_name");
                String email     = request.getParameter("email");
                String phoneStr  = request.getParameter("phoneNumber");
                String roleName  = request.getParameter("role");

                // basic validation
                if (firstName == null || firstName.isBlank() ||
                    lastName == null  || lastName.isBlank()  ||
                    email == null     || email.isBlank()     ||
                    phoneStr == null  || phoneStr.isBlank()) {

                    session.setAttribute("error", "All fields are required to create an employee.");
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                // normalize phone (keep only digits)
                String phoneDigits = phoneStr.replaceAll("[^0-9]", "");

                // validate role
                String normalizedRole = (roleName == null ? "" : roleName.toUpperCase());
                if (!"EMPLOYEE".equals(normalizedRole)
                        && !"HOST".equals(normalizedRole)
                        && !"MANAGER".equals(normalizedRole)) {
                    normalizedRole = "EMPLOYEE"; // default
                }

                try {
                    userDao.createEmployee(restaurantId, firstName, lastName, email, phoneDigits, normalizedRole);
                    session.setAttribute("success", "Employee created successfully.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    String msg = ex.getMessage();
                    if (msg != null && msg.contains("users.email")) {
                        session.setAttribute("error", "Email is already in use by another user.");
                    } else if (msg != null && msg.contains("users.phone_number")) {
                        session.setAttribute("error", "Phone number is already in use by another user.");
                    } else {
                        session.setAttribute("error", "Could not create employee. Please check details.");
                    }
                }

                response.sendRedirect(request.getContextPath() + "/admin/employees");
                return;
            }
            if ("resetPassword".equals(action)) {
                String employeeIdStr = request.getParameter("employeeId");
                if (employeeIdStr == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                int employeeId = Integer.parseInt(employeeIdStr);

                User target = userDao.getUserById(employeeId);
                if (target == null) {
                    session.setAttribute("error", "Employee not found.");
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                if (target.getRestaurantId() == null || !restaurantId.equals(target.getRestaurantId())) {
                    session.setAttribute("error", "You cannot modify employees from another restaurant.");
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                // Simple reset: set to 'changeme'
                String newPassword = "changeme";
                userDao.resetUserPassword(employeeId, newPassword);
                session.setAttribute("success", "Password reset to 'changeme'. Ask the employee to change it on next login.");

                response.sendRedirect(request.getContextPath() + "/admin/employees");
                return;
            }

            // ---------- TOGGLE ACTIVE ----------
            if ("toggleActive".equals(action)) {
                String employeeIdStr = request.getParameter("employeeId");
                if (employeeIdStr == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                int employeeId = Integer.parseInt(employeeIdStr);

                User target = userDao.getUserById(employeeId);
                if (target == null) {
                    session.setAttribute("error", "Employee not found.");
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                if (target.getRestaurantId() == null || !restaurantId.equals(target.getRestaurantId())) {
                    session.setAttribute("error", "You cannot modify employees from another restaurant.");
                    response.sendRedirect(request.getContextPath() + "/admin/employees");
                    return;
                }

                boolean newStatus = !target.isActive();
                userDao.updateUserActiveStatus(employeeId, newStatus);
                String msg = newStatus ? "Employee activated." : "Employee deactivated.";
                session.setAttribute("success", msg);

                response.sendRedirect(request.getContextPath() + "/admin/employees");
                return;
            }

            // Unknown action
            session.setAttribute("error", "Unknown action: " + action);
            response.sendRedirect(request.getContextPath() + "/admin/employees");

        } catch (NumberFormatException e) {
            session.setAttribute("error", "Invalid employee id.");
            response.sendRedirect(request.getContextPath() + "/admin/employees");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Failed to update employee.");
            response.sendRedirect(request.getContextPath() + "/admin/employees");
        }
    }


}
