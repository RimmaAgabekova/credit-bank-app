package ru.neoflex.dossier.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.neoflex.dossier.model.dto.EmailMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void setMailSender(EmailMessage emailMessage, String text) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(emailMessage.getAddress());
        message.setSubject(emailMessage.getTheme().toString());
        message.setText(text);
        message.setFrom("agabekova-developer@yandex.ru");

        try {
            mailSender.send(message);
        } catch (MailException e) {
            log.error("Ошибка отправки сообщения: {}", e.getMessage());
            throw new RuntimeException(e);
        }

    }
}
