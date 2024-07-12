package ru.neoflex.gateway.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.neoflex.gateway.feign.StatementFeignClient;
import ru.neoflex.gateway.model.dto.LoanOfferDTO;
import ru.neoflex.gateway.model.dto.LoanStatementRequestDTO;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StatementService {
    private final StatementFeignClient statementFeignClient;

    public List<LoanOfferDTO> createLoanStatement(LoanStatementRequestDTO requestDTO) {
        return statementFeignClient.statement(requestDTO);
    }

    public void applyOffer(LoanOfferDTO loanOfferDTO) {
        statementFeignClient.offer(loanOfferDTO);
    }

}
