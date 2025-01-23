package ru.hits.attackdefenceplatform.core.service_status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusDto;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ServiceStatusServiceImpl implements ServiceStatusService {

    private final ServiceStatusRepository serviceStatusRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final TeamRepository teamRepository;

    @Override
    @Transactional
    public void updateServiceStatus(UUID serviceId, UUID teamId, CheckerResult result) {
        var serviceStatus = serviceStatusRepository
                .findByServiceAndTeam(findService(serviceId), findTeam(teamId))
                .orElseGet(() -> createServiceStatus(serviceId, teamId));

        serviceStatus.updateDuration(result);
        serviceStatusRepository.save(serviceStatus);
    }

    @Override
    @Transactional
    public ServiceStatusDto getServiceStatus(UUID serviceId, UUID teamId) {
        var team = findTeam(teamId);
        var service = findService(serviceId);
        var serviceStatusEntity = serviceStatusRepository.findByServiceAndTeam(service, team)
                .orElseGet(() -> createServiceStatus(serviceId, teamId));

        return new ServiceStatusDto(
                serviceStatusEntity.getId(),
                service.getId(),
                team.getId(),
                service.getName(),
                team.getName(),
                serviceStatusEntity.getLastStatus(),
                serviceStatusEntity.getUpdatedAt()
        );
    }

    @Override
    public ServiceStatusInfo getAllServiceStatuses() {
        var statuses = serviceStatusRepository.findAll().stream()
                .map(serviceStatus -> new ServiceStatusDto(
                        serviceStatus.getId(),
                        serviceStatus.getService().getId(),
                        serviceStatus.getTeam().getId(),
                        serviceStatus.getService().getName(),
                        serviceStatus.getTeam().getName(),
                        serviceStatus.getLastStatus(),
                        serviceStatus.getUpdatedAt()
                ))
                .toList();

        return new ServiceStatusInfo(statuses);
    }


    @Override
    @Transactional
    public ServiceStatusEntity createServiceStatus(UUID serviceId, UUID teamId) {
        var service = findService(serviceId);
        var team = findTeam(teamId);

        var serviceStatus = new ServiceStatusEntity();
        serviceStatus.setService(service);
        serviceStatus.setTeam(team);
        serviceStatus.setLastStatus(CheckerResult.DEFAULT);
        serviceStatus.setLastChanged(LocalDateTime.now(ZoneOffset.UTC));
        return serviceStatusRepository.save(serviceStatus);
    }

    private VulnerableServiceEntity findService(UUID serviceId) {
        return vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + serviceId + " not found"));
    }

    private TeamEntity findTeam(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team with ID " + teamId + " not found"));
    }
}

