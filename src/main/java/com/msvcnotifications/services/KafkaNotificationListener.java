package com.msvcnotifications.services;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.Instant;
import java.util.Map;

@Service
public class KafkaNotificationListener {

    // @KafkaListener(topics = "user.created")
    // public void onUserCreated(Map<String, Object> event) {
    //     Map<String, Object> user = (Map<String, Object>) event.get("user");
    //     String title = "Nuevo usuario registrado";
    //     String body = "Usuario" + user.get("username") + "-" + user.get("email");

    //     NotificationEntity notification = NotificationEntity
    //             .builder()
    //             .title(title)
    //             .body(body)
    //             .target("ADMIN")
    //             .readFlag(false)
    //             .createdAt(Instant.now())
    //             .build();

    //     repository.save(notification);
    //     wsTemplate.convertAndSend("/topic/admin/notifications", Map.of("notification", notification));

    // }

}
