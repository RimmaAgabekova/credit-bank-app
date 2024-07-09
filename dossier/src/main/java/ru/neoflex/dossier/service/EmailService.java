package ru.neoflex.dossier.service;


import jakarta.activation.DataSource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.neoflex.dossier.model.dto.EmailMessage;
import ru.neoflex.dossier.utils.EmailData;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String serverEmail;

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;


    public void sendHtmlMessage(EmailMessage emailMessage, EmailData dataForEmail) {
        Context context = new Context();
        context.setVariable("payload", dataForEmail);
        String emailContent = templateEngine.process("mailTemplate", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(emailMessage.getAddress());
            helper.setSubject(dataForEmail.getSubject());
            helper.setText(emailContent, true);
            helper.setFrom(serverEmail);

            if (dataForEmail.getAttachments() != null && dataForEmail.getAttachments().size() > 0) {
                for (Map.Entry<String, DataSource> attachment : dataForEmail.getAttachments().entrySet()) {
                    helper.addAttachment(attachment.getKey(), attachment.getValue());
                }
            }

            mailSender.send(message);
        } catch (MailException | MessagingException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
        }
    }
}
