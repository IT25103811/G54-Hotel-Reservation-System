package com.hotel.servlet;

import com.hotel.dao.*;
import com.hotel.model.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * ReservationServlet – handles all reservation CRUD operations.
 *
 * Fixes applied:
 *  - handleModifyForm(): Added authentication guard (redirects to login if no session).
 *                        Added guest ownership check (guest can only modify their own reservation).
 *  - handleModify():     Added authentication guard.
 *                        Added guest ownership check before allowing the update.
 *                        [NEW] Past check-in date is now rejected.
 *                        [NEW] Duplicate guest+room booking on overlapping dates is now prevented.
 *
 * All other logic is unchanged to avoid breaking other modules.
 */
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
                case "book":
                    handleBookForm(req, resp);
                    break;
                case "modify":
                    handleModifyForm(req, resp);
                    break;
                case "view":
                    handleView(req, resp);
                    break;
                default:
                    handleList(req, resp);
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

        // Stats for staff dashboard card
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

    private void handleBookForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("availableRooms", roomDAO.findAvailable());
        req.getRequestDispatcher("/reservation/book.jsp").forward(req, resp);
    }

    /**
     * FIX #3: Added authentication guard.
     * FIX #4: Added guest ownership check — a guest may only edit their own reservation.
     *
     * Previously this method had no auth check at all, meaning:
     *  - Unauthenticated users could access the edit form directly via URL.
     *  - Any logged-in guest could open the edit form for any reservation by
     *    guessing or copying a reservationId from the URL.
     */
    private void handleModifyForm(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // ── Auth guard ───────────────────────────────────────────────────────
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

        // ── Ownership check (guests only) ────────────────────────────────────
        if (session.getAttribute("loggedInStaff") == null) {
            Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
            if (loggedGuest == null || !loggedGuest.getId().equals(res.getGuestId())) {
                // Guest is trying to edit someone else's reservation — silently redirect
                resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
                return;
            }
        }

        req.setAttribute("reservation", res);
        req.setAttribute("availableRooms", roomDAO.findAvailable());
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
        // Fetch guest name for display
        Guest guest = guestDAO.findById(res.getGuestId());
        req.setAttribute("reservation", res);
        req.setAttribute("guestName", guest != null ? guest.getName() : res.getGuestId());
        req.setAttribute("cancellationFee", res.calculateCancellationFee());
        req.getRequestDispatcher("/reservation/view.jsp").forward(req, resp);
    }

    // ── Handlers: POST ───────────────────────────────────────────────────────

    private void handleBook(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        // ── 1. Collect params ────────────────────────────────────────────────
        HttpSession session  = req.getSession(false);
        Guest loggedGuest    = session != null ? (Guest) session.getAttribute("loggedInGuest") : null;

        String guestId       = trim(req.getParameter("guestId"));
        String roomNumber    = trim(req.getParameter("roomNumber"));
        String checkInStr    = trim(req.getParameter("checkIn"));
        String checkOutStr   = trim(req.getParameter("checkOut"));
        String specialReqs   = trim(req.getParameter("specialRequests"));

        // ── 2. Required-field validation ─────────────────────────────────────
        if (guestId.isEmpty() || roomNumber.isEmpty() || checkInStr.isEmpty() || checkOutStr.isEmpty()) {
            bookError(req, resp, "All required fields must be filled in.");
            return;
        }

        // ── 3. Date parsing ──────────────────────────────────────────────────
        LocalDate checkIn, checkOut;
        try {
            checkIn  = LocalDate.parse(checkInStr);
            checkOut = LocalDate.parse(checkOutStr);
        } catch (DateTimeParseException e) {
            bookError(req, resp, "Invalid date format. Please use the date picker.");
            return;
        }

        // ── 4. Date logic ────────────────────────────────────────────────────
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

        // ── 5. Entity validation ─────────────────────────────────────────────
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
        if (!room.isAvailable()) {
            bookError(req, resp, "Room " + roomNumber + " is currently unavailable.");
            return;
        }

        // ── 6. Date-overlap check ────────────────────────────────────────────
        if (reservationDAO.checkDateOverlap(roomNumber, checkIn, checkOut, null)) {
            bookError(req, resp,
                    "Room " + roomNumber + " is already booked for the selected dates. "
                            + "Please choose different dates or another room.");
            return;
        }

        // ── 7. Guest active-booking limit (max 5) ────────────────────────────
        if (reservationDAO.countActiveByGuest(guestId) >= 5) {
            bookError(req, resp,
                    "You already have 5 active reservations. "
                            + "Please complete or cancel an existing reservation before making a new one.");
            return;
        }

        // ── 8. Calculate price ───────────────────────────────────────────────
        double baseAmount  = room.calculatePrice() * nights;
        double discount    = guest.calculateDiscount();
        double totalAmount = baseAmount * (1 - discount);

        // ── 9. Persist ───────────────────────────────────────────────────────
        String id = FileUtils.generateId("R");
        Reservation reservation = new Reservation(
                id, guestId, roomNumber, checkIn, checkOut,
                Reservation.Status.CONFIRMED, totalAmount);
        reservation.setSpecialRequests(specialReqs);
        reservationDAO.save(reservation);

        room.setAvailable(false);
        roomDAO.update(room);

        req.getSession().setAttribute("successMessage",
                "Reservation " + id + " confirmed for room " + roomNumber
                        + " (" + nights + " night" + (nights > 1 ? "s" : "") + ")!");
        resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
    }

    /**
     * FIX #3: Added authentication guard.
     * FIX #4: Added guest ownership check — a guest may only update their own reservation.
     * FIX #5: [NEW] Check-in date cannot be in the past.
     * FIX #6: [NEW] Duplicate booking prevention — same guest, same room, overlapping dates.
     *
     * Previously this method had no auth or ownership checks, meaning any POST to
     * /reservations?action=modify could modify any reservation — a serious security gap.
     */
    private void handleModify(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {

        // ── Auth guard ───────────────────────────────────────────────────────
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

        // ── Ownership check (guests only) ────────────────────────────────────
        if (session.getAttribute("loggedInStaff") == null) {
            Guest loggedGuest = (Guest) session.getAttribute("loggedInGuest");
            if (loggedGuest == null || !loggedGuest.getId().equals(res.getGuestId())) {
                resp.sendRedirect(req.getContextPath() + "/reservations?action=list");
                return;
            }
        }

        // Prevent editing cancelled/checked-out reservations
        if (res.getStatus() == Reservation.Status.CANCELLED
                || res.getStatus() == Reservation.Status.CHECKED_OUT) {
            req.setAttribute("reservation", res);
            req.setAttribute("error", "Cannot modify a " + res.getStatus().name().toLowerCase() + " reservation.");
            req.setAttribute("availableRooms", roomDAO.findAvailable());
            req.getRequestDispatcher("/reservation/modify.jsp").forward(req, resp);
            return;
        }

        String checkInStr  = trim(req.getParameter("checkIn"));
        String checkOutStr = trim(req.getParameter("checkOut"));
        String statusStr   = trim(req.getParameter("status"));
        String specialReqs = trim(req.getParameter("specialRequests"));

        // ── Date validation ──────────────────────────────────────────────────
        if (!checkInStr.isEmpty() && !checkOutStr.isEmpty()) {
            LocalDate newIn, newOut;
            try {
                newIn  = LocalDate.parse(checkInStr);
                newOut = LocalDate.parse(checkOutStr);
            } catch (DateTimeParseException e) {
                modifyError(req, resp, res, "Invalid date format.");
                return;
            }

            // FIX #5: check-in date must not be in the past
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

            // Room availability overlap check (excludes current reservation's own dates)
            if (reservationDAO.checkDateOverlap(res.getRoomNumber(), newIn, newOut, resId)) {
                modifyError(req, resp, res,
                        "Room is already booked for selected dates. Please choose different dates.");
                return;
            }

            // FIX #6: Duplicate booking — same guest, same room, overlapping dates (excluding self)
            if (reservationDAO.checkDuplicateGuestRoomBooking(
                    res.getGuestId(), res.getRoomNumber(), newIn, newOut, resId)) {
                modifyError(req, resp, res,
                        "You already have another reservation for this room overlapping the selected dates.");
                return;
            }

            // Recalculate total if dates changed
            Room room = roomDAO.findByNumber(res.getRoomNumber());
            if (room != null) {
                Guest guest = guestDAO.findById(res.getGuestId());
                double discount = guest != null ? guest.calculateDiscount() : 0;
                res.setTotalAmount(room.calculatePrice() * nights * (1 - discount));
            }
            res.setCheckIn(newIn);
            res.setCheckOut(newOut);
        }

        // ── Status update (staff only) ───────────────────────────────────────
        if (!statusStr.isEmpty() && session.getAttribute("loggedInStaff") != null) {
            try {
                Reservation.Status newStatus = Reservation.Status.valueOf(statusStr);
                // Release room if checking out or cancelling
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
        req.setAttribute("availableRooms", roomDAO.findAvailable());
        // Re-populate form values so user doesn't lose their input
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
        req.setAttribute("availableRooms", roomDAO.findAvailable());
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
