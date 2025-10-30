<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>

<head>
    <title>Register</title>
   <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
    </head>
    <body> 
    <div class="home-link">
	 <p><a href="<%=request.getContextPath()%>/jsp/index.jsp">Home</a></p></div>
	 <div class="register-container">
    	<h1>Register</h1>
    	<form action="<%= request.getContextPath() %>/register" method="post">
    		<Label for="username">Username</Label>
    		<input type="text" name="username" id="username" required>
    		<br>
    		<Label for="password">Password</Label>
    		<input type="password" name="password" id="password" required>
    		<br>
    		<Label for="confirmPassword">Confirm Password</Label>
    		<input type="password" name="confirmPassword" id="confirmPassword" required>
    		<br>
    		<input type="submit" value="Register">
    		<% if (request.getAttribute("error") != null) { %> <p style="color:red;"><%= request.getAttribute("error") %></p> <% } %>
    	</form>
    	<p>Already have an account then <a href="<%= request.getContextPath() %>/jsp/Login/login_page.jsp">Login here</a></p>
    </div>
    </body>
   

</html>