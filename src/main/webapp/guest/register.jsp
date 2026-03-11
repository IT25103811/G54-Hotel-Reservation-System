<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Guest Registration - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="form-section">
        <h3 class="text-center mb-4" style="color: var(--hotel-primary);">
            <i class="bi bi-person-plus"></i> Guest Registration
        </h3>

        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><i class="bi bi-exclamation-circle"></i> ${error}</div>
        <% } %>
        <% if (request.getParameter("registered") != null) { %>
        <div class="alert alert-success"><i class="bi bi-check-circle"></i> Registration successful! Please login.</div>
        <% } %>

        <form action="${pageContext.request.contextPath}/guests" method="post" class="needs-validation" novalidate>
            <input type="hidden" name="action" value="register">

            <div class="mb-3">
                <label class="form-label">Full Name *</label>
                <input type="text" name="name" class="form-control" required placeholder="Enter your full name">
                <div class="invalid-feedback">Name is required.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Email Address *</label>
                <input type="email" name="email" class="form-control" required placeholder="your@email.com">
                <div class="invalid-feedback">Valid email is required.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Phone Number</label>
                <input type="tel" name="phone" class="form-control" placeholder="+1 (555) 000-0000">
            </div>

            <div class="mb-3">
                <label class="form-label">Password *</label>
                <input type="password" name="password" id="password" class="form-control" required minlength="6">
                <div class="invalid-feedback">Password must be at least 6 characters.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Confirm Password *</label>
                <input type="password" id="confirmPassword" class="form-control" required>
                <div class="invalid-feedback">Passwords must match.</div>
            </div>

            <div class="mb-3">
                <label class="form-label">Guest Type *</label>
                <select name="guestType" id="guestType" class="form-select" onchange="toggleVIPField()">
                    <option value="REGULAR">Regular Guest</option>
                    <option value="VIP">VIP Member</option>
                </select>
            </div>

            <div class="mb-3" id="vipTierSection" style="display:none;">
                <label class="form-label">Membership Tier</label>
                <select name="membershipTier" class="form-select">
                    <option value="Silver">Silver</option>
                    <option value="Gold">Gold</option>
                    <option value="Platinum">Platinum</option>
                </select>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-hotel-primary btn-lg">
                    <i class="bi bi-person-check"></i> Register
                </button>
            </div>
        </form>
        <hr>
        <p class="text-center text-muted">Already have an account?
            <a href="${pageContext.request.contextPath}/guests?action=login">Login here</a>
        </p>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function toggleVIPField() {
        const type = document.getElementById('guestType').value;
        document.getElementById('vipTierSection').style.display = (type === 'VIP') ? 'block' : 'none';
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
