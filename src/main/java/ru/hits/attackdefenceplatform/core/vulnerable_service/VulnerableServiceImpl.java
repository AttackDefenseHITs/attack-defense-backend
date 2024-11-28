package ru.hits.attackdefenceplatform.core.vulnerable_service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hits.attackdefenceplatform.core.vulnerable_service.mapper.VulnerableServiceMapper;
import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceRepository;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.CreateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.UpdateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.VulnerableServiceDto;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VulnerableServiceImpl implements VulnerableService {

    private final VulnerableServiceRepository serviceRepository;

    @Override
    @Transactional
    public UUID createService(CreateVulnerableServiceRequest request) {
        if (serviceRepository.findByName(request.name()).isPresent()) {
            throw new EntityNotFoundException("Сервис с таким именем уже существует");
        }
        var service = VulnerableServiceMapper.fromRequest(request);
        return serviceRepository.save(service).getId();
    }

    @Override
    @Transactional(readOnly = true)
    public VulnerableServiceDto getServiceById(UUID id) {
        var service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + id + " не найден"));
        return VulnerableServiceMapper.toDto(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VulnerableServiceDto> getAllServices() {
        return serviceRepository.findAll().stream()
                .map(VulnerableServiceMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void updateService(UUID id, UpdateVulnerableServiceRequest request) {
        var service = serviceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Сервис с ID " + id + " не найден"));

        service.setName(request.name());
        service.setGitRepositoryUrl(request.gitRepositoryUrl());

        serviceRepository.save(service);
    }

    @Override
    @Transactional
    public void deleteService(UUID id) {
        if (!serviceRepository.existsById(id)) {
            throw new EntityNotFoundException("Сервис с ID " + id + " не найден");
        }
        serviceRepository.deleteById(id);
    }
}

