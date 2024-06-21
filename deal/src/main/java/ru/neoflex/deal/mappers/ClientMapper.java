package ru.neoflex.deal.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import ru.neoflex.deal.model.dto.FinishRegistrationRequestDto;
import ru.neoflex.deal.model.dto.LoanStatementRequestDTO;
import ru.neoflex.deal.models.Client;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ClientMapper {
    @Mapping(target = "passportId.series", source = "request.passportSeries")
    @Mapping(target = "passportId.number", source = "request.passportNumber")
    @Mapping(target = "birthDate", source = "birthdate")
    Client loanRequestToClient(LoanStatementRequestDTO request);

    @Mapping(target = "client.gender", source = "finishRegistrationRequest.gender")
    @Mapping(target = "client.maritalStatus", source = "finishRegistrationRequest.maritalStatus")
    @Mapping(target = "client.dependentAmount", source = "finishRegistrationRequest.dependentAmount")
    @Mapping(target = "client.accountNumber", source = "finishRegistrationRequest.accountNumber")
    @Mapping(target = "client.employmentId", source = "finishRegistrationRequest.employment")
    @Mapping(target = "client.employmentId.status", source = "finishRegistrationRequest.employment.employmentStatus")
    @Mapping(target = "client.employmentId.employerInn", source = "finishRegistrationRequest.employment.employerINN")
    @Mapping(target = "client.passportId.issueBranch", source = "finishRegistrationRequest.passportIssueBranch")
    @Mapping(target = "client.passportId.issueDate", source = "finishRegistrationRequest.passportIssueDate")
    Client updateClientMapper(FinishRegistrationRequestDto finishRegistrationRequest, @MappingTarget Client client);
}
