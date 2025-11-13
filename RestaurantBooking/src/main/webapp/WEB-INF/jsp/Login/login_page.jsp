<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
    <div class="home-link">
        <!-- Home goes to '/', which hits your public index.jsp and forwards to WEB-INF/jsp/index.jsp -->
        <p style="margin:10px">
            <a href="<c:url value='/'/>">Home</a>
        </p>
    </div>

    <div class="login-container">
        <h1>Log In</h1>

        <!-- POST to the /login servlet -->
        <form action="<c:url value='/login'/>" method="post">
            <div class="input-row">
                <label for="email">Email:</label>
                <input type="text" name="email" id="email" />
            </div>

            <div class="input-row">
                <label for="password">Password:</label>
                <input type="password" name="password" id="password" />
            </div>

            <input type="submit" value="Login" />

            <c:if test="${not empty error}">
                <p style="color:red;">${error}</p>
            </c:if>
        </form>

        <div class="register-link">
            <p>
                Don't have an account?
                <!-- Route through a RegisterServlet mapped to /register -->
                <a href="<c:url value='/register'/>">Register</a>
            </p>
        </div>
    </div>
</body>
</html>
