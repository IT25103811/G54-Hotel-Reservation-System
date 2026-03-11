package com.hotel.model;

/**
 * Abstract base class for all hotel guests.
 * Demonstrates encapsulation and abstraction.
 */
public abstract class Guest {
    private String id;
    private String name;
    private String email;
    private String phone;
    private String password; // NOTE: Plain text for educational purposes only
    private int loyaltyPoints;

    public Guest() {}

    public Guest(String id, String name, String email, String phone, String password, int loyaltyPoints) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.loyaltyPoints = loyaltyPoints;
    }

    // Abstract method - polymorphism
    public abstract double calculateDiscount();

    // Getters and setters - encapsulation
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getGuestType() { return this.getClass().getSimpleName(); }

    @Override
    public String toString() {
        return "Guest{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
}
