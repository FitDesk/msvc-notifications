package com.msvcnotifications.services;

import com.msvcnotifications.events.PaymentApprovedEvent;

public interface JasperReportService {
      byte[] generatePaymentReceiptPdf(PaymentApprovedEvent paymentEvent) throws Exception;
}