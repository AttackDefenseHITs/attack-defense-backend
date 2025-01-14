package ru.hits.attackdefenceplatform.core.checker.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CheckerRepository extends JpaRepository<CheckerEntity, UUID> {
    CheckerEntity findByVulnerableServiceId(UUID serviceId);
}
