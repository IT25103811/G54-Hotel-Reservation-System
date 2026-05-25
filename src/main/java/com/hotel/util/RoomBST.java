package com.hotel.util;

import com.hotel.model.Room;
import java.util.ArrayList;
import java.util.List;

/**
 * Binary Search Tree for {@link Room} objects.
 *
 * Keyed on roomNumber (String, lexicographic order).
 * Provides O(log n) average-case search, insert, and delete —
 * far faster than the O(n) linear scan in RoomDAO.findByNumber()
 * when the hotel has many rooms.
 *
 * Usage pattern in RoomService:
 *   1. On start-up (or after a bulk load), call buildFromList() once.
 *   2. Use search() instead of iterating the full list.
 *   3. Keep the BST in sync via insert() / update() / delete() on every
 *      write that goes through RoomDAO.
 */
public class RoomBST {

    // ── Inner node ────────────────────────────────────────────────────────────
    private static class Node {
        Room data;
        Node left, right;
        Node(Room data) { this.data = data; }
    }

    private Node root;

    public RoomBST() { this.root = null; }

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Build (or rebuild) the BST from a flat list of rooms.
     * Call this after a bulk load from file.
     */
    public void buildFromList(List<Room> rooms) {
        root = null;
        if (rooms == null) return;
        for (Room r : rooms) {
            insert(r);
        }
    }

    /**
     * Insert a room into the BST.
     * If a room with the same roomNumber already exists, it is updated.
     */
    public void insert(Room room) {
        if (room == null || room.getRoomNumber() == null) return;
        root = insertRec(root, room);
    }

    /**
     * Search for a room by its room number.
     *
     * @param roomNumber the room number to look up
     * @return the matching Room, or {@code null} if not found
     */
    public Room search(String roomNumber) {
        if (roomNumber == null) return null;
        Node node = searchRec(root, roomNumber);
        return node != null ? node.data : null;
    }

    /**
     * Update an existing room in-place (same roomNumber, new data).
     * No-op if the room is not present.
     */
    public void update(Room room) {
        if (room == null || room.getRoomNumber() == null) return;
        Node node = searchRec(root, room.getRoomNumber());
        if (node != null) node.data = room;
    }

    /**
     * Remove a room from the BST by its room number.
     */
    public void delete(String roomNumber) {
        if (roomNumber == null) return;
        root = deleteRec(root, roomNumber);
    }

    /** Remove all entries. */
    public void clear() { root = null; }

    /** @return {@code true} if the BST contains no rooms */
    public boolean isEmpty() { return root == null; }

    /**
     * Return all rooms in ascending roomNumber order (in-order traversal).
     */
    public List<Room> inOrderList() {
        List<Room> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    // ── Private recursive helpers ─────────────────────────────────────────────

    private Node insertRec(Node node, Room room) {
        if (node == null) return new Node(room);
        int cmp = room.getRoomNumber().compareTo(node.data.getRoomNumber());
        if      (cmp < 0) node.left  = insertRec(node.left,  room);
        else if (cmp > 0) node.right = insertRec(node.right, room);
        else              node.data  = room; // duplicate → update in-place
        return node;
    }

    private Node searchRec(Node node, String roomNumber) {
        if (node == null) return null;
        int cmp = roomNumber.compareTo(node.data.getRoomNumber());
        if (cmp == 0) return node;
        return cmp < 0 ? searchRec(node.left, roomNumber) : searchRec(node.right, roomNumber);
    }

    private Node deleteRec(Node node, String roomNumber) {
        if (node == null) return null;
        int cmp = roomNumber.compareTo(node.data.getRoomNumber());
        if (cmp < 0) {
            node.left  = deleteRec(node.left,  roomNumber);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, roomNumber);
        } else {
            // Case 1: leaf node
            if (node.left == null && node.right == null) return null;
            // Case 2: one child
            if (node.left  == null) return node.right;
            if (node.right == null) return node.left;
            // Case 3: two children — replace with in-order successor
            Node successor = findMin(node.right);
            node.data  = successor.data;
            node.right = deleteRec(node.right, successor.data.getRoomNumber());
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) node = node.left;
        return node;
    }

    private void inOrderRec(Node node, List<Room> result) {
        if (node == null) return;
        inOrderRec(node.left, result);
        result.add(node.data);
        inOrderRec(node.right, result);
    }
}
