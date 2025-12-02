<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Profile</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="dashboard-body">

    <jsp:include page="/WEB-INF/jsp/header.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-card profile-card">
        	<c:if test="${not empty sessionScope.success}">
			    <div class="flash-message flash-success">
			        ${sessionScope.success}
			    </div>
			    <c:remove var="success" scope="session"/>
			</c:if>
			
			<c:if test="${not empty sessionScope.error}">
			    <div class="flash-message flash-error">
			        ${sessionScope.error}
			    </div>
			    <c:remove var="error" scope="session"/>
			</c:if>

            <h1>My Profile</h1>
            <p class="dashboard-subtitle">View and manage your account details.</p>

            <hr class="management-separator">

            <div class="profile-info">
                <p><strong>First Name:</strong> ${sessionScope.user.firstName}</p>
                <p><strong>Last Name:</strong> ${sessionScope.user.lastName}</p>
                <p><strong>Email:</strong> ${sessionScope.user.email}</p>

                <c:if test="${not empty sessionScope.user.phoneNumber}">
                    <p><strong>Phone:</strong> ${sessionScope.user.phoneNumber}</p>
                </c:if>
            </div>

            <div class="auth-actions">
                <a href="<c:url value='/profile/edit'/>" class="auth-link-button">
                    Edit Profile
                </a>

                <a href="<c:url value='/logout'/>" class="auth-primary-button logout-btn">
                    Logout
                </a>
            </div>

        </div>

    </div>

</body>
</html>
