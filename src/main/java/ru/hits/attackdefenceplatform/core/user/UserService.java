package ru.hits.attackdefenceplatform.core.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.repository.UserMapper;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.public_interface.user.CreateUserDto;
import ru.hits.attackdefenceplatform.public_interface.user.LoginUserDto;
import ru.hits.attackdefenceplatform.util.JwtTokenUtils;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenUtils jwtTokenUtils;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public TokenResponse registerUser(CreateUserDto dto){
        var userEntity = UserMapper.mapCreateUserDtoToEntity(dto);
        userEntity.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        userRepository.save(userEntity);

        var userDto = UserMapper.mapUserEntityToDto(userEntity);
        return new TokenResponse(
                jwtTokenUtils.generateAccessToken(userDto),
                jwtTokenUtils.generateRefreshToken(userDto)
        );
    }

    @Transactional(readOnly = true)
    public TokenResponse loginUser(LoginUserDto dto){
        Optional<UserEntity> user = userRepository.findByLogin(dto.login());
        if (!validateUser(user, dto.password())){
            throw new RuntimeException("Invalid login or password");
        }

        var userDto = UserMapper.mapUserEntityToDto(user.get());
        return new TokenResponse(
                jwtTokenUtils.generateAccessToken(userDto),
                jwtTokenUtils.generateRefreshToken(userDto)
        );

    }

    private boolean validateUser(Optional<UserEntity> user, String rawPassword) {
        return user.filter(userEntity -> bCryptPasswordEncoder.matches(rawPassword, userEntity.getPassword())).isPresent();
    }
}
