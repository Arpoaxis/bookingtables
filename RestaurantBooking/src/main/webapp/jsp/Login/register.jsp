<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
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
    		<Label for="email">Email:</Label>
        	<input type="email" name="email" id="email" required>
        	<br>
        	
            <c:if test="${accountType == 'personal'}">
            <input type="hidden" name="account_type" value="personal" />
            <p>Selected account type: <c:out value="${accountType}"/></p>
        	
        	<Label for="FirstName">First Name:</Label>
        	<input type="text" name="FirstName" id="FirstName" required>
        	<br>
        	<Label for="LastName">Last Name:</Label>
        	<input type="text" name="LastName" id="LastName" required>
        	<br>
        	</c:if>
    		<Label for="PhoneNumber">Phone Number:</Label>
    		<input type="text" name="PhoneNumber" id="PhoneNumber" required>
    	    <br>
    		<Label for="password">Password:</Label>
    		<input type="password" name="password" id="password" required>
    		<br>
    		<Label for="confirmPassword">Confirm Password:</Label>
    		<input type="password" name="confirmPassword" id="confirmPassword" required>
    		<input type="submit" value="Register">
    		<c:if test="${not empty error}">
		    	<p style="color:red;">${error}</p>
			</c:if>
    	</form>
    	<p>Already have an account then <a href="<%= request.getContextPath() %>/jsp/Login/login_page.jsp">Login here</a></p>
    </div>
    </body>
   

</html>