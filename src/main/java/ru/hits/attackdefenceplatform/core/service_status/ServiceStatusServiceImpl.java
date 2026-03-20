package ru.hits.attackdefenceplatform.core.service_status;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.core.service_status.repository.ServiceStatusRepository;
import ru.hits.attackdefenceplatform.core.team.TeamService;
import ru.hits.attackdefenceplatform.core.team.repository.TeamRepository;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusInfo;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.ServiceStatusSummary;
import ru.hits.attackdefenceplatform.public_interface.service_statuses.TeamServiceStatusDto;
import ru.hits.attackdefenceplatform.public_interface.team.TeamShortDataDto;

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
        var teams = teamRepository.findAllByIsSystemFalse();
        var serviceStatuses = serviceStatusRepository.findAll();

        var data = teams.stream().map(team -> {
            var statusesForTeam = serviceStatuses.stream()
                    .filter(status -> status.getTeam().equals(team))
                    .toList();

            Map<String, ServiceStatusSummary> services = serviceStatusMapper.mapStatusesToServiceSummaries(statusesForTeam);

            TeamShortDataDto teamDto = teamService.mapToTeamServiceStatusDto(team);

            return new TeamServiceStatusDto(
                    teamDto,
                    services
            );
        }).toList();

        return new ServiceStatusInfo(data);
    }
}

