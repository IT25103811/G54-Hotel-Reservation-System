package com.hotel.service;

import com.hotel.dao.RoomDAO;
import com.hotel.dao.ReservationDAO;
import com.hotel.model.*;
import com.hotel.util.RoomBST;
import com.hotel.util.RoomSorter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service layer for Room business logic.
 *
 * Algorithms integrated:
 *  - Binary Search Tree (RoomBST)  — O(log n) average lookup by roomNumber,
 *    used in getRoomByNumber(), updateRoom(), deleteRoom(), and
 *    updateRoomStatusByReservation().  The BST is rebuilt lazily whenever
 *    the underlying file changes (save / update / delete).
 *
 *  - QuickSort (RoomSorter)        — O(n log n) sorting of room lists,
 *    used in getAllRooms(), getAvailableRooms(), getRoomsByType(), and
 *    the search handler so results are always returned in a predictable order.
 */
public class RoomService {

    private final RoomDAO roomDAO;
    private final ReservationDAO reservationDAO;

    /**
     * In-memory BST cache.
     * Rebuilt on first use after any write operation (lazy invalidation).
     */
    private RoomBST roomBST;
    private boolean bstDirty = true; // start dirty so first call builds it

    public RoomService() {
        this.roomDAO          = new RoomDAO();
        this.reservationDAO   = new ReservationDAO();
        this.roomBST          = new RoomBST();
    }

    // ── BST cache management ──────────────────────────────────────────────────

    /**
     * Ensure the BST is up-to-date before any read operation.
     * Rebuilds from the DAO only when a write has occurred since the last build.
     */
    private void ensureBSTReady() {
        if (bstDirty) {
            roomBST.buildFromList(roomDAO.findAll());
            bstDirty = false;
        }
    }

    /** Mark the BST as stale after any write to the underlying file. */
    private void invalidateCache() {
        bstDirty = true;
    }

    // ── Read operations (BST + QuickSort) ────────────────────────────────────

    /**
     * Get all rooms sorted by room number using QuickSort.
     */
    public List<Room> getAllRooms() {
        List<Room> rooms = roomDAO.findAll();
        return RoomSorter.sort(rooms, RoomSorter.SortBy.ROOM_NUMBER);
    }

    /**
     * Get all rooms sorted by price (ascending) using QuickSort.
     */
    public List<Room> getAllRoomsSortedByPrice() {
        List<Room> rooms = roomDAO.findAll();
        return RoomSorter.sort(rooms, RoomSorter.SortBy.PRICE_ASC);
    }

    /**
     * Get all rooms sorted by floor using QuickSort.
     */
    public List<Room> getAllRoomsSortedByFloor() {
        List<Room> rooms = roomDAO.findAll();
        return RoomSorter.sort(rooms, RoomSorter.SortBy.FLOOR_ASC);
    }

    /**
     * Get a specific room by its number using the BST for O(log n) lookup.
     */
    public Room getRoomByNumber(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        ensureBSTReady();
        return roomBST.search(roomNumber);
    }

    /**
     * Get all available rooms sorted by price (ascending) using QuickSort.
     */
    public List<Room> getAvailableRooms() {
        List<Room> available = roomDAO.findAvailable();
        return RoomSorter.sort(available, RoomSorter.SortBy.PRICE_ASC);
    }

