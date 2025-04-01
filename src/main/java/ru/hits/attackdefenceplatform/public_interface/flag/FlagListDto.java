package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.UUID;

public record FlagListDto(
        UUID id,
        String teamName,
        String serviceName,
        String value
) {}
