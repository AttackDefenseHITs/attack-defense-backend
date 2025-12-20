package ru.hits.attackdefenceplatform.core.vulnerable_service;

import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.Map;

public interface VulnerableServiceManagementService {
    void syncServices(Map<String, VulnerableServiceEntity> detected);
}
