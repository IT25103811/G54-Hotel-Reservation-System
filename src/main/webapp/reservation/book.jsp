<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.hotel.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Book a Room - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-7">
            <div class="card">
                <div class="card-header card-header-hotel">
                    <h4 class="mb-0"><i class="bi bi-calendar-plus"></i> Make a Reservation</h4>
                </div>
                <div class="card-body">
                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger">${error}</div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/reservations" method="post" id="bookForm">
                        <input type="hidden" name="action" value="book">

                        <div class="mb-3">
                            <label class="form-label">Guest ID *</label>
                            <%
                                Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
                                String guestIdVal = (loggedGuest != null) ? loggedGuest.getId() : "";
                            %>
                            <input type="text" name="guestId" class="form-control" required
                                   value="<%= guestIdVal %>"
                                <%= loggedGuest != null ? "readonly" : "" %>
                                   placeholder="Enter Guest ID">
                            <% if (loggedGuest == null) { %>
                            <div class="form-text text-muted">
                                <a href="${pageContext.request.contextPath}/guests?action=login">Login</a> to auto-fill your ID.
                            </div>
                            <% } %>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Select Room *</label>
                            <select name="roomNumber" id="roomSelect" class="form-select" required onchange="updatePriceDisplay()">
                                <option value="">-- Select Available Room --</option>
                                <c:forEach var="room" items="${availableRooms}">
                                    <option value="${room.roomNumber}"
                                            data-price="${room.calculatePrice()}">
                                        Room ${room.roomNumber} - ${room.type} - Floor ${room.floor} - $${room.calculatePrice()}/night
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">Check-In Date *</label>
                                <input type="date" name="checkIn" id="checkIn" class="form-control" required
                                       min="<%= java.time.LocalDate.now() %>" onchange="calcTotal()">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">Check-Out Date *</label>
                                <input type="date" name="checkOut" id="checkOut" class="form-control" required
                                       min="<%= java.time.LocalDate.now().plusDays(1) %>" onchange="calcTotal()">
                            </div>
                        </div>

                        <div class="alert alert-info" id="priceAlert" style="display:none;">
                            <strong><i class="bi bi-calculator"></i> Estimated Total:</strong>
                            <span id="totalDisplay"></span>
                            <% if (loggedGuest != null) { %>
                            <br><small>Includes <strong><%= (int)(loggedGuest.calculateDiscount()*100) %>% discount</strong>
                            as <%= (loggedGuest instanceof VIPGuest) ? "VIP" : "Regular" %> member</small>
                            <% } %>
                        </div>

                        <div class="d-grid mt-3">
                            <button type="submit" class="btn btn-hotel-primary btn-lg">
                                <i class="bi bi-check-circle"></i> Confirm Reservation
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var discount = <%= loggedGuest != null ? loggedGuest.calculateDiscount() : 0.0 %>;

    function updatePriceDisplay() { calcTotal(); }

    function calcTotal() {
        const roomSel = document.getElementById('roomSelect');
        const checkIn = document.getElementById('checkIn').value;
        const checkOut = document.getElementById('checkOut').value;
        const priceAlert = document.getElementById('priceAlert');

        if (roomSel.value && checkIn && checkOut) {
            const price = parseFloat(roomSel.selectedOptions[0].dataset.price);
            const nights = Math.ceil((new Date(checkOut) - new Date(checkIn)) / (1000*60*60*24));
            if (nights > 0) {
                const base = price * nights;
                const total = base * (1 - discount);
                document.getElementById('totalDisplay').innerHTML =
                    ' $' + total.toFixed(2) + ' (' + nights + ' night' + (nights > 1 ? 's' : '') + ')';
                priceAlert.style.display = 'block';
            }
        }
    }
</script>
</body>
</html>
