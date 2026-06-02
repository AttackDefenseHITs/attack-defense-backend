package ru.hits.attackdefenceplatform.public_interface.deployment;

import java.util.List;

public record DeploymentResult(
        List<DeploymentDataDto> deploymentData
) {
}
