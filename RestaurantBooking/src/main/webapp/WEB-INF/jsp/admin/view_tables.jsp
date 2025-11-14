<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>View Tables - Restaurant Booking</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

    <!-- Include the common header / navigation -->
    <jsp:include page="/WEB-INF/jsp/header.jsp" />

    <div class="dashboard-container">
        <h1>Restaurant Tables</h1>

        <c:if test="${not empty tableError}">
            <p style="color:red;">${tableError}</p>
        </c:if>

        <c:choose>
            <c:when test="${empty tables}">
                <p>No tables found in the system.</p>
            </c:when>

            <c:otherwise>
                <table class="status-table">
                    <thead>
    					<tr>
        					<th>Table #</th>
        					<th>Min Capacity</th>
        					<th>Max Capacity</th>
        					<th>Can Combine?</th>
        					<th>Actions</th>
    					</tr>
					</thead>

                    <tbody>
                        <c:forEach var="t" items="${tables}">
                            <tr>
                                <td>${t.tableNumber}</td>
                                <td>${t.minCapacity}</td>
                                <td>${t.maxCapacity}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${t.canCombine}">
                                            Yes
                                        </c:when>
                                        <c:otherwise>
                                            No
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
            						<a href="<c:url value='/admin/edit_table'/>?id=${t.tableId}">Edit</a>
        						</td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>

        <p>
            <a href="<c:url value='/admin/dashboard'/>">Back to Dashboard</a>
        </p>
    </div>

</body>
</html>
