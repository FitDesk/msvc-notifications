package com.msvcnotifications.services.implemts;

import com.msvcnotifications.services.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;


    @Value("${app.email.from}")
    private String fromEmail;

    @Value("${app.email.welcome.subject}")
    private String welcomeSubject;

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
}