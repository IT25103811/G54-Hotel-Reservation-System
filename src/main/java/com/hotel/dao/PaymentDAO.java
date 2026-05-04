package com.hotel.dao;

import com.hotel.model.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * DAO for Payment data.
 * Format: paymentId|reservationId|amount|status|timestamp|paymentType|cardLast4|cardHolder|receivedBy
 */
public class PaymentDAO {
    private static final String FILE = "payments.txt";

    public PaymentDAO() {
        FileUtils.ensureFileExists(FILE);
    }

    public void save(Payment payment) {
        List<String> lines = FileUtils.readLines(FILE);
        lines.add(toLine(payment));
        FileUtils.writeLines(FILE, lines);
    }

    public Payment findById(String id) {
        for (String line : FileUtils.readLines(FILE)) {
            Payment p = fromLine(line);
            if (p != null && id.equals(p.getPaymentId())) return p;
        }
        return null;
    }

    public List<Payment> findByReservationId(String reservationId) {
        List<Payment> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Payment p = fromLine(line);
            if (p != null && reservationId.equals(p.getReservationId())) result.add(p);
        }
        return result;
    }

    public List<Payment> findAll() {
        List<Payment> result = new ArrayList<>();
        for (String line : FileUtils.readLines(FILE)) {
            Payment p = fromLine(line);
            if (p != null) result.add(p);
        }
        return result;
    }

    public void update(Payment payment) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Payment p = fromLine(line);
            if (p != null && p.getPaymentId().equals(payment.getPaymentId())) {
                updated.add(toLine(payment));
            } else {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    public void delete(String id) {
        List<String> lines = FileUtils.readLines(FILE);
        List<String> updated = new ArrayList<>();
        for (String line : lines) {
            Payment p = fromLine(line);
            if (p != null && !p.getPaymentId().equals(id)) {
                updated.add(line);
            }
        }
        FileUtils.writeLines(FILE, updated);
    }

    private String toLine(Payment p) {
        String type = p.getPaymentType();
        String card = "", holder = "", received = "";
        if (p instanceof CreditCardPayment) {
            card = safe(((CreditCardPayment) p).getCardNumber());
            holder = safe(((CreditCardPayment) p).getCardHolder());
        } else if (p instanceof CashPayment) {
            received = safe(((CashPayment) p).getReceivedBy());
        }
        return String.join("|",
                safe(p.getPaymentId()), safe(p.getReservationId()),
                String.valueOf(p.getAmount()),
                p.getStatus() != null ? p.getStatus().name() : "",
                p.getTimestamp() != null ? p.getTimestamp().toString() : "",
                safe(type), card, holder, received);
    }

    private Payment fromLine(String line) {
        if (line == null || line.trim().isEmpty()) return null;
        String[] p = line.split("\\|", -1);
        if (p.length < 9) return null;
        try {
            String id = p[0], resId = p[1];
            double amount = parseDouble(p[2]);
            Payment.Status status = p[3].isEmpty() ? Payment.Status.PENDING : Payment.Status.valueOf(p[3]);
            LocalDateTime ts = p[4].isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(p[4]);
            String type = p[5], card = p[6], holder = p[7], received = p[8];
            if ("Credit Card".equals(type)) {
                return new CreditCardPayment(id, resId, amount, status, ts, card, holder);
            } else {
                return new CashPayment(id, resId, amount, status, ts, received);
            }
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String s) { return s == null ? "" : s.replace("|", ""); }
    private double parseDouble(String s) { try { return Double.parseDouble(s.trim()); } catch (Exception e) { return 0; } }
}
