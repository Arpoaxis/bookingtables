<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Administrator Dashboard</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">

<jsp:include page="/WEB-INF/jsp/header.jsp" />

<div class="dashboard-main">

    <!-- Header -->
    <div class="dashboard-header">
        <h1>Administrator Dashboard</h1>
        <p class="dashboard-subtitle">
            Welcome,
            <strong>${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong>
            (<c:out value="${sessionScope.user.email}" />)
        </p>
    </div>

    <!-- Any error message from the servlet -->
    <c:if test="${not empty reportError}">
        <p class="flash-message flash-error">${reportError}</p>
    </c:if>

    <!-- Main grid: left = metrics + status, right = management -->
    <div class="dashboard-grid">

        <!-- LEFT COLUMN -->
        <div>

            <!-- Summary metrics row -->
            <div class="dashboard-card dashboard-cards-row">
                <div class="metric-card">
                    <p class="metric-label">Total bookings</p>
                    <p class="metric-number">${totalBookings}</p>
                </div>
                <div class="metric-card">
                    <p class="metric-label">Bookings today</p>
                    <p class="metric-number">${bookingsToday}</p>
                </div>
                <div class="metric-card">
                    <p class="metric-label">Distinct customers</p>
                    <p class="metric-number">${distinctCustomers}</p>
                </div>
            </div>

            <!-- Bookings by status -->
            <div class="dashboard-card wide-card">
                <h2>Bookings by status</h2>
                <table class="status-table">
                    <thead>
                    <tr>
                        <th>Status</th>
                        <th>Count</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>PENDING</td>
                        <td>${statusCounts['PENDING']}</td>
                    </tr>
                    <tr>
                        <td>CONFIRMED</td>
                        <td>${statusCounts['CONFIRMED']}</td>
                    </tr>
                    <tr>
                        <td>SEATED</td>
                        <td>${statusCounts['SEATED']}</td>
                    </tr>
                    <tr>
                        <td>CANCELLED</td>
                        <td>${statusCounts['CANCELLED']}</td>
                    </tr>
                    </tbody>
                </table>
            </div>

        </div>

        <!-- RIGHT COLUMN: MANAGEMENT -->
        <div>
            <div class="dashboard-card management-card">
                <h2>Management</h2>
                <ul class="management-list">
                    <li>
                        <a class="primary-link"
                           href="<c:url value='/admin/manage_tables'/>">
                            Manage tables
                        </a>
                    </li>
                    <li>
                        <a class="primary-link"
                           href="<c:url value='/admin/floor_plan'/>">
                            View floor plan (beta)
                        </a>
                    </li>
                    <li>
                        <a class="primary-link"
                           href="<c:url value='/admin/employees'/>">
                            Manage employees
                        </a>
                    </li>
                </ul>

                <hr class="management-separator"/>

                <a class="danger-link" href="<c:url value='/logout'/>">
                    Logout
                </a>
            </div>
        </div>

    </div><!-- /dashboard-grid -->
</div><!-- /dashboard-main -->

</body>
</html>
