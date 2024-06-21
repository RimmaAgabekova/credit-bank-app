package ru.neoflex.deal.mappers;

import org.mapstruct.Mapper;
import ru.neoflex.deal.enums.CreditStatus;
import ru.neoflex.deal.model.dto.CreditDTO;
import ru.neoflex.deal.models.Credit;

@Mapper(componentModel = "spring")
public interface CreditMapper {
    Credit creditDTOToCredit(CreditDTO creditDTO, CreditStatus creditStatus);
}
