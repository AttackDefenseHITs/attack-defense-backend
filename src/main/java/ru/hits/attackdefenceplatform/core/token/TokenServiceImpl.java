package ru.hits.attackdefenceplatform.core.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public TokenResponse updateToken(String refreshToken) {
        return null;
    }
}
