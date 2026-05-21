<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
  FIX #1: Added the missing fmt taglib declaration below.
  Without this, Tomcat's Jasper JSP compiler throws:
    JasperException: The prefix "fmt" for element "fmt:formatNumber" is not bound.
  This caused the ENTIRE list page to fail with a 500 error, making the
  Edit button completely unreachable.
--%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="com.hotel.model.*" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reservations - Grand Vista Hotel</title>
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
        .stat-card-mini {
            background: rgba(255,255,255,0.15);
            border-radius: 10px;
            padding: 14px 20px;
            text-align: center;
            border: 1px solid rgba(255,255,255,0.2);
        }
        .stat-card-mini .val { font-size: 1.6rem; font-weight: 700; }
        .stat-card-mini .lbl { font-size: 0.75rem; opacity: 0.85; text-transform: uppercase; letter-spacing: 0.5px; }
        .filter-bar {
            background: white;
            border-radius: 12px;
            padding: 16px 20px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            margin-bottom: 20px;
        }
        .table-res thead th {
            background: var(--hotel-primary);
            color: white;
            font-size: 0.82rem;
            font-weight: 600;
            text-transform: uppercase;
            letter-spacing: 0.4px;
            border: none;
            padding: 12px 14px;
        }
        .table-res tbody tr {
            transition: background 0.15s;
        }
        .table-res tbody tr:hover { background: #f0f5fb; }
        .table-res td { vertical-align: middle; padding: 12px 14px; font-size: 0.9rem; }
        .res-id { font-family: monospace; font-size: 0.78rem; color: var(--hotel-primary); font-weight: 600; }
        .status-badge {
            display: inline-flex; align-items: center; gap: 4px;
            padding: 4px 10px; border-radius: 20px; font-size: 0.75rem; font-weight: 600;
        }
        .s-PENDING    { background:#fff3cd; color:#856404; }
        .s-CONFIRMED  { background:#d1e7dd; color:#0f5132; }
        .s-CHECKED_IN { background:#cff4fc; color:#055160; }
        .s-CHECKED_OUT{ background:#e2e3e5; color:#383d41; }
        .s-CANCELLED  { background:#f8d7da; color:#842029; }
        .amount-cell { font-weight: 700; color: #1a6b3c; }
        .action-btn { padding: 5px 10px; font-size: 0.8rem; border-radius: 6px; }
        .empty-state { padding: 60px 20px; text-align: center; }
        .empty-state i { font-size: 3rem; color: #ced4da; }
    </style>
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<%
    boolean isStaff = session.getAttribute("loggedInStaff") != null;
%>

<!-- Page Header -->
<div class="page-header">
    <div class="container">
        <div class="row align-items-center">
            <div class="col-md-6">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-1" style="--bs-breadcrumb-divider-color:rgba(255,255,255,0.4);">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/index.jsp" class="text-white-50">Home</a></li>
                        <li class="breadcrumb-item active text-white">Reservations</li>
                    </ol>
                </nav>
                <h3 class="fw-bold mb-0">
                    <i class="bi bi-calendar-check me-2"></i>
                    <%= isStaff ? "All Reservations" : "My Reservations" %>
                </h3>
            </div>
            <% if (isStaff) { %>
            <div class="col-md-6 mt-3 mt-md-0">
                <div class="row g-2">
                    <div class="col-4">
                        <div class="stat-card-mini">
                            <div class="val">${statPending}</div>
                            <div class="lbl">Pending</div>
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="stat-card-mini">
                            <div class="val">${statConfirmed}</div>
                            <div class="lbl">Confirmed</div>
                        </div>
                    </div>
                    <div class="col-4">
                        <div class="stat-card-mini">
                            <div class="val">${statCheckedIn}</div>
                            <div class="lbl">Checked In</div>
                        </div>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
    </div>
</div>

<div class="container py-5">

    <!-- Success toast -->
    <% String success = (String) session.getAttribute("successMessage");
       if (success != null) {
           session.removeAttribute("successMessage"); %>
    <div class="alert alert-success alert-dismissible fade show mb-4" role="alert">
        <i class="bi bi-check-circle-fill me-2"></i><%= success %>
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    </div>
    <% } %>

    <!-- Filter Bar -->
    <div class="filter-bar d-flex flex-wrap gap-3 align-items-center justify-content-between">
        <div class="d-flex gap-2 align-items-center flex-wrap">
            <div class="input-group" style="width:240px;">
                <span class="input-group-text"><i class="bi bi-search"></i></span>
                <input type="text" id="searchInput" class="form-control form-control-sm"
                       placeholder="Search ID, room, guest…" oninput="filterTable()">
            </div>
            <select id="statusFilter" class="form-select form-select-sm" style="width:160px;" onchange="filterTable()">
                <option value="">All Statuses</option>
                <option value="PENDING">Pending</option>
                <option value="CONFIRMED">Confirmed</option>
                <option value="CHECKED_IN">Checked In</option>
                <option value="CHECKED_OUT">Checked Out</option>
                <option value="CANCELLED">Cancelled</option>
            </select>
        </div>
        <a href="${pageContext.request.contextPath}/reservations?action=book"
           class="btn btn-hotel-primary">
            <i class="bi bi-plus-circle me-1"></i> New Reservation
        </a>
    </div>

    <!-- Table -->
    <div class="card" style="border-radius:12px; overflow:hidden;">
        <div class="card-body p-0">
            <div class="table-responsive">
                <table class="table table-res mb-0" id="resTable">
                    <thead>
                    <tr>
                        <th>Reservation ID</th>
                        <%-- FIX #2: Use Java scriptlet conditional (not EL) for the header
                             so it stays consistent with the tbody fix below. --%>
                        <% if (isStaff) { %><th>Guest ID</th><% } %>
                        <th>Room</th>
                        <th>Check-In <span class="badge bg-light text-primary border border-primary ms-1" style="font-size:0.65rem;font-weight:600;" title="QuickSort — check-in date ascending"><i class="bi bi-sort-up"></i> sorted</span></th>
                        <th>Check-Out</th>
                        <th>Nights</th>
                        <th>Status</th>
                        <th class="text-end">Total</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody id="resTableBody">
                    <c:forEach var="res" items="${reservations}">
                        <tr data-status="${res.status}">
                            <td><span class="res-id">${res.reservationId}</span></td>
                            <%--
                              FIX #2: Changed <c:if test="${isStaff}"> to a JSP scriptlet.
                              EL cannot access Java scriptlet local variables — ${isStaff}
                              always evaluated to null/false, so the Guest ID body cells
                              were NEVER rendered for staff. This caused a column count
                              mismatch that broke the table layout for all staff users.
                            --%>
                            <% if (isStaff) { %><td><code class="text-secondary">${res.guestId}</code></td><% } %>
                            <td><strong>Rm ${res.roomNumber}</strong></td>
                            <td>${res.checkIn}</td>
                            <td>${res.checkOut}</td>
                            <td><span class="badge bg-secondary">${res.nights}N</span></td>
                            <td>
                                <span class="status-badge s-${res.status}">
                                    <c:choose>
                                        <c:when test="${res.status == 'PENDING'}"><i class="bi bi-clock-fill"></i></c:when>
                                        <c:when test="${res.status == 'CONFIRMED'}"><i class="bi bi-check-circle-fill"></i></c:when>
                                        <c:when test="${res.status == 'CHECKED_IN'}"><i class="bi bi-door-open-fill"></i></c:when>
                                        <c:when test="${res.status == 'CHECKED_OUT'}"><i class="bi bi-box-arrow-right"></i></c:when>
                                        <c:otherwise><i class="bi bi-x-circle-fill"></i></c:otherwise>
                                    </c:choose>
                                    ${res.status}
                                </span>
                            </td>
                            <%-- FIX #1 (continuation): fmt:formatNumber now works because
                                 the fmt taglib is declared at the top of this file. --%>
                            <td class="text-end amount-cell">$<fmt:formatNumber value="${res.totalAmount}" pattern="#,##0.00"/></td>
                            <td>
                                <div class="d-flex gap-1">
                                    <a href="${pageContext.request.contextPath}/reservations?action=view&reservationId=${res.reservationId}"
                                       class="btn btn-sm btn-outline-secondary action-btn" title="View Details">
                                        <i class="bi bi-eye"></i>
                                    </a>
                                    <c:if test="${res.status != 'CANCELLED' and res.status != 'CHECKED_OUT'}">
                                        <a href="${pageContext.request.contextPath}/reservations?action=modify&reservationId=${res.reservationId}"
                                           class="btn btn-sm btn-outline-primary action-btn" title="Modify">
                                            <i class="bi bi-pencil"></i>
                                        </a>
                                        <c:if test="${res.status != 'CHECKED_IN'}">
                                            <a href="${pageContext.request.contextPath}/payments?action=checkout&reservationId=${res.reservationId}"
                                               class="btn btn-sm btn-success action-btn" title="Pay">
                                                <i class="bi bi-credit-card"></i>
                                            </a>
                                        </c:if>
                                        <form action="${pageContext.request.contextPath}/reservations"
                                              method="post" class="d-inline"
                                              onsubmit="return confirmCancel('${res.reservationId}')">
                                            <input type="hidden" name="action" value="cancel">
                                            <input type="hidden" name="reservationId" value="${res.reservationId}">
                                            <button type="submit" class="btn btn-sm btn-outline-danger action-btn" title="Cancel">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </form>
                                    </c:if>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${empty reservations}">
                <div class="empty-state">
                    <i class="bi bi-calendar-x d-block mb-3"></i>
                    <h5 class="text-muted">No reservations found</h5>
                    <p class="text-muted mb-4">You haven't made any reservations yet.</p>
                    <a href="${pageContext.request.contextPath}/reservations?action=book"
                       class="btn btn-hotel-primary">
                        <i class="bi bi-calendar-plus me-1"></i> Book Your First Room
                    </a>
                </div>
            </c:if>
        </div>

        <!-- Table Footer -->
        <div class="card-footer d-flex justify-content-between align-items-center py-2 px-3 bg-light">
            <small class="text-muted" id="tableCount"></small>
            <small class="text-muted">Showing all results</small>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function filterTable() {
        var query  = document.getElementById('searchInput').value.toLowerCase();
        var status = document.getElementById('statusFilter').value;
        var rows   = document.querySelectorAll('#resTableBody tr');
        var visible = 0;

        rows.forEach(function(row) {
            var text   = row.textContent.toLowerCase();
            var rowSt  = row.dataset.status;
            var match  = (!query || text.includes(query)) && (!status || rowSt === status);
            row.style.display = match ? '' : 'none';
            if (match) visible++;
        });

        document.getElementById('tableCount').textContent = visible + ' reservation' + (visible !== 1 ? 's' : '');
    }

    function confirmCancel(id) {
        return confirm('Cancel reservation ' + id + '?\nThis action cannot be undone.');
    }

    window.addEventListener('DOMContentLoaded', filterTable);
</script>
</body>
</html>
