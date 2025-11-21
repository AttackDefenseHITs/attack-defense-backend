package ru.hits.attackdefenceplatform.modules.vulnerable_service;

import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;

import java.util.Map;

public interface VulnerableServiceManagementService {
    void syncServices(Map<String, VulnerableServiceEntity> detected);
}
