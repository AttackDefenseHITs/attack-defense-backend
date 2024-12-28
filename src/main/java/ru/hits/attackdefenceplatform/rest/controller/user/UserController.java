package ru.hits.attackdefenceplatform.rest.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.user.UserService;
import ru.hits.attackdefenceplatform.core.user.repository.UserEntity;
import ru.hits.attackdefenceplatform.public_interface.user.RoleDto;
import ru.hits.attackdefenceplatform.public_interface.user.UpdateUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import java.util.List;

@RestController
@RequestMapping("api/users")
@Tag(name = "Пользователи")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить список всех пользователей")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/profile")
    @Operation(summary = "Получить профиль пользователя")
    public ResponseEntity<UserDto> getUserProfile(@AuthenticationPrincipal UserEntity user){
        var dto = userService.getUserProfile(user);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/profile")
    @Operation(summary = "Получить профиль пользователя")
    public ResponseEntity<UserDto> updateUserProfile(
            @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserEntity user
    ){
        var dto = userService.updateUserProfile(user, request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/role")
    @Operation(summary = "Получить роль пользователя")
    public ResponseEntity<RoleDto> getUserRole(
            @AuthenticationPrincipal UserEntity user
    ){
        var dto = new RoleDto(user.getRole());
        return ResponseEntity.ok(dto);
    }
}
