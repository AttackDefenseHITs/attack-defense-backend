package ru.hits.attackdefenceplatform.modules.vulnerable_service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.modules.deploy.status.mapper.DeploymentStatusInitializer;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;

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
    public void syncServices(Map<String, VulnerableServiceEntity> detected) {

        List<VulnerableServiceEntity> existing = serviceRepository.findAll();

        // Добавляем новые
        for (VulnerableServiceEntity s : detected.values()) {
            boolean exists = existing.stream().anyMatch(e -> e.getName().equals(s.getName()));
            if (!exists) {
                var newService = serviceRepository.save(s);
                deploymentStatusInitializer.initializeStatusesForNewService(newService.getId());
                log.info("Добавлен новый сервис: {}", s.getName());
            }
        }

        // Удаляем отсутствующие
        for (VulnerableServiceEntity s : existing) {
            if (!detected.containsKey(s.getName())) {
                serviceRepository.delete(s);
                deploymentStatusInitializer.deleteStatusesForService(s.getId());
                log.info("Удалён сервис: {}", s.getName());
            }
        }
    }
}
