package ru.neoflex.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.neoflex.deal.model.dto.FinishRegistrationRequestDto;
import ru.neoflex.deal.model.dto.LoanOfferDTO;
import ru.neoflex.deal.model.dto.ScoringDataDTO;
import ru.neoflex.deal.models.Client;

@Mapper(componentModel = "spring")
public interface ScoringDataDTOMapper {
    @Mapping(target = "gender", source = "finishRegistrationRequest.gender")
    @Mapping(target = "maritalStatus", source = "finishRegistrationRequest.maritalStatus")
    @Mapping(target = "dependentAmount", source = "finishRegistrationRequest.dependentAmount")
    @Mapping(target = "accountNumber", source = "finishRegistrationRequest.accountNumber")
    @Mapping(target = "amount", source = "offer.requestedAmount")
    @Mapping(target = "passportSeries", source = "client.passportId.series")
    @Mapping(target = "passportNumber", source = "client.passportId.number")
    @Mapping(target = "birthdate", source = "client.birthDate")
    ScoringDataDTO finishOfferClientToScoringData(FinishRegistrationRequestDto finishRegistrationRequest, LoanOfferDTO offer, Client client);
}
