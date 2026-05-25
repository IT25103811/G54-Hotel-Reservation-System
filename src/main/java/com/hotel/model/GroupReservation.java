package com.hotel.model;

import java.time.LocalDate;


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


    public void setGroupReference(String groupReference) {
        this.groupReference = groupReference == null ? "" : groupReference;
        super.setSpecialRequests("GROUP:" + this.groupReference);
    }


    public static boolean isGroupSpecialRequest(String s) {
        return s != null && s.startsWith("GROUP:");
    }


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

