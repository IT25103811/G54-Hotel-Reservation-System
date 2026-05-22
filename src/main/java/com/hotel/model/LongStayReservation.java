package com.hotel.model;

import java.time.LocalDate;

/**
 * Long-stay reservations (> 7 nights).
 * Policy: apply a small discount to cancellation fee (10% discount on fee).
 */
public class LongStayReservation extends Reservation {

    public LongStayReservation() {
        super();
    }

    public LongStayReservation(String reservationId, String guestId, String roomNumber,
                               LocalDate checkIn, LocalDate checkOut,
                               Status status, double totalAmount) {
        super(reservationId, guestId, roomNumber, checkIn, checkOut, status, totalAmount);
    }

    @Override
    public String getReservationType() {
        return "Long Stay";
    }

    @Override
    public double calculateCancellationFee() {
        // Apply the standard reservation cancellation policy, then give a 10% discount on the fee.
        double fee = super.calculateCancellationFee();
        return fee * 0.90;
    }

    @Override
    public long getMaxNights() {
        return 365;
    }
}

