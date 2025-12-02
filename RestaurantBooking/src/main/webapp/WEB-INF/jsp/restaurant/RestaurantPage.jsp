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

<div class="restaurant-page">

    <%-- Restaurant Info Card --%>
    <div class="restaurant-details-card">

        <h1 class="restaurant-title">${restaurant.name}</h1>

        <c:if test="${not empty restaurant.address}">
            <p class="restaurant-detail">${restaurant.address}</p>
        </c:if>

        <c:if test="${not empty restaurant.phone}">
            <p class="restaurant-detail">${restaurant.phone}</p>
        </c:if>

        <c:if test="${not empty restaurant.description}">
            <p class="restaurant-description">${restaurant.description}</p>
        </c:if>

        <hr class="restaurant-divider"/>

        <div class="restaurant-buttons">
            <a href="<c:url value='/booking/start?restaurantId=${restaurant.restaurantId}'/>"
               class="btn btn-primary">
                Make a Reservation
            </a>

            <a href="<c:url value='/restaurants'/>"
               class="btn btn-secondary">
                Back to All Restaurants
            </a>
        </div>

    </div>

    

</div>

</body>
</html>
