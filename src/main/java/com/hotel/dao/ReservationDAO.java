package com.hotel.dao;

import com.hotel.model.*;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO for Reservation data.
 * Format: reservationId|guestId|roomNumber|checkIn|checkOut|status|totalAmount|specialRequests|createdAt
 *
 * Improvements (Commit 1):
 *  - Added checkDateOverlap() to prevent double-booking
 *  - Added findByRoomNumber() and findByStatus()
 *  - Added countActiveByGuest() for guest booking limits
 *  - Graceful fromLine() with 9-field, backward compat with old 7-field lines
 *  - Added specialRequests and createdAt fields
 *
 * Improvements (Commit 2 - Edit Validation):
 *  - Added checkDuplicateGuestRoomBooking() to prevent a guest from booking the
 *    same room twice for overlapping dates during an edit operation.
 */
public class ReservationDAO {
    private static final String FILE = "reservations.txt";

    public ReservationDAO() {
        FileUtils.ensureFileExists(FILE);
    }

    public void save(Reservation res) {
        if (res.getCreatedAt() == null || res.getCreatedAt().isEmpty()) {
            res.setCreatedAt(LocalDate.now().toString());
        }
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(res));
        FileUtils.writeLines(FILE, lines);
    }

    public void update(Reservation res) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Reservation r = fromLine(line);
            if (r != null && r.getReservationId().equals(res.getReservationId())) {
                updated.add(toLine(res));
            } else {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    public void delete(String id) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Reservation r = fromLine(line);
            if (r == null || !r.getReservationId().equals(id)) updated.add(line);
        }
        FileUtils.writeLines(FILE, updated);
    }

    public Reservation findById(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && id.equals(r.getReservationId())) return r;
        }
        return null;
    }

    public List<Reservation> findAll() {
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null) result.add(r);
        }
        return result;
    }

    public List<Reservation> findByGuestId(String guestId) {
        if (guestId == null) return Collections.emptyList();
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && guestId.equals(r.getGuestId())) result.add(r);
        }
        return result;
    }

    public List<Reservation> findByRoomNumber(String roomNumber) {
        if (roomNumber == null) return Collections.emptyList();
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && roomNumber.equals(r.getRoomNumber())) result.add(r);
        }
        return result;
    }

    public List<Reservation> findByStatus(Reservation.Status status) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : findAll()) {
            if (r.getStatus() == status) result.add(r);
        }
        return result;
    }

    public List<Reservation> findActive() {
        return findAll().stream()
                .filter(r -> r.getStatus() != Reservation.Status.CANCELLED
                        && r.getStatus() != Reservation.Status.CHECKED_OUT)
                .collect(Collectors.toList());
    }

    /**
     * Returns true if the given room has a confirmed/pending/checked-in reservation
     * that overlaps with [checkIn, checkOut), excluding the reservation with excludeId.
     *
     * Used for: preventing any double-booking of a room (by any guest).
     */
    public boolean checkDateOverlap(String roomNumber, LocalDate checkIn,
                                    LocalDate checkOut, String excludeId) {
        for (Reservation r : findByRoomNumber(roomNumber)) {
            if (excludeId != null && excludeId.equals(r.getReservationId())) continue;
            if (r.getStatus() == Reservation.Status.CANCELLED
                    || r.getStatus() == Reservation.Status.CHECKED_OUT) continue;
            if (r.getCheckIn() == null || r.getCheckOut() == null) continue;
            if (r.getCheckIn().isBefore(checkOut) && r.getCheckOut().isAfter(checkIn)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the given guest already has a DIFFERENT active reservation
     * for the SAME room that overlaps with [checkIn, checkOut).
     * Excludes the reservation identified by excludeId (i.e. the one being edited).
     *
     * Used for: preventing duplicate reservations when a guest edits an existing booking.
     */
    public boolean checkDuplicateGuestRoomBooking(String guestId, String roomNumber,
                                                  LocalDate checkIn, LocalDate checkOut,
                                                  String excludeId) {
        for (Reservation r : findByGuestId(guestId)) {
            if (excludeId != null && excludeId.equals(r.getReservationId())) continue;
            if (!roomNumber.equals(r.getRoomNumber())) continue;
            if (r.getStatus() == Reservation.Status.CANCELLED
                    || r.getStatus() == Reservation.Status.CHECKED_OUT) continue;
            if (r.getCheckIn() == null || r.getCheckOut() == null) continue;
            if (r.getCheckIn().isBefore(checkOut) && r.getCheckOut().isAfter(checkIn)) {
                return true;
            }
        }
        return false;
    }

    public long countActiveByGuest(String guestId) {
        return findByGuestId(guestId).stream()
                .filter(r -> r.getStatus() != Reservation.Status.CANCELLED
                        && r.getStatus() != Reservation.Status.CHECKED_OUT)
                .count();
    }

    private String toLine(Reservation r) {
        return String.join("|",
                safe(r.getReservationId()),
                safe(r.getGuestId()),
                safe(r.getRoomNumber()),
                r.getCheckIn()  != null ? r.getCheckIn().toString()  : "",
                r.getCheckOut() != null ? r.getCheckOut().toString() : "",
                r.getStatus()   != null ? r.getStatus().name()       : "",
                String.format("%.2f", r.getTotalAmount()),
                safe(r.getSpecialRequests()),
                r.getCreatedAt() != null ? r.getCreatedAt() : LocalDate.now().toString()
        );
    }

    private Reservation fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 7) return null;
        try {
            LocalDate checkIn  = p[3].isEmpty() ? null : LocalDate.parse(p[3]);
            LocalDate checkOut = p[4].isEmpty() ? null : LocalDate.parse(p[4]);
            Reservation.Status status = p[5].isEmpty()
                    ? Reservation.Status.PENDING
                    : Reservation.Status.valueOf(p[5]);
            double amount          = parseDouble(p[6]);
            String specialRequests = p.length > 7 ? p[7] : "";
            String createdAt       = p.length > 8 ? p[8] : "";
            Reservation r = new Reservation(p[0], p[1], p[2], checkIn, checkOut, status, amount);
            r.setSpecialRequests(specialRequests);
            r.setCreatedAt(createdAt);
            return r;
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private double parseDouble(String s) {
        try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; }
    }
}
