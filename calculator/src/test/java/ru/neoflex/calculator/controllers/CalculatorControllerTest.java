package ru.neoflex.calculator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.neoflex.calculator.handlers.ApiErrorResponse;
import ru.neoflex.calculator.handlers.AppError;
import ru.neoflex.calculator.model.dto.CreditDTO;
import ru.neoflex.calculator.model.dto.EmploymentDTO;
import ru.neoflex.calculator.model.dto.LoanStatementRequestDTO;
import ru.neoflex.calculator.model.dto.ScoringDataDTO;
import ru.neoflex.calculator.services.CalcService;
import ru.neoflex.calculator.services.OfferService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        mvc.perform(MockMvcRequestBuilders
                        .post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isOk());

        verify(offerService, times(1)).generateOffers(any());
    }

    @Test
    void calculateCreditDetailsShouldReturn200() throws Exception {
        ScoringDataDTO scoringDataDto = createScoringDataDto();
        when(calcService.calculateCredit(any())).thenReturn(new CreditDTO());

        mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringDataDto)))
                .andExpect(status().isOk());

        verify(calcService, times(1)).calculateCredit(any());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_amountMustByGreater30000() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setAmount(new BigDecimal("29999"));

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("amount")
                                .message("must be greater than or equal to 30000")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_termMustByGreater6() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setTerm(5);

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("term")
                                .message("must be greater than or equal to 6")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_firstnameMustBeFrom2To30characters() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setFirstName("a");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("firstName")
                                .message("must match \"[A-Za-z\\-]{2,30}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_middleNameMustBeFrom2To30characters() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setMiddleName("a");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("middleName")
                                .message("must match \"[A-Za-z]{2,30}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_lastNameMustBeFrom2To30characters() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setLastName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("lastName")
                                .message("must match \"[A-Za-z\\-]{2,30}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_InvalidEmail() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setEmail("@yandex.ru");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("email")
                                .message("must match \"^[a-zA-Z0-9_!#$%&*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_passportSeriesMustByGreater4() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setPassportSeries("4");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("passportSeries")
                                .message("must match \"[0-9]{4}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateOffers_shouldReturnBadRequest_passportNumberMustByGreater6() throws Exception {
        LoanStatementRequestDTO loanStatementRequest = createLoanStatementRequestDto();
        loanStatementRequest.setPassportNumber("6");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("passportNumber")
                                .message("must match \"[0-9]{6}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/offers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loanStatementRequest)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_amountMustByGreater30000() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setAmount(new BigDecimal("29999"));

        mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_termMustByGreater6() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setTerm(5);

        mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_firstNameMustBeFrom2To30characters() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setFirstName("a");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("firstName")
                                .message("must match \"[A-Za-z\\-]{2,30}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_lastNameMustBeFrom2To30characters() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setLastName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("lastName")
                                .message("must match \"[A-Za-z\\-]{2,30}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_middleNameMustBeFrom2To30characters() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setMiddleName("a");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("middleName")
                                .message("must match \"[A-Za-z]{2,30}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_passportSeriesMustByGreater4() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setPassportSeries("4");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("passportSeries")
                                .message("must match \"[0-9]{4}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_passportNumberMustByGreater6() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setPassportNumber("6");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("passportNumber")
                                .message("must match \"[0-9]{6}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
    }

    @Test
    void calculateCalc_shouldReturnBadRequest_accountNumberMustByGreater20() throws Exception {
        ScoringDataDTO scoringData = createScoringDataDto();
        scoringData.setAccountNumber("1231231231231231231");

        ApiErrorResponse expectedResponse = ApiErrorResponse.builder()
                .apiErrorsResponse(List.of(
                        AppError.builder()
                                .name("accountNumber")
                                .message("must match \"[0-9]{20}\"")
                                .build()))
                .build();

        MockHttpServletResponse response = mvc.perform(post("/api/v1/calculator/calc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scoringData)))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse();

        ApiErrorResponse apiErrorResponse = objectMapper.readValue(response.getContentAsString(), ApiErrorResponse.class);

        assertThat(apiErrorResponse).isNotNull()
                .extracting(ApiErrorResponse::getApiErrorsResponse)
                .isEqualTo(expectedResponse.getApiErrorsResponse());
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
