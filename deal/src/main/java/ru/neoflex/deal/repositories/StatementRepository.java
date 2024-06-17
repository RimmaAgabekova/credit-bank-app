package ru.neoflex.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.deal.models.Statement;

import java.util.UUID;

public interface StatementRepository extends JpaRepository<Statement, UUID> {
}
