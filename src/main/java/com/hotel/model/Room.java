package com.hotel.model;

/**
 * Abstract base class for hotel rooms.
 */
public abstract class Room {
    private String roomNumber;
    private String type;
    private double price;
    private String amenities;
    private boolean available;
    private int floor;

    public Room() {}

    public Room(String roomNumber, String type, double price, String amenities, boolean available, int floor) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.price = price;
        this.amenities = amenities;
        this.available = available;
        this.floor = floor;
    }

    public abstract double calculatePrice();
    public abstract String getDisplayInfo();

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getAmenities() { return amenities; }
    public void setAmenities(String amenities) { this.amenities = amenities; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
}
