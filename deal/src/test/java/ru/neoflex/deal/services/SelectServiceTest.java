package ru.neoflex.deal.services;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SelectServiceTest {
    @Mock
    private StatementService statementService;
    @Mock
    private StatementRepository statementRepository;
    @InjectMocks
    private SelectService selectService;

    @Test
    void getStatementByIdShouldThrowExceptionWhenStatement() {

        when(statementRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> selectService.getStatementById(UUID.randomUUID()));
    }

    @Test
    void getStatementByIdShouldReturnNotNull() {

        when(statementRepository.findById(any())).thenReturn(Optional.of(new Statement()));
        assertNotNull(selectService.getStatementById(UUID.randomUUID()));
        verify(statementRepository, times(1)).findById(any());
    }

    @Test
    void updateStatementSchedule() {
        when(statementRepository.findById(any())).thenReturn(Optional.ofNullable(createStatement(createLoanOfferDto())));
        when(statementRepository.save(any())).thenReturn(createStatement(createLoanOfferDto()));
        selectService.updateStatement(createLoanOfferDto());
        verify(statementService, times(1)).updateStatementStatus(any(), any());
        assertEquals(createStatement(createLoanOfferDto()), statementRepository.save(any()));
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




