<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Login Page</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body> 
	<div class="home-link">
	 <p style="margin:10px"><a href="<%=request.getContextPath()%>/jsp/index.jsp">Home</a></p></div>
<div class="login-container">
	
	<h1> Log In</h1>
	<form action="<%=request.getContextPath()%>/login" method="post">
	<div class="input-row">
	    <label for="email">Email:</label><input type="email" id="email" name="email" required>
	</div>
	<div class="input-row">
	    <label for="password">Password:</label><input type= "password" name = "password" required/>
	    </div>
	    <input type="submit" value="Login"/>
		
	<% if (request.getParameter("error") != null) { %>
	    <div class="error-message">
	        <p style="color:red">Invalid email or password.</p>
	    </div>
	<% } %>
	    </form>
	    
	 <div class="register-link">
	 <p> 
	 	Don't have an account: <a href="<%= request.getContextPath() %>/jsp/Login/register.jsp">Register</a>
	 	</p>
	 
	 </div>
	 </div>


</body>
</html>