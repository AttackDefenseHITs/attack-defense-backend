package ru.hits.attackdefenceplatform.public_interface.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Логин обязателен")
        String login,

        @NotBlank(message = "Имя обязательно")
        String name,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, message = "Пароль не может быть менее 6 символов")
        String password,

        @NotBlank(message = "Подтверждение пароля обязательно")
        String confirmPassword
) {
    public CreateUserRequest {
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Пароли должны совпадать");
        }
    }
}
