package com.sava.booking.controller;

import com.sava.booking.service.SeatService;
import com.sava.booking.simulation.BookingSimulation;
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
