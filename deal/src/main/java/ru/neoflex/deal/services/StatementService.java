package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.ClientMapper;
import ru.neoflex.deal.mappers.ClientMapperImpl;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.StatementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StatementService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CalculatorFeignClient calculatorFeignClient;
    private final ClientMapper clientMapper = new ClientMapperImpl();

    public List<LoanOfferDTO> createStatement(LoanStatementRequestDTO request) {

        Client savedClient = clientRepository.save(clientMapper.loanRequestToClient(request));
        log.info("Создали и сохранили клиента - " + savedClient.getClientId());

        Statement savedStatement = createAndSaveStatement(savedClient);
        log.info("Создали заявку - " + savedStatement.getStatementId());

        updateStatementStatus(savedStatement, StatementStatus.PREAPPROVAL);
        statementRepository.save(savedStatement);

        log.info("Создали заявку - " + savedStatement.getStatementId());

        List<LoanOfferDTO> loanOffers = calculatorFeignClient.offers(request);

        if (loanOffers != null) {
            loanOffers.forEach(offer -> offer.setStatementId(savedStatement.getStatementId()));
        }
        log.info("данные с микросеривиса calculator" + loanOffers);
        return loanOffers;
    }

    public Statement createAndSaveStatement(Client client) {

        Statement statement = Statement.builder()
                .clientId(client)
                .creationDate(LocalDate.now())
                .statusHistory(new ArrayList<>())
                .build();

        return statement;
    }

    public Statement updateStatementStatus(Statement statement, StatementStatus status) {
        statement.setStatus(status);
        statement.getStatusHistory().add(StatementStatusHistoryDTO.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(StatementStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC)
                .build());
        return statement;
    }
}
