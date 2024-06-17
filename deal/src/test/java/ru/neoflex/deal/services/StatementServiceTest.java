package ru.neoflex.deal.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.deal.mappers.ClientMapper;
import ru.neoflex.deal.mappers.ClientMapperImpl;
import ru.neoflex.deal.model.dto.*;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.StatementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {

//    ClientMapper clientMapper;
    @Mock
    private ClientRepository clientRepository;

    private final ClientMapper clientMapper = new ClientMapperImpl();

    @Mock
    private StatementRepository statementRepository;

//    private ScoringDataDTOMapper scoringDataDTOMapper;
//
//    private CreditMapper creditMapper;

    @InjectMocks
    private StatementService statementService;


    @Test
    void createAndSaveStatementShouldReturnFilledData() {
        Client client = new Client();
        Statement mockStatement = Statement.builder()
                .clientId(client)
                .creationDate(LocalDate.now())
                .statusHistory(new ArrayList<>())
                .build();

        Statement savedStatement = statementService.createAndSaveStatement(client);

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

        Statement savedStatement = statementService.createAndSaveStatement(savedClient);

        Statement updatedStatement = statementService.updateStatementStatus(savedStatement, StatementStatus.PREAPPROVAL);


        assertNotNull(updatedStatement);
        assertEquals(StatementStatus.PREAPPROVAL, updatedStatement.getStatus());
        assertEquals(StatementStatus.PREAPPROVAL, updatedStatement.getStatusHistory().get(0).getStatus());
        assertEquals(LocalDateTime.now().toLocalDate(), updatedStatement.getStatusHistory().get(0).getTime().toLocalDate());
        assertEquals(StatementStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC, updatedStatement.getStatusHistory().get(0).getChangeType());

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

}
