package ru.neoflex.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.neoflex.dossier.model.dto.EmailMessage;
import ru.neoflex.dossier.feign.DealFeignClient;

import java.text.MessageFormat;

@Service
@EnableKafka
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {
    private final EmailService emailService;
    private final ObjectMapper objectMapper;
    private final DealFeignClient dealFeignClient;

    @KafkaListener(topics = "finish-registration", groupId = "${spring.kafka.consumer.group-id}")
    public void sendFinishRegistrationRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        String text = "Уважаемый клиент, завершите регистрацию по заявке №" + emailMessage.getStatementId();
        emailService.setMailSender(emailMessage, text);
        log.info("Клиенту отправлено сообщение с текстом: {}", text);
    }

    @KafkaListener(topics = "create-documents", groupId = "${spring.kafka.consumer.group-id}")
    public void sendCreateDocumentsRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        String text = "Уважаемый клиент, отправьте запрос для оформления документов по заявке №" + emailMessage.getStatementId();
        emailService.setMailSender(emailMessage, text);
        log.info("Клиенту отправлено сообщение с текстом: {}", text);
    }

    @KafkaListener(topics = "send-documents", groupId = "${spring.kafka.consumer.group-id}")
    public void sendSendDocumentsRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        String text = "Уважаемый клиент, сформированы документы по кредитной заявке №" + emailMessage.getStatementId();
        emailService.setMailSender(emailMessage, text);
        log.info("Клиенту отправлено сообщение с текстом: {}", text);
        dealFeignClient.status(emailMessage.getStatementId().toString(), "DOCUMENT_CREATED");
    }

    @KafkaListener(topics = "send-ses", groupId = "${spring.kafka.consumer.group-id}")
    public void sendSendSesRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        String text = MessageFormat.format("Уважаемый клиент, " +
                "подпишите с помощью кода документы по кредитной заявке №{0}. " +
                "Код подписания: {1,number,####}", emailMessage.getStatementId(), emailMessage.getSesCode());
        emailService.setMailSender(emailMessage, text);
        log.info("Клиенту отправлено сообщение с текстом: {}", text);
    }

    @KafkaListener(topics = "credit-issued", groupId = "${spring.kafka.consumer.group-id}")
    public void sendCreditIssueRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        String text = "Уважаемый клиент, Вам выдан кредит по заявке №" + emailMessage.getStatementId();
        emailService.setMailSender(emailMessage, text);
        log.info("Клиенту отправлено сообщение с текстом: {}", text);
    }

    @KafkaListener(topics = "statement-denied", groupId = "${spring.kafka.consumer.group-id}")
    public void sendApplicationDeniedMessage(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        String text = "Уважаемый клиент, Вам отказано в выдаче кредита по заявке №" + emailMessage.getStatementId();
        emailService.setMailSender(emailMessage, text);
        log.info("Клиенту отправлено сообщение с текстом: {}", text);
    }

    private EmailMessage getEmailMessageFromJson(String message) {
        EmailMessage emailMessage;
        try {
            emailMessage = objectMapper.readValue(message, EmailMessage.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return emailMessage;
    }
}
