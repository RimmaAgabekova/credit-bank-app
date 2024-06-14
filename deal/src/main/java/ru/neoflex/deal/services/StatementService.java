package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.model.dto.StatementStatusHistoryDTO;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.StatementRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CalculatorFeignClient calculatorFeignClient;


    @Transactional
    public List<LoanOfferDTO> createStatement(LoanStatementRequestDTO request) {
        Client savedClient = createAndSaveClient(request);

        log.info("Создали и сохранили клиента - " + savedClient.getClientId());

        Statement savedStatement = createAndSaveStatement(savedClient);

        log.info("Создали заявку - " + savedStatement.getStatementId());

        List<LoanOfferDTO> loanOffers = calculatorFeignClient.offers(request);

        updateStatementStatus(savedStatement, StatementStatus.PREAPPROVAL);

        if (loanOffers != null) {
            loanOffers.forEach(offer -> offer.setStatementId(savedStatement.getStatementId()));
        }
        log.info("данные с микросеривиса calculator" + loanOffers);
        return loanOffers;
    }

    @Transactional
    public Statement createAndSaveStatement(Client client) {

        Statement statement = Statement.builder()
                .clientId(client)
                .creationDate(LocalDate.now())
                .statusHistory(new ArrayList<>())
                .build();

        return statementRepository.save(statement);
    }

    @Transactional
    public Client createAndSaveClient(LoanStatementRequestDTO request) {

        Client client = Client.builder()
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

        return clientRepository.save(client);
    }

    public void updateStatementStatus(Statement statement, StatementStatus status) {
        statement.setStatus(status);
        statement.getStatusHistory().add(StatementStatusHistoryDTO.builder()
                .status(status)
                .time(LocalDateTime.now())
                .changeType(StatementStatusHistoryDTO.ChangeTypeEnum.AUTOMATIC)
                .build());

    }

}
