package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


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
    private String specialRequests;  // NEW – optional, never null in serialisation
    private String createdAt;        // NEW – ISO date string, set on first save

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
        this.specialRequests = "";
    }


    public long getNights() {
        if (checkIn == null || checkOut == null) return 0;
        long n = ChronoUnit.DAYS.between(checkIn, checkOut);
        return Math.max(n, 0);
    }


    public double calculateCancellationFee() {
        if (checkIn == null) return 0;
        long daysUntilCheckIn = ChronoUnit.DAYS.between(LocalDate.now(), checkIn);
        if (daysUntilCheckIn > 7) return 0;
        if (daysUntilCheckIn >= 1) return totalAmount * 0.50;
        return totalAmount;
    }

    // ── Getters / Setters ────────────────────────────────────────────────────

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

    public String getSpecialRequests() { return specialRequests == null ? "" : specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public String getCreatedAt() { return createdAt == null ? "" : createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
