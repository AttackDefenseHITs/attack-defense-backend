package ru.hits.attackdefenceplatform.public_interface.service_statuses;

import ru.hits.attackdefenceplatform.public_interface.team.TeamShortDataDto;

import java.util.Map;

public record TeamServiceStatusDto(
        TeamShortDataDto team,
        Map<String, ServiceStatusSummary> services
) {
}
