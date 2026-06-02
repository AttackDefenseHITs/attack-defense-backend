package ru.hits.attackdefenceplatform.public_interface.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
        @NotBlank(message = "Логин обязателен")
        @Size(min = 3, max = 20, message = "Логин должен содержать от 3 до 20 символов")
        String login,

        @NotBlank(message = "Имя обязательно")
        @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
        String name,

        @NotBlank(message = "Пароль обязателен")
        @Size(min = 6, max = 50, message = "Пароль не может быть менее 6 и более 50 символов")
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "Пароль должен содержать буквы и цифры")
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
