package ru.neoflex.deal.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.deal.model.dto.StatementDTO;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.services.StatementService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AdminController implements AdminControllerApi{

    private final StatementService statementService;

    @Override
    public void status(String statementId, @NotNull String newStatus) {
        statementService.updateStatementStatus(statementService.getStatementById(UUID.fromString(statementId)),
                StatementStatus.fromValue(newStatus));
    }
    @Override
    public List<StatementDTO> getAllStatement() {
        return statementService.getAllStatements();
    }

    @Override
    public void getStatementId(String statementId) {
        statementService.getStatementById(UUID.fromString(statementId));
    }

}
