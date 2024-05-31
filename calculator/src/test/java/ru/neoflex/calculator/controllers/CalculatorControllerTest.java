package ru.neoflex.calculator.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;
import ru.neoflex.calculator.services.CalcService;
import ru.neoflex.calculator.services.OfferService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CalculatorControllerTest {

    @InjectMocks
    CalculatorController calculatorController;
    @Mock
    OfferService offerService;
    @Mock
    CalcService calcService;

    @Test
    void testForOffers() {
        LoanStatementRequestDTO loanStatementRequestDTO = new LoanStatementRequestDTO();

        ResponseEntity<List<LoanOfferDTO>> response = calculatorController.offers(loanStatementRequestDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }
    @Test
    void testForCalc() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO();

        ResponseEntity<CreditDTO> response = calculatorController.calc(scoringDataDTO);

        assertEquals(HttpStatus.OK.value(), response.getStatusCode().value());
    }

}