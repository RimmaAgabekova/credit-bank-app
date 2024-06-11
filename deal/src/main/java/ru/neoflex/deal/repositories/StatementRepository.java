package ru.neoflex.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.deal.models.Statement;

import java.util.UUID;


@Repository
public interface StatementRepository extends JpaRepository<Statement, UUID> {
}
