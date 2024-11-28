package ru.hits.attackdefenceplatform.public_interface.vitrual_machine;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateVirtualMachineRequest(
        @NotBlank String ipAddress,
        @NotBlank String username,
        @NotBlank String password,
        @NotNull UUID teamId
) {}
