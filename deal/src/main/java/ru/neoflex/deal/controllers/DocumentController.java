package ru.neoflex.deal.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.deal.services.DocumentService;
import ru.neoflex.deal.services.StatementService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DocumentController implements DocumentControllerApi {

    private final DocumentService documentService;
    private final StatementService statementService;

    @Override
    public void send(String statementId) {
        documentService.sendSendDocumentRequest(statementService.getStatementById(UUID.fromString(statementId)));
    }

    @Override
    public void sign(String statementId) {
        documentService.sendSignDocumentRequest(statementService.getStatementById(UUID.fromString(statementId)));
    }

    @Override
    public void code(String statementId, @NotNull String sesCode) {
        documentService.sendCreditIssueRequest(statementService.getStatementById(UUID.fromString(statementId)), Integer.valueOf(sesCode));
    }
}
