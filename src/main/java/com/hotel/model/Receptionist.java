package com.hotel.model;

/**
 * Receptionist staff - limited permissions, cannot approve refunds.
 */
public class Receptionist extends Staff {

    public Receptionist() {}

    public Receptionist(String staffId, String name, String email, String password,
                        double salary, String shift) {
        super(staffId, name, email, password, "RECEPTIONIST", salary, shift);
    }

    @Override
    public String getPermissions() {
        return "GUEST_MANAGEMENT,RESERVATIONS,BILLING";
    }

    public boolean canApproveRefunds() {
        return false;
    }
}
