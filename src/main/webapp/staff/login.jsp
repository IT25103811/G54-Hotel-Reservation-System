<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Staff Login - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body style="background: linear-gradient(135deg, #1a3c5e 0%, #0d1f33 100%); min-height:100vh;">

<div class="container py-5">
  <div class="row justify-content-center">
    <div class="col-md-5">
      <div class="text-center mb-4">
        <i class="bi bi-building" style="font-size:3rem; color: #c9a84c;"></i>
        <h3 class="text-white mt-2">Grand Vista Hotel</h3>
        <p class="text-muted">Staff Portal</p>
      </div>

      <div class="card">
        <div class="card-header card-header-hotel text-center">
          <h5 class="mb-0"><i class="bi bi-shield-lock"></i> Staff Login</h5>
        </div>
        <div class="card-body p-4">
          <% if (request.getAttribute("error") != null) { %>
          <div class="alert alert-danger"><i class="bi bi-exclamation-circle"></i> ${error}</div>
          <% } %>

          <form action="${pageContext.request.contextPath}/staff" method="post">
            <input type="hidden" name="action" value="login">
            <div class="mb-3">
              <label class="form-label">Email Address</label>
              <input type="email" name="email" class="form-control" required placeholder="staff@hotel.com">
            </div>
            <div class="mb-3">
              <label class="form-label">Password</label>
              <input type="password" name="password" class="form-control" required>
            </div>
            <div class="d-grid">
              <button type="submit" class="btn btn-hotel-primary btn-lg">
                <i class="bi bi-box-arrow-in-right"></i> Login
              </button>
            </div>
          </form>
        </div>
      </div>

      <div class="text-center mt-3">
        <a href="${pageContext.request.contextPath}/index.jsp" class="text-white-50 small">
          <i class="bi bi-house"></i> Back to Hotel Website
        </a>
      </div>

      <div class="alert alert-info mt-3 small">
        <strong>Default Admin:</strong><br>
        Email: admin@hotel.com | Password: admin123
      </div>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
