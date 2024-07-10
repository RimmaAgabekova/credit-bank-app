package ru.neoflex.dossier.utils;

import jakarta.activation.DataSource;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder(toBuilder = true)
public class EmailData {
    private String subject;
    private String contentText;
    private String linkForClient;
    private Map<String, DataSource> attachments;
}
