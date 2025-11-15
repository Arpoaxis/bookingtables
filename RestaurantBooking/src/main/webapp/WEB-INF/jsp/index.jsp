<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Website Page</title>
</head>
<body>
  <h1>Restaurant Booking</h1>

  <!-- user is logged in if we have an email in session -->
  <c:choose>
<c:when test="${sessionScope.user != null}">

      <p>Welcome, ${sessionScope.user.firstName}!</p>

      <!-- Admin Dashboard Link -->
      <c:if test="${sessionScope.user.accountType == 'ADMIN'}">
          <a href="${pageContext.request.contextPath}/jsp/admin/dashboard.jsp">Administrator Dashboard</a>
      </c:if>

      <!-- Customer Homepage Link -->
      <c:if test="${sessionScope.user.accountType == 'CUSTOMER'}">
          <a href="${pageContext.request.contextPath}/jsp/index.jsp">Homepage</a>
      </c:if>

      <p><a href="${pageContext.request.contextPath}/logout">Logout</a></p>
    </c:when>

    <c:otherwise>
      <p>Please <a href="${pageContext.request.contextPath}/login">login</a> to continue.</p>
    </c:otherwise>
  </c:choose>
</body>
</html>