package ru.neoflex.gateway.controllers;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import ru.neoflex.gateway.model.dto.FinishRegistrationRequestDto;
import ru.neoflex.gateway.model.dto.LoanOfferDTO;
import ru.neoflex.gateway.model.dto.LoanStatementRequestDTO;
import ru.neoflex.gateway.service.DealService;
import ru.neoflex.gateway.service.StatementService;

import java.util.List;


@RestController
@RequiredArgsConstructor
public class GatewayController implements GatewayControllerApi {

    private final StatementService statementService;
    private final DealService dealService;

    @Override
    public List<LoanOfferDTO> statement(LoanStatementRequestDTO loanStatementRequestDTO) {
        return statementService.createLoanStatement(loanStatementRequestDTO);
    }

    @Override
    public void select(LoanOfferDTO loanOfferDTO) {
        statementService.applyOffer(loanOfferDTO);
    }

    @Override
    public void registration(String statementId, FinishRegistrationRequestDto finishRegistrationRequestDto) {
        dealService.finishRegistration(statementId, finishRegistrationRequestDto);
    }

    @Override
    public void create(String statementId) {
        dealService.createDocuments(statementId);
    }

    @Override
    public void sign(String statementId) {
        dealService.signDocuments(statementId);
    }

    @Override
    public void code(String statementId, @NotNull String sesCode) {
        dealService.sendSesCode(statementId, sesCode);
    }
}
