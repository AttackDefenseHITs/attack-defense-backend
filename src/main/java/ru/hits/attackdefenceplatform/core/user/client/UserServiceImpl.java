package ru.hits.attackdefenceplatform.core.user.client;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.common.exception.UserAlreadyExistsException;
import ru.hits.attackdefenceplatform.core.token.TokenService;
import ru.hits.attackdefenceplatform.core.user.client.UserService;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.core.user.mapper.UserMapper;
import ru.hits.attackdefenceplatform.core.user.repository.UserRepository;
import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.public_interface.user.CreateUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.LoginUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.UpdateUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final TokenService tokenService;

    @Override
    @Transactional
    public TokenResponse registerUser(CreateUserRequest dto) {
        if (userRepository.findByLogin(dto.login()).isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с таким логином уже существует: " + dto.login());
        }

        var userEntity = UserMapper.mapCreateUserDtoToEntity(dto);
        userEntity.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        userRepository.save(userEntity);

        var userDto = UserMapper.mapUserEntityToDto(userEntity);
        return tokenService.createTokenResponse(userDto);
    }

    @Override
    @Transactional
    public TokenResponse loginUser(LoginUserRequest dto){
        Optional<UserEntity> user = userRepository.findByLogin(dto.login());
        if (!validateUser(user, dto.password())){
            throw new IllegalArgumentException("Неверный логин или пароль");
        }

        var userDto = UserMapper.mapUserEntityToDto(user.get());
        return tokenService.createTokenResponse(userDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapUserEntityToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserProfile(UserEntity user) {
        return UserMapper.mapUserEntityToDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUserProfile(UserEntity user, UpdateUserRequest dto) {
        if (dto.name() != null) {
            user.setName(dto.name());
        }
        if (dto.login() != null) {
            if (userRepository.findByLogin(dto.login()).isPresent() && !dto.login().equals(user.getLogin())) {
                throw new UserAlreadyExistsException("Пользователь с таким логином уже существует: " + dto.login());
            }
            user.setLogin(dto.login());
        }
        userRepository.save(user);
        return UserMapper.mapUserEntityToDto(user);
    }


    private boolean validateUser(Optional<UserEntity> user, String rawPassword) {
        return user.filter(userEntity -> bCryptPasswordEncoder.matches(rawPassword, userEntity.getPassword())).isPresent();
    }
}
