<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Reservation res = (Reservation) request.getAttribute("reservation");
    if (res == null) { response.sendRedirect(request.getContextPath() + "/reservations?action=list"); return; }
    boolean isStaff = session.getAttribute("loggedInStaff") != null;
    String formError = (String) request.getAttribute("errorMessage");
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

          <%-- Inline error from server-side re-validation --%>
          <% if (formError != null && !formError.isEmpty()) { %>
          <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
            <i class="bi bi-exclamation-triangle-fill me-2"></i><%= formError %>
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
          </div>
          <% } %>

          <!-- Reservation Summary -->
          <div class="alert alert-info mb-4">
            <h6><i class="bi bi-calendar-check"></i> Reservation Summary</h6>
            <table class="table table-sm table-borderless mb-0">
              <tr><th>Reservation ID:</th><td><code><%= res.getReservationId() %></code></td></tr>
              <tr><th>Room:</th><td><strong><%= res.getRoomNumber() %></strong></td></tr>
              <tr><th>Check-In:</th><td><%= res.getCheckIn() %></td></tr>
              <tr><th>Check-Out:</th><td><%= res.getCheckOut() %></td></tr>
              <tr><th>Status:</th>
                <td><span class="badge bg-warning text-dark"><%= res.getStatus() %></span></td>
              </tr>
              <tr><th>Total Amount:</th>
                <td><strong class="text-success fs-5">$<%= String.format("%.2f", res.getTotalAmount()) %></strong></td>
              </tr>
            </table>
          </div>

          <!-- Payment Form -->
          <form action="${pageContext.request.contextPath}/payments" method="post"
                id="paymentForm" novalidate>
            <input type="hidden" name="action" value="checkout">
            <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">

            <!-- Payment Method Selection -->
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
                <%-- Cash option: only available to logged-in staff --%>
                <% if (isStaff) { %>
                <div class="form-check">
                  <input class="form-check-input" type="radio" name="paymentType"
                         value="Cash" id="payCash" onchange="togglePayFields()">
                  <label class="form-check-label" for="payCash">
                    <i class="bi bi-cash"></i> Cash
                    <span class="badge bg-secondary ms-1" style="font-size:0.65rem;">Staff Only</span>
                  </label>
                </div>
                <% } %>
              </div>
              <% if (!isStaff) { %>
              <div class="form-text text-muted mt-1">
                <i class="bi bi-info-circle"></i>
                Online payments are processed by Credit Card only.
                Cash payments must be done at the front desk.
              </div>
              <% } %>
            </div>

            <!-- Credit Card Fields -->
            <div id="ccFields">
              <div class="mb-3">
                <label class="form-label" for="cardHolder">Card Holder Name <span class="text-danger">*</span></label>
                <input type="text" name="cardHolder" id="cardHolder" class="form-control"
                       placeholder="Name on card" autocomplete="cc-name">
                <div class="invalid-feedback" id="cardHolderError">Card holder name is required.</div>
              </div>
              <div class="mb-3">
                <label class="form-label" for="cardLast4">Card Number (last 4 digits) <span class="text-danger">*</span></label>
                <input type="text" name="cardLast4" id="cardLast4" class="form-control"
                       maxlength="4" placeholder="1234" pattern="\d{4}"
                       autocomplete="off" inputmode="numeric">
                <div class="invalid-feedback" id="cardLast4Error">Please enter exactly 4 digits.</div>
                <div class="form-text text-muted">
                  <i class="bi bi-shield-lock"></i> We only store the last 4 digits for security.
                </div>
              </div>
            </div>

            <!-- Cash Fields (staff only) -->
            <div id="cashFields" style="display:none;">
              <div class="alert alert-secondary">
                <i class="bi bi-person-badge"></i>
                Cash payment will be recorded under your staff account:
                <strong><%= isStaff && session.getAttribute("loggedInStaff") != null
                    ? ((com.hotel.model.Staff) session.getAttribute("loggedInStaff")).getName()
                    : "" %></strong>
              </div>
            </div>

            <div class="d-flex justify-content-between align-items-center mt-4">
              <div>
                <strong>Total to Pay:</strong>
                <span class="text-success fs-4 fw-bold ms-2">
                  $<%= String.format("%.2f", res.getTotalAmount()) %>
                </span>
              </div>
              <button type="submit" class="btn btn-hotel-gold btn-lg" id="payBtn">
                <i class="bi bi-lock-fill me-1"></i> Process Payment
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
    var isCash = document.getElementById('payCash') && document.getElementById('payCash').checked;
    document.getElementById('ccFields').style.display  = isCash ? 'none' : 'block';
    document.getElementById('cashFields').style.display = isCash ? 'block' : 'none';

    // Toggle required on card fields
    var cardHolder = document.getElementById('cardHolder');
    var cardLast4  = document.getElementById('cardLast4');
    if (cardHolder) cardHolder.required = !isCash;
    if (cardLast4)  cardLast4.required  = !isCash;
}

// Client-side validation before submit
document.getElementById('paymentForm').addEventListener('submit', function(e) {
    var isCash = document.getElementById('payCash') && document.getElementById('payCash').checked;
    var valid = true;

    if (!isCash) {
        var cardHolder = document.getElementById('cardHolder');
        var cardLast4  = document.getElementById('cardLast4');

        if (!cardHolder.value.trim()) {
            cardHolder.classList.add('is-invalid');
            valid = false;
        } else {
            cardHolder.classList.remove('is-invalid');
        }

        if (!/^\d{4}$/.test(cardLast4.value.trim())) {
            cardLast4.classList.add('is-invalid');
            valid = false;
        } else {
            cardLast4.classList.remove('is-invalid');
        }
    }

    if (!valid) e.preventDefault();
});

// Initialise on load
window.addEventListener('DOMContentLoaded', function() {
    togglePayFields();
    // Set required on card fields by default (Credit Card is pre-selected)
    var cardHolder = document.getElementById('cardHolder');
    var cardLast4  = document.getElementById('cardLast4');
    if (cardHolder) cardHolder.required = true;
    if (cardLast4)  cardLast4.required  = true;
});
</script>
</body>
</html>
