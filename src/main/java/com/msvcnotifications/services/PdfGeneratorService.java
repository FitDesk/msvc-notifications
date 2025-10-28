package com.msvcnotifications.services;

import com.msvcnotifications.events.PaymentApprovedEvent;

public interface PdfGeneratorService {
    byte[] generatePaymentReceiptPdf(PaymentApprovedEvent paymentEvent);
}