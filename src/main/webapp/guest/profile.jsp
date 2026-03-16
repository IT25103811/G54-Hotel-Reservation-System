<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Guest guest = (Guest) session.getAttribute("loggedInGuest");
    if (guest == null) { response.sendRedirect(request.getContextPath() + "/guests?action=login"); return; }
    String guestType = (guest instanceof VIPGuest) ? "VIP" : "Regular";
    String tier = (guest instanceof VIPGuest) ? ((VIPGuest)guest).getMembershipTier() : "N/A";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>My Profile - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="row">
        <div class="col-md-4 mb-4">
            <div class="card text-center p-4">
                <i class="bi bi-person-circle" style="font-size:5rem; color: var(--hotel-primary);"></i>
                <h4 class="mt-3"><%= guest.getName() %></h4>
                <span class="badge <%= "VIP".equals(guestType) ? "bg-warning text-dark" : "bg-secondary" %>">
          <%= guestType %> Guest
        </span>
                <% if ("VIP".equals(guestType)) { %>
                <p class="mt-2 text-muted">Tier: <strong><%= tier %></strong></p>
                <% } %>
                <hr>
                <p class="mb-1"><i class="bi bi-star-fill text-warning"></i>
                    <strong><%= guest.getLoyaltyPoints() %></strong> Loyalty Points</p>
                <p class="mb-1 text-muted small">Discount Rate:
                    <strong><%= (int)(guest.calculateDiscount()*100) %>%</strong></p>
            </div>
        </div>
        <div class="col-md-8">
            <div class="card">
                <div class="card-header card-header-hotel">
                    <h5 class="mb-0"><i class="bi bi-pencil-square"></i> Profile Details</h5>
                </div>
                <div class="card-body">
                    <% if (request.getAttribute("success") != null) { %>
                    <div class="alert alert-success"><i class="bi bi-check-circle"></i> ${success}</div>
                    <% } %>

                    <table class="table table-borderless mb-4">
                        <tr><th width="35%">Guest ID:</th><td><code><%= guest.getId() %></code></td></tr>
                        <tr><th>Email:</th><td><%= guest.getEmail() %></td></tr>
                        <tr><th>Phone:</th><td><%= guest.getPhone() != null ? guest.getPhone() : "Not set" %></td></tr>
                    </table>

                    <h6>Update Profile</h6>

                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger"><i class="bi bi-exclamation-triangle"></i> ${error}</div>
                    <% } %>

                    <form action="${pageContext.request.contextPath}/guests" method="post">
                        <input type="hidden" name="action" value="profile">

                        <div class="mb-3">
                            <label class="form-label">Full Name</label>
                            <input type="text" name="name" class="form-control" value="<%= guest.getName() %>">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Email</label>
                            <input type="email" name="email" class="form-control" value="<%= guest.getEmail() %>">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Phone Number</label>
                            <input type="tel" name="phone" class="form-control" value="<%= guest.getPhone() != null ? guest.getPhone() : "" %>">
                        </div>

                        <div class="mb-3">
                            <label class="form-label">New Password <span class="text-muted small">(leave blank to keep current)</span></label>
                            <input type="password" name="password" class="form-control" placeholder="New password">
                        </div>

                        <button type="submit" class="btn btn-hotel-primary">
                            <i class="bi bi-save"></i> Save Changes
                        </button>
                    </form>
                </div>
            </div>
            <hr class="my-4">

            <form action="${pageContext.request.contextPath}/guests" method="post"
                  onsubmit="return confirm('Are you sure you want to permanently delete your account?');">
                <input type="hidden" name="action" value="selfDelete">
                <button type="submit" class="btn btn-danger">
                    <i class="bi bi-trash"></i> Delete My Account
                </button>
            </form>

            <div class="row mt-4">
                <div class="col-sm-6">
                    <a href="${pageContext.request.contextPath}/reservations?action=list"
                       class="btn btn-outline-primary w-100">
                        <i class="bi bi-calendar-check"></i> My Reservations
                    </a>
                </div>
                <div class="col-sm-6">
                    <a href="${pageContext.request.contextPath}/payments?action=history"
                       class="btn btn-outline-secondary w-100">
                        <i class="bi bi-receipt"></i> Payment History
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
