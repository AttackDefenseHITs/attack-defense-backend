package ru.hits.attackdefenceplatform.public_interface.team;

import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import java.util.List;
import java.util.UUID;

public record TeamInfoDto(
        UUID id,
        String name,
        Long userCount,
        Long membersCount,
        Boolean canJoin,
        Boolean isMyTeam,
        List<UserDto> memberList
) {
}

