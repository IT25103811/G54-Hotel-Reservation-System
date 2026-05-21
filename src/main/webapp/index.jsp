<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Grand Vista Hotel</title>

    <!-- Bootstrap & Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

    <!-- Main Style -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- Animate.css for entrance animations -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>

    <style>
        /* Hero Section with Animated Gradient & Rain/Snow Effect */
        .hero-section {
            position: relative;
            background: linear-gradient(-45deg, #1a3c5e, #2d6a9f, #0f2027, #203a43);
            background-size: 400% 400%;
            animation: gradientBG 15s ease infinite;
            color: white;
            padding: 120px 0 140px 0;
            text-align: center;
            overflow: hidden;
            border-bottom: 5px solid var(--hotel-secondary);
        }

        @keyframes gradientBG {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        /* ------------------------------------
           Falling Rain / Snow (පොද වැටෙන Animation)
           ------------------------------------ */
        .rain-container {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            pointer-events: none;
            z-index: 1;
        }

        .drop {
            position: absolute;
            background: rgba(255, 255, 255, 0.4);
            border-radius: 50%;
            /* පොඩි තිත් වගේ පේන්න */
            width: 3px;
            height: 3px;
            box-shadow: 0 0 5px rgba(255,255,255,0.8);
            animation: fall linear infinite;
        }

        @keyframes fall {
            0% {
                transform: translateY(-20px) translateX(0);
                opacity: 0;
            }
            10% { opacity: 1; }
            90% { opacity: 1; }
            100% {
                transform: translateY(100vh) translateX(20px); /* වැටෙද්දී පොඩ්ඩක් ඇලවෙනවා */
                opacity: 0;
            }
        }

        /* Generating Multiple Drops using CSS nth-child */
        .drop:nth-child(1) { left: 10%; animation-duration: 4s; animation-delay: 0s; width: 4px; height: 4px; }
        .drop:nth-child(2) { left: 20%; animation-duration: 3s; animation-delay: 1s; }
        .drop:nth-child(3) { left: 30%; animation-duration: 5s; animation-delay: 2s; width: 2px; height: 2px; }
        .drop:nth-child(4) { left: 40%; animation-duration: 4.5s; animation-delay: 0.5s; }
        .drop:nth-child(5) { left: 50%; animation-duration: 3.5s; animation-delay: 1.5s; width: 5px; height: 5px; }
        .drop:nth-child(6) { left: 60%; animation-duration: 6s; animation-delay: 0.2s; }
        .drop:nth-child(7) { left: 70%; animation-duration: 4s; animation-delay: 2s; width: 3px; height: 3px; }
        .drop:nth-child(8) { left: 80%; animation-duration: 3.8s; animation-delay: 1.2s; }
        .drop:nth-child(9) { left: 90%; animation-duration: 5s; animation-delay: 0.8s; }
        .drop:nth-child(10) { left: 15%; animation-duration: 4.2s; animation-delay: 1.8s; width: 4px; height: 4px;}
        .drop:nth-child(11) { left: 35%; animation-duration: 3.2s; animation-delay: 0.3s; }
        .drop:nth-child(12) { left: 55%; animation-duration: 5.5s; animation-delay: 2.5s; }
        .drop:nth-child(13) { left: 75%; animation-duration: 4.8s; animation-delay: 0.6s; width: 2px; height: 2px;}
        .drop:nth-child(14) { left: 85%; animation-duration: 3.7s; animation-delay: 1.1s; }
        .drop:nth-child(15) { left: 95%; animation-duration: 4.1s; animation-delay: 1.9s; }
        .drop:nth-child(16) { left: 5%; animation-duration: 5.2s; animation-delay: 0.7s; }
        .drop:nth-child(17) { left: 25%; animation-duration: 3.9s; animation-delay: 1.4s; width: 5px; height: 5px;}
        .drop:nth-child(18) { left: 45%; animation-duration: 4.6s; animation-delay: 0.9s; }
        .drop:nth-child(19) { left: 65%; animation-duration: 3.4s; animation-delay: 2.1s; }
        .drop:nth-child(20) { left: 98%; animation-duration: 5.8s; animation-delay: 0.4s; width: 3px; height: 3px;}

        /* Hero Content Styling */
        .hero-content {
            position: relative;
            z-index: 10;
        }

        .hero-section h1 {
            font-size: 3.8rem;
            font-weight: 800;
            text-shadow: 2px 4px 8px rgba(0,0,0,0.5);
            margin-bottom: 20px;
            letter-spacing: 1px;
        }

        .hero-section p.lead {
            font-size: 1.4rem;
            text-shadow: 1px 2px 4px rgba(0,0,0,0.4);
            margin-bottom: 40px;
            font-weight: 300;
        }

        .hotel-icon-main {
            font-size: 5rem;
            background: -webkit-linear-gradient(45deg, #c9a84c, #f4c430);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            filter: drop-shadow(0px 5px 15px rgba(0,0,0,0.4));
            animation: pulse 2s infinite;
        }

        /* Hover Effects for Cards below */
        .service-card {
            transition: all 0.3s ease;
            border: none;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
            border-bottom: 4px solid transparent;
        }
        .service-card:hover {
            transform: translateY(-10px);
            box-shadow: 0 15px 30px rgba(0,0,0,0.15);
            border-bottom: 4px solid var(--hotel-secondary);
        }

    </style>
</head>
<body>

<%@ include file="/includes/header.jsp" %>

<!-- Hero Section -->
<section class="hero-section">
    <!-- Rain / Falling Snow Animation Container -->
    <div class="rain-container">
        <div class="drop"></div><div class="drop"></div><div class="drop"></div><div class="drop"></div>
        <div class="drop"></div><div class="drop"></div><div class="drop"></div><div class="drop"></div>
        <div class="drop"></div><div class="drop"></div><div class="drop"></div><div class="drop"></div>
        <div class="drop"></div><div class="drop"></div><div class="drop"></div><div class="drop"></div>
        <div class="drop"></div><div class="drop"></div><div class="drop"></div><div class="drop"></div>
    </div>

    <div class="container hero-content">
        <div class="row justify-content-center">
            <div class="col-lg-9">
                <div class="mb-3 animate__animated animate__fadeInDown">
                    <i class="bi bi-building hotel-icon-main"></i>
                </div>
                <h1 class="animate__animated animate__zoomIn" style="animation-delay: 0.3s;">
                    Welcome to Grand Vista Hotel
                </h1>
                <p class="lead animate__animated animate__fadeInUp" style="animation-delay: 0.6s;">
                    Experience luxury, comfort, and world-class hospitality.
                </p>
                <div class="mt-4 animate__animated animate__fadeInUp" style="animation-delay: 0.9s;">
                    <a href="${pageContext.request.contextPath}/reservations?action=book"
                       class="btn btn-hotel-gold btn-lg me-3 px-4 py-3 shadow-lg rounded-pill" style="transition: 0.3s;">
                        <i class="bi bi-calendar-plus-fill me-2"></i> Book a Room
                    </a>
                    <a href="${pageContext.request.contextPath}/rooms?action=list"
                       class="btn btn-outline-light btn-lg px-4 py-3 rounded-pill" style="transition: 0.3s;">
                        <i class="bi bi-door-open-fill me-2"></i> View Rooms
                    </a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Quick Links -->
<section class="py-5" style="background-color: #f8f9fa;">
    <div class="container mt-n5 position-relative" style="z-index: 20; transform: translateY(-60px);">
        <div class="row g-4">
            <div class="col-md-3">
                <div class="card service-card h-100 text-center p-4 rounded-4">
                    <div class="card-body">
                        <div class="mb-3 p-3 bg-primary bg-opacity-10 rounded-circle d-inline-block">
                            <i class="bi bi-door-open" style="font-size:2.5rem; color: var(--hotel-primary);"></i>
                        </div>
                        <h5 class="fw-bold">Browse Rooms</h5>
                        <p class="text-muted small">Explore our Standard and Suite rooms</p>
                        <a href="${pageContext.request.contextPath}/rooms?action=list"
                           class="btn btn-outline-primary btn-sm rounded-pill px-3">View Rooms</a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card service-card h-100 text-center p-4 rounded-4">
                    <div class="card-body">
                        <div class="mb-3 p-3 bg-success bg-opacity-10 rounded-circle d-inline-block">
                            <i class="bi bi-calendar-check" style="font-size:2.5rem; color: #28a745;"></i>
                        </div>
                        <h5 class="fw-bold">Make a Reservation</h5>
                        <p class="text-muted small">Book your stay in minutes</p>
                        <a href="${pageContext.request.contextPath}/reservations?action=book"
                           class="btn btn-outline-success btn-sm rounded-pill px-3">Book Now</a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card service-card h-100 text-center p-4 rounded-4">
                    <div class="card-body">
                        <div class="mb-3 p-3 bg-info bg-opacity-10 rounded-circle d-inline-block">
                            <i class="bi bi-person-plus" style="font-size:2.5rem; color: #17a2b8;"></i>
                        </div>
                        <h5 class="fw-bold">Guest Login</h5>
                        <p class="text-muted small">Manage your reservations & profile</p>
                        <a href="${pageContext.request.contextPath}/guests?action=login"
                           class="btn btn-outline-info btn-sm rounded-pill px-3">Login</a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card service-card h-100 text-center p-4 rounded-4">
                    <div class="card-body">
                        <div class="mb-3 p-3 bg-warning bg-opacity-10 rounded-circle d-inline-block">
                            <i class="bi bi-shield-lock" style="font-size:2.5rem; color: var(--hotel-secondary);"></i>
                        </div>
                        <h5 class="fw-bold">Staff Portal</h5>
                        <p class="text-muted small">Hotel management dashboard</p>
                        <a href="${pageContext.request.contextPath}/staff?action=login"
                           class="btn btn-outline-warning btn-sm rounded-pill px-3">Staff Login</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Reviews Preview -->
<section class="py-5 bg-white mb-5">
    <div class="container">
        <div class="text-center mb-5">
            <h2 class="fw-bold" style="color: var(--hotel-primary);">What Our Guests Say</h2>
            <div style="width: 60px; height: 3px; background: var(--hotel-secondary); margin: 0 auto;"></div>
        </div>

        <div class="row justify-content-center g-4">
            <div class="col-md-4">
                <div class="card p-4 service-card rounded-4 border-0 bg-light">
                    <div class="stars mb-3 text-warning fs-4">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
                    <p class="fst-italic text-secondary">"An absolutely wonderful experience. The rooms were immaculate and the staff were incredibly helpful!"</p>
                    <div class="mt-3 d-flex align-items-center">
                        <div class="bg-primary text-white rounded-circle d-flex justify-content-center align-items-center me-3" style="width: 40px; height: 40px; fw-bold">J</div>
                        <small class="fw-bold">— John D.<br><span class="text-success fw-normal"><i class="bi bi-check-circle-fill"></i> Verified Guest</span></small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card p-4 service-card rounded-4 border-0 bg-light">
                    <div class="stars mb-3 text-warning fs-4">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
                    <p class="fst-italic text-secondary">"The suite was breathtaking. Jacuzzi, amazing views, and the breakfast was superb."</p>
                    <div class="mt-3 d-flex align-items-center">
                        <div class="bg-success text-white rounded-circle d-flex justify-content-center align-items-center me-3" style="width: 40px; height: 40px; fw-bold">S</div>
                        <small class="fw-bold">— Sarah M.<br><span class="text-success fw-normal"><i class="bi bi-check-circle-fill"></i> Verified Guest</span></small>
                    </div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card p-4 service-card rounded-4 border-0 bg-light">
                    <div class="stars mb-3 text-warning fs-4">&#9733;&#9733;&#9733;&#9733;&#9734;</div>
                    <p class="fst-italic text-secondary">"Great value for money. Clean, comfortable and perfectly located. Highly recommended!"</p>
                    <div class="mt-3 d-flex align-items-center">
                        <div class="bg-secondary text-white rounded-circle d-flex justify-content-center align-items-center me-3" style="width: 40px; height: 40px; fw-bold">T</div>
                        <small class="fw-bold">— TravelLover42<br><span class="text-muted fw-normal">Guest</span></small>
                    </div>
                </div>
            </div>
        </div>
        <div class="text-center mt-5">
            <a href="${pageContext.request.contextPath}/reviews?action=view" class="btn btn-outline-primary rounded-pill px-4 py-2">
                <i class="bi bi-star-fill me-2"></i> Read All Reviews
            </a>
        </div>
    </div>
</section>

<%@ include file="/includes/footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>