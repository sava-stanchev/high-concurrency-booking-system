package com.sava.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SeatService {
    private static final Logger log = LoggerFactory.getLogger(SeatService.class);

    private final JdbcTemplate jdbcTemplate;

    public SeatService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Reserves a seat in the database if it's not already taken.
     * Uses row-level locking (SELECT ... FOR UPDATE) for thread-safety.
     *
     * @param seatId the seat ID to book
     * @return true if reserved, false if already reserved or doesn't exist
     */
    @Transactional
    public boolean reserveSeat(int seatId) {
        try {
            // Lock the row and check if it's reserved
            Boolean reserved = jdbcTemplate.queryForObject(
                    "SELECT reserved FROM seats WHERE id = ? FOR UPDATE",
                    Boolean.class,
                    seatId
            );

            if (Boolean.FALSE.equals(reserved)) {
                // seat is available, reserve it
                jdbcTemplate.update(
                        "UPDATE seats SET reserved = TRUE WHERE id = ?",
                        seatId
                );
                return true;
            } else {
                throw new SeatAlreadyReservedException("Seat " + seatId + " is already reserved.");
            }
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            // seat does not exist
            throw new SeatNotFoundException("Seat " + seatId + " does not exist.");
        } catch (Exception e) {
            log.error("Failed to reserve seat {}", seatId, e);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public int resetAllSeats() {
        return jdbcTemplate.update("UPDATE seats SET reserved = FALSE");
    }
}