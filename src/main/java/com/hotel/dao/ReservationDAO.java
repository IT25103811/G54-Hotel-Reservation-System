package com.hotel.dao;

import com.hotel.model.*;
import java.time.LocalDate;
import java.util.*;

/**
 * DAO for Reservation data. Format: reservationId|guestId|roomNumber|checkIn|checkOut|status|totalAmount
 */
public class ReservationDAO {
    private static final String FILE = "reservations.txt";

    public ReservationDAO() {
        FileUtils.ensureFileExists(FILE);
    }

    public void save(Reservation res) {
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(res));
        FileUtils.writeLines(FILE, lines);
    }

    public Reservation findById(String id) {
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && id.equals(r.getReservationId())) return r;
        }
        return null;
    }

    public List<Reservation> findByGuestId(String guestId) {
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && guestId.equals(r.getGuestId())) result.add(r);
        }
        return result;
    }

    public List<Reservation> findAll() {
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null) result.add(r);
        }
        return result;
    }

    public List<Reservation> findActive() {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : findAll()) {
            if (r.getStatus() != Reservation.Status.CANCELLED
                    && r.getStatus() != Reservation.Status.CHECKED_OUT) {
                result.add(r);
            }
        }
        return result;
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
            if (r != null && !r.getReservationId().equals(id)) {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    private String toLine(Reservation r) {
        return String.join("|",
                safe(r.getReservationId()), safe(r.getGuestId()), safe(r.getRoomNumber()),
                r.getCheckIn() != null ? r.getCheckIn().toString() : "",
                r.getCheckOut() != null ? r.getCheckOut().toString() : "",
                r.getStatus() != null ? r.getStatus().name() : "",
                String.valueOf(r.getTotalAmount()));
    }

    private Reservation fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 7) return null;
        try {
            String id = p[0], guestId = p[1], roomNum = p[2];
            LocalDate checkIn = p[3].isEmpty() ? null : LocalDate.parse(p[3]);
            LocalDate checkOut = p[4].isEmpty() ? null : LocalDate.parse(p[4]);
            Reservation.Status status = p[5].isEmpty() ? Reservation.Status.PENDING
                    : Reservation.Status.valueOf(p[5]);
            double amount = parseDouble(p[6]);
            return new Reservation(id, guestId, roomNum, checkIn, checkOut, status, amount);
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
}
