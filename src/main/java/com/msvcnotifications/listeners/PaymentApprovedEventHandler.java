package com.msvcnotifications.listeners;

import com.msvcnotifications.events.PaymentApprovedEvent;
import com.msvcnotifications.services.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovedEventHandler {

        private final EmailService emailService;

     @KafkaListener(topics = "payment-approved-event-topic", groupId = "notification-service-group")
    @Transactional
    public void handle(PaymentApprovedEvent payload) {
        try {
            log.info("📧 Procesando notificación de transacción para: {} - Plan: {}", 
                    payload.userEmail(), payload.planName());
            
            emailService.sendTransactionNotificationEmail(payload);
            
            log.info("✅ Email de transacción enviado exitosamente a: {} por ${}", 
                    payload.userEmail(), payload.amount());
            
        } catch (Exception e) {
            log.error("❌ Error procesando notificación de transacción para {}: {}", 
                     payload.userEmail(), e.getMessage(), e);
        }
    }
}