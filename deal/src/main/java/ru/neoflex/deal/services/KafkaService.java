package ru.neoflex.deal.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.neoflex.deal.config.KafkaProducerConfig;
import ru.neoflex.deal.model.dto.EmailMessage;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaService {

    private final KafkaProducerConfig kafkaProducerConfig;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private String topic;

    public void sendFinishRegistrationRequest(EmailMessage message) {
        topic = kafkaProducerConfig.getFinishRegistrationTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
    }

    public void sendCreateDocumentRequest(EmailMessage message) {
        topic = kafkaProducerConfig.getCreateDocumentsTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
    }

    public void sendSendDocumentRequest(EmailMessage message) {
        topic = kafkaProducerConfig.getSendDocumentsTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
    }

    public void sendSignDocumentRequest(EmailMessage message) {
        topic = kafkaProducerConfig.getSendSesTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
    }

    public void sendCreditIssueRequest(EmailMessage message) {
        topic = kafkaProducerConfig.getCreditIssuedTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
    }

    public void sendStatementDeniedRequest(EmailMessage message) {
        topic = kafkaProducerConfig.getStatementDeniedTopic();
        kafkaTemplate.send(topic, getMessageJson(message));
        log.info("Сообщение отправлено в мс dossier: {}", message);
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
