package ru.neoflex.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.deal.models.Credit;

public interface CreditRepository extends JpaRepository<Credit, Long> {
}
