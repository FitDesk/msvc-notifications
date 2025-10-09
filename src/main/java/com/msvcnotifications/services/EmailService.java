package com.msvcnotifications.services;


public interface EmailService {
    void sendConfirmationEmail(String to, String firstName);
}