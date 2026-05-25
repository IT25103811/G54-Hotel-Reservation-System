package com.hotel.util;

import com.hotel.model.Payment;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree for Payment objects.
 *
 * Keyed on paymentId (String, lexicographic order).
 * Provides O(log n) average-case search, insert, and delete —
 * significantly faster than the O(n) linear scan previously used
 * in PaymentDAO / PaymentService when the payment list is large.
 *
 * Usage pattern in PaymentService:
 *   1. On start-up (or after a bulk load), call buildFromList() once.
 *   2. Use search() for fast lookups.
 *   3. Keep the BST in sync via insert() / update() / delete() on every
 *      write that goes through PaymentDAO.
 */
public class PaymentBST {

    // ── Inner node ────────────────────────────────────────────────────────────
    private static class Node {
        Payment data;
        Node left, right;
        Node(Payment data) { this.data = data; }
    }

    private Node root;

    public PaymentBST() { this.root = null; }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Build (or rebuild) the BST from a flat list of payments.
     * Call this after a bulk load from file.
     */
    public void buildFromList(List<Payment> payments) {
        root = null;
        if (payments == null) return;
        for (Payment p : payments) {
            insert(p);
        }
    }

    /**
     * Insert a payment into the BST.
     * If a payment with the same paymentId already exists, it is updated.
     */
    public void insert(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) return;
        root = insertRec(root, payment);
    }

    /**
     * Search for a payment by its paymentId.
     *
     * @param paymentId the ID to look up
     * @return the matching Payment, or {@code null} if not found
     */
    public Payment search(String paymentId) {
        if (paymentId == null) return null;
        Node node = searchRec(root, paymentId);
        return node != null ? node.data : null;
    }

    /**
     * Update an existing payment in-place (same paymentId, new data).
     * No-op if the payment is not present.
     */
    public void update(Payment payment) {
        if (payment == null || payment.getPaymentId() == null) return;
        Node node = searchRec(root, payment.getPaymentId());
        if (node != null) node.data = payment;
    }

    /**
     * Remove a payment from the BST by its paymentId.
     */
    public void delete(String paymentId) {
        if (paymentId == null) return;
        root = deleteRec(root, paymentId);
    }

    /** Remove all entries. */
    public void clear() { root = null; }

    /** @return {@code true} if the BST contains no payments */
    public boolean isEmpty() { return root == null; }

    /**
     * Return all payments in ascending paymentId order (in-order traversal).
     */
    public List<Payment> inOrderList() {
        List<Payment> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    // ── Private recursive helpers ─────────────────────────────────────────────

    private Node insertRec(Node node, Payment payment) {
        if (node == null) return new Node(payment);
        int cmp = payment.getPaymentId().compareTo(node.data.getPaymentId());
        if      (cmp < 0) node.left  = insertRec(node.left,  payment);
        else if (cmp > 0) node.right = insertRec(node.right, payment);
        else              node.data  = payment; // duplicate → update in-place
        return node;
    }

    private Node searchRec(Node node, String id) {
        if (node == null) return null;
        int cmp = id.compareTo(node.data.getPaymentId());
        if (cmp == 0) return node;
        return cmp < 0 ? searchRec(node.left, id) : searchRec(node.right, id);
    }

    private Node deleteRec(Node node, String id) {
        if (node == null) return null;
        int cmp = id.compareTo(node.data.getPaymentId());
        if (cmp < 0) {
            node.left  = deleteRec(node.left,  id);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, id);
        } else {
            // Case 1: leaf node
            if (node.left == null && node.right == null) return null;
            // Case 2: one child
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;
            // Case 3: two children — replace with in-order successor
            Node successor = findMin(node.right);
            node.data  = successor.data;
            node.right = deleteRec(node.right, successor.data.getPaymentId());
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private void inOrderRec(Node node, List<Payment> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.data);
        inOrderRec(node.right, result);
    }
}
