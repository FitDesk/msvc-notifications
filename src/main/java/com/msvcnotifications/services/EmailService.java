package com.msvcnotifications.services;


import com.msvcnotifications.events.PaymentApprovedEvent;

public interface EmailService {
    void sendConfirmationEmail(String to, String firstName);
    void sendPasswordChangedConfirmation(String email);
    void sendTransactionNotificationEmail(PaymentApprovedEvent paymentEvent);
    }