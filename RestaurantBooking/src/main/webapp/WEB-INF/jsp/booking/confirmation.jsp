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
	<h1>Your reservation is confirmed!</h1>
	
	<p><a href="<c:url value='/'/>">Back to restaurants</a></p>
</body>
</html>