package ru.neoflex.deal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.neoflex.deal.mappers.EmailMessageMapper;
import ru.neoflex.deal.model.dto.EmailMessage;
import ru.neoflex.deal.models.Statement;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    @Value("${spring.kafka.finish-registration-topic}")
    private String finishRegistrationTopic;
    @Value("${spring.kafka.create-documents-topic}")
    private String createDocumentsTopic;
    @Value("${spring.kafka.send-documents-topic}")
    private String sendDocumentsTopic;
    @Value("${spring.kafka.send-ses-topic}")
    private String sendSesTopic;
    @Value("${spring.kafka.credit-issued-topic}")
    private String creditIssuedTopic;
    @Value("${spring.kafka.statement-denied-topic}")
    private String statementDeniedTopic;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final EmailMessageMapper emailMessageMapper;

    public void sendToTopic(EmailMessage message) {
        kafkaTemplate.send(getTopic(message.getTheme()), getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
    }

    private String getTopic(EmailMessage.ThemeEnum theme) {
        String topic = null;

        switch (theme) {
            case FINISH_REGISTRATION -> {
                topic = finishRegistrationTopic;
            }
            case CREATE_DOCUMENTS -> {
                topic = createDocumentsTopic;
            }
            case SEND_DOCUMENTS -> {
                topic = sendDocumentsTopic;
            }
            case SEND_SES -> {
                topic = sendSesTopic;
            }
            case CREDIT_ISSUED -> {
                topic = creditIssuedTopic;
            }
            case STATEMENT_DENIED -> {
                topic = statementDeniedTopic;
            }
        }
        return topic;
    }

    public void sendMessage(EmailMessage.ThemeEnum theme, Statement statement) {
        EmailMessage message = emailMessageMapper.createEmailMassage(
                theme,
                statement.getStatementId(),
                statement.getClientId().getEmail()
        );
        sendToTopic(message);
    }

    private String getMessageJson(EmailMessage message) {
        String messageJson;
        try {
            messageJson = objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return messageJson;
    }
}
