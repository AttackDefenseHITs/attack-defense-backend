package ru.hits.attackdefenceplatform.public_interface.user;

public record CreateUserDto(
    String login,
    String name,
    String password,
    String confirmPassword
)
{ }
