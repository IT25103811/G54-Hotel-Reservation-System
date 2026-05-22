package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Standard hotel reservation — the default concrete type.
 *
 * Cancellation policy (standard):
 *   > 7 days before check-in  → no fee
 *   1–7 days before check-in  → 50 % of total
 *   0 days (day of) or past   → 100 % of total (no refund)
 *
 * Maximum stay: 365 nights.
 */
public class Reservation extends AbstractReservation {

    // ── Constructors ──────────────────────────────────────────────────────────

    public Reservation() {
        super();
    }

    public Reservation(String reservationId, String guestId, String roomNumber,
                       LocalDate checkIn, LocalDate checkOut,
                       Status status, double totalAmount) {
        super(reservationId, guestId, roomNumber, checkIn, checkOut, status, totalAmount);
    }

    // ── Polymorphic overrides ─────────────────────────────────────────────────

    @Override
    public String getReservationType() {
        return "Standard";
    }

    /**
     * Standard cancellation policy:
     *   > 7 days  → 0 % fee
     *   1–7 days  → 50 % fee
     *   ≤ 0 days  → 100 % fee
     */
    @Override
    public double calculateCancellationFee() {
        if (getCheckIn() == null) return 0;
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), getCheckIn());
        if (daysUntilCheckIn > 7) return 0;
        if (daysUntilCheckIn >= 1) return getTotalAmount() * 0.50;
        return getTotalAmount();
    }

    @Override
    public long getMaxNights() {
        return 365;
    }
}
