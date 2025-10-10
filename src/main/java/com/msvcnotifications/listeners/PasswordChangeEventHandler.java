package com.msvcnotifications.listeners;

import com.msvcnotifications.events.PasswordChangedEvent;
import com.msvcnotifications.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PasswordChangeEventHandler {

    private final EmailService emailService;

    @KafkaListener(topics = "password-changed-event-topic")
    @Transactional
    public void handle(PasswordChangedEvent event) {
        log.info("Evento de cambio de contraseña recibido: {}", event);
        
        try {
            emailService.sendPasswordChangedConfirmation(
                event.email()
            );
            log.info("Email de confirmación de cambio de contraseña enviado exitosamente a: {}", event.email());
        } catch (Exception e) {
            log.error("Error enviando email de confirmación de cambio de contraseña: {}", e.getMessage());
        }
    }
}