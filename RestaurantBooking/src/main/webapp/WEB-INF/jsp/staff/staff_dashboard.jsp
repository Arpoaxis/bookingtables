<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Staff Dashboard</title>
<link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body class="dashboard-body">

	<div class="home-link">
		<jsp:include page="/WEB-INF/jsp/header.jsp" />
	</div>

	<div class="dashboard-main">

		<!-- Header -->
		<div class="dashboard-header">
			<h1>Staff dashboard</h1>
			<p class="dashboard-subtitle">
				Welcome, <strong>${sessionScope.user.firstName}
					${sessionScope.user.lastName}</strong> – today’s bookings.
			</p>
		</div>

		<c:if test="${not empty error}">
			<p class="flash-message flash-error">
				<c:out value="${error}" />
			</p>
		</c:if>

		<!-- 2-column layout: left = floor plan, right = waitlist/bookings -->
		<div class="dashboard-grid">

			<!-- LEFT: compact floor plan -->
			<div>
				<div class="dashboard-card wide-card">
					<div class="dashboard-card-header-row">
						<div>
							<h2>Table plan</h2>
							<p class="dashboard-subtitle">Quick view of tonight’s tables.
								Click a table or open the full view for details.</p>
						</div>
						<a class="primary-link" href="<c:url value='/admin/floor_plan'/>">
							Open full view </a>
					</div>

					<!-- Mini room layout -->
					<div class="floorplan-mini-room floorplan-room">

						<!-- BAR ROW -->
						<div class="floor-row">
							<div class="floor-row-label">Bar seating (1-seat)</div>
							<div class="floor-row-tables">
								<c:forEach var="t" items="${tables}">
									<c:if test="${t.maxCapacity == 1}">
										<c:set var="status" value="${tableStatusMap[t.tableId]}" />
										<c:set var="statusClass"
											value="${status != null ? fn:toLowerCase(status) : 'available'}" />
										<div
											class="table-node table-interactive status-${statusClass}"
											data-table-id="${t.tableId}"
											data-table-number="${t.tableNumber}"
											title="Table ${t.tableNumber} (${t.minCapacity}-${t.maxCapacity})"
											onclick="window.location.href='<c:url value="/admin/floor_plan"/>';">
											<div class="table-node-number">T${t.tableNumber}</div>
											<div class="table-node-cap">${t.minCapacity}&ndash;${t.maxCapacity}</div>
											<c:if test="${status != null}">
												<div class="table-node-status">${status}</div>
											</c:if>
										</div>
									</c:if>
								</c:forEach>
							</div>
						</div>

						<div class="floor-aisle">Main aisle</div>

						<!-- MAIN DINING ROW -->
						<div class="floor-row">
							<div class="floor-row-label">Main dining (4+ seats)</div>
							<div class="floor-row-tables">
								<c:forEach var="t" items="${tables}">
									<c:if test="${t.maxCapacity ge 4}">
										<c:set var="status" value="${tableStatusMap[t.tableId]}" />
										<c:set var="statusClass"
											value="${status != null ? fn:toLowerCase(status) : 'available'}" />
										<div
											class="table-node table-interactive status-${statusClass}"
											data-table-id="${t.tableId}"
											data-table-number="${t.tableNumber}"
											title="Table ${t.tableNumber} (${t.minCapacity}-${t.maxCapacity})"
											onclick="window.location.href='<c:url value="/admin/floor_plan"/>';">
											<div class="table-node-number">T${t.tableNumber}</div>
											<div class="table-node-cap">${t.minCapacity}&ndash;${t.maxCapacity}</div>
											<c:if test="${status != null}">
												<div class="table-node-status">${status}</div>
											</c:if>
										</div>
									</c:if>
								</c:forEach>
							</div>
						</div>

						<!-- WINDOW ROW -->
						<div class="floor-row">
							<div class="floor-row-label">Window seating (2–3 seats)</div>
							<div class="floor-row-tables">
								<c:forEach var="t" items="${tables}">
									<c:if test="${t.maxCapacity ge 2 && t.maxCapacity le 3}">
										<c:set var="status" value="${tableStatusMap[t.tableId]}" />
										<c:set var="statusClass"
											value="${status != null ? fn:toLowerCase(status) : 'available'}" />
										<div
											class="table-node table-interactive status-${statusClass}"
											data-table-id="${t.tableId}"
											data-table-number="${t.tableNumber}"
											title="Table ${t.tableNumber} (${t.minCapacity}-${t.maxCapacity})"
											onclick="window.location.href='<c:url value="/admin/floor_plan"/>';">
											<div class="table-node-number">T${t.tableNumber}</div>
											<div class="table-node-cap">${t.minCapacity}&ndash;${t.maxCapacity}</div>
											<c:if test="${status != null}">
												<div class="table-node-status">${status}</div>
											</c:if>
										</div>
									</c:if>
								</c:forEach>
							</div>
						</div>

						<div class="floor-back-wall">Kitchen / service area</div>
					</div>
				</div>
			</div>

			<!-- RIGHT: waitlist + bookings -->
			<div>
				<div class="dashboard-card wide-card bookings-card">

					<div
						style="display: flex; justify-content: space-between; align-items: center;">
						<h2>Today’s bookings</h2>
						<span><c:out value="${today}" /></span>
					</div>

					<!-- WAITLIST -->
					<div class="dashboard-card wide-card">
						<h2>Waitlist</h2>

						<c:if test="${not empty sessionScope.error}">
							<p class="flash-message flash-error">
								<c:out value="${sessionScope.error}" />
							</p>
							<c:remove var="error" scope="session" />
						</c:if>

						<c:if test="${not empty sessionScope.success}">
							<p class="flash-message flash-success">
								<c:out value="${sessionScope.success}" />
							</p>
							<c:remove var="success" scope="session" />
						</c:if>

						<c:if test="${empty waitlist}">
							<p>No one is currently on the waitlist.</p>
						</c:if>

						<c:if test="${not empty waitlist}">
							<table class="status-table employees-table">
								<thead>
									<tr>
										<th>#</th>
										<th>Name</th>
										<th>Party</th>
										<th>Arrived</th>
										<th>Status</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="w" items="${waitlist}">
										<tr>
											<td><c:out value="${w.queuePosition}" /></td>
											<td><c:out value="${w.customerName}" /></td>
											<td><c:out value="${w.partySize}" /></td>
											<td><c:if test="${not empty w.arrivedAt}">
													<fmt:parseDate value="${w.arrivedAt}"
														pattern="yyyy-MM-dd HH:mm:ss" var="arrivedDt" />
													<fmt:formatDate value="${arrivedDt}" pattern="h:mm a" />
												</c:if></td>
											<td><c:out value="${w.status}" /></td>
											<td>
												<form method="post"
													action="<c:url value='/staff/waitlist'/>"
													style="display: inline;">
													<input type="hidden" name="csrf_token"
														value="${sessionScope.csrf_token}" /> <input
														type="hidden" name="waitlistId" value="${w.waitlistId}" />

													<c:if test="${w.status == 'WAITING'}">
														<button type="submit" name="action" value="notify"
															class="primary-link button-link"
															style="margin-right: 0.25rem;">Notify</button>
													</c:if>

													<c:if
														test="${w.status == 'WAITING' || w.status == 'NOTIFIED'}">
														<button type="submit" name="action" value="seat"
															class="primary-link button-link"
															style="margin-right: 0.25rem;">Seat</button>
													</c:if>

													<c:if
														test="${w.status == 'WAITING' || w.status == 'NOTIFIED'}">
														<button type="submit" name="action" value="cancel"
															class="danger-link button-link">Cancel</button>
													</c:if>
												</form>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</c:if>
					</div>

					<p style="margin-top: 1rem;">
						<a href="<c:url value='/staff/waitlist/new'/>">Add booking /
							walk-in</a>
					</p>

					<!-- BOOKINGS LIST (scrollable) -->
					<c:if test="${not empty bookings}">
						<div class="bookings-scroll">
							<!-- figure out which direction each column should toggle to -->
							<c:set var="currentSort" value="${param.sort}" />
							<c:set var="currentDir" value="${param.dir}" />

							<c:set var="timeDir"
								value="${currentSort == 'time'     && currentDir != 'desc' ? 'desc' : 'asc'}" />
							<c:set var="guestDir"
								value="${currentSort == 'guests'   && currentDir != 'desc' ? 'desc' : 'asc'}" />
							<c:set var="lastDir"
								value="${currentSort == 'lastName' && currentDir != 'desc' ? 'desc' : 'asc'}" />

							<table class="status-table booking-table">
								<thead>
									<tr>
										<th><a href="?sort=time&amp;dir=${timeDir}">Time</a></th>
										<th><a href="?sort=guests&amp;dir=${guestDir}">Guests</a></th>
										<th><a href="?sort=lastName&amp;dir=${lastDir}">Customer</a></th>
										<th>Table</th>
										<th>Notes</th>
										<th>Status</th>
										<th>Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="b" items="${bookings}">
										<tr class="booking-row" data-booking-id="${b.bookingId}">
											<td><c:out value="${b.displayTime}" /></td>
											<td><c:out value="${b.guests}" /></td>
											<td><c:out value="${b.customerFullName}" /></td>

											<!-- Table assignment dropdown -->
											<td><c:set var="assignedTableId"
													value="${bookingTableMap[b.bookingId]}" />
												<form method="post"
													action="<c:url value='/staff/bookings'/>"
													class="inline-form">
													<input type="hidden" name="csrf_token"
														value="${sessionScope.csrf_token}" /> <input
														type="hidden" name="bookingId" value="${b.bookingId}" />

													<select name="tableId">
														<option value="">-- none --</option>
														<c:forEach var="t" items="${allTables}">
															<option value="${t.tableId}"
																<c:if test="${assignedTableId != null && assignedTableId == t.tableId}">
                                                selected
                                            </c:if>>
																T${t.tableNumber} (${t.minCapacity}-${t.maxCapacity})</option>
														</c:forEach>
													</select>

													<button type="submit" name="action" value="assignTable"
														class="btn btn-small">Assign</button>
												</form></td>

											<td><c:out value="${b.requests}" /></td>
											<td><c:out value="${b.status}" /></td>

											<!-- Status buttons -->
											<td>
												<form method="post"
													action="<c:url value='/staff/bookings'/>"
													class="inline-form">
													<input type="hidden" name="csrf_token"
														value="${sessionScope.csrf_token}" /> <input
														type="hidden" name="bookingId" value="${b.bookingId}" />

													<button type="submit" name="action" value="confirm"
														class="btn btn-small">Confirm</button>
													<button type="submit" name="action" value="seat"
														class="btn btn-small">Seat</button>
													<button type="submit" name="action" value="complete"
														class="btn btn-small">Complete</button>
													<button type="submit" name="action" value="cancel"
														class="btn btn-small danger-btn">Cancel</button>
												</form>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>
						</div>
					</c:if>


				</div>
			</div>

		</div>
	</div>
	<script>
    document.addEventListener("DOMContentLoaded", function () {

        // Make booking rows draggable
        document.querySelectorAll(".booking-row").forEach(function (row) {
            row.setAttribute("draggable", "true");
            row.addEventListener("dragstart", function (e) {
                e.dataTransfer.setData("text/plain", row.dataset.bookingId);
                e.dataTransfer.effectAllowed = "move";
            });
        });

        // Make each table circle a drop target
        document.querySelectorAll(".table-node").forEach(function (node) {

            node.addEventListener("dragover", function (e) {
                e.preventDefault();
                e.dataTransfer.dropEffect = "move";
            });

            node.addEventListener("drop", function (e) {
                e.preventDefault();

                var bookingId = e.dataTransfer.getData("text/plain");
                var tableId   = node.dataset.tableId;

                if (!bookingId || !tableId) {
                    return;
                }

                fetch("<c:url value='/staff/bookings'/>", {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/x-www-form-urlencoded"
                    },
                    body:
                        "action=assignTable" +
                        "&bookingId=" + encodeURIComponent(bookingId) +
                        "&tableId="   + encodeURIComponent(tableId) +
                        "&csrf_token=" + encodeURIComponent("${sessionScope.csrf_token}")
                }).then(function (resp) {
                    if (resp.ok) {
                        // reload so colours / table numbers update
                        window.location.reload();
                    } else {
                        alert("Could not assign table.");
                    }
                }).catch(function (err) {
                    console.error(err);
                    alert("Error assigning table.");
                });
            });
        });
    });
    </script>
</body>
</html>

</body>
</html>
