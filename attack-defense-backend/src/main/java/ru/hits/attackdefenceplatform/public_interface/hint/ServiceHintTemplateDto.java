package ru.hits.attackdefenceplatform.public_interface.hint;

import java.util.UUID;

public record ServiceHintTemplateDto(
        UUID id,
        UUID serviceId,
        int level,
        String text,
        double multiplier,
        boolean enabled
) {}
