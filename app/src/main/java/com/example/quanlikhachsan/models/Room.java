package com.example.quanlikhachsan.models;

public class Room {
    private int id;
    private String roomNumber;
    private String roomType;
    private int price;
    private boolean isBooked;

    public Room(int id, String roomNumber, String roomType, int price, boolean isBooked) {
        this.id = id;
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.price = price;
        this.isBooked = isBooked;
    }

    public Room(int anInt, String string, String string1, int anInt1, String string2, String string3) {
    }

    public int getId() { return id; }
    public String getRoomNumber() { return roomNumber; }
    public String getRoomType() { return roomType; }
    public int getPrice() { return price; }
    public boolean isBooked() { return isBooked; }

    public void setId(int id) { this.id = id; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public void setRoomType(String roomType) { this.roomType = roomType; }
    public void setPrice(int price) { this.price = price; }
    public void setBooked(boolean booked) { isBooked = booked; }

    public int getDescription() {
        return 0;
    }
}
