package ru.hits.attackdefenceplatform.business.deploy.deployment;

import java.util.UUID;

public interface DeploymentService {
    void deployAllServices();
    void deployServiceOnVirtualMachine(UUID serviceId, UUID virtualMachineId);
    Boolean isDeploymentInProgress();
}
