package ru.neoflex.calculator.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.PaymentScheduleElementDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalcService {

    public final ScoringService scoringService;
    private static final BigDecimal INSURANCE_PRICE = BigDecimal.valueOf(100000.00);
    private static final Integer BASE_PERIODS_AMOUNT_IN_YEAR = 12;
    private static final Integer DEFAULT_BINARY_SCALE = 2;
    private static final Integer DEFAULT_DECIMAL_SCALE = 10;
    private static final Integer QUANTITY_DAYS_IN_YEAR = 365;
    private static final Integer QUANTITY_DAYS_IN_MONTH = 30;


    public BigDecimal calculateTotalAmount(Boolean isInsuranceEnabled, Boolean isSalaryClient,
                                           BigDecimal amount) {
        BigDecimal totalAmount = amount;

        if (isInsuranceEnabled && !isSalaryClient) {
            totalAmount = totalAmount.add(INSURANCE_PRICE);
        }
        return totalAmount;
    }

    public BigDecimal getMonthlyPayment(BigDecimal amount, Integer term, BigDecimal rate) {
        log.info("Начали расчет ежемесячного платежа");

        BigDecimal monthRate = rate.divide(BigDecimal.valueOf(BASE_PERIODS_AMOUNT_IN_YEAR), DEFAULT_DECIMAL_SCALE, RoundingMode.CEILING);
        monthRate = monthRate.divide(BigDecimal.valueOf(100), DEFAULT_DECIMAL_SCALE, RoundingMode.CEILING);


        BigDecimal intermediateCoefficient = (BigDecimal.ONE.add(monthRate)).pow(term)
                .setScale(DEFAULT_DECIMAL_SCALE, RoundingMode.CEILING);

        BigDecimal annuityCoefficient = monthRate.multiply(intermediateCoefficient)
                .divide(intermediateCoefficient.subtract(BigDecimal.ONE), DEFAULT_DECIMAL_SCALE, RoundingMode.CEILING);


        BigDecimal monthlyPayment = amount.multiply(annuityCoefficient).setScale(DEFAULT_BINARY_SCALE, RoundingMode.CEILING);

        log.info("Закончили расчет ежемесячного платежа");
        return monthlyPayment;

    }

    public BigDecimal calculateInterest(BigDecimal remainingDebt, BigDecimal rate) {
        BigDecimal remainingDebtOfInterest = remainingDebt.multiply(rate.divide(BigDecimal.valueOf(100), DEFAULT_DECIMAL_SCALE, RoundingMode.CEILING));

        return remainingDebtOfInterest.multiply(BigDecimal.valueOf(QUANTITY_DAYS_IN_MONTH).divide(BigDecimal.valueOf(QUANTITY_DAYS_IN_YEAR), DEFAULT_DECIMAL_SCALE,
                RoundingMode.CEILING)).setScale(DEFAULT_BINARY_SCALE, RoundingMode.CEILING);
    }

    public List<PaymentScheduleElementDTO> getMonthlyPaymentSchedule(BigDecimal amount,
                                                                     BigDecimal rate,
                                                                     Integer term,
                                                                     BigDecimal monthlyPayment) {


        List<PaymentScheduleElementDTO> paymentSchedule = new ArrayList<>();
        BigDecimal remainingDebt = amount.setScale(DEFAULT_BINARY_SCALE, RoundingMode.CEILING);

        for (int i = 0; i < term - 1; i++) {

            BigDecimal interestPayment = calculateInterest(remainingDebt, rate).setScale(DEFAULT_BINARY_SCALE, RoundingMode.CEILING);
            BigDecimal debtPayment = monthlyPayment.subtract(interestPayment);

            remainingDebt = remainingDebt.subtract(debtPayment);

            paymentSchedule.add(PaymentScheduleElementDTO.builder()
                    .number(i + 1)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt)
                    .build());
        }

        BigDecimal lastInterestPayment = calculateInterest(paymentSchedule.get(paymentSchedule.size() - 1).getRemainingDebt(), rate)
                .setScale(DEFAULT_BINARY_SCALE, RoundingMode.CEILING);
        BigDecimal lastTotalPayment = paymentSchedule.get(paymentSchedule.size() - 1).getRemainingDebt().add(lastInterestPayment);

        paymentSchedule.add(PaymentScheduleElementDTO.builder()
                .number(paymentSchedule.size())
                .date(LocalDate.now().plusMonths(paymentSchedule.size()))
                .totalPayment(lastTotalPayment)
                .interestPayment(lastInterestPayment)
                .debtPayment(paymentSchedule.get(paymentSchedule.size() - 1).getRemainingDebt())
                .remainingDebt(new BigDecimal("0.00"))
                .build());

        return paymentSchedule;
    }

    public CreditDTO calculateCredit(ScoringDataDTO scoringData) {

        BigDecimal rate = scoringService.executeScoring(scoringData);

        BigDecimal psk = calculateTotalAmount(
                scoringData.getIsInsuranceEnabled(),
                scoringData.getIsSalaryClient(),
                scoringData.getAmount()
        );

        BigDecimal monthlyPayment = getMonthlyPayment(psk, scoringData.getTerm(), rate);

        return CreditDTO.builder()
                .amount(scoringData.getAmount())
                .term(scoringData.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(rate)
                .psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(getMonthlyPaymentSchedule(psk, rate, scoringData.getTerm(), monthlyPayment))
                .build();
    }
}
