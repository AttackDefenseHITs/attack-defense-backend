package ru.hits.attackdefenceplatform.core.user;

import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.public_interface.user.CreateUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.LoginUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import java.util.List;

public interface UserService {
    TokenResponse registerUser(CreateUserRequest dto);
    TokenResponse loginUser(LoginUserRequest dto);
    List<UserDto> getAllUsers();
}
