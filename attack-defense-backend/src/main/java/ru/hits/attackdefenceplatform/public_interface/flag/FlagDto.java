package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.UUID;

public record FlagDto(
        UUID id,
        UUID teamId,
        String teamName,
        UUID serviceId,
        String serviceName,
        String value,
        Boolean isActive
) {}
