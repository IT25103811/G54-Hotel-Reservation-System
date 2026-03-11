<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Guest List - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="container py-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 style="color: var(--hotel-primary);"><i class="bi bi-people"></i> Guest Management</h3>
    </div>

    <!-- Search/Filter -->
    <div class="card mb-4">
        <div class="card-body">
            <input type="text" id="searchInput" class="form-control" placeholder="Search by name or email...">
        </div>
    </div>

    <div class="card">
        <div class="card-body p-0">
            <table class="table table-hover table-hotel mb-0" id="guestTable">
                <thead>
                <tr>
                    <th>ID</th><th>Name</th><th>Email</th><th>Phone</th>
                    <th>Type</th><th>Loyalty Points</th><th>Actions</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="g" items="${guests}">
                    <tr>
                        <td><code>${g.id}</code></td>
                        <td>${g.name}</td>
                        <td>${g.email}</td>
                        <td>${g.phone}</td>
                        <td>
                            <c:choose>
                                <c:when test="${g.guestType == 'VIPGuest'}">
                                    <span class="badge bg-warning text-dark">VIP</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-secondary">Regular</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td><i class="bi bi-star-fill text-warning"></i> ${g.loyaltyPoints}</td>
                        <td>
                            <form action="${pageContext.request.contextPath}/guests" method="post" class="d-inline"
                                  onsubmit="return confirm('Delete this guest?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${g.id}">
                                <button type="submit" class="btn btn-danger btn-sm">
                                    <i class="bi bi-trash"></i>
                                </button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty guests}">
                    <tr><td colspan="7" class="text-center text-muted py-4">No guests registered yet.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    document.getElementById('searchInput').addEventListener('keyup', function() {
        const filter = this.value.toLowerCase();
        document.querySelectorAll('#guestTable tbody tr').forEach(function(row) {
            const text = row.textContent.toLowerCase();
            row.style.display = text.includes(filter) ? '' : 'none';
        });
    });
</script>
</body>
</html>
