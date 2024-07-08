package ru.neoflex.deal.mappers;

import org.mapstruct.Mapper;
import ru.neoflex.deal.model.dto.StatementDTO;
import ru.neoflex.deal.models.Statement;

@Mapper(componentModel = "spring")
public interface StatementDTOMapper {

    StatementDTO statementToStatementDto(Statement statement);
}
