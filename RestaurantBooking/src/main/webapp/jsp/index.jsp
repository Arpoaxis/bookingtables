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
  <h1>Welcome to Restaurant Booking!</h1>

  <!-- user is logged in if we have an email in session -->
  <c:choose>
    <c:when test="${not empty sessionScope.email}">
      <!-- Optional admin link for business accounts -->
      <c:if test="${sessionScope.user != null and sessionScope.user.accountType == 'Business'}">
        <c:url value="/admin/dashboard" var="adminUrl"/>
        <a href="${adminUrl}">Administrator Dashboard</a>
      </c:if>

      <p>Welcome, ${sessionScope.email}!</p>
      <p><a href="${pageContext.request.contextPath}/LogoutServlet">Logout</a></p>
    </c:when>

    <c:otherwise>
      <p>Please <a href="${pageContext.request.contextPath}/jsp/Login/login_page.jsp">login</a> to continue.</p>
    </c:otherwise>
  </c:choose>
</body>
</html>
