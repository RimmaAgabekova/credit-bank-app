package ru.neoflex.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.neoflex.deal.models.Client;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findById(UUID clientId);
}
