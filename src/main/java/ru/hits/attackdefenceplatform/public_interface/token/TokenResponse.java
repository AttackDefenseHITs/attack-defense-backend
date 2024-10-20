package ru.hits.attackdefenceplatform.public_interface.token;

public record TokenResponse(
    String accessToken,
    String refreshToken
) { }
