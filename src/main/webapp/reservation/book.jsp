<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.hotel.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Book a Room - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .booking-hero {
            background: linear-gradient(135deg, var(--hotel-primary) 0%, #2d6a9f 100%);
            color: white;
            padding: 40px 0 60px;
            margin-bottom: -30px;
        }
        .booking-card {
            border-radius: 16px;
            box-shadow: 0 8px 32px rgba(26,60,94,0.18);
            border: none;
        }
        .step-badge {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            width: 32px;
            height: 32px;
            border-radius: 50%;
            background: var(--hotel-primary);
            color: white;
            font-weight: 700;
            font-size: 0.85rem;
            margin-right: 10px;
            flex-shrink: 0;
        }
        .step-title {
            display: flex;
            align-items: center;
            font-weight: 600;
            font-size: 0.95rem;
            color: var(--hotel-primary);
            margin-bottom: 16px;
            padding-bottom: 10px;
            border-bottom: 2px solid #e8edf2;
        }
        .room-card-option {
            border: 2px solid #dee2e6;
            border-radius: 10px;
            padding: 14px;
            cursor: pointer;
            transition: all 0.2s;
            background: white;
        }
        .room-card-option:hover,
        .room-card-option.selected {
            border-color: var(--hotel-primary);
            background: #eef3f8;
        }
        .price-summary {
            background: linear-gradient(135deg, #eef3f8, #dce8f5);
            border: 1px solid #b8d0e8;
            border-radius: 12px;
            padding: 20px;
        }
        .price-row {
            display: flex;
            justify-content: space-between;
            margin-bottom: 8px;
            font-size: 0.95rem;
        }
        .price-row.total {
            border-top: 2px solid var(--hotel-primary);
            padding-top: 12px;
            font-weight: 700;
            font-size: 1.1rem;
            color: var(--hotel-primary);
        }
        .discount-badge {
            background: #d4edda;
            color: #155724;
            padding: 2px 8px;
            border-radius: 20px;
            font-size: 0.8rem;
            font-weight: 600;
        }
        .form-control:focus, .form-select:focus {
            border-color: var(--hotel-primary);
            box-shadow: 0 0 0 0.2rem rgba(26,60,94,0.15);
        }
        .btn-book {
            background: linear-gradient(135deg, var(--hotel-primary), #2d6a9f);
            border: none;
            color: white;
            padding: 14px 32px;
            font-size: 1.05rem;
            font-weight: 600;
            border-radius: 10px;
            transition: all 0.2s;
        }
        .btn-book:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(26,60,94,0.35);
            color: white;
        }
        .btn-book:disabled { opacity: 0.6; cursor: not-allowed; }
        .nights-pill {
            background: var(--hotel-secondary);
            color: var(--hotel-dark);
            font-weight: 700;
            padding: 3px 12px;
            border-radius: 20px;
            font-size: 0.85rem;
        }
        .validation-feedback { font-size: 0.82rem; }
        .char-counter { font-size: 0.78rem; color: #6c757d; float: right; }
    </style>
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<%
    Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
    String guestIdVal = (loggedGuest != null) ? loggedGuest.getId() : "";
    if (request.getAttribute("prevGuestId") != null && ((String)request.getAttribute("prevGuestId")).length() > 0)
        guestIdVal = (String) request.getAttribute("prevGuestId");
    double discountRate = (loggedGuest != null) ? loggedGuest.calculateDiscount() : 0.0;
    int discountPct = (int)(discountRate * 100);
    String guestType = (loggedGuest instanceof VIPGuest) ? "VIP" : "Regular";
%>

<!-- Hero Banner -->
<div class="booking-hero">
    <div class="container">
        <nav aria-label="breadcrumb">
            <ol class="breadcrumb mb-2" style="--bs-breadcrumb-divider-color: rgba(255,255,255,0.5);">
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/index.jsp" class="text-white-50">Home</a></li>
                <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/reservations?action=list" class="text-white-50">Reservations</a></li>
                <li class="breadcrumb-item active text-white">Book a Room</li>
            </ol>
        </nav>
        <h2 class="fw-bold mb-1"><i class="bi bi-calendar-plus me-2"></i>Make a Reservation</h2>
        <p class="opacity-75 mb-0">Complete the form below to reserve your ideal room</p>
    </div>
</div>

<div class="container py-5">

    <!-- Alert messages -->
    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger alert-dismissible fade show mb-4" role="alert">
        <i class="bi bi-exclamation-triangle-fill me-2"></i>
        <strong>Booking Error:</strong> ${error}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <div class="row g-4">
        <!-- Main Form -->
        <div class="col-lg-7">
            <div class="card booking-card">
                <div class="card-body p-4">

                    <form action="${pageContext.request.contextPath}/reservations"
                          method="post" id="bookForm" novalidate>
                        <input type="hidden" name="action" value="book">

                        <!-- Step 1: Guest -->
                        <div class="step-title">
                            <span class="step-badge">1</span> Guest Information
                        </div>
                        <div class="mb-4">
                            <label class="form-label fw-semibold">Guest ID <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-person-badge"></i></span>
                                <input type="text" name="guestId" id="guestId"
                                       class="form-control"
                                       value="<%= guestIdVal %>"
                                       <%= loggedGuest != null ? "readonly" : "" %>
                                       placeholder="e.g. G123456789"
                                       pattern="[A-Za-z0-9]+" required>
                                <div class="invalid-feedback validation-feedback">Please enter a valid Guest ID.</div>
                            </div>
                            <% if (loggedGuest != null) { %>
                            <div class="mt-2">
                                <i class="bi bi-check-circle-fill text-success me-1"></i>
                                <strong><%= loggedGuest.getName() %></strong>
                                <% if (discountPct > 0) { %>
                                &nbsp;<span class="discount-badge"><%= guestType %> – <%= discountPct %>% discount applied</span>
                                <% } %>
                            </div>
                            <% } else { %>
                            <div class="form-text">
                                <a href="${pageContext.request.contextPath}/guests?action=login">
                                    <i class="bi bi-box-arrow-in-right"></i> Log in
                                </a> to auto-fill and unlock member discounts.
                            </div>
                            <% } %>
                        </div>

                        <!-- Step 2: Room -->
                         <div class="step-title">
                             <span class="step-badge">2</span> Select Room
                         </div>
                        <div class="mb-4">
                            <label class="form-label fw-semibold">Available Rooms <span class="text-danger">*</span></label>
                            <select name="roomNumber" id="roomSelect" class="form-select" required
                                     onchange="onRoomChange()">
                                <option value="">-- Choose a room --</option>
                                <c:forEach var="room" items="${availableRooms}">
                                    <option value="${room.roomNumber}"
                                             data-price="${room.calculatePrice()}"
                                             data-type="${room.type}"
                                             data-floor="${room.floor}"
                                             data-amenities="${room.amenities}"
                                             ${(not empty param.prevRoom and param.prevRoom == room.roomNumber) ? 'selected' : ''}>
                                         Room ${room.roomNumber} &mdash; ${room.type}, Floor ${room.floor} &mdash; $<fmt:formatNumber value="${room.calculatePrice()}" pattern="#,##0.00" xmlns:fmt="http://java.sun.com/jsp/jstl/fmt"/> / night
                                     </option>
                                </c:forEach>
                            </select>
                            <div id="roomDetails" class="mt-2 text-muted small" style="display:none;">
                                 <i class="bi bi-info-circle"></i> <span id="roomAmenities"></span>
                            </div>
                            <div class="invalid-feedback validation-feedback">Please select a room.</div>
                        </div>

                        <!-- Step 3: Dates -->
                         <div class="step-title">
                             <span class="step-badge">3</span> Stay Dates
                         </div>
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Check-In <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-calendar-event"></i></span>
                                    <input type="date" name="checkIn" id="checkIn"
                                           class="form-control"
                                           min="<%= java.time.LocalDate.now() %>"
                                           value="${prevCheckIn}"
                                           required onchange="calcTotal()">
                                    <div class="invalid-feedback validation-feedback">Select a valid check-in date.</div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <label class="form-label fw-semibold">Check-Out <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="bi bi-calendar-check"></i></span>
                                    <input type="date" name="checkOut" id="checkOut"
                                           class="form-control"
                                           min="<%= java.time.LocalDate.now().plusDays(1) %>"
                                           value="${prevCheckOut}"
                                           required onchange="calcTotal()">
                                    <div class="invalid-feedback validation-feedback">Select a valid check-out date.</div>
                                </div>
                            </div>
                        </div>

                        <!-- Step 4: Special Requests -->
                         <div class="step-title">
                             <span class="step-badge">4</span> Special Requests
                             <span class="badge bg-secondary fw-normal ms-2" style="font-size:0.7rem;">Optional</span>
                         </div>
                        <div class="mb-4">
                            <textarea name="specialRequests" id="specialRequests"
                                      class="form-control" rows="3" maxlength="300"
                                      placeholder="e.g. Late check-in, extra pillows, high floor preference…"
                                      onkeyup="updateCharCount()">${prevSpecial}</textarea>
                            <div><span class="char-counter"><span id="charCount">0</span>/300</span></div>
                        </div>

                        <div class="d-grid">
                            <button type="submit" class="btn btn-book" id="submitBtn" disabled>
                                <i class="bi bi-check-circle me-2"></i>Confirm Reservation
                            </button>
                        </div>
                    </form>

                </div>
            </div>
        </div>

        <!-- Sidebar: Price Summary -->
        <div class="col-lg-5">
            <div class="sticky-top" style="top: 80px;">
                <!-- Price Summary -->
                <div class="card booking-card mb-3" id="priceSummaryCard" style="display:none !important;">
                    <div class="card-header card-header-hotel">
                        <h6 class="mb-0"><i class="bi bi-receipt me-2"></i>Booking Summary</h6>
                    </div>
                    <div class="card-body p-3">
                        <div class="price-summary">
                            <div class="price-row">
                                <span class="text-muted">Room</span>
                                <span id="summaryRoom" class="fw-semibold">—</span>
                            </div>
                            <div class="price-row">
                                <span class="text-muted">Duration</span>
                                <span id="summaryNights">—</span>
                            </div>
                            <div class="price-row">
                                <span class="text-muted">Rate / night</span>
                                <span id="summaryRate">—</span>
                            </div>
                            <div class="price-row">
                                <span class="text-muted">Subtotal</span>
                                <span id="summarySubtotal">—</span>
                            </div>
                            <% if (discountPct > 0) { %>
                            <div class="price-row text-success">
                                <span><i class="bi bi-tag-fill me-1"></i><%= guestType %> discount</span>
                                <span>-<%= discountPct %>%</span>
                            </div>
                            <% } %>
                            <div class="price-row total">
                                <span>Total</span>
                                <span id="summaryTotal">—</span>
                            </div>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    var discount = <%= discountRate %>;
    var MIN_DATE = '<%= java.time.LocalDate.now() %>';

    function onRoomChange() {
        var sel = document.getElementById('roomSelect');
        var opt = sel.selectedOptions[0];
        var roomDetails = document.getElementById('roomDetails');
        if (sel.value && opt.dataset.amenities) {
            document.getElementById('roomAmenities').textContent = opt.dataset.amenities;
            roomDetails.style.display = 'block';
        } else {
            roomDetails.style.display = 'none';
        }
        calcTotal();
    }

    function calcTotal() {
        var roomSel  = document.getElementById('roomSelect');
        var checkIn  = document.getElementById('checkIn').value;
        var checkOut = document.getElementById('checkOut').value;
        var card     = document.getElementById('priceSummaryCard');
        var btn      = document.getElementById('submitBtn');

        // Enforce checkOut > checkIn
        if (checkIn) {
            var nextDay = new Date(checkIn);
            nextDay.setDate(nextDay.getDate() + 1);
            document.getElementById('checkOut').min = nextDay.toISOString().split('T')[0];
        }

        if (roomSel.value && checkIn && checkOut) {
            var price  = parseFloat(roomSel.selectedOptions[0].dataset.price);
            var msDay  = 86400000;
            var nights = Math.round((new Date(checkOut) - new Date(checkIn)) / msDay);

            if (nights > 0 && nights <= 90) {
                var subtotal = price * nights;
                var total    = subtotal * (1 - discount);

                document.getElementById('summaryRoom').textContent     = 'Room ' + roomSel.value;
                document.getElementById('summaryNights').innerHTML     = nights + ' night' + (nights > 1 ? 's' : '') +
                    ' <span class="nights-pill">' + nights + 'N</span>';
                document.getElementById('summaryRate').textContent     = '$' + price.toFixed(2);
                document.getElementById('summarySubtotal').textContent = '$' + subtotal.toFixed(2);
                document.getElementById('summaryTotal').textContent    = '$' + total.toFixed(2);

                card.style.removeProperty('display');
                btn.disabled = false;
                return;
            }
        }
        card.style.display = 'none !important';
        btn.disabled = true;
    }

    function updateCharCount() {
        var ta = document.getElementById('specialRequests');
        document.getElementById('charCount').textContent = ta.value.length;
    }

    // Bootstrap validation
    document.getElementById('bookForm').addEventListener('submit', function(e) {
        if (!this.checkValidity()) {
            e.preventDefault();
            e.stopPropagation();
        }
        this.classList.add('was-validated');
    });

    // Init on page load (handles back-navigation with values)
    window.addEventListener('DOMContentLoaded', function() {
        onRoomChange();
        calcTotal();
        updateCharCount();
    });
</script>
</body>
</html>
