package com.hotel.service;

import com.hotel.dao.ReservationDAO;
import com.hotel.dao.RoomDAO;
import com.hotel.dao.FileUtils;
import com.hotel.dao.GuestDAO;
import com.hotel.model.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Reservation business logic.
 * Responsibilities: validate business rules, double-booking prevention, auto-calculations,
 * coordinate file operations, enforce system integrity.
 */
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final RoomDAO roomDAO;
    private final GuestDAO guestDAO;
    private final RoomService roomService;

    public ReservationService() {
        this.reservationDAO = new ReservationDAO();
        this.roomDAO = new RoomDAO();
        this.guestDAO = new GuestDAO();
        this.roomService = new RoomService();
    }

    /**
     * Get all reservations.
     */
    public List<Reservation> getAllReservations() {
        return reservationDAO.findAll();
    }

    /**
     * Get a specific reservation by ID.
     */
    public Reservation getReservationById(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }
        return reservationDAO.findById(reservationId);
    }

    /**
     * Get all reservations for a specific guest.
     */
    public List<Reservation> getReservationsByGuest(String guestId) {
        if (guestId == null || guestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest ID cannot be empty");
        }
        return reservationDAO.findByGuestId(guestId);
    }

    /**
     * Get all active reservations for a guest.
     */
    public List<Reservation> getActiveReservationsByGuest(String guestId) {
        if (guestId == null || guestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest ID cannot be empty");
        }
        return reservationDAO.findByGuestId(guestId).stream()
                .filter(r -> r.getStatus() != Reservation.Status.CANCELLED
                        && r.getStatus() != Reservation.Status.CHECKED_OUT)
                .collect(Collectors.toList());
    }

    /**
     * Get all reservations for a specific room.
     */
    public List<Reservation> getReservationsByRoom(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        return reservationDAO.findByRoomNumber(roomNumber);
    }

    /**
     * Get reservations by status.
     */
    public List<Reservation> getReservationsByStatus(Reservation.Status status) {
        if (status == null) {
            throw new IllegalArgumentException("Status cannot be null");
        }
        return reservationDAO.findByStatus(status);
    }

    /**
     * CRITICAL: Create a new reservation with full validation.
     *
     * Business Rules Enforced:
     * 1. Check-out date must be after check-in date
     * 2. Room must exist and be available
     * 3. Check-in date cannot be in the past
     * 4. Double-booking prevention: NO overlapping reservations for the same room
     * 5. Guest must exist
     * 6. Auto-calculate number of nights and total amount
     * 7. Guest cannot have more than 5 active reservations
     *
     * @param guestId Guest ID
     * @param roomNumber Room number to book
     * @param checkIn Check-in date (YYYY-MM-DD)
     * @param checkOut Check-out date (YYYY-MM-DD)
     * @param specialRequests Special requests
     * @return Created Reservation object
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException if business rule violation occurs
     */
    public Reservation createReservation(String guestId, String roomNumber,
                                         LocalDate checkIn, LocalDate checkOut,
                                         String specialRequests) {
        // 1. Validate inputs
        if (guestId == null || guestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Guest ID cannot be empty");
        }
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }

        // 2. Validate date range
        if (!checkOut.isAfter(checkIn)) {
            throw new IllegalArgumentException("Check-out date must be after check-in date");
        }

        // 3. Check-in cannot be in the past
        if (checkIn.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Check-in date cannot be in the past");
        }

        // 4. Calculate nights
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        if (nights <= 0) {
            throw new IllegalArgumentException("Reservation must be for at least 1 night");
        }
        if (nights > 365) {
            throw new IllegalArgumentException("Reservations cannot exceed 365 nights");
        }

        // 5. Verify room exists
        Room room = roomDAO.findByNumber(roomNumber);
        if (room == null) {
            throw new IllegalArgumentException("Room " + roomNumber + " does not exist");
        }

        // 6. Verify room is available
        if (!room.isAvailable()) {
            throw new IllegalStateException("Room " + roomNumber + " is not currently available");
        }

        // 7. Verify guest exists
        Guest guest = guestDAO.findById(guestId);
        if (guest == null) {
            throw new IllegalArgumentException("Guest ID " + guestId + " not found");
        }

        // 8. CRITICAL: Double-booking prevention - Check for overlapping reservations
        boolean hasOverlap = reservationDAO.checkDateOverlap(roomNumber, checkIn, checkOut, null);
        if (hasOverlap) {
            throw new IllegalStateException(
                    "Room " + roomNumber + " is already booked for the selected dates. "
                            + "Please choose different dates or another room.");
        }

        // 9. Check active reservation limit
        long activeCount = getActiveReservationsByGuest(guestId).size();
        if (activeCount >= 5) {
            throw new IllegalStateException(
                    "Guest already has 5 active reservations. "
                            + "Please complete or cancel an existing reservation before making a new one.");
        }

        // 10. Auto-calculate total amount
        double totalAmount = room.calculatePrice() * nights * (1 - guest.calculateDiscount());

        // 11. Create reservation with PENDING status (not CONFIRMED)
        String reservationId = FileUtils.generateId("RES");
        Reservation reservation = new Reservation(
                reservationId, guestId, roomNumber,
                checkIn, checkOut,
                Reservation.Status.PENDING,
                totalAmount
        );
        reservation.setSpecialRequests(specialRequests != null ? specialRequests : "");
        reservation.setCreatedAt(LocalDate.now().toString());

        // 12. Save to file
        reservationDAO.save(reservation);

        // 13. Update room status - mark as reserved (but not yet occupied)
        roomService.updateRoomStatusByReservation(roomNumber, Reservation.Status.PENDING);

        return reservation;
    }

    /**
     * Update an existing reservation.
     * Validates date changes and enforces business rules.
     */
    public void updateReservation(Reservation reservation) {
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation cannot be null");
        }
        if (reservation.getReservationId() == null || reservation.getReservationId().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }

        Reservation existing = reservationDAO.findById(reservation.getReservationId());
        if (existing == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservation.getReservationId());
        }

        // Cannot modify cancelled or checked-out reservations
        if (existing.getStatus() == Reservation.Status.CANCELLED
                || existing.getStatus() == Reservation.Status.CHECKED_OUT) {
            throw new IllegalStateException(
                    "Cannot modify a " + existing.getStatus().name().toLowerCase() + " reservation");
        }

        // If dates changed, re-validate double-booking
        if (!existing.getCheckIn().equals(reservation.getCheckIn())
                || !existing.getCheckOut().equals(reservation.getCheckOut())) {

            if (!reservation.getCheckOut().isAfter(reservation.getCheckIn())) {
                throw new IllegalArgumentException("Check-out date must be after check-in date");
            }

            long nights = ChronoUnit.DAYS.between(reservation.getCheckIn(), reservation.getCheckOut());
            if (nights <= 0) {
                throw new IllegalArgumentException("Reservation must be for at least 1 night");
            }

            // Re-check for overlaps (excluding this reservation)
            boolean hasOverlap = reservationDAO.checkDateOverlap(
                    existing.getRoomNumber(),
                    reservation.getCheckIn(),
                    reservation.getCheckOut(),
                    reservation.getReservationId()
            );

            if (hasOverlap) {
                throw new IllegalStateException(
                        "Room is already booked for the selected dates. "
                                + "Please choose different dates.");
            }

            // Recalculate total amount if dates changed
            Room room = roomDAO.findByNumber(existing.getRoomNumber());
            if (room != null) {
                Guest guest = guestDAO.findById(existing.getGuestId());
                double discount = guest != null ? guest.calculateDiscount() : 0;
                double newTotal = room.calculatePrice() * nights * (1 - discount);
                reservation.setTotalAmount(newTotal);
            }
        }

        // Save changes
        reservationDAO.update(reservation);

        // Update room status if status changed
        if (existing.getStatus() != reservation.getStatus()) {
            roomService.updateRoomStatusByReservation(
                    existing.getRoomNumber(),
                    reservation.getStatus()
            );
        }
    }

    /**
     * Cancel a reservation.
     * Updates reservation status to CANCELLED and makes room available again.
     */
    public void cancelReservation(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }

        Reservation reservation = reservationDAO.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        if (reservation.getStatus() == Reservation.Status.CANCELLED) {
            throw new IllegalStateException("Reservation is already cancelled");
        }

        if (reservation.getStatus() == Reservation.Status.CHECKED_OUT) {
            throw new IllegalStateException("Cannot cancel a checked-out reservation");
        }

        reservation.setStatus(Reservation.Status.CANCELLED);
        reservationDAO.update(reservation);

        // Make room available again
        roomService.updateRoomStatusByReservation(
                reservation.getRoomNumber(),
                Reservation.Status.CANCELLED
        );
    }

    /**
     * Check if dates for a room are available.
     * Returns true if no overlapping reservations found.
     */
    public boolean isRoomAvailableForDates(String roomNumber, LocalDate checkIn,
                                           LocalDate checkOut, String excludeReservationId) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Check-in and check-out dates are required");
        }

        if (!checkOut.isAfter(checkIn)) {
            return false;
        }

        String excludeId = (excludeReservationId == null || excludeReservationId.isEmpty())
                ? null : excludeReservationId;
        return !reservationDAO.checkDateOverlap(roomNumber, checkIn, checkOut, excludeId);
    }

    /**
     * Calculate cancellation fee based on days until check-in.
     * Business rule:
     * - More than 7 days before: No cancellation fee (100% refund)
     * - 1-7 days before: 50% refund
     * - Day of check-in or later: No refund
     */
    public double calculateCancellationFee(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }

        Reservation reservation = reservationDAO.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        return reservation.calculateCancellationFee();
    }

    /**
     * Get number of nights for a reservation.
     */
    public long getNumberOfNights(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }

        Reservation reservation = reservationDAO.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        return reservation.getNights();
    }
}

