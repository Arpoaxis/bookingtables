<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
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
