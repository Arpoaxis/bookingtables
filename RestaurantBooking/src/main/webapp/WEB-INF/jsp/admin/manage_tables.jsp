<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Tables</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>

<div class="dashboard-container">
    <h1>Manage Tables</h1>

	<c:if test="${not empty sessionScope.tableMessage}">
    	<p style="color:green;">${sessionScope.tableMessage}</p>
    	<c:remove var="tableMessage" scope="session" />
	</c:if>

	<c:if test="${not empty sessionScope.tableError}">
    	<p style="color:red;">${sessionScope.tableError}</p>
    	<c:remove var="tableError" scope="session" />
	</c:if>

	<c:if test="${not empty sessionScope.lastDeletedTable}">
    	<div style="margin: 10px 0; padding: 8px; border: 1px solid #999; background:#f9f9f9;">
        	<p>
            	Last deleted table:
            	#${sessionScope.lastDeletedTable.tableNumber}
            	(min ${sessionScope.lastDeletedTable.minCapacity},
            	 max ${sessionScope.lastDeletedTable.maxCapacity})
        	</p>
        	<form action="${pageContext.request.contextPath}/admin/undo_delete_table"
              	method="post" style="display:inline;">
            	<!-- CSRF token -->
            	<input type="hidden" name="csrf_token"
                   		value="${sessionScope.csrf_token}" />
            	<button type="submit">Undo delete</button>
        	</form>
    	</div>
	</c:if>
    

    <!-- Top actions -->
    <p>
        <a href="<c:url value='/admin/add_table'/>">Add New Table</a> |
        <a href="<c:url value='/admin/table_planner'/>">Table Planner (suggest tables)</a>
    </p>

    <!-- Any error passed from the servlet -->
    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>


    <h2>All Restaurant Tables</h2>

    <c:choose>
        <c:when test="${empty tables}">
            <p>No tables found in the system.</p>
        </c:when>
        <c:otherwise>
            <table border="1" cellpadding="5" cellspacing="0">
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Table #</th>
                    <th>Min Capacity</th>
                    <th>Max Capacity</th>
                    <th>Can Combine</th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="t" items="${tables}">
    				<tr>
        				<td>${t.tableId}</td>
        				<td>${t.tableNumber}</td>
        				<td>${t.minCapacity}</td>
        				<td>${t.maxCapacity}</td>
        				<td>
            				<c:choose>
                				<c:when test="${t.canCombine}">Yes</c:when>
                				<c:otherwise>No</c:otherwise>
            				</c:choose>
        				</td>
        				<td>
            				<form action="${pageContext.request.contextPath}/admin/delete_table"
                 				method="post"
                  				style="display:inline;">
                			<input type="hidden" name="tableId" value="${t.tableId}" />
                			<!-- CSRF token from session -->
                			<input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}" />
                			<button type="submit">Delete</button>
            				</form>
        				</td>
    				</tr>
				</c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>
</div>

</body>
</html>
