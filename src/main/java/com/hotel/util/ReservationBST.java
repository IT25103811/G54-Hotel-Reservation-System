package com.hotel.util;

import com.hotel.model.Reservation;
import java.util.ArrayList;
import java.util.List;


public class ReservationBST {

    // ── Private inner class — Encapsulation ──────────────────────────────────
    private static class Node {
        Reservation data;
        Node left, right;
        Node(Reservation data) { this.data = data; }
    }

    private Node root;

    public ReservationBST() { this.root = null; }

    // ── Public API ────────────────────────────────────────────────────────────


    public void insert(Reservation res) {
        if (res == null || res.getReservationId() == null) return;
        root = insertRec(root, res);
    }


    public Reservation search(String reservationId) {
        if (reservationId == null) return null;
        Node node = searchRec(root, reservationId);
        return node != null ? node.data : null;
    }


    public void update(Reservation res) {
        if (res == null || res.getReservationId() == null) return;
        Node node = searchRec(root, res.getReservationId());
        if (node != null) node.data = res;
    }


    public void delete(String reservationId) {
        if (reservationId == null) return;
        root = deleteRec(root, reservationId);
    }


    public void clear() { root = null; }


    public List<Reservation> inOrderList() {
        List<Reservation> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    public boolean isEmpty() { return root == null; }

    // ── Private recursive methods ─────────────────────────────────────────────

    private Node insertRec(Node node, Reservation res) {
        if (node == null) return new Node(res);
        int cmp = res.getReservationId().compareTo(node.data.getReservationId());
        if      (cmp < 0) node.left  = insertRec(node.left, res);
        else if (cmp > 0) node.right = insertRec(node.right, res);
        else              node.data  = res; // duplicate → update
        return node;
    }

    private Node searchRec(Node node, String id) {
        if (node == null) return null;
        if (id.equals(node.data.getReservationId())) return node;
        int cmp = id.compareTo(node.data.getReservationId());
        return cmp < 0 ? searchRec(node.left, id) : searchRec(node.right, id);
    }

    private Node deleteRec(Node node, String id) {
        if (node == null) return null;
        int cmp = id.compareTo(node.data.getReservationId());
        if (cmp < 0) {
            node.left  = deleteRec(node.left, id);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, id);
        } else {
            // Case 1: leaf
            if (node.left == null && node.right == null) return null;
            // Case 2: one child
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;
            // Case 3: two children — in-order successor
            Node successor = findMin(node.right);
            node.data  = successor.data;
            node.right = deleteRec(node.right, successor.data.getReservationId());
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private void inOrderRec(Node node, List<Reservation> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.data);
        inOrderRec(node.right, result);
    }
}
