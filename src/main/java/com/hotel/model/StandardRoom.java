package com.hotel.model;

/**
 * Standard Room - price equals base price. Demonstrates inheritance.
 */
public class StandardRoom extends Room {

    public StandardRoom() {}

    public StandardRoom(String roomNumber, String type, double price, String amenities,
                        boolean available, int floor) {
        super(roomNumber, type, price, amenities, available, floor);
    }

    @Override
    public double calculatePrice() {
        return getPrice();
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Standard Room %s | Floor %d | $%.2f/night | %s",
                getRoomNumber(), getFloor(), calculatePrice(), getAmenities());
    }
}
