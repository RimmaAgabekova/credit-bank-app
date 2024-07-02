package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.ClientMapper;
import ru.neoflex.deal.mappers.CreditMapper;
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

    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequest) {
        log.info("Запрос на рассчет кредита для statementId =  {} по параметрам {}", statementId, finishRegistrationRequest);
        Statement statement = statementService.getStatementById(statementId);
        LoanOfferDTO loanOffer = statement.getAppliedOffer();
        Client client = statement.getClientId();

        ScoringDataDTO scoringData = scoringDataDTOMapper.finishOfferClientToScoringData(finishRegistrationRequest, loanOffer, client);

        CreditDTO creditDTO = calculatorFeignClient.calc(scoringData);

        String clientEmail = statement.getClientId().getEmail();

        EmailMessage massage = documentService.createEmailMassage(EmailMessage.ThemeEnum.STATEMENT_DENIED, statementId, clientEmail);
        documentService.sendStatementDeniedRequest(massage);

        Credit savedCredit = creditMapper.creditDTOToCredit(creditDTO, CreditStatus.CALCULATED);
        statement.setCreditId(savedCredit);

        statementService.updateStatementStatus(statement, StatementStatus.CC_APPROVED);

        clientMapper.updateClientMapper(finishRegistrationRequest, client);

        statementService.save(statement);
        log.info("Заявка сохранена в БД {}", statement);

        EmailMessage message = documentService.createEmailMassage(EmailMessage.ThemeEnum.CREATE_DOCUMENTS, statementId, clientEmail);
        documentService.sendCreateDocumentRequest(message);
    }

}
