<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

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

		<!-- 2-column layout: left = table plan, right = bookings/waitlist -->
		<div class="dashboard-grid">

			<!-- LEFT: table plan -->
			<div>
				<div class="dashboard-card wide-card">
					<h2>Table plan</h2>
					<p>A visual table plan will be embedded here so hosts can see
						which tables are free/occupied.</p>
					<p>
						For now you can open the existing floor plan page: <a
							class="primary-link" href="<c:url value='/admin/floor_plan'/>">
							View floor plan </a>
					</p>
				</div>
			</div>

			<!-- RIGHT: today's bookings + waitlist -->
			<div>

				<div class="dashboard-card wide-card bookings-card">

					<div
						style="display: flex; justify-content: space-between; align-items: center;">
						<h2>Today’s bookings</h2>
						<span><c:out value="${today}" /></span>
					</div>

					<!-- ==== WAITLIST CARD ==== -->
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
											<td><c:out value="${w.arrivedAt}" /></td>
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
						<a href="<c:url value='/staff/waitlist/new'/>"> Add booking /
							walk-in </a>
					</p>

					<!-- ==== BOOKINGS LIST (scrollable) ==== -->

					<c:if test="${empty bookings}">
						<p>No bookings for today yet.</p>
					</c:if>

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
										<th>Notes</th>
										<th>Status</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="b" items="${bookings}">
										<tr>
											<td><c:out value="${b.displayTime}" /></td>
											<td><c:out value="${b.guests}" /></td>
											<td><c:out value="${b.customerFullName}" /></td>
											<td><c:out value="${b.requests}" /></td>
											<td><c:out value="${b.status}" /></td>
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

</body>
</html>
