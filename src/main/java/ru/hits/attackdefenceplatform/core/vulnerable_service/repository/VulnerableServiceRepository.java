package ru.hits.attackdefenceplatform.core.vulnerable_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VulnerableServiceRepository extends JpaRepository<VulnerableServiceEntity, UUID> {
    Optional<VulnerableServiceEntity> findByName(String name);
}
