package com.sava.booking.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Service
public class SeatService {
    private final DataSource dataSource;

    public SeatService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Reserves a seat in the database if it's not already taken.
     * Uses row-level locking (SELECT ... FOR UPDATE) for thread-safety.
     * Transactions are managed by Spring.
     *
     * @param seatId the seat ID to book
     * @return true if reserved, false if already reserved or doesn't exist
     */
    @Transactional
    public boolean reserveSeat(int seatId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(
                     "SELECT reserved FROM seats WHERE id = ? FOR UPDATE")) {
            checkStmt.setInt(1, seatId);

            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next()) {
                    boolean reserved = rs.getBoolean("reserved");

                    if (!reserved) {
                        try (PreparedStatement updateStmt = conn.prepareStatement(
                                "UPDATE seats SET reserved = TRUE WHERE id = ?")) {
                            updateStmt.setInt(1, seatId);
                            updateStmt.executeUpdate();
                        }

                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return false; // seat does not exist
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
