package ru.hits.attackdefenceplatform.core.deploy;

public interface DeploymentService {
    void deployAllServices();
    void deployServiceOnVirtualMachine(String serviceName, String virtualMachineIp);
}
