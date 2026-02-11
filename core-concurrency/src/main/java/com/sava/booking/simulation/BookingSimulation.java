package com.sava.booking.simulation;

import com.sava.booking.service.SeatService;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class BookingSimulation {
    private final SeatService seatService;

    public BookingSimulation(SeatService seatService) {
        this.seatService = seatService;
    }

    public void runSimulation() {
        int totalSeats = 100;
        int totalUsers = 500;
        int threadPoolSize = 50; // number of threads to simulate concurrent users

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        // Metrics
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // Simulate 500 users trying to book seats
        for (int i = 0; i < totalUsers; i++) {
            final int seatId = (i % totalSeats) + 1; // cycle through seats 1..100
            executor.submit(() -> {
                boolean success = seatService.reserveSeat(seatId);
                if (success) successCount.incrementAndGet();
                else failCount.incrementAndGet();

                System.out.println(Thread.currentThread().getName() +
                        " tried to book seat " + seatId +
                        " - Success: " + success);
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.MINUTES); // wait for threads

        System.out.println("=== Booking Summary ===");
        System.out.println("Successful bookings: " + successCount.get());
        System.out.println("Failed bookings: " + failCount.get());
    }
}

