package ru.neoflex.deal.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.neoflex.deal.models.Client;
import ru.neoflex.deal.models.Statement;

import java.time.LocalDate;
import java.util.ArrayList;

@Mapper(componentModel = "spring", imports = {LocalDate.class, ArrayList.class})
public interface StatementMapper {

    @Mapping(target = "clientId", source = "client")
    @Mapping(target = "creationDate",expression = "java(LocalDate.now())")
    @Mapping(target = "statusHistory", expression = "java(new ArrayList<>())")
    Statement buildStatement(Client client);
}
