package ru.neoflex.calculator.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    @Mock
    private ScoringService scoringService;
    @Mock
    private CalcService calcService;
    @InjectMocks
    private OfferService offerService;
    @Test
    void generateOffers() {
        LoanStatementRequestDTO request = new LoanStatementRequestDTO();
        BigDecimal amount = new BigDecimal("100000");

        request.setAmount(amount);
        List<LoanOfferDTO> loanOfferDTO = offerService.generateOffers(request);

        assertEquals(4, loanOfferDTO.size());
        assertEquals(amount, loanOfferDTO.get(0).getRequestedAmount());

    }
}