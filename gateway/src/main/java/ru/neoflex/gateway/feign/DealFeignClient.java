package ru.neoflex.gateway.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import ru.neoflex.gateway.model.dto.FinishRegistrationRequestDto;

@FeignClient(url = "${DEAL.URL}", name = "deal")
public interface DealFeignClient {

    @PostMapping("/api/v1/deal/calculate/{statementId}")
    void calculate(@PathVariable("statementId") String statementId, FinishRegistrationRequestDto finishRegistrationRequestDto);

    @PostMapping("/api/v1/deal/document/{statementId}/send")
    void send(@PathVariable("statementId") String statementId);

    @PostMapping("/api/v1/deal/document/{statementId}/sign")
    void sign(@PathVariable("statementId") String statementId);

    @PostMapping("/api/v1/deal/document/{statementId}/code")
    void code(@PathVariable("statementId") String statementId, @RequestHeader("ses-code") String sesCode);
}
