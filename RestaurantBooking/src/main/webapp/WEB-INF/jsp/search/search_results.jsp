<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Search Results</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body class="home-body">

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<div class="home-main">

    <h1>Search Results</h1>
    <p>Showing results for "<strong>${query}</strong>"</p>

    <div class="restaurant-list-container">

        <c:if test="${empty results}">
            <p>No restaurants found.</p>
        </c:if>

        <div class="restaurant-grid">

            <c:forEach var="r" items="${results}">
                <div class="restaurant-card">
                    <h3>${r.name}</h3>
                    <p class="address">${r.address}</p>
                    <p class="desc">${r.description}</p>

                    <a class="view-btn"
                       href="<c:url value='/restaurants?id=${r.restaurantId}'/>">
                        View Restaurant
                    </a>
                </div>
            </c:forEach>

        </div>

    </div>

</div>

</body>
</html>
