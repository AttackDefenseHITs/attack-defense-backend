package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.UUID;

public record FlagDto(
        UUID id,
        Integer points,
        Integer flagNumber,
        UUID teamId,
        String teamName,
        UUID serviceId,
        String serviceName,
        Boolean isActive
) {}
