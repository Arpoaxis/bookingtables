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
    <c:when test="${sessionScope.email != null}">
      <p>Welcome, ${sessionScope.email}!</p>
      <c:if test="$sessionScope.user.accountType == 'Admin'}">
      	<a href="${pageContext.request.contextPath}/jsp/Admin/admin_dashboard.jsp">Administrator Dashboard</a>
      </c:if>
	  <c:if test="$sessionScope.user.accountType != 'ADMIN'}">
      	<p><a href="${pageContext.request.contextPath}/jsp/index.jsp">Homepage</a></p>
      </c:if>
      
      <p><a href="${pageContext.request.contextPath}/logout ">Logout</a></p>
    </c:when>

    <c:otherwise>
      <p>Please <a href="${pageContext.request.contextPath}/login">login</a> to continue.</p>
    </c:otherwise>
  </c:choose>
</body>
</html>