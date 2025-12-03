<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Employee</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">

<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>

<div class="dashboard-main">

    <div class="dashboard-header">
        <h1>Edit employee</h1>
        <p class="dashboard-subtitle">
            Update details for
            <strong>
                <c:out value="${employee.firstName}" />
                <c:out value=" " />
                <c:out value="${employee.lastName}" />
            </strong>
        </p>
    </div>

    <c:if test="${not empty sessionScope.error}">
        <p class="flash-message flash-error">
            <c:out value="${sessionScope.error}" />
        </p>
        <c:remove var="error" scope="session" />
    </c:if>

    <c:if test="${not empty sessionScope.success}">
        <p class="flash-message flash-success">
            <c:out value="${sessionScope.success}" />
        </p>
        <c:remove var="success" scope="session" />
    </c:if>

    <div class="dashboard-card wide-card">
        <form method="post" action="<c:url value='/admin/employee/edit'/>" class="employee-form">
            <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}" />
            <input type="hidden" name="employeeId" value="${employee.userId}" />

            <div class="form-row">
                <label>First name</label>
                <input type="text" name="first_name" value="${employee.firstName}" required>
            </div>

            <div class="form-row">
                <label>Last name</label>
                <input type="text" name="last_name" value="${employee.lastName}" required>
            </div>

            <div class="form-row">
                <label>Email</label>
                <input type="email" name="email" value="${employee.email}" required>
            </div>

            <div class="form-row">
                <label>Phone</label>
                <input type="text" name="phoneNumber" value="${employee.phoneNumber}" required>
            </div>

            <div class="form-row">
                <label>Role</label>
                <select name="role">
                    <option value="EMPLOYEE"
                        <c:if test="${employee.accountType == 'EMPLOYEE'}">selected</c:if>>Employee</option>
                    <option value="HOST"
                        <c:if test="${employee.accountType == 'HOST'}">selected</c:if>>Host</option>
                    <option value="MANAGER"
                        <c:if test="${employee.accountType == 'MANAGER'}">selected</c:if>>Manager</option>
                </select>
            </div>

            <div class="form-row">
                <button type="submit" class="primary-link button-link">
                    Save changes
                </button>
            </div>
        </form>
    </div>

    <div style="margin-top: 1rem;">
        <a class="primary-link" href="<c:url value='/admin/employees'/>">
            &larr; Back to employees
        </a>
    </div>

</div>

</body>
</html>
