package ru.neoflex.deal.mappers;

import org.mapstruct.Mapper;
import ru.neoflex.deal.model.dto.StatementDTO;
import ru.neoflex.deal.models.Statement;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ListStatementDtoMapper {
    List<StatementDTO> toStatementDtoList(List<Statement> statement);
}
