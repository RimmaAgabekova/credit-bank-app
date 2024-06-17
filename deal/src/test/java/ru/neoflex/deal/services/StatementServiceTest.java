package ru.neoflex.deal.services;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.model.dto.StatementStatusHistoryDTO;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {
    @InjectMocks
    private StatementService statementService;
    @Mock
    private StatementRepository statementRepository;

    @Test
    void createAndSaveStatementShouldReturnFilledData() {
        Client client = new Client();
        Statement mockStatement = Statement.builder()
                .clientId(client)
                .creationDate(LocalDate.now())
                .statusHistory(new ArrayList<>())
                .build();

        Statement savedStatement = statementService.buildStatement(client);

        assertNotNull(savedStatement);
        assertEquals(mockStatement.getStatementId(), savedStatement.getStatementId());
        assertEquals(mockStatement.getClientId().getClientId(), savedStatement.getClientId().getClientId());
        assertEquals(mockStatement.getCreationDate(), savedStatement.getCreationDate());
    }

    @Test
    void createAndSaveClientShouldReturnFilledData() {

        LoanStatementRequestDTO request = createLoanStatementRequestDto();
        Client mockClient = createClient(request);

        Client savedClient = createClient(request);

        assertNotNull(savedClient);

        assertEquals(mockClient.getFirstName(), savedClient.getFirstName());
        assertEquals(mockClient.getLastName(), savedClient.getLastName());
        assertEquals(mockClient.getMiddleName(), savedClient.getMiddleName());
        assertEquals(mockClient.getEmail(), savedClient.getEmail());
        assertEquals(mockClient.getBirthDate(), savedClient.getBirthDate());
        assertEquals(mockClient.getPassportId().getSeries(), savedClient.getPassportId().getSeries());
        assertEquals(mockClient.getPassportId().getNumber(), savedClient.getPassportId().getNumber());
    }

    @Test
    void updateStatementWithSelectedOffer() {

        Client savedClient = new Client();

        Statement savedStatement = statementService.buildStatement(savedClient);

        Statement updatedStatement = statementService.updateStatementStatus(savedStatement, StatementStatus.PREAPPROVAL);

        assertNotNull(updatedStatement);
        assertEquals(StatementStatus.PREAPPROVAL, updatedStatement.getStatus());
        assertEquals(StatementStatus.PREAPPROVAL, updatedStatement.getStatusHistory().get(0).getStatus());
        assertEquals(LocalDateTime.now().toLocalDate(), updatedStatement.getStatusHistory().get(0).getTime().toLocalDate());
        assertEquals(StatementStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC, updatedStatement.getStatusHistory().get(0).getChangeType());
    }
    @Test
    void getStatementByIdShouldThrowExceptionWhenStatement() {

        when(statementRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> statementService.getStatementById(UUID.randomUUID()));
    }
    @Test
    void getStatementByIdShouldReturnNotNull() {

        when(statementRepository.findById(any())).thenReturn(Optional.of(new Statement()));
        assertNotNull(statementService.getStatementById(UUID.randomUUID()));
        verify(statementRepository, times(1)).findById(any());
    }
    @Test
    void updateStatementSchedule() {
        when(statementRepository.findById(any())).thenReturn(Optional.ofNullable(createStatement(createLoanOfferDto())));
        when(statementRepository.save(any())).thenReturn(createStatement(createLoanOfferDto()));
        statementService.updateStatement(createLoanOfferDto());

        assertEquals(createStatement(createLoanOfferDto()), statementRepository.save(any()));
    }

    private LoanStatementRequestDTO createLoanStatementRequestDto() {
        return LoanStatementRequestDTO.builder()
                .term(12)
                .firstName("Ivan")
                .lastName("Ivanov")
                .middleName("Ivanovich")
                .email("ivanov@mail.ru")
                .birthdate(LocalDate.of(1994, 1, 10))
                .passportSeries("1234")
                .passportNumber("123456")
                .build();
    }

    private Client createClient(LoanStatementRequestDTO request) {
        return Client.builder()
                .lastName(request.getLastName())
                .firstName(request.getFirstName())
                .middleName(request.getMiddleName())
                .birthDate(request.getBirthdate())
                .email(request.getEmail())
                .passportId(Passport.builder()
                        .series(request.getPassportSeries())
                        .number(request.getPassportNumber())
                        .build())
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
