package com.hotel.model;

/**
 * Regular Guest - gets 5% discount. Demonstrates inheritance.
 */
public class RegularGuest extends Guest {

    public RegularGuest() {}

    public RegularGuest(String id, String name, String email, String phone, String password, int loyaltyPoints) {
        super(id, name, email, phone, password, loyaltyPoints);
    }

    @Override
    public double calculateDiscount() {
        return 0.05; // 5% discount for regular guests
    }

    @Override
    public String toString() {
        return "RegularGuest{id='" + getId() + "', name='" + getName() + "'}";
    }
}
