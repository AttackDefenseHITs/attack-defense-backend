package ru.hits.attackdefenceplatform.core.vulnerable_service.mapper;

import ru.hits.attackdefenceplatform.core.vulnerable_service.repository.VulnerableServiceEntity;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.CreateVulnerableServiceRequest;
import ru.hits.attackdefenceplatform.public_interface.vulnerable_service.VulnerableServiceDto;

public class VulnerableServiceMapper {

    public static VulnerableServiceDto toDto(VulnerableServiceEntity entity) {
        return new VulnerableServiceDto(
                entity.getId(),
                entity.getName(),
                entity.getGitRepositoryUrl(),
                entity.getPort()
        );
    }

    public static VulnerableServiceEntity fromRequest(CreateVulnerableServiceRequest request) {
        var entity = new VulnerableServiceEntity();
        entity.setName(request.name());
        entity.setGitRepositoryUrl(request.gitRepositoryUrl());
        entity.setPort(request.port());
        return entity;
    }
}

