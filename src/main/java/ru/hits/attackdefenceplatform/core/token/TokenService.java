package ru.hits.attackdefenceplatform.core.token;

import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;

public interface TokenService {
    TokenResponse updateToken(String refreshToken);
}
