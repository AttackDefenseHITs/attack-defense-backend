package ru.hits.attackdefenceplatform.public_interface.flag;

import java.util.UUID;

public record FlagListDto(
        UUID id,
        Integer flagNumber,
        String teamName,
        String serviceName
) {}
