package ru.hits.attackdefenceplatform.public_interface.token;

import ru.hits.attackdefenceplatform.core.user.repository.Role;

public record TokenResponse(
    String name,
    String login,
    Role role,
    String accessToken,
    String refreshToken,
    String tokenType
) { }
