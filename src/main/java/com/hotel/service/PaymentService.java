package com.hotel.service;

import com.hotel.dao.PaymentDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.dao.FileUtils;
import com.hotel.model.*;
import com.hotel.util.PaymentBST;
import com.hotel.util.PaymentSorter;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Payment business logic.
 *
 * Algorithms integrated:
 *  - Binary Search Tree (PaymentBST)  — O(log n) average lookup by paymentId,
 *    used in getPaymentById(), voidPayment(), completePendingPayment(),
 *    and deletePayment().  The BST is rebuilt lazily whenever the underlying
 *    file changes (save / update / delete) by calling invalidateCache().
 *
 *  - QuickSort (PaymentSorter)        — O(n log n) sorting of payment lists,
 *    used in getAllPayments(), getPaymentsByDateRange(), and getPaidPayments()
 *    so that results are returned in a deterministic, useful order.
 */
public class PaymentService {

    private final PaymentDAO paymentDAO;
    private final ReservationDAO reservationDAO;

    /**
     * In-memory BST cache.
     * Rebuilt on first use after any write operation (lazy invalidation).
     */
    private PaymentBST paymentBST;
    private boolean bstDirty = true; // start dirty so first call builds it

    public PaymentService() {
        this.paymentDAO   = new PaymentDAO();
        this.reservationDAO = new ReservationDAO();
        this.paymentBST   = new PaymentBST();
    }

    // ── BST cache management ──────────────────────────────────────────────────

    /**
     * Ensure the BST is up-to-date before any read operation.
     * Rebuilds from the DAO only when a write has occurred since the last build.
     */
    private void ensureBSTReady() {
        if (bstDirty) {
            paymentBST.buildFromList(paymentDAO.findAll());
            bstDirty = false;
        }
    }

    /** Mark the BST as stale after any write to the underlying file. */
    private void invalidateCache() {
        bstDirty = true;
    }

    // ── Read operations (BST + QuickSort) ────────────────────────────────────

    /**
     * Get all payments sorted by date (newest first) using QuickSort.
     */
    public List<Payment> getAllPayments() {
        List<Payment> payments = paymentDAO.findAll();
        return PaymentSorter.sort(payments, PaymentSorter.SortBy.DATE_DESC);
    }

    /**
     * Get all payments sorted by amount (ascending) using QuickSort.
     */
    public List<Payment> getAllPaymentsSortedByAmount() {
        List<Payment> payments = paymentDAO.findAll();
        return PaymentSorter.sort(payments, PaymentSorter.SortBy.AMOUNT_ASC);
    }

    /**
     * Get all payments sorted by status (alphabetical) using QuickSort.
     */
    public List<Payment> getAllPaymentsSortedByStatus() {
        List<Payment> payments = paymentDAO.findAll();
        return PaymentSorter.sort(payments, PaymentSorter.SortBy.STATUS);
    }

