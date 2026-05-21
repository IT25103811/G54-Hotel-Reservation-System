<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Review editReview = (Review) request.getAttribute("review");
    boolean isEdit = (editReview != null);
    Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Submit Review - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="form-section">
    <h3 class="text-center mb-4" style="color: var(--hotel-primary);">
      <i class="bi bi-star"></i> <%= isEdit ? "Edit Review" : "Submit a Review" %>
    </h3>

    <form action="${pageContext.request.contextPath}/reviews" method="post">
      <input type="hidden" name="action" value="<%= isEdit ? "edit" : "submit" %>">
      <% if (isEdit) { %>
      <input type="hidden" name="reviewId" value="<%= editReview.getReviewId() %>">
      <% } %>

      <div class="mb-4">
        <label class="form-label fw-bold">Rating *</label>
        <div class="d-flex gap-2">
          <% for (int i = 1; i <= 5; i++) {
              boolean checked = isEdit && editReview.getRating() == i; %>
          <div class="form-check form-check-inline">
            <input class="form-check-input" type="radio" name="rating"
                   id="star<%= i %>" value="<%= i %>" <%= checked ? "checked" : (i==5 && !isEdit ? "checked" : "") %> required>
            <label class="form-check-label" for="star<%= i %>">
              <% for (int s = 0; s < i; s++) { %><i class="bi bi-star-fill text-warning"></i><% } %>
              <%= i %>
            </label>
          </div>
          <% } %>
        </div>
      </div>

      <div class="mb-3">
        <label class="form-label">Your Review *</label>
        <textarea name="comment" class="form-control" rows="4" required
                  placeholder="Share your experience at Grand Vista Hotel..."><%= isEdit ? editReview.getComment() : "" %></textarea>
      </div>

      <% if (!isEdit) { %>
      <% if (loggedGuest != null) { %>
      <div class="mb-3">
        <label class="form-label">Reservation ID (optional)</label>
        <input type="text" name="reservationId" class="form-control"
               placeholder="Your reservation ID for verified review">
      </div>
      <% } %>
      <div class="mb-3">
        <div class="form-check">
          <input class="form-check-input" type="checkbox" name="anonymous"
                 id="anonymous" onchange="toggleAlias()">
          <label class="form-check-label" for="anonymous">Submit anonymously</label>
        </div>
      </div>
      <div class="mb-3" id="aliasSection" style="display:none;">
        <label class="form-label">Alias</label>
        <input type="text" name="alias" class="form-control" placeholder="Your alias (e.g., HappyTraveler)">
      </div>
      <% } %>

      <div class="d-grid mt-4">
        <button type="submit" class="btn btn-hotel-primary btn-lg">
          <i class="bi bi-send"></i> <%= isEdit ? "Update Review" : "Submit Review" %>
        </button>
      </div>
    </form>
    <div class="text-center mt-3">
      <a href="${pageContext.request.contextPath}/reviews?action=view" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Back to Reviews
      </a>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function toggleAlias() {
    const anon = document.getElementById('anonymous').checked;
    document.getElementById('aliasSection').style.display = anon ? 'block' : 'none';
}
</script>
</body>
</html>
