<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Forgot Password</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">

<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>

<div class="dashboard-main auth-main">
    <div class="auth-card">
        <h1>Forgot Password</h1>
        <p class="dashboard-subtitle">
            Enter your email and we’ll send you a temporary password.
        </p>

        <c:if test="${not empty error}">
            <p class="flash-message flash-error">
                <c:out value="${error}" />
            </p>
        </c:if>

        <form method="post" action="<c:url value='/login/forgot'/>">
            <div class="form-row">
                <label>Email</label>
                <input type="email" name="email" required>
            </div>

            <div class="form-row">
                <button type="submit" class="primary-link button-link">
                    Send reset email
                </button>
            </div>
        </form>

        <p style="margin-top:1rem;">
            <a href="<c:url value='/login'/>">Back to login</a>
        </p>
    </div>
</div>

</body>
</html>
