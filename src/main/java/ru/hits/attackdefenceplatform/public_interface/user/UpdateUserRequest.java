package ru.hits.attackdefenceplatform.public_interface.user;

public record UpdateUserRequest(
        String name,
        String login
) {}
