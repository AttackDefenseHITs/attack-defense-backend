package ru.hits.attackdefenceplatform.core.token;

import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

public interface TokenService {
    TokenResponse updateToken(String refreshToken);

    TokenResponse createTokenResponse(UserDto userDto);
}
