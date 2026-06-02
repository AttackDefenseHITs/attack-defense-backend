package ru.hits.attackdefenceplatform.public_interface.team;

import java.util.UUID;

public record CreatedTeamResponse(
        UUID id,
        String name,
        Long userCount,
        Long membersCount
) {
}
