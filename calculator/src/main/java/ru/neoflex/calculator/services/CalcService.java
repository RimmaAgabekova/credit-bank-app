package ru.neoflex.calculator.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.PaymentScheduleElementDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalcService {

    private static BigDecimal INSURANCE_PRICE = BigDecimal.valueOf(100000.00);
    private static BigDecimal totalAmount;

    private final ScoringService scoringService;

    public static BigDecimal calculateTotalAmount(Boolean isInsuranceEnabled, Boolean isSalaryClient,
                                                  BigDecimal amount) {
        totalAmount = amount;

        if (isInsuranceEnabled && !isSalaryClient) {
            totalAmount = totalAmount.add(INSURANCE_PRICE);
        }

        return totalAmount;
    }

    public BigDecimal getMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {

        BigDecimal monthRate = rate.divide(BigDecimal.valueOf(12), 2, RoundingMode.CEILING);
        monthRate = monthRate.divide(BigDecimal.valueOf(100), 2, RoundingMode.CEILING);

        BigDecimal intermediateCoefficient = (BigDecimal.valueOf(1).add(monthRate)).pow(term)
                .setScale(2, RoundingMode.CEILING);

        BigDecimal annuityCoefficient = monthRate.multiply(intermediateCoefficient)
                .divide(intermediateCoefficient.subtract(BigDecimal.valueOf(1)), RoundingMode.CEILING);

        BigDecimal monthlyPayment = totalAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.CEILING);

        return monthlyPayment;

    }

    public BigDecimal calculateInterest(BigDecimal remainingDebt, BigDecimal rate) {
        BigDecimal monthlyRateAbsolute = rate.divide(BigDecimal.valueOf(100), RoundingMode.CEILING);

        BigDecimal monthlyRate = monthlyRateAbsolute.divide(new BigDecimal(12), 2, RoundingMode.CEILING);

        return remainingDebt.multiply(monthlyRate);
    }


    public List<PaymentScheduleElementDTO> getMonthlyPaymentSchedule(BigDecimal totalAmount,
                                                                     BigDecimal rate,
                                                                     Integer term,
                                                                     BigDecimal monthlyPayment) {

        List<PaymentScheduleElementDTO> paymentSchedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount.setScale(2, RoundingMode.CEILING);


        for (int i = 0; i < term; i++) {

            BigDecimal interestPayment = calculateInterest(remainingDebt, rate).setScale(2, RoundingMode.CEILING);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);

            remainingDebt = remainingDebt.subtract(debtPayment);

            paymentSchedule.add(new PaymentScheduleElementDTO()
                    .number(i + 1)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt));

        }
        return paymentSchedule;
    }

    public CreditDTO calculateCredit(ScoringDataDTO scoringData) throws Exception {
        BigDecimal psk = calculateTotalAmount(
                scoringData.getIsInsuranceEnabled(),
                scoringData.getIsSalaryClient(),
                scoringData.getAmount());

        scoringService.scoring(scoringData);


        BigDecimal monthlyPayment = getMonthlyPayment(totalAmount, scoringData.getTerm(), ScoringService.currentRate);

        if (ScoringService.currentRate == null) {
            return null;
        }

        return new CreditDTO()
                .amount(totalAmount)
                .term(scoringData.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(ScoringService.currentRate)
                .psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(getMonthlyPaymentSchedule(totalAmount, ScoringService.currentRate, scoringData.getTerm(), monthlyPayment));

    }
}
