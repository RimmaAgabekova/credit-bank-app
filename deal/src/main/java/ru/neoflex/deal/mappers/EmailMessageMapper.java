package ru.neoflex.deal.mappers;


import org.mapstruct.Mapper;
import ru.neoflex.deal.model.dto.EmailMessage;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface EmailMessageMapper {
    EmailMessage createEmailMassage(EmailMessage.ThemeEnum theme, UUID statementId, String address);
}
