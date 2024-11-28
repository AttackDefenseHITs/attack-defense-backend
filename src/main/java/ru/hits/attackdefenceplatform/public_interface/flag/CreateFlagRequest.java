package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.UUID;

public record CreateFlagRequest(
        String value,
        Integer points,
        Integer flagNumberInService,
        UUID serviceId,
        UUID teamId
) {
}
