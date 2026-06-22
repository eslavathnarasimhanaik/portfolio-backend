package com.naik.portfolio.controller;

import com.naik.portfolio.model.ContactMessage;
import com.naik.portfolio.repository.ContactMessageRepository;
import com.naik.portfolio.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*") // Allows local file listings & GitHub Pages previews
public class ContactController {

    private final ContactMessageRepository repository;
    private final EmailService emailService;

    @Autowired
    public ContactController(ContactMessageRepository repository, EmailService emailService) {
        this.repository = repository;
        this.emailService = emailService;
    }

    /** POST /api/contact — Save a new contact message */
    @PostMapping
    public ResponseEntity<Map<String, Object>> submitContactForm(@RequestBody ContactMessage message) {
        ContactMessage savedMessage = repository.save(message);

        // Send email notification asynchronously
        emailService.notifyNewContactMessage(savedMessage.getName(), savedMessage.getEmail(), savedMessage.getMessage());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Message saved successfully to database.");
        response.put("data", savedMessage);

        return ResponseEntity.ok(response);
    }

    /** GET /api/contact — List all contact messages */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllMessages() {
        List<ContactMessage> messages = repository.findAll();

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", messages.size());
        response.put("data", messages);

        return ResponseEntity.ok(response);
    }

    /** GET /api/contact/{id} — Get a specific contact message */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMessageById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();

        return repository.findById(id)
            .map(msg -> {
                response.put("success", true);
                response.put("data", msg);
                return ResponseEntity.ok(response);
            })
            .orElseGet(() -> {
                response.put("success", false);
                response.put("message", "Contact message not found with id: " + id);
                return ResponseEntity.status(404).body(response);
            });
    }
}
