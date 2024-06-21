package ru.neoflex.statement.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.neoflex.statement.feign.StatementFeignClient;
import ru.neoflex.statement.model.dto.LoanOfferDTO;
import ru.neoflex.statement.model.dto.LoanStatementRequestDTO;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatementService {

    private final StatementFeignClient statementFeignClient;

    public List<LoanOfferDTO> calculationOfLoanTerms(LoanStatementRequestDTO request) {
        List<LoanOfferDTO> loanOffers = statementFeignClient.statement(request);
        log.info("Полученные предложения: {}", loanOffers);
        return loanOffers;
    }
    public void selectAnOffers(LoanOfferDTO loanOfferDTO) {
        statementFeignClient.select(loanOfferDTO);
    }

}
