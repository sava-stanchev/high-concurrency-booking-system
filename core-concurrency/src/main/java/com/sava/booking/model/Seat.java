package com.sava.booking.model;

import java.util.concurrent.locks.ReentrantLock;

public class Seat {
    private final int id; // unique seat number
    private boolean reserved = false;
    // fair lock for thread-safe booking
    private final ReentrantLock lock = new ReentrantLock(true);

    public Seat(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public boolean isReserved() {
        return reserved;
    }

    public boolean reserve() {
        lock.lock(); // acquire the lock for this seat
        try {
            if (!reserved) {
                reserved = true;
                return true;
            } else {
                return false;
            }
        } finally {
            lock.unlock(); // release lock
        }
    }
}
