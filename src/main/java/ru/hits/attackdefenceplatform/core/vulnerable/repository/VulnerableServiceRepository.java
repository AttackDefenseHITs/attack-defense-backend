package ru.hits.attackdefenceplatform.core.vulnerable.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VulnerableServiceRepository extends JpaRepository<VulnerableServiceEntity, UUID> {
}
