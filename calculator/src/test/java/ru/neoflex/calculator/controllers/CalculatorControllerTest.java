package ru.neoflex.calculator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.neoflex.calculator.model.dto.*;
import ru.neoflex.calculator.services.CalcService;
import ru.neoflex.calculator.services.OfferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(CalculatorController.class)
class CalculatorControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CalcService calcService;

    @MockBean
    private OfferService offerService;

    @Test
    void calculateOffersShouldReturn200() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();

        when(offerService.generateOffers(any())).thenReturn(new ArrayList<LoanOfferDTO>());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isOk());

        verify(offerService, times(1)).generateOffers(any());
    }

    @Test
    void calculateOffersShouldReturn400() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = new LoanStatementRequestDTO();

        when(offerService.generateOffers(any())).thenReturn(new ArrayList<LoanOfferDTO>());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest());
    }

    private LoanStatementRequestDTO createLoanStatementRequestDto() {
        LoanStatementRequestDTO loanStatementRequestDto = new LoanStatementRequestDTO();

        loanStatementRequestDto.setAmount(new BigDecimal("100000"));
        loanStatementRequestDto.setTerm(12);
        loanStatementRequestDto.setFirstName("Ivan");
        loanStatementRequestDto.setLastName("Ivanov");
        loanStatementRequestDto.setMiddleName("Ivanovich");
        loanStatementRequestDto.setBirthdate(LocalDate.of(1994, 1, 10));
        loanStatementRequestDto.setEmail("ivanov@gmail.com");
        loanStatementRequestDto.setPassportSeries("1234");
        loanStatementRequestDto.setPassportNumber("123456");

        return loanStatementRequestDto;
    }

    @Test
    void calculateCreditDetailsShouldReturn200() throws Exception {
        ScoringDataDTO scoringDataDto = createScoringDataDto();

        when(calcService.calculateCredit(any())).thenReturn(new CreditDTO());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringDataDto)))
                .andExpect(status().isOk());

        verify(calcService, times(1)).calculateCredit(any());
    }

    @Test
    void calculateCreditDetailsShouldReturn400() throws Exception {
        ScoringDataDTO scoringData = new ScoringDataDTO();

        when(calcService.calculateCredit(any())).thenReturn(new CreditDTO());

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isOk());
    }

    private ScoringDataDTO createScoringDataDto() {
        ScoringDataDTO scoringDataDto = new ScoringDataDTO();

        scoringDataDto.setAmount(new BigDecimal("1000000"));
        scoringDataDto.setTerm(12);
        scoringDataDto.setFirstName("Ivan");
        scoringDataDto.setLastName("Ivanov");
        scoringDataDto.setPassportSeries("1234");
        scoringDataDto.setPassportNumber("123456");
        scoringDataDto.setAccountNumber("12345678999912345678");
        scoringDataDto.setPassportIssueDate(LocalDate.of(2014, 2, 1));
        scoringDataDto.setPassportIssueBranch("отдел выдачи паспорта");
        scoringDataDto.setDependentAmount(0);
        scoringDataDto.setGender(ScoringDataDTO.GenderEnum.MALE);
        scoringDataDto.setBirthdate(LocalDate.of(1994, 1, 10));
        scoringDataDto.setMaritalStatus(ScoringDataDTO.MaritalStatusEnum.MARRIED);
        scoringDataDto.setIsInsuranceEnabled(true);
        scoringDataDto.setIsSalaryClient(true);
        EmploymentDTO employmentDto = new EmploymentDTO();
        employmentDto.setEmploymentStatus(EmploymentDTO.EmploymentStatusEnum.EMPLOYED);
        employmentDto.setSalary(new BigDecimal("100000.00"));
        employmentDto.setPosition(EmploymentDTO.PositionEnum.WORKER);
        employmentDto.setWorkExperienceTotal(20);
        employmentDto.setWorkExperienceCurrent(7);
        employmentDto.setEmployerINN("123456789098");
        scoringDataDto.setEmployment(employmentDto);

        return scoringDataDto;
    }
}
