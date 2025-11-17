<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Admin Dashboard - Restaurant Booking</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>

<body>
  <div class="home-link">
  
    <jsp:include page="/WEB-INF/jsp/header.jsp"/>
  </div>

  <div class="dashboard-container">
    <h1>Administrator Dashboard</h1>

    <c:if test="${sessionScope.user != null and sessionScope.user.accountType == 'ADMIN'}">
      <p>Welcome, ${sessionScope.user.email}</p>
      <hr>
	  <div class="dashboard-summary">
       <p><strong>Total bookings:</strong> ${totalBookings}</p>
       <p><strong>Bookings today:</strong> ${bookingsToday}</p>
       <p><strong>Distinct customers:</strong> ${distinctCustomers}</p>

       <c:if test="${not empty reportError}">
           <p style="color:red;">${reportError}</p>
       </c:if>
   </div>
         <!-- NEW: Bookings by Status -->
      <h2>Bookings by Status</h2>

      <table class="status-table">
          <thead>
              <tr>
                  <th>Status</th>
                  <th>Count</th>
              </tr>
          </thead>
          <tbody>
              <tr>
                  <td>Pending</td>
                  <td>${statusCounts['PENDING']}</td>
              </tr>
              <tr>
                  <td>Confirmed</td>
                  <td>${statusCounts['CONFIRMED']}</td>
              </tr>
              <tr>
                  <td>Seated</td>
                  <td>${statusCounts['SEATED']}</td>
              </tr>
              <tr>
                  <td>Cancelled</td>
                  <td>${statusCounts['CANCELLED']}</td>
              </tr>
          </tbody>
      </table>
   
      	<h2>Management Options</h2>
		<ul>
    		<li><a href="<c:url value='/admin/manage_tables'/>">Manage Tables</a></li>
		</ul>



      <p><a href="<c:url value='/logout'/>">Logout</a></p>

    </c:if>

    <c:if test="${sessionScope.user == null or sessionScope.user.accountType != 'ADMIN'}">
      	<p style="color:red;">Access Denied: You must be logged in as an administrator to view this page.</p>
		<a href="<c:url value='/login'/>">Login</a>

    </c:if>
  </div>
</body>
</html>