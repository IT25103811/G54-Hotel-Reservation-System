<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Add Room - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="form-section">
    <h3 class="text-center mb-4" style="color: var(--hotel-primary);">
      <i class="bi bi-plus-square"></i> Add New Room
    </h3>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger">${error}</div>
    <% } %>

    <form action="${pageContext.request.contextPath}/rooms" method="post">
      <input type="hidden" name="action" value="add">

      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Room Number *</label>
          <input type="text" name="roomNumber" class="form-control" required placeholder="e.g., 101">
        </div>
        <div class="col-md-6">
          <label class="form-label">Floor *</label>
          <input type="number" name="floor" class="form-control" required min="1" placeholder="1">
        </div>
        <div class="col-md-6">
          <label class="form-label">Room Type *</label>
          <input type="text" name="type" class="form-control" required placeholder="e.g., Deluxe, Economy">
        </div>
        <div class="col-md-6">
          <label class="form-label">Base Price ($/night) *</label>
          <input type="number" name="price" class="form-control" required step="0.01" min="0" placeholder="150.00">
        </div>
        <div class="col-12">
          <label class="form-label">Amenities</label>
          <input type="text" name="amenities" class="form-control" placeholder="WiFi, TV, Mini Bar, Air Conditioning">
        </div>
        <div class="col-md-6">
          <label class="form-label">Room Class *</label>
          <select name="roomClass" id="roomClass" class="form-select" onchange="toggleJacuzzi()">
            <option value="STANDARD">Standard</option>
            <option value="SUITE">Suite</option>
          </select>
        </div>
        <div class="col-md-6" id="jacuzziSection" style="display:none;">
          <label class="form-label">&nbsp;</label>
          <div class="form-check mt-2">
            <input class="form-check-input" type="checkbox" name="hasJacuzzi" id="hasJacuzzi">
            <label class="form-check-label" for="hasJacuzzi">Has Jacuzzi</label>
          </div>
        </div>
      </div>

      <div class="d-grid mt-4">
        <button type="submit" class="btn btn-hotel-primary btn-lg">
          <i class="bi bi-plus-circle"></i> Add Room
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
<script>
function toggleJacuzzi() {
    const cls = document.getElementById('roomClass').value;
    document.getElementById('jacuzziSection').style.display = (cls === 'SUITE') ? 'block' : 'none';
}
</script>
</body>
</html>
