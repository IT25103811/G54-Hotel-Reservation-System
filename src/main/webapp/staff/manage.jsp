<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Manage Staff - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="d-flex justify-content-between align-items-center mb-4">
    <h3 style="color: var(--hotel-primary);"><i class="bi bi-people-fill"></i> Staff Management</h3>
    <a href="${pageContext.request.contextPath}/staff?action=register" class="btn btn-hotel-primary">
      <i class="bi bi-person-plus"></i> Add Staff
    </a>
  </div>

  <div class="card">
    <div class="card-body p-0">
      <table class="table table-hover table-hotel mb-0">
        <thead>
          <tr>
            <th>Staff ID</th><th>Name</th><th>Email</th><th>Role</th>
            <th>Shift</th><th>Salary</th><th>Permissions</th><th>Actions</th>
          </tr>
        </thead>
        <tbody>
          <c:forEach var="s" items="${staffList}">
          <tr>
            <td><code>${s.staffId}</code></td>
            <td>${s.name}</td>
            <td>${s.email}</td>
            <td>
              <span class="badge ${s.role == 'MANAGER' ? 'bg-danger' : 'bg-primary'}">
                ${s.role}
              </span>
            </td>
            <td>${s.shift}</td>
            <td>$${s.salary}</td>
            <td><small class="text-muted">${s.permissions}</small></td>
            <td>
              <div class="d-flex gap-1">
                <a href="${pageContext.request.contextPath}/staff?action=edit&staffId=${s.staffId}"
                   class="btn btn-sm btn-outline-primary">
                  <i class="bi bi-pencil"></i>
                </a>
                <form action="${pageContext.request.contextPath}/staff" method="post" class="d-inline"
                      onsubmit="return confirm('Delete this staff member?');">
                  <input type="hidden" name="action" value="delete">
                  <input type="hidden" name="staffId" value="${s.staffId}">
                  <button type="submit" class="btn btn-sm btn-outline-danger">
                    <i class="bi bi-trash"></i>
                  </button>
                </form>
              </div>
            </td>
          </tr>
          </c:forEach>
          <c:if test="${empty staffList}">
            <tr><td colspan="8" class="text-center text-muted py-4">No staff members found.</td></tr>
          </c:if>
        </tbody>
      </table>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
