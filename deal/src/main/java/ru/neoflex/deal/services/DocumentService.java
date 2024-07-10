package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.exception.SesCodeException;
import ru.neoflex.deal.model.dto.EmailMessage;
import ru.neoflex.deal.models.Statement;

import java.time.LocalDate;
import java.util.Random;

import static ru.neoflex.deal.model.dto.StatementStatus.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {

    private final KafkaService kafkaService;
    private final StatementService statementService;
    private final Random random = new Random();

    public void sendCreateDocumentRequest(Statement statement) {
        kafkaService.sendMessage(EmailMessage.ThemeEnum.CREATE_DOCUMENTS, statement);
    }

    public void sendSendDocumentRequest(Statement statement) {
        Statement updateStatus = statementService.updateStatementStatus(statement, PREPARE_DOCUMENTS);
        log.info("Обновлен статус заявки {} на: {}", statement.getStatementId(), updateStatus);
        kafkaService.sendMessage(EmailMessage.ThemeEnum.SEND_DOCUMENTS, statement);
    }

    public void sendSignDocumentRequest(Statement statement) {
        statement.setSesCode(random.nextInt(10000));
        log.info("Для заявки {} сгенерирован код подписания: {}", statement.getStatementId(), statement.getSesCode());
        kafkaService.sendMessage(EmailMessage.ThemeEnum.SEND_SES, statement);
    }

    public void sendCreditIssueRequest(Statement statement, Integer sesCode) {
        log.info("От клиента получен код: {}", sesCode);

        if (!statement.getSesCode().equals(sesCode)) {
            throw new SesCodeException("Неверный код");
        }

        statementService.updateStatementStatus(statement, DOCUMENT_SIGNED);
        statementService.updateStatementStatus(statement, CREDIT_ISSUED);
        statement.setSignDate(LocalDate.now());
        log.info("Обновленная заявка сохранена в БД");

        kafkaService.sendMessage(EmailMessage.ThemeEnum.CREDIT_ISSUED, statement);
        statement.getCreditId().setCreditStatus(CreditStatus.ISSUED.toString());
    }

    public void sendStatementDeniedRequest(Statement statement) {
        kafkaService.sendMessage(EmailMessage.ThemeEnum.STATEMENT_DENIED, statement);
    }
}
