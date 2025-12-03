<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Table</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
    <div class="home-link">
        <jsp:include page="/WEB-INF/jsp/header.jsp" />
    </div>
	<jsp:include page="/WEB-INF/jsp/admin/back_to_dashboard.jsp" />
    <div class="dashboard-container">
        <h2>Edit Table</h2>

        <c:if test="${not empty error}">
            <p style="color:red;">${error}</p>
        </c:if>

        <c:if test="${empty table}">
            <p style="color:red;">No table data available.</p>
            <p><a href="<c:url value='/admin/tables'/>">Back to tables</a></p>
        </c:if>

        <c:if test="${not empty table}">
            <form action="<c:url value='/admin/edit_table'/>" method="post">
                <!-- Keep ID hidden -->
                <input type="hidden" name="tableId" value="${table.tableId}" />
                <input type="hidden" name="csrf_token" value="${csrfToken}" />
                

                <label>Table Number:</label>
                <input type="number" name="tableNumber" value="${table.tableNumber}" required />
                <br><br>

                <label>Min Capacity:</label>
                <input type="number" name="minCapacity" value="${table.minCapacity}" min="1" required />
                <br><br>

                <label>Max Capacity:</label>
                <input type="number" name="maxCapacity" value="${table.maxCapacity}" min="1" required />
                <br><br>

                <label>Can Combine with Others:</label>
                <input type="checkbox" name="canCombine"
                    <c:if test="${table.canCombine}">checked</c:if> />
                <br><br>

                <button type="submit">Save Changes</button>
                <a href="<c:url value='/admin/tables'/>">Cancel</a>
            </form>
        </c:if>
    </div>
</body>
</html>
