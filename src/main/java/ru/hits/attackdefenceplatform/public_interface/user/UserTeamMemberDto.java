package ru.hits.attackdefenceplatform.public_interface.user;

import ru.hits.attackdefenceplatform.core.user.repository.Role;

import java.util.UUID;

public record UserTeamMemberDto (
        UUID id,
        String login,
        String name,
        Role role,
        Integer points
){}
