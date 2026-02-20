package com.sava.booking.controller;

import com.sava.booking.service.SeatService;
import com.sava.booking.simulation.BookingSimulation;
import com.sava.booking.service.SeatAlreadyReservedException;
import com.sava.booking.service.SeatNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;
    private final BookingSimulation simulation;

    public SeatController(SeatService seatService, BookingSimulation simulation) {
        this.seatService = seatService;
        this.simulation = simulation;
    }

    // reserve a specific seat
    @PostMapping("/{id}/reserve")
    public ResponseEntity<String> reserveSeat(@PathVariable int id) {
        seatService.reserveSeat(id);
        return ResponseEntity.ok("Seat " + id + " reserved successfully.");
    }

    // reserve a seat with a userId
    @PostMapping("/{seatId}/reserve/{userId}")
    public ResponseEntity<String> reserveSeatWithUser(
            @PathVariable int seatId,
            @PathVariable int userId
    ) {
        try {
            seatService.reserveSeatWithReservation(seatId, userId);
            return ResponseEntity.ok("Seat " + seatId + " reserved for user " + userId + " successfully.");
        } catch (SeatNotFoundException | SeatAlreadyReservedException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
        }
    }

    // trigger the booking simulation
    @PostMapping("/simulate")
    public ResponseEntity<String> runSimulation() {
        simulation.runSimulation();
        return ResponseEntity.ok("Booking simulation executed.");
    }

    // reset all seats for re-running the simulation
    @PostMapping("/reset")
    public ResponseEntity<String> resetSeats() {
        int updated = seatService.resetAllSeats();
        return ResponseEntity.ok(updated + " seats reset successfully.");
    }
}
