package com.msvcnotifications.listeners;

import com.msvcnotifications.events.CreatedUserEvent;
import com.msvcnotifications.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
//@KafkaListener(topics = "user-created-event-topic")
@RequiredArgsConstructor
@Slf4j
public class RegisterUserEventHandler {

    @KafkaListener(topics = "user-created-event-topic")
    @Transactional
    public void handle(CreatedUserEvent message) {
        log.info("Usuario recibido: {}", message);
    }

}
