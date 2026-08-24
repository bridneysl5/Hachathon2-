package com.example.oreoinsightfactory.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(content);
            mailSender.send(msg);
            System.out.println("Email plano enviado a: " + to);
        } catch (Exception e) {
            System.err.println("Error enviando email plano: " + e.getMessage());
        }
    }

    public void sendHtmlEmailWithAttachment(String to, String subject, String htmlBody, byte[] pdfBytes) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            if (pdfBytes != null && pdfBytes.length > 0) {
                helper.addAttachment("Reporte_Oreo_Insight.pdf", new ByteArrayResource(pdfBytes));
            }

            mailSender.send(message);
            System.out.println("Email HTML Premium enviado a: " + to);
        } catch (Exception e) {
            System.err.println("Error enviando email HTML con adjunto: " + e.getMessage());
        }
    }
}