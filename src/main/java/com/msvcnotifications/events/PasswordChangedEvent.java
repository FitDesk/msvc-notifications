package com.msvcnotifications.events;

public record PasswordChangedEvent(
        String userId,
        String email
) {}