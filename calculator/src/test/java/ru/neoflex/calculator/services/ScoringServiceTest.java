package ru.neoflex.calculator.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import ru.neoflex.calculator.exceptions.CalcException;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ScoringServiceTest {

    static ScoringDataDTO scoringData;
    static EmploymentDTO employment;

    @Value("${app.base-rate}")
    public BigDecimal baseRate;


    @InjectMocks
    private static ScoringService scoringService;

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
    void getCurrentRate() {
        BigDecimal baseRate = BigDecimal.valueOf(16);
        BigDecimal currentRate = BigDecimal.valueOf(1);

        scoringService.baseRate = baseRate;

        Boolean isInsuranceEnabled = true;
        Boolean isSalaryClient = true;

        BigDecimal actual = baseRate.subtract(currentRate);

        assertEquals(actual, scoringService.getCurrentRate(isInsuranceEnabled, isSalaryClient));
    }

    @Test
    void testEmployment() {
        employment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED);

        String unEmploymentException = "Отказ: Причина - безработный";

        CalcException calcException = assertThrows(CalcException.class, () -> scoringService.scoring(scoringData));
        assertEquals(unEmploymentException, calcException.getMessage());
    }
    @Test
    void testWorkExperience() {
        employment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED);
        employment.setWorkExperienceTotal(15);

        String employmentTotalException = "Отказ: Причина - общий стаж работы менее 18 месяцев";
        String employmentCurrentException = "Отказ - Причина - стаж работы на текущем месте работы менее 3 месяцев";

        CalcException calcException = assertThrows(CalcException.class, () -> scoringService.scoring(scoringData));
        assertEquals(employmentTotalException, calcException.getMessage());

        employment.setWorkExperienceTotal(18);
        employment.setWorkExperienceCurrent(2);
        calcException = assertThrows(CalcException.class, ()-> scoringService.scoring(scoringData));
        assertEquals(employmentCurrentException, calcException.getMessage());
    }
    @Test
    void testEmploymentSalary(){
        String badSalary = "Отказ: Причина - заработная плата не соответсвует сумме займа клиента";

        scoringData.setAmount(new BigDecimal("1000000"));
        employment.setSalary(new BigDecimal("35000"));

        CalcException calcException = assertThrows(CalcException.class, () -> scoringService.scoring(scoringData));
        assertEquals(badSalary, calcException.getMessage());

    }

    @Test
    void testAgeClients(){
        String badSalary = "Отказ: Причина - клиент не соответсвует возрасту выдачи кредитов";
        scoringData.setBirthdate(LocalDate.of(2005, 12, 12));

        scoringService.baseRate = baseRate;

        CalcException calcException = assertThrows(CalcException.class, () -> scoringService.scoring(scoringData));
        assertEquals(badSalary, calcException.getMessage());

    }
}