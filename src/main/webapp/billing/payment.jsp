<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Payment payment = (Payment) request.getAttribute("payment");
    Reservation res = (Reservation) request.getAttribute("reservation");
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Payment Receipt - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-lg-6">
      <div class="card">
        <div class="card-header" style="background: #28a745; color: white; text-align:center; padding:20px;">
          <i class="bi bi-check-circle-fill" style="font-size:2rem;"></i>
          <h4 class="mt-2 mb-0">Payment Successful!</h4>
        </div>
        <div class="card-body p-4">
          <h6 class="text-muted text-center mb-4">PAYMENT RECEIPT</h6>
          <table class="table table-borderless">
            <% if (payment != null) { %>
            <tr><th>Payment ID:</th><td><code><%= payment.getPaymentId() %></code></td></tr>
            <tr><th>Payment Type:</th><td><%= payment.getPaymentType() %></td></tr>
            <tr><th>Amount Paid:</th>
              <td><strong class="text-success fs-5">$<%= String.format("%.2f", payment.getAmount()) %></strong></td>
            </tr>
            <tr><th>Status:</th>
              <td><span class="badge bg-success"><%= payment.getStatus() %></span></td>
            </tr>
            <tr><th>Date/Time:</th>
              <td><%= payment.getTimestamp() != null ? payment.getTimestamp().toString().replace("T"," ") : "N/A" %></td>
            </tr>
            <% if (payment instanceof CreditCardPayment) {
                CreditCardPayment cc = (CreditCardPayment) payment; %>
            <tr><th>Card Holder:</th><td><%= cc.getCardHolder() %></td></tr>
            <tr><th>Card No:</th><td>**** **** **** <%= cc.getCardNumber() %></td></tr>
            <% } else if (payment instanceof CashPayment) { %>
            <tr><th>Received By:</th><td><%= ((CashPayment)payment).getReceivedBy() %></td></tr>
            <% } %>
            <% } %>
            <% if (res != null) { %>
            <tr><td colspan="2"><hr></td></tr>
            <tr><th>Reservation:</th><td><code><%= res.getReservationId() %></code></td></tr>
            <tr><th>Room:</th><td><%= res.getRoomNumber() %></td></tr>
            <tr><th>Check-In:</th><td><%= res.getCheckIn() %></td></tr>
            <tr><th>Check-Out:</th><td><%= res.getCheckOut() %></td></tr>
            <% } %>
          </table>

          <div class="d-flex gap-2 mt-4">
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-hotel-primary flex-grow-1">
              <i class="bi bi-house"></i> Home
            </a>
            <a href="${pageContext.request.contextPath}/reservations?action=list"
               class="btn btn-outline-secondary flex-grow-1">
              <i class="bi bi-calendar-check"></i> My Reservations
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
