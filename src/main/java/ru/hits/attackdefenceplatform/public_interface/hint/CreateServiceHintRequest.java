package ru.hits.attackdefenceplatform.public_interface.hint;

import java.util.UUID;

public record CreateServiceHintRequest(
        UUID serviceId,
        String text,
        double multiplier
) {}
