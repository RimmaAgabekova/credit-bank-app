package ru.neoflex.dossier.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.neoflex.dossier.model.dto.StatementDTO;

@FeignClient(url = "${DEAL.URL}", name = "deal")
public interface DealFeignClient {

    @PutMapping( "/api/v1/deal/admin/statement/{statementId}/status")
    void status(@PathVariable("statementId") String statementId, @RequestParam ("newStatus") String statusName);

    @GetMapping("/api/v1/deal/admin/statement/{statementId}/statement-data")
    StatementDTO statementData(@PathVariable("statementId") String statementId);
}
