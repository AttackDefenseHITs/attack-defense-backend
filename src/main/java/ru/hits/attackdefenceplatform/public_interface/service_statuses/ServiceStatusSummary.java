package ru.hits.attackdefenceplatform.public_interface.service_statuses;

import ru.hits.attackdefenceplatform.core.checker.enums.CheckerResult;

import java.util.UUID;

public record ServiceStatusSummary(
        UUID serviceId,
        String sla,
        FlagPointsForServiceDto flagPoints,
        CheckerResult status
) { }
