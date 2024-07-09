package ru.neoflex.dossier.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.neoflex.dossier.feign.DealFeignClient;
import ru.neoflex.dossier.model.dto.EmailMessage;
import ru.neoflex.dossier.model.dto.StatementDTO;
import ru.neoflex.dossier.utils.EmailData;

import java.text.MessageFormat;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaListenerService {

    @Value("${app.base.url.swagger}")
    private String SWAGGER_URL;
    private final EmailService emailService;
    private final DocumentsService documentsService;
    private final ObjectMapper objectMapper;
    private final DealFeignClient dealFeignClient;

    @KafkaListener(topics = "finish-registration")
    public void sendFinishRegistrationRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        EmailData dataForEmail = EmailData.builder()
                .subject("Завершение регистрации")
                .contentText("Завершите регистрацию по заявке №" + emailMessage.getStatementId())
                .build();
        emailService.sendHtmlMessage(emailMessage, dataForEmail);
        log.info("Клиенту отправлено сообщение с текстом: {}", dataForEmail.getContentText());
    }

    @KafkaListener(topics = "create-documents")
    public void sendCreateDocumentsRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        EmailData dataForEmail = EmailData.builder()
                .subject("Создание документов")
                .contentText("Отправьте запрос для оформления документов по заявке №"
                        + emailMessage.getStatementId())
                .linkForClient(SWAGGER_URL)
                .build();
        emailService.sendHtmlMessage(emailMessage, dataForEmail);
        log.info("Клиенту отправлено сообщение с текстом: {}", dataForEmail.getContentText());
    }

    @KafkaListener(topics = "send-documents")
    public void sendSendDocumentsRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);

        EmailData dataForEmail = EmailData.builder()
                .subject("Документы по кредиту")
                .contentText("Ваши документы по кредитной заявке №" + emailMessage.getStatementId() + " сформированы")
                .attachments(documentsService.createCreditDocuments(emailMessage))
                .build();

        emailService.sendHtmlMessage(emailMessage, dataForEmail);
        log.info("Клиенту отправлено сообщение с текстом: {}", dataForEmail.getContentText());
        dealFeignClient.status(emailMessage.getStatementId().toString(), "DOCUMENT_CREATED");
    }

    @KafkaListener(topics = "send-ses")
    public void sendSendSesRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        StatementDTO statement = dealFeignClient.statementData(emailMessage.getStatementId().toString());
        EmailData dataForEmail = EmailData.builder()
                .subject("Код подписания")
                .contentText(MessageFormat.format("Подпишите с помощью кода документы по кредитной заявке №{0}. " +
                        "Код подписания: {1}", emailMessage.getStatementId(), statement.getSesCode()))
                .build();
        emailService.sendHtmlMessage(emailMessage, dataForEmail);
        log.info("Клиенту отправлено сообщение с текстом: {}", dataForEmail.getContentText());
    }

    @KafkaListener(topics = "credit-issued")
    public void sendCreditIssueRequest(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        EmailData dataForEmail = EmailData.builder()
                .subject("Подтверждение успешной выдачи кредита")
                .contentText("Поздравляем! Вам выдан кредит по заявке №" + emailMessage.getStatementId())
                .build();
        emailService.sendHtmlMessage(emailMessage, dataForEmail);
        log.info("Клиенту отправлено сообщение с текстом: {}", dataForEmail.getContentText());
    }

    @KafkaListener(topics = "statement-denied")
    public void sendApplicationDeniedMessage(String message) {
        EmailMessage emailMessage = getEmailMessageFromJson(message);
        EmailData dataForEmail = EmailData.builder()
                .subject("Отмена выдачи кредита")
                .contentText("К сожалению, Вам отказано в выдаче кредита по заявке №" + emailMessage.getStatementId())
                .build();
        emailService.sendHtmlMessage(emailMessage, dataForEmail);
        log.info("Клиенту отправлено сообщение с текстом: {}", dataForEmail.getContentText());
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
