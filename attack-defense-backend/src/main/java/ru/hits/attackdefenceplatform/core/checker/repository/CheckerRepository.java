package ru.hits.attackdefenceplatform.core.checker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CheckerRepository extends JpaRepository<CheckerEntity, UUID> {
    Optional<CheckerEntity> findByVulnerableServiceId(UUID serviceId);
}
