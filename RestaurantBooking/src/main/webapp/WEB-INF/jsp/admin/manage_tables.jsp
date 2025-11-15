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
    <jsp:include page="/WEB-INF/jsp/header.jsp"/>
</div>

<div class="dashboard-container">
    <h1>Manage Tables</h1>

    <c:if test="${not empty error}">
        <p style="color:red;">${error}</p>
    </c:if>

    <c:if test="${empty tables}">
        <p>No tables found in the system.</p>
    </c:if>

    <c:if test="${not empty tables}">
        <table border="1">
            <thead>
            <tr>
                <th>ID</th>
                <th>Table #</th>
                <th>Min</th>
                <th>Max</th>
                <th>Can Combine</th>
                <!-- future: actions -->
                <!-- <th>Actions</th> -->
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
                    <%-- future: edit / delete / combine buttons here --%>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <p>
        <a href="<c:url value='/admin/add_table'/>">Add New Table</a> |
        <a href="<c:url value='/admin/table_planner'/>">Table Planner</a>
    </p>
</div>
</body>
</html>
