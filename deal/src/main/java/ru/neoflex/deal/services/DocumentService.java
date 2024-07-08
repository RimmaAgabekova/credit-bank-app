package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.exception.SesCodeException;
import ru.neoflex.deal.mappers.EmailMessageMapper;
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

    private final EmailMessageMapper emailMessageMapper;
    private final Random random = new Random();

    public void sendCreateDocumentRequest(EmailMessage massage) {
        kafkaService.sendTopic(massage);
    }

    public void sendSendDocumentRequest(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        Statement updateStatus = statementService.updateStatementStatus(statement.getStatementId(), PREPARE_DOCUMENTS);
        log.info("Обновлен статус заявки {} на: {}", statementId, updateStatus);

        String clientEmail = statement.getClientId().getEmail();

        EmailMessage message = emailMessageMapper.createEmailMassage(EmailMessage.ThemeEnum.SEND_DOCUMENTS, statementId, clientEmail);
        kafkaService.sendTopic(message);
    }

    public void sendSignDocumentRequest(UUID statementId) {
        Statement statement = statementService.getStatementById(statementId);
        Integer sesCode = random.nextInt(10000);
        statement.setSesCode(sesCode);
        log.info("Для заявки {} сгенерирован код подписания: {}", statement.getStatementId(), sesCode);

        String clientEmail = statement.getClientId().getEmail();

        EmailMessage message = emailMessageMapper.createEmailMassage(EmailMessage.ThemeEnum.SEND_SES, statementId, clientEmail);
        kafkaService.sendTopic(message);
    }

    public void sendCreditIssueRequest(UUID statementId, Integer sesCode) {
        log.info("От клиента получен код: {}", sesCode);
        Statement statement = statementService.getStatementById(statementId);

        if (!statement.getSesCode().equals(sesCode)) {
            throw new SesCodeException("Неверный код");
        }

        statementService.updateStatementStatus(statement.getStatementId(), DOCUMENT_SIGNED);
        statementService.updateStatementStatus(statement.getStatementId(), CREDIT_ISSUED);
        log.info("Обновленная заявка сохранена в БД");

        String clientEmail = statement.getClientId().getEmail();

        EmailMessage message = emailMessageMapper.createEmailMassage(EmailMessage.ThemeEnum.CREDIT_ISSUED, statementId, clientEmail);
        kafkaService.sendTopic(message);

        statement.getCreditId().setCreditStatus(CreditStatus.ISSUED.toString());
    }

    public void sendStatementDeniedRequest(EmailMessage message) {
        kafkaService.sendTopic(message);
    }
}
