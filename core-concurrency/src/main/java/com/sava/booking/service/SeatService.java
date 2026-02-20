package com.sava.booking.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
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
            Boolean reserved = jdbcTemplate.queryForObject(
                    "SELECT reserved FROM seats WHERE id = ? FOR UPDATE",
                    Boolean.class,
                    seatId
            );

            if (Boolean.FALSE.equals(reserved)) {
                jdbcTemplate.update(
                        "UPDATE seats SET reserved = TRUE WHERE id = ?",
                        seatId
                );
                return true;
            } else {
                throw new SeatAlreadyReservedException("Seat " + seatId + " is already reserved.");
            }
        } catch (EmptyResultDataAccessException e) {
            throw new SeatNotFoundException("Seat " + seatId + " does not exist.");
        }
    }

    @Transactional
    public boolean reserveSeatWithReservation(int seatId, int userId) {
        try {
            // Lock the seat row
            Boolean reserved = jdbcTemplate.queryForObject(
                    "SELECT reserved FROM seats WHERE id = ? FOR UPDATE",
                    Boolean.class,
                    seatId
            );

            if (Boolean.FALSE.equals(reserved)) {
                // Mark seat as reserved
                jdbcTemplate.update(
                        "UPDATE seats SET reserved = TRUE WHERE id = ?",
                        seatId
                );

                // Insert a reservation record
                jdbcTemplate.update(
                        "INSERT INTO reservations (seat_id, user_id, status) VALUES (?, ?, ?)",
                        seatId,
                        userId,
                        ReservationStatus.PENDING.name()
                );

                return true;
            } else {
                throw new SeatAlreadyReservedException("Seat " + seatId + " is already reserved.");
            }

        } catch (EmptyResultDataAccessException e) {
            throw new SeatNotFoundException("Seat " + seatId + " does not exist.");
        }
    }

    @Transactional
    public int resetAllSeats() {
        return jdbcTemplate.update("UPDATE seats SET reserved = FALSE");
    }
}