<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*, com.hotel.dao.*" %>
<%
    Staff staff = (Staff) session.getAttribute("loggedInStaff");
    if (staff == null) { response.sendRedirect(request.getContextPath() + "/staff?action=login"); return; }

    GuestDAO guestDAO = new GuestDAO();
    RoomDAO roomDAO = new RoomDAO();
    ReservationDAO resDAO = new ReservationDAO();
    PaymentDAO payDAO = new PaymentDAO();

    int totalGuests = guestDAO.findAll().size();
    long availableRooms = roomDAO.findAll().stream().filter(com.hotel.model.Room::isAvailable).count();
    int activeRes = resDAO.findActive().size();
    double totalRevenue = payDAO.findAll().stream()
        .filter(p -> p.getStatus() == Payment.Status.PAID)
        .mapToDouble(Payment::getAmount).sum();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Dashboard - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <div>
      <h3 style="color: var(--hotel-primary);">
        <i class="bi bi-speedometer2"></i> Staff Dashboard
      </h3>
      <p class="text-muted mb-0">Welcome back, <strong><%= staff.getName() %></strong>
        &mdash; <%= staff.getRole() %></p>
    </div>
  </div>

  <!-- Stats Cards -->
  <div class="row g-4 mb-5">
    <div class="col-md-3">
      <div class="stat-card stat-card-blue">
        <div class="stat-number"><%= totalGuests %></div>
        <div class="stat-label"><i class="bi bi-people"></i> Total Guests</div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card stat-card-green">
        <div class="stat-number"><%= availableRooms %></div>
        <div class="stat-label"><i class="bi bi-door-open"></i> Available Rooms</div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card stat-card-orange">
        <div class="stat-number"><%= activeRes %></div>
        <div class="stat-label"><i class="bi bi-calendar-check"></i> Active Reservations</div>
      </div>
    </div>
    <div class="col-md-3">
      <div class="stat-card stat-card-purple">
        <div class="stat-number">$<%= String.format("%,.0f", totalRevenue) %></div>
        <div class="stat-label"><i class="bi bi-currency-dollar"></i> Total Revenue</div>
      </div>
    </div>
  </div>

  <!-- Quick Navigation -->
  <h5 class="mb-3">Quick Actions</h5>
  <div class="row g-3">
    <div class="col-md-2">
      <a href="${pageContext.request.contextPath}/guests?action=list"
         class="btn btn-outline-primary w-100 py-3 d-flex flex-column align-items-center">
        <i class="bi bi-people fs-3"></i><span class="mt-1 small">Guests</span>
      </a>
    </div>
    <div class="col-md-2">
      <a href="${pageContext.request.contextPath}/rooms?action=list"
         class="btn btn-outline-success w-100 py-3 d-flex flex-column align-items-center">
        <i class="bi bi-door-open fs-3"></i><span class="mt-1 small">Rooms</span>
      </a>
    </div>
    <div class="col-md-2">
      <a href="${pageContext.request.contextPath}/reservations?action=list"
         class="btn btn-outline-warning w-100 py-3 d-flex flex-column align-items-center">
        <i class="bi bi-calendar-check fs-3"></i><span class="mt-1 small">Reservations</span>
      </a>
    </div>
    <div class="col-md-2">
      <a href="${pageContext.request.contextPath}/payments?action=history"
         class="btn btn-outline-info w-100 py-3 d-flex flex-column align-items-center">
        <i class="bi bi-credit-card fs-3"></i><span class="mt-1 small">Payments</span>
      </a>
    </div>
    <div class="col-md-2">
      <a href="${pageContext.request.contextPath}/reviews?action=moderate"
         class="btn btn-outline-secondary w-100 py-3 d-flex flex-column align-items-center">
        <i class="bi bi-chat-square-text fs-3"></i><span class="mt-1 small">Reviews</span>
      </a>
    </div>
    <% if (staff instanceof Manager) { %>
    <div class="col-md-2">
      <a href="${pageContext.request.contextPath}/staff?action=manage"
         class="btn btn-outline-dark w-100 py-3 d-flex flex-column align-items-center">
        <i class="bi bi-person-badge fs-3"></i><span class="mt-1 small">Staff</span>
      </a>
    </div>
    <% } %>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
