package ru.hits.attackdefenceplatform.public_interface.vitrual_machine;

import java.util.UUID;

public record UpdateVirtualMachineRequest(
        String ipAddress,
        String username,
        String password,
        UUID teamId
) {}
