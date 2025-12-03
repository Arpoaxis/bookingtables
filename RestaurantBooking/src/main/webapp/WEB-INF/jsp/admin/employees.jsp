<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Manage Employees</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">

<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>

<div class="dashboard-main">

    <!-- Header -->
    <div class="dashboard-header">
        <h1>Employees</h1>
        <p class="dashboard-subtitle">
            Manage staff for your restaurant.
        </p>
    </div>

    <!-- Flash messages from session (set in servlet) -->
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

    <!-- Per-request messages (e.g. from forward) -->
    <c:if test="${not empty error}">
        <p class="flash-message flash-error">
            <c:out value="${error}" />
        </p>
    </c:if>

    <c:if test="${not empty success}">
        <p class="flash-message flash-success">
            <c:out value="${success}" />
        </p>
    </c:if>

    <!-- Main card -->
    <div class="dashboard-card wide-card">

        <!-- Filter/search bar -->
        <form method="get"
              action="<c:url value='/admin/employees'/>"
              class="employee-filter-form">
            <div class="form-row">
                <label>Search</label>
                <input type="text" name="q"
                       value="${fn:escapeXml(param.q)}"
                       placeholder="Name or email">
            </div>

            <div class="form-row">
                <label>Role</label>
                <select name="role">
                    <option value="ALL">All</option>
                    <option value="EMPLOYEE" <c:if test="${param.role == 'EMPLOYEE'}">selected</c:if>>Employee</option>
                    <option value="HOST"     <c:if test="${param.role == 'HOST'}">selected</c:if>>Host</option>
                    <option value="MANAGER"  <c:if test="${param.role == 'MANAGER'}">selected</c:if>>Manager</option>
                </select>
            </div>

            <div class="form-row">
                <label>Status</label>
                <select name="active">
                    <option value="all"
                        <c:if test="${param.active == 'all' || empty param.active}">selected</c:if>>
                        All
                    </option>
                    <option value="active"
                        <c:if test="${param.active == 'active'}">selected</c:if>>
                        Active
                    </option>
                    <option value="inactive"
                        <c:if test="${param.active == 'inactive'}">selected</c:if>>
                        Inactive
                    </option>
                </select>
            </div>

            <div class="form-row">
                <button type="submit" class="primary-link button-link">
                    Apply filters
                </button>
            </div>
        </form>

        <hr style="margin: 1.5rem 0;">

        <!-- New employee form -->
        <h2 style="margin-top:0;">Add new employee</h2>
        <form method="post" action="<c:url value='/admin/employees'/>" class="employee-form">
            <!-- CSRF -->
            <input type="hidden" name="csrf_token"
                   value="${sessionScope.csrf_token}" />

            <div class="form-row">
                <label>First name</label>
                <input type="text" name="first_name" required>
            </div>

            <div class="form-row">
                <label>Last name</label>
                <input type="text" name="last_name" required>
            </div>

            <div class="form-row">
                <label>Email</label>
                <input type="email" name="email" required>
            </div>

            <div class="form-row">
                <label>Phone</label>
                <input type="text" name="phoneNumber" required>
            </div>

            <div class="form-row">
                <label>Role</label>
                <select name="role">
                    <option value="EMPLOYEE">Employee</option>
                    <option value="HOST">Host</option>
                    <option value="MANAGER">Manager</option>
                </select>
            </div>

            <div class="form-row">
                <button type="submit"
                        name="action"
                        value="create"
                        class="primary-link button-link">
                    Create employee
                </button>
            </div>
        </form>

        <hr style="margin: 2rem 0;">

        <!-- Employees table -->
        <c:if test="${empty employees}">
            <p>No employees found for this restaurant.</p>
        </c:if>

        <c:if test="${not empty employees}">
            <c:set var="currentSort" value="${param.sort}" />
            <c:set var="currentDir"  value="${param.dir}" />
            <c:set var="nextDir"     value="${currentDir == 'asc' ? 'desc' : 'asc'}" />

            <table class="status-table employees-table">
                <thead>
                <tr>
                    <th>
                        <a href="<c:url value='/admin/employees'>
                                    <c:param name='sort' value='id'/>
                                    <c:param name='dir'  value='${nextDir}'/>
                                    <c:param name='q'    value='${param.q}'/>
                                    <c:param name='role' value='${param.role}'/>
                                    <c:param name='active' value='${param.active}'/>
                                 </c:url>">
                            ID
                        </a>
                    </th>
                    <th>
                        <a href="<c:url value='/admin/employees'>
                                    <c:param name='sort' value='name'/>
                                    <c:param name='dir'  value='${nextDir}'/>
                                    <c:param name='q'    value='${param.q}'/>
                                    <c:param name='role' value='${param.role}'/>
                                    <c:param name='active' value='${param.active}'/>
                                 </c:url>">
                            Name
                        </a>
                    </th>
                    <th>
                        <a href="<c:url value='/admin/employees'>
                                    <c:param name='sort' value='email'/>
                                    <c:param name='dir'  value='${nextDir}'/>
                                    <c:param name='q'    value='${param.q}'/>
                                    <c:param name='role' value='${param.role}'/>
                                    <c:param name='active' value='${param.active}'/>
                                 </c:url>">
                            Email
                        </a>
                    </th>
                    <th>
                        Phone
                    </th>
                    <th>
                        <a href="<c:url value='/admin/employees'>
                                    <c:param name='sort' value='role'/>
                                    <c:param name='dir'  value='${nextDir}'/>
                                    <c:param name='q'    value='${param.q}'/>
                                    <c:param name='role' value='${param.role}'/>
                                    <c:param name='active' value='${param.active}'/>
                                 </c:url>">
                            Job title
                        </a>
                    </th>
                    <th>
                        <a href="<c:url value='/admin/employees'>
                                    <c:param name='sort' value='active'/>
                                    <c:param name='dir'  value='${nextDir}'/>
                                    <c:param name='q'    value='${param.q}'/>
                                    <c:param name='role' value='${param.role}'/>
                                    <c:param name='active' value='${param.active}'/>
                                 </c:url>">
                            Active
                        </a>
                    </th>
                    <th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="emp" items="${employees}">
                    <tr>
                        <td><c:out value="${emp.userId}" /></td>
                        <td>
                            <c:out value="${emp.firstName}" />
                            &nbsp;
                            <c:out value="${emp.lastName}" />
                        </td>
                        <td><c:out value="${emp.email}" /></td>
                        <td><c:out value="${emp.phoneNumber}" /></td>
                        <td><c:out value="${emp.accountType}" /></td>
                        <td>
                            <c:choose>
                                <c:when test="${emp.active}">Yes</c:when>
                                <c:otherwise>No</c:otherwise>
                            </c:choose>
                        </td>
                        <td>
                            <!-- toggle active -->
                            <form method="post"
                                  action="<c:url value='/admin/employees'/>"
                                  style="display:inline;">
                                <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}" />
                                <input type="hidden" name="employeeId" value="${emp.userId}" />
                                <button type="submit"
                                        name="action"
                                        value="toggleActive"
                                        class="primary-link button-link small-button">
                                    <c:choose>
                                        <c:when test="${emp.active}">
                                            Deactivate
                                        </c:when>
                                        <c:otherwise>
                                            Activate
                                        </c:otherwise>
                                    </c:choose>
                                </button>
                            </form>

                            <!-- reset password -->
                            <form method="post"
                                  action="<c:url value='/admin/employees'/>"
                                  style="display:inline; margin-left:0.5rem;">
                                <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}" />
                                <input type="hidden" name="employeeId" value="${emp.userId}" />
                                <button type="submit"
                                        name="action"
                                        value="resetPassword"
                                        class="danger-link button-link small-button">
                                    Reset password
                                </button>
                            </form>

                            <!-- edit link -->
                            <c:url var="editUrl" value="/admin/employee/edit">
                                <c:param name="employeeId" value="${emp.userId}"/>
                            </c:url>
                            <a href="${editUrl}" class="secondary-link" style="margin-left:0.5rem;">
                                Edit
                            </a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>

    <!-- Back link -->
    <div style="margin-top: 1rem;">
        <a class="primary-link"
           href="<c:url value='/admin/dashboard'/>">
            &larr; Back to dashboard
        </a>
    </div>

</div><!-- /dashboard-main -->

</body>
</html>
