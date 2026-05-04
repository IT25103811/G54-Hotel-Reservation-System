<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reservations - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 style="color: var(--hotel-primary);"><i class="bi bi-calendar-check"></i> Reservations</h3>
        <a href="${pageContext.request.contextPath}/reservations?action=book" class="btn btn-hotel-primary">
            <i class="bi bi-plus-circle"></i> New Reservation
        </a>
    </div>

    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover table-hotel mb-0">
                <thead>
                <tr>
                    <th>Reservation ID</th><th>Guest ID</th><th>Room</th>
                    <th>Check-In</th><th>Check-Out</th><th>Status</th>
                    <th>Total</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="res" items="${reservations}">
                    <tr>
                        <td><code>${res.reservationId}</code></td>
                        <td><code>${res.guestId}</code></td>
                        <td><strong>${res.roomNumber}</strong></td>
                        <td>${res.checkIn}</td>
                        <td>${res.checkOut}</td>
                        <td>
              <span class="badge
                <c:choose>
                  <c:when test="${res.status == 'CONFIRMED'}">bg-success</c:when>
                  <c:when test="${res.status == 'PENDING'}">bg-warning text-dark</c:when>
                  <c:when test="${res.status == 'CHECKED_IN'}">bg-info</c:when>
                  <c:when test="${res.status == 'CHECKED_OUT'}">bg-secondary</c:when>
                  <c:otherwise>bg-danger</c:otherwise>
                </c:choose>">
                      ${res.status}
              </span>
                        </td>
                        <td class="text-success fw-bold">$${res.totalAmount}</td>
                        <td>
                            <div class="d-flex gap-1">
                                <a href="${pageContext.request.contextPath}/reservations?action=modify&reservationId=${res.reservationId}"
                                   class="btn btn-sm btn-outline-primary">
                                    <i class="bi bi-pencil"></i>
                                </a>
                                <c:if test="${res.status != 'CANCELLED' and res.status != 'CHECKED_OUT'}">
                                    <a href="${pageContext.request.contextPath}/payments?action=checkout&reservationId=${res.reservationId}"
                                       class="btn btn-sm btn-success">
                                        <i class="bi bi-credit-card"></i> Pay
                                    </a>
                                </c:if>
                                <form action="${pageContext.request.contextPath}/reservations" method="post" class="d-inline"
                                      onsubmit="return confirm('Cancel this reservation?');">
                                    <input type="hidden" name="action" value="cancel">
                                    <input type="hidden" name="reservationId" value="${res.reservationId}">
                                    <button type="submit" class="btn btn-sm btn-outline-danger"
                                            <c:if test="${res.status == 'CANCELLED' or res.status == 'CHECKED_OUT'}">disabled</c:if>>
                                        <i class="bi bi-x-circle"></i>
                                    </button>
                                </form>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty reservations}">
                    <tr><td colspan="8" class="text-center text-muted py-5">No reservations found.</td></tr>
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
