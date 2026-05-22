<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Reservation res = (Reservation) request.getAttribute("reservation");
    if (res == null) { response.sendRedirect(request.getContextPath() + "/reservations?action=list"); return; }
    String guestName = (String) request.getAttribute("guestName");
    Double cancellationFee = (Double) request.getAttribute("cancellationFee");
    if (cancellationFee == null) cancellationFee = 0.0;
    boolean isStaff = session.getAttribute("loggedInStaff") != null;
    long nights = res.getNights();
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reservation Details - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .page-header { background: linear-gradient(135deg, var(--hotel-primary), #2d6a9f); color: white; padding: 32px 0 50px; margin-bottom: -24px; }
        .view-card { border-radius: 14px; border: none; box-shadow: 0 4px 20px rgba(26,60,94,0.12); }
        .detail-section { padding: 24px; border-bottom: 1px solid #eef2f7; }
        .detail-section:last-child { border-bottom: none; }
        .section-heading { font-size: 0.75rem; text-transform: uppercase; letter-spacing: 0.7px; font-weight: 700; color: var(--hotel-primary); margin-bottom: 16px; }
        .detail-row { display: flex; justify-content: space-between; align-items: center; margin-bottom: 10px; }
        .detail-label { color: #6c757d; font-size: 0.88rem; }
        .detail-value { font-weight: 600; font-size: 0.92rem; }
        .status-badge { display: inline-flex; align-items: center; gap: 5px; padding: 6px 14px; border-radius: 20px; font-weight: 600; font-size: 0.82rem; }
        .s-PENDING    { background:#fff3cd; color:#856404; }
        .s-CONFIRMED  { background:#d1e7dd; color:#0f5132; }
        .s-CHECKED_IN { background:#cff4fc; color:#055160; }
        .s-CHECKED_OUT{ background:#e2e3e5; color:#383d41; }
        .s-CANCELLED  { background:#f8d7da; color:#842029; }
        .total-display { font-size: 2rem; font-weight: 800; color: #1a6b3c; }
        .timeline { position: relative; padding-left: 28px; }
        .timeline::before { content:''; position: absolute; left: 7px; top: 0; bottom: 0; width: 2px; background: #e0e8f0; }
        .tl-item { position: relative; margin-bottom: 16px; }
        .tl-dot { position: absolute; left: -28px; top: 2px; width: 16px; height: 16px; border-radius: 50%; border: 2px solid; background: white; }
        .tl-dot.done   { border-color: #198754; background: #198754; }
        .tl-dot.active { border-color: var(--hotel-primary); background: var(--hotel-primary); }
        .tl-dot.future { border-color: #ced4da; }
        .tl-label { font-weight: 600; font-size: 0.85rem; }
        .tl-date  { font-size: 0.78rem; color: #6c757d; }
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
                <li class="breadcrumb-item active text-white">Details</li>
            </ol>
        </nav>
        <div class="d-flex align-items-center gap-3">
            <h3 class="fw-bold mb-0"><i class="bi bi-file-earmark-text me-2"></i>Reservation Details</h3>
            <span class="status-badge s-<%= res.getStatus().name() %>">
                <i class="bi bi-circle-fill" style="font-size:0.5rem;"></i>
                <%= res.getStatus() %>
            </span>
        </div>
        <div class="mt-1 opacity-75" style="font-family:monospace; font-size:0.85rem;">
            #<%= res.getReservationId() %>
        </div>
    </div>
</div>

<div class="container py-5">
    <div class="row g-4">
        <!-- Main Details -->
        <div class="col-lg-8">
            <div class="card view-card">

                <!-- Guest & Room -->
                <div class="detail-section">
                    <div class="section-heading"><i class="bi bi-person me-1"></i>Guest & Room</div>
                    <div class="row g-3">
                        <div class="col-md-6">
                            <div class="detail-row">
                                <span class="detail-label">Guest Name</span>
                                <span class="detail-value"><%= guestName %></span>
                            </div>
                            <div class="detail-row">
                                <span class="detail-label">Guest ID</span>
                                <code class="text-secondary"><%= res.getGuestId() %></code>
                            </div>
                        </div>
                        <div class="col-md-6">
                            <div class="detail-row">
                                <span class="detail-label">Room Number</span>
                                <span class="detail-value">Room <%= res.getRoomNumber() %></span>
                            </div>
                            <% if (!res.getCreatedAt().isEmpty()) { %>
                            <div class="detail-row">
                                <span class="detail-label">Booked On</span>
                                <span class="detail-value"><%= res.getCreatedAt() %></span>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Stay Dates -->
                <div class="detail-section">
                    <div class="section-heading"><i class="bi bi-calendar-range me-1"></i>Stay Details</div>
                    <div class="row g-3">
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3">
                                <div style="font-size:0.72rem; text-transform:uppercase; color:#6c757d; letter-spacing:0.5px;">Check-In</div>
                                <div class="fw-bold mt-1"><%= res.getCheckIn() != null ? res.getCheckIn() : "—" %></div>
                            </div>
                        </div>
                        <div class="col-md-4 text-center d-flex align-items-center justify-content-center">
                            <div>
                                <div class="fw-bold" style="font-size:1.4rem; color:var(--hotel-primary);"><%= nights %></div>
                                <div style="font-size:0.78rem; color:#6c757d;">night<%= nights != 1 ? "s" : "" %></div>
                            </div>
                        </div>
                        <div class="col-md-4 text-center">
                            <div class="border rounded p-3">
                                <div style="font-size:0.72rem; text-transform:uppercase; color:#6c757d; letter-spacing:0.5px;">Check-Out</div>
                                <div class="fw-bold mt-1"><%= res.getCheckOut() != null ? res.getCheckOut() : "—" %></div>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Special Requests -->
                <% if (res.getSpecialRequests() != null && !res.getSpecialRequests().isEmpty()) { %>
                <div class="detail-section">
                    <div class="section-heading"><i class="bi bi-chat-square-text me-1"></i>Special Requests</div>
                    <p class="text-muted mb-0"><%= res.getSpecialRequests() %></p>
                </div>
                <% } %>

                <!-- Cancellation Policy -->
                <div class="detail-section">
                    <div class="section-heading"><i class="bi bi-shield-exclamation me-1"></i>Cancellation Policy</div>
                    <div class="detail-row">
                        <span class="detail-label">Cancellation Fee (if cancelled now)</span>
                        <span class="detail-value">
                            $<%= String.format("%.2f", cancellationFee) %>
                            <% if (cancellationFee == 0) { %><span class="badge bg-success ms-1">Free</span>
                            <% } else if (cancellationFee < res.getTotalAmount()) { %><span class="badge bg-warning text-dark ms-1">50%</span>
                            <% } else { %><span class="badge bg-danger ms-1">Full charge</span><% } %>
                        </span>
                    </div>
                </div>

            </div>
        </div>

        <!-- Sidebar -->
        <div class="col-lg-4">
            <!-- Total -->
            <div class="card view-card mb-3">
                <div class="card-body p-4 text-center">
                    <div style="font-size:0.75rem; text-transform:uppercase; letter-spacing:0.5px; color:#6c757d;">Total Amount</div>
                    <div class="total-display my-2">$<%= String.format("%.2f", res.getTotalAmount()) %></div>
                    <div class="text-muted small"><%= nights %> night<%= nights!=1?"s":"" %> &bull; Room <%= res.getRoomNumber() %></div>
                </div>
            </div>

            <!-- Status Timeline -->
            <div class="card view-card mb-3">
                <div class="card-body p-4">
                    <div class="section-heading"><i class="bi bi-arrow-right-circle me-1"></i>Status Progress</div>
                    <div class="timeline">
                        <%
                            Reservation.Status[] statuses = {
                                Reservation.Status.PENDING,
                                Reservation.Status.CONFIRMED,
                                Reservation.Status.CHECKED_IN,
                                Reservation.Status.CHECKED_OUT
                            };
                            int curIdx = 0;
                            for (int i = 0; i < statuses.length; i++) {
                                if (statuses[i] == res.getStatus()) { curIdx = i; break; }
                            }
                            if (res.getStatus() == Reservation.Status.CANCELLED) { curIdx = -1; }
                        %>
                        <% if (res.getStatus() == Reservation.Status.CANCELLED) { %>
                        <div class="tl-item">
                            <div class="tl-dot" style="border-color:#dc3545; background:#dc3545;"></div>
                            <div class="tl-label text-danger">CANCELLED</div>
                        </div>
                        <% } else {
                            for (int i = 0; i < statuses.length; i++) { %>
                        <div class="tl-item">
                            <div class="tl-dot <%= i < curIdx ? "done" : (i == curIdx ? "active" : "future") %>"></div>
                            <div class="tl-label <%= i == curIdx ? "text-primary" : (i < curIdx ? "text-success" : "text-muted") %>">
                                <%= statuses[i].name() %>
                            </div>
                        </div>
                        <% } } %>
                    </div>
                </div>
            </div>

            <!-- Actions -->
            <div class="card view-card">
                <div class="card-body p-3">
                    <div class="d-grid gap-2">

                        <%-- Pay: PENDING only --%>
                        <% if (res.getStatus() == Reservation.Status.PENDING) { %>
                        <a href="${pageContext.request.contextPath}/payments?action=checkout&reservationId=<%= res.getReservationId() %>"
                           class="btn btn-success">
                            <i class="bi bi-credit-card me-1"></i>Make Payment
                        </a>
                        <% } %>

                        <%-- Admin actions --%>
                        <% if (isStaff) { %>

                            <%-- Modify: non-terminal --%>
                            <% if (res.getStatus() != Reservation.Status.CANCELLED
                                && res.getStatus() != Reservation.Status.CHECKED_OUT) { %>
                            <a href="${pageContext.request.contextPath}/reservations?action=modify&reservationId=<%= res.getReservationId() %>"
                               class="btn btn-hotel-primary">
                                <i class="bi bi-pencil me-1"></i>Modify Reservation
                            </a>
                            <% } %>

                            <%-- Check-In: CONFIRMED only --%>
                            <% if (res.getStatus() == Reservation.Status.CONFIRMED) { %>
                            <form action="${pageContext.request.contextPath}/reservations" method="post">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">
                                <input type="hidden" name="newStatus" value="CHECKED_IN">
                                <button type="submit" class="btn btn-info text-white w-100">
                                    <i class="bi bi-door-open-fill me-1"></i>Check-In Guest
                                </button>
                            </form>
                            <% } %>

                            <%-- Check-Out: CHECKED_IN only --%>
                            <% if (res.getStatus() == Reservation.Status.CHECKED_IN) { %>
                            <form action="${pageContext.request.contextPath}/reservations" method="post"
                                  onsubmit="return confirm('Check out this guest?')">
                                <input type="hidden" name="action" value="updateStatus">
                                <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">
                                <input type="hidden" name="newStatus" value="CHECKED_OUT">
                                <button type="submit" class="btn btn-secondary w-100">
                                    <i class="bi bi-box-arrow-right me-1"></i>Check-Out Guest
                                </button>
                            </form>
                            <% } %>

                            <%-- Cancel: any non-terminal state --%>
                            <% if (res.getStatus() != Reservation.Status.CANCELLED
                                && res.getStatus() != Reservation.Status.CHECKED_OUT) {
                                String _cancelFeeStr = String.format("%.2f", res.calculateCancellationFee());
                            %>
                            <form action="${pageContext.request.contextPath}/reservations" method="post"
                                  onsubmit="return confirm('Cancel reservation <%= res.getReservationId() %>?\nCancellation fee: $<%= _cancelFeeStr %>\n\nThis cannot be undone.')">
                                <input type="hidden" name="action" value="cancel">
                                <input type="hidden" name="reservationId" value="<%= res.getReservationId() %>">
                                <button type="submit" class="btn btn-outline-danger w-100">
                                    <i class="bi bi-x-circle me-1"></i>Cancel Reservation
                                </button>
                            </form>
                            <% } %>

                        <% } else { %>
                            <%-- Guest actions: Modify (non-terminal, non-confirmed) --%>
                            <%-- CONFIRMED means payment is done; guests cannot modify paid reservations --%>
                            <% if (res.getStatus() != Reservation.Status.CANCELLED
                                && res.getStatus() != Reservation.Status.CHECKED_OUT
                                && res.getStatus() != Reservation.Status.CONFIRMED) { %>
                            <a href="${pageContext.request.contextPath}/reservations?action=modify&reservationId=<%= res.getReservationId() %>"
                               class="btn btn-hotel-primary">
                                <i class="bi bi-pencil me-1"></i>Modify Reservation
                            </a>
                            <% } else if (res.getStatus() == Reservation.Status.CONFIRMED) { %>
                            <div class="alert alert-info py-2 px-3 mb-0 small">
                                <i class="bi bi-lock-fill me-1"></i>This reservation is confirmed and paid. Contact the front desk for changes.
                            </div>
                            <% } %>
                        <% } %>

                        <a href="${pageContext.request.contextPath}/reservations?action=list"
                           class="btn btn-outline-secondary">
                            <i class="bi bi-arrow-left me-1"></i>Back to List
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
