package ru.hits.attackdefenceplatform.public_interface.vitrual_machine;

import java.util.UUID;

public record VirtualMachineDto(
        UUID id,
        String ipAddress,
        String username,
        UUID teamId
) {}
