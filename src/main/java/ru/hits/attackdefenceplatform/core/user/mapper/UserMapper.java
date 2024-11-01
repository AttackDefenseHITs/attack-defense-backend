package ru.hits.attackdefenceplatform.core.user.mapper;

import ru.hits.attackdefenceplatform.core.user.repository.Role;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.user.CreateUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

public class UserMapper {
    public static UserDto mapUserEntityToDto(UserEntity user){
        return new UserDto(user.getId(), user.getLogin(), user.getName(), user.getRole());
    }

    public static UserEntity mapCreateUserDtoToEntity(CreateUserRequest dto){
        UserEntity user = new UserEntity();
        user.setName(dto.name());
        user.setLogin(dto.login());
        user.setRole(Role.USER);
        user.setPassword(dto.password());
        return user;
    }
}
