package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.*;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.CreditRepository;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CalculateService {

    private final CreditRepository creditRepository;
    private final CalculatorFeignClient calculatorFeignClient;
    private final SelectService selectService;
    private final ClientRepository clientRepository;
    private final StatementService statementService;

    @Transactional
    public void calculateCredit(UUID statementId, FinishRegistrationRequestDto finishRegistrationRequest) {
        log.info("Пришел finishRegistrationRequestDto - " + finishRegistrationRequest);

        log.info("Достаётся из БД заявка по statementId");
        Statement statement = selectService.getStatementById(statementId);

        log.info("ScoringDataDto насыщается информацией");
        ScoringDataDTO scoringData = formatScoringDataDto(finishRegistrationRequest, statement.getAppliedOffer(),
                statement.getClientId());
        log.info("Сформировали scoringData - " + scoringData);

        CreditDTO credit = calculatorFeignClient.calc(scoringData);
        log.info("Отправляется POST запрос в микросервис calculator");

        Credit savedCredit = creditRepository.save(formatCredit(credit));
        log.info("создаётся сущность Credit и сохраняется в базу со статусом CALCULATED");

        statementService.updateStatementStatus(statement, StatementStatus.CC_APPROVED);
        log.info("Обновляется статус, история статусов");

        updateClientData(statement.getClientId(), finishRegistrationRequest);
        log.info("Обновили данные по клиенту - " + statement.getClientId().getClientId());

        statement.setCreditId(savedCredit);
        log.info("Заявка сохраняется");
        log.info("Всё!)");
    }

    private Credit formatCredit(CreditDTO creditDto) {

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

    private ScoringDataDTO formatScoringDataDto(FinishRegistrationRequestDto finishRegistrationRequestDto,
                                                LoanOfferDTO loanOfferDTO,
                                                Client client) {
        ScoringDataDTO scoringDataObject = ScoringDataDTO.builder()
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

        scoringDataObject.setMaritalStatus(finishRegistrationRequestDto.getMaritalStatus());

        return scoringDataObject;
    }

    private void updateClientData(Client client, FinishRegistrationRequestDto finishRegistrationRequest) {
        client.setGender(finishRegistrationRequest.getGender().toString());
        client.setMaritalStatus(finishRegistrationRequest.getMaritalStatus().toString());
        client.setDependentAmount(finishRegistrationRequest.getDependentAmount());
        client.getPassportId().setIssueBranch(finishRegistrationRequest.getPassportIssueBranch());
        client.getPassportId().setIssueDate(finishRegistrationRequest.getPassportIssueDate());
        client.setAccountNumber(finishRegistrationRequest.getAccountNumber());
        client.setEmploymentId(Employment.builder()
                .status(finishRegistrationRequest.getEmployment().getEmploymentStatus())
                .employerInn(finishRegistrationRequest.getEmployment().getEmployerINN())
                .salary(finishRegistrationRequest.getEmployment().getSalary())
                .position(finishRegistrationRequest.getEmployment().getPosition())
                .workExperienceTotal(finishRegistrationRequest.getEmployment().getWorkExperienceTotal())
                .workExperienceCurrent(finishRegistrationRequest.getEmployment().getWorkExperienceCurrent())
                .build());

        clientRepository.save(client);

    }

}
