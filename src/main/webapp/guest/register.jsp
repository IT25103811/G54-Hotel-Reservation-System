
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Guest Registration - Grand Vista Hotel</title>

    <!-- Bootstrap & Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Animate.css for entrance animations -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>

    <style>
        /* Animated Gradient Background */
        .register-wrapper {
            position: relative;
            min-height: 90vh;
            background: linear-gradient(-45deg, #0f2027, #203a43, #2c5364, #1f4037, #99f2c8);
            background-size: 400% 400%;
            animation: gradientBG 15s ease infinite;
            display: flex;
            align-items: center;
            justify-content: center;
            overflow: hidden;
            padding: 40px 15px;
        }

        @keyframes gradientBG {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        /* Glassmorphism Card (වීදුරු වගේ පෙනුම) */
        .glass-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            padding: 40px;
            width: 100%;
            max-width: 600px;
            z-index: 10;
            color: #ffffff;
            transition: transform 0.3s ease;
        }

        .glass-card:hover {
            transform: translateY(-5px);
        }

        .glass-card h3 {
            font-weight: 700;
            letter-spacing: 1px;
            text-shadow: 2px 2px 4px rgba(0,0,0,0.3);
            margin-bottom: 25px;
        }

        /* Input Fields ඩිසයින් එක */
        .form-control, .form-select {
            background: rgba(255, 255, 255, 0.15) !important;
            border: 1px solid rgba(255, 255, 255, 0.3) !important;
            border-radius: 10px;
            color: #fff !important;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }

        /* Select Dropdown එකේ Option වල පාට කළු කරනවා (පෙනෙන්න ඕන නිසා) */
        .form-select option {
            color: #000;
        }

        .form-control::placeholder {
            color: rgba(255, 255, 255, 0.7);
        }

        .form-control:focus, .form-select:focus {
            background: rgba(255, 255, 255, 0.25) !important;
            box-shadow: 0 0 15px rgba(255, 255, 255, 0.5) !important;
            transform: scale(1.02);
        }

        /* Animated Submit Button */
        .btn-custom {
            background: linear-gradient(45deg, #00b4db, #0083b0);
            border: none;
            border-radius: 25px;
            font-weight: bold;
            letter-spacing: 1px;
            color: white;
            padding: 12px;
            transition: 0.4s;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            overflow: hidden;
            position: relative;
        }

        .btn-custom:hover {
            transform: translateY(-3px) scale(1.03);
            box-shadow: 0 8px 25px rgba(0, 180, 219, 0.5);
            color: white;
        }

        /* Links */
        .glass-card a {
            color: #99f2c8;
            text-decoration: none;
            transition: 0.3s;
            font-weight: 500;
        }

        .glass-card a:hover {
            color: #ffffff;
            text-shadow: 0 0 8px rgba(255,255,255,0.6);
        }

        /* Floating Bubbles Animation */
        .bubbles {
            position: absolute;
            top: 0; left: 0; width: 100%; height: 100%;
            z-index: 1; pointer-events: none;
        }
        .bubble {
            position: absolute;
            bottom: -100px;
            background: rgba(255, 255, 255, 0.1);
            border-radius: 50%;
            animation: rise 10s infinite ease-in;
        }
        .bubble:nth-child(1) { width: 40px; height: 40px; left: 10%; animation-duration: 8s; }
        .bubble:nth-child(2) { width: 20px; height: 20px; left: 20%; animation-duration: 5s; animation-delay: 1s; }
        .bubble:nth-child(3) { width: 50px; height: 50px; left: 35%; animation-duration: 10s; animation-delay: 2s; }
        .bubble:nth-child(4) { width: 80px; height: 80px; left: 50%; animation-duration: 14s; animation-delay: 0s; }
        .bubble:nth-child(5) { width: 35px; height: 35px; left: 65%; animation-duration: 9s; animation-delay: 3s; }
        .bubble:nth-child(6) { width: 60px; height: 60px; left: 80%; animation-duration: 12s; animation-delay: 1s; }

        @keyframes rise {
            0% { bottom: -100px; transform: translateX(0); }
            50% { transform: translateX(50px); }
            100% { bottom: 100vh; transform: translateX(-50px); }
        }
    </style>
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<!-- අලුත් Animated Section එක -->
<div class="register-wrapper">
    <!-- Floating Bubbles (පාවෙන බුබුළු) -->
    <div class="bubbles">
        <div class="bubble"></div><div class="bubble"></div><div class="bubble"></div>
        <div class="bubble"></div><div class="bubble"></div><div class="bubble"></div>
    </div>

    <!-- Glassmorphism Card (වීදුරු කොටුව) -->
    <div class="glass-card animate__animated animate__zoomIn">
        <h3 class="text-center">
            <i class="bi bi-person-plus fs-2 d-block mb-2 animate__animated animate__pulse animate__infinite"></i>
            Create an Account
        </h3>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger animate__animated animate__shakeX" style="background: rgba(220, 53, 69, 0.8); color: white; border: none;">
            <i class="bi bi-exclamation-triangle-fill"></i> ${error}
        </div>
        <% } %>
        <% if (request.getParameter("registered") != null) { %>
        <div class="alert alert-success animate__animated animate__fadeInDown" style="background: rgba(25, 135, 84, 0.8); color: white; border: none;">
            <i class="bi bi-check-circle-fill"></i> Registration successful! Please login.
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/guests" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="action" value="register">

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label text-light"><i class="bi bi-person"></i> Full Name *</label>
                    <input type="text" name="name" class="form-control" required placeholder="Enter full name">
                    <div class="invalid-feedback text-warning">Name is required.</div>
                </div>

                <div class="col-md-6 mb-3">
                    <label class="form-label text-light"><i class="bi bi-envelope"></i> Email Address *</label>
                    <input type="email" name="email" class="form-control" required placeholder="your@email.com"
                           pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                           title="Please enter a valid email address">
                    <div class="invalid-feedback text-warning">Valid email is required.</div>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label text-light"><i class="bi bi-telephone"></i> Phone Number *</label>
                <input type="tel" name="phone" class="form-control" placeholder="+94771234567"
                       required maxlength="12" pattern="^\+[0-9]{11}$"
                       title="Must start with '+' followed by country code and 11 digits total">
                <div class="invalid-feedback text-warning">Valid phone number with country code is required (+94xxxxxxxxx).</div>
            </div>

            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label text-light"><i class="bi bi-lock"></i> Password *</label>
                    <input type="password" name="password" id="password" class="form-control" required minlength="6" placeholder="Min 6 characters">
                    <div class="invalid-feedback text-warning">Password must be at least 6 characters.</div>
                </div>

                <div class="col-md-6 mb-3">
                    <label class="form-label text-light"><i class="bi bi-shield-lock"></i> Confirm Password *</label>
                    <input type="password" id="confirmPassword" class="form-control" required placeholder="Confirm password">
                    <div class="invalid-feedback text-warning">Passwords must match.</div>
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label text-light"><i class="bi bi-star"></i> Guest Type *</label>
                <select name="guestType" id="guestType" class="form-select" onchange="toggleVIPField()">
                    <option value="REGULAR">Regular Guest</option>
                    <option value="VIP">VIP Member</option>
                </select>
            </div>

            <div class="mb-3 animate__animated animate__fadeIn" id="vipTierSection" style="display:none;">
                <label class="form-label text-light"><i class="bi bi-award"></i> Membership Tier</label>
                <select name="membershipTier" class="form-select">
                    <option value="Silver">Silver</option>
                    <option value="Gold">Gold</option>
                    <option value="Platinum">Platinum</option>
                </select>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-custom btn-lg">
                    Register Now <i class="bi bi-person-check ms-2"></i>
                </button>
            </div>
        </form>

        <hr style="border-color: rgba(255,255,255,0.2); margin: 25px 0;">

        <p class="text-center mb-0">Already have an account?
            <a href="${pageContext.request.contextPath}/guests?action=login">Login here</a>
        </p>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // VIP කොටස පෙන්නන එක
    function toggleVIPField() {
        const type = document.getElementById('guestType').value;
        const vipSection = document.getElementById('vipTierSection');
        if (type === 'VIP') {
            vipSection.style.display = 'block';
            vipSection.classList.add('animate__fadeIn');
        } else {
            vipSection.style.display = 'none';
        }
    }

    // Bootstrap form validation
    (function() {
        'use strict';
        var forms = document.querySelectorAll('.needs-validation');
        forms.forEach(function(form) {
            form.addEventListener('submit', function(event) {
                const pw = document.getElementById('password').value;
                const cpw = document.getElementById('confirmPassword').value;
                if (pw !== cpw) {
                    document.getElementById('confirmPassword').setCustomValidity('Passwords must match.');
                } else {
                    document.getElementById('confirmPassword').setCustomValidity('');
                }
                if (!form.checkValidity()) {
                    event.preventDefault();
                    event.stopPropagation();
                }
                form.classList.add('was-validated');
            }, false);
        });
    })();
</script>
</body>
</html>