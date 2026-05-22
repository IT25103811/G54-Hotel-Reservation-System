<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Reservation res = (Reservation) request.getAttribute("reservation");
    if (res == null) { response.sendRedirect(request.getContextPath() + "/reservations?action=list"); return; }
    boolean isStaff   = session.getAttribute("loggedInStaff") != null;
    // After payment, CONFIRMED reservations cannot be edited by guests (only staff can)
    boolean isEditable = res.getStatus() != Reservation.Status.CANCELLED
                      && res.getStatus() != Reservation.Status.CHECKED_OUT
                      && (isStaff || res.getStatus() != Reservation.Status.CONFIRMED);
    String statusCss   = res.getStatus().name();
    long nights        = res.getNights();
    String cancelFeeStr = String.format("%.2f", res.calculateCancellationFee());
    String resIdStr     = res.getReservationId();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Modify Reservation - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .page-header {
            background: linear-gradient(135deg, var(--hotel-primary), #2d6a9f);
            color: white;
            padding: 32px 0 50px;
            margin-bottom: -24px;
        }
        .detail-card { border-radius: 14px; border: none; box-shadow: 0 4px 20px rgba(26,60,94,0.12); }
        .info-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px 24px; }
        .info-item label { font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.5px; color: #6c757d; font-weight: 600; margin-bottom: 2px; }
        .info-item .val  { font-weight: 600; font-size: 0.95rem; }
        .status-badge {
            display: inline-flex; align-items: center; gap: 5px;
            padding: 5px 12px; border-radius: 20px; font-weight: 600; font-size: 0.8rem;
        }
        .s-PENDING    { background:#fff3cd; color:#856404; }
        .s-CONFIRMED  { background:#d1e7dd; color:#0f5132; }
        .s-CHECKED_IN { background:#cff4fc; color:#055160; }
        .s-CHECKED_OUT{ background:#e2e3e5; color:#383d41; }
        .s-CANCELLED  { background:#f8d7da; color:#842029; }
        .form-control:focus, .form-select:focus {
            border-color: var(--hotel-primary);
            box-shadow: 0 0 0 0.2rem rgba(26,60,94,0.15);
        }
        .section-label {
            font-size: 0.78rem; text-transform: uppercase; letter-spacing: 0.5px;
            color: var(--hotel-primary); font-weight: 700;
            margin-bottom: 12px; padding-bottom: 8px;
            border-bottom: 2px solid #e0e8f0;
        }
        .btn-update {
            background: linear-gradient(135deg, var(--hotel-primary), #2d6a9f);
            color: white; border: none; border-radius: 8px;
            padding: 12px 28px; font-weight: 600; transition: all 0.2s;
        }
        .btn-update:hover { transform: translateY(-1px); box-shadow: 0 4px 16px rgba(26,60,94,0.3); color: white; }
        .new-total { color: var(--hotel-primary); font-weight: 700; font-size: 1.05rem; }
    </style>
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="page-header">
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-1" style="--bs-breadcrumb-divider-color:rgba(255,255,255,0.4);">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/index.jsp" class="text-white-50">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/reservations?action=list" class="text-white-50">Reservations</a></li>
                <li class="breadcrumb-item active text-white">Modify</li>
            </ol>
        </nav>
        <h3 class="fw-bold mb-0"><i class="bi bi-pencil-square me-2"></i>Modify Reservation</h3>
    </div>
</div>

<div class="container py-5">

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        <strong>Error:</strong> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <div class="row g-4">
        <!-- Current Info Panel -->
        <div class="col-lg-4">
            <div class="card detail-card h-100">
                <div class="card-header card-header-hotel">
                    <h6 class="mb-0"><i class="bi bi-info-circle me-2"></i>Current Details</h6>
                </div>
                <div class="card-body p-4">
                    <div class="info-grid mb-4">
                        <div class="info-item">
                            <label>Reservation ID</label>
                            <div class="val" style="font-family:monospace; font-size:0.82rem; color:var(--hotel-primary);">
                                <%= res.getReservationId() %>
                            </div>
                        </div>
                        <div class="info-item">
                            <label>Status</label>
                            <div>
                                <span class="status-badge s-<%= statusCss %>">
                                    <% if (res.getStatus() == Reservation.Status.PENDING)     { %><i class="bi bi-clock-fill"></i>
                                    <% } else if (res.getStatus() == Reservation.Status.CONFIRMED)  { %><i class="bi bi-check-circle-fill"></i>
                                    <% } else if (res.getStatus() == Reservation.Status.CHECKED_IN) { %><i class="bi bi-door-open-fill"></i>
                                    <% } else { %><i class="bi bi-x-circle-fill"></i><% } %>
                                    <%= res.getStatus() %>
                                </span>
                            </div>
                        </div>
                        <div class="info-item">
                            <label>Room</label>
                            <div class="val">Room <%= res.getRoomNumber() %></div>
                        </div>
                        <div class="info-item">
                            <label>Nights</label>
                            <div class="val"><%= nights %> night<%= nights != 1 ? "s" : "" %></div>
                        </div>
                        <div class="info-item">
                            <label>Check-In</label>
                            <div class="val"><%= res.getCheckIn() != null ? res.getCheckIn() : "—" %></div>
                        </div>
                        <div class="info-item">
                            <label>Check-Out</label>
                            <div class="val"><%= res.getCheckOut() != null ? res.getCheckOut() : "—" %></div>
                        </div>
                    </div>

                    <div class="section-label">Current Total</div>
                    <div style="font-size:1.5rem; font-weight:700; color:#1a6b3c;">
                        $<%= String.format("%.2f", res.getTotalAmount()) %>
                    </div>

                    <% if (res.getSpecialRequests() != null && !res.getSpecialRequests().isEmpty()) { %>
                    <div class="mt-3">
                        <div class="section-label">Special Requests</div>
                        <p class="text-muted small mb-0"><%= res.getSpecialRequests() %></p>
                    </div>
                    <% } %>

                    <% if (res.getCreatedAt() != null && !res.getCreatedAt().isEmpty()) { %>
                    <div class="mt-3">
                        <small class="text-muted"><i class="bi bi-clock-history me-1"></i>Booked: <%= res.getCreatedAt() %></small>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>

        <!-- Edit Form -->
        <div class="col-lg-8">
            <div class="card detail-card">
                <div class="card-header card-header-hotel">
                    <h6 class="mb-0"><i class="bi bi-sliders me-2"></i>Update Reservation</h6>
                </div>
                <div class="card-body p-4">

                    <% if (!isEditable) { %>
                    <div class="alert alert-warning">
                        <i class="bi bi-lock-fill me-2"></i>
                        This reservation is <strong><%= res.getStatus().name().toLowerCase() %></strong>
                        and cannot be modified.
                    </div>
                    <% } else { %>

                    <form action="${pageContext.request.contextPath}/reservations"
                          method="post" id="modifyForm" novalidate>
                        <input type="hidden" name="action" value="modify">
                        <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">

                        <!-- Dates -->
                        <div class="section-label"><i class="bi bi-calendar-range me-1"></i>Update Stay Dates</div>
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">New Check-In Date</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-calendar-event"></i></span>
                                    <input type="date" name="checkIn" id="checkIn" class="form-control"
                                           value="<%= res.getCheckIn() != null ? res.getCheckIn() : "" %>"
                                           min="<%= java.time.LocalDate.now() %>"
                                           onchange="onCheckInChange()">
                                </div>
                                <div id="checkInError" class="text-danger small mt-1" style="display:none;"></div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">New Check-Out Date</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-calendar-check"></i></span>
                                    <input type="date" name="checkOut" id="checkOut" class="form-control"
                                           value="<%= res.getCheckOut() != null ? res.getCheckOut() : "" %>"
                                           min="<%= java.time.LocalDate.now().plusDays(1) %>"
                                           onchange="calcNewTotal()">
                                </div>
                                <div id="checkOutError" class="text-danger small mt-1" style="display:none;"></div>
                            </div>
                        </div>

                        <!-- Live recalculation preview -->
                        <div id="newTotalDiv" class="alert alert-info mb-4" style="display:none;">
                            <i class="bi bi-calculator me-2"></i>
                            New estimated total: <span id="newTotalVal" class="new-total"></span>
                            <small class="d-block text-muted mt-1">(based on current room rate; final amount calculated on save)</small>
                        </div>

                        <!-- Special Requests -->
                        <div class="section-label"><i class="bi bi-chat-text me-1"></i>Special Requests</div>
                        <div class="mb-4">
                            <textarea name="specialRequests" class="form-control" rows="3" maxlength="300"
                                      placeholder="Update special requests (optional)"><%= res.getSpecialRequests() %></textarea>
                        </div>

                        <!-- Status (staff only) -->
                        <% if (isStaff) { %>
                        <div class="section-label"><i class="bi bi-toggle-on me-1"></i>Update Status
                            <span class="badge bg-warning text-dark fw-normal ms-2" style="font-size:0.7rem;">Staff Only</span>
                        </div>
                        <div class="mb-4">
                            <select name="status" class="form-select">
                                <% for (Reservation.Status s : Reservation.Status.values()) { %>
                                <option value="<%= s.name() %>" <%= s == res.getStatus() ? "selected" : "" %>>
                                    <%= s.name() %>
                                </option>
                                <% } %>
                            </select>
                            <div class="form-text text-warning">
                                <i class="bi bi-exclamation-triangle"></i>
                                Changing to CHECKED_OUT or CANCELLED will automatically release the room.
                            </div>
                        </div>
                        <% } %>

                        <div class="d-flex gap-3">
                            <button type="submit" class="btn btn-update flex-grow-1" id="submitBtn">
                                <i class="bi bi-save me-2"></i>Save Changes
                            </button>
                            <a href="${pageContext.request.contextPath}/reservations?action=list"
                               class="btn btn-outline-secondary">
                                <i class="bi bi-arrow-left me-1"></i>Back
                            </a>
                        </div>
                    </form>

                    <% } /* end isEditable */ %>

                    <!-- Cancel button shown always (if editable) -->
                    <% if (isEditable) { %>
                    <hr class="my-4">
                    <div class="d-flex align-items-center justify-content-between">
                        <div>
                            <strong class="text-danger"><i class="bi bi-x-octagon me-1"></i>Cancel Reservation</strong>
                            <div class="text-muted small mt-1">
                                Cancellation fee:
                                <strong>$<%= String.format("%.2f", res.calculateCancellationFee()) %></strong>
                                <% double fee = res.calculateCancellationFee();
                                   if (fee == 0) { %>
                                <span class="badge bg-success ms-1">Free cancellation</span>
                                <% } else if (fee < res.getTotalAmount()) { %>
                                <span class="badge bg-warning text-dark ms-1">50% fee applies</span>
                                <% } else { %>
                                <span class="badge bg-danger ms-1">Full charge applies</span>
                                <% } %>
                            </div>
                        </div>
                        <form action="${pageContext.request.contextPath}/reservations" method="post"
                              onsubmit="return confirm('Cancel reservation <%= resIdStr %>?\n\nCancellation fee: $<%= cancelFeeStr %>\n\nThis cannot be undone.')">
                            <input type="hidden" name="action" value="cancel">
                            <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">
                            <button type="submit" class="btn btn-outline-danger">
                                <i class="bi bi-x-circle me-1"></i>Cancel Reservation
                            </button>
                        </form>
                    </div>
                    <% } %>

                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var currentTotal  = <%= res.getTotalAmount() %>;
    var currentNights = <%= nights > 0 ? nights : 1 %>;
    var ratePerNight  = currentTotal / currentNights;

    // Today at midnight for accurate comparison
    var today = new Date();
    today.setHours(0, 0, 0, 0);

    function toDate(str) {
        return str ? new Date(str + 'T00:00:00') : null;
    }

    function showError(elId, msg) {
        var el = document.getElementById(elId);
        el.textContent = msg;
        el.style.display = msg ? 'block' : 'none';
    }

    function onCheckInChange() {
        var ci = document.getElementById('checkIn').value;
        showError('checkInError', '');

        if (ci) {
            var ciDate = toDate(ci);

            // Client-side: check-in cannot be in the past
            if (ciDate < today) {
                showError('checkInError', 'Check-in date cannot be in the past.');
                document.getElementById('checkIn').value = '';
                document.getElementById('newTotalDiv').style.display = 'none';
                return;
            }

            // Keep checkOut min always = checkIn + 1 day
            var nextDay = new Date(ciDate.getTime() + 86400000).toISOString().split('T')[0];
            document.getElementById('checkOut').min = nextDay;

            // If existing checkOut is now invalid (on or before new checkIn), clear it
            var co = document.getElementById('checkOut').value;
            if (co && co <= ci) {
                document.getElementById('checkOut').value = '';
                showError('checkOutError', 'Check-out date must be after check-in date.');
            }
        }
        calcNewTotal();
    }

    function calcNewTotal() {
        var ci  = document.getElementById('checkIn').value;
        var co  = document.getElementById('checkOut').value;
        var div = document.getElementById('newTotalDiv');
        var val = document.getElementById('newTotalVal');
        showError('checkOutError', '');

        if (ci && co) {
            var ciDate = toDate(ci);
            var coDate = toDate(co);

            // Client-side: check-out must be after check-in
            if (coDate <= ciDate) {
                showError('checkOutError', 'Check-out date must be after check-in date.');
                div.style.display = 'none';
                return;
            }

            var n = Math.round((coDate - ciDate) / 86400000);
            if (n > 0) {
                val.textContent = '$' + (ratePerNight * n).toFixed(2)
                    + ' (' + n + ' night' + (n !== 1 ? 's' : '') + ')';
                div.style.display = 'block';
                return;
            }
        }
        div.style.display = 'none';
    }

    // Client-side form submit validation — last line of defence before server
    document.getElementById('modifyForm').addEventListener('submit', function (e) {
        var ci = document.getElementById('checkIn').value;
        var co = document.getElementById('checkOut').value;

        // Only validate if either date field has been touched
        if (ci || co) {
            if (!ci || !co) {
                e.preventDefault();
                alert('Please provide both a check-in and check-out date.');
                return;
            }

            var ciDate = toDate(ci);
            var coDate = toDate(co);

            if (ciDate < today) {
                e.preventDefault();
                showError('checkInError', 'Check-in date cannot be in the past.');
                document.getElementById('checkIn').focus();
                return;
            }

            if (coDate <= ciDate) {
                e.preventDefault();
                showError('checkOutError', 'Check-out date must be after check-in date.');
                document.getElementById('checkOut').focus();
                return;
            }

            var nights = Math.round((coDate - ciDate) / 86400000);
            if (nights > 90) {
                e.preventDefault();
                alert('Reservations cannot exceed 90 nights.');
                return;
            }
        }
    });

    window.addEventListener('DOMContentLoaded', calcNewTotal);
</script>
</body>
</html>
