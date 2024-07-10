package ru.neoflex.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.neoflex.deal.model.dto.StatementStatus;
import ru.neoflex.deal.model.dto.StatementStatusHistoryDTO;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class})
public interface StatementStatusHistoryMapper {

    @Mapping(target = "time", expression = "java(LocalDateTime.now())")
    @Mapping(target = "changeType", source = "statusType")
    StatementStatusHistoryDTO addStatus(StatementStatus status, StatementStatusHistoryDTO.ChangeTypeEnum statusType);
}
