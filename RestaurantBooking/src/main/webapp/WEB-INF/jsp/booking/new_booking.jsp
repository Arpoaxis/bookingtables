<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>New Reservation</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>

<body>

<jsp:include page="/WEB-INF/jsp/header.jsp" />

<div class="reservation-container">
    <div class="reservation-card">

        <h1 class="reservation-title">Reserve at ${restaurant.name}</h1>

        <c:if test="${not empty error}">
            <p class="booking-error">${error}</p>
        </c:if>

        <form action="<c:url value='/booking/create'/>"
              method="post"
              class="reservation-form">

            <!-- we MUST send restaurantId to the POST -->
            <input type="hidden" name="restaurantId"
                   value="${restaurant.restaurantId}" />

            <div class="res-form-row">
                <label>Date:</label>
                <input type="date"
                       id="datePicker"
                       name="date"
                       min="${today}"
                       value="${date != null ? date : today}" />
            </div>

            <div class="res-form-row">
                <label>Time:</label>
                <select id="timePicker" name="time" required>
                    <option value="">-- choose a time --</option>

                    <!-- value is 24-hour, label is pretty 12-hour -->
                    <option value="11:30">11:30 AM</option>
                    <option value="11:45">11:45 AM</option>
                    <option value="12:00">12:00 PM</option>
                    <option value="12:15">12:15 PM</option>
                    <option value="12:30">12:30 PM</option>
                    <option value="12:45">12:45 PM</option>
                    <option value="13:00">1:00 PM</option>
                    <option value="13:15">1:15 PM</option>
                    <option value="13:30">1:30 PM</option>
                    <option value="13:45">1:45 PM</option>
                    <option value="14:00">2:00 PM</option>
                    <option value="14:15">2:15 PM</option>
                    <option value="14:30">2:30 PM</option>
                    <option value="14:45">2:45 PM</option>
                    <option value="15:00">3:00 PM</option>
                    <option value="15:15">3:15 PM</option>
                    <option value="15:30">3:30 PM</option>
                    <option value="15:45">3:45 PM</option>
                    <option value="16:00">4:00 PM</option>
                    <option value="16:15">4:15 PM</option>
                    <option value="16:30">4:30 PM</option>
                    <option value="16:45">4:45 PM</option>
                    <option value="17:00">5:00 PM</option>
                    <option value="17:15">5:15 PM</option>
                    <option value="17:30">5:30 PM</option>
                    <option value="17:45">5:45 PM</option>
                    <option value="18:00">6:00 PM</option>
                    <option value="18:15">6:15 PM</option>
                    <option value="18:30">6:30 PM</option>
                    <option value="18:45">6:45 PM</option>
                    <option value="19:00">7:00 PM</option>
                    <option value="19:15">7:15 PM</option>
                    <option value="19:30">7:30 PM</option>
                    <option value="19:45">7:45 PM</option>
                    <option value="20:00">8:00 PM</option>
                    <option value="20:15">8:15 PM</option>
                    <option value="20:30">8:30 PM</option>
                    <option value="20:45">8:45 PM</option>
                    <option value="21:00">9:00 PM</option>
                    <option value="21:15">9:15 PM</option>
                    <option value="21:30">9:30 PM</option>
                    <option value="21:45">9:45 PM</option>
                    <option value="22:00">10:00 PM</option>
                </select>
            </div>

            <div class="res-form-row">
                <label>Guests:</label>
                <input type="number"
                       id="guestInput"
                       name="guests"
                       min="1"
                       max="20"
                       value="${guests != null ? guests : 2}" />
            </div>

            <div class="res-form-row">
                <label>Special Requests:</label>
                <textarea name="requests"
                          rows="3"
                          class="reservation-requests">${requests}</textarea>
            </div>

            <div id="tableContainer" class="table-select-box">
                <em>Choose a date and time to load available tables…</em>
            </div>

            <button type="submit" class="reservation-submit-btn">
                Submit Reservation
            </button>

        </form>

    </div>
</div>

<script>
const ctx = "${pageContext.request.contextPath}";
const restaurantId = "<c:out value='${restaurant.restaurantId}'/>";

function loadTables() {
    const date   = document.getElementById("datePicker").value;
    const time   = document.getElementById("timePicker").value;
    const guests = document.getElementById("guestInput").value;

    if (!date || !time || !guests || !restaurantId) {
        return;
    }

    const url = ctx + "/api/available-tables"
        + "?restaurantId=" + encodeURIComponent(restaurantId)
        + "&date="        + encodeURIComponent(date)
        + "&time="        + encodeURIComponent(time)
        + "&guests="      + encodeURIComponent(guests);

    fetch(url)
        .then(res => {
            if (!res.ok) throw new Error("HTTP " + res.status);
            return res.json();
        })
        .then(data => {
            const container = document.getElementById("tableContainer");
            container.innerHTML = "";

            if (!Array.isArray(data) || data.length === 0) {
                container.innerHTML =
                    "<em>No available tables for this time. Staff will assign a table on arrival.</em>";
                return;
            }

            data.forEach(function (t) {
                // handle both camelCase and snake_case coming from JSON
                var tableId     = (t.tableId     != null) ? t.tableId     : t.table_id;
                var tableNumber = (t.tableNumber != null) ? t.tableNumber : t.table_number;
                var minCapacity = (t.minCapacity != null) ? t.minCapacity : t.min_capacity;
                var maxCapacity = (t.maxCapacity != null) ? t.maxCapacity : t.max_capacity;

                var div = document.createElement("div");
                div.className = "table-option";

                div.innerHTML =
                    '<label>' +
                        '<input type="radio" name="tableId" value="' + tableId + '">' +
                        'Table #' + tableNumber + ' — Seats ' + minCapacity + '-' + maxCapacity +
                    '</label>';

                container.appendChild(div);
            });


        })
        .catch(err => {
            console.error("API ERROR:", err);
            document.getElementById("tableContainer").innerHTML =
                "<em>We couldn’t load the table list, but your reservation can still be submitted. Staff will assign a table.</em>";
        });
}

document.getElementById("datePicker").addEventListener("change", loadTables);
document.getElementById("guestInput").addEventListener("input", loadTables);
document.getElementById("timePicker").addEventListener("change", loadTables);

// If we're coming back after an error and a time is already set, restore it and load tables
if ("${time}" !== "") {
    document.getElementById("timePicker").value = "${time}";
    loadTables();
}
</script>

</body>
</html>
