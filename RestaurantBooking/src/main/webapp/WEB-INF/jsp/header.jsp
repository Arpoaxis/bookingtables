<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="home-link">

    <%-- Navigation / Home link --%>
    <div class="navigation">
        <c:choose>
            <c:when test="${sessionScope.role == 'ADMIN' or sessionScope.role == 'MANAGER'}">
                <a href="<c:url value='/admin/dashboard'/>">Home</a>
            </c:when>

            <c:when test="${sessionScope.role == 'CUSTOMER'}">
                <a href="<c:url value='/index'/>">Home</a>
            </c:when>

            <c:otherwise>
                <a href="<c:url value='/index'/>">Home</a>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- User info --%>
    <div class="user-info">
        <c:choose>

            <%-- Preferred: display full name --%>
            <c:when test="${not empty sessionScope.user}">
                Logged in as:
                <strong>
                    ${sessionScope.user.firstName} ${sessionScope.user.lastName}
                </strong>

                <c:if test="${not empty sessionScope.role}">
                    (${fn:toUpperCase(sessionScope.role)})
                </c:if>
                |
                <a href="<c:url value='/logout'/>">Logout</a>
            </c:when>

            <%-- Fallback: only email available --%>
            <c:when test="${not empty sessionScope.email}">
                Logged in as:
                <strong>${sessionScope.email}</strong>

                <c:if test="${not empty sessionScope.role}">
                    (${fn:toUpperCase(sessionScope.role)})
                </c:if>
                |
                <a href="<c:url value='/logout'/>">Logout</a>
            </c:when>

            <%-- Not logged in --%>
            <c:otherwise>
                Not logged in |
                <a href="<c:url value='/login'/>">Login</a>
            </c:otherwise>

        </c:choose>
    </div>

    <%-- CUSTOMER-ONLY: My Reservations --%>
    <c:if test="${sessionScope.role == 'CUSTOMER'}">
        <a href="<c:url value='/booking/mine'/>">My Reservations</a>
    </c:if>

</div>
