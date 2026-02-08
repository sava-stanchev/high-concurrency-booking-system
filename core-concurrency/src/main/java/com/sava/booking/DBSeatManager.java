package com.sava.booking;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBSeatManager {

    /**
     * Attempts to reserve a seat in the database using a transaction.
     * Thread-safe: uses row-level locking to prevent double-booking.
     * @param seatId the seat ID to book
     * @return true if reserved, false if already reserved or doesn't exist
     */
    public boolean reserveSeat(int seatId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // start transaction

            // Check if seat is already reserved AND lock the row to prevent concurrent bookings
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT reserved FROM seats WHERE id = ? FOR UPDATE"
            );
            checkStmt.setInt(1, seatId);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                boolean reserved = rs.getBoolean("reserved");
                if (!reserved) {
                    // Reserve seat
                    PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE seats SET reserved = TRUE WHERE id = ?"
                    );
                    updateStmt.setInt(1, seatId);
                    updateStmt.executeUpdate();
                    conn.commit();
                    return true;
                } else {
                    conn.rollback(); // seat already taken, undo transaction
                    return false;
                }
            } else {
                conn.rollback();
                return false; // seat does not exist
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
