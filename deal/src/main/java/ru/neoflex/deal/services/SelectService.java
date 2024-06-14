package ru.neoflex.deal.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SelectService {

    private final StatementRepository statementRepository;
    private final StatementService statementService;

    @Transactional
    public void updateStatement(LoanOfferDTO loanOffer) {

        Statement statement = getStatementById(loanOffer.getStatementId());

        statementService.updateStatementStatus(statement, StatementStatus.APPROVED);

        log.info("Обновили статус statement - " + statement.getStatementId());

        statement.setAppliedOffer(loanOffer);
        log.info(" принятое предложение установили в поле appliedOffer - " + loanOffer);

        log.info("Заявка сохраняется");

        statementRepository.save(statement);

    }

    public Statement getStatementById(UUID statementId) {

        return statementRepository.findById(statementId)
                .orElseThrow(() -> new EntityNotFoundException("Заявление с идентификатором не найдено - " + statementId));
    }

}