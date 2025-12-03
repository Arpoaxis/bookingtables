<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:if test="${not empty sessionScope.role
             && sessionScope.role == 'ADMIN'}">
    <div class="back-to-dashboard-bar">
        <a class="primary-link"
           href="<c:url value='/admin/dashboard'/>">
            &larr; Back to dashboard
        </a>
    </div>
</c:if>
