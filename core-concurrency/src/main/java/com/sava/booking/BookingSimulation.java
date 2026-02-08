package com.sava.booking;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookingSimulation {
    public static void main(String[] args) {
        int totalSeats = 100;
        int totalUsers = 500;
        int threadPoolSize = 50; // number of threads to simulate concurrent users

        // Create 100 seats
        DBSeatManager manager = new DBSeatManager();
        // 50 threads simulating concurrent users
        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        // Simulate 500 users trying to book seats
        for (int i = 0; i < totalUsers; i++) {
            final int seatId = (i % totalSeats) + 1; // cycle through seats 1..100
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

