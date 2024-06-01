package ru.neoflex.calculator.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class CalcServiceTest {
    @Value("${app.base-rate}")
    public BigDecimal baseRate;
    @Mock
    public ScoringService scoringService;
    @InjectMocks
    private CalcService calcService;

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

        scoringData.setBirthdate(LocalDate.of(1997, 12, 18));
        scoringData.setGender(ScoringDataDTO.GenderEnum.FEMALE);
        scoringData.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED);
        scoringData.setDependentAmount(0);

        employment.setSalary(new BigDecimal("50000.00"));
        employment.setPosition(EmploymentDTO.PositionEnum.WORKER);
        employment.employmentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED);
        employment.setWorkExperienceCurrent(10);
        employment.setWorkExperienceTotal(20);
    }

    @Test
    void calculateTotalAmount() {
        Boolean isInsuranceEnabled = true;
        Boolean isSalaryClient = false;
        BigDecimal insurancePrice = BigDecimal.valueOf(100000.00);
        BigDecimal amount = BigDecimal.valueOf(1000000.00);
        BigDecimal actual = amount.add(insurancePrice);

        assertEquals(actual, calcService.calculateTotalAmount(isInsuranceEnabled, isSalaryClient, amount));
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

    }

}
