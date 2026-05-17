package com.hotel.servlet;

import com.hotel.dao.*;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

@WebServlet("/reservations")
public class ReservationServlet extends HttpServlet {

    private final ReservationDAO reservationDAO = new ReservationDAO();
    private final RoomDAO        roomDAO        = new RoomDAO();
    private final GuestDAO       guestDAO       = new GuestDAO();

    // ── GET ──────────────────────────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "list";
        try {
            switch (action) {
                case "book":             handleBookForm(req, resp);         break;
                case "modify":           handleModifyForm(req, resp);       break;
                case "view":             handleView(req, resp);             break;
                case "checkAvailability":handleCheckAvailability(req, resp);break;
                default:                 handleList(req, resp);
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    // ── POST ─────────────────────────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) action = "";
        try {
            switch (action) {
                case "book":   handleBook(req, resp);   break;
                case "modify": handleModify(req, resp); break;
                case "cancel": handleCancel(req, resp); break;
                default:
                    resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
            }
        } catch (Exception e) {
            handleError(req, resp, e);
        }
    }

    // ── Handlers: GET ────────────────────────────────────────────────────────

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

        if (session != null && session.getAttribute("loggedInStaff") != null) {
            long pending   = reservations.stream().filter(r -> r.getStatus() == Reservation.Status.PENDING).count();
            long confirmed = reservations.stream().filter(r -> r.getStatus() == Reservation.Status.CONFIRMED).count();
            long checkedIn = reservations.stream().filter(r -> r.getStatus() == Reservation.Status.CHECKED_IN).count();
            req.setAttribute("statPending",   pending);
            req.setAttribute("statConfirmed", confirmed);
            req.setAttribute("statCheckedIn", checkedIn);
        }

        req.setAttribute("reservations", reservations);
        req.getRequestDispatcher("/reservation/list.jsp").forward(req, resp);
    }

    private void handleCheckAvailability(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        PrintWriter out = resp.getWriter();

        String roomNumber  = trim(req.getParameter("roomNumber"));
        String checkInStr  = trim(req.getParameter("checkIn"));
        String checkOutStr = trim(req.getParameter("checkOut"));
        String excludeId   = trim(req.getParameter("excludeId"));

        if (roomNumber.isEmpty() || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            out.print("{\"available\":true}");
            return;
        }

        try {
            LocalDate checkIn  = LocalDate.parse(checkInStr);
            LocalDate checkOut = LocalDate.parse(checkOutStr);

            if (!checkOut.isAfter(checkIn)) {
                out.print("{\"available\":false,\"message\":\"Check-out date must be after check-in date.\"}");
                return;
            }
            if (checkIn.isBefore(LocalDate.now())) {
                out.print("{\"available\":false,\"message\":\"Check-in date cannot be in the past.\"}");
                return;
            }

            String excl = excludeId.isEmpty() ? null : excludeId;
            boolean overlap = reservationDAO.checkDateOverlap(roomNumber, checkIn, checkOut, excl);

            if (overlap) {
                out.print("{\"available\":false,\"message\":\"Room is already booked for selected dates.\"}");
            } else {
                out.print("{\"available\":true}");
            }
        } catch (DateTimeParseException e) {
            out.print("{\"available\":false,\"message\":\"Invalid date format.\"}");
        }
    }


