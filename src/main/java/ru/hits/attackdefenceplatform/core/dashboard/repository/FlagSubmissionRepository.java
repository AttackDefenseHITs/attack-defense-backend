package ru.hits.attackdefenceplatform.core.dashboard.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface FlagSubmissionRepository extends JpaRepository<FlagSubmissionEntity, UUID>, JpaSpecificationExecutor<FlagSubmissionEntity> {
}
