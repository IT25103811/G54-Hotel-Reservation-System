package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Abstract base class for payments.
 */
public abstract class Payment {

    public enum Status {
        PENDING, PAID, VOIDED
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
