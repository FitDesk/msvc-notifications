package com.msvcnotifications.services.implemts;

import com.itextpdf.html2pdf.HtmlConverter;
import com.msvcnotifications.events.PaymentApprovedEvent;
import com.msvcnotifications.services.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Slf4j
public class PdfGeneratorServiceImpl implements PdfGeneratorService {

     private final TemplateEngine templateEngine;

    @Override
    public byte[] generatePaymentReceiptPdf(PaymentApprovedEvent paymentEvent) {
        try {
            log.info("📄 Generando PDF para: {}", paymentEvent.userName());

            // Crear contexto con datos del pago
            Context context = createPdfContext(paymentEvent);

            // Renderizar HTML desde template Thymeleaf
            String htmlContent = templateEngine.process("payment-receipt-pdf", context);

            // Convertir HTML a PDF usando iText7
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            HtmlConverter.convertToPdf(htmlContent, outputStream);

            byte[] pdfBytes = outputStream.toByteArray();
            log.info("✅ PDF generado: {} bytes para {}", pdfBytes.length, paymentEvent.userName());

            return pdfBytes;

        } catch (Exception e) {
            log.error("❌ Error generando PDF: {}", e.getMessage(), e);
            throw new RuntimeException("Error generando PDF", e);
        }
    }

    private Context createPdfContext(PaymentApprovedEvent paymentEvent) {
        Context context = new Context();

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // ✅ SOLUCIÓN: Convertir OffsetDateTime a LocalDateTime
        LocalDateTime paymentDateTime = paymentEvent.paymentDate().toLocalDateTime();

        // Datos principales
        context.setVariable("companyName", "FitDesk");
        context.setVariable("userName", paymentEvent.userName());
        context.setVariable("userEmail", paymentEvent.userEmail());
        context.setVariable("transactionId", paymentEvent.mercadoPagoTransactionId());
        context.setVariable("paymentDate", paymentDateTime.format(dateTimeFormatter));
        context.setVariable("externalReference", paymentEvent.externalReference());
        context.setVariable("paymentId", paymentEvent.paymentId().toString());

        // Datos del plan
        context.setVariable("planName", paymentEvent.planName());
        context.setVariable("durationMonths", paymentEvent.durationMonths());
        context.setVariable("amount", paymentEvent.amount());

        // Fechas del plan - Usar paymentDateTime en lugar de startDate
        LocalDateTime endDate = paymentDateTime.plusMonths(paymentEvent.durationMonths());
        context.setVariable("planStartDate", paymentDateTime.toLocalDate().format(dateFormatter));
        context.setVariable("planEndDate", endDate.toLocalDate().format(dateFormatter));

        // Total
        context.setVariable("totalAmount", paymentEvent.amount());
        context.setVariable("currentYear", LocalDateTime.now().getYear());

        return context;
    }
}