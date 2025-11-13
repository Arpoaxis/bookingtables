<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Restaurant Booking</title>
  <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
  <h1>Welcome to Restaurant Booking!</h1>

  <c:choose>
  <c:when test="${not empty sessionScope.email}">
    	<c:if test="${sessionScope.role == 'MANAGER' || sessionScope.role == 'ADMIN'}">
      		<a href="<c:url value='/admin/dashboard'/>">Administrator Dashboard</a>
    	</c:if>
    	<p>Welcome, ${sessionScope.email}!</p>
    	<p><a href="<c:url value='/logout'/>">Logout</a></p>
  	</c:when>
  	<c:otherwise>
   			<p>Please <a href="<c:url value='/login'/>">login</a> to continue.</p>
  		</c:otherwise>
	</c:choose>

</body>
</html>
