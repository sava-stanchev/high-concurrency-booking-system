package com.sava.booking.simulation;

import com.sava.booking.service.SeatAlreadyReservedException;
import com.sava.booking.service.SeatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulates concurrent seat booking to test race condition handling.
 * Creates 500 users competing for 100 seats using a thread pool of 50 threads.
 */
@Component
public class BookingSimulation {
    private static final Logger log = LoggerFactory.getLogger(BookingSimulation.class);

    private final SeatService seatService;

    public BookingSimulation(SeatService seatService) {
        this.seatService = seatService;
    }

    public void runSimulation() {
        int totalSeats = 100;
        int totalUsers = 500;
        int threadPoolSize = 50;

        log.info("Starting booking simulation: {} users, {} seats, {} threads",
                totalUsers, totalSeats, threadPoolSize);

        long startTime = System.currentTimeMillis();

        try (ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize)) {
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            AtomicInteger errorCount = new AtomicInteger(0);

            for (int i = 0; i < totalUsers; i++) {
                final int userId = i + 1;
                final int seatId = (i % totalSeats) + 1; // cycle through seats 1..100

                executor.submit(() -> {
                    try {
                        seatService.reserveSeatWithReservation(seatId, userId);
                        successCount.incrementAndGet();
                        log.debug("User {} successfully booked seat {}", userId, seatId);
                    } catch (SeatAlreadyReservedException e) {
                        failCount.incrementAndGet();
                        log.debug("User {} failed to book seat {} (already reserved)", userId, seatId);
                    } catch (Exception e) {
                        errorCount.incrementAndGet();
                        log.error("User {} encountered error booking seat {}: {}",
                                userId, seatId, e.getMessage());
                    }
                });
            }

            executor.shutdown();

            // Wait for all threads to finish
            try {
                boolean finished = executor.awaitTermination(1, TimeUnit.MINUTES);
                if (!finished) {
                    log.warn("Not all booking tasks finished within 1 minute!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Simulation interrupted!", e);
            }

            long duration = System.currentTimeMillis() - startTime;
            double throughput = (totalUsers * 1000.0) / duration;

            log.info("=== Booking Simulation Summary ===");
            log.info("Total users: {}", totalUsers);
            log.info("Successful bookings: {}", successCount.get());
            log.info("Failed bookings: {}", failCount.get());
            log.info("Errors: {}", errorCount.get());
            log.info("Duration: {}ms", duration);
            log.info("Throughput: {} bookings/sec", String.format("%.2f", throughput));
        }
    }
}