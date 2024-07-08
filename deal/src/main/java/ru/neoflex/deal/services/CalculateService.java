package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.exception.DeniedException;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.ClientMapper;
import ru.neoflex.deal.mappers.CreditMapper;
import ru.neoflex.deal.mappers.EmailMessageMapper;
import ru.neoflex.deal.mappers.ScoringDataDTOMapper;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Credit;
import ru.neoflex.deal.models.Statement;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalculateService {

    private final CalculatorFeignClient calculatorFeignClient;
    private final StatementService statementService;
    private final ClientMapper clientMapper;
    private final CreditMapper creditMapper;
    private final ScoringDataDTOMapper scoringDataDTOMapper;
    private final DocumentService documentService;
    private final EmailMessageMapper emailMessageMapper;

    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequest) {
        log.info("Запрос на рассчет кредита для statementId =  {} по параметрам {}", statementId, finishRegistrationRequest);
        Statement statement = statementService.getStatementById(statementId);

        if (statement.getStatus() == StatementStatus.CC_DENIED) {
            log.error("По данной заявке стоит статус - ОТКАЗ");
            throw new DeniedException("По данной заявке стоит статус - ОТКАЗ");
        }
        LoanOfferDTO loanOffer = statement.getAppliedOffer();
        Client client = statement.getClientId();

        ScoringDataDTO scoringData = scoringDataDTOMapper.finishOfferClientToScoringData(finishRegistrationRequest, loanOffer, client);
        String clientEmail = statement.getClientId().getEmail();

        try {
            CreditDTO creditDTO = calculatorFeignClient.calc(scoringData);
            Credit savedCredit = creditMapper.creditDTOToCredit(creditDTO, CreditStatus.CALCULATED);
            statement.setCreditId(savedCredit);

            statementService.updateStatementStatus(statement.getStatementId(), StatementStatus.CC_APPROVED);

            clientMapper.updateClientMapper(finishRegistrationRequest, client);

            EmailMessage message = emailMessageMapper.createEmailMassage(EmailMessage.ThemeEnum.CREATE_DOCUMENTS, statementId, clientEmail);
            documentService.sendCreateDocumentRequest(message);
        } catch (Exception e) {
            if (e.getMessage().toUpperCase().contains("ОТКАЗ")) {
                statementService.updateStatementStatus(statement.getStatementId(), StatementStatus.CC_DENIED);
                EmailMessage massage = emailMessageMapper.createEmailMassage(EmailMessage.ThemeEnum.STATEMENT_DENIED, statementId, clientEmail);
                documentService.sendStatementDeniedRequest(massage);
            }
            log.error("ОТКАЗ - " + e.getMessage());
        }
    }
}
