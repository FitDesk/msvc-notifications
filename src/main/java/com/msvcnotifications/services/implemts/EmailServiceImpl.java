package com.msvcnotifications.services.implemts;

import com.msvcnotifications.events.PaymentApprovedEvent;
import com.msvcnotifications.services.EmailService;
import com.msvcnotifications.services.JasperReportService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final JasperReportService jasperReportService; // ← SOLO AGREGAR ESTA LÍNEA


    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.welcome.subject}")
    private String welcomeSubject;

    @Value("${app.email.password-changed.subject}")
    private String passwordChangedSubject;

    @Value("${app.email.transaction-notification.subject}")
    private String transactionNotificationSubject;


    @Override
    public void sendConfirmationEmail(String to, String firstName) {
        try {
            Context context = new Context();
            context.setVariable("firstName", firstName);
            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("welcome-email", context);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(welcomeSubject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("📧 Email de bienvenida enviado exitosamente a: {}", to);

        } catch (MessagingException e) {
            log.error("❌ Error enviando email de bienvenida a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error enviando email de confirmación", e);
        }
    }

    @Override
    public void sendPasswordChangedConfirmation(String email) {
        try {
            Context context = new Context();
            context.setVariable("email", email);
            context.setVariable("changeDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));


            String htmlContent = templateEngine.process("password-changed-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject(passwordChangedSubject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email de confirmación de cambio de contraseña enviado exitosamente a: {}", email);

        } catch (MessagingException e) {
            log.error("Error enviando email de confirmación de cambio de contraseña a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error enviando email de confirmación", e);
        }
    }

    @Override
    public void sendTransactionNotificationEmail(PaymentApprovedEvent paymentEvent) {
        try {
            Context context = new Context();

            // MISMOS DATOS QUE YA TENÍAS - SIN CAMBIOS
            context.setVariable("userName", paymentEvent.userName());
            context.setVariable("userEmail", paymentEvent.userEmail());
            context.setVariable("planName", paymentEvent.planName());
            context.setVariable("durationMonths", paymentEvent.durationMonths());
            context.setVariable("amount", paymentEvent.amount());
            context.setVariable("transactionId", paymentEvent.mercadoPagoTransactionId());
            context.setVariable("paymentId", paymentEvent.paymentId().toString());
            context.setVariable("externalReference", paymentEvent.externalReference());

            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            context.setVariable("paymentDate", paymentEvent.paymentDate().format(dateTimeFormatter));

            LocalDateTime startDate = LocalDateTime.now();
            LocalDateTime endDate = startDate.plusMonths(paymentEvent.durationMonths());
            context.setVariable("planStartDate", startDate.format(dateFormatter));
            context.setVariable("planEndDate", endDate.format(dateFormatter));

            context.setVariable("currentYear", LocalDateTime.now().getYear());

            String htmlContent = templateEngine.process("transaction-notification-email", context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(paymentEvent.userEmail());
            helper.setSubject(transactionNotificationSubject);
            helper.setText(htmlContent, true);

            // 📎 SOLO AGREGAR ESTAS LÍNEAS PARA EL PDF
            try {
                byte[] pdfBytes = jasperReportService.generatePaymentReceiptPdf(paymentEvent);
                String fileName = String.format("Comprobante_FitDesk_%s.pdf",
                        paymentEvent.externalReference().replaceAll("[^a-zA-Z0-9]", "_"));

                helper.addAttachment(fileName, new ByteArrayResource(pdfBytes), "application/pdf");
                log.info("📄 PDF adjuntado: {} ({} bytes)", fileName, pdfBytes.length);

            } catch (Exception e) {
                log.error("❌ Error generando PDF: {}", e.getMessage(), e);
                // Continúa enviando email sin PDF si hay error
            }

            mailSender.send(message);
            log.info("📧 Email de notificación de transacción enviado exitosamente a: {} - Plan: {} - Monto: ${}",
                    paymentEvent.userEmail(), paymentEvent.planName(), paymentEvent.amount());

        } catch (MessagingException e) {
            log.error("❌ Error enviando email de notificación de transacción para {}: {}",
                    paymentEvent.userEmail(), e.getMessage());
            throw new RuntimeException("Error enviando email de notificación de transacción", e);
        }
    }
}