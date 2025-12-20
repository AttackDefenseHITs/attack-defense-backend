package ru.hits.attackdefenceplatform.public_interface.deployment;

import ru.hits.attackdefenceplatform.core.deploy.enums.DeploymentStatus;
import ru.hits.attackdefenceplatform.public_interface.vitrual_machine.VirtualMachineDto;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.VulnerableServiceDto;

import java.time.LocalDateTime;

public record DeploymentDataDto(
        VirtualMachineDto virtualMachine,
        VulnerableServiceDto vulnerableService,
        DeploymentStatus deploymentStatus,
        String message,
        LocalDateTime updatedAt
) {
}
