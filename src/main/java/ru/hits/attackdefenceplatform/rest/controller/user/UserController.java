package ru.hits.attackdefenceplatform.rest.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.user.UserService;
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
}
