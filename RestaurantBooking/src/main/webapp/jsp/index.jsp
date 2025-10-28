<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html lang="en">
<head> 
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Website Page</title>
    
</head>
<body>
	<% String email = (String) session.getAttribute("email");%>
	<h1>Welcome to Restaurant Booking!</h1>
	<% if (email != null) { %>
	<c:if test="${sessionScope.user.accountType == 'Business'}">
    <c:url value="/admin/dashboard" var="adminUrl" />
    <a href="${adminUrl}">Administrator Dash-board</a>
</c:if>
	<p>Welcome, <%= email %>!</p>
	<p><a href="<%=request.getContextPath()%>/LogoutServlet">Logout</a></p>
	<% } else { %>
	<!-- User is not logged in -->
	<p>Please <a href="<%=request.getContextPath()%>/jsp/Login/login_page.jsp">login</a> to continue.</p>
	<% } %>
</body>
</html>