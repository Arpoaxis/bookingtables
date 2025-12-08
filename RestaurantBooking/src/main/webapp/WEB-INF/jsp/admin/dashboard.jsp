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

    <!-- Error banner (stats or bookings load issues) -->
    <c:if test="${not empty reportError}">
        <p class="flash-message flash-error">
            <c:out value="${reportError}" />
        </p>
    </c:if>

    <div class="dashboard-grid">

        <!-- LEFT COLUMN -->
        <div>

            <!-- Summary metrics row -->
            <div class="dashboard-card dashboard-cards-row">
                <div class="metric-card">
                    <p class="metric-label">Total bookings</p>
                    <p class="metric-number">
                        <a href="?view=all" class="metric-link">
                            <c:out value="${totalBookings}" />
                        </a>
                    </p>
                </div>
                <div class="metric-card">
                    <p class="metric-label">Bookings today</p>
                    <p class="metric-number">
                        <a href="?view=today" class="metric-link">
                            <c:out value="${bookingsToday}" />
                        </a>
                    </p>
                </div>
                <div class="metric-card">
                    <p class="metric-label">Distinct customers</p>
                    <p class="metric-number">
                        <c:out value="${distinctCustomers}" />
                    </p>
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
                        <td><c:out value="${statusCounts['PENDING']}" /></td>
                    </tr>
                    <tr>
                        <td>CONFIRMED</td>
                        <td><c:out value="${statusCounts['CONFIRMED']}" /></td>
                    </tr>
                    <tr>
                        <td>SEATED</td>
                        <td><c:out value="${statusCounts['SEATED']}" /></td>
                    </tr>
                    <tr>
                        <td>CANCELLED</td>
                        <td><c:out value="${statusCounts['CANCELLED']}" /></td>
                    </tr>
                    </tbody>
                </table>
            </div>

            <!-- Bookings list when a metric is clicked -->
            <c:if test="${param.view == 'today' || param.view == 'all'}">
                <div class="dashboard-card wide-card">
                    <div class="dashboard-card-header-row">
                        <h2>
                            <c:choose>
                                <c:when test="${param.view == 'today'}">
                                    Today&#39;s bookings
                                </c:when>
                                <c:when test="${param.view == 'all'}">
                                    All bookings
                                </c:when>
                                <c:otherwise>
                                    Bookings
                                </c:otherwise>
                            </c:choose>
                        </h2>
                        <a href="<c:url value='/admin/dashboard'/>" class="primary-link">Hide list</a>
                    </div>

                    <c:choose>
                        <c:when test="${empty bookings}">
                            <p>No bookings found for this view.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="table-wrapper">
                                <table class="status-table">
                                    <thead>
                                    <tr>
                                        <th>Date</th>
                                        <th>Time</th>
                                        <th>Customer</th>
                                        <th>Guests</th>
                                        <th>Status</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="b" items="${bookings}">
                                        <tr>
                                            <!-- NOTE: use 'date', not 'bookingDate' -->
                                            <td><c:out value="${b.date}" /></td>
                                            <td><c:out value="${b.displayTime}" /></td>
                                            <td><c:out value="${b.customerFullName}" /></td>
                                            <td><c:out value="${b.guests}" /></td>
                                            <td><c:out value="${b.status}" /></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </c:if>

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
