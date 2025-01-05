package ru.hits.attackdefenceplatform.public_interface.team;

import java.util.UUID;

public record TeamListDto(
        UUID id,
        String name,
        Integer place,
        Integer points,
        Long userCount,
        Long membersCount,
        Boolean isMyTeam
) {
}
