package ru.hits.attackdefenceplatform.public_interface.user;

import ru.hits.attackdefenceplatform.business.user.repository.Role;

import java.util.UUID;

public record UserTeamMemberDto (
        UUID id,
        String login,
        String name,
        Role role,
        Double points
){}
