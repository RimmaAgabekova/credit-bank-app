package ru.neoflex.dossier.service;

import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import jakarta.activation.DataSource;
import jakarta.mail.util.ByteArrayDataSource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.neoflex.dossier.feign.DealFeignClient;
import ru.neoflex.dossier.model.dto.EmailMessage;
import ru.neoflex.dossier.model.dto.StatementDTO;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentsService {

    private final DealFeignClient dealFeignClient;
    private final SpringTemplateEngine templateEngine;

    Map<String, DataSource> createCreditDocuments(EmailMessage emailMessage) {
        Map<String, DataSource> documents = new HashMap<>();
        StatementDTO statement = dealFeignClient.statementData(emailMessage.getStatementId().toString());

        Context context = new Context();
        context.setVariable("statement", statement);

        documents.put("Payment_Schedule.pdf", createDocument("paymentScheduleTemplate", context));
        documents.put("Questionnaire.pdf", createDocument("questionnaireTemplate", context));

        return documents;
    }

    DataSource createDocument(String templateName, Context context) {
        try {
            String emailContent = templateEngine.process(templateName, context);
            ByteArrayOutputStream document = new ByteArrayOutputStream();
            ConverterProperties converterProperties = new ConverterProperties();
            converterProperties.setBaseUri("http://localhost:8083");

            HtmlConverter.convertToPdf(emailContent, document, converterProperties);

            InputStream inputStream = new ByteArrayInputStream(document.toByteArray());
            return new ByteArrayDataSource(inputStream, "application/pdf");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
