package com.sava.booking;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingSimulation {
    public static void main(String[] args) {
        // Create 10 seats
        SeatManager manager = new SeatManager(10);
        // 20 threads simulating concurrent users
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // Simulate 50 users trying to book seats
        for (int i = 0; i < 50; i++) {
            final int seatId = (i % 10) + 1; // cycle through seats 1–10
            executor.submit(() -> {
                boolean success = manager.reserveSeat(seatId);
                System.out.println(Thread.currentThread().getName() +
                        " tried to book seat " + seatId +
                        " - Success: " + success);
            });
        }

        executor.shutdown();
    }
}

