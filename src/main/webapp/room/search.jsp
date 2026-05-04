<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Search Rooms - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <h3 class="mb-4" style="color: var(--hotel-primary);"><i class="bi bi-search"></i> Search Rooms</h3>

  <div class="card mb-4">
    <div class="card-body">
      <form action="${pageContext.request.contextPath}/rooms" method="get" class="row g-3">
        <input type="hidden" name="action" value="search">
        <div class="col-md-3">
          <label class="form-label">Room Type</label>
          <input type="text" name="type" class="form-control" placeholder="e.g., Deluxe"
                 value="${param.type}">
        </div>
        <div class="col-md-3">
          <label class="form-label">Max Price ($/night)</label>
          <input type="number" name="maxPrice" class="form-control" placeholder="500"
                 value="${param.maxPrice}">
        </div>
        <div class="col-md-3">
          <label class="form-label">Availability</label>
          <select name="available" class="form-select">
            <option value="">Any</option>
            <option value="true" ${param.available == 'true' ? 'selected' : ''}>Available Only</option>
          </select>
        </div>
        <div class="col-md-3 d-flex align-items-end">
          <button type="submit" class="btn btn-hotel-primary w-100">
            <i class="bi bi-search"></i> Search
          </button>
        </div>
      </form>
    </div>
  </div>

  <c:if test="${not empty rooms}">
  <div class="card">
    <div class="card-body p-0">
      <table class="table table-hover table-hotel mb-0">
        <thead>
          <tr>
            <th>Room #</th><th>Type</th><th>Floor</th><th>Price/Night</th>
            <th>Amenities</th><th>Status</th><th>Action</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="room" items="${rooms}">
          <tr>
            <td><strong>${room.roomNumber}</strong></td>
            <td>${room.type}</td>
            <td>${room.floor}</td>
            <td class="text-success fw-bold">$${room.calculatePrice()}</td>
            <td class="small">${room.amenities}</td>
            <td>
              <span class="badge ${room.available ? 'bg-success' : 'bg-danger'}">
                ${room.available ? 'Available' : 'Occupied'}
              </span>
            </td>
            <td>
              <c:if test="${room.available}">
              <a href="${pageContext.request.contextPath}/reservations?action=book"
                 class="btn btn-sm btn-hotel-primary">Book</a>
              </c:if>
            </td>
          </tr>
          </c:forEach>
        </tbody>
      </table>
    </div>
  </div>
  </c:if>

  <c:if test="${empty rooms and not empty param.action}">
    <div class="text-center text-muted py-5">
      <i class="bi bi-search" style="font-size:3rem;"></i>
      <p class="mt-3">No rooms match your search criteria.</p>
    </div>
  </c:if>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
