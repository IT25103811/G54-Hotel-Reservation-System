package com.hotel.servlet;

import com.hotel.dao.*;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@WebServlet("/reservations")
public class ReservationServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO roomDAO = new RoomDAO();
    private final GuestDAO guestDAO = new GuestDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        try {
            switch (action) {
                case "book":
                    req.setAttribute("availableRooms", roomDAO.findAvailable());
                    req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
                    break;
                case "modify":
                    Reservation res = reservationDAO.findById(req.getParameter("reservationId"));
                    req.setAttribute("reservation", res);
                    req.getRequestDispatcher("/reservation/modify.jsp").forward(req, resp);
                    break;
                default:
                    handleList(req, resp);
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
                case "book":
                    handleBook(req, resp);
                    break;
                case "modify":
                    handleModify(req, resp);
                    break;
                case "cancel":
                    handleCancel(req, resp);
                    break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    private void handleList(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        List<Reservation> reservations;
        if (session != null && session.getAttribute("loggedInStaff") != null) {
            reservations = reservationDAO.findAll();
        } else if (session != null && session.getAttribute("loggedInGuest") != null) {
            Guest guest = (Guest) session.getAttribute("loggedInGuest");
            reservations = reservationDAO.findByGuestId(guest.getId());
        } else {
            resp.sendRedirect(req.getContextPath() + "/guests?action=login");
            return;
        }
        req.setAttribute("reservations", reservations);
        req.getRequestDispatcher("/reservation/list.jsp").forward(req, resp);
    }

    private void handleBook(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        String guestId = req.getParameter("guestId");
        String roomNumber = req.getParameter("roomNumber");
        String checkInStr = req.getParameter("checkIn");
        String checkOutStr = req.getParameter("checkOut");

        if (guestId == null || guestId.isEmpty() || roomNumber == null || checkInStr == null || checkOutStr == null) {
            req.setAttribute("error", "All fields are required.");
            req.setAttribute("availableRooms", roomDAO.findAvailable());
            req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
            return;
        }
        LocalDate checkIn = LocalDate.parse(checkInStr);
        LocalDate checkOut = LocalDate.parse(checkOutStr);
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            req.setAttribute("error", "Check-out must be after check-in.");
            req.setAttribute("availableRooms", roomDAO.findAvailable());
            req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
            return;
        }
        Room room = roomDAO.findByNumber(roomNumber);
        Guest guest = guestDAO.findById(guestId);
        if (room == null || guest == null) {
            req.setAttribute("error", "Invalid room or guest.");
            req.setAttribute("availableRooms", roomDAO.findAvailable());
            req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
            return;
        }
        double baseAmount = room.calculatePrice() * nights;
        double discount = guest.calculateDiscount();
        double totalAmount = baseAmount * (1 - discount);

        String id = FileUtils.generateId("R");
        Reservation reservation = new Reservation(id, guestId, roomNumber, checkIn, checkOut,
                Reservation.Status.CONFIRMED, totalAmount);
        reservationDAO.save(reservation);
        room.setAvailable(false);
        roomDAO.update(room);
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    private void handleModify(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String resId = req.getParameter("reservationId");
        Reservation res = reservationDAO.findById(resId);
        if (res == null) { resp.sendRedirect(req.getContextPath() + "/reservations?action=list"); return; }
        String checkInStr = req.getParameter("checkIn");
        String checkOutStr = req.getParameter("checkOut");
        String statusStr = req.getParameter("status");
        if (checkInStr != null && !checkInStr.isEmpty()) res.setCheckIn(LocalDate.parse(checkInStr));
        if (checkOutStr != null && !checkOutStr.isEmpty()) res.setCheckOut(LocalDate.parse(checkOutStr));
        if (statusStr != null && !statusStr.isEmpty()) {
            try { res.setStatus(Reservation.Status.valueOf(statusStr)); } catch (Exception ignored) {}
        }
        reservationDAO.update(res);
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    private void handleCancel(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String resId = req.getParameter("reservationId");
        Reservation res = reservationDAO.findById(resId);
        if (res != null) {
            res.setStatus(Reservation.Status.CANCELLED);
            reservationDAO.update(res);
            Room room = roomDAO.findByNumber(res.getRoomNumber());
            if (room != null) { room.setAvailable(true); roomDAO.update(room); }
        }
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        req.setAttribute("errorMessage", e.getMessage());
        req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
    }
}
