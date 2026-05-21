package com.hotel.servlet;

import com.hotel.dao.*;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/payments")
public class PaymentServlet extends HttpServlet {

    private final PaymentDAO paymentDAO = new PaymentDAO();
    private final ReservationDAO reservationDAO = new ReservationDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "history";
        try {
            switch (action) {
                case "checkout":
                    String resId = req.getParameter("reservationId");
                    Reservation res = reservationDAO.findById(resId);
                    req.setAttribute("reservation", res);
                    req.getRequestDispatcher("/billing/checkout.jsp").forward(req, resp);
                    break;
                case "history":
                    handleHistory(req, resp);
                    break;
                default:
                    handleHistory(req, resp);
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
                case "checkout":
                    handleCheckout(req, resp);
                    break;
                case "void":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) handleVoid(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/payments?action=history");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleHistory(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        List<Payment> payments;
        if (session != null && session.getAttribute("loggedInStaff") != null) {
            payments = paymentDAO.findAll();
        } else if (session != null && session.getAttribute("loggedInGuest") != null) {
            Guest guest = (Guest) session.getAttribute("loggedInGuest");
            payments = new java.util.ArrayList<>();
            for (Reservation r : reservationDAO.findByGuestId(guest.getId())) {
                payments.addAll(paymentDAO.findByReservationId(r.getReservationId()));
            }
        } else {
            resp.sendRedirect(req.getContextPath() + "/guests?action=login");
            return;
        }
        req.setAttribute("payments", payments);
        req.getRequestDispatcher("/billing/history.jsp").forward(req, resp);
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String resId = req.getParameter("reservationId");
        String paymentType = req.getParameter("paymentType");
        Reservation res = reservationDAO.findById(resId);
        if (res == null) { resp.sendRedirect(req.getContextPath() + "/reservations?action=list"); return; }

        String paymentId = FileUtils.generateId("P");
        Payment payment;
        if ("CreditCard".equals(paymentType)) {
            String cardLast4 = req.getParameter("cardLast4");
            String cardHolder = req.getParameter("cardHolder");
            payment = new CreditCardPayment(paymentId, resId, res.getTotalAmount(),
                    Payment.Status.PAID, LocalDateTime.now(), cardLast4, cardHolder);
        } else {
            HttpSession session = req.getSession(false);
            String staffName = "";
            if (session != null && session.getAttribute("loggedInStaff") != null) {
                staffName = ((Staff) session.getAttribute("loggedInStaff")).getName();
            }
            payment = new CashPayment(paymentId, resId, res.getTotalAmount(),
                    Payment.Status.PAID, LocalDateTime.now(), staffName);
        }
        payment.processPayment();
        paymentDAO.save(payment);
        res.setStatus(Reservation.Status.CHECKED_OUT);
        reservationDAO.update(res);
        req.setAttribute("payment", payment);
        req.setAttribute("reservation", res);
        req.getRequestDispatcher("/billing/payment.jsp").forward(req, resp);
    }

    private void handleVoid(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String paymentId = req.getParameter("paymentId");
        Payment p = paymentDAO.findById(paymentId);
        if (p != null) {
            p.setStatus(Payment.Status.VOIDED);
            paymentDAO.update(p);
        }
        resp.sendRedirect(req.getContextPath() + "/payments?action=history");
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
}
