package ru.neoflex.deal.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Credit;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.CreditRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateServiceTest {
    @InjectMocks
    private CalculateService calculateService;
    @Mock
    private SelectService selectService;
    @Mock
    private StatementService statementService;
    @Mock
    private CreditRepository creditRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private CalculatorFeignClient calculatorFeignClient;

    @Test
    void calculateCredit_shouldThrowNullPointerException() {

        FinishRegistrationRequestDto finishRegistrationRequestDto = createFinishRegistrationRequestDto();

        when(selectService.getStatementById(any())).thenReturn(createStatement(createLoanOfferDto()));

        assertThrows(NullPointerException.class, () -> calculateService.calculateCredit(UUID.randomUUID(), finishRegistrationRequestDto));
    }

    @Test
    void calculateCreditAndFinishRegistrationShouldReturnFilledOptional() {

        FinishRegistrationRequestDto finishRegistrationRequest = createFinishRegistrationRequestDto();


        when(selectService.getStatementById(any())).thenReturn(createStatement(createLoanOfferDto()));
        when(creditRepository.save(any())).thenReturn(createCredit(createCreditDto()));
        when(clientRepository.findById(any())).thenReturn(Optional.ofNullable(createClient(createLoanStatementRequestDto())));

        Statement statement = createStatement(new LoanOfferDTO());

        calculateService.calculateCredit(statement.getStatementId(),
                finishRegistrationRequest);

        assertNotNull(statement);
        verify(selectService, times(1)).getStatementById(any());
        verify(clientRepository, times(1)).findById(any());
        verify(calculatorFeignClient, times(1)).calc(any());
        verify(statementService, times(1)).updateStatementStatus(any(), any());

    }

    private CreditDTO createCreditDto() {

        return CreditDTO.builder()
                .amount(new BigDecimal("100000"))
                .term(12)
                .build();
    }


    private Credit createCredit(CreditDTO creditDto) {

        return Credit.builder()
                .amount(creditDto.getAmount())
                .term(creditDto.getTerm())
                .creditStatus(String.valueOf(CreditStatus.CALCULATED))
                .build();
    }


    private Statement createStatement(LoanOfferDTO loanOfferDto) {

        return Statement.builder()
                .clientId(Client.builder()
                        .lastName("Ivanov")
                        .firstName("Ivan")
                        .middleName("Ivanovich")
                        .birthDate(LocalDate.of(1994, 1, 10))
                        .email("ivanov@mail.com")
                        .passportId(Passport.builder()
                                .series("1111")
                                .number("222222")
                                .build())
                        .build())
                .appliedOffer(loanOfferDto)
                .statusHistory(new ArrayList<>())
                .build();
    }

    private FinishRegistrationRequestDto createFinishRegistrationRequestDto() {

        return FinishRegistrationRequestDto.builder()
                .gender(FinishRegistrationRequestDto.GenderEnum.MALE)
                .maritalStatus(MaritalStatus.MARRIED)
                .dependentAmount(0)
                .passportIssueDate(LocalDate.of(2015, 1, 20))
                .passportIssueBranch("отделение по выдаче паспортов")
                .employment(EmploymentDTO.builder()
                        .employmentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED)
                        .employerINN("1234567890")
                        .salary(new BigDecimal("150000"))
                        .position(EmploymentDTO.PositionEnum.MID_MANAGER)
                        .workExperienceTotal(60)
                        .workExperienceCurrent(3)
                        .build())
                .accountNumber("1234567890")
                .build();
    }

    private LoanOfferDTO createLoanOfferDto() {
        return LoanOfferDTO.builder()
                .requestedAmount(new BigDecimal("100000"))
                .totalAmount(new BigDecimal("100000"))
                .term(12)
                .monthlyPayment(new BigDecimal("10000"))
                .rate(new BigDecimal("12"))
                .isInsuranceEnabled(false)
                .isSalaryClient(false)
                .build();
    }

    private LoanStatementRequestDTO createLoanStatementRequestDto() {
        return LoanStatementRequestDTO.builder()
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivanov@gmail.com")
                .birthdate(LocalDate.of(1990, 1, 1))
                .passportSeries("1111")
                .passportNumber("222222")
                .build();
    }

    private Client createClient(LoanStatementRequestDTO loanStatementRequestDto) {
        return Client.builder()
                .lastName(loanStatementRequestDto.getLastName())
                .firstName(loanStatementRequestDto.getFirstName())
                .middleName(loanStatementRequestDto.getMiddleName())
                .birthDate(loanStatementRequestDto.getBirthdate())
                .email(loanStatementRequestDto.getEmail())
                .passportId(Passport.builder()
                        .series(loanStatementRequestDto.getPassportSeries())
                        .number(loanStatementRequestDto.getPassportNumber())
                        .build())
                .build();
    }

}
