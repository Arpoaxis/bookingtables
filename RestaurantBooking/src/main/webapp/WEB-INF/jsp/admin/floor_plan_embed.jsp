<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<div class="floorplan-embed">

	<div class="floorplan-embed-header">
		<h3>Restaurant floor plan</h3>
		<div class="floorplan-embed-controls">
			<label for="embedDate">Date:</label> <input type="date"
				id="embedDate" value="${selectedDate}"
				onchange="staffFloorChangeDate(this.value)">
		</div>
	</div>

	<div class="floorplan-legend">
		<span class="legend-item"><span
			class="legend-dot status-available"></span> Available</span> <span
			class="legend-item"><span class="legend-dot status-pending"></span>
			Pending</span> <span class="legend-item"><span
			class="legend-dot status-confirmed"></span> Confirmed</span> <span
			class="legend-item"><span class="legend-dot status-seated"></span>
			Seated</span>
	</div>

	<c:if test="${not empty error}">
		<p class="flash-message flash-error">${error}</p>
	</c:if>

	<c:if test="${empty tables}">
		<p>No tables found.</p>
	</c:if>

	<c:if test="${not empty tables}">
		<div class="floorplan-room floorplan-room-compact">
			<div class="floorplan-layout">

				<!-- Bar seating (1 seat) -->
				<div class="floor-section floor-section-left">
					<div class="floor-section-label">Bar (1 seat)</div>
					<div class="floor-section-tables">
						<c:forEach var="t" items="${tables}">
							<c:if test="${t.maxCapacity == 1}">
								<c:set var="status" value="${tableStatusMap[t.tableId]}" />
								<c:set var="statusClass"
									value="${status != null ? fn:toLowerCase(status) : 'available'}" />
								<div class="table-node table-interactive status-${statusClass}"
									onclick="staffOpenFullPlan(${t.tableId})">
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

				<!-- Main dining (4+ seats) -->
				<div class="floor-section floor-section-middle">
					<div class="floor-section-label">Main dining (4+)</div>
					<div class="floor-section-tables">
						<c:forEach var="t" items="${tables}">
							<c:if test="${t.maxCapacity ge 4}">
								<c:set var="status" value="${tableStatusMap[t.tableId]}" />
								<c:set var="statusClass"
									value="${status != null ? fn:toLowerCase(status) : 'available'}" />
								<div class="table-node table-interactive status-${statusClass}"
									onclick="staffOpenFullPlan(${t.tableId})">
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

				<!-- Window seating (2–3 seats) -->
				<div class="floor-section floor-section-right">
					<div class="floor-section-label">Window (2–3)</div>
					<div class="floor-section-tables">
						<c:forEach var="t" items="${tables}">
							<c:if test="${t.maxCapacity ge 2 && t.maxCapacity le 3}">
								<c:set var="status" value="${tableStatusMap[t.tableId]}" />
								<c:set var="statusClass"
									value="${status != null ? fn:toLowerCase(status) : 'available'}" />
								<div class="table-node table-interactive status-${statusClass}"
									onclick="staffOpenFullPlan(${t.tableId})">
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

			<div class="floor-back-wall">Kitchen / Service</div>
		</div>
	</c:if>
</div>

<script>
    function staffFloorChangeDate(date) {
        // reload just the embedded view for the new date
        const params = new URLSearchParams(window.location.search);
        params.set("date", date);
        // keep us on the same staff page; the include will pass embedded=true
        window.location.search = params.toString();
    }

    function staffOpenFullPlan(tableId) {
        const ctx = '<c:out value="${pageContext.request.contextPath}"/>';
        const date = document.getElementById('embedDate').value;
        window.location.href = ctx + '/admin/floor_plan?date=' + encodeURIComponent(date)
                + '&focusTable=' + tableId;
    }
</script>
