package com.hotel.model;

import java.time.LocalDateTime;

/**
 * Credit card payment. Stores only last 4 digits for security.
 */
public class CreditCardPayment extends Payment {
    private String cardNumber; // Last 4 digits only
    private String cardHolder;

    public CreditCardPayment() {}

    public CreditCardPayment(String paymentId, String reservationId, double amount,
                             Status status, LocalDateTime timestamp,
                             String cardNumber, String cardHolder) {
        super(paymentId, reservationId, amount, status, timestamp);
        this.cardNumber = cardNumber;
        this.cardHolder = cardHolder;
    }

    @Override
    public boolean processPayment() {
        // Simulated credit card processing
        return true;
    }

    @Override
    public String getPaymentType() {
        return "Credit Card";
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
}
