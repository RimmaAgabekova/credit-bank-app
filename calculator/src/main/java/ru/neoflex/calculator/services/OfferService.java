package ru.neoflex.calculator.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator.exceptions.ScoringException;
import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final CalcService calcService;
    private final ScoringService scoringService;

    public List<LoanOfferDTO> generateOffers(LoanStatementRequestDTO request) {

        long age = ChronoUnit.YEARS.between(request.getBirthdate(), LocalDate.now());
        if (age < 18) {
            throw new ScoringException("Отказ: Клиент не соответсвует возрасту выдачи кредитов");
        }

        return new ArrayList<>(List.of(
                createOffer(false, false, request),
                createOffer(false, true, request),
                createOffer(true, false, request),
                createOffer(true, true, request)
        ));
    }
    private LoanOfferDTO createOffer(Boolean isInsuranceEnabled, Boolean isSalaryClient, LoanStatementRequestDTO request) {

        BigDecimal totalAmount = calcService.calculateTotalAmount(isInsuranceEnabled, isSalaryClient, request.getAmount());
        BigDecimal currentRate = scoringService.getCurrentRateForOffers(isInsuranceEnabled, isSalaryClient);
        BigDecimal monthlyPayment = calcService.getMonthlyPayment(totalAmount, request.getTerm(),currentRate);

        return LoanOfferDTO.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(currentRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient)
                .build();
    }
}
