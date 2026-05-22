package com.hotel.model;

import java.time.LocalDate;

/**
 * Group bookings: stores a group reference and encodes/decodes it via a
 * specialRequests prefix "GROUP:<ref>".
 */
public class GroupReservation extends Reservation {

    private String groupReference = "";

    public GroupReservation() {
        super();
    }

    public GroupReservation(String reservationId, String guestId, String roomNumber,
                            LocalDate checkIn, LocalDate checkOut,
                            Status status, double totalAmount, String groupReference) {
        super(reservationId, guestId, roomNumber, checkIn, checkOut, status, totalAmount);
        setGroupReference(groupReference);
    }

    @Override
    public String getReservationType() {
        return "Group";
    }

    public String getGroupReference() {
        return groupReference == null ? "" : groupReference;
    }

    /**
     * Sets the group reference and also ensures specialRequests is set with the
     * "GROUP:" prefix so the DAO's serialization/deserialization stays compatible.
     */
    public void setGroupReference(String groupReference) {
        this.groupReference = groupReference == null ? "" : groupReference;
        super.setSpecialRequests("GROUP:" + this.groupReference);
    }

    /**
     * Helper used by ReservationDAO.fromLine(...) to detect group-special-requests.
     */
    public static boolean isGroupSpecialRequest(String s) {
        return s != null && s.startsWith("GROUP:");
    }

    /**
     * Extracts the group reference string from a specialRequests value that begins with "GROUP:".
     */
    public static String extractGroupReference(String s) {
        if (!isGroupSpecialRequest(s)) return "";
        return s.substring("GROUP:".length());
    }

    @Override
    public double calculateCancellationFee() {
        // Keep default behaviour for now (same as standard). Modify if group-specific rules are required.
        return super.calculateCancellationFee();
    }

    @Override
    public long getMaxNights() {
        return 365;
    }
}

