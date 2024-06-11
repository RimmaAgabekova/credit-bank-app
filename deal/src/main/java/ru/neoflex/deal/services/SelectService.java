package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.exception.DealException;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.StatementStatusHistoryDTO;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SelectService {


    private final StatementRepository statementRepository;

    @Transactional
    public void applyOffer(LoanOfferDTO loanOffer) {
        log.info("Получили select - " + loanOffer);
        Statement statement = statementRepository.findById(loanOffer.getStatementId()).orElseThrow(() ->
                new DealException("Заявление с идентификатором не найдено"));

        updateStatementStatusById(statement, StatementStatusHistoryDTO.StatusEnum.PREAPPROVAL);
        log.info("Обновили статус statement - "+statement.getStatementId());

        statement.setAppliedOffer(loanOffer);
        log.info("Установили setAppliedOffer - "+loanOffer);
        statementRepository.save(statement);
    }

    public void updateStatementStatusById(Statement statement, StatementStatusHistoryDTO.StatusEnum status) {

        log.info(String.valueOf(LocalDateTime.now()));

        statement.getStatusHistory().add(StatementStatusHistoryDTO.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(StatementStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC).build()
        );
    }
}