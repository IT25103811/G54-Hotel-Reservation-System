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

    <!-- Bootstrap & Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Animate.css for entrance animations -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>

    <style>
        /* Animated Gradient Background */
        .profile-wrapper {
            position: relative;
            min-height: 90vh;
            background: linear-gradient(-45deg, #0f2027, #203a43, #2c5364, #1f4037, #99f2c8);
            background-size: 400% 400%;
            animation: gradientBG 15s ease infinite;
            overflow: hidden;
            padding: 40px 0;
            color: #fff;
        }

        @keyframes gradientBG {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        /* Glassmorphism Card */
        .glass-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }

        .glass-card:hover {
            transform: translateY(-5px);
            box-shadow: 0 15px 35px rgba(0,0,0,0.5);
        }

        /* Input Fields */
        .form-control {
            background: rgba(255, 255, 255, 0.15) !important;
            border: 1px solid rgba(255, 255, 255, 0.3) !important;
            border-radius: 10px;
            color: #fff !important;
            padding: 10px 15px;
            transition: all 0.3s ease;
        }

        .form-control::placeholder {
            color: rgba(255, 255, 255, 0.6);
        }

        .form-control:focus {
            background: rgba(255, 255, 255, 0.25) !important;
            box-shadow: 0 0 15px rgba(255, 255, 255, 0.5) !important;
            transform: scale(1.01);
        }

        /* Table Design - සුදු පාට අයින් කරලා Transparent කලා */
        .glass-table, .glass-table tr, .glass-table th, .glass-table td {
            background-color: transparent !important;
            --bs-table-bg: transparent !important;
        }
        .glass-table th { color: #99f2c8; font-weight: 600; border-bottom: 1px solid rgba(255,255,255,0.1); }
        .glass-table td { color: #ffffff; border-bottom: 1px solid rgba(255,255,255,0.1); }

        /* Buttons */
        .btn-custom {
            background: linear-gradient(45deg, #00b4db, #0083b0);
            border: none;
            border-radius: 25px;
            font-weight: bold;
            color: white;
            transition: 0.4s;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }

        .btn-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(0, 180, 219, 0.5);
            color: white;
        }

        .btn-danger-custom {
            background: linear-gradient(45deg, #cb2d3e, #ef473a);
            border: none;
            border-radius: 25px;
            font-weight: bold;
            color: white;
            transition: 0.4s;
        }

        .btn-danger-custom:hover {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(239, 71, 58, 0.5);
            color: white;
        }

        .btn-outline-glass {
            border: 2px solid rgba(255,255,255,0.5);
            color: white;
            border-radius: 25px;
            font-weight: 500;
            background: rgba(255,255,255,0.1);
            transition: 0.3s;
        }

        .btn-outline-glass:hover {
            background: rgba(255,255,255,0.3);
            color: white;
            transform: translateY(-2px);
        }

        /* Floating Bubbles */
        .bubbles {
            position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; pointer-events: none;
        }
        .bubble {
            position: absolute; bottom: -100px; background: rgba(255, 255, 255, 0.1); border-radius: 50%;
            animation: rise 10s infinite ease-in;
        }
        .bubble:nth-child(1) { width: 40px; height: 40px; left: 10%; animation-duration: 8s; }
        .bubble:nth-child(2) { width: 20px; height: 20px; left: 20%; animation-duration: 5s; animation-delay: 1s; }
        .bubble:nth-child(3) { width: 50px; height: 50px; left: 35%; animation-duration: 10s; animation-delay: 2s; }
        .bubble:nth-child(4) { width: 80px; height: 80px; left: 50%; animation-duration: 14s; }

        @keyframes rise {
            0% { bottom: -100px; transform: translateX(0); }
            50% { transform: translateX(30px); }
            100% { bottom: 100vh; transform: translateX(-30px); }
        }

        /* 3D Profile Icon */
        .profile-icon {
            font-size: 6rem;
            background: -webkit-linear-gradient(45deg, #99f2c8, #1f4037);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            filter: drop-shadow(0px 5px 10px rgba(0,0,0,0.5));
            animation: float 4s ease-in-out infinite;
        }

        @keyframes float {
            0% { transform: translateY(0px); }
            50% { transform: translateY(-10px); }
            100% { transform: translateY(0px); }
        }
    </style>
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="profile-wrapper">
    <!-- Floating Bubbles -->
    <div class="bubbles">
        <div class="bubble"></div><div class="bubble"></div>
        <div class="bubble"></div><div class="bubble"></div>
    </div>

    <div class="container position-relative" style="z-index: 10;">
        <div class="row">

            <!-- Left Column: Profile Summary -->
            <div class="col-md-4 mb-4">
                <div class="glass-card text-center p-4 animate__animated animate__fadeInLeft">
                    <i class="bi bi-person-circle profile-icon"></i>
                    <h4 class="mt-3 fw-bold text-uppercase"><%= guest.getName() %></h4>

                    <span class="badge <%= "VIP".equals(guestType) ? "bg-warning text-dark" : "bg-light text-dark" %> px-3 py-2 mt-2" style="border-radius: 15px; font-size: 0.9rem;">
                        <i class="bi bi-award-fill"></i> <%= guestType %> Guest
                    </span>

                    <% if ("VIP".equals(guestType)) { %>
                    <p class="mt-3 mb-0 text-light">Membership Tier</p>
                    <h5 class="fw-bold" style="color: #f1c40f;"><%= tier %></h5>
                    <% } %>

                    <hr style="border-color: rgba(255,255,255,0.2); margin: 20px 0;">

                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <span><i class="bi bi-star-fill text-warning"></i> Loyalty Points</span>
                        <strong class="fs-5"><%= guest.getLoyaltyPoints() %></strong>
                    </div>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="text-light small"><i class="bi bi-tags"></i> Discount Rate</span>
                        <strong class="text-success fs-5"><%= (int)(guest.calculateDiscount()*100) %>%</strong>
                    </div>
                </div>
            </div>

            <!-- Right Column: Profile Form -->
            <div class="col-md-8">
                <div class="glass-card p-4 animate__animated animate__fadeInRight">
                    <h4 class="mb-4 border-bottom border-light pb-2" style="border-color: rgba(255,255,255,0.1)!important;">
                        <i class="bi bi-pencil-square"></i> Profile Details
                    </h4>

                    <% if (request.getAttribute("success") != null) { %>
                    <div class="alert alert-success animate__animated animate__pulse" style="background: rgba(25, 135, 84, 0.8); color: white; border: none;">
                        <i class="bi bi-check-circle-fill"></i> ${success}
                    </div>
                    <% } %>
                    <% if (request.getAttribute("error") != null) { %>
                    <div class="alert alert-danger animate__animated animate__shakeX" style="background: rgba(220, 53, 69, 0.8); color: white; border: none;">
                        <i class="bi bi-exclamation-triangle-fill"></i> ${error}
                    </div>
                    <% } %>

                    <table class="table table-borderless glass-table mb-4">
                        <tr><th width="30%">Guest ID:</th><td><code style="background: rgba(0,0,0,0.5); padding: 5px 10px; border-radius: 5px; color: #99f2c8;"><%= guest.getId() %></code></td></tr>
                        <tr><th>Email:</th><td><%= guest.getEmail() %></td></tr>
                        <tr><th>Phone:</th><td><%= guest.getPhone() != null ? guest.getPhone() : "Not set" %></td></tr>
                    </table>

                    <h5 class="mt-4 mb-3 text-light">Update Profile</h5>

                    <form action="${pageContext.request.contextPath}/guests" method="post">
                        <input type="hidden" name="action" value="profile">

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label text-light"><i class="bi bi-person"></i> Full Name</label>
                                <input type="text" name="name" class="form-control" value="<%= guest.getName() %>">
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label text-light"><i class="bi bi-envelope"></i> Email</label>
                                <input type="email" name="email" class="form-control" value="<%= guest.getEmail() %>">
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label text-light"><i class="bi bi-telephone"></i> Phone Number</label>
                                <input type="tel" name="phone" class="form-control" value="<%= guest.getPhone() != null ? guest.getPhone() : "" %>"
                                       maxlength="12" pattern="^\+[0-9]{11}$"
                                       title="Must start with '+' followed by country code and 11 digits total">
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label text-light"><i class="bi bi-key"></i> New Password</label>
                                <input type="password" name="password" class="form-control" placeholder="Leave blank to keep current">
                            </div>
                        </div>

                        <button type="submit" class="btn btn-custom px-4 mt-2">
                            <i class="bi bi-save"></i> Save Changes
                        </button>
                    </form>
                </div>

                <!-- Action Buttons -->
                <div class="row mt-4 animate__animated animate__fadeInUp" style="animation-delay: 0.5s;">
                    <div class="col-sm-5 mb-2">
                        <form action="${pageContext.request.contextPath}/guests" method="post"
                              onsubmit="return confirm('Are you sure you want to permanently delete your account? This action cannot be undone.');">
                            <input type="hidden" name="action" value="selfDelete">
                            <button type="submit" class="btn btn-danger-custom w-100 py-2">
                                <i class="bi bi-trash-fill"></i> Delete Account
                            </button>
                        </form>
                    </div>

                    <div class="col-sm-7">
                        <div class="d-flex gap-2">
                            <a href="${pageContext.request.contextPath}/reservations?action=list"
                               class="btn btn-outline-glass w-50 py-2">
                                <i class="bi bi-calendar-check"></i> Reservations
                            </a>
                            <a href="${pageContext.request.contextPath}/payments?action=history"
                               class="btn btn-outline-glass w-50 py-2">
                                <i class="bi bi-receipt"></i> Payments
                            </a>
                        </div>
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