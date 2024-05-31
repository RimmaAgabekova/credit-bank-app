package ru.neoflex.calculator.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.calculator.model.dto.*;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class CalcServiceTest {

    @Mock
    private ScoringService scoringService;

    @InjectMocks
    private CalcService calcService;


    @Test
    void calculateTotalAmount() {
        Boolean isInsuranceEnabled = true;
        Boolean isSalaryClient = false;
        BigDecimal insurancePrice = BigDecimal.valueOf(100000.00);
        BigDecimal amount = BigDecimal.valueOf(1000000.00);
        BigDecimal actual = amount.add(insurancePrice);

        assertEquals(actual, calcService.calculateTotalAmount(isInsuranceEnabled,isSalaryClient,amount));

    }

    @Test
    void getMonthlyPayment() {
        BigDecimal amount = new BigDecimal("150000.00");
        BigDecimal rate = new BigDecimal("15.00");
        int term = 24;
        BigDecimal actual = new BigDecimal("7273.00");

        assertEquals(actual, calcService.getMonthlyPayment(amount, term, rate));
    }

    @Test
    void calculateInterest() {
        BigDecimal remainingDebt = new BigDecimal("100000.00");
        BigDecimal rate = new BigDecimal("15.00");
        BigDecimal actual = new BigDecimal("1232.88");

        assertEquals(actual, calcService.calculateInterest(remainingDebt, rate));

    }

    @Test
    void calculateCredit() {
        ScoringDataDTO scoringData = new ScoringDataDTO();

        }
    }
