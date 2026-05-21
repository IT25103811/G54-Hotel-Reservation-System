package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Cash payment processed by a staff member.
 */
public class CashPayment extends Payment {
    private String receivedBy; // Staff name who received the payment

    public CashPayment() {}

    public CashPayment(String paymentId, String reservationId, double amount,
                       Status status, LocalDateTime timestamp, String receivedBy) {
        super(paymentId, reservationId, amount, status, timestamp);
        this.receivedBy = receivedBy;
    }

    @Override
    public boolean processPayment() {
        return true;
    }

    @Override
    public String getPaymentType() {
        return "Cash";
    }

    public String getReceivedBy() { return receivedBy; }
    public void setReceivedBy(String receivedBy) { this.receivedBy = receivedBy; }
}
