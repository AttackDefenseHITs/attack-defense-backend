package ru.hits.attackdefenceplatform.public_interface.user;

public record LoginUserDto(
        String login,
        String password
) { }
