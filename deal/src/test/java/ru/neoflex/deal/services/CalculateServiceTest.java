package ru.neoflex.deal.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.deal.model.dto.EmploymentDTO;
import ru.neoflex.deal.model.dto.FinishRegistrationRequestDto;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.MaritalStatus;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateServiceTest {
    @InjectMocks
    private CalculateService calculateService;
    @Mock
    private StatementService statementService;

    @Test
    void calculateCredit_shouldThrowNullPointerException() {

        FinishRegistrationRequestDto finishRegistrationRequestDto = createFinishRegistrationRequestDto();

        when(statementService.getStatementById(any())).thenReturn(createStatement(createLoanOfferDto()));

        assertThrows(NullPointerException.class, () -> calculateService.calculateCredit(UUID.randomUUID(), finishRegistrationRequestDto));
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
}
