<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Guest Reviews - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h3 style="color: var(--hotel-primary);"><i class="bi bi-star"></i> Guest Reviews</h3>
    <a href="${pageContext.request.contextPath}/reviews?action=submit" class="btn btn-hotel-primary">
      <i class="bi bi-pencil"></i> Write a Review
    </a>
  </div>

  <!-- Filter by Rating -->
  <div class="mb-4">
    <div class="d-flex gap-2 flex-wrap">
      <button class="btn btn-outline-secondary btn-sm" onclick="filterRating(0)">All</button>
      <button class="btn btn-outline-warning btn-sm" onclick="filterRating(5)">&#9733;&#9733;&#9733;&#9733;&#9733;</button>
      <button class="btn btn-outline-warning btn-sm" onclick="filterRating(4)">&#9733;&#9733;&#9733;&#9733;</button>
      <button class="btn btn-outline-warning btn-sm" onclick="filterRating(3)">&#9733;&#9733;&#9733;</button>
      <button class="btn btn-outline-warning btn-sm" onclick="filterRating(2)">&#9733;&#9733;</button>
      <button class="btn btn-outline-warning btn-sm" onclick="filterRating(1)">&#9733;</button>
    </div>
  </div>

  <div class="row g-4" id="reviewsContainer">
    <c:forEach var="review" items="${reviews}">
    <div class="col-md-6 review-card" data-rating="${review.rating}">
      <div class="card h-100">
        <div class="card-body">
          <div class="d-flex justify-content-between align-items-start mb-2">
            <div class="stars">
              <c:forEach begin="1" end="${review.rating}" var="s">&#9733;</c:forEach>
              <c:forEach begin="${review.rating + 1}" end="5" var="s">&#9734;</c:forEach>
            </div>
            <div class="d-flex gap-1">
              <c:if test="${review.verified}">
                <span class="badge bg-success small"><i class="bi bi-patch-check"></i> Verified</span>
              </c:if>

              <c:if test="${review.verified and not empty sessionScope.loggedInGuest and review.guestId eq sessionScope.loggedInGuest.id}">
                <a href="${pageContext.request.contextPath}/reviews?action=edit&reviewId=${review.reviewId}" class="btn btn-sm btn-outline-primary" style="padding: 0.1rem 0.5rem; font-size: 0.75rem;">
                  <i class="bi bi-pencil"></i> Edit
                </a>
              </c:if>
            </div>
          </div>

          <p class="card-text">"${review.comment}"</p>

          <div class="mt-2 text-muted small">
            ${review.displayFormat}
          </div>

          <c:if test="${not empty review.hotelResponse}">
          <div class="alert alert-light mt-3 mb-0 p-2 border-start border-primary border-3">
            <small><strong><i class="bi bi-reply"></i> Hotel Response:</strong> ${review.hotelResponse}</small>
          </div>
          </c:if>
        </div>
      </div>
    </div>
    </c:forEach>
    <c:if test="${empty reviews}">
      <div class="col-12 text-center text-muted py-5">
        <i class="bi bi-chat-square" style="font-size:3rem;"></i>
        <p class="mt-3">No reviews yet. Be the first to review!</p>
        <a href="${pageContext.request.contextPath}/reviews?action=submit" class="btn btn-hotel-primary">
          Write a Review
        </a>
      </div>
    </c:if>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function filterRating(rating) {
    document.querySelectorAll('.review-card').forEach(function(card) {
        if (rating === 0 || parseInt(card.dataset.rating) === rating) {
            card.style.display = '';
        } else {
            card.style.display = 'none';
        }
    });
}
</script>
</body>
</html>