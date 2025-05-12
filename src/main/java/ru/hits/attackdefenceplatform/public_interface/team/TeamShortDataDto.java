package ru.hits.attackdefenceplatform.public_interface.team;

import java.util.UUID;

public record TeamShortDataDto(
        UUID id,
        String name,
        Integer place,
        Double points,
        String ipAddress
) {
}
