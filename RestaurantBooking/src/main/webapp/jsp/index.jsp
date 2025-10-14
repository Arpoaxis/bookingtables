<!DOCTYPE html>
<html lang="en">
<head> 
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Website Page</title>
    
</head>
<body>
	<% String username = (String) session.getAttribute("username");%>
	<h1>Welcome to Restaurant Booking!</h1>
	<% if (username != null) { %>
	<!-- User is logged in-->
	<p>Welcome, <%= username %>!</p>
	<p><a href="<%=request.getContextPath()%>/LogoutServlet">Logout</a></p>
	<% } else { %>
	<!-- User is not logged in -->
	<p>Please <a href="<%=request.getContextPath()%>/jsp/Login/login_page.jsp">login</a> to continue.</p>
	<% } %>
</body>
</html>