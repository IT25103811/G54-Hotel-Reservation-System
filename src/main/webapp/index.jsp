<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="/includes/header.jsp" %>

<!-- Hero Section -->
<section class="hero-section">
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                <div class="mb-3">
                    <i class="bi bi-building" style="font-size:4rem; color: #c9a84c;"></i>
                </div>
                <h1>Welcome to Grand Vista Hotel</h1>
                <p class="lead">Experience luxury, comfort, and world-class hospitality.</p>
                <div class="mt-4">
                    <a href="${pageContext.request.contextPath}/reservations?action=book"
                       class="btn btn-hotel-gold btn-lg me-3">
                        <i class="bi bi-calendar-plus"></i> Book a Room
                    </a>
                    <a href="${pageContext.request.contextPath}/rooms?action=list"
                       class="btn btn-outline-light btn-lg">
                        <i class="bi bi-door-open"></i> View Rooms
                    </a>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Quick Links -->
<section class="py-5">
    <div class="container">
        <h2 class="text-center mb-4" style="color: var(--hotel-primary);">Our Services</h2>
        <div class="row g-4">
            <div class="col-md-3">
                <div class="card h-100 text-center p-3">
                    <div class="card-body">
                        <i class="bi bi-door-open" style="font-size:2.5rem; color: var(--hotel-primary);"></i>
                        <h5 class="mt-3">Browse Rooms</h5>
                        <p class="text-muted small">Explore our Standard and Suite rooms</p>
                        <a href="${pageContext.request.contextPath}/rooms?action=list"
                           class="btn btn-hotel-primary btn-sm">View Rooms</a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card h-100 text-center p-3">
                    <div class="card-body">
                        <i class="bi bi-calendar-check" style="font-size:2.5rem; color: #28a745;"></i>
                        <h5 class="mt-3">Make a Reservation</h5>
                        <p class="text-muted small">Book your stay in minutes</p>
                        <a href="${pageContext.request.contextPath}/reservations?action=book"
                           class="btn btn-success btn-sm">Book Now</a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card h-100 text-center p-3">
                    <div class="card-body">
                        <i class="bi bi-person-plus" style="font-size:2.5rem; color: #17a2b8;"></i>
                        <h5 class="mt-3">Guest Login</h5>
                        <p class="text-muted small">Manage your reservations & profile</p>
                        <a href="${pageContext.request.contextPath}/guests?action=login"
                           class="btn btn-info btn-sm text-white">Login</a>
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                <div class="card h-100 text-center p-3">
                    <div class="card-body">
                        <i class="bi bi-shield-lock" style="font-size:2.5rem; color: var(--hotel-secondary);"></i>
                        <h5 class="mt-3">Staff Portal</h5>
                        <p class="text-muted small">Hotel management dashboard</p>
                        <a href="${pageContext.request.contextPath}/staff?action=login"
                           class="btn btn-warning btn-sm">Staff Login</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</section>

<!-- Reviews Preview -->
<section class="py-5 bg-white">
    <div class="container">
        <h2 class="text-center mb-4" style="color: var(--hotel-primary);">What Our Guests Say</h2>
        <div class="row justify-content-center">
            <div class="col-md-4">
                <div class="card p-3">
                    <div class="stars">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
                    <p class="mt-2 text-muted">"An absolutely wonderful experience. The rooms were immaculate and the staff were incredibly helpful!"</p>
                    <small class="text-muted">— John D., Verified Guest</small>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card p-3">
                    <div class="stars">&#9733;&#9733;&#9733;&#9733;&#9733;</div>
                    <p class="mt-2 text-muted">"The suite was breathtaking. Jacuzzi, amazing views, and the breakfast was superb."</p>
                    <small class="text-muted">— Sarah M., Verified Guest</small>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card p-3">
                    <div class="stars">&#9733;&#9733;&#9733;&#9733;&#9734;</div>
                    <p class="mt-2 text-muted">"Great value for money. Clean, comfortable and perfectly located."</p>
                    <small class="text-muted">— TravelLover42, Guest</small>
                </div>
            </div>
        </div>
        <div class="text-center mt-4">
            <a href="${pageContext.request.contextPath}/reviews?action=view" class="btn btn-hotel-primary">
                <i class="bi bi-star"></i> Read All Reviews
            </a>
        </div>
    </div>
</section>

<%@ include file="/includes/footer.jsp" %>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
