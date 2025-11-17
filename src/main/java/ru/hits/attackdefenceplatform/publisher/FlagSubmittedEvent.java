package ru.hits.attackdefenceplatform.publisher;

import java.util.UUID;

public record FlagSubmittedEvent(
        UUID teamId,
        UUID serviceId,
        int round,
        boolean correct
) {}
