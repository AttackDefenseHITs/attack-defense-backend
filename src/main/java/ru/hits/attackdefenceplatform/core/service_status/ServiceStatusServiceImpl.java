package ru.hits.attackdefenceplatform.core.service_status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusDto;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusSummary;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.TeamServiceStatusDto;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceStatusServiceImpl implements ServiceStatusService {

    private final ServiceStatusRepository serviceStatusRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final TeamRepository teamRepository;
    private final TeamService teamService;

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
        var teams = teamRepository.findAll();
        var serviceStatuses = serviceStatusRepository.findAll();

        var data = teams.stream().map(team -> {
            var statusesForTeam = serviceStatuses.stream()
                    .filter(status -> status.getTeam().equals(team))
                    .toList();

            Map<String, ServiceStatusSummary> services = statusesForTeam.stream()
                    .collect(Collectors.toMap(
                            status -> status.getService().getName(),
                            status -> {
                                long totalDuration = status.getTotalOkDuration() + status.getTotalMumbleDuration()
                                        + status.getTotalCorruptDuration() + status.getTotalDownDuration();
                                double sla = totalDuration == 0 ? 0 : (status.getTotalOkDuration() * 100.0 / totalDuration);

                                return new ServiceStatusSummary(
                                        String.format("%.2f%%", sla),
                                        status.getLastStatus()
                                );
                            }
                    ));


            return new TeamServiceStatusDto(
                    teamService.mapTeamEntityToTeamListDto(team, null),
                    services
            );
        }).toList();

        return new ServiceStatusInfo(data);
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

