package ru.hits.attackdefenceplatform.rest.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.hits.attackdefenceplatform.core.user.client.UserService;
import ru.hits.attackdefenceplatform.core.user.repository.Role;
import ru.hits.attackdefenceplatform.public_interface.user.RoleDto;
import ru.hits.attackdefenceplatform.public_interface.user.UserDto;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/users")
@Tag(name = "Управление пользователями для администратора")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;

    @GetMapping
    @Operation(summary = "Получить всех пользователей")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        var users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/{id}")
    @Operation(summary = "Назначить пользователю роль")
    public ResponseEntity<UserDto> setUserRole(@PathVariable UUID id, @RequestBody RoleDto role) {
        var user = userService.setUserRole(id, role.role());
        return ResponseEntity.ok(user);
    }
}
