package com.sava.booking;

public class Seat {
    private final int id; // seat number, cannot change
    private boolean reserved = false; // is it booked yet?

    public Seat(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void reserve() {
        reserved = true;
    }
}