    private void handleBookForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("availableRooms", roomDAO.findAll());
        req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
    }

    private void handleModifyForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null
                || (session.getAttribute("loggedInStaff") == null
                && session.getAttribute("loggedInGuest") == null)) {
            resp.sendRedirect(req.getContextPath() + "/guests?action=login");
            return;
        }

        String id  = req.getParameter("reservationId");
        Reservation res = reservationDAO.findById(id);
        if (res == null) {
            req.setAttribute("errorMessage", "Reservation not found (ID: " + id + ").");
            req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
            return;
        }

        if (session.getAttribute("loggedInStaff") == null) {
            Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
            if (loggedGuest == null || !loggedGuest.getId().equals(res.getGuestId())) {
                resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
                return;
            }
        }

        req.setAttribute("reservation", res);
        // FIX: findAvailable() → findAll()
        req.setAttribute("availableRooms", roomDAO.findAll());
        req.getRequestDispatcher("/reservation/modify.jsp").forward(req, resp);
    }

    private void handleView(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String id  = req.getParameter("reservationId");
        Reservation res = reservationDAO.findById(id);
        if (res == null) {
            resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
            return;
        }
        Guest guest = guestDAO.findById(res.getGuestId());
        req.setAttribute("reservation", res);
        req.setAttribute("guestName", guest != null ? guest.getName() : res.getGuestId());
        req.setAttribute("cancellationFee", res.calculateCancellationFee());
        req.getRequestDispatcher("/reservation/view.jsp").forward(req, resp);
    }

    // ── Handlers: POST ───────────────────────────────────────────────────────

    private void handleBook(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session  = req.getSession(false);
        Guest loggedGuest    = session != null ? (Guest) session.getAttribute("loggedInGuest") : null;

        String guestId       = trim(req.getParameter("guestId"));
        String roomNumber    = trim(req.getParameter("roomNumber"));
        String checkInStr    = trim(req.getParameter("checkIn"));
        String checkOutStr   = trim(req.getParameter("checkOut"));
        String specialReqs   = trim(req.getParameter("specialRequests"));

        if (guestId.isEmpty() || roomNumber.isEmpty() || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            bookError(req, resp, "All required fields must be filled in.");
            return;
        }

        LocalDate checkIn, checkOut;
        try {
            checkIn  = LocalDate.parse(checkInStr);
            checkOut = LocalDate.parse(checkOutStr);
        } catch (DateTimeParseException e) {
            bookError(req, resp, "Invalid date format. Please use the date picker.");
            return;
        }

        if (!checkIn.isAfter(LocalDate.now().minusDays(1))) {
            bookError(req, resp, "Check-in date must be today or in the future.");
            return;
        }
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            bookError(req, resp, "Check-out date must be after check-in date.");
            return;
        }
        if (nights > 90) {
            bookError(req, resp, "Reservations cannot exceed 90 nights. Please contact us for extended stays.");
            return;
        }

        Room  room  = roomDAO.findByNumber(roomNumber);
        Guest guest = guestDAO.findById(guestId);

        if (room == null) {
            bookError(req, resp, "Selected room does not exist.");
            return;
        }
        if (guest == null) {
            bookError(req, resp, "Guest ID not found. Please log in or enter a valid Guest ID.");
            return;
        }

        if (reservationDAO.checkDateOverlap(roomNumber, checkIn, checkOut, null)) {
            bookError(req, resp,
                    "Room " + roomNumber + " is already booked for the selected dates. "
                            + "Please choose different dates or another room.");
            return;
        }

        if (reservationDAO.countActiveByGuest(guestId) >= 5) {
            bookError(req, resp,
                    "You already have 5 active reservations. "
                            + "Please complete or cancel an existing reservation before making a new one.");
            return;
        }

        double baseAmount  = room.calculatePrice() * nights;
        double discount    = guest.calculateDiscount();
        double totalAmount = baseAmount * (1 - discount);

        String id = FileUtils.generateId("R");
        Reservation reservation = new Reservation(
                id, guestId, roomNumber, checkIn, checkOut,
                Reservation.Status.CONFIRMED, totalAmount);
        reservation.setSpecialRequests(specialReqs);
        reservationDAO.save(reservation);



        req.getSession().setAttribute("successMessage",
                "Reservation " + id + " confirmed for room " + roomNumber
                        + " (" + nights + " night" + (nights > 1 ? "s" : "") + ")!");
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    private void handleModify(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        HttpSession session = req.getSession(false);
        if (session == null
                || (session.getAttribute("loggedInStaff") == null
                && session.getAttribute("loggedInGuest") == null)) {
            resp.sendRedirect(req.getContextPath() + "/guests?action=login");
            return;
        }

        String resId    = trim(req.getParameter("reservationId"));
        Reservation res = reservationDAO.findById(resId);
        if (res == null) {
            resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
            return;
        }

        if (session.getAttribute("loggedInStaff") == null) {
            Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
            if (loggedGuest == null || !loggedGuest.getId().equals(res.getGuestId())) {
                resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
                return;
            }
        }

        if (res.getStatus() == Reservation.Status.CANCELLED
                || res.getStatus() == Reservation.Status.CHECKED_OUT) {
            req.setAttribute("reservation", res);
            req.setAttribute("error", "Cannot modify a " + res.getStatus().name().toLowerCase() + " reservation.");
            req.setAttribute("availableRooms", roomDAO.findAll());
            req.getRequestDispatcher("/reservation/modify.jsp").forward(req, resp);
            return;
        }

        String checkInStr  = trim(req.getParameter("checkIn"));
        String checkOutStr = trim(req.getParameter("checkOut"));
        String statusStr   = trim(req.getParameter("status"));
        String specialReqs = trim(req.getParameter("specialRequests"));

        if (!checkInStr.isEmpty() && !checkOutStr.isEmpty()) {
            LocalDate newIn, newOut;
            try {
                newIn  = LocalDate.parse(checkInStr);
                newOut = LocalDate.parse(checkOutStr);
            } catch (DateTimeParseException e) {
                modifyError(req, resp, res, "Invalid date format.");
                return;
            }

            if (newIn.isBefore(LocalDate.now())) {
                modifyError(req, resp, res, "Check-in date cannot be in the past.");
                return;
            }

            long nights = ChronoUnit.DAYS.between(newIn, newOut);
            if (nights <= 0) {
                modifyError(req, resp, res, "Check-out must be after check-in.");
                return;
            }
            if (nights > 90) {
                modifyError(req, resp, res, "Reservations cannot exceed 90 nights.");
                return;
            }

            if (reservationDAO.checkDateOverlap(res.getRoomNumber(), newIn, newOut, resId)) {
                modifyError(req, resp, res,
                        "Room is already booked for selected dates. Please choose different dates.");
                return;
            }

            if (reservationDAO.checkDuplicateGuestRoomBooking(
                    res.getGuestId(), res.getRoomNumber(), newIn, newOut, resId)) {
                modifyError(req, resp, res,
                        "You already have another reservation for this room overlapping the selected dates.");
                return;
            }

            Room room = roomDAO.findByNumber(res.getRoomNumber());
            if (room != null) {
                Guest guest = guestDAO.findById(res.getGuestId());
                double discount = guest != null ? guest.calculateDiscount() : 0;
                res.setTotalAmount(room.calculatePrice() * nights * (1 - discount));
            }
            res.setCheckIn(newIn);
            res.setCheckOut(newOut);
        }

        if (!statusStr.isEmpty() && session.getAttribute("loggedInStaff") != null) {
            try {
                Reservation.Status newStatus = Reservation.Status.valueOf(statusStr);
                if ((newStatus == Reservation.Status.CHECKED_OUT
                        || newStatus == Reservation.Status.CANCELLED)
                        && res.getStatus() != newStatus) {
                    Room room = roomDAO.findByNumber(res.getRoomNumber());
                    if (room != null) { room.setAvailable(true); roomDAO.update(room); }
                }
                res.setStatus(newStatus);
            } catch (IllegalArgumentException ignored) {}
        }

        if (!specialReqs.isEmpty()) {
            res.setSpecialRequests(specialReqs);
        }

        reservationDAO.update(res);
        req.getSession().setAttribute("successMessage", "Reservation updated successfully.");
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    private void handleCancel(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String resId    = trim(req.getParameter("reservationId"));
        Reservation res = reservationDAO.findById(resId);
        if (res != null && res.getStatus() != Reservation.Status.CANCELLED
                && res.getStatus() != Reservation.Status.CHECKED_OUT) {
            res.setStatus(Reservation.Status.CANCELLED);
            reservationDAO.update(res);
            Room room = roomDAO.findByNumber(res.getRoomNumber());
            if (room != null) { room.setAvailable(true); roomDAO.update(room); }
            req.getSession().setAttribute("successMessage",
                    "Reservation " + resId + " has been cancelled.");
        }
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void bookError(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        // FIX: findAvailable() → findAll()
        req.setAttribute("availableRooms", roomDAO.findAll());
        req.setAttribute("prevGuestId",  req.getParameter("guestId"));
        req.setAttribute("prevRoom",     req.getParameter("roomNumber"));
        req.setAttribute("prevCheckIn",  req.getParameter("checkIn"));
        req.setAttribute("prevCheckOut", req.getParameter("checkOut"));
        req.setAttribute("prevSpecial",  req.getParameter("specialRequests"));
        req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
    }

    private void modifyError(HttpServletRequest req, HttpServletResponse resp,
                             Reservation res, String msg)
            throws ServletException, IOException {
        req.setAttribute("reservation", res);
        req.setAttribute("error", msg);
        // FIX: findAvailable() → findAll()
        req.setAttribute("availableRooms", roomDAO.findAll());
        req.getRequestDispatcher("/reservation/modify.jsp").forward(req, resp);
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, Exception e)
            throws ServletException, IOException {
        e.printStackTrace();
        req.setAttribute("errorMessage", "An unexpected error occurred: " + e.getMessage());
        req.getRequestDispatcher("/WEB-INF/error.jsp").forward(req, resp);
    }

    private String trim(String s) { return s == null ? "" : s.trim(); }
}