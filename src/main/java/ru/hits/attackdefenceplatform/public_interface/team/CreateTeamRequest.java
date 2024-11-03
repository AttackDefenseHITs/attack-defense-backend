package ru.hits.attackdefenceplatform.public_interface.team;

import jakarta.validation.constraints.NotBlank;

public record CreateTeamRequest(
        @NotBlank(message = "Имя команды обязательно")
        String name,

        Long maxMembers
) { }
