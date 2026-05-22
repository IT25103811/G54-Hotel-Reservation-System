<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>All Rooms - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h3 style="color: var(--hotel-primary);"><i class="bi bi-door-open"></i> All Rooms</h3>
    <div>
      <a href="${pageContext.request.contextPath}/rooms?action=search" class="btn btn-outline-primary me-2">
        <i class="bi bi-search"></i> Search Rooms
      </a>
      <% if (session.getAttribute("loggedInStaff") != null) { %>
      <a href="${pageContext.request.contextPath}/rooms?action=add" class="btn btn-hotel-primary">
        <i class="bi bi-plus-circle"></i> Add Room
      </a>
      <% } %>
    </div>
  </div>

  <!-- Filter -->
  <div class="mb-3">
    <select id="availFilter" class="form-select w-auto" onchange="filterRooms()">
      <option value="all">All Rooms</option>
      <option value="available">Available Only</option>
      <option value="occupied">Occupied Only</option>
    </select>
  </div>

  <div class="row g-4" id="roomCards">
    <c:forEach var="room" items="${rooms}">
      <div class="col-md-4 room-card" data-available="${room.available}">
        <div class="card h-100"
             style="border-left: 4px solid ${room.available ? '#28a745' : '#dc3545'};">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <h5 class="card-title mb-0">Room ${room.roomNumber}</h5>
              <c:choose>
                <c:when test="${room.available}">
                  <span class="badge bg-success">Available</span>
                </c:when>
                <c:otherwise>
                  <span class="badge bg-danger">Occupied</span>
                </c:otherwise>
              </c:choose>
            </div>
            <p class="text-muted mb-1">
              <i class="bi bi-building"></i> Floor ${room.floor} &bull;
              <i class="bi bi-tag"></i> ${room.type}
            </p>
            <p class="mb-1">
              <strong class="text-success fs-5">$<fmt:formatNumber value="${room.calculatePrice()}" pattern="#,##0.00" /></strong>
              <span class="text-muted">/night</span>
            </p>
            <p class="text-muted small mb-2"><i class="bi bi-check2-circle"></i> ${room.amenities}</p>
            <div class="d-flex gap-2 mt-3">
              <% if (session.getAttribute("loggedInStaff") != null) { %>
              <a href="${pageContext.request.contextPath}/rooms?action=edit&roomNumber=${room.roomNumber}"
                 class="btn btn-sm btn-outline-primary flex-grow-1">
                <i class="bi bi-pencil"></i> Edit
              </a>
              <form action="${pageContext.request.contextPath}/rooms" method="post" class="flex-grow-1"
                    onsubmit="return confirm('Delete this room?');">
                <input type="hidden" name="action" value="delete">
                <input type="hidden" name="roomNumber" value="${room.roomNumber}">
                <button type="submit" class="btn btn-sm btn-outline-danger w-100">
                  <i class="bi bi-trash"></i> Delete
                </button>
              </form>
              <% } else { %>
              <c:if test="${room.available}">
                <a href="${pageContext.request.contextPath}/reservations?action=book"
                   class="btn btn-sm btn-hotel-primary flex-grow-1">
                  <i class="bi bi-calendar-plus"></i> Book Now
                </a>
              </c:if>
              <% } %>
            </div>
          </div>
        </div>
      </div>
    </c:forEach>
    <c:if test="${empty rooms}">
      <div class="col-12 text-center text-muted py-5">
        <i class="bi bi-door-closed" style="font-size:3rem;"></i>
        <p class="mt-3">No rooms found.</p>
      </div>
    </c:if>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
  function filterRooms() {
    const filter = document.getElementById('availFilter').value;
    document.querySelectorAll('.room-card').forEach(function(card) {
      const avail = card.dataset.available === 'true';
      if (filter === 'all') card.style.display = '';
      else if (filter === 'available') card.style.display = avail ? '' : 'none';
      else card.style.display = !avail ? '' : 'none';
    });
  }
</script>
</body>
</html>
