package com.sava.booking;

import java.util.ArrayList;
import java.util.List;

public class SeatManager {
    private final List<Seat> seats;

    public SeatManager(int totalSeats) {
        seats = new ArrayList<>();
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat(i));
        }
    }

    public boolean reserveSeat(int seatId) {
        for (Seat seat : seats) {
            if (seat.getId() == seatId) {
                return seat.reserve(); // now thread-safe
            }
        }
        return false;
    }

    public List<Seat> getSeats() {
        return seats;
    }
}
