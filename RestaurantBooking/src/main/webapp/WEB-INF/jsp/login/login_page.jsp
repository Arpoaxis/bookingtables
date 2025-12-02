<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Login</title>
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
			
            <h1 class="auth-title">Sign In</h1>
            <p class="auth-subtitle">
                Log in to access your reservations.
            </p>

            <!-- Error message -->
            <c:if test="${not empty error}">
                <div class="auth-error">${error}</div>
            </c:if>

            <form action="<c:url value='/login'/>" method="post" class="auth-form">

                <label for="email">Email</label>
                <input type="email" name="email" required>

                <label for="password">Password</label>
                <input type="password" name="password" required>

                <button type="submit" class="auth-primary-button" style="width:100%;">
                    Login
                </button>
            </form>

            <p class="auth-footer-text">
                Don’t have an account?
                <a href="<c:url value='/register'/>">Register</a>
            </p>

        </div>
    </div>

</body>
</html>