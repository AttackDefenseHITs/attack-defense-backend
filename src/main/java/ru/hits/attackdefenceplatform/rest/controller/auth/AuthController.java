package ru.hits.attackdefenceplatform.rest.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.user.UserService;
import ru.hits.attackdefenceplatform.public_interface.token.TokenResponse;
import ru.hits.attackdefenceplatform.public_interface.user.CreateUserRequest;
import ru.hits.attackdefenceplatform.public_interface.user.LoginUserRequest;

@RestController
@RequestMapping("api/auth")
@Tag(name = "Авторизация")
@RequiredArgsConstructor
public class AuthController {
    private final UserService userService;

    @PostMapping("login")
    @Operation(summary = "Авторизация пользователя", description = "Позволяет пользователю войти в систему")
    public ResponseEntity<TokenResponse> loginUser(@RequestBody LoginUserRequest body){
        return ResponseEntity.ok(userService.loginUser(body));
    }

    @PostMapping("register")
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя")
    public ResponseEntity<TokenResponse> registerUser(@Valid @RequestBody CreateUserRequest body) {
        return ResponseEntity.ok(userService.registerUser(body));
    }
}
