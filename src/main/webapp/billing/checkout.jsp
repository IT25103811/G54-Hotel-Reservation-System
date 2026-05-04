<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Reservation res = (Reservation) request.getAttribute("reservation");
    if (res == null) { response.sendRedirect(request.getContextPath() + "/reservations?action=list"); return; }
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Checkout - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-lg-7">
      <div class="card">
        <div class="card-header card-header-hotel">
          <h4 class="mb-0"><i class="bi bi-credit-card"></i> Checkout &amp; Payment</h4>
        </div>
        <div class="card-body">

          <!-- Reservation Summary -->
          <div class="alert alert-info mb-4">
            <h6><i class="bi bi-calendar-check"></i> Reservation Summary</h6>
            <table class="table table-sm table-borderless mb-0">
              <tr><th>Reservation ID:</th><td><code><%= res.getReservationId() %></code></td></tr>
              <tr><th>Room:</th><td><strong><%= res.getRoomNumber() %></strong></td></tr>
              <tr><th>Check-In:</th><td><%= res.getCheckIn() %></td></tr>
              <tr><th>Check-Out:</th><td><%= res.getCheckOut() %></td></tr>
              <tr><th>Total Amount:</th>
                <td><strong class="text-success fs-5">$<%= String.format("%.2f", res.getTotalAmount()) %></strong></td>
              </tr>
            </table>
          </div>

          <!-- Payment Form -->
          <form action="${pageContext.request.contextPath}/payments" method="post" id="paymentForm">
            <input type="hidden" name="action" value="checkout">
            <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">

            <div class="mb-4">
              <label class="form-label fw-bold">Payment Method</label>
              <div class="d-flex gap-3">
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="paymentType"
                         value="CreditCard" id="payCC" checked onchange="togglePayFields()">
                  <label class="form-check-label" for="payCC">
                    <i class="bi bi-credit-card"></i> Credit Card
                  </label>
                </div>
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="paymentType"
                         value="Cash" id="payCash" onchange="togglePayFields()">
                  <label class="form-check-label" for="payCash">
                    <i class="bi bi-cash"></i> Cash
                  </label>
                </div>
              </div>
            </div>

            <div id="ccFields">
              <div class="mb-3">
                <label class="form-label">Card Holder Name</label>
                <input type="text" name="cardHolder" class="form-control" placeholder="Name on card">
              </div>
              <div class="mb-3">
                <label class="form-label">Card Number (last 4 digits)</label>
                <input type="text" name="cardLast4" class="form-control" maxlength="4"
                       placeholder="1234" pattern="\d{4}">
                <div class="form-text text-muted">
                  <i class="bi bi-shield-lock"></i> We only store the last 4 digits for security.
                </div>
              </div>
            </div>

            <div id="cashFields" style="display:none;">
              <div class="alert alert-secondary">
                <i class="bi bi-info-circle"></i>
                Cash payment will be recorded by the staff member processing this transaction.
              </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mt-4">
              <div>
                <strong>Total to Pay:</strong>
                <span class="text-success fs-4 fw-bold ms-2">
                  $<%= String.format("%.2f", res.getTotalAmount()) %>
                </span>
              </div>
              <button type="submit" class="btn btn-hotel-gold btn-lg">
                <i class="bi bi-check-circle"></i> Process Payment
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function togglePayFields() {
    const isCash = document.getElementById('payCash').checked;
    document.getElementById('ccFields').style.display = isCash ? 'none' : 'block';
    document.getElementById('cashFields').style.display = isCash ? 'block' : 'none';
}
</script>
</body>
</html>
