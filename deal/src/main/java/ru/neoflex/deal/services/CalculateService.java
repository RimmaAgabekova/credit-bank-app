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
import ru.neoflex.deal.mappers.ScoringDataDTOMapper;
import ru.neoflex.deal.model.dto.CreditDTO;
import ru.neoflex.deal.model.dto.FinishRegistrationRequestDto;
import ru.neoflex.deal.model.dto.ScoringDataDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
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

        if (statement.getStatus() == StatementStatus.CC_DENIED) {
            throw new DeniedException("По данной заявке стоит статус - ОТКАЗ");
        }

        Client client = statement.getClientId();

        ScoringDataDTO scoringData = scoringDataDTOMapper.finishOfferClientToScoringData(
                finishRegistrationRequest,
                statement.getAppliedOffer(),
                client
        );

        try {
            CreditDTO creditDTO = calculatorFeignClient.calc(scoringData);
            Credit savedCredit = creditMapper.creditDTOToCredit(creditDTO, CreditStatus.CALCULATED);
            statement.setCreditId(savedCredit);

            statementService.updateStatementStatus(statement, StatementStatus.CC_APPROVED);
            clientMapper.updateClientMapper(finishRegistrationRequest, client);

            documentService.sendCreateDocumentRequest(statement);
        } catch (Exception e) {
            if (e.getMessage().toUpperCase().contains("ОТКАЗ")) {
                statementService.updateStatementStatus(statement, StatementStatus.CC_DENIED);
                documentService.sendStatementDeniedRequest(statement);
            }
            log.error("ОТКАЗ - " + e.getMessage());
        }
    }
}
