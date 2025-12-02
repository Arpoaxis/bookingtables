<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Reservation Confirmed</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body>

    <jsp:include page="/WEB-INF/jsp/header.jsp"/>

    <div class="dashboard-main">

        <div class="dashboard-card" style="text-align:center; padding:30px;">

            <h1 class="auth-title">Reservation Confirmed</h1>

            <p class="dashboard-subtitle">
                Your reservation has been successfully created.
            </p>

            <hr class="management-separator">

        
            
            <div style="margin-top:25px;">
                <a href="<c:url value='/booking/mine'/>" class="auth-primary-button">
                    View My Reservations
                </a>
            </div>

            <div style="margin-top:10px;">
                <a href="<c:url value='/'/>" class="auth-link-button">
                    Back to Restaurants
                </a>
            </div>

        </div>

    </div>

</body>
</html>
