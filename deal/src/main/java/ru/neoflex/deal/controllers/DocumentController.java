package ru.neoflex.deal.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.deal.services.DocumentService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class DocumentController implements DocumentControllerApi {

    private final DocumentService documentService;

    @Override
    public void send(String statementId) {
        documentService.sendSendDocumentRequest(UUID.fromString(statementId));
    }

    @Override
    public void sign(String statementId) {
        documentService.sendSignDocumentRequest(UUID.fromString(statementId));
    }

    @Override
    public void code(String statementId, @NotNull String sesCode) {
        documentService.sendCreditIssueRequest(UUID.fromString(statementId), Integer.valueOf(sesCode.toString()));
    }
}
