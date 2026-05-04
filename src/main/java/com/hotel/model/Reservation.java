package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Represents a hotel reservation.
 */
public class Reservation {

    public enum Status {
        PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }

    private String reservationId;
    private String guestId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Status status;
    private double totalAmount;

    public Reservation() {}

    public Reservation(String reservationId, String guestId, String roomNumber,
                       LocalDate checkIn, LocalDate checkOut, Status status, double totalAmount) {
        this.reservationId = reservationId;
        this.guestId = guestId;
        this.roomNumber = roomNumber;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.status = status;
        this.totalAmount = totalAmount;
    }

    /**
     * Calculates cancellation fee based on how far in advance the guest cancels.
     * >7 days: no fee; 1-7 days: 50%; <1 day: 100%
     */
    public double calculateCancellationFee() {
        if (checkIn == null) return 0;
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
        if (daysUntilCheckIn > 7) {
            return 0;
        } else if (daysUntilCheckIn >= 1) {
            return totalAmount * 0.50;
        } else {
            return totalAmount;
        }
    }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public String getGuestId() { return guestId; }
    public void setGuestId(String guestId) { this.guestId = guestId; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
}
