package com.hotel.servlet;

import com.hotel.service.PaymentService;
import com.hotel.service.ReservationService;
import com.hotel.dao.*;
import com.hotel.model.*;
import com.hotel.util.PaymentSorter;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/payments")
public class PaymentServlet extends HttpServlet {

    private final PaymentService paymentService = new PaymentService();
    private final ReservationService reservationService = new ReservationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "history";
        try {
            switch (action) {
                case "checkout":
                    String resId = req.getParameter("reservationId");
                    try {
                        Reservation res = reservationService.getReservationById(resId);
                        req.setAttribute("reservation", res);
                        req.getRequestDispatcher("/billing/checkout.jsp").forward(req, resp);
                    } catch (IllegalArgumentException e) {
                        req.setAttribute("errorMessage", e.getMessage());
                        req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                    }
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
                case "delete":
                    requireStaffLogin(req, resp);
                    if (!resp.isCommitted()) handleDelete(req, resp);
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
            payments = paymentService.getAllPayments();
        } else if (session != null && session.getAttribute("loggedInGuest") != null) {
            Guest guest = (Guest) session.getAttribute("loggedInGuest");
            payments = new java.util.ArrayList<>();
            for (Reservation r : reservationService.getReservationsByGuest(guest.getId())) {
                payments.addAll(paymentService.getPaymentsByReservation(r.getReservationId()));
            }
            // Sort guest's own payments by date (newest first) via QuickSort
            payments = PaymentSorter.sort(payments, PaymentSorter.SortBy.DATE_DESC);
        } else {
            resp.sendRedirect(req.getContextPath() + "/guests?action=login");
            return;
        }

        // Apply QuickSort based on requested sort criterion
        String sortParam = req.getParameter("sortBy");
        if ("amount_asc".equals(sortParam)) {
            payments = PaymentSorter.sort(payments, PaymentSorter.SortBy.AMOUNT_ASC);
        } else if ("amount_desc".equals(sortParam)) {
            payments = PaymentSorter.sort(payments, PaymentSorter.SortBy.AMOUNT_DESC);
        } else if ("date_asc".equals(sortParam)) {
            payments = PaymentSorter.sort(payments, PaymentSorter.SortBy.DATE_ASC);
        } else if ("status".equals(sortParam)) {
            payments = PaymentSorter.sort(payments, PaymentSorter.SortBy.STATUS);
        } else if ("id".equals(sortParam)) {
            payments = PaymentSorter.sort(payments, PaymentSorter.SortBy.PAYMENT_ID);
        }
        // Default (date_desc) already applied above by getAllPayments()

        req.setAttribute("payments", payments);
        req.setAttribute("sortBy", sortParam);
        req.getRequestDispatcher("/billing/history.jsp").forward(req, resp);
    }

    private void handleCheckout(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String resId = req.getParameter("reservationId");
        try {
            Reservation res = reservationService.getReservationById(resId);
            if (res == null) {
                resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
                return;
            }

            // Block payment if reservation is already confirmed/paid
            if (res.getStatus() == Reservation.Status.CONFIRMED
                    || res.getStatus() == Reservation.Status.CHECKED_IN
                    || res.getStatus() == Reservation.Status.CHECKED_OUT) {
                req.setAttribute("errorMessage",
                        "Reservation " + resId + " has already been paid (status: "
                                + res.getStatus() + "). No further payment is required.");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                return;
            }

            if (res.getStatus() == Reservation.Status.CANCELLED) {
                req.setAttribute("errorMessage",
                        "Cannot process payment for a cancelled reservation.");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                return;
            }

            String paymentType = req.getParameter("paymentType");
            String cardLast4   = req.getParameter("cardLast4");
            String cardHolder  = req.getParameter("cardHolder");

            HttpSession session = req.getSession(false);
            boolean isStaff = session != null && session.getAttribute("loggedInStaff") != null;
            String staffName = "";
            if (isStaff) {
                staffName = ((Staff) session.getAttribute("loggedInStaff")).getName();
            }

            // Guests may only pay by credit card
            if ("Cash".equalsIgnoreCase(paymentType) && !isStaff) {
                req.setAttribute("errorMessage",
                        "Cash payments must be processed by a staff member at the front desk. "
                                + "Please use Credit Card for online payment.");
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
                return;
            }

            // Credit card field validation
            if ("CreditCard".equals(paymentType)) {
                if (cardHolder == null || cardHolder.trim().isEmpty()) {
                    req.setAttribute("errorMessage", "Card holder name is required.");
                    req.setAttribute("reservation", res);
                    req.getRequestDispatcher("/billing/checkout.jsp").forward(req, resp);
                    return;
                }
                if (cardLast4 == null || !cardLast4.trim().matches("\\d{4}")) {
                    req.setAttribute("errorMessage", "Please enter the last 4 digits of your card.");
                    req.setAttribute("reservation", res);
                    req.getRequestDispatcher("/billing/checkout.jsp").forward(req, resp);
                    return;
                }
            }

            Payment payment;
            try {
                if ("CreditCard".equals(paymentType)) {
                    payment = paymentService.processPayment(
                            resId, res.getTotalAmount(), "CARD",
                            cardLast4.trim(), cardHolder.trim(), null
                    );
                } else {
                    payment = paymentService.processPayment(
                            resId, res.getTotalAmount(), "CASH",
                            null, null, staffName
                    );
                }

                req.setAttribute("payment", payment);
                req.setAttribute("reservation", res);
                req.getRequestDispatcher("/billing/payment.jsp").forward(req, resp);
            } catch (IllegalStateException | IllegalArgumentException e) {
                req.setAttribute("errorMessage", e.getMessage());
                req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            }
        } catch (IllegalArgumentException e) {
            req.setAttribute("errorMessage", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
        }
    }

    private void handleVoid(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String paymentId = req.getParameter("paymentId");
        try {
            paymentService.voidPayment(paymentId);
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/payments?action=history");
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String paymentId = req.getParameter("paymentId");
        try {
            paymentService.deletePayment(paymentId);
            req.getSession().setAttribute("successMessage", "Payment " + paymentId + " deleted successfully.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            req.getSession().setAttribute("errorMessage", e.getMessage());
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
