package ru.neoflex.deal.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class KafkaProducerConfig {

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
}
