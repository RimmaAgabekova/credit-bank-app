package ru.neoflex.deal.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.neoflex.deal.model.dto.CreditDTO;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.model.dto.ScoringDataDTO;

import java.util.List;

@FeignClient(value = "calculator", url = "${CALCULATOR.URL}")
public interface CalculatorFeignClient{

    @PostMapping(value = "/api/v1/calculator/offers")
    List<LoanOfferDTO> offers(LoanStatementRequestDTO loanStatementRequest);

    @PostMapping(value = "/api/v1/calculator/calc")
    CreditDTO calc(ScoringDataDTO scoringData);

}
