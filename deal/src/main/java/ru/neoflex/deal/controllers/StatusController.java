package ru.neoflex.deal.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.services.StatementService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class StatusController implements StatusControllerApi {
    private final StatementService statementService;

    @Override
    public void status(String statementId, @NotNull String newStatus) {
        statementService.updateStatementStatus(UUID.fromString(statementId), StatementStatus.fromValue(newStatus));
    }
}
