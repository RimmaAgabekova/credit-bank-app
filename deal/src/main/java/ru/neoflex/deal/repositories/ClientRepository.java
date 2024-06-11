package ru.neoflex.deal.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.neoflex.deal.models.Client;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface  ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findById(UUID clientId);
}
