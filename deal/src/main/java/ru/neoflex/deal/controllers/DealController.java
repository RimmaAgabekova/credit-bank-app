package ru.neoflex.deal.controllers;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.deal.model.dto.FinishRegistrationRequestDto;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.services.CalculateService;
import ru.neoflex.deal.services.StatementService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/deal")
@RequiredArgsConstructor
public class DealController implements DealControllerApi {

    private final StatementService statementService;
    private final CalculateService calculateService;

    @Override
    public List<LoanOfferDTO> statement(LoanStatementRequestDTO loanStatementRequestDTO) {
        return statementService.createStatement(loanStatementRequestDTO);
    }

    @Override
    public void select(LoanOfferDTO loanOfferDTO) {
        statementService.updateStatement(loanOfferDTO);
    }

    @Override
    public void calculate(String statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        calculateService.calculateCredit(UUID.fromString(statementId), finishRegistrationRequestDto);
    }


}
