package ru.neoflex.calculator.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;
import ru.neoflex.calculator.services.CalcService;
import ru.neoflex.calculator.services.OfferService;


import java.util.List;

@RestController
@RequestMapping("/calculator")
@RequiredArgsConstructor
@Validated
public class CalculatorController {

    private final OfferService offerService;
    private final CalcService calcService;


    @PostMapping("/offers")
    public ResponseEntity<List<LoanOfferDTO>> generateOffers(@Valid @RequestBody LoanStatementRequestDTO request) {
        return ResponseEntity.ok(offerService.generateOffers(request));
    }

    @PostMapping("/calc")
    public ResponseEntity calculateCredit(@Valid @RequestBody ScoringDataDTO scoringData) {
        try {
            return ResponseEntity.ok(calcService.calculateCredit(scoringData));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
