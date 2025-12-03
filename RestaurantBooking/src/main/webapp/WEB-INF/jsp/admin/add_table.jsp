<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<title>Add Restaurant Table</title>
<link rel="stylesheet" href="<%=request.getContextPath()%>/css/style.css">
<meta charset="UTF-8">
<script>
        setTimeout(() => {
            const msg = document.getElementById('alertMessage');
            if (msg) {
                msg.style.transition = 'opacity 1s ease';
                msg.style.opacity = '0';
                setTimeout(() => {
                    msg.style.display = 'none';
                }, 1000);
            }
        }, 3000);
    </script>
</head>

<body>
	<div class="home-link">
	    <jsp:include page="/WEB-INF/jsp/header.jsp"/>
	  </div>
	  <jsp:include page="/WEB-INF/jsp/admin/back_to_dashboard.jsp" />
	<h2>Add New Table</h2>
    <form action="<%=request.getContextPath()%>/admin/add_table" method="post">
    	<input type="hidden" name="csrf_token" value="${csrfToken}" />
    	
        <label>Table Number:</label>
        <input type="number" name="tableNumber" required><br><br>
        

        <label>Minimum Capacity:</label>
        <input type="number" name="minCapacity" min="1" required><br><br>

        <label>Max Capacity:</label>
        <input type="number" name="maxCapacity" min="1" required><br><br>

        <label>Combine with Others:</label>
        <input type="checkbox" name="canCombine"><br><br>

        <button type="submit">Add Table</button>
        
    </form>

    <c:if test="${not empty sessionScope.tableMessage}">
    <p id="alertMessage" style="color:green;">${sessionScope.tableMessage}</p>
    <c:remove var="tableMessage" scope="session" />
	</c:if>
	
	<c:if test="${not empty sessionScope.tableError}">
	    <p id="alertMessage" style="color:red;">${sessionScope.tableError}</p>
	    <c:remove var="tableError" scope="session" />
    </c:if>

</body>
</html>