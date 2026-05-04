<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Reservation res = (Reservation) request.getAttribute("reservation");
    if (res == null) { response.sendRedirect(request.getContextPath() + "/reservations?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Modify Reservation - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-lg-6">
            <div class="card">
                <div class="card-header card-header-hotel">
                    <h5 class="mb-0"><i class="bi bi-pencil-square"></i> Modify Reservation</h5>
                </div>
                <div class="card-body">
                    <div class="alert alert-secondary">
                        <strong>Reservation ID:</strong> <%= res.getReservationId() %><br>
                        <strong>Room:</strong> <%= res.getRoomNumber() %><br>
                        <strong>Current Status:</strong>
                        <span class="badge badge-<%= res.getStatus().name().toLowerCase() %>"><%= res.getStatus() %></span><br>
                        <strong>Total:</strong> $<%= String.format("%.2f", res.getTotalAmount()) %>
                    </div>

                    <form action="${pageContext.request.contextPath}/reservations" method="post">
                        <input type="hidden" name="action" value="modify">
                        <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">

                        <div class="row g-3 mb-3">
                            <div class="col-md-6">
                                <label class="form-label">New Check-In Date</label>
                                <input type="date" name="checkIn" class="form-control"
                                       value="<%= res.getCheckIn() != null ? res.getCheckIn().toString() : "" %>">
                            </div>
                            <div class="col-md-6">
                                <label class="form-label">New Check-Out Date</label>
                                <input type="date" name="checkOut" class="form-control"
                                       value="<%= res.getCheckOut() != null ? res.getCheckOut().toString() : "" %>">
                            </div>
                        </div>

                        <% if (session.getAttribute("loggedInStaff") != null) { %>
                        <div class="mb-3">
                            <label class="form-label">Update Status</label>
                            <select name="status" class="form-select">
                                <% for (Reservation.Status s : Reservation.Status.values()) { %>
                                <option value="<%= s.name() %>" <%= s == res.getStatus() ? "selected" : "" %>>
                                    <%= s.name() %>
                                </option>
                                <% } %>
                            </select>
                        </div>
                        <% } %>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-hotel-primary">
                                <i class="bi bi-save"></i> Update Reservation
                            </button>
                        </div>
                    </form>

                    <hr>
                    <div class="d-flex gap-2">
                        <a href="${pageContext.request.contextPath}/reservations?action=list"
                           class="btn btn-outline-secondary flex-grow-1">
                            <i class="bi bi-arrow-left"></i> Back
                        </a>
                        <form action="${pageContext.request.contextPath}/reservations" method="post" class="flex-grow-1"
                              onsubmit="return confirm('Cancel this reservation?');">
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">
                            <button type="submit" class="btn btn-danger w-100">
                                <i class="bi bi-x-circle"></i> Cancel Reservation
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
