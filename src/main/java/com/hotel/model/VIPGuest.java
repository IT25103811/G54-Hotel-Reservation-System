package com.hotel.model;

/**
 * VIP Guest - gets 20% discount. Demonstrates inheritance.
 */
public class VIPGuest extends Guest {
    private String membershipTier;

    public VIPGuest() {}

    public VIPGuest(String id, String name, String email, String phone, String password,
                    int loyaltyPoints, String membershipTier) {
        super(id, name, email, phone, password, loyaltyPoints);
        this.membershipTier = membershipTier;
    }

    @Override
    public double calculateDiscount() {
        return 0.20; // 20% discount for VIP guests
    }

    public String getMembershipTier() { return membershipTier; }
    public void setMembershipTier(String membershipTier) { this.membershipTier = membershipTier; }

    @Override
    public String toString() {
        return "VIPGuest{id='" + getId() + "', name='" + getName() + "', tier='" + membershipTier + "'}";
    }
}
