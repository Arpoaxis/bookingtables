<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Register</title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/css/style.css">
</head>
<body> 
	<div class="home-link">
	<p style="margin:10px"><a href="<%=request.getContextPath()%>/jsp/index.jsp">Home</a></p></div>
    <div class="register-container">
    	<h1>Register</h1>
    	
    	<form action="<%= request.getContextPath() %>/selectAccount" method="post">
    	
    		<label><input type="radio" name="account_type" value="personal" required> Personal</label>
			<label><input type="radio" name="account_type" value="business" required> Business</label>
            <br>
    		

    		<input type="submit" value="Continue to Register">
    		
    	</form>
    </div>
    </body>
</html>