package ru.neoflex.calculator.services;


import org.springframework.stereotype.Service;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;


import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

//    Правила скоринга (можно придумать новые правила или изменить существующие):
//Рабочий статус: Безработный → отказ; Самозанятый → ставка увеличивается на 1; Владелец бизнеса → ставка увеличивается на 2
//Позиция на работе: Менеджер среднего звена → ставка уменьшается на 2; Топ-менеджер → ставка уменьшается на 3
//Сумма займа больше, чем 25 зарплат → отказ
//Семейное положение: Замужем/женат → ставка уменьшается на 3; Разведен → ставка увеличивается на 1
//Возраст менее 20 или более 65 лет → отказ
//Пол: Женщина, возраст от 32 до 60 лет → ставка уменьшается на 3; Мужчина, возраст от 30 до 55 лет → ставка уменьшается на 3;
// Не бинарный → ставка увеличивается на 7
//Стаж работы: Общий стаж менее 18 месяцев → отказ; Текущий стаж менее 3 месяцев → отказ
@Service
public class ScoringService {
    public static BigDecimal BASE_RATE = BigDecimal.valueOf(16.00);

    public static BigDecimal currentRate;

    public static BigDecimal getCurrentRate(Boolean isInsuranceEnabled, Boolean isSalaryClient) {
        currentRate = new BigDecimal(String.valueOf(BASE_RATE));

        if (isInsuranceEnabled) {
            if (isSalaryClient) {
                currentRate = currentRate.subtract(BigDecimal.valueOf(1));
            } else {
                currentRate = currentRate.subtract(BigDecimal.valueOf(3));
            }
        }

        return currentRate;
    }

    public void scoring(ScoringDataDTO scoringData) throws Exception {
        List<String> possibleRejection = new ArrayList<>();

        currentRate = BASE_RATE;

        EmploymentDTO employment = scoringData.getEmployment();

        if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.UNEMPLOYED) {
            possibleRejection.add("Отказ: Причина - безработный");
        } else {
            if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.SELF_EMPLOYED) {
                currentRate.add(BigDecimal.valueOf(1));
            } else if (employment.getEmploymentStatus() == EmploymentDTO.EmploymentStatusEnum.BUSINESS_OWNER) {
                currentRate.add(BigDecimal.valueOf(2));
            }

            if (employment.getPosition() == EmploymentDTO.PositionEnum.MID_MANAGER) {
                currentRate.subtract(BigDecimal.valueOf(2));
            }
            if (employment.getPosition() == EmploymentDTO.PositionEnum.TOP_MANAGER) {
                currentRate.subtract(BigDecimal.valueOf(3));
            }

            if (scoringData.getAmount().compareTo(employment.getSalary().multiply(BigDecimal.valueOf(25))) > 0) {
                possibleRejection.add("Отказ: Причина - заработная плата не соответсвует сумме займа клиента");
            }

            if (employment.getWorkExperienceTotal() < 18) {
                possibleRejection.add("Отказ: Причина - общий стаж работы менее 18 месяцев");
            }

            if (employment.getWorkExperienceCurrent() < 3) {
                possibleRejection.add("Отказ - Причина - стаж работы на текущем месте работы менее 3 месяцев");
            }
        }

        if (scoringData.getMaritalStatus() == ScoringDataDTO.MaritalStatusEnum.MARRIED) {
            currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getMaritalStatus() == ScoringDataDTO.MaritalStatusEnum.DIVORCED) {
            currentRate.add(BigDecimal.valueOf(1));
        }


        long age = ChronoUnit.YEARS.between(scoringData.getBirthdate(), LocalDate.now());
        if (age < 20 || age > 65) {
            possibleRejection.add("Отказ: Причина - клиент не соответсвует возрасту выдачи кредитов");
        }

        if (scoringData.getGender() == ScoringDataDTO.GenderEnum.FEMALE && (age > 32 && age < 60)) {
            currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == ScoringDataDTO.GenderEnum.MALE && (age > 30 && age < 55)) {
            currentRate.subtract(BigDecimal.valueOf(3));
        } else if (scoringData.getGender() == ScoringDataDTO.GenderEnum.NON_BINARY) {
            currentRate.add(BigDecimal.valueOf(7));
        }

        if (possibleRejection.size() > 0) {
            throw new Exception(possibleRejection.toString());
        }

    }
}
