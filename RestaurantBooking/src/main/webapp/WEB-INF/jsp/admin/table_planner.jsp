<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Table Planner</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>

<div class="dashboard-container">
    <h1>Table Planner</h1>

    <form action="<c:url value='/admin/table_planner'/>" method="post">
    	<!-- CSRF protection: name must be 'csrf_token' to match the filter -->
    	<input type="hidden" name="csrf_token" value="${csrfToken}" />

    	<label>Party size:</label>
    	<input type="number" name="partySize" min="1"
           value="${partySize}" required />
    	<button type="submit">Suggest Tables</button>
	</form>


    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <h2>Current Tables</h2>
    <table border="1">
        <thead>
        <tr>
            <th>ID</th>
            <th>Table #</th>
            <th>Min</th>
            <th>Max</th>
            <th>Can Combine</th>
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
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <c:if test="${not empty suggestedTables}">
        <h2>Suggested Combination for party of ${partySize}</h2>
        <ul>
            <c:forEach var="t" items="${suggestedTables}">
                <li>
                    Table ${t.tableNumber}
                    (min ${t.minCapacity}, max ${t.maxCapacity},
                    <c:choose>
                        <c:when test="${t.canCombine}">can combine</c:when>
                        <c:otherwise>no combine</c:otherwise>
                    </c:choose>)
                </li>
            </c:forEach>
        </ul>
    </c:if>

</div>
</body>
</html>
