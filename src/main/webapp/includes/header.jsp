<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.hotel.model.*" %>
<%
    Guest loggedInGuest = (Guest) session.getAttribute("loggedInGuest");
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
                    <a class="nav-link" href="<%= contextPath %>/index.jsp">
                        <i class="bi bi-house"></i> Home
                    </a>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <% if (loggedInGuest != null) { %>
                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" data-bs-toggle="dropdown">
                        <i class="bi bi-person-circle"></i> <%= loggedInGuest.getName() %>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark dropdown-menu-end">
                        <li><a class="dropdown-item" href="<%= contextPath %>/guests?action=profile">My Profile</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li><a class="dropdown-item text-danger" href="<%= contextPath %>/guests?action=logout">Logout</a></li>
                    </ul>
                </li>
                <% } else { %>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/guests?action=login">
                        <i class="bi bi-box-arrow-in-right"></i> Guest Login
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="<%= contextPath %>/guests?action=register">
                        <i class="bi bi-person-plus"></i> Register
                    </a>
                </li>
                <% } %>
            </ul>
        </div>
    </div>
</nav>