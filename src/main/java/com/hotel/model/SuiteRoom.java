package com.hotel.model;

/**
 * Suite Room - price is 1.5x base price. Demonstrates inheritance.
 */
public class SuiteRoom extends Room {
    private boolean hasJacuzzi;

    public SuiteRoom() {}

    public SuiteRoom(String roomNumber, String type, double price, String amenities,
                     boolean available, int floor, boolean hasJacuzzi) {
        super(roomNumber, type, price, amenities, available, floor);
        this.hasJacuzzi = hasJacuzzi;
    }

    @Override
    public double calculatePrice() {
        return getPrice() * 1.5;
    }

    @Override
    public String getDisplayInfo() {
        return String.format("Suite Room %s | Floor %d | $%.2f/night%s | %s",
                getRoomNumber(), getFloor(), calculatePrice(),
                hasJacuzzi ? " (Jacuzzi)" : "",
                getAmenities());
    }

    public boolean isHasJacuzzi() { return hasJacuzzi; }
    public void setHasJacuzzi(boolean hasJacuzzi) { this.hasJacuzzi = hasJacuzzi; }
}
