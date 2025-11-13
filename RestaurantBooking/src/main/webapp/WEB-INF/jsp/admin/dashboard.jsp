<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Admin Dashboard - Restaurant Booking</title>
  <link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
</head>

<body>
  <div class="home-link">
  
    <jsp:include page="/WEB-INF/jsp/header.jsp"/>
  </div>

  <div class="dashboard-container">
    <h1>Administrator Dashboard</h1>

    <c:if test="${sessionScope.user != null and sessionScope.user.accountType == 'ADMIN'}">
      <p>Welcome, ${sessionScope.user.email}</p>
      <hr>

      <h2>Management Options</h2>
      <ul>
        <li><a href="${pageContext.request.contextPath}/jsp/admin/add_table.jsp">Add New Table</a></li>
        <li><a href="${pageContext.request.contextPath}/jsp/admin/view_tables.jsp">View All Tables</a></li>
        <li><a href="${pageContext.request.contextPath}/jsp/admin/manage_users.jsp">Manage Tables</a></li>
      </ul>

      <p><a href="<c:url value='/logout'/>">Logout</a></p>

    </c:if>

    <c:if test="${sessionScope.user == null or sessionScope.user.accountType != 'ADMIN'}">
      	<p style="color:red;">Access Denied: You must be logged in as an administrator to view this page.</p>
		<a href="<c:url value='/login'/>">Login</a>

    </c:if>
  </div>
</body>
</html>