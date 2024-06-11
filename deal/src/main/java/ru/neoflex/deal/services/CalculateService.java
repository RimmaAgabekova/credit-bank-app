package ru.neoflex.deal.services;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.*;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.CreditRepository;
import ru.neoflex.deal.repositories.StatementRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalculateService {

    private final StatementRepository statementRepository;
    private final ClientRepository clientRepository;
    private final CreditRepository creditRepository;
    private final CalculatorFeignClient calculatorFeignClient;

    private final SelectService selectService;


    @Transactional
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {

        log.info("Запустили calculateCredit");
        log.info("statementId - " + statementId);
        log.info("finishRegistrationRequestDto - " + finishRegistrationRequestDto);

        Statement statement = statementRepository.findById(statementId).orElseThrow();
        log.info("Нашли заявку");

        Client client = clientRepository.findById(statement.getClientId().getClientId()).orElseThrow();
        log.info("Нашли клиента");

        ScoringDataDTO scoringData = buildScoringDataDto(finishRegistrationRequestDto, statement.getAppliedOffer(),
                statement.getClientId());
        log.info("Сформировали scoringData - " + scoringData);

        CreditDTO credit = calculatorFeignClient.calc(scoringData);
        log.info("Вернулся credit");

        Credit savedCredit = creditRepository.save(buildCredit(credit));
        log.info("Сформировали сущность credit и записали в базу");

        selectService.updateStatementStatusById(statement, StatementStatusHistoryDTO.StatusEnum.APPROVED);
        log.info("Обновили статус по заявке");

        statement.setCreditId(savedCredit);
        log.info("Всё!)");
    }

    private Credit buildCredit(CreditDTO creditDto) {

        return Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .monthlyPayment(creditDto.getMonthlyPayment())
                .rate(creditDto.getRate())
                .psk(creditDto.getPsk())
                .paymentSchedule(creditDto.getPaymentSchedule())
                .isInsuranceEnabled(creditDto.getIsInsuranceEnabled())
                .isSalaryClient(creditDto.getIsSalaryClient())
                .creditStatus(String.valueOf(CreditStatus.CALCULATED))
                .build();
    }

    private ScoringDataDTO buildScoringDataDto(FinishRegistrationRequestDto finishRegistrationRequestDto,
                                               LoanOfferDTO loanOfferDTO,
                                               Client client) {

        return ScoringDataDTO.builder()
                .amount(loanOfferDTO.getRequestedAmount())
                .term(loanOfferDTO.getTerm())
                .firstName(client.getFirstName())
                .lastName(client.getLastName())
                .middleName(client.getMiddleName())
                .birthdate(client.getBirthDate())
                .passportSeries(client.getPassportId().getSeries())
                .passportNumber(client.getPassportId().getNumber())
                .passportIssueDate(finishRegistrationRequestDto.getPassportIssueDate())
                .passportIssueBranch(finishRegistrationRequestDto.getPassportIssueBranch())
                .maritalStatus(finishRegistrationRequestDto.getMaritalStatus())
                .dependentAmount(finishRegistrationRequestDto.getDependentAmount())
                .employment(EmploymentDTO.builder()
                        .employmentStatus(finishRegistrationRequestDto.getEmployment().getEmploymentStatus())
                        .employerINN(finishRegistrationRequestDto.getEmployment().getEmployerINN())
                        .salary(finishRegistrationRequestDto.getEmployment().getSalary())
                        .position(finishRegistrationRequestDto.getEmployment().getPosition())
                        .workExperienceTotal(finishRegistrationRequestDto.getEmployment().getWorkExperienceTotal())
                        .workExperienceCurrent(finishRegistrationRequestDto.getEmployment().getWorkExperienceCurrent())
                        .build())
                .accountNumber(finishRegistrationRequestDto.getAccountNumber())
                .isInsuranceEnabled(loanOfferDTO.getIsInsuranceEnabled())
                .isSalaryClient(loanOfferDTO.getIsSalaryClient())
                .build();
    }

}
