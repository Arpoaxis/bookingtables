<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Profile</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="dashboard-body">

    <jsp:include page="/WEB-INF/jsp/header.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-card">

            <h1>Edit Profile</h1>
            <p class="dashboard-subtitle">
                Update your personal information.
            </p>

            <c:if test="${not empty error}">
                <div class="flash-message flash-error">${error}</div>
            </c:if>

            <c:if test="${not empty success}">
                <div class="flash-message flash-success">${success}</div>
            </c:if>

            <!-- IMPORTANT: post back to /profile/edit -->
            <form action="<c:url value='/profile/edit'/>" method="post" class="auth-form">

                <label for="first_name">First Name</label>
                <input type="text" name="first_name"
                       id="first_name" value="${user.firstName}" required>

                <label for="last_name">Last Name</label>
                <input type="text" name="last_name"
                       id="last_name" value="${user.lastName}" required>

                <label for="phone_number">Phone Number</label>
                <input type="tel" name="phoneNumber"
                       id="phone_number" value="${user.phoneNumber}" required>

                <hr class="profile-divider"/>

                <h2>Change Password</h2>
                <p class="form-hint">
                    Leave these fields blank if you do not want to change your password.
                </p>

                <label for="current_password">Current Password</label>
                <input type="password" name="current_password" id="current_password">

                <label for="new_password">New Password</label>
                <input type="password" name="new_password" id="new_password">

                <label for="confirm_password">Confirm New Password</label>
                <input type="password" name="confirm_password" id="confirm_password">

                <button type="submit" class="auth-primary-button" style="width:100%;">
                    Save Changes
                </button>

            </form>

            <div class="auth-actions">
                <a href="<c:url value='/profile'/>" class="auth-link-button">
                    Cancel
                </a>
            </div>
        </div>

    </div>

</body>
</html>
