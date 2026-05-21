package com.hotel.model;

/**
 * Manager staff - has all permissions and can approve refunds.
 */
public class Manager extends Staff {

    public Manager() {}

    public Manager(String staffId, String name, String email, String password,
                   double salary, String shift) {
        super(staffId, name, email, password, "MANAGER", salary, shift);
    }

    @Override
    public String getPermissions() {
        return "ALL_PERMISSIONS";
    }

    public boolean canApproveRefunds() {
        return true;
    }
}
