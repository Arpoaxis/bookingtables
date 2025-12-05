<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Restaurant Floor Plan</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

<div class="home-link">
    <jsp:include page="/WEB-INF/jsp/header.jsp" />
</div>
<jsp:include page="/WEB-INF/jsp/admin/back_to_dashboard.jsp" />
<div class="floorplan-main">
    <div class="floorplan-header">
        <h1>Restaurant Floor Plan</h1>
        <div class="floorplan-controls">
            <label for="dateSelect">View Date:</label>
            <input type="date" id="dateSelect" value="${selectedDate}" onchange="changeDate(this.value)">
            <a href="<c:url value='/admin/add_table'/>" class="btn btn-primary btn-small">+ Add Table</a>
        </div>
    </div>

    <div class="floorplan-legend">
        <span class="legend-item"><span class="legend-dot status-available"></span> Available</span>
        <span class="legend-item"><span class="legend-dot status-pending"></span> Pending</span>
        <span class="legend-item"><span class="legend-dot status-confirmed"></span> Confirmed</span>
        <span class="legend-item"><span class="legend-dot status-seated"></span> Seated</span>
    </div>

    <c:if test="${not empty error}">
        <p class="flash-message flash-error">${error}</p>
    </c:if>

    <c:if test="${empty tables}">
        <p>No tables found. <a href="<c:url value='/admin/add_table'/>">Add some tables</a> to get started.</p>
    </c:if>

    <c:if test="${not empty tables}">
        <div class="floorplan-room">
            <div class="floorplan-layout">

                <!-- LEFT SECTION: Bar tables (1 seat) -->
                <div class="floor-section floor-section-left">
                    <div class="floor-section-label">Bar Seating (1-seat)</div>
                    <div class="floor-section-tables">
                        <c:forEach var="t" items="${tables}">
                            <c:if test="${t.maxCapacity eq 1}">
                                <c:set var="status" value="${tableStatusMap[t.tableId]}" />
                                <c:set var="statusClass" value="${status != null ? fn:toLowerCase(status) : 'available'}" />
                                <div class="table-node table-interactive status-${statusClass}"
                                     data-table-id="${t.tableId}"
                                     data-table-number="${t.tableNumber}"
                                     data-status="${statusClass}"
                                     onclick="showTableDetails(${t.tableId}, ${t.tableNumber}, '${statusClass}')">
                                    <div class="table-node-number">T${t.tableNumber}</div>
                                    <div class="table-node-cap">${t.minCapacity}–${t.maxCapacity}</div>
                                    <c:if test="${status != null}">
                                        <div class="table-node-status">${status}</div>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>

                <!-- MIDDLE SECTION: Main dining (4+ seats) -->
                <div class="floor-section floor-section-middle">
                    <div class="floor-section-label">Main Dining (4+ seats)</div>
                    <div class="floor-section-tables">
                        <c:forEach var="t" items="${tables}">
                            <c:if test="${t.maxCapacity ge 4}">
                                <c:set var="status" value="${tableStatusMap[t.tableId]}" />
                                <c:set var="statusClass" value="${status != null ? fn:toLowerCase(status) : 'available'}" />
                                <div class="table-node table-interactive status-${statusClass}"
                                     data-table-id="${t.tableId}"
                                     data-table-number="${t.tableNumber}"
                                     data-status="${statusClass}"
                                     onclick="showTableDetails(${t.tableId}, ${t.tableNumber}, '${statusClass}')">
                                    <div class="table-node-number">T${t.tableNumber}</div>
                                    <div class="table-node-cap">${t.minCapacity}–${t.maxCapacity}</div>
                                    <c:if test="${status != null}">
                                        <div class="table-node-status">${status}</div>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>

                <!-- RIGHT SECTION: Window tables (2-3 seats) -->
                <div class="floor-section floor-section-right">
                    <div class="floor-section-label">Window Seating (2-3 seats)</div>
                    <div class="floor-section-tables">
                        <c:forEach var="t" items="${tables}">
                            <c:if test="${t.maxCapacity ge 2 && t.maxCapacity le 3}">
                                <c:set var="status" value="${tableStatusMap[t.tableId]}" />
                                <c:set var="statusClass" value="${status != null ? fn:toLowerCase(status) : 'available'}" />
                                <div class="table-node table-interactive status-${statusClass}"
                                     data-table-id="${t.tableId}"
                                     data-table-number="${t.tableNumber}"
                                     data-status="${statusClass}"
                                     onclick="showTableDetails(${t.tableId}, ${t.tableNumber}, '${statusClass}')">
                                    <div class="table-node-number">T${t.tableNumber}</div>
                                    <div class="table-node-cap">${t.minCapacity}–${t.maxCapacity}</div>
                                    <c:if test="${status != null}">
                                        <div class="table-node-status">${status}</div>
                                    </c:if>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>

            </div>

            <div class="floor-back-wall">
                Kitchen / Service Area
            </div>

        </div>
    </c:if>
