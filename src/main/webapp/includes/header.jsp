<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Guest loggedInGuest = (Guest) session.getAttribute("loggedInGuest");
    Staff loggedInStaff = (Staff) session.getAttribute("loggedInStaff");
    String contextPath = request.getContextPath();
%>
<nav class="navbar navbar-expand-lg navbar-hotel navbar-dark">
    <div class="container">
        <a class="navbar-brand" href="<%= contextPath %>/index.jsp">
            <i class="bi bi-building"></i> Grand Vista Hotel
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarMain">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarMain">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/rooms?action=list">
                        <i class="bi bi-door-open"></i> Rooms
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/reservations?action=book">
                        <i class="bi bi-calendar-plus"></i> Book Now
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/reviews?action=view">
                        <i class="bi bi-star"></i> Reviews
                    </a>
                </li>
                <% if (loggedInStaff != null) { %>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                        <i class="bi bi-gear"></i> Management
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark">
                        <li><a class="dropdown-item" href="<%= contextPath %>/staff?action=dashboard">Dashboard</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/guests?action=list">Guests</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/reservations?action=list">Reservations</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/payments?action=history">Payments</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/reviews?action=moderate">Moderate Reviews</a></li>
                        <% if (loggedInStaff instanceof Manager) { %>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/rooms?action=add">Add Room</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/staff?action=manage">Manage Staff</a></li>
                        <% } %>
                    </ul>
                </li>
                <% } %>
            </ul>
            <ul class="navbar-nav ms-auto">
                <% if (loggedInGuest != null) { %>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i> <%= loggedInGuest.getName() %>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end">
                        <li><a class="dropdown-item" href="<%= contextPath %>/guests?action=profile">My Profile</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/reservations?action=list">My Reservations</a></li>
                        <li><a class="dropdown-item" href="<%= contextPath %>/payments?action=history">My Payments</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="<%= contextPath %>/guests?action=logout">Logout</a></li>
                    </ul>
                </li>
                <% } else if (loggedInStaff != null) { %>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                        <i class="bi bi-person-badge"></i> <%= loggedInStaff.getName() %>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end">
                        <li><span class="dropdown-item-text text-muted small"><%= loggedInStaff.getRole() %></span></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="<%= contextPath %>/staff?action=logout">Logout</a></li>
                    </ul>
                </li>
                <% } else { %>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/guests?action=login">
                        <i class="bi bi-box-arrow-in-right"></i> Guest Login
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/staff?action=login">
                        <i class="bi bi-shield-lock"></i> Staff Login
                    </a>
                </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>
