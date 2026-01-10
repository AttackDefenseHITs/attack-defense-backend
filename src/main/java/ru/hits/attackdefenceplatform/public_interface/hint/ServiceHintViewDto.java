package ru.hits.attackdefenceplatform.public_interface.hint;

import java.util.UUID;

public record ServiceHintViewDto(
        UUID id,
        int level,
        String text,
        double multiplier,
        boolean purchased
) {}
