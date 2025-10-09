package com.msvcnotifications.listeners;

import com.msvcnotifications.events.PaymentApprovedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentApprovedEventHandler {

    @KafkaListener(topics = "payment-approved-event-topic")
    @Transactional
    public void handle(PaymentApprovedEvent payload) {
        log.info("Membrecia adquirida {}", payload);
    }

}
