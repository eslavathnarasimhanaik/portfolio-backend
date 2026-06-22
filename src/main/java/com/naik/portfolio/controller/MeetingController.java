package com.naik.portfolio.controller;

import com.naik.portfolio.model.MeetingBooking;
import com.naik.portfolio.repository.MeetingBookingRepository;
import com.naik.portfolio.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meetings")
@CrossOrigin(origins = "*")
public class MeetingController {

    private final MeetingBookingRepository repository;
    private final EmailService emailService;

    @Autowired
    public MeetingController(MeetingBookingRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    /** POST /api/meetings — Book a new meeting */
    @PostMapping
    public ResponseEntity<Map<String, Object>> bookMeeting(@RequestBody MeetingBooking booking) {
        MeetingBooking savedBooking = repository.save(booking);

        // Send email notification asynchronously
        emailService.notifyNewMeetingBooking(
            savedBooking.getName(), savedBooking.getEmail(), savedBooking.getTopic(),
            savedBooking.getPreferredDate(), savedBooking.getPreferredTime()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Meeting booked successfully!");
        response.put("data", savedBooking);

        return ResponseEntity.ok(response);
    }

    /** GET /api/meetings — List all meeting bookings */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMeetings() {
        List<MeetingBooking> meetings = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", meetings.size());
        response.put("data", meetings);

        return ResponseEntity.ok(response);
    }

    /** GET /api/meetings/{id} — Get a specific booking */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMeetingById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        return repository.findById(id)
            .map(booking -> {
                response.put("success", true);
                response.put("data", booking);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "Meeting booking not found with id: " + id);
                return ResponseEntity.status(404).body(response);
            });
    }
}
