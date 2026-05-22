package com.hotel.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Abstract base class for all Reservation types.
 *
 * Inheritance Hierarchy:
 *   AbstractReservation  (abstract — core fields, template methods)
 *       └── Reservation  (concrete — standard hotel reservation)
 *               ├── LongStayReservation   (concrete — stays > 7 nights, 10 % discount on fee)
 *               └── GroupReservation      (concrete — group bookings with group reference)
 *
 * Polymorphic contract (abstract methods every subclass must implement):
 *   - getReservationType()       → human-readable type label
 *   - calculateCancellationFee() → type-specific cancellation policy
 *   - getMaxNights()             → maximum allowed stay length
 */
public abstract class AbstractReservation {

    // ── Status enum (shared by all subtypes) ─────────────────────────────────
    public enum Status {
        PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED
    }

    // ── Core fields ───────────────────────────────────────────────────────────
    private String reservationId;
    private String guestId;
    private String roomNumber;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private Status status;
    private double totalAmount;
    private String specialRequests;
    private String createdAt;

    // ── Constructors ──────────────────────────────────────────────────────────

    protected AbstractReservation() {}

    protected AbstractReservation(String reservationId, String guestId, String roomNumber,
                                  LocalDate checkIn, LocalDate checkOut,
                                  Status status, double totalAmount) {
        this.reservationId   = reservationId;
        this.guestId         = guestId;
        this.roomNumber      = roomNumber;
        this.checkIn         = checkIn;
        this.checkOut        = checkOut;
        this.status          = status;
        this.totalAmount     = totalAmount;
        this.specialRequests = "";
    }

    // ── Abstract methods — subclasses MUST override ───────────────────────────

    /**
     * Returns a human-readable label for this reservation type.
     * e.g. "Standard", "Long Stay", "Group"
     */
    public abstract String getReservationType();

    /**
     * Calculates the cancellation fee according to the type-specific policy.
     * Each subclass defines its own rules.
     */
    public abstract double calculateCancellationFee();

    /**
     * Returns the maximum number of nights allowed for this reservation type.
     */
    public abstract long getMaxNights();

    // ── Concrete shared methods ───────────────────────────────────────────────

    /**
     * Returns the number of booked nights (always >= 0).
     */
    public long getNights() {
        if (checkIn == null || checkOut == null) return 0;
        return Math.max(ChronoUnit.DAYS.between(checkIn, checkOut), 0);
    }

    /**
     * Returns true when the reservation can still be modified or cancelled.
     * Shared rule: CANCELLED and CHECKED_OUT reservations are immutable.
     */
    public boolean isModifiable() {
        return status != Status.CANCELLED && status != Status.CHECKED_OUT;
    }

    /**
     * Returns true when the supplied date range is logically valid.
     */
    protected boolean isValidDateRange(LocalDate ci, LocalDate co) {
        if (ci == null || co == null) return false;
        return co.isAfter(ci);
    }

    // ── Getters / Setters ─────────────────────────────────────────────────────

    public String getReservationId()                          { return reservationId; }
    public void   setReservationId(String reservationId)      { this.reservationId = reservationId; }

    public String getGuestId()                                { return guestId; }
    public void   setGuestId(String guestId)                  { this.guestId = guestId; }

    public String getRoomNumber()                             { return roomNumber; }
    public void   setRoomNumber(String roomNumber)            { this.roomNumber = roomNumber; }

    public LocalDate getCheckIn()                             { return checkIn; }
    public void      setCheckIn(LocalDate checkIn)            { this.checkIn = checkIn; }

    public LocalDate getCheckOut()                            { return checkOut; }
    public void      setCheckOut(LocalDate checkOut)          { this.checkOut = checkOut; }

    public Status getStatus()                                 { return status; }
    public void   setStatus(Status status)                    { this.status = status; }

    public double getTotalAmount()                            { return totalAmount; }
    public void   setTotalAmount(double totalAmount)          { this.totalAmount = totalAmount; }

    public String getSpecialRequests() {
        return specialRequests == null ? "" : specialRequests;
    }
    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getCreatedAt() {
        return createdAt == null ? "" : createdAt;
    }
    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
