<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Moderate Reviews - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <h3 class="mb-4" style="color: var(--hotel-primary);">
    <i class="bi bi-shield-check"></i> Moderate Reviews
  </h3>

  <c:forEach var="review" items="${reviews}">
  <div class="card mb-4">
    <div class="card-header d-flex justify-content-between align-items-center">
      <div>
        <span class="stars">
          <c:forEach begin="1" end="${review.rating}" var="s">&#9733;</c:forEach>
          <c:forEach begin="${review.rating + 1}" end="5" var="s">&#9734;</c:forEach>
        </span>
        <c:if test="${review.verified}">
          <span class="badge bg-success ms-2"><i class="bi bi-patch-check"></i> Verified</span>
        </c:if>
        <span class="badge bg-secondary ms-1">${review.reviewId}</span>
      </div>
      <form action="${pageContext.request.contextPath}/reviews" method="post" class="d-inline"
            onsubmit="return confirm('Delete this review?');">
        <input type="hidden" name="action" value="delete">
        <input type="hidden" name="reviewId" value="${review.reviewId}">
        <button type="submit" class="btn btn-sm btn-outline-danger">
          <i class="bi bi-trash"></i> Delete
        </button>
      </form>
    </div>
    <div class="card-body">
      <p class="mb-2">"${review.comment}"</p>
      <small class="text-muted">${review.displayFormat}</small>

      <c:if test="${not empty review.hotelResponse}">
      <div class="alert alert-light mt-3 p-2 border-start border-success border-3">
        <small><strong>Current Response:</strong> ${review.hotelResponse}</small>
      </div>
      </c:if>

      <form action="${pageContext.request.contextPath}/reviews" method="post" class="mt-3">
        <input type="hidden" name="action" value="moderate">
        <input type="hidden" name="reviewId" value="${review.reviewId}">
        <div class="input-group">
          <input type="text" name="hotelResponse" class="form-control"
                 placeholder="Add or update hotel response..."
                 value="${review.hotelResponse}">
          <button type="submit" class="btn btn-hotel-primary">
            <i class="bi bi-reply"></i> Respond
          </button>
        </div>
      </form>
    </div>
  </div>
  </c:forEach>

  <c:if test="${empty reviews}">
    <div class="text-center text-muted py-5">
      <i class="bi bi-chat-square" style="font-size:3rem;"></i>
      <p class="mt-3">No reviews to moderate.</p>
    </div>
  </c:if>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
