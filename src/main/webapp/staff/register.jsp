<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Staff editStaff = (Staff) request.getAttribute("staffMember");
    boolean isEdit = (editStaff != null);
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title><%= isEdit ? "Edit Staff" : "Register Staff" %> - Grand Vista Hotel</title>
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
  <div class="form-section">
    <h3 class="text-center mb-4" style="color: var(--hotel-primary);">
      <i class="bi bi-person-badge"></i>
      <%= isEdit ? "Edit Staff Member" : "Register New Staff" %>
    </h3>

    <% if (request.getAttribute("error") != null) { %>
    <div class="alert alert-danger">${error}</div>
    <% } %>

    <form action="${pageContext.request.contextPath}/staff" method="post">
      <input type="hidden" name="action" value="<%= isEdit ? "edit" : "register" %>">
      <% if (isEdit) { %>
      <input type="hidden" name="staffId" value="<%= editStaff.getStaffId() %>">
      <% } %>

      <div class="mb-3">
        <label class="form-label">Full Name *</label>
        <input type="text" name="name" class="form-control" required
               value="<%= isEdit ? editStaff.getName() : "" %>" placeholder="Staff name">
      </div>

      <% if (!isEdit) { %>
      <div class="mb-3">
        <label class="form-label">Email Address *</label>
        <input type="email" name="email" class="form-control" required placeholder="staff@hotel.com">
      </div>
      <% } %>

      <div class="mb-3">
        <label class="form-label">Password <%= isEdit ? "(leave blank to keep)" : "*" %></label>
        <input type="password" name="password" class="form-control"
               <%= isEdit ? "" : "required" %> placeholder="Password">
      </div>

      <% if (!isEdit) { %>
      <div class="mb-3">
        <label class="form-label">Role *</label>
        <select name="role" class="form-select">
          <option value="MANAGER">Manager</option>
          <option value="RECEPTIONIST">Receptionist</option>
        </select>
      </div>
      <% } %>

      <div class="row g-3">
        <div class="col-md-6">
          <label class="form-label">Salary ($)</label>
          <input type="number" name="salary" class="form-control" step="0.01" min="0"
                 value="<%= isEdit ? editStaff.getSalary() : "" %>" placeholder="3000.00">
        </div>
        <div class="col-md-6">
          <label class="form-label">Shift</label>
          <select name="shift" class="form-select">
            <option value="MORNING" <%= isEdit && "MORNING".equals(editStaff.getShift()) ? "selected" : "" %>>Morning</option>
            <option value="EVENING" <%= isEdit && "EVENING".equals(editStaff.getShift()) ? "selected" : "" %>>Evening</option>
            <option value="NIGHT" <%= isEdit && "NIGHT".equals(editStaff.getShift()) ? "selected" : "" %>>Night</option>
            <option value="ALL" <%= isEdit && "ALL".equals(editStaff.getShift()) ? "selected" : "" %>>All Shifts</option>
          </select>
        </div>
      </div>

      <div class="d-grid mt-4">
        <button type="submit" class="btn btn-hotel-primary btn-lg">
          <i class="bi bi-save"></i> <%= isEdit ? "Update Staff" : "Register Staff" %>
        </button>
      </div>
    </form>
    <div class="text-center mt-3">
      <a href="${pageContext.request.contextPath}/staff?action=manage" class="btn btn-outline-secondary">
        <i class="bi bi-arrow-left"></i> Back to Staff List
      </a>
    </div>
  </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
