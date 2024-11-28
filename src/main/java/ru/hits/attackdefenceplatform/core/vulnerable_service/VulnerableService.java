package ru.hits.attackdefenceplatform.core.vulnerable_service;

import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.CreateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.UpdateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.VulnerableServiceDto;

import java.util.List;
import java.util.UUID;

public interface VulnerableService {
    UUID createService(CreateVulnerableServiceRequest request);
    VulnerableServiceDto getServiceById(UUID id);
    List<VulnerableServiceDto> getAllServices();
    void updateService(UUID id, UpdateVulnerableServiceRequest request);
    void deleteService(UUID id);
}
