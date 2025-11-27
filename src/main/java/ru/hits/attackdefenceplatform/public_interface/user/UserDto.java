package ru.hits.attackdefenceplatform.public_interface.user;

import ru.hits.attackdefenceplatform.modules.user.repository.Role;

import java.util.UUID;

public record UserDto (
    UUID id,
    String login,
    String name,
    Role role
){}
