package ru.hits.attackdefenceplatform.public_interface.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @NotBlank(message = "Имя обязательно")
        @Size(min = 1, max = 50, message = "Имя должно содержать от 1 до 50 символов")
        String name,

        @NotBlank(message = "Логин обязателен")
        @Size(min = 3, max = 20, message = "Логин должен содержать от 3 до 20 символов")
        String login
) {}
