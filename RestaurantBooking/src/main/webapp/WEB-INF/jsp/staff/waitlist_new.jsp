<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add booking / walk-in</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<div class="dashboard-main">

    <div class="dashboard-header">
        <h1>Add booking / walk-in</h1>
        <p class="dashboard-subtitle">
            Look up an existing customer or add a new guest to the waitlist.
        </p>
    </div>

    <c:if test="${not empty error}">
        <p class="flash-message flash-error">
            <c:out value="${error}"/>
        </p>
    </c:if>

    <c:if test="${not empty success}">
        <p class="flash-message flash-success">
            <c:out value="${success}"/>
        </p>
    </c:if>

    <div class="dashboard-layout">
        <!-- Left: customer search -->
        <div class="dashboard-card">
            <h2>Search existing customer</h2>

            <form method="get" action="<c:url value='/staff/waitlist/new'/>" class="auth-form">
                <label for="q">Name, email, or phone</label>
                <input type="text" id="q" name="q"
                       value="${fn:escapeXml(searchQuery)}"
                       placeholder="Start typing name, email, or phone">

                <button type="submit" class="auth-primary-button" style="width:100%;">
                    Search
                </button>
            </form>

            <c:if test="${not empty customerResults}">
                <h3>Matches</h3>
                <table class="status-table">
                    <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="u" items="${customerResults}">
                        <tr>
                            <td><c:out value="${u.firstName}"/> <c:out value="${u.lastName}"/></td>
                            <td><c:out value="${u.email}"/></td>
                            <td><c:out value="${u.phoneNumber}"/></td>
                            <td>
                                <a class="primary-link" href="<c:url value='/staff/waitlist/new'>
                                    <c:param name='userId' value='${u.userId}'/>
                                </c:url>">
                                    Use for waitlist
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </c:if>
        </div>

        <!-- Right: waitlist form -->
        <div class="dashboard-card">
            <h2>Add to waitlist</h2>

            <c:if test="${not empty selectedUser}">
                <p>
                    Using existing customer:
                    <strong><c:out value="${selectedUser.firstName}"/> <c:out value="${selectedUser.lastName}"/></strong>
                    (<c:out value="${selectedUser.email}"/>)
                </p>
            </c:if>

            <form method="post" action="<c:url value='/staff/waitlist/new'/>" class="auth-form">
                <input type="hidden" name="csrf_token" value="${sessionScope.csrf_token}"/>

                <c:if test="${not empty selectedUser}">
                    <input type="hidden" name="userId" value="${selectedUser.userId}"/>
                </c:if>

                <label for="first_name">First name</label>
                <input type="text" id="first_name" name="first_name"
                       value="${empty selectedUser ? first_name : selectedUser.firstName}" required>

                <label for="last_name">Last name</label>
                <input type="text" id="last_name" name="last_name"
                       value="${empty selectedUser ? last_name : selectedUser.lastName}" required>

                <label for="phone">Phone (optional)</label>
                <input type="text" id="phone" name="phone"
                       value="${empty selectedUser ? phone : selectedUser.phoneNumber}">

                <label for="party_size">Party size</label>
                <input type="number" id="party_size" name="party_size"
                       min="1" value="${empty party_size ? 2 : party_size}" required>

                <label for="special_requests">Notes / special requests</label>
                <textarea id="special_requests" name="special_requests"
                          rows="3">${special_requests}</textarea>

                <h3 style="margin-top:1.5rem;">Optional: create customer account</h3>
                <p class="dashboard-subtitle">
                    If the guest wants an online account, enter an email and check this box.
                    They can change their password later using "Forgot password".
                </p>

                <label for="email">Email (for account)</label>
                <input type="email" id="email" name="email"
                       value="${empty selectedUser ? email : selectedUser.email}">

                <label>
                    <input type="checkbox" name="create_account">
                    Create a customer account with a temporary password
                </label>

                <button type="submit" class="auth-primary-button" style="width:100%; margin-top:1rem;">
                    Add to waitlist
                </button>
            </form>

            <div class="auth-actions">
                <a href="<c:url value='/staff/dashboard'/>" class="auth-link-button">
                    &larr; Back to staff dashboard
                </a>
            </div>
        </div>
    </div>

</div>

</body>
</html>
