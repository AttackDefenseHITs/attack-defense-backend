package ru.hits.attackdefenceplatform.publisher;

import java.util.UUID;

public record AttackExecutedEvent(
        UUID teamId,
        String teamName,
        UUID serviceId,
        String serviceName,
        long round,
        boolean success,
        int exitCode,
        String output
) {}
