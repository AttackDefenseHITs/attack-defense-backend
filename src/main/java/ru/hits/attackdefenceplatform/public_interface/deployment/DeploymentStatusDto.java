package ru.hits.attackdefenceplatform.public_interface.deployment;

import ru.hits.attackdefenceplatform.core.deploy.enums.DeploymentStatus;

import java.util.UUID;

public record DeploymentStatusDto(
        UUID virtualMachineId,
        UUID vulnerableServiceId,
        DeploymentStatus status,
        String message
) { }
