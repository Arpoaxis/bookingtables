<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Reservations</title>
    <link rel="stylesheet" type="text/css" href="<c:url value='/css/style.css'/>">
</head>
<body>
<h1>My Reservations</h1>

<c:if test="${not empty error}">
    <p style="color:red">${error}</p>
</c:if>

<c:if test="${empty bookings}">
    <p>You have no reservations.</p>
</c:if>

<table border="1">
    <tr>
        <td>Restaurant</td>
        <td>Date</td>
        <td>Time</td>
        <td>Guests</td>
        <td>Status</td>
        <td>Actions</td>
    </tr>

   <c:forEach var="b" items="${bookings}">
    <c:if test="${b.status != 'CANCELLED'}">
        <tr>
            <td>${b.restaurantName}</td>
            <td>${b.date}</td>     
            <td>${b.time}</td>     
            <td>${b.guests}</td>
            <td>${b.status}</td>

            <td>
                <form action="<c:url value='/booking/cancel'/>" method="post">
                    <input type="hidden" name="bookingId" value="${b.bookingId}">
                    <button type="submit">Cancel</button>
                </form>
            </td>
        </tr>
    </c:if>
</c:forEach>

</table>

<p><a href="<c:url value='/restaurants'/>">Back to Restaurants</a></p>
</body>
</html>