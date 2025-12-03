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

<div class="booking-page">

    <div class="booking-card">

        <h1 class="booking-title">Reserve at ${restaurant.name}</h1>

        <%-- Display error message if present --%>
        <c:if test="${not empty error}">
            <div class="booking-error">${error}</div>
        </c:if>

        <form action="<c:url value='/booking/create'/>" method="post" class="booking-form">

            <%-- CSRF token --%>
            <input type="hidden" name="csrf_token" value="${csrfToken}"/>

            <%-- Restaurant ID --%>
            <input type="hidden" name="restaurantId" value="${restaurant.restaurantId}"/>

            <label>Date</label>
            <input type="date" id="datePicker" name="date" required>

            <label>Time</label>
           
            <select id="timeSlot" name="time" required>
			  
			    <option value="08:00">08:00</option>
			    <option value="09:00">09:00</option>
			    <option value="10:00">10:00</option>
			    <option value="11:00">11:00</option>
			    <option value="12:00">12:00</option>
			    <option value="13:00">13:00</option>
			    <option value="14:00">14:00</option>
			    <option value="15:00">15:00</option>
			    <option value="16:00">16:00</option>
			    <option value="17:00">17:00</option>
			    <option value="18:00">18:00</option>
			    <option value="19:00">19:00</option>
			    <option value="20:00">20:00</option>
			    <option value="21:00">21:00</option>
			    <option value="22:00">22:00</option>
			   
			</select>

            <label>Number of Guests</label>
            <div style="display:flex; align-items:center; gap:8px;">
                <input type="range" id="guestSlider" name="guests" min="1" max="20" value="2">
                <span id="guestCount">2</span>
            </div>

            <label>Special Requests</label>
            <textarea name="requests" rows="3"></textarea>

            <button type="submit" class="btn btn-primary booking-btn">
                Confirm Reservation
            </button>

        </form>

    </div>

</div>

<script>
    (function() {
        var slider = document.getElementById('guestsSlider');
        var output = document.getElementById('guestCount');
        if (!slider || !output) return;

        // initial sync
        output.textContent = slider.value;

        slider.addEventListener('input', function () {
            output.textContent = this.value;
        });
    })();
</script>



</body>
</html>
