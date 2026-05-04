<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Room room = (Room) request.getAttribute("room");
    if (room == null) { response.sendRedirect(request.getContextPath() + "/rooms?action=list"); return; }
    boolean isSuite = room instanceof SuiteRoom;
    boolean jacuzzi = isSuite && ((SuiteRoom)room).isHasJacuzzi();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Edit Room - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="form-section">
    <h3 class="text-center mb-4" style="color: var(--hotel-primary);">
      <i class="bi bi-pencil"></i> Edit Room <%= room.getRoomNumber() %>
    </h3>

    <form action="${pageContext.request.contextPath}/rooms" method="post">
      <input type="hidden" name="action" value="edit">
      <input type="hidden" name="roomNumber" value="<%= room.getRoomNumber() %>">

      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Room Number</label>
          <input type="text" class="form-control" value="<%= room.getRoomNumber() %>" disabled>
        </div>
        <div class="col-md-6">
          <label class="form-label">Floor</label>
          <input type="number" name="floor" class="form-control" value="<%= room.getFloor() %>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label">Room Type</label>
          <input type="text" name="type" class="form-control" value="<%= room.getType() %>" required>
        </div>
        <div class="col-md-6">
          <label class="form-label">Base Price ($/night)</label>
          <input type="number" name="price" class="form-control" value="<%= room.getPrice() %>" step="0.01" required>
        </div>
        <div class="col-12">
          <label class="form-label">Amenities</label>
          <input type="text" name="amenities" class="form-control" value="<%= room.getAmenities() != null ? room.getAmenities() : "" %>">
        </div>
        <div class="col-md-6">
          <label class="form-label">Availability</label>
          <select name="available" class="form-select">
            <option value="true" <%= room.isAvailable() ? "selected" : "" %>>Available</option>
            <option value="false" <%= !room.isAvailable() ? "selected" : "" %>>Occupied</option>
          </select>
        </div>
        <% if (isSuite) { %>
        <div class="col-md-6">
          <label class="form-label">&nbsp;</label>
          <div class="form-check mt-2">
            <input class="form-check-input" type="checkbox" name="hasJacuzzi" <%= jacuzzi ? "checked" : "" %>>
            <label class="form-check-label">Has Jacuzzi</label>
          </div>
        </div>
        <% } %>
      </div>

      <div class="d-grid mt-4">
        <button type="submit" class="btn btn-hotel-primary btn-lg">
          <i class="bi bi-save"></i> Update Room
        </button>
      </div>
    </form>
    <div class="text-center mt-3">
      <a href="${pageContext.request.contextPath}/rooms?action=list" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Back to Room List
      </a>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
