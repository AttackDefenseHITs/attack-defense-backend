package ru.hits.attackdefenceplatform.core.deploy.deployment;

public interface DeploymentService {
    void deployAllServices();
    void deployServiceOnVirtualMachine(String serviceName, String virtualMachineIp);
}
