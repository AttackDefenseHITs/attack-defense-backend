package ru.hits.attackdefenceplatform.core.vulnerable.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.hits.attackdefenceplatform.core.vulnerable.VulnerableServiceEntity;

import java.util.UUID;

public interface VulnerableServiceRepository extends JpaRepository<VulnerableServiceEntity, UUID> {
}
