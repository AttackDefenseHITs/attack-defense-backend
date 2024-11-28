package ru.hits.attackdefenceplatform.public_interface.vitrual_machine;

import jakarta.validation.constraints.NotBlank;

public record UpdateVirtualMachineRequest(
        @NotBlank String ipAddress,
        @NotBlank String username,
        @NotBlank String password
) {}
