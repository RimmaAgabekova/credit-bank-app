package ru.neoflex.calculator.controllers;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;
import ru.neoflex.calculator.services.CalcService;
import ru.neoflex.calculator.services.OfferService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CalculatorController implements CalculatorControllerApi {

    private final OfferService offerService;
    private final CalcService calcService;

    @Override
    public ResponseEntity<List<LoanOfferDTO>> offers(LoanStatementRequestDTO loanStatementRequest) {
        return ResponseEntity.ok(offerService.generateOffers(loanStatementRequest));
    }

    @Override
    public ResponseEntity<CreditDTO> calc(ScoringDataDTO scoringData) {
        return ResponseEntity.ok(calcService.calculateCredit(scoringData));
    }
}
