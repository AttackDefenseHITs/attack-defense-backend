package ru.hits.attackdefenceplatform.core.hint.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ServiceHintTemplateRepository extends JpaRepository<ServiceHintTemplateEntity, UUID> {
    @Query("select coalesce(max(h.level), 0) from ServiceHintTemplateEntity h where h.service.id = :serviceId")
    int findMaxLevelByServiceId(@Param("serviceId") UUID serviceId);

    List<ServiceHintTemplateEntity> findAllByService_IdOrderByLevelAsc(UUID serviceId);
    List<ServiceHintTemplateEntity> findAllByOrderByService_IdAscLevelAsc();
}

