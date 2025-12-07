<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Table Planner</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">


    <jsp:include page="/WEB-INF/jsp/header.jsp" />

<jsp:include page="/WEB-INF/jsp/admin/back_to_dashboard.jsp" />

<div class="dashboard-main">
    <div class="dashboard-card">
        <h1>Table Planner</h1>

        <form action="<c:url value='/admin/table_planner'/>" method="post">
            <input type="hidden" name="csrf_token" value="${csrf_token}" />

            <label>Party size:</label>
            <input type="number" name="partySize" min="1"
                   value="${partySize}" required />

            <button type="submit" class="btn btn-primary" style="margin-top:10px;">
                Suggest Tables
            </button>
        </form>

        <c:if test="${not empty error}">
            <p class="flash-message flash-error">${error}</p>
        </c:if>
    </div>

    <!-- Current tables card -->
    <div class="dashboard-card">
        <h2>Current Tables</h2>

        <div class="table-wrapper">
            <table class="data-table">
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
                                <c:when test="${t.canCombine}">
                                    <span class="badge badge-yes">Yes</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge badge-no">No</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- Suggested tables card -->
    <c:if test="${not empty suggestedTables}">
        <div class="dashboard-card">
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
        </div>
    </c:if>

</div>

</body>
</html>
