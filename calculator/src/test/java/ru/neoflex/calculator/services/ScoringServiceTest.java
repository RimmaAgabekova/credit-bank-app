package ru.neoflex.calculator.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.neoflex.calculator.exceptions.ScoringException;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.neoflex.calculator.model.dto.EmploymentDTO.EmploymentStatusEnum.*;
import static ru.neoflex.calculator.model.dto.EmploymentDTO.PositionEnum.MID_MANAGER;
import static ru.neoflex.calculator.model.dto.EmploymentDTO.PositionEnum.TOP_MANAGER;
import static ru.neoflex.calculator.model.dto.ScoringDataDTO.GenderEnum.*;
import static ru.neoflex.calculator.model.dto.ScoringDataDTO.MaritalStatusEnum.DIVORCED;
import static ru.neoflex.calculator.model.dto.ScoringDataDTO.MaritalStatusEnum.MARRIED;

class ScoringServiceTest {
    private static ScoringService scoringService;
    static ScoringDataDTO scoringData;
    static EmploymentDTO employment;

    @BeforeAll
    public static void createData() {
        employment = new EmploymentDTO();
        scoringData = new ScoringDataDTO()
                .employment(employment);
        scoringService = new ScoringService();
        scoringService.baseRate = BigDecimal.valueOf(16);
    }

    @BeforeEach
    void setDataValues() {
        scoringData.setAmount(new BigDecimal("100000.00"));
        scoringData.setTerm(6);
        scoringData.setIsSalaryClient(false);
        scoringData.setIsInsuranceEnabled(true);

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
    void testCurrentRateForOffers() {
        BigDecimal actualForInsuranceSalaryClient = scoringService.baseRate.subtract(BigDecimal.valueOf(1));
        BigDecimal actualForInsurance = scoringService.baseRate.subtract(BigDecimal.valueOf(3));

        assertEquals(actualForInsuranceSalaryClient, scoringService.getCurrentRateForOffers(true, true));
        assertEquals(actualForInsurance, scoringService.getCurrentRateForOffers(true, false));
    }

    @Test
    void testEmployment() {
        employment.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED);

        String unEmploymentException = "Отказ: Причина - безработный";

        ScoringException scoringException = assertThrows(ScoringException.class, () -> scoringService.executeScoring(scoringData));
        assertEquals(unEmploymentException, scoringException.getMessage());
    }

    @Test
    public void testWhenEmploymentStatusSelfManager() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", MALE, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(SELF_EMPLOYED, "123", BigDecimal.valueOf(100000), TOP_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(10), actual);
    }

    @Test
    public void testWhenEmploymentStatusBusinessOwner() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", MALE, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(BUSINESS_OWNER, "123", BigDecimal.valueOf(100000), TOP_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(11), actual);
    }

    @Test
    public void testWhenGenderFemaleMarried() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", FEMALE, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), TOP_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(9), actual);
    }

    @Test
    public void testWhenGenderMale() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", MALE, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), TOP_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(9), actual);
    }

    @Test
    public void testWhenGenderNonBinary() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", NON_BINARY, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), TOP_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(16), actual);
    }

    @Test
    public void testWhenMaritalStatusDivorced() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", MALE, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", DIVORCED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), TOP_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(13), actual);
    }

    @Test
    public void testWhenPositionMidManager() {
        ScoringDataDTO scoringDataDTO = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", FEMALE, LocalDate.now().minusYears(30), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), MID_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringDataDTO);

        assertEquals(BigDecimal.valueOf(10), actual);
    }

    @Test
    void testWorkExperience() {
        employment.setEmploymentStatus(EMPLOYED);
        employment.setWorkExperienceTotal(15);

        String employmentTotalException = "Отказ: Причина - общий стаж работы менее 18 месяцев";
        String employmentCurrentException = "Отказ - Причина - стаж работы на текущем месте работы менее 3 месяцев";

        ScoringException scoringException = assertThrows(ScoringException.class, () -> scoringService.executeScoring(scoringData));
        assertEquals(employmentTotalException, scoringException.getMessage());

        employment.setWorkExperienceTotal(18);
        employment.setWorkExperienceCurrent(2);
        scoringException = assertThrows(ScoringException.class, () -> scoringService.executeScoring(scoringData));
        assertEquals(employmentCurrentException, scoringException.getMessage());
    }

    @Test
    void testEmploymentSalary() {
        String badSalary = "Отказ: Причина - заработная плата не соответсвует сумме займа клиента";

        scoringData.setAmount(new BigDecimal("1000000"));
        employment.setSalary(new BigDecimal("35000"));

        ScoringException scoringException = assertThrows(ScoringException.class, () -> scoringService.executeScoring(scoringData));
        assertEquals(badSalary, scoringException.getMessage());
    }

    @Test
    void testAgeClients() {
        String badSalary = "Отказ: Причина - клиент не соответсвует возрасту выдачи кредитов";
        scoringData.setBirthdate(LocalDate.of(2005, 12, 12));

        ScoringException scoringException = assertThrows(ScoringException.class, () -> scoringService.executeScoring(scoringData));
        assertEquals(badSalary, scoringException.getMessage());
    }

    @Test
    public void testAgeClientsWhenFemale() {
        ScoringDataDTO scoringData = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", FEMALE, LocalDate.now().minusYears(55), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), MID_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringData);

        assertEquals(BigDecimal.valueOf(7), actual);
    }

    @Test
    public void testAgeClientsWhenMale() {
        ScoringDataDTO scoringData = new ScoringDataDTO(BigDecimal.valueOf(300000), 6, "test", "test",
                "test", MALE, LocalDate.now().minusYears(45), "1234", "123456", LocalDate.now(), "test", MARRIED,
                0, new EmploymentDTO(EMPLOYED, "123", BigDecimal.valueOf(100000), MID_MANAGER, 20, 20), "12312", true, true);
        BigDecimal actual = scoringService.executeScoring(scoringData);

        assertEquals(BigDecimal.valueOf(7), actual);
    }
}