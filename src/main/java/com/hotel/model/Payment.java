package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for payments.
 */
public abstract class Payment {

    public enum Status {
        PENDING, PAID, VOIDED
    }

    private String paymentId;
    private String reservationId;
    private double amount;
    private Status status;
    private LocalDateTime timestamp;

    public Payment() {}

    public Payment(String paymentId, String reservationId, double amount,
                   Status status, LocalDateTime timestamp) {
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.amount = amount;
        this.status = status;
        this.timestamp = timestamp;
    }

    public abstract boolean processPayment();
    public abstract String getPaymentType();

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getReservationId() { return reservationId; }
    public void setReservationId(String reservationId) { this.reservationId = reservationId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
