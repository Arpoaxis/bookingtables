package com.restaurant.filter;

import java.io.IOException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.restaurant.model.User;

@WebFilter(urlPatterns = { "/admin/*" })
public class AdminAuthorizationFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		HttpSession session = httpRequest.getSession(false);
		User user = (session != null) ? (User) session.getAttribute("user") : null;

		if (user == null) {
			// Not logged in → send to login
			String ctx = httpRequest.getContextPath();
			httpResponse.sendRedirect(ctx + "/login?error=unauthorized");
			return;
		}

		String role = (user.getAccountType() == null) ? "" : user.getAccountType().toUpperCase();

		String ctx = httpRequest.getContextPath(); // e.g. /RestaurantBooking
		String uri = httpRequest.getRequestURI(); // e.g. /RestaurantBooking/admin/employees
		String path = uri.substring(ctx.length()); // e.g. /admin/employees

		boolean isAdmin = "ADMIN".equals(role);
		boolean isManager = "MANAGER".equals(role);
		boolean isHost = "HOST".equals(role);
		boolean isEmployee = "EMPLOYEE".equals(role);
		boolean isAdminOrMgr = isAdmin || isManager;
		boolean isStaff = isAdmin || isManager || isHost || isEmployee;

		// --- Staff features (floor plan & table management) ---
		boolean staffFeature = path.startsWith("/admin/floor_plan") || path.startsWith("/admin/add_table")
				|| path.startsWith("/admin/edit_table") || path.startsWith("/admin/delete_table")
				|| path.startsWith("/admin/manage_tables");

		// --- Employees page: ADMIN + MANAGER ---
		if (path.startsWith("/admin/employees")) {
			if (isAdminOrMgr) {
				chain.doFilter(request, response);
			} else {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
			}
			return;
		}

		// --- Staff features: any staff role ---
		if (staffFeature) {
			if (isStaff) {
				chain.doFilter(request, response);
			} else {
				httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
			}
			return;
		}

		// --- Default for any other /admin/* page: ADMIN or MANAGER ---
		if (isAdminOrMgr) {
			chain.doFilter(request, response);
		} else {
			httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
		}
	}

	@Override
	public void init(FilterConfig filterConfig) {
	}

	@Override
	public void destroy() {
	}
}
