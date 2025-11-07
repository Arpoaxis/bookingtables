<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
	    <Label for="email">Email:</Label>
	    <input type="text" name="email"/>
	    </div>
	<div class="input-row">
	    <label for="password">Password:</label>
	    <input type= "password" name = "password"/>
	</div>
	    <input type="submit" value="Login"/>
		<c:if test="${not empty error}">
		    <p style="color:red;">${error}</p>
		</c:if>
	
	    </form>
	    
	 <div class="register-link">
	 <p> 
 	 	<!-- Use a context-path-aware link so the JSP is found after deployment -->
 	 	<!--Don't have an account: <a href="<%=request.getContextPath()%>/jsp/Login/account_type.jsp">Register</a> -->
 	 	Don't have an account:<a href="<%=request.getContextPath()%>/jsp/Login/register.jsp">Register</a>
 	 	</p>
 	 
 	 </div>
 	 </div>


</body>
</html>