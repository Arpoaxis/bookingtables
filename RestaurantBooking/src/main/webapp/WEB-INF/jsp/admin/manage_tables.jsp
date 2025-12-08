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


<jsp:include page="/WEB-INF/jsp/header.jsp" />

<jsp:include page="/WEB-INF/jsp/admin/back_to_dashboard.jsp" />
<div class="dashboard-container">

    <h1>Manage Tables</h1>

    <!-- Flash messages (success / error) -->
    <c:if test="${not empty sessionScope.tableMessage}">
        <div class="flash-message flash-success">
            ${sessionScope.tableMessage}
        </div>
        <c:remove var="tableMessage" scope="session" />
    </c:if>

    <c:if test="${not empty sessionScope.tableError}">
        <div class="flash-message flash-error">
            ${sessionScope.tableError}
        </div>
        <c:remove var="tableError" scope="session" />
    </c:if>

    <!-- Undo last delete -->
    <c:if test="${not empty sessionScope.lastDeletedTable}">
        <div class="card undo-card">
            <p>
                Last deleted table:
                <strong>#${sessionScope.lastDeletedTable.tableNumber}</strong>
                (min ${sessionScope.lastDeletedTable.minCapacity},
                 max ${sessionScope.lastDeletedTable.maxCapacity})
            </p>
            <form action="${pageContext.request.contextPath}/admin/undo_delete_table"
                  method="post">
                <input type="hidden" name="csrf_token"
                       value="${sessionScope.csrf_token}" />
                <button type="submit" class="btn btn-secondary btn-small">
                    Undo delete
                </button>
            </form>
        </div>
    </c:if>

    <!-- Top actions -->
    <div class="toolbar">
        <a class="btn btn-primary"
           href="<c:url value='/admin/add_table'/>">
            + Add New Table
        </a>
        <a class="btn btn-secondary"
           href="<c:url value='/admin/table_planner'/>">
            Table Planner
        </a>
    </div>

    <!-- Any request-scoped error (fallback) -->
    <c:if test="${not empty error}">
        <div class="flash-message flash-error">
            ${error}
        </div>
    </c:if>

    <h2>All Restaurant Tables</h2>

    <c:choose>
        <c:when test="${empty tables}">
            <p>No tables found in the system.</p>
        </c:when>

        <c:otherwise>
            <div class="table-wrapper">
                <table class="data-table">
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
                                <span class="badge
                                    <c:out value='${t.canCombine ? "badge-yes" : "badge-no"}'/>">
                                    <c:choose>
                                        <c:when test="${t.canCombine}">Yes</c:when>
                                        <c:otherwise>No</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>
                            <td class="table-actions">
                                <form action="${pageContext.request.contextPath}/admin/delete_table"
                                      method="post">
                                    <input type="hidden" name="tableId" value="${t.tableId}" />
                                    <input type="hidden" name="csrf_token"
                                           value="${sessionScope.csrf_token}" />
                                    <button type="submit"
                                            class="btn btn-danger btn-small"
                                            onclick="return confirm('Delete table #${t.tableNumber}?');">
                                        Delete
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:otherwise>
    </c:choose>

</div>

</body>
</html>
