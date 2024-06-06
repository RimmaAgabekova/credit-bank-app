package ru.neoflex.calculator.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.PaymentScheduleElementDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.neoflex.calculator.model.dto.EmploymentDTO.EmploymentStatusEnum.EMPLOYED;
import static ru.neoflex.calculator.model.dto.ScoringDataDTO.GenderEnum.FEMALE;
import static ru.neoflex.calculator.model.dto.ScoringDataDTO.MaritalStatusEnum.MARRIED;

@SpringBootTest
class CalcServiceTest {

    @Autowired
    CalcService calcService;

    static ScoringDataDTO scoringData;
    static EmploymentDTO employment;

    @BeforeAll
    public static void createData() {
        employment = new EmploymentDTO();
        scoringData = new ScoringDataDTO()
                .employment(employment);
    }

    @BeforeEach
    void setDataValues() {
        scoringData.setAmount(new BigDecimal("100000.00"));
        scoringData.setTerm(6);
        scoringData.setIsSalaryClient(false);
        scoringData.setIsInsuranceEnabled(true);

        scoringData.setFirstName("Тесто");
        scoringData.setLastName("Тестовый");
        scoringData.setMiddleName("Тесточко");

        scoringData.setBirthdate(LocalDate.of(1997, 12, 18));
        scoringData.setGender(FEMALE);
        scoringData.setMaritalStatus(MARRIED);
        scoringData.setDependentAmount(0);

        employment.setSalary(new BigDecimal("50000.00"));
        employment.setPosition(EmploymentDTO.PositionEnum.WORKER);
        employment.employmentStatus(EMPLOYED);
        employment.setWorkExperienceCurrent(10);
        employment.setWorkExperienceTotal(20);
    }

    @Test
    void calculateTotalAmount() {
        BigDecimal totalAmount = calcService.calculateTotalAmount(true, false,
                new BigDecimal("100000").setScale(2, RoundingMode.CEILING));

        assertEquals(new BigDecimal("200000.00"), totalAmount);
    }

    @Test
    void calculateMonthlyPayment() {
        BigDecimal monthlyPayment = calcService.getMonthlyPayment(
                new BigDecimal("1000000"), 12, new BigDecimal("15"));

        assertEquals(new BigDecimal("90258.32"), monthlyPayment);
    }

    @Test
    void calculateInterest() {
        BigDecimal remainingDebt = new BigDecimal("100000.00");
        BigDecimal rate = new BigDecimal("15.00");
        BigDecimal actual = new BigDecimal("1232.88");

        assertEquals(actual, calcService.calculateInterest(remainingDebt, rate));
    }

    @Test
    void getPaymentSchedule() {

        List<PaymentScheduleElementDTO> paymentSchedule = calcService.getMonthlyPaymentSchedule(
                new BigDecimal("1000000"), new BigDecimal("10"), 15, new BigDecimal("87915.89"));

        assertEquals(15, paymentSchedule.size());

        PaymentScheduleElementDTO lastPayment = paymentSchedule.get(paymentSchedule.size() - 1);

        assertEquals(14, lastPayment.getNumber());
        assertEquals(paymentSchedule.get(0).getDate().plusMonths(14), lastPayment.getDate());
        assertEquals(new BigDecimal("0.00"), lastPayment.getRemainingDebt());
        assertEquals(lastPayment.getTotalPayment(), lastPayment.getDebtPayment().add(lastPayment.getInterestPayment()));
    }

    @Test
    void getCreditDtoShouldReturnFilledObject() {

        CreditDTO creditDto = calcService.calculateCredit(scoringData);

        assertNotNull(creditDto.getAmount());
        assertNotEquals(0, creditDto.getTerm());
        assertNotNull(creditDto.getMonthlyPayment());
        assertNotNull(creditDto.getRate());
        assertNotNull(creditDto.getPsk());
        assertEquals(scoringData.getIsInsuranceEnabled(), creditDto.getIsInsuranceEnabled());
        assertEquals(scoringData.getIsSalaryClient(), creditDto.getIsSalaryClient());
        assertNotNull(creditDto.getPaymentSchedule());
    }


}
