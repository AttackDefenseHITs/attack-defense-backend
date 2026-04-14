package ru.hits.attackdefenceplatform.core.vulnerable_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.deploy.status.mapper.DeploymentStatusInitializer;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class VulnerableServiceManagementServiceImpl implements VulnerableServiceManagementService {

    private final VulnerableServiceRepository serviceRepository;
    private final DeploymentStatusInitializer deploymentStatusInitializer;

    @Override
    @Transactional
    public void syncServices(Map<String, VulnerableServiceEntity> detected, String gitRepositoryUrl) {

        List<VulnerableServiceEntity> existing = serviceRepository.findAll();

        // Добавляем новые
        for (VulnerableServiceEntity s : detected.values()) {
            boolean exists = existing.stream().anyMatch(e -> e.getName().equals(s.getName()));
            if (!exists) {
                s.setPort(1111);
                s.setGitRepositoryUrl(gitRepositoryUrl);
                var newService = serviceRepository.save(s);
                deploymentStatusInitializer.initializeStatusesForNewService(newService.getId());
                log.info("Добавлен новый сервис: {}", s.getName());
            }
        }

        // Удаляем отсутствующие
        for (VulnerableServiceEntity s : existing) {
            if (!detected.containsKey(s.getName())) {
                deploymentStatusInitializer.deleteStatusesForService(s.getId());
                serviceRepository.delete(s);
                log.info("Удалён сервис: {}", s.getName());
            }
        }
    }
}
