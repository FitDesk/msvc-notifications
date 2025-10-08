package com.msvcnotifications.controllers;

import com.msvcnotifications.services.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailService emailService;

    @PostMapping("/confirm")
    public ResponseEntity<Void> sendConfirmation(@RequestBody EmailRequest req) {
        try {
            emailService.sendConfirmationEmail(req.email(), req.firstName());
            return ResponseEntity.accepted().build();
        } catch (RuntimeException ex) {
            return ResponseEntity.status(500).build();
        }
    }

    // Actualizar el record para que coincida con EmailService
    public static record EmailRequest(String email, String firstName) {}
}