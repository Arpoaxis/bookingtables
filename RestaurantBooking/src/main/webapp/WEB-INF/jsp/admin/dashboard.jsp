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

<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>

<div class="dashboard-main">

    <!-- Header -->
    <div class="dashboard-header">
        <h1>Administrator Dashboard</h1>
        <p class="dashboard-subtitle">
            Welcome, <strong>${sessionScope.email}</strong>
        </p>
    </div>

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
                    <c:forEach var="entry" items="${statusCounts}">
                        <tr>
                            <td>${entry.key}</td>
                            <td>${entry.value}</td>
                        </tr>
                    </c:forEach>
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
