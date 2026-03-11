<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Guest Login - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="form-section">
        <h3 class="text-center mb-4" style="color: var(--hotel-primary);">
            <i class="bi bi-box-arrow-in-right"></i> Guest Login
        </h3>

        <% if (request.getParameter("registered") != null) { %>
        <div class="alert alert-success"><i class="bi bi-check-circle"></i> Registration successful! Please login.</div>
        <% } %>
        <% if (request.getAttribute("error") != null) { %>
        <div class="alert alert-danger"><i class="bi bi-exclamation-circle"></i> ${error}</div>
        <% } %>

        <form action="${pageContext.request.contextPath}/guests" method="post">
            <input type="hidden" name="action" value="login">

            <div class="mb-3">
                <label class="form-label">Email Address</label>
                <input type="email" name="email" class="form-control" required placeholder="your@email.com">
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <input type="password" name="password" class="form-control" required>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-hotel-primary btn-lg">
                    <i class="bi bi-box-arrow-in-right"></i> Login
                </button>
            </div>
        </form>
        <hr>
        <p class="text-center text-muted">New guest?
            <a href="${pageContext.request.contextPath}/guests?action=register">Register here</a>
        </p>
        <p class="text-center text-muted small">
            Hotel staff? <a href="${pageContext.request.contextPath}/staff?action=login">Staff login</a>
        </p>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
