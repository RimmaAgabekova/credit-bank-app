package ru.neoflex.deal.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.ClientMapper;
import ru.neoflex.deal.mappers.EmailMessageMapper;
import ru.neoflex.deal.mappers.StatementDTOMapper;
import ru.neoflex.deal.model.dto.*;
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
    private final KafkaService kafkaService;
    private final EmailMessageMapper emailMessageMapper;
    private final StatementDTOMapper statementDTOMapper;

    public List<LoanOfferDTO> createStatement(LoanStatementRequestDTO request) {
        Client client = clientMapper.loanRequestToClient(request);
        log.info("Создали клиента - {}", client.getClientId());

        Statement createStatement = buildStatement(client);
        save(createStatement);
        updateStatementStatus(createStatement.getStatementId(), StatementStatus.PREAPPROVAL);
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

    public Statement updateStatementStatus(UUID statementId, StatementStatus status) {
        Statement statement = getStatementById(statementId);
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
        updateStatementStatus(statement.getStatementId(), StatementStatus.APPROVED);

        statement.setAppliedOffer(loanOffer);

        String clientEmail = statement.getClientId().getEmail();
        EmailMessage message = emailMessageMapper.createEmailMassage(EmailMessage.ThemeEnum.FINISH_REGISTRATION,
                statement.getStatementId(), clientEmail);
        kafkaService.sendTopic(message);
    }

    public Statement getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Заявление с идентификатором не найдено - " + statementId));
    }

    public StatementDTO getStatementDTO(UUID statementId) {
        Statement statement = getStatementById(statementId);
        return statementDTOMapper.statementToStatementDto(statement);
    }

    public Statement save(Statement statement) {
        return statementRepository.save(statement);
    }

}
