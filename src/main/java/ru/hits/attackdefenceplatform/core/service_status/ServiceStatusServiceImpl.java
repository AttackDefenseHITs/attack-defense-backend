package ru.hits.attackdefenceplatform.core.service_status;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.core.points.PointsService;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.FlagPointsForServiceDto;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusSummary;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.TeamServiceStatusDto;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceStatusServiceImpl implements ServiceStatusService {
    private final static Double DEFAULT_SLA = 100.0;

    private final ServiceStatusRepository serviceStatusRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamService teamService;
    private final PointsService pointsService;

    private final WebSocketClient<ServiceStatusInfo> webSocketClient;

    @Override
    @Transactional
    public void updateServiceStatus(UUID serviceId, UUID teamId, CheckerResult result) {
        var team = findTeam(teamId);
        var service = findService(serviceId);

        var serviceStatus = serviceStatusRepository
                .findByServiceAndTeam(service, team)
                .orElseGet(() -> createServiceStatus(serviceId, teamId, result));

        serviceStatus.updateDuration(result);

        serviceStatusRepository.save(serviceStatus);
        var serviceStatusDto = getServiceStatusByTeamAndService(service, team);
        sendNewServiceStatusToUsers(serviceStatusDto);
    }

    @Override
    @Transactional
    public TeamServiceStatusDto getServiceStatus(UUID serviceId, UUID teamId) {
        var team = findTeam(teamId);
        var service = findService(serviceId);

        return getServiceStatusByTeamAndService(service, team);
    }

    @Override
    public ServiceStatusInfo getAllServiceStatuses() {
        var teams = teamRepository.findAll();
        var serviceStatuses = serviceStatusRepository.findAll();

        var data = teams.stream().map(team -> {
            var statusesForTeam = serviceStatuses.stream()
                    .filter(status -> status.getTeam().equals(team))
                    .toList();

            Map<String, ServiceStatusSummary> services = mapStatusesToServiceSummaries(statusesForTeam);

            return new TeamServiceStatusDto(
                    teamService.mapTeamEntityToTeamListDto(team, null),
                    services
            );
        }).toList();

        return new ServiceStatusInfo(data);
    }

    @Override
    @Transactional
    public ServiceStatusEntity createServiceStatus(UUID serviceId, UUID teamId, CheckerResult result) {
        var service = findService(serviceId);
        var team = findTeam(teamId);

        var serviceStatus = new ServiceStatusEntity();
        serviceStatus.setService(service);
        serviceStatus.setTeam(team);
        serviceStatus.setLastStatus(result);
        serviceStatus.setLastChanged(LocalDateTime.now(ZoneOffset.UTC));
        return serviceStatusRepository.save(serviceStatus);
    }

    private void sendNewServiceStatusToUsers(TeamServiceStatusDto serviceStatus){
        var serviceStatusInfo = new ServiceStatusInfo(List.of(serviceStatus));
        webSocketClient.sendNotification(serviceStatusInfo, getUserIdsList());
    }

    private TeamServiceStatusDto getServiceStatusByTeamAndService(VulnerableServiceEntity service, TeamEntity team){
        var statusesForTeam = serviceStatusRepository.findByServiceAndTeam(service, team)
                .stream()
                .toList();

        var services = mapStatusesToServiceSummaries(statusesForTeam);

        return new TeamServiceStatusDto(
                teamService.mapTeamEntityToTeamListDto(team, null),
                services
        );
    }

    /**
     * Вспомогательный метод для маппинга статусов сервисов в DTO ServiceStatusSummary.
     */
    private Map<String, ServiceStatusSummary> mapStatusesToServiceSummaries(List<ServiceStatusEntity> statusesForTeam) {
        return statusesForTeam.stream()
                .collect(Collectors.toMap(
                        status -> status.getService().getName(),
                        status -> {

                            long totalDuration = status.getTotalOkDuration() + status.getTotalMumbleDuration()
                                    + status.getTotalCorruptDuration() + status.getTotalDownDuration();

                            double sla = totalDuration == 0
                                    ? DEFAULT_SLA
                                    : (status.getTotalOkDuration() * DEFAULT_SLA / totalDuration);

                            var flagPoints = pointsService
                                    .getFlagPointsForServiceAndTeam(status.getTeam(), status.getService());

                            return new ServiceStatusSummary(
                                    status.getService().getId(),
                                    String.format("%.2f%%", sla),
                                    flagPoints,
                                    status.getLastStatus()
                            );
                        }
                ));
    }

    private VulnerableServiceEntity findService(UUID serviceId) {
        return vulnerableServiceRepository.findById(serviceId)
                .orElseThrow(() -> new IllegalArgumentException("Service with ID " + serviceId + " not found"));
    }

    private TeamEntity findTeam(UUID teamId) {
        return teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("Team with ID " + teamId + " not found"));
    }

    private List<String> getUserIdsList(){
        var users = userRepository.findAll();
        return users.stream()
                .map(UserEntity::getId)
                .map(UUID::toString)
                .toList();
    }
}


