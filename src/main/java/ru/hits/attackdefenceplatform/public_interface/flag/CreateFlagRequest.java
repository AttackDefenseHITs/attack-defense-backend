package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.UUID;

public record CreateFlagRequest(
        String value,
        UUID serviceId,
        UUID teamId
) {
}
