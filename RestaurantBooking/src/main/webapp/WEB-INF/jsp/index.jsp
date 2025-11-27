<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Restaurant Booking</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="home-body">

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<div class="home-main">

    <%-- Center white card --%>
    <div class="auth-wrapper">
        <div class="auth-card">
            <h1 class="auth-title">Welcome to Restaurant Booking</h1>

            <c:choose>

                <%-- Logged in --%>
                <c:when test="${not empty sessionScope.email}">
                    <p class="auth-subtitle">
                        You are logged in as
                        <strong>${sessionScope.email}</strong>.
                    </p>

                    <div class="auth-actions">

                        <c:if test="${sessionScope.role == 'ADMIN' or sessionScope.role == 'MANAGER'}">
                            <a class="auth-primary-button" href="<c:url value='/admin/dashboard'/>">
                                Go to Dashboard
                            </a>
                        </c:if>

                        <a class="auth-link-button" href="<c:url value='/logout'/>">
                            Logout
                        </a>
                    </div>
                </c:when>

                <%-- Not logged in --%>
                <c:otherwise>
                    <p class="auth-subtitle">
                        Please log in to choose a restaurant and make a booking.
                    </p>

                    <div class="auth-actions">
                        <a class="auth-primary-button" href="<c:url value='/login'/>">
                            Login
                        </a>
                        <a class="auth-link-button" href="<c:url value='/register'/>">
                            Register
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>

        </div>
    </div>

    <%-- Restaurants underneath the card --%>
    <div class="restaurant-list-container">
        <h2 class="restaurant-list-title">Choose a Restaurant</h2>

        <c:if test="${empty restaurants}">
            <p style="text-align:center;">No restaurants found.</p>
        </c:if>

        <div class="restaurant-grid">
            <c:forEach var="r" items="${restaurants}">
                <div class="restaurant-card">
                    <div class="restaurant-info">
                        <h3>${r.name}</h3>
                        <p class="address">${r.address}</p>
                        <p class="desc">${r.description}</p>

                        <a class="view-btn"
                           href="<c:url value='/restaurant?id=${r.restaurantId}'/>">
                            View Restaurant
                        </a>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>

</div><%-- /.home-main --%>

</body>
</html>
