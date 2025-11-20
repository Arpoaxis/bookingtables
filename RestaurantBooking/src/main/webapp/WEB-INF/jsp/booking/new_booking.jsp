<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Make a Reservation</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<div class="booking-form-container">

    <h1>Reserve at ${restaurant.name}</h1>

    <c:if test="${not empty error}">
        <p style="color:red">${error}</p>
    </c:if>

    <form action="<c:url value='/booking/create'/>" method="post">

        <%-- CSRF token --%>
        <input type="hidden" name="csrf_token" value="${csrfToken}"/>

        <input type="hidden" name="restaurantId" value="${restaurant.restaurantId}"/>

        <label>Date:</label>
        <input type="date" name="date" required><br><br>

        <label>Time:</label>
        <input type="time" name="time" required><br><br>

        <label>Number of Guests:</label>
        <input type="number" name="guests" min="1" required><br><br>

        <label>Special Requests:</label>
        <textarea name="requests" rows="3"></textarea><br><br>

        <button type="submit">Confirm Reservation</button>
    </form>

</div>

</body>
</html>