</div>

<!-- Modal for table details -->
<div id="tableModal" class="modal">
    <div class="modal-content">
        <span class="modal-close" onclick="closeModal()">&times;</span>
        <h2>Table <span id="modalTableNumber"></span></h2>
        <div id="modalBody">
            <p>Loading...</p>
        </div>
        <div class="modal-actions">
            <a id="editTableLink" href="#" class="btn btn-secondary btn-small">Edit Table</a>
            <button id="deleteTableBtn" onclick="deleteTable()" class="btn btn-danger btn-small">Delete Table</button>
        </div>
    </div>
</div>

<script>
let currentTableId = null;
let currentTableNumber = null;

function changeDate(date) {
    window.location.href = '<c:url value="/admin/floor_plan"/>?date=' + date;
}

function showTableDetails(tableId, tableNumber, status) {
    currentTableId = tableId;
    currentTableNumber = tableNumber;

    document.getElementById('modalTableNumber').textContent = tableNumber;
    document.getElementById('editTableLink').href = '<c:url value="/admin/edit_table"/>?tableId=' + tableId;

    const modalBody = document.getElementById('modalBody');
    modalBody.innerHTML = '<p>Loading booking details...</p>';

    document.getElementById('tableModal').style.display = 'block';

    // Fetch booking details via AJAX
    fetch('<c:url value="/api/table_bookings"/>?tableId=' + tableId + '&date=${selectedDate}')
        .then(response => response.json())
        .then(data => {
            if (data.bookings && data.bookings.length > 0) {
                let html = '<h3>Bookings for ${selectedDate}</h3><div class="booking-list">';
                data.bookings.forEach(booking => {
                    html += '<div class="booking-item">';
                    html += '<p><strong>Time:</strong> ' + booking.time + '</p>';
                    html += '<p><strong>Guests:</strong> ' + booking.guests + '</p>';
                    html += '<p><strong>Customer:</strong> ' + booking.customerEmail + '</p>';
                    html += '<p><strong>Status:</strong> <span class="status-badge status-' + booking.status.toLowerCase() + '">' + booking.status + '</span></p>';
                    if (booking.requests) {
                        html += '<p><strong>Requests:</strong> ' + booking.requests + '</p>';
                    }
                    html += '</div>';
                });
                html += '</div>';
                modalBody.innerHTML = html;
            } else {
                modalBody.innerHTML = '<p>No bookings for this table on ${selectedDate}.</p>';
            }
        })
        .catch(error => {
            modalBody.innerHTML = '<p class="flash-error">Error loading bookings: ' + error.message + '</p>';
        });
}

function closeModal() {
    document.getElementById('tableModal').style.display = 'none';
}

function deleteTable() {
    if (confirm('Are you sure you want to delete Table ' + currentTableNumber + '?')) {
        window.location.href = '<c:url value="/admin/delete_table"/>?tableId=' + currentTableId;
    }
}

// Close modal when clicking outside of it
window.onclick = function(event) {
    const modal = document.getElementById('tableModal');
    if (event.target == modal) {
        closeModal();
    }
}
</script>

</body>
</html>
