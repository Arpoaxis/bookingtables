<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Register</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>

<body>

    <div class="home-link">
        <p><a href="<c:url value='/'/>">Home</a></p>
    </div>

    <div class="register-container">
        <h1>Register</h1>

        <!-- Error message from RegisterServlet -->
        <c:if test="${not empty error}">
            <p style="color:red;">${error}</p>
        </c:if>

        <!-- Registration Form -->
        <form action="<c:url value='/register'/>" method="post">

            <label for="firstName">First Name:</label>
            <input type="text" name="firstName" id="firstName" required>
            <br>

            <label for="lastName">Last Name:</label>
            <input type="text" name="lastName" id="lastName" required>
            <br>

            <label for="username">Username:</label>
            <input type="text" name="username" id="username" required>
            <br>

            <label for="email">Email:</label>
            <input type="email" name="email" id="email" required>
            <br>

            <label for="phoneNumber">Phone Number:</label>
            <input type="tel" name="phoneNumber" id="phoneNumber" required>
            <br>

            <label for="password">Password:</label>
            <input type="password" name="password" id="password" required>
            <br>

            <label for="confirmPassword">Confirm Password:</label>
            <input type="password" name="confirmPassword" id="confirmPassword" required>
            <br>

            <input type="submit" value="Register">
        </form>

        <p>Already have an account? <a href="<c:url value='/login'/>">Login here</a></p>
    </div>

</body>
</html>
