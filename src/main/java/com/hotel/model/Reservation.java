package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


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
