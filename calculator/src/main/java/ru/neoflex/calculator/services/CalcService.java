package ru.neoflex.calculator.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    private static final BigDecimal INSURANCE_PRICE = BigDecimal.valueOf(100000.00);
    private static BigDecimal totalAmount;

    private final ScoringService scoringService;

    public BigDecimal calculateTotalAmount(Boolean isInsuranceEnabled, Boolean isSalaryClient,
                                           BigDecimal amount) {
        totalAmount = amount;

        if (isInsuranceEnabled && !isSalaryClient) {
            totalAmount = totalAmount.add(INSURANCE_PRICE);
        }
        return totalAmount;
    }

    public BigDecimal getMonthlyPayment(BigDecimal totalAmount, Integer term, BigDecimal rate) {
        log.info("Начали расчет ежемесячного платежа");

        BigDecimal monthRate = rate.divide(BigDecimal.valueOf(12), 10, RoundingMode.CEILING);
        monthRate = monthRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.CEILING);
        log.info("Месячная ставка - {}", monthRate);

        BigDecimal intermediateCoefficient = (BigDecimal.valueOf(1).add(monthRate)).pow(term)
                .setScale(10, RoundingMode.CEILING);

        BigDecimal annuityCoefficient = monthRate.multiply(intermediateCoefficient)
                .divide(intermediateCoefficient.subtract(BigDecimal.valueOf(1)), 10, RoundingMode.CEILING);
        log.info("Коэффициент аннуитета - {}", annuityCoefficient);

        BigDecimal monthlyPayment = totalAmount.multiply(annuityCoefficient).setScale(2, RoundingMode.CEILING);

        log.info("Закончили расчет ежемесячного платежа");
        return monthlyPayment;

    }

    public BigDecimal calculateInterest(BigDecimal remainingDebt, BigDecimal rate) {
        BigDecimal remainingDebtOfInterest = remainingDebt.multiply(rate.divide(BigDecimal.valueOf(100), 10, RoundingMode.CEILING));

        return remainingDebtOfInterest.multiply(BigDecimal.valueOf(30).divide(BigDecimal.valueOf(365), 10, RoundingMode.CEILING)).setScale(2, RoundingMode.CEILING);
    }

    private List<PaymentScheduleElementDTO> getMonthlyPaymentSchedule(BigDecimal totalAmount,
                                                                     BigDecimal rate,
                                                                     Integer term,
                                                                     BigDecimal monthlyPayment) {

        List<PaymentScheduleElementDTO> paymentSchedule = new ArrayList<>();
        BigDecimal remainingDebt = totalAmount.setScale(2, RoundingMode.CEILING);

        for (int i = 0; i < term - 1; i++) {

            BigDecimal interestPayment = calculateInterest(remainingDebt, rate).setScale(2, RoundingMode.CEILING);
            BigDecimal debtPayment =  monthlyPayment.subtract(interestPayment);

            remainingDebt = remainingDebt.subtract(debtPayment);

            paymentSchedule.add(new PaymentScheduleElementDTO()
                    .number(i + 1)
                    .date(LocalDate.now().plusMonths(i))
                    .totalPayment(monthlyPayment)
                    .interestPayment(interestPayment)
                    .debtPayment(debtPayment)
                    .remainingDebt(remainingDebt));
        }

        //Последний платеж
        BigDecimal lastInterestPayment = calculateInterest(paymentSchedule.get(paymentSchedule.size()-1).getRemainingDebt(), rate).setScale(2, RoundingMode.CEILING);
        BigDecimal lastTotalPayment = paymentSchedule.get(paymentSchedule.size()-1).getRemainingDebt().add(lastInterestPayment);

        paymentSchedule.add(new PaymentScheduleElementDTO()
                .number(paymentSchedule.size())
                .date(LocalDate.now().plusMonths(paymentSchedule.size()))
                .totalPayment(lastTotalPayment)
                .interestPayment(lastInterestPayment)
                .debtPayment(paymentSchedule.get(paymentSchedule.size()-1).getRemainingDebt())
                .remainingDebt(new BigDecimal("0.00")));

        return paymentSchedule;
    }

    public CreditDTO calculateCredit(ScoringDataDTO scoringData) {
        BigDecimal psk = calculateTotalAmount(scoringData.getIsInsuranceEnabled(),scoringData.getIsSalaryClient(),
                scoringData.getAmount());

        scoringService.scoring(scoringData);

        BigDecimal monthlyPayment = getMonthlyPayment(totalAmount, scoringData.getTerm(),
                scoringService.getCurrentRate(scoringData.getIsInsuranceEnabled(), scoringData.getIsSalaryClient()));


        if (scoringService.currentRate == null) {
            return null;
        }

        return new CreditDTO()
                .amount(totalAmount)
                .term(scoringData.getTerm())
                .monthlyPayment(monthlyPayment)
                .rate(scoringService.currentRate)
                .psk(psk)
                .isInsuranceEnabled(scoringData.getIsInsuranceEnabled())
                .isSalaryClient(scoringData.getIsSalaryClient())
                .paymentSchedule(getMonthlyPaymentSchedule(totalAmount, scoringService.currentRate, scoringData.getTerm(), monthlyPayment));

    }
}
