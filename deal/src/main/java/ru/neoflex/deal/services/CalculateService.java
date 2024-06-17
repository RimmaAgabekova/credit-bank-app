package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.*;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Credit;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.CreditRepository;
import ru.neoflex.deal.enums.CreditStatus;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalculateService {

    private final CreditRepository creditRepository;
    private final CalculatorFeignClient calculatorFeignClient;
    private final SelectService selectService;
    private final ClientRepository clientRepository;
    private final StatementService statementService;

    private final ClientMapper clientMapper = new ClientMapperImpl();
    private final CreditMapper creditMapper = new CreditMapperImpl();
    private final ScoringDataDTOMapper scoringDataDTOMapper = new ScoringDataDTOMapperImpl();

    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequest) {
        log.info("Достаётся из БД заявка по statementId");
        Statement statement = selectService.getStatementById(statementId);
        LoanOfferDTO loanOffer = statement.getAppliedOffer();
        Client client = clientRepository.findById(statement.getClientId().getClientId()).orElseThrow(null);

        log.info("Данные в ScoringDataDto заполняются  информацией из FinishRegistrationRequestDto и Client");
        ScoringDataDTO scoringData = scoringDataDTOMapper.finishOfferClientToScoringData(finishRegistrationRequest,loanOffer,client);

        log.info("Отправляется POST запрос в микросервис calculator");
        CreditDTO creditDTO = calculatorFeignClient.calc(scoringData);

        Credit savedCredit = creditRepository.save(creditMapper.creditDTOToCredit(creditDTO, CreditStatus.CALCULATED));
        log.info("создаётся сущность Credit и сохраняется в базу со статусом CALCULATED");

        statementService.updateStatementStatus(statement, StatementStatus.CC_APPROVED);
        log.info("В заявке обновляется статус на CC_APPROVED и история статусов");

        Client updatedClient = clientMapper.updateClientMapper(finishRegistrationRequest, client);
        clientRepository.save(updatedClient);
        log.info("Данные о клиенте обновлены в БД: {}", updatedClient);

        statement.setCreditId(savedCredit);
        log.info("Заявка сохранена в БД");
    }

}
