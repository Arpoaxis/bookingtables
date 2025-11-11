<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<div class="navigation">
	<c:choose>
		<c:when test="${sessionScope.user != null and sessionScope.user.accountType == 'ADMIN'}">
            <a href="${pageContext.request.contextPath}/jsp/admin/dashboard.jsp">Home</a>
        </c:when>
		<c:when test="${sessionScope.user != null and sessionScope.user.accountType == 'CUSTOMER'}">
            <a href="${pageContext.request.contextPath}/jsp/index.jsp">Home</a>
        </c:when>
		<%--<c:when test="${sessionScope.user != null and sessionScope.user.accountType == 'HOST'}">
			<a href="${pageContext.request.contextPath}/jsp/host/host_dashboard.jsp">Home</a> 
		</c:when>--%>
		<c:otherwise>
            <a href="${pageContext.request.contextPath}/jsp/index.jsp">Home</a>
        </c:otherwise>
	</c:choose>
</div>