    /**
     * Get a specific payment by ID using the BST for O(log n) lookup.
     */
    public Payment getPaymentById(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }
        ensureBSTReady();
        return paymentBST.search(paymentId);
    }

    /**
     * Get all payments for a specific reservation.
     * Results are sorted by date (newest first) using QuickSort.
     */
    public List<Payment> getPaymentsByReservation(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }
        List<Payment> result = paymentDAO.findByReservationId(reservationId);
        return PaymentSorter.sort(result, PaymentSorter.SortBy.DATE_DESC);
    }

    /**
     * Get all paid payments sorted by amount (highest first) using QuickSort.
     */
    public List<Payment> getPaidPayments() {
        List<Payment> paid = paymentDAO.findAll().stream()
                .filter(p -> p.getStatus() == Payment.Status.PAID)
                .collect(Collectors.toList());
        return PaymentSorter.sort(paid, PaymentSorter.SortBy.AMOUNT_DESC);
    }

    /**
     * Get all pending payments sorted by date (oldest first) using QuickSort.
     */
    public List<Payment> getPendingPayments() {
        List<Payment> pending = paymentDAO.findAll().stream()
                .filter(p -> p.getStatus() == Payment.Status.PENDING)
                .collect(Collectors.toList());
        return PaymentSorter.sort(pending, PaymentSorter.SortBy.DATE_ASC);
    }

    /**
     * Get payment history for a specific date range sorted by date using QuickSort.
     */
    public List<Payment> getPaymentsByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start and end dates are required");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }
        List<Payment> filtered = paymentDAO.findAll().stream()
                .filter(p -> p.getTimestamp() != null
                        && !p.getTimestamp().toLocalDate().isBefore(startDate)
                        && !p.getTimestamp().toLocalDate().isAfter(endDate))
                .collect(Collectors.toList());
        return PaymentSorter.sort(filtered, PaymentSorter.SortBy.DATE_ASC);
    }

    // ── Write operations (invalidate BST cache after each write) ─────────────

    /**
     * CRITICAL: Process a payment for a reservation.
     *
     * Business Rules:
     * 1. Payment amount must exactly match reservation total_amount
     * 2. reservation_id must reference a valid reservation in reservations.txt
     * 3. Reservation cannot be checked-in unless payment is made
     * 4. Generate payment_id sequentially or with timestamp
     * 5. Maintain consistency: if payment is Paid, reservation can transition to checked-in
     */
    public Payment processPayment(String reservationId, double amount, String paymentMethod,
                                  String cardNumber, String cardHolder, String receivedBy) {
        // 1. Validate inputs
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }

        // 2. Verify reservation exists
        Reservation reservation = reservationDAO.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found: " + reservationId);
        }

        // 3. Validate amount
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than 0");
        }

        // 4. CRITICAL: Amount must match reservation total
        if (Math.abs(amount - reservation.getTotalAmount()) > 0.01) {
            throw new IllegalStateException(
                    "Payment amount (" + String.format("%.2f", amount) + ") does not match "
                            + "reservation total (" + String.format("%.2f", reservation.getTotalAmount()) + ")");
        }

        // 5. Validate payment method
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        // 6. Create payment record
        String paymentId = FileUtils.generateId("PAY");
        Payment payment;

        if ("CARD".equalsIgnoreCase(paymentMethod) || "CreditCard".equals(paymentMethod)) {
            if (cardNumber == null || cardNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Card number is required for card payments");
            }
            if (cardHolder == null || cardHolder.trim().isEmpty()) {
                throw new IllegalArgumentException("Card holder name is required for card payments");
            }
            payment = new CreditCardPayment(
                    paymentId, reservationId, amount,
                    Payment.Status.PAID,
                    LocalDateTime.now(),
                    cardNumber, cardHolder
            );
        } else if ("CASH".equalsIgnoreCase(paymentMethod)) {
            if (receivedBy == null || receivedBy.trim().isEmpty()) {
                throw new IllegalArgumentException("Staff member name is required for cash payments");
            }
            payment = new CashPayment(
                    paymentId, reservationId, amount,
                    Payment.Status.PAID,
                    LocalDateTime.now(),
                    receivedBy
            );
        } else {
            throw new IllegalArgumentException("Unsupported payment method: " + paymentMethod);
        }

        // 7. Process payment (simulated)
        payment.processPayment();

        // 8. Save payment to file and invalidate BST
        paymentDAO.save(payment);
        invalidateCache();

        // 9. Automatically update reservation status to allow check-in
        if (reservation.getStatus() == Reservation.Status.PENDING
                || reservation.getStatus() == Reservation.Status.CONFIRMED) {
            reservation.setStatus(Reservation.Status.CONFIRMED);
            reservationDAO.update(reservation);
        }

        return payment;
    }

    /**
     * Create a pending payment (for future processing).
     */
    public Payment createPendingPayment(String reservationId, String paymentMethod,
                                        String cardNumber, String cardHolder, String receivedBy) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }

        Reservation reservation = reservationDAO.findById(reservationId);
        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        String paymentId = FileUtils.generateId("PAY");
        Payment payment;

        if ("CARD".equalsIgnoreCase(paymentMethod)) {
            payment = new CreditCardPayment(
                    paymentId, reservationId, reservation.getTotalAmount(),
                    Payment.Status.PENDING,
                    LocalDateTime.now(),
                    cardNumber, cardHolder
            );
        } else {
            payment = new CashPayment(
                    paymentId, reservationId, reservation.getTotalAmount(),
                    Payment.Status.PENDING,
                    LocalDateTime.now(),
                    receivedBy
            );
        }

        paymentDAO.save(payment);
        invalidateCache();
        return payment;
    }

    /**
     * Complete a pending payment.
     * Uses BST for fast lookup of the payment to complete.
     */
    public void completePendingPayment(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        ensureBSTReady();
        Payment payment = paymentBST.search(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if (payment.getStatus() != Payment.Status.PENDING) {
            throw new IllegalStateException(
                    "Payment is not in PENDING status. Current status: " + payment.getStatus());
        }

        payment.setStatus(Payment.Status.PAID);
        paymentDAO.update(payment);
        invalidateCache();

        Reservation reservation = reservationDAO.findById(payment.getReservationId());
        if (reservation != null && (reservation.getStatus() == Reservation.Status.PENDING
                || reservation.getStatus() == Reservation.Status.CONFIRMED)) {
            reservation.setStatus(Reservation.Status.CONFIRMED);
            reservationDAO.update(reservation);
        }
    }

    /**
     * Void a payment.
     * Uses BST for fast lookup, then updates the file and invalidates cache.
     */
    public void voidPayment(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        ensureBSTReady();
        Payment payment = paymentBST.search(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found");
        }

        if (payment.getStatus() == Payment.Status.VOIDED) {
            throw new IllegalStateException("Payment is already voided");
        }

        payment.setStatus(Payment.Status.VOIDED);
        paymentDAO.update(payment);
        invalidateCache();
    }

    /**
     * Delete a payment record permanently.
     * Business Rule: Only VOIDED payments can be deleted.
     * Uses BST for fast lookup before deletion.
     */
    public void deletePayment(String paymentId) {
        if (paymentId == null || paymentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Payment ID cannot be empty");
        }

        ensureBSTReady();
        Payment payment = paymentBST.search(paymentId);
        if (payment == null) {
            throw new IllegalArgumentException("Payment not found: " + paymentId);
        }

        if (payment.getStatus() != Payment.Status.VOIDED) {
            throw new IllegalStateException(
                    "Only VOIDED payments can be deleted. "
                            + "Please void this payment first before deleting.");
        }

        paymentDAO.delete(paymentId);
        invalidateCache();
    }

    // ── Utility / aggregate methods ───────────────────────────────────────────

    /**
     * Check if a reservation has a paid payment.
     */
    public boolean isReservationPaid(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return false;
        }
        List<Payment> payments = paymentDAO.findByReservationId(reservationId);
        return payments.stream().anyMatch(p -> p.getStatus() == Payment.Status.PAID);
    }

    /**
     * Get total paid amount for a reservation.
     */
    public double getTotalPaidAmount(String reservationId) {
        if (reservationId == null || reservationId.trim().isEmpty()) {
            return 0;
        }
        return paymentDAO.findByReservationId(reservationId).stream()
                .filter(p -> p.getStatus() == Payment.Status.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /**
     * Calculate total revenue for a date range.
     */
    public double getTotalRevenue(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return getPaymentsByDateRange(startDate, endDate).stream()
                .filter(p -> p.getStatus() == Payment.Status.PAID)
                .mapToDouble(Payment::getAmount)
                .sum();
    }

    /**
     * Get payment count by status.
     */
    public long getPaymentCountByStatus(Payment.Status status) {
        if (status == null) {
            return 0;
        }
        return paymentDAO.findAll().stream()
                .filter(p -> p.getStatus() == status)
                .count();
    }
}
