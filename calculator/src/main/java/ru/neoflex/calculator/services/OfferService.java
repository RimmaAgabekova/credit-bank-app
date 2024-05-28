package ru.neoflex.calculator.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;


import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OfferService {
    private final CalcService calcService;

    public List<LoanOfferDTO> generateOffers(LoanStatementRequestDTO request) {
        return List.of(
                createOffer(false, false, request),
                createOffer(false, true, request),
                createOffer(true, false, request),
                createOffer(true, true, request)
        );
    }
    private LoanOfferDTO createOffer(Boolean isInsuranceEnabled, Boolean isSalaryClient, LoanStatementRequestDTO request) {

        BigDecimal totalAmount = CalcService.calculateTotalAmount(isInsuranceEnabled, isSalaryClient, request.getAmount());
        BigDecimal currentRate = ScoringService.getCurrentRate(isInsuranceEnabled, isSalaryClient);

        return new LoanOfferDTO()
                .statementId(UUID.randomUUID())
                .requestedAmount(request.getAmount())
                .totalAmount(totalAmount)
                .term(request.getTerm())
                .monthlyPayment(calcService.getMonthlyPayment(totalAmount, request.getTerm(),currentRate))
                .rate(currentRate)
                .isInsuranceEnabled(isInsuranceEnabled)
                .isSalaryClient(isSalaryClient);
    }
}
