package ru.neoflex.statement.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.neoflex.statement.model.dto.LoanOfferDTO;
import ru.neoflex.statement.model.dto.LoanStatementRequestDTO;

import java.util.List;

@FeignClient(value = "deal", url = "${DEAL.URL}")
public interface StatementFeignClient {

    @PostMapping(value = "/api/v1/deal/statement")
    List<LoanOfferDTO> statement(LoanStatementRequestDTO loanStatementRequestDTO);

    @PostMapping(value = "/api/v1/deal/offer/select")
    void select(LoanOfferDTO loanOfferDTO);

}