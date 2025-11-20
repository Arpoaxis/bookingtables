<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Restaurant Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
	
    <jsp:include page="/WEB-INF/jsp/header.jsp"/>
    <h1>Restaurant Booking</h1>

    <%-- USER LOGIN / ROLE LOGIC START --%>
    <c:choose>

        <c:when test="${not empty sessionScope.user}">
            <%-- LOGGED IN USER AREA --%>

            <p>
                Welcome,
                <c:out value="${sessionScope.user.firstName}" />
                (<c:out value="${sessionScope.user.email}" />)
            </p>

            <c:if test="${sessionScope.role == 'ADMIN' || sessionScope.role == 'MANAGER'}">
                <a href="<c:url value='/admin/dashboard'/>">Administrator Dashboard</a>
            </c:if>

            <c:if test="${sessionScope.role == 'CUSTOMER'}">
                <a href="<c:url value='/'/>">Home</a>
            </c:if>

            <p><a href="<c:url value='/logout'/>">Logout</a></p>
        </c:when>

        <c:otherwise>
            <%-- NOT LOGGED IN MESSAGE --%>
            <p>Please <a href="<c:url value='/login'/>">login</a> to continue.</p>
        </c:otherwise>

    </c:choose>
    <%-- USER LOGIN / ROLE LOGIC END --%>


    <%-- RESTAURANT LIST --%>
    <div class="restaurant-list-container">
        <h2>Choose a Restaurant</h2>

        <c:if test="${not empty error}">
            <p style="color:red;">${error}</p>
        </c:if>

        <c:if test="${empty restaurants}">
            <p>No restaurants found.</p>
        </c:if>

        <div class="restaurant-grid">

            <c:forEach var="r" items="${restaurants}">
                <div class="restaurant-card">

                 <%-- Do not have images yet 
                 <img src="<c:url value='/images/restaurant_default.jpg' />"
				     alt="Restaurant image"
				     class="restaurant-img"--%>


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
    <title>Restaurant Booking</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="auth-body">
<div class="auth-wrapper">
    <div class="auth-card">
        <h1 class="auth-title">Welcome to Restaurant Booking</h1>

        <c:choose>
            <c:when test="${not empty sessionScope.email}">
                <p class="auth-subtitle">
                    You are logged in as <strong>${sessionScope.email}</strong>.
                </p>

                <div class="auth-actions">
                    <c:if test="${sessionScope.role == 'ADMIN' or sessionScope.role == 'MANAGER'}">
                        <a class="auth-primary-button"
                           href="<c:url value='/admin/dashboard'/>">
                            Go to Dashboard
                        </a>
                    </c:if>

                    <a class="auth-link-button"
                       href="<c:url value='/logout'/>">
                        Logout
                    </a>
                </div>
            </c:when>

            <c:otherwise>
                <p class="auth-subtitle">
                    Book, manage, and plan tables for your restaurant.
                    Please log in or create an account to continue.
                </p>

                <div class="auth-actions">
                    <a class="auth-primary-button"
                       href="<c:url value='/login'/>">
                        Login
                    </a>
                    <a class="auth-link-button"
                       href="<c:url value='/register'/>">
                        Register
                    </a>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>