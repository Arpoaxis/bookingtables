<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Reservations</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="dashboard-body">

    <jsp:include page="/WEB-INF/jsp/header.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-card">

            <h1>My Reservations</h1>
            <p class="dashboard-subtitle">View and manage your upcoming reservations.</p>

            <hr class="management-separator">

            <!-- Error message -->
            <c:if test="${not empty error}">
                <div class="error-message">${error}</div>
            </c:if>

            <!-- No bookings -->
            <c:if test="${empty bookings}">
                <p>You have no active reservations.</p>
            </c:if>

            <!-- Reservation Table -->
            <c:if test="${not empty bookings}">
            <div class="table-wrapper">
                <table class="styled-table">

                    <thead>
                        <tr>
                            <th>Restaurant</th>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Guests</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>

                    <tbody>
                    <c:forEach var="b" items="${bookings}">
                        <c:if test="${b.status != 'CANCELLED'}">
                            <tr>
                                <td>${b.restaurantName}</td>
                                <td>${b.date}</td>
                                <td>${b.time}</td>
                                <td>${b.guests}</td>
                                <td>
                                    <span class="status-badge status-${fn:toLowerCase(b.status)}">
                                        ${b.status}
                                    </span>
                                </td>

                                <td>
                                    <form action="<c:url value='/booking/cancel'/>" method="post">
                                        <input type="hidden" name="bookingId" value="${b.bookingId}">
                                        <button type="submit" class="auth-link-button danger-btn">
                                            Cancel
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                    </tbody>

                </table>
            </div>
            </c:if>

            <div style="margin-top:20px;">
                <a href="<c:url value='/'/>" class="auth-link-button">Back to Restaurants</a>
            </div>

        </div>

    </div>

</body>
</html>
