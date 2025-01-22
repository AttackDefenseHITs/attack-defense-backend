package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.List;
import java.util.UUID;

public record CreateFlagRequest(
        List<String> values,
        UUID serviceId,
        UUID teamId
) {
}
