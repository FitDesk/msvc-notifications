package com.msvcnotifications.services.implemts;

import com.msvcnotifications.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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


    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.welcome.subject}")
    private String welcomeSubject;

    @Value("${app.email.password-changed.subject}")
    private String passwordChangedSubject;


    @Override
    public void sendConfirmationEmail(String to, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String body = "<p>Hola " + firstName + ",</p>" +
                    "<p>¡Bienvenido a FitDesk! Tu cuenta ha sido creada exitosamente.</p>" +
                    "<p>Saludos,<br>El equipo de FitDesk</p>";

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(welcomeSubject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", to);

        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage());
            throw new RuntimeException("Error enviando email de confirmación", e);
        }
    }

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
}