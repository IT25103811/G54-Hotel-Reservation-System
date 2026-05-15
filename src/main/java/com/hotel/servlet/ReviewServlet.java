package com.hotel.servlet;

import com.hotel.dao.*;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/reviews")
public class ReviewServlet extends HttpServlet {

    private final ReviewDAO reviewDAO = new ReviewDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "view";
        try {
            switch (action) {
                case "submit":
                    req.getRequestDispatcher("/review/submit.jsp").forward(req, resp);
                    break;
                case "edit":
                    Review r = reviewDAO.findById(req.getParameter("reviewId"));
                    req.setAttribute("review", r);
                    req.getRequestDispatcher("/review/submit.jsp").forward(req, resp);
                    break;
                case "moderate":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) {
                        req.setAttribute("reviews", reviewDAO.findAll());
                        req.getRequestDispatcher("/review/moderate.jsp").forward(req, resp);
                    }
                    break;
                default:
                    req.setAttribute("reviews", reviewDAO.findAll());
                    req.getRequestDispatcher("/review/view.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";
        try {
            switch (action) {
                case "submit":
                    handleSubmit(req, resp);
                    break;
                case "edit":
                    handleEdit(req, resp);
                    break;
                case "delete":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) {
                        reviewDAO.delete(req.getParameter("reviewId"));
                        resp.sendRedirect(req.getContextPath() + "/reviews?action=moderate");
                    }
                    break;
                case "moderate":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) handleModerate(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/reviews?action=view");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleSubmit(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        int rating = parseInt(req.getParameter("rating"));
        String comment = req.getParameter("comment");
        boolean anonymous = "on".equals(req.getParameter("anonymous"));
        String alias = req.getParameter("alias");
        String id = FileUtils.generateId("RV");

        HttpSession session = req.getSession(false);
        Review review;
        if (!anonymous && session != null && session.getAttribute("loggedInGuest") != null) {
            Guest guest = (Guest) session.getAttribute("loggedInGuest");
            String resId = req.getParameter("reservationId");
            review = new VerifiedGuestReview(id, rating, comment, LocalDateTime.now(),
                    null, guest.getId(), guest.getName(), resId);
        } else {
            review = new AnonymousReview(id, rating, comment, LocalDateTime.now(),
                    null, alias != null && !alias.isEmpty() ? alias : "Anonymous");
        }
        reviewDAO.save(review);
        resp.sendRedirect(req.getContextPath() + "/reviews?action=view");
    }

    private void handleEdit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reviewId = req.getParameter("reviewId");
        Review review = reviewDAO.findById(reviewId);
        if (review == null) { resp.sendRedirect(req.getContextPath() + "/reviews?action=view"); return; }
        review.setRating(parseInt(req.getParameter("rating")));
        review.setComment(req.getParameter("comment"));
        reviewDAO.update(review);
        resp.sendRedirect(req.getContextPath() + "/reviews?action=view");
    }

    private void handleModerate(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String reviewId = req.getParameter("reviewId");
        Review review = reviewDAO.findById(reviewId);
        if (review != null) {
            review.setHotelResponse(req.getParameter("hotelResponse"));
            reviewDAO.update(review);
        }
        resp.sendRedirect(req.getContextPath() + "/reviews?action=moderate");
    }

    private void requireStaffLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("loggedInStaff") == null) {
            resp.sendRedirect(req.getContextPath() + "/staff?action=login");
        }
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        req.setAttribute("errorMessage", e.getMessage());
        req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
    }

    private int parseInt(String s) { try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 3; } }
}
