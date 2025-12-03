<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
    <h1>Restaurant Floor Plan</h1>
    <p class="floorplan-subtitle">
        Window 2-tops at the front, 4-tops in the center, large party tables near the back.
        (Later we’ll color these by booking status and time.)
    </p>

    <c:if test="${not empty error}">
        <p class="flash-message flash-error">${error}</p>
    </c:if>

    <c:if test="${empty tables}">
        <p>No tables found. Add some tables first.</p>
    </c:if>

    <c:if test="${not empty tables}">
        <div class="floorplan-room">

            <!-- FRONT: window 2-tops -->
            <div class="floor-row">
                <div class="floor-row-label">Window 2-tops</div>
                <div class="floor-row-tables">
                    <c:forEach var="t" items="${tables}">
                        <c:if test="${t.maxCapacity le 2}">
                            <div class="table-node table-cap-2">
                                <div class="table-node-number">T${t.tableNumber}</div>
                                <div class="table-node-cap">
                                    ${t.minCapacity}–${t.maxCapacity} seats
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>

            <!-- Aisle between rows -->
            <div class="floor-aisle">
                Main aisle
            </div>

            <!-- MIDDLE: 4-tops in center -->
            <div class="floor-row">
                <div class="floor-row-label">Center 4-tops</div>
                <div class="floor-row-tables">
                    <c:forEach var="t" items="${tables}">
                        <c:if test="${t.maxCapacity == 4}">
                            <div class="table-node table-cap-4">
                                <div class="table-node-number">T${t.tableNumber}</div>
                                <div class="table-node-cap">
                                    ${t.minCapacity}–${t.maxCapacity} seats
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>

            <!-- BACK: big party tables -->
            <div class="floor-row">
                <div class="floor-row-label">Large parties</div>
                <div class="floor-row-tables">
                    <c:forEach var="t" items="${tables}">
                        <c:if test="${t.maxCapacity ge 6}">
                            <div class="table-node table-cap-big">
                                <div class="table-node-number">T${t.tableNumber}</div>
                                <div class="table-node-cap">
                                    ${t.minCapacity}–${t.maxCapacity} seats
                                </div>
                            </div>
                        </c:if>
                    </c:forEach>
                </div>
            </div>

            <!-- optional “Kitchen” wall at the very back -->
            <div class="floor-back-wall">
                Kitchen / Service Area
            </div>

        </div><!-- /floorplan-room -->
    </c:if>
</div><!-- /floorplan-main -->

</body>
</html>
