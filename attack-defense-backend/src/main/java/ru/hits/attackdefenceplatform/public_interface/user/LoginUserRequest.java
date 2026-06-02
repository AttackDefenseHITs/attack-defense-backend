package ru.hits.attackdefenceplatform.public_interface.user;

public record LoginUserRequest(
        String login,
        String password
) { }
