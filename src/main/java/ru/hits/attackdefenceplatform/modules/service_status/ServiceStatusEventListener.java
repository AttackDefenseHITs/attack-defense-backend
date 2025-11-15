package ru.hits.attackdefenceplatform.modules.service_status;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.hits.attackdefenceplatform.modules.checker.enums.CheckerResult;
import ru.hits.attackdefenceplatform.modules.service_status.repository.ServiceStatusEntity;
import ru.hits.attackdefenceplatform.modules.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamEntity;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.modules.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.TeamServiceStatusDto;
import ru.hits.attackdefenceplatform.publisher.ServiceStatusUpdatedEvent;
import ru.hits.attackdefenceplatform.websocket.client.WebSocketClient;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceStatusEventListener {

    private final WebSocketClient<ServiceStatusInfo> webSocketClient;
    private final ServiceStatusRepository serviceStatusRepository;
    private final TeamRepository teamRepository;
    private final VulnerableServiceRepository vulnerableServiceRepository;
    private final TeamService teamService;
    private final ServiceStatusMapper serviceStatusMapper;

    private final EventBus eventBus;

    @PostConstruct
    public void register() {
        eventBus.register(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onServiceStatusUpdated(ServiceStatusUpdatedEvent event) {
        var team = findTeam(event.teamId());
        var service = findService(event.serviceId());

        var serviceStatus = serviceStatusRepository
                .findByServiceAndTeam(service, team)
                .orElseGet(() -> createServiceStatus(event.result(), team, service));

        serviceStatus.updateDuration(event.result());
        serviceStatusRepository.save(serviceStatus);
        
        var serviceStatusDto = getServiceStatusByTeamAndService(service, team);
        sendNewServiceStatusToUsers(serviceStatusDto);
    }

    private ServiceStatusEntity createServiceStatus(CheckerResult result, TeamEntity team, VulnerableServiceEntity service) {
        var serviceStatus = new ServiceStatusEntity();
        serviceStatus.setService(service);
        serviceStatus.setTeam(team);
        serviceStatus.setLastStatus(result);
        serviceStatus.setLastChanged(LocalDateTime.now(ZoneOffset.UTC));
        return serviceStatusRepository.save(serviceStatus);
    }

    private TeamServiceStatusDto getServiceStatusByTeamAndService(VulnerableServiceEntity service, TeamEntity team){
        var statusesForTeam = serviceStatusRepository.findByServiceAndTeam(service, team)
                .stream()
                .toList();

        return new TeamServiceStatusDto(
                teamService.mapToTeamServiceStatusDto(team),
                serviceStatusMapper.mapStatusesToServiceSummaries(statusesForTeam)
        );
    }

    private void sendNewServiceStatusToUsers(TeamServiceStatusDto serviceStatus){
        var serviceStatusInfo = new ServiceStatusInfo(List.of(serviceStatus));
        webSocketClient.sendNotification(serviceStatusInfo);
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

