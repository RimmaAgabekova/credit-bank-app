package ru.neoflex.deal.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.ClientMapper;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.model.dto.StatementStatusHistoryDTO;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class StatementService {

    private final StatementRepository statementRepository;
    private final CalculatorFeignClient calculatorFeignClient;
    private final ClientMapper clientMapper;

    public List<LoanOfferDTO> createStatement(LoanStatementRequestDTO request) {
        Client client = clientMapper.loanRequestToClient(request);
        log.info("Создали клиента - {}",  client.getClientId());

        Statement createStatement = buildStatement(client);
        updateStatementStatus(createStatement, StatementStatus.PREAPPROVAL);
        Statement savedStatement = save(createStatement);

        List<LoanOfferDTO> loanOffers = calculatorFeignClient.offers(request);

        if (loanOffers != null) {
            loanOffers.forEach(offer -> offer.setStatementId(savedStatement.getStatementId()));
        }
        return loanOffers;
    }

    public Statement buildStatement(Client client) {
        return Statement.builder()
                .clientId(client)
                .creationDate(LocalDate.now())
                .statusHistory(new ArrayList<>())
                .build();
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

    public void updateStatement(LoanOfferDTO loanOffer) {
        Statement statement = getStatementById(loanOffer.getStatementId());
        updateStatementStatus(statement, StatementStatus.APPROVED);

        statement.setAppliedOffer(loanOffer);
    }

    public Statement getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Заявление с идентификатором не найдено - " + statementId));
    }

    public Statement save(Statement statement) {
        return statementRepository.save(statement);
    }
}
