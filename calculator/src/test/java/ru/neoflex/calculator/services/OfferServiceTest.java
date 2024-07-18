package ru.neoflex.calculator.services;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.calculator.exceptions.ScoringException;
import ru.neoflex.calculator.model.dto.LoanOfferDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {
    @InjectMocks
    private OfferService offerService;
    @Mock
    private CalcService calcService;
    @Mock
    private ScoringService scoringService;

    static LoanStatementRequestDTO request;

    @BeforeAll
    static void createMocksObjects() {
        request = new LoanStatementRequestDTO();
    }

    @BeforeEach
    void setDataMocksObjects() {
        request.setAmount(BigDecimal.valueOf(100000));
        request.setTerm(36);
        request.setBirthdate(LocalDate.of(2000, 1, 10));
        request.setEmail("testemail@mail.ru");
        request.setFirstName("Тест");
        request.setLastName("Тестовый");
        request.setMiddleName("Тестович");
        request.setPassportSeries("1234");
        request.setPassportNumber("234123");
    }

    @Test
    void generateOffers() {
        BigDecimal amount = new BigDecimal("100000");
        List<LoanOfferDTO> loanOffers = offerService.generateOffers(request);

        assertEquals(4, loanOffers.size());
        assertEquals(amount, loanOffers.get(0).getRequestedAmount());
    }

    @Test
    void getLoanOffersErrorAgeTest() {
        request.setBirthdate(LocalDate.of(2010, 3, 8));
        assertThrows(ScoringException.class, () -> offerService.generateOffers(request));
    }
}