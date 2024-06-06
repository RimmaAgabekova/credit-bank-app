package ru.neoflex.calculator.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator.exceptions.ScoringException;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Slf4j
@Service
public class ScoringService {
    @Value("${app.base-rate}")
    public BigDecimal baseRate;

    public BigDecimal getCurrentRateForOffers(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        BigDecimal rate = baseRate;

        if (isInsuranceEnabled) {
            if (isSalaryClient) {
                rate = rate.subtract(BigDecimal.valueOf(1));
            } else {
                rate = rate.subtract(BigDecimal.valueOf(3));
            }
        }
        return rate;
    }

    public BigDecimal executeScoring(ScoringDataDTO scoringData) {
        log.info("Начало скоринга");

        BigDecimal currentRate = baseRate;

        if (scoringData.getIsInsuranceEnabled()) {
            if (scoringData.getIsSalaryClient()) {
                currentRate = currentRate.subtract(BigDecimal.valueOf(1));
            } else {
                currentRate = currentRate.subtract(BigDecimal.valueOf(3));
            }
        }

        EmploymentDTO employment = scoringData.getEmployment();

        if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED) {
            throw new ScoringException("Отказ: Причина - безработный");
        } else {
            if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED) {
                currentRate = currentRate.add(BigDecimal.valueOf(1));
            } else if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER) {
                currentRate = currentRate.add(BigDecimal.valueOf(2));
            }

            if (employment.getPosition() == EmploymentDTO.PositionEnum.MID_MANAGER) {
                currentRate = currentRate.subtract(BigDecimal.valueOf(2));
            }
            if (employment.getPosition() == EmploymentDTO.PositionEnum.TOP_MANAGER) {
                currentRate = currentRate.subtract(BigDecimal.valueOf(3));
            }

            if (scoringData.getAmount().compareTo(employment.getSalary().multiply(BigDecimal.valueOf(25))) > 0) {
                throw new ScoringException("Отказ: Причина - заработная плата не соответсвует сумме займа клиента");
            }

            if (employment.getWorkExperienceTotal() < 18) {
                throw new ScoringException("Отказ: Причина - общий стаж работы менее 18 месяцев");
            }

            if (employment.getWorkExperienceCurrent() < 3) {
                throw new ScoringException("Отказ - Причина - стаж работы на текущем месте работы менее 3 месяцев");
            }
        }

        if (scoringData.getMaritalStatus() == ScoringDataDTO.MaritalStatusEnum.MARRIED) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getMaritalStatus() == ScoringDataDTO.MaritalStatusEnum.DIVORCED) {
            currentRate = currentRate.add(BigDecimal.valueOf(1));
        }

        long age = ChronoUnit.YEARS.between(scoringData.getBirthdate(), LocalDate.now());
        if (age < 20 || age > 65) {
            throw new ScoringException("Отказ: Причина - клиент не соответсвует возрасту выдачи кредитов");
        }

        if (scoringData.getGender() == ScoringDataDTO.GenderEnum.FEMALE && (age > 32 && age < 60)) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == ScoringDataDTO.GenderEnum.MALE && (age > 30 && age < 55)) {
            currentRate = currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == ScoringDataDTO.GenderEnum.NON_BINARY) {
            currentRate = currentRate.add(BigDecimal.valueOf(7));
        }

        log.info("Конец скоринга");
        return currentRate;
    }

}
