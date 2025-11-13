<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div class="navigation">
    <c:choose>
        <c:when test="${sessionScope.role == 'ADMIN' or sessionScope.role == 'MANAGER'}">
            <a href="<c:url value='/admin/dashboard'/>">Home</a>
        </c:when>

        <c:when test="${sessionScope.role == 'CUSTOMER'}">
            <a href="<c:url value='/'/>">Home</a>
        </c:when>

        <c:otherwise>
            <a href="<c:url value='/'/>">Home</a>
        </c:otherwise>
    </c:choose>
</div>
