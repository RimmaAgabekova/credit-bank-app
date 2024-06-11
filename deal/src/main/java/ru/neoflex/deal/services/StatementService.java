package ru.neoflex.deal.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.neoflex.deal.feign.CalculatorFeignClient;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Passport;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.ClientRepository;
import ru.neoflex.deal.repositories.PassportRepository;
import ru.neoflex.deal.repositories.StatementRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementService {

    private final ClientRepository clientRepository;
    private final StatementRepository statementRepository;
    private final CalculatorFeignClient calculatorFeignClient;


    @Transactional
    public List<LoanOfferDTO> createStatement(LoanStatementRequestDTO request) {
        Client savedClient = createClientByRequest(request);
        log.info("Создали клиента - "+savedClient.getClientId());

        Statement statement = new Statement();
        statement.setStatusHistory(new ArrayList<>());
        statement.setClientId(savedClient);

        Statement savedStatement = statementRepository.save(statement);
        log.info("Создали заявку - "+savedStatement.getStatementId());

        List<LoanOfferDTO> loanOffers = calculatorFeignClient.offers(request);

        if (loanOffers != null) {
            loanOffers.forEach(offer -> offer.setStatementId(savedStatement.getStatementId()));
        }
        return loanOffers;
    }

    private Client createClientByRequest(LoanStatementRequestDTO request) {
        log.info("Начинаем создание клента - "+request);

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


}
