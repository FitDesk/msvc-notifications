package com.msvcnotifications.events;


public record CreatedUserEvent(
        String userId,
        String firstName,
        String lastName,
        String dni,
        String phone,
        String email,
        String profileImageUrl
) {
}