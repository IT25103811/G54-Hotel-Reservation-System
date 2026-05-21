package com.hotel.dao;

import com.hotel.model.*;
import com.hotel.util.ReservationBST;
import com.hotel.util.ReservationSorter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class ReservationDAO extends ReservationRepository {

    private static final String FILE = "reservations.txt";

    // DSA: BST — reservationId by in-memory fast lookup
    private final ReservationBST bst = new ReservationBST();

    public ReservationDAO() {
        FileUtils.ensureFileExists(FILE);
        rebuildBST();
    }

    private void rebuildBST() {
        bst.clear();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null) bst.insert(r);
        }
    }

    // CRUD — @Override ========================================================

    @Override
    public void save(Reservation res) {
        if (res.getCreatedAt() == null || res.getCreatedAt().isEmpty()) {
            res.setCreatedAt(LocalDate.now().toString());
        }
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(res));
        FileUtils.writeLines(FILE, lines);
        bst.insert(res);
    }

    @Override
    public void update(Reservation res) {
        List<String> lines   = FileUtils.readLines(FILE);
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
        bst.update(res);
    }

    @Override
    public void delete(String id) {
        List<String> lines   = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Reservation r = fromLine(line);
            if (r == null || !r.getReservationId().equals(id)) updated.add(line);
        }
        FileUtils.writeLines(FILE, updated);
        bst.delete(id);
    }


    @Override
    public Reservation findById(String id) {
        if (id == null || id.trim().isEmpty()) return null;
        return bst.search(id);
    }


    @Override
    public List<Reservation> findAll() {
        return ReservationSorter.sort(readAllFromFile(), ReservationSorter.SortBy.CHECK_IN);
    }

    @Override
    public List<Reservation> findByGuestId(String guestId) {
        if (guestId == null) return Collections.emptyList();
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && guestId.equals(r.getGuestId())) result.add(r);
        }
        return ReservationSorter.sort(result, ReservationSorter.SortBy.CHECK_IN);
    }

    @Override
    public List<Reservation> findByRoomNumber(String roomNumber) {
        if (roomNumber == null) return Collections.emptyList();
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && roomNumber.equals(r.getRoomNumber())) result.add(r);
        }
        return ReservationSorter.sort(result, ReservationSorter.SortBy.CHECK_IN);
    }

    @Override
    public List<Reservation> findByStatus(Reservation.Status status) {
        List<Reservation> result = new ArrayList<>();
        for (Reservation r : readAllFromFile()) {
            if (r.getStatus() == status) result.add(r);
        }
        return ReservationSorter.sort(result, ReservationSorter.SortBy.CHECK_IN);
    }

    @Override
    public List<Reservation> findActive() {
        return readAllFromFile().stream()
                .filter(this::isModifiable)
                .collect(Collectors.toList());
    }

    // Validation — @Override ==================================================
    // isValidDateRange() / isModifiable() — abstract class ගෙන් inherit

    @Override
    public boolean checkDateOverlap(String roomNumber, LocalDate checkIn,
                                    LocalDate checkOut, String excludeId) {
        if (!isValidDateRange(checkIn, checkOut)) return false;
        for (Reservation r : findByRoomNumber(roomNumber)) {
            if (excludeId != null && excludeId.equals(r.getReservationId())) continue;
            if (!isModifiable(r)) continue;
            if (r.getCheckIn() == null || r.getCheckOut() == null) continue;
            if (r.getCheckIn().isBefore(checkOut) && r.getCheckOut().isAfter(checkIn)) return true;
        }
        return false;
    }

    @Override
    public boolean checkDuplicateGuestRoomBooking(String guestId, String roomNumber,
                                                  LocalDate checkIn, LocalDate checkOut,
                                                  String excludeId) {
        if (!isValidDateRange(checkIn, checkOut)) return false;
        for (Reservation r : findByGuestId(guestId)) {
            if (excludeId != null && excludeId.equals(r.getReservationId())) continue;
            if (!roomNumber.equals(r.getRoomNumber())) continue;
            if (!isModifiable(r)) continue;
            if (r.getCheckIn() == null || r.getCheckOut() == null) continue;
            if (r.getCheckIn().isBefore(checkOut) && r.getCheckOut().isAfter(checkIn)) return true;
        }
        return false;
    }

    @Override
    public long countActiveByGuest(String guestId) {
        return findByGuestId(guestId).stream()
                .filter(this::isModifiable)
                .count();
    }

    // Private helpers — file I/O ==============================================

    private List<Reservation> readAllFromFile() {
        List<Reservation> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null) result.add(r);
        }
        return result;
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
