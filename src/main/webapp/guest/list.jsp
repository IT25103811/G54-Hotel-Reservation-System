<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*, java.util.*" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Guest List - Grand Vista Hotel</title>

    <!-- Bootstrap & Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">

    <!-- Main Style (For Header/Footer) -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <!-- Animate.css for animations -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/animate.css/4.1.1/animate.min.css"/>

    <style>
        /* Animated Gradient Background */
        .list-wrapper {
            position: relative;
            min-height: 90vh;
            background: linear-gradient(-45deg, #0f2027, #203a43, #2c5364, #1f4037, #99f2c8);
            background-size: 400% 400%;
            animation: gradientBG 15s ease infinite;
            padding: 40px 0;
            color: #fff;
            overflow: hidden;
        }

        @keyframes gradientBG {
            0% { background-position: 0% 50%; }
            50% { background-position: 100% 50%; }
            100% { background-position: 0% 50%; }
        }

        /* Glassmorphism Card */
        .glass-card {
            background: rgba(255, 255, 255, 0.1);
            border-radius: 20px;
            box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
            backdrop-filter: blur(12px);
            -webkit-backdrop-filter: blur(12px);
            border: 1px solid rgba(255, 255, 255, 0.2);
            padding: 25px;
            margin-bottom: 25px;
            z-index: 10;
            position: relative;
        }

        /* Search Input */
        .search-glass {
            background: rgba(255, 255, 255, 0.15) !important;
            border: 1px solid rgba(255, 255, 255, 0.3) !important;
            border-radius: 30px;
            color: #fff !important;
            padding: 12px 25px;
            font-size: 1.1rem;
            transition: all 0.3s ease;
        }

        .search-glass::placeholder { color: rgba(255, 255, 255, 0.7); }
        .search-glass:focus {
            background: rgba(255, 255, 255, 0.25) !important;
            box-shadow: 0 0 15px rgba(255, 255, 255, 0.4) !important;
            transform: scale(1.01);
        }

        /* Glass Table Design */
        .glass-table-container {
            border-radius: 15px;
            overflow: hidden;
            background: rgba(0, 0, 0, 0.2);
        }

        .table-hotel { margin-bottom: 0; }
        .table-hotel, .table-hotel tr, .table-hotel th, .table-hotel td {
            background-color: transparent !important;
            --bs-table-bg: transparent !important;
            --bs-table-hover-bg: rgba(255, 255, 255, 0.1) !important;
        }

        .table-hotel thead th {
            background: rgba(255, 255, 255, 0.1) !important;
            color: #99f2c8 !important;
            border-bottom: 2px solid rgba(255,255,255,0.2);
            padding: 15px;
            font-weight: 600;
            letter-spacing: 1px;
            text-transform: uppercase;
            font-size: 0.85rem;
        }

        .table-hotel tbody td {
            color: #ffffff !important;
            border-bottom: 1px solid rgba(255,255,255,0.1);
            padding: 15px;
            vertical-align: middle;
        }

        /* Buttons & Badges inside Table */
        .btn-glass-danger {
            background: rgba(220, 53, 69, 0.2);
            color: #ff6b6b;
            border: 1px solid rgba(220, 53, 69, 0.5);
            border-radius: 8px;
            transition: all 0.3s ease;
        }

        .btn-glass-danger:hover {
            background: rgba(220, 53, 69, 0.8);
            color: white;
            box-shadow: 0 0 10px rgba(220, 53, 69, 0.5);
            transform: scale(1.1);
        }

        .badge-vip { background: linear-gradient(45deg, #f1c40f, #e67e22); color: #000; }
        .badge-regular { background: rgba(255,255,255,0.2); border: 1px solid rgba(255,255,255,0.3); }

        /* Floating Bubbles Background */
        .bubbles { position: absolute; top: 0; left: 0; width: 100%; height: 100%; z-index: 1; pointer-events: none; }
        .bubble {
            position: absolute; bottom: -100px; background: rgba(255, 255, 255, 0.1); border-radius: 50%;
            animation: rise 10s infinite ease-in;
        }
        .bubble:nth-child(1) { width: 40px; height: 40px; left: 10%; animation-duration: 8s; }
        .bubble:nth-child(2) { width: 20px; height: 20px; left: 20%; animation-duration: 5s; animation-delay: 1s; }
        .bubble:nth-child(3) { width: 50px; height: 50px; left: 35%; animation-duration: 10s; animation-delay: 2s; }
        .bubble:nth-child(4) { width: 80px; height: 80px; left: 60%; animation-duration: 14s; }
        .bubble:nth-child(5) { width: 35px; height: 35px; left: 80%; animation-duration: 9s; }

        @keyframes rise {
            0% { bottom: -100px; transform: translateX(0); }
            50% { transform: translateX(30px); }
            100% { bottom: 100vh; transform: translateX(-30px); }
        }
    </style>
</head>
<body>
<%@ include file="/includes/header.jsp" %>

<div class="list-wrapper">
    <!-- Floating Bubbles -->
    <div class="bubbles">
        <div class="bubble"></div><div class="bubble"></div><div class="bubble"></div>
        <div class="bubble"></div><div class="bubble"></div>
    </div>

    <div class="container position-relative" style="z-index: 10;">

        <div class="d-flex justify-content-between align-items-center mb-4 animate__animated animate__fadeInDown">
            <h2 style="color: #fff; text-shadow: 2px 2px 5px rgba(0,0,0,0.5);">
                <i class="bi bi-people-fill text-info me-2"></i> Guest Management
            </h2>
        </div>

        <!-- Search/Filter -->
        <div class="glass-card animate__animated animate__fadeInUp" style="animation-delay: 0.1s; padding: 15px 25px;">
            <div class="input-group">
                <span class="input-group-text bg-transparent border-0 text-white fs-4"><i class="bi bi-search"></i></span>
                <input type="text" id="searchInput" class="form-control search-glass" placeholder="Search guests by name, email or phone...">
            </div>
            <form class="mt-3" method="get" action="${pageContext.request.contextPath}/guests">
                <input type="hidden" name="action" value="list">
                <div class="row g-2">
                    <div class="col-md-6">
                        <select class="form-select search-glass" name="sort">
                            <option value="name" <c:if test="${sort == 'name'}">selected</c:if>>Sort by Name</option>
                            <option value="points" <c:if test="${sort == 'points'}">selected</c:if>>Sort by Loyalty Points</option>
                            <option value="type" <c:if test="${sort == 'type'}">selected</c:if>>Sort by Type</option>
                            <option value="email" <c:if test="${sort == 'email'}">selected</c:if>>Sort by Email</option>
                            <option value="id" <c:if test="${sort == 'id'}">selected</c:if>>Sort by Guest ID</option>
                        </select>
                    </div>
                    <div class="col-md-3">
                        <button type="submit" class="btn btn-light w-100">Apply Sort</button>
                    </div>
                </div>
            </form>
        </div>



        <!-- Data Table -->
        <div class="glass-card p-0 animate__animated animate__fadeInUp" style="animation-delay: 0.2s;">
            <div class="glass-table-container table-responsive">
                <table class="table table-hover table-hotel mb-0" id="guestTable">
                    <thead>
                    <tr>
                        <th>Guest ID</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Type</th>
                        <th>Loyalty Points</th>
                        <th class="text-center">Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach var="g" items="${guests}">
                        <tr class="animate__animated animate__fadeIn">
                            <td><code style="background: rgba(0,0,0,0.4); padding: 4px 8px; border-radius: 4px; color: #99f2c8;">${g.id}</code></td>
                            <td class="fw-bold">${g.name}</td>
                            <td><i class="bi bi-envelope-at opacity-75 me-1"></i> ${g.email}</td>
                            <td><i class="bi bi-telephone opacity-75 me-1"></i> ${g.phone}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${g.guestType == 'VIPGuest'}">
                                        <span class="badge badge-vip px-3 py-2 shadow-sm"><i class="bi bi-star-fill me-1"></i> VIP</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge badge-regular px-3 py-2"><i class="bi bi-person me-1"></i> Regular</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td><i class="bi bi-star-fill text-warning"></i> <strong>${g.loyaltyPoints}</strong></td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/guests" method="post" class="d-inline"
                                      onsubmit="return confirm('Are you sure you want to delete this guest? This action cannot be undone.');">
                                    <input type="hidden" name="action" value="delete">
                                    <input type="hidden" name="id" value="${g.id}">
                                    <button type="submit" class="btn btn-glass-danger btn-sm px-3">
                                        <i class="bi bi-trash3-fill"></i> Delete
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty guests}">
                        <tr>
                            <td colspan="7" class="text-center py-5">
                                <i class="bi bi-emoji-frown fs-1 d-block mb-3 opacity-50"></i>
                                <span class="fs-5 opacity-75">No guests registered yet.</span>
                            </td>
                        </tr>
                    </c:if>
                    </tbody>
                </table>
            </div>
        </div>

    </div>
</div>

<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    // Search Filter Logic
    document.getElementById('searchInput').addEventListener('keyup', function() {
        const filter = this.value.toLowerCase();
        document.querySelectorAll('#guestTable tbody tr').forEach(function(row) {
            // Include all text content from the row to allow searching by phone, ID, etc.
            const text = row.textContent.toLowerCase();
            if(text.includes(filter)) {
                row.style.display = '';
                row.classList.add('animate__fadeIn'); // Add animation when appearing
            } else {
                row.style.display = 'none';
                row.classList.remove('animate__fadeIn');
            }
        });
    });
</script>
</body>
</html>