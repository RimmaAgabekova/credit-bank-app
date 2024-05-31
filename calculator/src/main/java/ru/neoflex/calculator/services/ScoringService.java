package ru.neoflex.calculator.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.neoflex.calculator.exceptions.CalcException;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ScoringService {

    @Value("${app.base-rate}")
    public BigDecimal baseRate;

    public BigDecimal currentRate;

    public BigDecimal getCurrentRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        currentRate =  new BigDecimal(baseRate.toString());

        if (isInsuranceEnabled) {
            if (isSalaryClient) {
                currentRate = currentRate.subtract(BigDecimal.valueOf(1));
            } else {
                currentRate = currentRate.subtract(BigDecimal.valueOf(3));
            }
        }

        return currentRate;
    }

    public void scoring(ScoringDataDTO scoringData) throws CalcException {
        log.info("Начало скоринга");

        List<String> possibleRejection = new ArrayList<>();

        currentRate = baseRate;

        EmploymentDTO employment = scoringData.getEmployment();


        if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED) {
            possibleRejection.add("Отказ: Причина - безработный");
            throw new CalcException(possibleRejection.toString());
        } else {
            if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED) {
                log.info("Клиент самозанятый - увеличили ставку на 1 п.п.");
                currentRate.add(BigDecimal.valueOf(1));
            } else if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER) {
                log.info("Клиент владелец бизнеса - увеличили ставку на 2 п.п.");
                currentRate.add(BigDecimal.valueOf(2));
            }


            if (employment.getPosition() == EmploymentDTO.PositionEnum.MID_MANAGER) {
                log.info("Клиент менеджер среднего звена - снизили ставку на 2 п.п.");
                currentRate.subtract(BigDecimal.valueOf(2));
            }
            if (employment.getPosition() == EmploymentDTO.PositionEnum.TOP_MANAGER) {
                log.info("Клиент топ-менеджер - снизили ставку на 3 п.п.");
                currentRate.subtract(BigDecimal.valueOf(3));
            }

            if (scoringData.getAmount().compareTo(employment.getSalary().multiply(BigDecimal.valueOf(25))) > 0) {
                possibleRejection.add("Отказ: Причина - заработная плата не соответсвует сумме займа клиента");
                throw new CalcException(possibleRejection.toString());
            }

            if (employment.getWorkExperienceTotal() < 18) {
                possibleRejection.add("Отказ: Причина - общий стаж работы менее 18 месяцев");
                throw new CalcException(possibleRejection.toString());
            }

            if (employment.getWorkExperienceCurrent() < 3) {
                possibleRejection.add("Отказ - Причина - стаж работы на текущем месте работы менее 3 месяцев");
                throw new CalcException(possibleRejection.toString());
            }
        }

        if (scoringData.getMaritalStatus() == ScoringDataDTO.MaritalStatusEnum.MARRIED) {
            log.info("Клиент замужем/женат - снизили ставку на 3 п.п.");
            currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getMaritalStatus() == ScoringDataDTO.MaritalStatusEnum.DIVORCED) {
            log.info("Клиент разведен - увеличили ставку на 1 п.п.");
            currentRate.add(BigDecimal.valueOf(1));
        }


        long age = ChronoUnit.YEARS.between(scoringData.getBirthdate(), LocalDate.now());
        if (age < 20 || age > 65) {
            possibleRejection.add("Отказ: Причина - клиент не соответсвует возрасту выдачи кредитов");
            throw new CalcException(possibleRejection.toString());
        }

        if (scoringData.getGender() == ScoringDataDTO.GenderEnum.FEMALE && (age > 32 && age < 60)) {
            log.info("Клиент женщина (возраст от 32 до 60 - снизили ставку на 3 п.п.");
            currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == ScoringDataDTO.GenderEnum.MALE && (age > 30 && age < 55)) {
            log.info("Клиент мужчина (возраст от 30 до 55 - снизили ставку на 3 п.п.");
            currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == ScoringDataDTO.GenderEnum.NON_BINARY) {
            log.info("Клиент не бинарный - увеличили ставку на 7 п.п.");
            currentRate.add(BigDecimal.valueOf(7));
        }

        log.info("Конец скоринга");
    }}
