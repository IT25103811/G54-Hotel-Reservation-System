package com.hotel.dao;

import com.hotel.model.*;
import com.hotel.util.ReservationBST;
import com.hotel.util.ReservationSorter;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

// ── Package-private abstract base ──────────────────────────────────────────────
// Replaces the old IReservationRepository interface + ReservationRepository
// abstract class. Shared validation helpers live here; all CRUD operations are
// declared abstract and implemented by ReservationDAO below.
abstract class AbstractReservationDAO {

    // ── Shared validation helpers (inherited by ReservationDAO) ──────────────

    protected boolean isValidDateRange(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) return false;
        return checkOut.isAfter(checkIn);
    }

    protected boolean isModifiable(Reservation res) {
        if (res == null) return false;
        return res.getStatus() != Reservation.Status.CANCELLED
                && res.getStatus() != Reservation.Status.CHECKED_OUT;
    }

    // ── Abstract CRUD contract ────────────────────────────────────────────────

    public abstract void save(Reservation res);
    public abstract void update(Reservation res);
    public abstract void delete(String id);
    public abstract Reservation findById(String id);
    public abstract List<Reservation> findAll();
    public abstract List<Reservation> findByGuestId(String guestId);
    public abstract List<Reservation> findByRoomNumber(String roomNumber);
    public abstract List<Reservation> findByStatus(Reservation.Status status);
    public abstract List<Reservation> findActive();
    public abstract boolean checkDateOverlap(String roomNumber, LocalDate checkIn,
                                             LocalDate checkOut, String excludeId);
    public abstract boolean checkDuplicateGuestRoomBooking(String guestId, String roomNumber,
                                                           LocalDate checkIn, LocalDate checkOut,
                                                           String excludeId);
    public abstract long countActiveByGuest(String guestId);
}


/**
 * Concrete file-backed DAO for Reservation objects.
 *
 * Extends AbstractReservationDAO (package-private) which carries shared
 * validation helpers. The old IReservationRepository interface and the
 * separate ReservationRepository abstract class have been removed; their
 * responsibilities are now consolidated here.
 *
 * Polymorphism: fromLine() inspects the persisted data and returns the most
 * specific subtype (LongStayReservation, GroupReservation, or Reservation),
 * so callers automatically get the correct calculateCancellationFee() etc.
 */
public class ReservationDAO extends AbstractReservationDAO {

    private static final String FILE = "reservations.txt";

    // DSA: BST — reservationId fast in-memory lookup
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

    // ── CRUD — @Override ──────────────────────────────────────────────────────

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
        // Always read from file — authoritative source of truth.
        // The BST is per-instance and can be stale when multiple service classes
        // each hold their own ReservationDAO.  Reading from file ensures that a
        // reservation saved by one DAO is always visible to another.
        for (String line : FileUtils.readLines(FILE)) {
            Reservation r = fromLine(line);
            if (r != null && id.equals(r.getReservationId())) return r;
        }
        return null;
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

    // ── Validation — @Override ────────────────────────────────────────────────
    // isValidDateRange() / isModifiable() inherited from AbstractReservationDAO

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

    // ── Private helpers — file I/O ────────────────────────────────────────────

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

    /**
     * Deserialises one file line into the most specific Reservation subtype.
     *
     * Polymorphic dispatch rules:
     *   1. specialRequests starts with "GROUP:" → GroupReservation
     *   2. nights > 7                           → LongStayReservation
     *   3. otherwise                            → Reservation  (standard)
     */
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

            // ── Polymorphic subtype selection ─────────────────────────────────
            Reservation r;

            if (GroupReservation.isGroupSpecialRequest(specialRequests)) {
                // Group reservation — decode group reference from the prefix
                GroupReservation gr = new GroupReservation();
                gr.setReservationId(p[0]);
                gr.setGuestId(p[1]);
                gr.setRoomNumber(p[2]);
                gr.setCheckIn(checkIn);
                gr.setCheckOut(checkOut);
                gr.setStatus(status);
                gr.setTotalAmount(amount);
                // setGroupReference also updates specialRequests via the prefix
                gr.setGroupReference(GroupReservation.extractGroupReference(specialRequests));
                gr.setCreatedAt(createdAt);
                r = gr;

            } else if (checkIn != null && checkOut != null
                    && java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut) > 7) {
                // Long-stay reservation
                r = new LongStayReservation(p[0], p[1], p[2], checkIn, checkOut, status, amount);
                r.setSpecialRequests(specialRequests);
                r.setCreatedAt(createdAt);

            } else {
                // Standard reservation
                r = new Reservation(p[0], p[1], p[2], checkIn, checkOut, status, amount);
                r.setSpecialRequests(specialRequests);
                r.setCreatedAt(createdAt);
            }

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
