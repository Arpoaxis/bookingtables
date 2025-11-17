<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${restaurant.name} - Restaurant</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

	<jsp:include page="/WEB-INF/jsp/header.jsp"/>
	<div class="restaurant-container">
	<h1>${restaurant.name}</h1>
	
	<c:if test="${not empty restaurant.address}">
	    <p>${restaurant.address}</p>
	</c:if>
	
	<c:if test="${not empty restaurant.phone}">
	    <p>${restaurant.phone}</p>
	</c:if>
	
	<c:if test="${not empty restaurant.description}">
	    <p>${restaurant.description}</p>
	</c:if>

<hr>

<h2>Reservations</h2>


<c:url var="reserveUrl" value="/booking/new">
    <c:param name="restaurantId" value="${restaurant.restaurantId}" />
</c:url>

<p>
    <a href="<c:url value='/booking/start?restaurantId=${restaurant.restaurantId}'/>">
    Make a Reservation
</a>
</p>

<p>
    <a href="<c:url value='/restaurants'/>">Back to all restaurants</a>
</p>
</div>
</body>
</html>
