package ru.hits.attackdefenceplatform.public_interface.user;

public record CreateUserRequest(
    String login,
    String name,
    String password,
    String confirmPassword
)
{ }
