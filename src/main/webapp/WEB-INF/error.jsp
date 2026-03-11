<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Error - Grand Vista Hotel</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.0/font/bootstrap-icons.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/includes/header.jsp" %>
<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-6 text-center">
            <i class="bi bi-exclamation-triangle-fill text-danger" style="font-size:5rem;"></i>
            <h2 class="mt-3">Oops! Something went wrong</h2>
            <%
                String errMsg = (String) request.getAttribute("errorMessage");
                if (errMsg == null && exception != null) errMsg = exception.getMessage();
                if (errMsg == null) errMsg = "An unexpected error occurred. Please try again.";
            %>
            <p class="text-muted"><%= errMsg %></p>
            <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-hotel-primary mt-3">
                <i class="bi bi-house"></i> Back to Home
            </a>
        </div>
    </div>
</div>
<%@ include file="/includes/footer.jsp" %>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
