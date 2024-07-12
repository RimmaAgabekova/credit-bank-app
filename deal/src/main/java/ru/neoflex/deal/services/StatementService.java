package ru.neoflex.deal.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.exception.DeniedException;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.mappers.*;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

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
    private final StatementDTOMapper statementDTOMapper;
    private final StatementMapper statementMapper;
    private final StatementStatusHistoryMapper statementStatusHistoryMapper;
    private final ListStatementDtoMapper listStatementDtoMapper;

    public List<LoanOfferDTO> createStatement(LoanStatementRequestDTO request) {
        log.info("Пришли данные по заявке = {}", request);
        Statement createStatement = statementMapper.buildStatement(clientMapper.loanRequestToClient(request));

        updateStatementStatus(createStatement, StatementStatus.PREAPPROVAL);
        Statement savedStatement = save(createStatement);

        List<LoanOfferDTO> loanOffers = calculatorFeignClient.offers(request);

        if (loanOffers != null) {
            loanOffers.forEach(offer -> offer.setStatementId(savedStatement.getStatementId()));
        }
        return loanOffers;
    }

    public Statement updateStatementStatus(Statement statement, StatementStatus status) {
        statement.setStatus(status);
        statement.getStatusHistory().add(statementStatusHistoryMapper.addStatus(status, StatementStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC));
        return statement;
    }

    public void updateStatement(Statement statement, LoanOfferDTO loanOffer) {
        if (statement.getStatus() != StatementStatus.PREAPPROVAL) {
            throw new DeniedException("На данном этапе загрузка данных не допускается!");
        }

        updateStatementStatus(statement, StatementStatus.APPROVED);
        statement.setAppliedOffer(loanOffer);
        kafkaService.sendMessage(EmailMessage.ThemeEnum.FINISH_REGISTRATION, statement);
    }

    public Statement getStatementById(UUID statementId) {
        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Заявление с идентификатором не найдено - " + statementId));
    }

    public StatementDTO getStatementDTO(UUID statementId) {
        return statementDTOMapper.statementToStatementDto(getStatementById(statementId));
    }

    public List<StatementDTO> getAllStatements() {
        List<Statement> statements = statementRepository.findAll();
        return listStatementDtoMapper.toStatementDtoList(statements);
    }

    public Statement save(Statement statement) {
        return statementRepository.saveAndFlush(statement);
    }
}
