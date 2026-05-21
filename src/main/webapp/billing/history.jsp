<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Payment History - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <h3 class="mb-4" style="color: var(--hotel-primary);">
    <i class="bi bi-receipt"></i> Payment History
  </h3>

  <div class="card">
    <div class="card-body p-0">
      <table class="table table-hover table-hotel mb-0">
        <thead>
          <tr>
            <th>Payment ID</th><th>Reservation ID</th><th>Type</th>
            <th>Amount</th><th>Status</th><th>Date</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="p" items="${payments}">
          <tr>
            <td><code>${p.paymentId}</code></td>
            <td><code>${p.reservationId}</code></td>
            <td>
              <i class="bi ${p.paymentType == 'Credit Card' ? 'bi-credit-card' : 'bi-cash'}"></i>
              ${p.paymentType}
            </td>
            <td class="text-success fw-bold">$${p.amount}</td>
            <td>
              <span class="badge
                <c:choose>
                  <c:when test="${p.status == 'PAID'}">bg-success</c:when>
                  <c:when test="${p.status == 'VOIDED'}">bg-danger</c:when>
                  <c:otherwise>bg-warning text-dark</c:otherwise>
                </c:choose>">
                ${p.status}
              </span>
            </td>
            <td class="small">${p.timestamp}</td>
            <td>
              <% if (session.getAttribute("loggedInStaff") != null) { %>
              <c:if test="${p.status == 'PAID'}">
              <form action="${pageContext.request.contextPath}/payments" method="post" class="d-inline"
                    onsubmit="return confirm('Void this payment?');">
                <input type="hidden" name="action" value="void">
                <input type="hidden" name="paymentId" value="${p.paymentId}">
                <button type="submit" class="btn btn-sm btn-outline-danger">
                  <i class="bi bi-x-circle"></i> Void
                </button>
              </form>
              </c:if>
              <% } %>
            </td>
          </tr>
          </c:forEach>
          <c:if test="${empty payments}">
            <tr><td colspan="7" class="text-center text-muted py-5">No payments found.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
