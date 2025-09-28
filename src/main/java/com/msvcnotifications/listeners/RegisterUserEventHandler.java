package com.msvcnotifications.listeners;

import com.msvcnotifications.events.NotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@KafkaListener(topics = "user-created-event-topic")
@RequiredArgsConstructor
@Slf4j
public class RegisterUserEventHandler {

    @KafkaHandler
    @Transactional
    public void handle(NotificationEvent message) {
        log.info("Mensaje recibido {}", message.message());
    }

}
