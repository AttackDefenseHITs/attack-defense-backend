package ru.hits.attackdefenceplatform.core.deploy.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeploymentStatusEntityRepository extends JpaRepository<DeploymentStatusEntity, UUID> {
}
