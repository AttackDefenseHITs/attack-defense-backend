package ru.hits.attackdefenceplatform.modules.service_status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.modules.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusSummary;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.TeamServiceStatusDto;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServiceStatusServiceImpl implements ServiceStatusService {
    private final ServiceStatusRepository serviceStatusRepository;
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final ServiceStatusMapper serviceStatusMapper;

    @Override
    public ServiceStatusInfo getAllServiceStatuses() {
        var teams = teamRepository.findAll();
        var serviceStatuses = serviceStatusRepository.findAll();

        var data = teams.stream().map(team -> {
            var statusesForTeam = serviceStatuses.stream()
                    .filter(status -> status.getTeam().equals(team))
                    .toList();

            Map<String, ServiceStatusSummary> services = serviceStatusMapper.mapStatusesToServiceSummaries(statusesForTeam);

            return new TeamServiceStatusDto(
                    teamService.mapToTeamServiceStatusDto(team),
                    services
            );
        }).toList();

        return new ServiceStatusInfo(data);
    }
}


