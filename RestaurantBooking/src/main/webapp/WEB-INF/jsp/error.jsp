<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Error</title>
    <style>
        body { font-family: Arial, sans-serif; padding: 20px; }
        h1 { color: #c00; }
        .msg { margin-top: 10px; font-size: 1.1em; }
        a { margin-top: 20px; display: inline-block; }
    </style>
</head>
<body>
<h1>Something went wrong</h1>

<div class="msg">
    <c:if test="${not empty error}">
        ${error}
    </c:if>
    <c:if test="${empty error}">
        An unexpected error occurred.
    </c:if>
</div>

<a href="${pageContext.request.contextPath}/admin/dashboard">Back to Dashboard</a>
</body>
</html>
