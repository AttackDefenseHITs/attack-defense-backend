package ru.hits.attackdefenceplatform.core.token;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.token.repository.RefreshTokenEntity;
import ru.hits.attackdefenceplatform.core.token.repository.RefreshTokenRepository;
import ru.hits.attackdefenceplatform.core.user.mapper.UserMapper;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

import java.util.Date;
import java.util.UUID;

import static ru.hits.attackdefenceplatform.common.constant.CommonConstants.TOKEN_TYPE;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{
    private final JwtTokenUtils jwtTokenUtils;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public TokenResponse updateToken(String refreshToken) {
        var userId = jwtTokenUtils.getUserIdFromToken(refreshToken);
        var tokenId = jwtTokenUtils.getTokenId(refreshToken);

        var storedToken = refreshTokenRepository.findById(UUID.fromString(tokenId))
                .orElseThrow(() -> new IllegalArgumentException("Недействительный рефреш токен"));

        if (storedToken.getExpirationDate().before(new Date())) {
            refreshTokenRepository.delete(storedToken);
            throw new IllegalArgumentException("Рефреш токен истек");
        }

        var user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь с ID " + userId + " не найден"));

        var userDto = UserMapper.mapUserEntityToDto(user);
        var newAccessToken = jwtTokenUtils.generateAccessToken(userDto);

        return new TokenResponse(
                userDto.name(),
                userDto.login(),
                userDto.role(),
                newAccessToken,
                refreshToken,
                TOKEN_TYPE
        );
    }

    @Override
    @Transactional
    public TokenResponse createTokenResponse(UserDto userDto) {
        var refreshToken = jwtTokenUtils.generateRefreshToken(userDto);

        var tokenId = jwtTokenUtils.getTokenId(refreshToken);
        var userId = jwtTokenUtils.getUserIdFromToken(refreshToken);
        var date = jwtTokenUtils.getExpirationDateFromToken(refreshToken);

        var refreshEntity = new RefreshTokenEntity();
        refreshEntity.setId(UUID.fromString(tokenId));
        refreshEntity.setToken(refreshToken);
        refreshEntity.setExpirationDate(date);
        refreshEntity.setUserId(userId);

        refreshTokenRepository.save(refreshEntity);
        return new TokenResponse(
                userDto.name(),
                userDto.login(),
                userDto.role(),
                jwtTokenUtils.generateAccessToken(userDto),
                refreshToken,
                TOKEN_TYPE
        );
    }
}
