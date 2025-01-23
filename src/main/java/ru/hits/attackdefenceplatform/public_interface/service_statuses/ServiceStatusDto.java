package ru.hits.attackdefenceplatform.public_interface.service_statuses;

import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;

import java.time.LocalDateTime;
import java.util.UUID;

public record ServiceStatusDto(
        UUID id,
        UUID serviceId,
        UUID teamId,
        String serviceName,
        String teamName,
        CheckerResult checkerResult,
        LocalDateTime updatedAt
) {
}
