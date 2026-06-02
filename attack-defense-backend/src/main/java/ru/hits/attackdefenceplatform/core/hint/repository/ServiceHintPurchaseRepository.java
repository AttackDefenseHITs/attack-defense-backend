package ru.hits.attackdefenceplatform.core.hint.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ServiceHintPurchaseRepository extends JpaRepository<ServiceHintPurchaseEntity, UUID> {
    List<ServiceHintPurchaseEntity> findAllByTeam_IdAndTemplate_Service_Id(UUID teamId, UUID serviceId);
    boolean existsByTeam_IdAndTemplate_Id(UUID teamId, UUID templateId);
    List<ServiceHintPurchaseEntity> findAllByTeam_Id(UUID teamId);
    long countByTeam_Id(UUID teamId);
}
