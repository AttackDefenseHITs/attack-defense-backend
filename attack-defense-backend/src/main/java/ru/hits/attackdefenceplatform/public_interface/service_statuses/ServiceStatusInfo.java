package ru.hits.attackdefenceplatform.public_interface.service_statuses;

import java.util.List;

public record ServiceStatusInfo(
        List<TeamServiceStatusDto> serviceStatuses
) {
}
