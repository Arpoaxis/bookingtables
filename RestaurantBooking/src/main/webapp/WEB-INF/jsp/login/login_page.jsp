<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
// If user is already logged in, send them to the home page instead of showing login form
if (session != null && session.getAttribute("user") != null) {
    response.sendRedirect(request.getContextPath() + "/");
    return;
}
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login Page</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>

    <div class="home-link">
        <p style="margin:10px">
            <a href="<c:url value='/'/>">Home</a>
        </p>
    </div>

    <div class="login-container">
        <h1>Log In</h1>

        <%-- Form posts to your LoginServlet --%>
        <form action="<c:url value='/login'/>" method="post">

            <div class="input-row">
                <label for="email">Email:</label>
                <input type="text" name="email" id="email"/>
            </div>

            <div class="input-row">
                <label for="password">Password:</label>
                <input type="password" name="password" id="password"/>
            </div>

            <input type="submit" value="Login"/>

            <%-- error message from LoginServlet --%>
            <c:if test="${not empty error}">
                <p style="color:red;">${error}</p>
            </c:if>
            <c:if test="${not empty sessionScope.loginMessage}">
			    <p style="color:red;">${sessionScope.loginMessage}</p>
			    <c:remove var="loginMessage" scope="session"/>
			</c:if>
        </form>

        <div class="register-link">
            <p>
                Don't have an account?
                <a href="<c:url value='/register'/>">Register</a>
            </p>
        </div>
    </div>

</body>
</html>