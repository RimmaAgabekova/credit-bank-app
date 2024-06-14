package ru.neoflex.deal.services;


import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.neoflex.deal.models.Statement;
import ru.neoflex.deal.repositories.StatementRepository;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SelectServiceTest {

    @Mock
    private StatementRepository statementRepository;

    @InjectMocks
    private SelectService selectService;

    @Test
    void getStatementById() {

        when(statementRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> selectService.getStatementById(UUID.randomUUID()));

    }

    @Test
    void getStatementByIdNotNull() {

        when(statementRepository.findById(any())).thenReturn(Optional.of(new Statement()));

        assertNotNull(selectService.getStatementById(UUID.randomUUID()));

    }


}




