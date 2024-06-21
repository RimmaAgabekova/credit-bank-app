package ru.neoflex.statement.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.statement.model.dto.LoanOfferDTO;
import ru.neoflex.statement.model.dto.LoanStatementRequestDTO;
import ru.neoflex.statement.services.StatementService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class StatementController implements StatementControllerApi {

    private final StatementService statementService;

    @Override
    public List<LoanOfferDTO> statement(LoanStatementRequestDTO loanStatementRequestDTO) {
        return statementService.calculationOfLoanTerms(loanStatementRequestDTO);
    }
    @Override
    public void offer(LoanOfferDTO loanOfferDTO) {
        statementService.selectAnOffers(loanOfferDTO);
    }

}
