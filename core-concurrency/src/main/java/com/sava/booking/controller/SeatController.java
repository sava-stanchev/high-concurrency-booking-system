package com.sava.booking.controller;

import com.sava.booking.service.SeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seats")
public class SeatController {
    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<String> reserveSeat(@PathVariable int id) {
        boolean success = seatService.reserveSeat(id);

        if (success) {
            return ResponseEntity.ok("Seat " + id + " reserved successfully.");
        } else {
            return ResponseEntity.badRequest()
                    .body("Seat " + id + " is already reserved or does not exist.");
        }
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetSeats() {
        int updated = seatService.resetAllSeats();
        return ResponseEntity.ok(updated + " seats reset successfully.");
    }
}
