package ru.hits.attackdefenceplatform.public_interface.user;

import ru.hits.attackdefenceplatform.business.user.repository.Role;

public record RoleDto(
        Role role
) {
}
