package ru.hits.attackdefenceplatform.public_interface.vulnerable_service;

import java.util.UUID;

public record VulnerableServiceDto(
        UUID id,
        String name,
        String gitRepositoryUrl
) {}
