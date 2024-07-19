package ru.neoflex.gateway.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import ru.neoflex.gateway.model.dto.LoanOfferDTO;
import ru.neoflex.gateway.model.dto.LoanStatementRequestDTO;

import java.util.List;

@FeignClient(url = "${STATEMENT.URL}", name = "statement")
public interface StatementFeignClient {

    @PostMapping(value = "/api/v1/statement")
    List<LoanOfferDTO> statement(LoanStatementRequestDTO loanStatementRequestDTO);

    @PostMapping("/api/v1/statement/offer")
    void offer(LoanOfferDTO loanOfferDTO);
}
