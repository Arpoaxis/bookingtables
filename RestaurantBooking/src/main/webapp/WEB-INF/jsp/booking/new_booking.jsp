<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>New Reservation</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">

</head>

<body>

<jsp:include page="/WEB-INF/jsp/header.jsp"/>

<div class="reservation-container">
    <div class="reservation-card">

        <h1 class="reservation-title">Reserve at ${restaurant.name}</h1>

        <form action="<c:url value='/booking/create'/>" method="post" class="reservation-form">

            <div class="res-form-row">
                <label>Date:</label>
                <input type="date" id="datePicker" name="date">
            </div>

            <div class="res-form-row">
                <label>Time:</label>
                <input type="time" id="timePicker" name="time">
            </div>

            <div class="res-form-row">
                <label>Guests:</label>
                <input type="number" id="guestInput" name="guests" min="1" max="20">
            </div>

            <div class="res-form-row">
                <label>Special Requests:</label>
                <textarea name="requests" rows="3"></textarea>
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
console.log("CTX =", ctx);

const restaurantId = "<c:out value='${restaurant.restaurantId}'/>";
console.log("Restaurant ID =", restaurantId);

function loadTables() {
    const date = document.getElementById("datePicker").value;
    const time = document.getElementById("timePicker").value;
    const guests = document.getElementById("guestInput").value;

    if (!date || !time || !guests || !restaurantId) {
        console.log("Missing values", {date, time, guests, restaurantId});
        return;
    }

    const url = ctx + "/api/available-tables"
    + "?restaurantId=" + restaurantId
    + "&date=" + date
    + "&time=" + time
    + "&guests=" + guests;
	console.log("FETCH URL =", url);


    fetch(url)
    .then(res => res.json())
    .then(data => {
    	console.log("TABLES RETURNED =", data);

        const container = document.getElementById("tableContainer");
        container.innerHTML = "";

        if (!Array.isArray(data) || data.length === 0) {
            container.innerHTML = "<em>No available tables for this time.</em>";
            return;
        }

        data.forEach(t => {
            const div = document.createElement("div");
            div.className = "table-option";
            const seatText = (t.minCapacity === t.maxCapacity)
            ? t.maxCapacity
            : `${t.minCapacity}-${t.maxCapacity}`;
            div.innerHTML = `
                <label>
                    <input type="radio" name="tableRadio" value="${t.tableId}">
                    Table #\${t.tableNumber} — Seats \${t.minCapacity}-\${t.maxCapacity}
                </label>
            `;

            div.querySelector("input").addEventListener("change", () => {
                document.getElementById("tableId").value = t.tableId;
            });

            container.appendChild(div);
        });
    })
    .catch(err => {
        console.error("API ERROR:", err);
        document.getElementById("tableContainer").innerHTML =
            "<em>Error loading tables.</em>";
    });

}

    document.getElementById("datePicker").addEventListener("change", loadTables);
    document.getElementById("timePicker").addEventListener("change", loadTables);
    document.getElementById("guestInput").addEventListener("input", loadTables);

    if ("${date}" && "${time}") {
        loadTables();
    }
</script>

</body>
</html>
