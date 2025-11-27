package ru.hits.attackdefenceplatform.publisher;

import ru.hits.attackdefenceplatform.business.checker.enums.CheckerResult;

import java.util.UUID;

public record ServiceStatusUpdatedEvent(UUID serviceId, UUID teamId, CheckerResult result) { }

