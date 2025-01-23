package ru.hits.attackdefenceplatform.public_interface.service_statuses;

import ru.hits.attackdefenceplatform.public_interface.team.TeamListDto;

import java.util.Map;

public record TeamServiceStatusDto(
        TeamListDto team,
        Map<String, ServiceStatusSummary> services
) {
}
