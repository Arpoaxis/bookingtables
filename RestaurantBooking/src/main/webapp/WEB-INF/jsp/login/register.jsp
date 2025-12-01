<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Account - Restaurant Booking</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="auth-body">
	<div class="auth-top-link">
	    <a href="${pageContext.request.contextPath}/">
	        ⟵ Back to Home
	    </a>
	</div>
<div class="auth-wrapper">
    <div class="auth-card">

        <h1 class="auth-title">Create your account</h1>
        <p class="auth-subtitle">
            Fill in your details to start booking and managing tables.
        </p>

        <form class="auth-form" action="<c:url value='/register'/>" method="post">

            <div>
                <label for="firstName">First Name</label>
                <input id="firstName" name="firstName" type="text"
                       value="${param.firstName}" required>
            </div>

            <div>
                <label for="lastName">Last Name</label>
                <input id="lastName" name="lastName" type="text"
                       value="${param.lastName}" required>
            </div>

            <div>
                <label for="username">Username</label>
                <input id="username" name="username" type="text"
                       value="${param.username}" required>
            </div>

            <div>
                <label for="email">Email</label>
                <input id="email" name="email" type="email"
                       value="${param.email}" required>
            </div>

            <div>
                <label for="phoneNumber">Phone Number</label>
                <input id="phoneNumber" name="phoneNumber" type="tel"
                       value="${param.phoneNumber}" required>
            </div>

            <div>
                <label for="password">Password</label>
                <input id="password" name="password" type="password" required>
            </div>

            <div>
                <label for="confirmPassword">Confirm Password</label>
                <input id="confirmPassword" name="confirmPassword" type="password" required>
            </div>

            <button type="submit" class="auth-primary-button" 
                    style="width:100%; margin-top:8px;">
                Register
            </button>
        </form>

        <c:if test="${not empty error}">
            <p class="auth-error">${error}</p>
        </c:if>

        <p class="auth-footer-text">
            Already have an account?
            <a href="<c:url value='/login'/>">Login</a>
        </p>

    </div>
</div>
</body>
</html>
