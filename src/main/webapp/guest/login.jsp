
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Guest Login - Grand Vista Hotel</title>

    <!-- Bootstrap & Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <!-- Animate.css for entrance animations -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>

    <style>
        /* Animated Gradient Background */
        .login-wrapper {
            position: relative;
            min-height: 85vh;
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
            max-width: 450px;
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
            margin-bottom: 30px;
        }

        /* Input Fields ඩිසයින් එක */
        .form-control {
            background: rgba(255, 255, 255, 0.15) !important;
            border: 1px solid rgba(255, 255, 255, 0.3) !important;
            border-radius: 10px;
            color: #fff !important;
            padding: 12px 15px;
            transition: all 0.3s ease;
        }

        .form-control::placeholder {
            color: rgba(255, 255, 255, 0.7);
        }

        .form-control:focus {
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

<!-- අලුත් Animated Section එක මෙතනින් පටන් ගන්නවා -->
<div class="login-wrapper">
    <!-- Floating Bubbles (පාවෙන බුබුළු) -->
    <div class="bubbles">
        <div class="bubble"></div><div class="bubble"></div><div class="bubble"></div>
        <div class="bubble"></div><div class="bubble"></div><div class="bubble"></div>
    </div>

    <!-- Glassmorphism Card (වීදුරු කොටුව) -->
    <div class="glass-card animate__animated animate__zoomIn">
        <h3 class="text-center">
            <i class="bi bi-person-circle fs-2 d-block mb-2 animate__animated animate__pulse animate__infinite"></i>
            Welcome Back!
        </h3>

        <% if (request.getParameter("registered") != null) { %>
        <div class="alert alert-success animate__animated animate__fadeInDown" style="background: rgba(25, 135, 84, 0.8); color: white; border: none;">
            <i class="bi bi-check-circle-fill"></i> Registration successful! Please login.
        </div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger animate__animated animate__shakeX" style="background: rgba(220, 53, 69, 0.8); color: white; border: none;">
            <i class="bi bi-exclamation-triangle-fill"></i> ${error}
        </div>
        <% } %>

        <form action="${pageContext.request.contextPath}/guests" method="post">
            <input type="hidden" name="action" value="login">

            <div class="mb-4">
                <label class="form-label text-light"><i class="bi bi-envelope"></i> Email Address</label>
                <input type="email" name="email" class="form-control" required placeholder="Enter your email">
            </div>

            <div class="mb-4">
                <label class="form-label text-light"><i class="bi bi-lock"></i> Password</label>
                <input type="password" name="password" class="form-control" required placeholder="Enter your password">
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-custom btn-lg">
                    Login <i class="bi bi-arrow-right-circle ms-2"></i>
                </button>
            </div>
        </form>

        <hr style="border-color: rgba(255,255,255,0.2); margin: 25px 0;">

        <p class="text-center mb-2">New guest?
            <a href="${pageContext.request.contextPath}/guests?action=register">Create an account</a>
        </p>
        <p class="text-center small mb-0">
            Hotel staff? <a href="${pageContext.request.contextPath}/staff?action=login">Staff Portal</a>
        </p>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>