package ru.neoflex.dossier.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(url = "http://localhost:8081", name = "deal")
public interface DealFeignClient {

    @PutMapping( "/api/v1/deal/admin/statement/{statementId}/status")
    void status(@PathVariable("statementId") String statementId, @RequestParam ("newStatus") String statusName);
}
