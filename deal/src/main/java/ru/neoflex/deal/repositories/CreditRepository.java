package ru.neoflex.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.deal.models.Credit;

import java.util.UUID;


public interface CreditRepository extends JpaRepository<Credit, Long> {
}
