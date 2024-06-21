package ru.neoflex.statement.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.statement.feign.StatementFeignClient;
import ru.neoflex.statement.model.dto.LoanOfferDTO;
import ru.neoflex.statement.model.dto.LoanStatementRequestDTO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatementServiceTest {
    @InjectMocks
    StatementService statementService;
    @Mock
    StatementFeignClient statementFeignClient;

    @Test
    void calculationOfLoanTerms() {
        LoanStatementRequestDTO loanApplicationRequestDTO = new LoanStatementRequestDTO();
        List<LoanOfferDTO> loanOfferDTO = new ArrayList<>();
        when(statementFeignClient.statement(loanApplicationRequestDTO)).thenReturn(loanOfferDTO);
        List<LoanOfferDTO> resultFromApplicationService = statementService.calculationOfLoanTerms(loanApplicationRequestDTO);
        assertEquals(loanOfferDTO, resultFromApplicationService);
    }

    @Test
    void selectAnOffers() {
        LoanOfferDTO loanOffer = LoanOfferDTO.builder()
                .statementId(UUID.randomUUID())
                .requestedAmount(BigDecimal.valueOf(100000))
                .totalAmount(BigDecimal.valueOf(107016.44))
                .term(6)
                .monthlyPayment(BigDecimal.valueOf(17502.74))
                .rate(BigDecimal.valueOf(17))
                .isInsuranceEnabled(true)
                .isSalaryClient(true)
                .build();

        statementService.selectAnOffers(loanOffer);
        Mockito.verify(statementFeignClient, times(1)).select(any());
    }
}