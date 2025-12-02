<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<div class="app-header">

    <%-- Left: Project Name --%>
    <div class="header-left">
        <a href="${pageContext.request.contextPath}/" class="header-logo">
            Restaurant Booking
        </a>
    </div>

    <%-- Center: Search bar --%>
    <div class="header-search">
        <form action="<c:url value='/search'/>" method="get">
            <input type="text" name="q" placeholder="Search restaurants">
        </form>
    </div>

    <%-- Right: Profile dropdown --%>
    <div class="header-right">

        <div class="profile-wrapper" onclick="toggleProfileMenu()">

            <%-- Profile Icon --%>
           <div class="profile-icon">
			    <c:choose>
			
			        <%-- If user logged in, show image if they have one uploaded later --%>
			        <c:when test="${not empty sessionScope.user}">
			            <span class="profile-initial">
			                ${fn:toUpperCase(fn:substring(sessionScope.user.firstName, 0, 1))}
			            </span>
			        </c:when>
			
			        <%-- If no user logged in, show default profile image --%>
			        <c:otherwise>
			            <img src="${pageContext.request.contextPath}/images/default_profile.png"
			                 class="profile-default-img">
			        </c:otherwise>
			
			    </c:choose>
			</div>

            <%-- Dropdown menu --%>
            <div id="profileMenu" class="profile-menu hidden">

                <c:choose>

                    <c:when test="${not empty sessionScope.user}">
                        <div class="profile-menu-header">
                            <strong>${sessionScope.user.firstName} ${sessionScope.user.lastName}</strong>
                            <div class="profile-email">${sessionScope.user.email}</div>
                        </div>

                        <a href="<c:url value='/profile'/>">Profile</a>
                        <a href="<c:url value='/booking/mine'/>">My Reservations</a>

                        <c:if test="${sessionScope.role == 'ADMIN' || sessionScope.role == 'MANAGER'}">
                            <a href="<c:url value='/admin/dashboard'/>">Admin Dashboard</a>
                        </c:if>

                        <div class="profile-separator"></div>

                        <a href="<c:url value='/logout'/>" class="logout-link">Logout</a>
                    </c:when>

                    <c:otherwise>
                        <a href="<c:url value='/login'/>">Login/Register</a>
                       
                    </c:otherwise>

                </c:choose>

            </div>
        </div>
    </div>

</div>

<script>
function toggleProfileMenu() {
    const menu = document.getElementById("profileMenu");
    menu.classList.toggle("hidden");
}

// Close dropdown when clicking outside
document.addEventListener("click", function(e) {
    const profile = document.querySelector(".profile-wrapper");
    if (!profile.contains(e.target)) {
        document.getElementById("profileMenu").classList.add("hidden");
    }
});
</script>