    /**
     * Get rooms by type, sorted by price ascending using QuickSort.
     */
    public List<Room> getRoomsByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Room type cannot be empty");
        }
        List<Room> byType = roomDAO.findByType(type);
        return RoomSorter.sort(byType, RoomSorter.SortBy.PRICE_ASC);
    }

    // ── Write operations (invalidate BST cache after each write) ─────────────

    /**
     * Add a new room to the system.
     * Validates that room number is unique using BST lookup.
     */
    public void addRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }
        if (room.getPrice() <= 0) {
            throw new IllegalArgumentException("Room price must be greater than 0");
        }

        // Use BST for fast duplicate check
        ensureBSTReady();
        if (roomBST.search(room.getRoomNumber()) != null) {
            throw new IllegalArgumentException("Room number " + room.getRoomNumber() + " already exists");
        }

        roomDAO.save(room);
        invalidateCache();
    }

    /**
     * Update an existing room.
     * Uses BST to verify existence, then persists and invalidates cache.
     */
    public void updateRoom(Room room) {
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null");
        }
        if (room.getRoomNumber() == null || room.getRoomNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }

        ensureBSTReady();
        Room existing = roomBST.search(room.getRoomNumber());
        if (existing == null) {
            throw new IllegalArgumentException("Room " + room.getRoomNumber() + " does not exist");
        }

        if (room.getPrice() <= 0) {
            throw new IllegalArgumentException("Room price must be greater than 0");
        }

        roomDAO.update(room);
        invalidateCache();
    }

    /**
     * Delete a room from the system.
     * BUSINESS RULE: Cannot delete a room if active reservations exist for it.
     * Uses BST for fast existence check before the deletion.
     */
    public void deleteRoom(String roomNumber) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }

        ensureBSTReady();
        Room existing = roomBST.search(roomNumber);
        if (existing == null) {
            throw new IllegalArgumentException("Room " + roomNumber + " does not exist");
        }

        // Check for active reservations (O(n) scan is unavoidable here)
        List<Reservation> activeReservations = reservationDAO.findByRoomNumber(roomNumber).stream()
                .filter(r -> r.getStatus() == Reservation.Status.PENDING
                        || r.getStatus() == Reservation.Status.CONFIRMED
                        || r.getStatus() == Reservation.Status.CHECKED_IN)
                .collect(Collectors.toList());

        if (!activeReservations.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot delete room " + roomNumber
                            + ". It has " + activeReservations.size() + " active reservation(s).");
        }

        roomDAO.delete(roomNumber);
        invalidateCache();
    }

    /**
     * Update room availability based on reservation status changes.
     *
     * BUSINESS RULE:
     *  - PENDING / CONFIRMED / CHECKED_IN  →  room unavailable
     *  - CANCELLED / CHECKED_OUT           →  room available (if no other active bookings)
     *
     * Uses BST for fast room lookup.
     */
    public void updateRoomStatusByReservation(String roomNumber, Reservation.Status reservationStatus) {
        if (roomNumber == null || roomNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Room number cannot be empty");
        }

        ensureBSTReady();
        Room room = roomBST.search(roomNumber);
        if (room == null) {
            return; // Room doesn't exist, nothing to update
        }

        boolean isActive = reservationStatus == Reservation.Status.PENDING
                || reservationStatus == Reservation.Status.CONFIRMED
                || reservationStatus == Reservation.Status.CHECKED_IN;

        if (isActive) {
            room.setAvailable(false);
        } else {
            List<Reservation> activeReservations = reservationDAO.findByRoomNumber(roomNumber).stream()
                    .filter(r -> r.getStatus() == Reservation.Status.PENDING
                            || r.getStatus() == Reservation.Status.CONFIRMED
                            || r.getStatus() == Reservation.Status.CHECKED_IN)
                    .collect(Collectors.toList());
            room.setAvailable(activeReservations.isEmpty());
        }

        roomDAO.update(room);
        invalidateCache();
    }

    // ── Aggregate / utility methods ───────────────────────────────────────────

    /**
     * Get room count by availability.
     */
    public long getAvailableRoomCount() {
        return roomDAO.findAll().stream()
                .filter(Room::isAvailable)
                .count();
    }

    /**
     * Get room count by type.
     */
    public long getRoomCountByType(String type) {
        if (type == null || type.trim().isEmpty()) {
            return 0;
        }
        return roomDAO.findAll().stream()
                .filter(r -> type.equalsIgnoreCase(r.getType()))
                .count();
    }
}
