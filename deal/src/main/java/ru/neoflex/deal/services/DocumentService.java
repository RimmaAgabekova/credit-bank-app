package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.model.dto.EmailMessage;
import ru.neoflex.deal.models.Statement;

import java.util.Random;
import java.util.UUID;

import static ru.neoflex.deal.model.dto.StatementStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final KafkaService kafkaService;

    private final StatementService statementService;
    private final Random random = new Random();

    public void sendCreateDocumentRequest(EmailMessage massage) {
        kafkaService.sendCreateDocumentRequest(massage);
    }

    public void sendSendDocumentRequest(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        Statement updateStatus = statementService.updateStatementStatus(statement, PREPARE_DOCUMENTS);
        log.info("Обновлен статус заявки {} на: {}", statementId, updateStatus);

        statementService.save(updateStatus);
        log.info("Обновленная заявка сохранена в БД");

        String clientEmail = statement.getClientId().getEmail();

        EmailMessage message = createEmailMassage(EmailMessage.ThemeEnum.CREATE_DOCUMENTS, statementId, clientEmail);
        kafkaService.sendSendDocumentRequest(message);
    }

    public void sendSignDocumentRequest(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        Integer sesCode = random.nextInt(10000);
        statement.setSesCode(sesCode);
        log.info("Для заявки {} сгенерирован код подписания: {}", statement.getStatementId(), sesCode);

        statementService.save(statement);
        log.info("Обновленная заявка с кодом сохранена в БД");

        String clientEmail = statement.getClientId().getEmail();
        EmailMessage message = createEmailMassage(EmailMessage.ThemeEnum.SEND_SES, statementId, clientEmail);
        message.setSesCode(sesCode);
        kafkaService.sendSignDocumentRequest(message);
    }

    public void sendCreditIssueRequest(UUID statementId, Integer sesCode) {
        log.info("От клиента получен код: {}", sesCode);
        Statement statement = statementService.getStatementById(statementId);

        if (!statement.getSesCode().equals(sesCode)) {
            throw new RuntimeException("Неверный код");
        }

        statementService.updateStatementStatus(statement, DOCUMENT_SIGNED);
        statementService.save(statement);

        statementService.updateStatementStatus(statement, CREDIT_ISSUED);
        statementService.save(statement);
        log.info("Обновленная заявка сохранена в БД");

        String clientEmail = statement.getClientId().getEmail();

        EmailMessage message = createEmailMassage(EmailMessage.ThemeEnum.CREDIT_ISSUED, statementId, clientEmail);
        kafkaService.sendCreditIssueRequest(message);

        statement.getCreditId().setCreditStatus(CreditStatus.ISSUED.toString());
    }

    public void sendStatementDeniedRequest(EmailMessage message) {
        kafkaService.sendStatementDeniedRequest(message);
    }

    public EmailMessage createEmailMassage(EmailMessage.ThemeEnum theme, UUID statementId, String address) {
        return EmailMessage.builder()
                .theme(theme)
                .statementId(statementId)
                .address(address)
                .build();
    }

}
